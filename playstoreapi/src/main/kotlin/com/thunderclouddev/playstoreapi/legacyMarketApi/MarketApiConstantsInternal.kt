/*
 * Copyright (c) 2017.
 * Distributed under the GNU GPLv3 by David Whitman.
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * This source code is made available to help others learn. Please don't clone my app.
 */

package com.thunderclouddev.playstoreapi.legacyMarketApi

import okhttp3.MediaType

internal object MarketApiConstantsInternal {
    val CONTENT_TYPE: MediaType = MediaType.parse("application/x-www-form-urlencoded; charset=UTF-8")
    val PROTOCOL_VERSION = 2

    val VERSION = 2009011
    val OPERATOR_NAME_ID_PAIR = "T-Mobile" to "310260"

    val URL = "http://android.clients.google.com/market/api/ApiRequest"
}

object MarketApiConstants{
    val REQUESTS_PER_GROUP = 10
}