package inaka.com.mangosta.videostream;

import android.util.Log;
import android.util.Pair;

import org.ice4j.TransportAddress;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

/**
 * Created by rafalslota on 25/04/2017.
 */
public class ProxyRTPServer extends Thread implements NewPeerHandler {
    private static final String TAG = "ProxyRTPServer";
    private final DatagramSocket localSockData;
    private final DatagramSocket localSockControl;

    private final RelayThread dataRelay;
    private final RelayThread controlRelay;
    private final DataReceiver dataReceiver;
    private final DataReceiver controlReceiver;

    public ProxyRTPServer(int serverBasePort, String turnAddr, int turnPort) throws SocketException {
        localSockData = new DatagramSocket(serverBasePort);
        localSockControl = new DatagramSocket(serverBasePort + 1);

        dataRelay = new RelayThread(turnAddr, turnPort);
        dataRelay.onData(new RelayDataHandler() {
            @Override
            public void handleData(String peerAddr, int peerPort, byte[] data) {
                DatagramPacket p = new DatagramPacket(data, data.length);
                try {
                    localSockData.send(p);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        controlRelay = new RelayThread(turnAddr, turnPort);
        controlRelay.onData(new RelayDataHandler() {
            @Override
            public void handleData(String peerAddr, int peerPort, byte[] data) {
                DatagramPacket p = new DatagramPacket(data, data.length);
                try {
                    localSockControl.send(p);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        dataReceiver = new DataReceiver(localSockData, dataRelay);
        controlReceiver = new DataReceiver(localSockData, controlRelay);
    }

    @Override
    public void run() {
        try {
            localSockData.connect(new InetSocketAddress("127.0.0.1", 5006));
            localSockControl.connect(new InetSocketAddress("127.0.0.1", 5007));

            dataRelay.start();
            Thread.sleep(500);
            controlRelay.start();

            dataReceiver.start();
            controlReceiver.start();
        } catch (SocketException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean isRelayReady() {
        return dataRelay.isAllocated() && controlRelay.isAllocated();
    }

    public Pair<TransportAddress, TransportAddress> getRelayAddrs() {
        return new Pair<>(dataRelay.getRelayAddr(), controlRelay.getRelayAddr());
    }

    @Override
    public void onNewPeerDiscovered(String peerAddr) {
        dataRelay.onNewPeerDiscovered(peerAddr);
        controlRelay.onNewPeerDiscovered(peerAddr);
    }

    private class DataReceiver extends Thread {
        private final DatagramSocket socket;
        private final RelayThread relay;

        public DataReceiver(DatagramSocket socket, RelayThread relay) {
            this.socket = socket;
            this.relay = relay;
        }

        @Override
        public void run() {
            byte[] buf = new byte[1024*1024];
            while (true) {
                DatagramPacket p = new DatagramPacket(buf, buf.length);
                try {
                    socket.receive(p);
                    Log.d(TAG, "Received " + " " + p.getOffset() + " " + p.getLength());
                    TransportAddress peerAddr = relay.getLastPeerAddr();

                    if (peerAddr == null)
                        continue;

                    Log.d(TAG, peerAddr.toString() + " " + p.getOffset() + " " + p.getLength());

                    byte[] sendBuf = new byte[p.getLength()];
                    System.arraycopy(buf, p.getOffset(), sendBuf, 0, p.getLength());

                    relay.send(peerAddr.getHostAddress(), peerAddr.getPort(), sendBuf);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
