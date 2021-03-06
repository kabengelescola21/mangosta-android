package inaka.com.mangosta.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import inaka.com.mangosta.services.XMPPSessionService;
import inaka.com.mangosta.xmpp.XMPPSession;

public class MangostaApplication extends Application {

    private static MangostaApplication CONTEXT;

    private Activity mCurrentActivity = null;
    public static Bus bus = new Bus(ThreadEnforcer.MAIN);

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        CONTEXT = this;
    }

    public static MangostaApplication getInstance() {
        return CONTEXT;
    }

    public void moveToBackground() {
        if (!XMPPSessionService.isRunning()) {
            XMPPSession.startService(this);
        }
    }

    public void moveToForeground() {
        if (!XMPPSessionService.isRunning()) {
            XMPPSession.startService(this);
        }
    }

    public Activity getCurrentActivity() {
        return mCurrentActivity;
    }

    public boolean isClosed() {
        return mCurrentActivity == null;
    }

    public void setCurrentActivity(Activity currentActivity) {
        mCurrentActivity = currentActivity;
    }

}
