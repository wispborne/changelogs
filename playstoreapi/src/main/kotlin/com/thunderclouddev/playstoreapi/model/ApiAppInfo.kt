/*
 * Copyright (c) 2017.
 * Distributed under the GNU GPLv3 by David Whitman.
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * This source code is made available to help others learn. Please don't clone my app.
 */

package com.thunderclouddev.playstoreapi.model

import java.util.*

/**
 * @author David Whitman on 30 Mar, 2017.
 */
data class ApiAppInfo(val packageName: String,
                      val versionCode: Int,
                      val title: String? = null,
                      val descriptionHtml: String? = null,
                      val shortDescription: String? = null,
                      val versionName: String? = null,

                      val rating: Float? = null,
                      val bayesianMeanRating: Double? = null,
                      val ratingsCount: Long = 0,
                      val oneStarRatings: Long? = null,
                      val twoStarRatings: Long? = null,
                      val threeStarRatings: Long? = null,
                      val fourStarRatings: Long? = null,
                      val fiveStarRatings: Long? = null,

                      val developerId: String? = null,
                      val developer: String? = null,
                      val developerEmail: String? = null,
                      val developerWebsite: String? = null,

                      val downloadsCount: Long = 0,
                      val downloadsCountString: String? = null,
                      val installSizeBytes: Long = 0,

                      val recentChangesHtml: String? = null,
                      val updateDate: Date? = null,

                      val category: String? = null,
                      val links: Links? = null,
                      val offer: Offer? = null,
                      val permissions: List<String>? = null,
                      val contentRating: String? = null // eg PEGI 3
)