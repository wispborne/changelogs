/*
 * Copyright (c) 2017.
 * Distributed under the GNU GPLv3 by David Whitman.
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * This source code is made available to help others learn. Please don't clone my app.
 */

package com.thunderclouddev.playstoreapi

import fdfeProtos.AndroidCheckinRequest

/**
 * @author David Whitman on 27 Mar, 2017.
 */
data class DeviceInfo(
        val androidCheckinRequest: AndroidCheckinRequest,
        val userAgentString: String,
        val sdkVersion: Int
) {
    companion object {
        private val GOOGLE_SERVICES_VERSION_CODE = 80711500

        fun generateUserAgentString(sdkVersion: Int, device: String, hardware: String, product: String): String {
            return ("Android-Finsky/7.1.15 ("
                    + "api=3"
                    + ",versionCode=" + GOOGLE_SERVICES_VERSION_CODE
                    + ",sdk=" + sdkVersion
                    + ",device=" + device
                    + ",hardware=" + hardware
                    + ",product=" + product
                    + ")")
        }
    }
}