/*
 * Copyright (c) 2017.
 * Distributed under the GNU GPLv3 by David Whitman.
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * This source code is made available to help others learn. Please don't clone my app.
 */

package com.thunderclouddev.playstoreapi

import android.content.Context
import android.content.SharedPreferences
import com.nhaarman.mockito_kotlin.mock
import com.thunderclouddev.playstoreapi.proto.AndroidCheckinRequest
import com.thunderclouddev.utils.empty
import io.reactivex.Single
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.context
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.*


/**
 * Created by David Whitman on 26 Mar, 2017.
 */
@RunWith(JUnitPlatform::class)
object PlayApiClientWrapperTest : Spek({
    given("a PlayApiClientWrapper") {
        val gsfId = "678910"
        val authToken = "12345"
        val TOKEN_KEY = "fdfe_token"
        val PREFS_NAME = "PREFS_NAME"

        val authenticationProvider = object : AuthenticationProvider {
            override fun getUserAuthToken() = Single.just(authToken)
        }

        var context = mock<Context>()
        var sharedPrefs = mock<SharedPreferences>()
        var editor = mock<SharedPreferences.Editor>()
        val deviceInfo = DeviceInfo(AndroidCheckinRequest.newBuilder().build(),
                DeviceInfo.generateUserAgentString(21, "device", "hardware", "apples"),
                21)

        beforeEachTest {
            context = mock<Context>()
            sharedPrefs = mock<SharedPreferences>()
            editor = mock<SharedPreferences.Editor>()
            `when`(sharedPrefs.edit()).thenReturn(editor)
            `when`(editor.putString(anyString(), anyString())).thenReturn(editor)
        }

        context("is that an auth token is cached") {
            beforeEachTest {
                `when`(context.getSharedPreferences(eq(PREFS_NAME), anyInt()))
                        .thenReturn(sharedPrefs)
                `when`(sharedPrefs.getString(anyString(), eq(String.empty)))
                        .thenReturn(String.empty)
            }

            it("tries to get cached auth token") {
                PlayApiClientWrapper(context, gsfId, deviceInfo, object : AuthorizationRequiredDelegate {
                    override fun onAuthorizationRequired() {
                        throw RuntimeException()
                    }
                }).build(authenticationProvider).blockingGet()

                verify(sharedPrefs).getString(TOKEN_KEY, String.empty)
            }

            it("caches auth token") {
                PlayApiClientWrapper(context, gsfId, deviceInfo, object : AuthorizationRequiredDelegate {
                    override fun onAuthorizationRequired() {
                        throw RuntimeException()
                    }
                }).build(authenticationProvider).blockingGet()

                verify(editor).putString(TOKEN_KEY, authToken)
            }
        }

    }
})