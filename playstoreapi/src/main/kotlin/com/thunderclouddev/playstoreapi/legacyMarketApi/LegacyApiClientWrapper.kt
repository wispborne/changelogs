/*
 * Copyright (c) 2017.
 * Distributed under the GNU GPLv3 by David Whitman.
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * This source code is made available to help others learn. Please don't clone my app.
 */

package com.thunderclouddev.playstoreapi.legacyMarketApi

import android.content.Context
import com.thunderclouddev.playstoreapi.ApiClientWrapper
import com.thunderclouddev.playstoreapi.AuthenticationProvider
import com.thunderclouddev.playstoreapi.AuthorizationRequiredDelegate
import com.thunderclouddev.playstoreapi.RxOkHttpClient
import okhttp3.OkHttpClient
import java.util.*

/**
 * Created by david on 4/17/17.
 */
class LegacyApiClientWrapper(context: Context,
                             private val gsfId: String,
                             authorizationRequiredDelegate: AuthorizationRequiredDelegate) {

    private val apiClientWrapper = object : ApiClientWrapper<MarketApiClient, MarketRequest<*>>(context, authorizationRequiredDelegate, "market_token") {
        override fun createClient(client: OkHttpClient, locale: Locale, authToken: String) =
                MarketApiClient(
                        RxOkHttpClient(client),
                        authToken,
                        gsfId,
                        locale)
    }

    /**
     * Execute a request, automatically handling missing client or token and retrying.
     */
    fun <T> rxecute(request: MarketRequest<T>) = apiClientWrapper.rxecute<T>(request)

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