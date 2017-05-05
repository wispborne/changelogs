/*
 * Copyright (c) 2017.
 * Distributed under the GNU GPLv3 by David Whitman.
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * This source code is made available to help others learn. Please don't clone my app.
 */

package com.thunderclouddev.playstoreapi.legacyMarketApi

import com.thunderclouddev.playstoreapi.ApiClient
import com.thunderclouddev.playstoreapi.RxOkHttpClient
import com.thunderclouddev.playstoreapi.legacyMarketApi.proto.Market
import com.thunderclouddev.playstoreapi.legacyMarketApi.proto.Market2
import com.thunderclouddev.playstoreapi.model.ApiAppInfo
import com.thunderclouddev.utils.getOrNullIfBlank
import com.thunderclouddev.utils.isNotNullOrBlank
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.Headers
import okhttp3.Request
import okhttp3.RequestBody
import timber.log.Timber
import java.io.ByteArrayInputStream
import java.util.*
import java.util.zip.GZIPInputStream

/**
 * Fetches information from the legacy Android Market API.
 *
 * Created by david on 4/8/17.
 */
internal class MarketApiClient(
        private val rxOkHttpClient: RxOkHttpClient,
        private val authToken: String,
        gsfId: String,
        locale: Locale)
    : ApiClient<MarketRequest<*>> {
    private val requestContext = Market2.RequestContext.newBuilder()
            .setAndroidId(gsfId)
            .setAuthSubToken(authToken)
            .setIsSecure(false)
            .setVersion(MarketApiConstants.VERSION)
            .setUserLanguage(locale.language.toLowerCase())
            .setUserCountry(locale.country.toLowerCase())
            .setDeviceAndSdkVersion("passion:9")
            .setOperatorAlpha(MarketApiConstants.OPERATOR_NAME_ID_PAIR.first)
            .setSimOperatorAlpha(MarketApiConstants.OPERATOR_NAME_ID_PAIR.first)
            .setOperatorNumeric(MarketApiConstants.OPERATOR_NAME_ID_PAIR.second)
            .setSimOperatorNumeric(MarketApiConstants.OPERATOR_NAME_ID_PAIR.second)
            .build()

    override fun <T> rxecute(request: com.thunderclouddev.playstoreapi.Request<MarketRequest<*>, T>): Single<T> =
            (request as MarketRequest)
                    .execute(rxOkHttpClient, Headers.of(getDefaultHeaders()), requestContext)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())

    private fun getDefaultHeaders(): Map<String, String> =
            mapOf("Cookie" to "ANDROID=$authToken",
                    "User-Agent" to "Android-Market/2 (sapphire PLAT-RC33); gzip",
                    "Accept-Charset" to "ISO-8859-1,utf-8;q=0.7,*;q=0.7",
                    "Content-Type" to MarketApiConstants.CONTENT_TYPE.toString()
            )
}

sealed class MarketRequest<T> : com.thunderclouddev.playstoreapi.Request<MarketRequest<*>, T> {
    abstract fun execute(apiClient: RxOkHttpClient, defaultHeaders: Headers, context: Market2.RequestContext): Single<T>

    class AppsRequest(val packageNamesRequested: List<String>) : MarketRequest<List<ApiAppInfo>>() {
        private val APPS_PER_REQUEST = 1
        private val REQUESTS_PER_GROUP = 10

        override fun execute(apiClient: RxOkHttpClient, defaultHeaders: Headers, context: Market2.RequestContext): Single<List<ApiAppInfo>> {

            val packageNames = if (packageNamesRequested.size > REQUESTS_PER_GROUP) {
                Timber.w("Only $REQUESTS_PER_GROUP apps at a time are supported!")
                packageNamesRequested.take(REQUESTS_PER_GROUP)
            } else {
                packageNamesRequested
            }

            val request = Market2.Request.newBuilder()
                    .setContext(context)

            packageNames.map { packageName ->
                Market2.AppsRequest.newBuilder()
                        .setQuery("pname:${packageName.trim()}")
                        .setStartIndex(0)
                        .setEntriesCount(APPS_PER_REQUEST)
                        .setWithExtendedInfo(true)
                        .build()
            }
                    .forEach { appsRequest ->
                        request.addRequestGroup(Market2.Request.RequestGroup.newBuilder()
                                .setAppsRequest(appsRequest))
                    }

            val encodedRequestBody = Base64.encodeBytes(request.build().toByteArray(), Base64.URL_SAFE)
            return apiClient.rxecute(Request.Builder()
                    .url(MarketApiConstants.URL)
                    .post(RequestBody.create(
                            MarketApiConstants.CONTENT_TYPE,
                            "version=${MarketApiConstants.PROTOCOL_VERSION}&request=$encodedRequestBody".toByteArray()))
                    .headers(defaultHeaders)
                    .build())
                    .doOnSubscribe { Timber.v("FdfeBulk fetching ${packageNames.joinToString()}") }
                    .map { decompressGzippedBytes(it.body().bytes()) }
                    .map {
                        Market.Response.parseFrom(it)
                                .responseGroupList
                                .map { it.appsResponse }
                                .filter { it.appList.isNotEmpty() }
                                .map { it.getApp(0) }
                                .filterIndexed { index, appInfo ->
                                    if (appInfo.packageName.isNullOrBlank()) {
                                        Timber.v("Ignoring app with no results: ${packageNames[index]}")
                                    }

                                    appInfo.packageName.isNotNullOrBlank()
                                }
                                .map { mapAppToAppInfo(it) }
                    }
        }
    }

    internal fun decompressGzippedBytes(bytes: ByteArray) = GZIPInputStream(ByteArrayInputStream(bytes)).readBytes()

    internal fun mapAppToAppInfo(app: Market.App): ApiAppInfo {
        return ApiAppInfo(packageName = app.packageName,
                title = app.title.getOrNullIfBlank(),
                developer = app.creator.getOrNullIfBlank(),
                developerId = app.creatorId.getOrNullIfBlank(),
                descriptionHtml = app.extendedInfo.description.getOrNullIfBlank(),
                downloadsCount = app.extendedInfo.downloadsCount.toLong(),
                installSizeBytes = app.extendedInfo.installSize.toLong(),
                recentChangesHtml = app.extendedInfo.recentChanges.getOrNullIfBlank(),
                rating = app.rating.toFloatOrNull(),
                ratingsCount = app.ratingsCount.toLong(),
                versionCode = app.versionCode,
                versionName = app.version.getOrNullIfBlank(),
                category = app.extendedInfo.category.getOrNullIfBlank())
    }
}