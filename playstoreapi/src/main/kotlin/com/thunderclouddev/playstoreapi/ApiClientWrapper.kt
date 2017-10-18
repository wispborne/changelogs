/*
 * Copyright (c) 2017.
 * Distributed under the GNU GPLv3 by David Whitman.
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * This source code is made available to help others learn. Please don't clone my app.
 */

package com.thunderclouddev.playstoreapi

import android.content.Context
import com.thunderclouddev.utils.empty
import com.thunderclouddev.utils.isNotNullOrBlank
import com.thunderclouddev.utils.simpleClassName
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import timber.log.Timber
import java.util.*

/**
 * Wraps an `ApiClient`, creating it if necessary before returning any request results.
 * @author David Whitman.
 */
internal abstract class ApiClientWrapper<out C : ApiClient<R>, R : Request<R, *>>(
        private val context: Context,
        private val authorizationRequiredDelegate: AuthorizationRequiredDelegate,
        private val tokenStorageKey: String) {

    companion object {
        private val PREFS_NAME = "PREFS_NAME"
//        val PREFS_KEY_GSF_ID = "PREFS_KEY_GSF_ID"
//        val PREFS_KEY_EMAIL = "PREFS_KEY_EMAIL"
    }

    private var apiClient: C? = null
    private val pendingRequests = mutableListOf<RetryableRequest>()
    private val sharedPrefs by lazy { context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) }
    private val hasCachedAuthToken
        get() = sharedPrefs.getString(tokenStorageKey, String.empty).isNotNullOrBlank()

    /**
     * Build an authenticated api client that can service api calls.
     * Retries on-hold api calls and attempts to deliver the results to the original requesters.
     */
    fun build(authenticationProvider: AuthenticationProvider): Completable {
        // Either get it from shared prefs, if available, or else from the user
        val getAuthToken =
                if (hasCachedAuthToken) {
                    Single.just(sharedPrefs.getString(tokenStorageKey, String.empty))
                } else {
                    authenticationProvider.getUserAuthToken()
                            .doOnSuccess {
                                sharedPrefs.edit().putString(tokenStorageKey, it).apply()
                                onNewAuthTokenCreated()
                            }
                }

        return getAuthToken
                .map { authToken ->
                    val locale = Locale.US
                    val client = OkHttpClient.Builder().build()
                    val playApiClient = createClient(client, locale, authToken)

                    apiClient = playApiClient
                    pendingRequests.forEach { it.retry() }
                }
                .toCompletable()
    }

    abstract fun createClient(client: OkHttpClient, locale: Locale, authToken: String): C

    protected open fun onNewAuthTokenCreated() {}

    /**
     * Execute a request, automatically handling missing client or token and retrying.
     */
    fun <T> rxecute(request: R): Single<T> {
        val localApiClient = apiClient

        // If we already have a client, all good, execute the request
        return if (localApiClient != null) {
            return localApiClient.rxecute(request)
                    .map { it as T }
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
//                    .doOnSuccess { Timber.v("Succeeded: ${request.simpleClassName}: $it") }
//                    .doOnError { Timber.w("Error: ${request.simpleClassName}: ${it.message}") }
        } else {
            // If we don't have a client but we have an auth token, build the client and then execute
            if (hasCachedAuthToken) {
                build(object : AuthenticationProvider {
                    override fun getUserAuthToken(): Single<String> {
                        Timber.wtf("we already have auth token, this will never be called")
                        return Single.error(RuntimeException("we already have auth token, this will never be called"))
                    }
                })
                        .toSingle { Any() }
                        .flatMap<T> { apiClient?.rxecute(request) as Single<T> }
                        .map { it as T }
//-----------------------------------------------
//                Single.create<T> { subscriber ->
//                    val shouldRequestAuth = pendingRequests.isEmpty()
//
//                    pendingRequests += object : RetryableRequest {
//                        override fun retry() {
//                            apiClient?.rxecute(request)
//                                    ?.subscribeOn(Schedulers.newThread())
//                                    ?.observeOn(AndroidSchedulers.mainThread())
//                                    ?.subscribe(
//                                            { result -> subscriber.onSuccess(result as T) },
//                                            { error -> subscriber.onError(error) })
//                        }
//                    }
//
//                    if (shouldRequestAuth) {
//                        Timber.v("Requesting authentication from user to create auth token.")
//                        authorizationRequiredDelegate.onAuthorizationRequired()
//                    }
//                }
//-----------------------------------------------
            } else {
                // And if we have neither a client nor an auth token, then queue the call and signal that auth is required
                Single.create<T> { subscriber ->
                    val shouldRequestAuth = pendingRequests.isEmpty()

                    pendingRequests += object : RetryableRequest {
                        override fun retry() {
                            apiClient?.rxecute(request)
                                    ?.subscribeOn(Schedulers.newThread())
                                    ?.observeOn(AndroidSchedulers.mainThread())
                                    ?.subscribe(
                                            { result -> subscriber.onSuccess(result as T) },
                                            { error -> subscriber.onError(error) })
                        }
                    }

                    if (shouldRequestAuth) {
                        Timber.v("Requesting authentication from user to create auth token.")
                        authorizationRequiredDelegate.onAuthorizationRequired()
                    }
                }
            }
        }
    }

    /**
     * Call when the user cancels the auth token creation process, so that on-hold requests can be cleared.
     */
    fun notifyAuthCanceled() {
        if (apiClient == null) {
            Timber.v("Clearing ${pendingRequests.size} pending requests (${pendingRequests.joinToString { it.simpleClassName }}).")
            pendingRequests.clear()
        }
    }
}

private interface RetryableRequest {
    fun retry()
}