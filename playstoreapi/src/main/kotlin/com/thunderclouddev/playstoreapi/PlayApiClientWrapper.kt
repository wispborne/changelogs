/*
 * Copyright (c) 2017.
 * Distributed under the GNU GPLv3 by David Whitman.
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * This source code is made available to help others learn. Please don't clone my app.
 */

package com.thunderclouddev.playstoreapi

import android.content.Context
import okhttp3.OkHttpClient
import java.util.*

/**
 * Wraps the `PlayStoreApiClient`, creating it if necessary before returning any request results.
 * @author David Whitman on 26 Mar, 2017.
 */
class PlayApiClientWrapper(context: Context,
                           private val gsfId: String,
                           private val deviceInfo: DeviceInfo,
                           authorizationRequiredDelegate: AuthorizationRequiredDelegate) {

    private var deviceConfigUploadRequired = false

    private val apiClientWrapper = object : ApiClientWrapper<PlayApiClient, PlayRequest<*>>(context, authorizationRequiredDelegate, "fdfe_token") {
        override fun createClient(client: OkHttpClient, locale: Locale, authToken: String) =
                PlayApiClient(
                        RxOkHttpClient(client),
                        authToken,
                        gsfId,
                        deviceInfo,
                        locale,
                        deviceConfigUploadRequired)

        override fun onNewAuthTokenCreated() {
            super.onNewAuthTokenCreated()
            // Need to upload device config if it's the first time using the app or getting new auth token
            deviceConfigUploadRequired = true
        }
    }

    /**
     * Execute a request, automatically handling missing client or token and retrying.
     */
    fun <T> rxecute(request: PlayRequest<T>) = apiClientWrapper.rxecute<T>(request)

    /**
     * Build an authenticated api client that can service api calls.
     * Retries on-hold api calls and attempts to deliver the results to the original requesters.
     */
    fun build(authenticationProvider: AuthenticationProvider) = apiClientWrapper.build(authenticationProvider)

    /**
     * Call when the user cancels the auth token creation process, so that on-hold requests can be cleared.
     */
    fun notifyAuthCanceled() = apiClientWrapper.notifyAuthCanceled()
}