/*
 * Copyright (c) 2017.
 * Distributed under the GNU GPLv3 by David Whitman.
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * This source code is made available to help others learn. Please don't clone my app.
 */

package com.thunderclouddev.playstoreapi

import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import fdfeProtos.AndroidCheckinRequest
import fdfeProtos.BulkDetailsResponse
import fdfeProtos.UploadDeviceConfigResponse
import io.reactivex.Single
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.context
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.junit.Assert
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import java.util.*


@RunWith(JUnitPlatform::class)
object PlayApiClientTest : Spek({

    given("a PlayApiClient") {
        val authToken = "12345"
        val gsfId = "678910"
        val uploadRequired = false

        var httpClient: RxOkHttpClient = mock()
        var locale: Locale = mock()

        val deviceInfo: DeviceInfo = DeviceInfo(
                AndroidCheckinRequest.getDefaultInstance(),
                "agent smith string",
                21
        )

        var playApiClient = PlayApiClient(
                httpClient,
                authToken,
                gsfId,
                deviceInfo,
                locale,
                uploadRequired
        )

        beforeEachTest {
            httpClient = mock<RxOkHttpClient>()
            locale = mock<Locale>()

            playApiClient = PlayApiClient(
                    httpClient,
                    authToken,
                    gsfId,
                    deviceInfo,
                    locale,
                    uploadRequired
            )
        }

        context("is that the api returns 200s") {
            val uploadResponse = Response.Builder()
                    .request(mock<Request>())
                    .protocol(Protocol.HTTP_1_1)
                    .code(200)
                    .body(ResponseBody.create(
                            PlayApiConstants.PROTOBUF_MEDIA_TYPE, UploadDeviceConfigResponse.getDefaultInstance().toByteArray()))
                    .build()

            val bulkDetailsResponse = Response.Builder()
                    .request(mock<Request>())
                    .protocol(Protocol.HTTP_1_1)
                    .code(200)
                    .body(ResponseBody.create(
                            PlayApiConstants.PROTOBUF_MEDIA_TYPE, BulkDetailsResponse
                            .getDefaultInstance()
                            // See: https://github.com/yeriomin/play-store-api/tree/master/src/test/resources
//                            .toBuilder()
//                            .addEntry(BulkDetailsEntry.getDefaultInstance())
//                            .build()
                            .toByteArray()))
                    .build()


            beforeEachTest {
                `when`(httpClient.rxecute(anyKt<Request>()))
                        .thenAnswer {
                            val url = (it.arguments[0] as Request).url().toString()
                            if (url == PlayApiConstants.BULKDETAILS_URL) {
                                Single.just(bulkDetailsResponse)
                            } else if (url == PlayApiConstants.UPLOADDEVICECONFIG_URL) {
                                Single.just(uploadResponse)
                            } else {
                                throw NotImplementedError("Url not recognized!")
                            }
                        }
            }

            context("is that a device config upload is required") {
                beforeEachTest {
                    playApiClient.deviceConfigUploadRequired = true
                }

                it("should be that the device config is uploaded") {
                    playApiClient.rxecute(PlayRequest.BulkDetailsRequest(emptyList()))
                            .blockingGet()

                    val captor = argumentCaptor<Request>()
                    Mockito.verify(httpClient, times(2)).rxecute(captor.capture())
                    Assert.assertEquals(PlayApiConstants.UPLOADDEVICECONFIG_URL, captor.firstValue.url().toString())
                }
            }

            it("should be that the bulkDetails call returns data") {
                val result = playApiClient.rxecute(PlayRequest.BulkDetailsRequest(emptyList()))
                        .blockingGet()

                val captor = argumentCaptor<Request>()
                Mockito.verify(httpClient).rxecute(captor.capture())
                Assert.assertEquals(PlayApiConstants.BULKDETAILS_URL, captor.firstValue.url().toString())
                Assert.assertEquals(0, result.size)
            }
        }
    }
})