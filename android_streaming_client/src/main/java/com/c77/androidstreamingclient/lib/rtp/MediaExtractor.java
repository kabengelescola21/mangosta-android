/*
* Copyright (C) 2015 Creativa77 SRL and others
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
* Contributors:
*
* Ayelen Chavez ashi@creativa77.com.ar
* Julian Cerruti jcerruti@creativa77.com.ar
*
*/

package com.c77.androidstreamingclient.lib.rtp;

import android.media.MediaFormat;

/**
 * Interface that defines basic behavior of a extractor.
 *
 * @author Julian Cerruti
 */
public interface MediaExtractor {
    // Think how to get CSD-0/CSD-1 codec-specific data chunks

    /**
     * Returns Android MediaFormat used to properly configure Android API decoder.
     * It depends on the codec configuration.
     *
     * @return Android MediaFormat for a given codec.
     */
    MediaFormat getMediaFormat();
}