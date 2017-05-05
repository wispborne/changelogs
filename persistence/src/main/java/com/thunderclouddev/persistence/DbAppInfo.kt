/*
 * Copyright (c) 2017.
 * Distributed under the GNU GPLv3 by David Whitman.
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * This source code is made available to help others learn. Please don't clone my app.
 */

package com.thunderclouddev.persistence

import com.google.gson.Gson
import com.thunderclouddev.utils.empty
import com.thunderclouddev.utils.isNotNullOrBlank
import io.requery.Convert
import io.requery.Converter
import timber.log.Timber
import java.util.*
import kotlin.text.isNotBlank

@io.requery.Entity
interface DbAppInfo : io.requery.Persistable {
    @get:io.requery.Key
    var packageName: String
    @get:io.requery.Key
    var versionCode: Int

    var title: String?
    var descriptionHtml: String?
    var shortDescription: String?
    var versionName: String?

    var rating: Float?
    var bayesianMeanRating: Double?
    var ratingsCount: Long?
    var oneStarRatings: Long?
    var twoStarRatings: Long?
    var threeStarRatings: Long?
    var fourStarRatings: Long?
    var fiveStarRatings: Long?

    var developerId: String?
    var developer: String?
    var developerEmail: String?
    var developerWebsite: String?

    var downloadsCount: Long?
    var downloadsCountString: String?
    var installSizeBytes: Long?

    var recentChangesHtml: String?
    var updateDate: Date?

    var category: String?

    @get:Convert(LinksConverter::class)
    var links: Links?

    @get:Convert(OfferConverter::class)
    var offer: Offer?

    @get:Convert(PermissionListConverter::class)
    var permissions: List<Permission>?

    var contentRating: String?
}

abstract class AbstractJsonConverter<T>(private val defaultObj: T, private val javaClass: Class<T>) : Converter<T, String> {
    override fun convertToPersisted(links: T?): String {
        return if (links != null)
            Gson().toJson(links)
        else
            String.empty
    }

    override fun convertToMapped(type: Class<out T>?, value: String?): T {
        return if (value.isNotNullOrBlank())
            try {
                Gson().fromJson(value, type)
            } catch (e: Exception) {
                Timber.w(e, "Failed to parse the following to a ${javaClass.name} object: $value")
                defaultObj
            }
        else
            defaultObj
    }

    override fun getPersistedType() = String::class.java

    override fun getMappedType(): Class<T> = javaClass

    override fun getPersistedSize() = null
}

class OfferConverter : AbstractJsonConverter<Offer>(Offer(), Offer::class.java)

class LinksConverter : AbstractJsonConverter<Links>(Links(), Links::class.java)

class PermissionListConverter : Converter<MutableList<Permission>, String> {
    private val SEPARATOR = "\u007C"

    override fun convertToPersisted(list: MutableList<Permission>?): String {
        return if (list != null && list.isNotEmpty()) list
                .map { it.toString() }
                .reduce { left, right -> "$left$SEPARATOR$right" }
        else String.empty
    }

    override fun convertToMapped(type: Class<out MutableList<Permission>>, value: String?) =
            value
                    ?.split(SEPARATOR)
                    ?.filter(String::isNotBlank)
                    ?.map(::Permission)
                    ?.toMutableList()
                    ?: mutableListOf()

    override fun getMappedType() = (MutableList::class.java as Class<MutableList<Permission>>)

    override fun getPersistedType() = String::class.java

    override fun getPersistedSize() = null
}