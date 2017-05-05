/*
 * Copyright (c) 2017.
 * Distributed under the GNU GPLv3 by David Whitman.
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * This source code is made available to help others learn. Please don't clone my app.
 */

package com.thunderclouddev.dataprovider

import com.thunderclouddev.persistence.Links
import com.thunderclouddev.persistence.Offer
import com.thunderclouddev.persistence.Permission
import java.util.*

/**
 * Created by david on 4/27/17.
 */
data class AppInfo(var packageName: String,
                       var versionCode: Int,
                       var title: String?,
                       var descriptionHtml: String?,
                       var shortDescription: String?,
                       var versionName: String?,
                       var rating: Float?,
                       var bayesianMeanRating: Double?,
                       var ratingsCount: Long?,
                       var oneStarRatings: Long?,
                       var twoStarRatings: Long?,
                       var threeStarRatings: Long?,
                       var fourStarRatings: Long?,
                       var fiveStarRatings: Long?,
                       var developerId: String?,
                       var developer: String?,
                       var developerEmail: String?,
                       var developerWebsite: String?,
                       var downloadsCount: Long?,
                       var downloadsCountString: String?,
                       var installSizeBytes: Long?,
                       var recentChangesHtml: String?,
                       var updateDate: Date?,
                       var category: String?,
                       var links: Links?,
                       var offer: Offer?,
                       var permissions: List<Permission>?,
                       var contentRating: String?)