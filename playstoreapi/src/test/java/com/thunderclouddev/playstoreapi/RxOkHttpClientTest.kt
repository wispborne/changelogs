/*
 * Copyright (c) 2017.
 * Distributed under the GNU GPLv3 by David Whitman.
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * This source code is made available to help others learn. Please don't clone my app.
 */

package com.thunderclouddev.playstoreapi

import com.nhaarman.mockito_kotlin.mock
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.context
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.junit.Assert
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`


@RunWith(JUnitPlatform::class)
object RxOkHttpClientTest : Spek({
    given("an RxOkHttpClient") {
        var httpClient = mock<OkHttpClient>()
        var client = RxOkHttpClient(httpClient)
        var response = mock<Response>()

        beforeEachTest {
            val call = mock<Call>()
            httpClient = mock<OkHttpClient>()
            client = RxOkHttpClient(httpClient)
            response = mock<Response>()
            `when`(httpClient.newCall(anyKt<Request>()))
                    .thenReturn(call)
            `when`(call.execute()).thenReturn(response)
        }

        context("is that the api calls succeed") {
            beforeEachTest {
                `when`(response.code()).thenReturn(200)
            }

            it("should be that the value is returned") {
                client.rxecute(mock<Request>())
                        .subscribe { response, error ->
                            Assert.assertNull(error)
                            Assert.assertNotNull(response)
                        }
            }
        }

        context("is that the api calls fail with 400") {
            beforeEachTest {
                `when`(response.code()).thenReturn(400)
            }

            it("should be that the single throws an error") {
                client.rxecute(mock<Request>())
                        .subscribe { response, error ->
                            Assert.assertNull(response)
                            Assert.assertNotNull(error)
                        }
            }
        }

        context("is that the api calls fail with 500") {
            beforeEachTest {
                `when`(response.code()).thenReturn(500)
            }

            it("should be that the single throws an error") {
                client.rxecute(mock<Request>())
                        .subscribe { response, error ->
                            Assert.assertNull(response)
                            Assert.assertNotNull(error)
                        }
            }
        }
    }
})