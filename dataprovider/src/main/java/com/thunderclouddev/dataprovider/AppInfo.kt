/*
 * Copyright (c) 2017.
 * Distributed under the GNU GPLv3 by David Whitman.
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * This source code is made available to help others learn. Please don't clone my app.
 */

package com.thunderclouddev.dataprovider

import java.util.*

/**
 * Created by david on 4/27/17.
 */
data class AppInfo(var packageName: String,
                   var versionCode: Int,
                   var title: String? = null,
                   var descriptionHtml: String? = null,
                   var shortDescription: String? = null,
                   var versionName: String? = null,
                   var rating: Float? = null,
                   var bayesianMeanRating: Double? = null,
                   var ratingsCount: Long? = null,
                   var oneStarRatings: Long? = null,
                   var twoStarRatings: Long? = null,
                   var threeStarRatings: Long? = null,
                   var fourStarRatings: Long? = null,
                   var fiveStarRatings: Long? = null,
                   var developerId: String? = null,
                   var developer: String? = null,
                   var developerEmail: String? = null,
                   var developerWebsite: String? = null,
                   var downloadsCount: Long? = null,
                   var downloadsCountString: String? = null,
                   var installSizeBytes: Long? = null,
                   var recentChangesHtml: String? = null,
                   var updateDate: Date? = null,
                   var category: String? = null,
                   var links: Links? = null,
                   var offer: Offer? = null,
                   var permissions: List<Permission>? = null,
                   var contentRating: String? = null) {
    companion object {
        val EMPTY = AppInfo(packageName = "no package name!", versionCode = 0)
    }
}