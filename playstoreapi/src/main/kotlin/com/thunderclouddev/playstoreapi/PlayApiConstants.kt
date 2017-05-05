/*
 * Copyright (c) 2017.
 * Distributed under the GNU GPLv3 by David Whitman.
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * This source code is made available to help others learn. Please don't clone my app.
 */

package com.thunderclouddev.playstoreapi

import okhttp3.MediaType

/**
 * Created by David Whitman on 30 Mar, 2017.
 */
internal object PlayApiConstants {
    val PROTOBUF_MEDIA_TYPE: MediaType = MediaType.parse("application/x-protobuf")

    val SCHEME = "https://"
    val HOST = "android.clients.google.com"
    val CHECKIN_URL = SCHEME + HOST + "/checkin"
    val URL_LOGIN = SCHEME + HOST + "/auth"
    val C2DM_REGISTER_URL = SCHEME + HOST + "/c2dm/register2"
    val FDFE_URL = SCHEME + HOST + "/fdfe/"
    val LIST_URL = FDFE_URL + "list"
    val BROWSE_URL = FDFE_URL + "browse"
    val DETAILS_URL = FDFE_URL + "details"
    val SEARCH_URL = FDFE_URL + "search"
    val SEARCHSUGGEST_URL = FDFE_URL + "searchSuggest"
    val BULKDETAILS_URL = FDFE_URL + "bulkDetails"
    val PURCHASE_URL = FDFE_URL + "purchase"
    val DELIVERY_URL = FDFE_URL + "delivery"
    val REVIEWS_URL = FDFE_URL + "rev"
    val ADD_REVIEW_URL = FDFE_URL + "addReview"
    val DELETE_REVIEW_URL = FDFE_URL + "deleteReview"
    val UPLOADDEVICECONFIG_URL = FDFE_URL + "uploadDeviceConfig"
    val RECOMMENDATIONS_URL = FDFE_URL + "rec"
    val CATEGORIES_URL = FDFE_URL + "categories"
}