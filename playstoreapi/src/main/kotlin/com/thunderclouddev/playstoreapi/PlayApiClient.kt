/*
 * Copyright (c) 2017.
 * Distributed under the GNU GPLv3 by David Whitman.
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * This source code is made available to help others learn. Please don't clone my app.
 */

package com.thunderclouddev.playstoreapi

import com.thunderclouddev.playstoreapi.model.ApiAppInfo
import com.thunderclouddev.playstoreapi.model.Links
import com.thunderclouddev.playstoreapi.model.Offer
import com.thunderclouddev.utils.asSingletonList
import com.thunderclouddev.utils.getOrNullIfBlank
import com.thunderclouddev.utils.isNotNullOrBlank
import fdfeProtos.UploadDeviceConfigRequest
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.Headers
import okhttp3.Request
import okhttp3.RequestBody
import timber.log.Timber
import java.io.ByteArrayInputStream
import java.text.DateFormat
import java.util.*
import java.util.zip.GZIPInputStream

/**
 * Executes [PlayRequest]s against the FDFE Google Play API.
 *
 * @author David Whitman on 27 Mar, 2017.
 */
internal class PlayApiClient(
        private val rxHttpClient: RxOkHttpClient,
        private val authToken: String,
        private val gsfId: String,
        private val deviceInfo: DeviceInfo,
        private val locale: Locale,
        var deviceConfigUploadRequired: Boolean = false)
    : ApiClient<PlayRequest<*>> {


    override fun <T> rxecute(request: com.thunderclouddev.playstoreapi.Request<PlayRequest<*>, T>): Single<T> =
            uploadDeviceConfigIfRequired()
                    .andThen((request as PlayRequest).execute(rxHttpClient, Headers.of(getDefaultHeaders())))
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())

    private fun uploadDeviceConfigToGoogle(): Completable =
            rxHttpClient.rxecute(Request.Builder()
                    .post(RequestBody.create(PlayApiConstants.PROTOBUF_MEDIA_TYPE, UploadDeviceConfigRequest.newBuilder()
                            .setDeviceConfiguration(deviceInfo.androidCheckinRequest.deviceConfiguration)
                            .build().toByteArray()))
                    .headers(Headers.of(getDefaultHeaders()
                            .plus(arrayListOf(
                                    "X-DFE-Enabled-Experiments" to "cl:billing.select_add_instrument_by_default",
                                    "X-DFE-Unsupported-Experiments" to "nocache:billing.use_charging_poller,market_emails,buyer_currency,prod_baseline,checkin.set_asset_paid_app_field,shekel_test,content_ratings,buyer_currency_in_app,nocache:encrypted_apk,recent_changes",
                                    "X-DFE-Client-Id" to "am-android-google",
                                    "X-DFE-SmallestScreenWidthDp" to "320",
                                    "X-DFE-Filter-Level" to "3"
                            ))))
                    .url(PlayApiConstants.UPLOADDEVICECONFIG_URL)
                    .build())
//                    .map { ResponseWrapper.parseFrom(it.body().bytes()).payload.uploadDeviceConfigResponse }
                    .toCompletable()

    private fun uploadDeviceConfigIfRequired() =
            if (deviceConfigUploadRequired) {
                uploadDeviceConfigToGoogle()
                        .doOnComplete {
                            deviceConfigUploadRequired = false
                        }
            } else {
                Completable.complete()
            }

    /**
     * Using Accept-Language you can fetch localized information such as reviews and descriptions.
     * Note that changing this value has no affect on localized application list that
     * server provides. It depends on only your IP location.
     */
    private fun getDefaultHeaders(): Map<String, String> {
        val headers = HashMap<String, String>()
        if (this.authToken.isNotEmpty()) {
            headers.put("Authorization", "GoogleLogin auth=" + this.authToken)
        }
        headers.put("User-Agent", this.deviceInfo.userAgentString)
        if (this.gsfId.isNotEmpty()) {
            headers.put("X-DFE-Device-Id", this.gsfId)
        }
        headers.put("Accept-Language", this.locale.toString().replace("_", "-"))
        // This is an encoded comma separated list of ints
        // Getting this list properly will be a huge task, so it is static for now
        // It probably depends both on device and account settings and is retrieved when the user logs in for the first time
        headers.put("X-DFE-Encoded-Targets",
                "CAEScFfqlIEG6gUYogFWrAISK1WDAg+hAZoCDgIU1gYEOIACFkLMAeQBnASLATlASUuyAyqCAjY5igOMBQzfA/IClwFbApUC4ANbtgKVAS7OAX8YswHFBhgDwAOPAmGEBt4OfKkB5weSB5AFASkiN68akgMaxAMSAQEBA9kBO7UBFE1KVwIDBGs3go6BBgEBAgMECQgJAQIEAQMEAQMBBQEBBAUEFQYCBgUEAwMBDwIBAgOrARwBEwMEAg0mrwESfTEcAQEKG4EBMxghChMBDwYGASI3hAEODEwXCVh/EREZA4sBYwEdFAgIIwkQcGQRDzQ2fTC2AjfVAQIBAYoBGRg2FhYFBwEqNzACJShzFFblAo0CFxpFNBzaAd0DHjIRI4sBJZcBPdwBCQGhAUd2A7kBLBVPngEECHl0UEUMtQETigHMAgUFCc0BBUUlTywdHDgBiAJ+vgKhAU0uAcYCAWQ/5ALUAw1UwQHUBpIBCdQDhgL4AY4CBQICjARbGFBGWzA1CAEMOQH+BRAOCAZywAIDyQZ2MgM3BxsoAgUEBwcHFia3AgcGTBwHBYwBAlcBggFxSGgIrAEEBw4QEqUCASsWadsHCgUCBQMD7QICA3tXCUw7ugJZAwGyAUwpIwM5AwkDBQMJA5sBCw8BNxBVVBwVKhebARkBAwsQEAgEAhESAgQJEBCZATMdzgEBBwG8AQQYKSMUkAEDAwY/CTs4/wEaAUt1AwEDAQUBAgIEAwYEDx1dB2wGeBFgTQ")
        return headers
    }
}

abstract class PlayRequest<T> : com.thunderclouddev.playstoreapi.Request<PlayRequest<*>, T> {
    internal val IMAGE_TYPE_ID_FULL = 4
    internal val IMAGE_TYPE_ID_THUMBNAIL = 2
    internal val IMAGE_TYPE_ID_SCREENSHOT = 1

    abstract fun execute(apiClient: RxOkHttpClient, defaultHeaders: Headers): Single<T>

    /**
     * TODO: How many packages can we request at once? 10?
     */
    class BulkDetailsRequest(val packageNames: List<String>) : PlayRequest<List<ApiAppInfo>>() {
        override fun execute(apiClient: RxOkHttpClient, defaultHeaders: Headers): Single<List<ApiAppInfo>> =
                apiClient.rxecute(Request.Builder()
                        .post(RequestBody.create(PlayApiConstants.PROTOBUF_MEDIA_TYPE,
                                fdfeProtos.BulkDetailsRequest.newBuilder()
                                        .addAllDocid(packageNames)
                                        .build().toByteArray()))
                        .headers(defaultHeaders)
                        .url(PlayApiConstants.BULKDETAILS_URL)
                        .build())
                        .doOnSubscribe { Timber.v("FdfeBulk fetching ${packageNames.joinToString()}") }
                        .map {
                            fdfeProtos.ResponseWrapper.parseFrom(it.body().bytes()).payload.bulkDetailsResponse.entryList
                                    .map { entry -> entry.doc }
                                    .map { mapDocToAppInfo(it) }
                                    .filterIndexed { index, appInfo ->
                                        if (appInfo.packageName.isNullOrBlank()) {
                                            Timber.v("Ignoring app with no results: ${packageNames[index]}")
                                        }

                                        appInfo.packageName.isNotNullOrBlank()
                                    }
                        }
    }

    class DetailsRequest(val packageName: String) : PlayRequest<ApiAppInfo>() {
        override fun execute(apiClient: RxOkHttpClient, defaultHeaders: Headers): Single<ApiAppInfo> =
                apiClient.rxecute(Request.Builder()
                        .get()
                        .headers(defaultHeaders)
                        .url("${PlayApiConstants.DETAILS_URL}?doc=$packageName")
                        .build())
                        .doOnSubscribe { Timber.v("FdfeDetails fetching $packageName") }
                        .map { fdfeProtos.ResponseWrapper.parseFrom(it.body().bytes()).payload.detailsResponse }
                        .map { mapDocToAppInfo(it.docV2) }
    }

    internal fun decompressGzippedBytes(bytes: ByteArray) = GZIPInputStream(ByteArrayInputStream(bytes)).readBytes()

    internal fun mapDocToAppInfo(doc: fdfeProtos.DocV2): ApiAppInfo {
        val links = mutableMapOf(Links.REL.SELF to doc.detailsUrl.asSingletonList())

        if (doc.imageList.any { it.imageType == IMAGE_TYPE_ID_FULL }) {
            links.put(Links.REL.ICON, doc.imageList.first { it.imageType == IMAGE_TYPE_ID_FULL }.imageUrl.asSingletonList())
        }

        if (doc.imageList.any { it.imageType == IMAGE_TYPE_ID_THUMBNAIL }) {
            links.put(Links.REL.THUMBNAIL, doc.imageList.first { it.imageType == IMAGE_TYPE_ID_THUMBNAIL }.imageUrl.asSingletonList())
        }

        if (doc.imageList.any { it.imageType == IMAGE_TYPE_ID_SCREENSHOT }) {
            links.put(Links.REL.SCREENSHOT, doc.imageList.filter { it.imageType == IMAGE_TYPE_ID_THUMBNAIL }.map { it.imageUrl })
        }

        val updateDate = try {
            DateFormat.getDateInstance().parse(doc.details.appDetails.uploadDate)
        } catch (exception: Exception) {
            null
        }

        return ApiAppInfo(title = doc.title,
                packageName = doc.docid,
                developer = doc.creator,
                rating = doc.aggregateRating.starRating,
                ratingsCount = doc.aggregateRating.ratingsCount,
                developerId = null,
                versionName = doc.details.appDetails.versionString.getOrNullIfBlank(),
                versionCode = doc.details.appDetails.versionCode,
                descriptionHtml = doc.descriptionHtml.getOrNullIfBlank(),
                downloadsCountString = doc.details.appDetails.numDownloads.getOrNullIfBlank(),
                recentChangesHtml = doc.details.appDetails.recentChangesHtml.getOrNullIfBlank(),
                category = doc.relatedLinks.categoryInfo.appCategory.getOrNullIfBlank(),
                installSizeBytes = doc.details.appDetails.installationSize,
                links = Links(links),
                updateDate = updateDate,
                bayesianMeanRating = doc.aggregateRating.bayesianMeanRating,
                oneStarRatings = doc.aggregateRating.oneStarRatings,
                twoStarRatings = doc.aggregateRating.twoStarRatings,
                threeStarRatings = doc.aggregateRating.threeStarRatings,
                fourStarRatings = doc.aggregateRating.fourStarRatings,
                fiveStarRatings = doc.aggregateRating.fiveStarRatings,
                contentRating = doc.relatedLinks.rated.label.getOrNullIfBlank(),
                offer = Offer(
                        micros = doc.offerList.firstOrNull()?.micros,
                        currencyCode = doc.offerList.firstOrNull()?.currencyCode,
                        formattedAmount = doc.offerList.firstOrNull()?.formattedAmount.getOrNullIfBlank(),
                        offerType = doc.offerList.firstOrNull()?.offerType
                ),
                permissions = doc.details.appDetails.permissionList
                        .map { it.removePrefix("android.permission.") }
        )
    }
}
