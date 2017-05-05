/*
 * Copyright (c) 2017.
 * Distributed under the GNU GPLv3 by David Whitman.
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * This source code is made available to help others learn. Please don't clone my app.
 */

package com.thunderclouddev.dataprovider

import com.thunderclouddev.persistence.*
import com.thunderclouddev.playstoreapi.model.ApiAppInfo

internal fun AppInfo.toDatabaseModel() =
        DbAppInfoEntity().apply {
            packageName = this@toDatabaseModel.packageName
            versionCode = this@toDatabaseModel.versionCode
            title = this@toDatabaseModel.title
            descriptionHtml = this@toDatabaseModel.descriptionHtml
            shortDescription = this@toDatabaseModel.shortDescription
            versionName = this@toDatabaseModel.versionName
            rating = this@toDatabaseModel.rating
            bayesianMeanRating = this@toDatabaseModel.bayesianMeanRating
            ratingsCount = this@toDatabaseModel.ratingsCount
            oneStarRatings = this@toDatabaseModel.oneStarRatings
            twoStarRatings = this@toDatabaseModel.twoStarRatings
            threeStarRatings = this@toDatabaseModel.threeStarRatings
            fourStarRatings = this@toDatabaseModel.fourStarRatings
            fiveStarRatings = this@toDatabaseModel.fiveStarRatings
            developerId = this@toDatabaseModel.developerId
            developer = this@toDatabaseModel.developer
            developerEmail = this@toDatabaseModel.developerEmail
            developerWebsite = this@toDatabaseModel.developerWebsite
            downloadsCount = this@toDatabaseModel.downloadsCount
            downloadsCountString = this@toDatabaseModel.downloadsCountString
            installSizeBytes = this@toDatabaseModel.installSizeBytes
            recentChangesHtml = this@toDatabaseModel.recentChangesHtml
            updateDate = this@toDatabaseModel.updateDate
            category = this@toDatabaseModel.category
            links = this@toDatabaseModel.links
            offer = this@toDatabaseModel.offer
            permissions = this@toDatabaseModel.permissions
            contentRating = this@toDatabaseModel.contentRating
        }

internal fun DbAppInfo.toModel() =
        AppInfo(
                packageName = this.packageName,
                versionCode = this.versionCode,
                title = this.title,
                descriptionHtml = this.descriptionHtml,
                shortDescription = this.shortDescription,
                versionName = this.versionName,
                rating = this.rating,
                bayesianMeanRating = this.bayesianMeanRating,
                ratingsCount = this.ratingsCount,
                oneStarRatings = this.oneStarRatings,
                twoStarRatings = this.twoStarRatings,
                threeStarRatings = this.threeStarRatings,
                fourStarRatings = this.fourStarRatings,
                fiveStarRatings = this.fiveStarRatings,
                developerId = this.developerId,
                developer = this.developer,
                developerEmail = this.developerEmail,
                developerWebsite = this.developerWebsite,
                downloadsCount = this.downloadsCount,
                downloadsCountString = this.downloadsCountString,
                installSizeBytes = this.installSizeBytes,
                recentChangesHtml = this.recentChangesHtml,
                updateDate = this.updateDate,
                category = this.category,
                links = this.links,
                offer = this.offer,
                permissions = this.permissions,
                contentRating = this.contentRating)

internal fun ApiAppInfo.toModel() =
        AppInfo(
                packageName = this.packageName,
                versionCode = this.versionCode,
                title = this.title,
                descriptionHtml = this.descriptionHtml,
                shortDescription = this.shortDescription,
                versionName = this.versionName,
                rating = this.rating,
                bayesianMeanRating = this.bayesianMeanRating,
                ratingsCount = this.ratingsCount,
                oneStarRatings = this.oneStarRatings,
                twoStarRatings = this.twoStarRatings,
                threeStarRatings = this.threeStarRatings,
                fourStarRatings = this.fourStarRatings,
                fiveStarRatings = this.fiveStarRatings,
                developerId = this.developerId,
                developer = this.developer,
                developerEmail = this.developerEmail,
                developerWebsite = this.developerWebsite,
                downloadsCount = this.downloadsCount,
                downloadsCountString = this.downloadsCountString,
                installSizeBytes = this.installSizeBytes,
                recentChangesHtml = this.recentChangesHtml,
                updateDate = this.updateDate,
                category = this.category,
                links = if (this.links != null) Links(this.links!!) else null,
                offer = Offer(micros = this.offer?.micros,
                        currencyCode = this.offer?.currencyCode,
                        formattedAmount = this.offer?.formattedAmount,
                        offerType = this.offer?.offerType),
                permissions = this.permissions?.map(::Permission),
                contentRating = this.contentRating
        )