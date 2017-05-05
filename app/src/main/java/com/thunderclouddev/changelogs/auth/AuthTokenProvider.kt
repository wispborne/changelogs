/*
 * Copyright (c) 2017.
 * Distributed under the GNU GPLv3 by David Whitman.
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * This source code is made available to help others learn. Please don't clone my app.
 */

package com.thunderclouddev.changelogs.auth

import android.accounts.Account
import android.accounts.AccountManager
import android.app.Activity
import android.content.Intent
import com.thunderclouddev.changelogs.UserAccountPrompt
import com.thunderclouddev.changelogs.preferences.UserPreferences
import com.thunderclouddev.utils.isNotNullOrBlank
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Creates auth tokens, prompting the user to choose a Google account if they haven't already.
 * @author David Whitman on 28 Mar, 2017.
 */
@Singleton
class AuthTokenProvider @Inject constructor(private val userPreferences: UserPreferences) {
    enum class TOKEN_TYPE(val value: String) {
        TOKEN_TYPE_OLD_MARKET("android"),
        TOKEN_TYPE_FDFE("androidmarket")
    }

    private val GOOGLE_ACCOUNT_TYPE = "com.google"

    private var userAccountPrompt: UserAccountPrompt? = null

    /**
     * Creates an auth token of the specified type.
     * Either uses the email account saved in prefs, or prompts the user to choose an account.
     */
    fun getAuthToken(activity: Activity, tokenType: TOKEN_TYPE): Single<String> =
            Single.create<String> { subscriber ->
                if (userPreferences.emailAccount.isNotNullOrBlank()) {
                    createUnexpiredAuthToken(activity, Account(userPreferences.emailAccount, GOOGLE_ACCOUNT_TYPE), tokenType)
                            .subscribe({ success -> subscriber.onSuccess(success) }, { error -> subscriber.onError(error) })
                } else {
                    promptUserToChooseAccount(activity, null, object : UserAccountPrompt.Companion.Listener {
                        override fun onAccountSelected(account: Account) {
                            userPreferences.emailAccount = account.name

                            createUnexpiredAuthToken(activity, account, tokenType)
                                    .subscribe(object : SingleObserver<String> {
                                        override fun onError(e: Throwable?) {
                                            subscriber.onError(e)
                                        }

                                        override fun onSuccess(t: String?) {
                                            subscriber.onSuccess(t)
                                        }

                                        override fun onSubscribe(d: Disposable?) {

                                        }
                                    })
                        }

                        override fun onFailure(errorReason: String) {
                            subscriber.onError(Exception(errorReason))
                        }
                    })
                }
            }

    /**
     * Notifies this [AuthTokenProvider] class of an activity result.
     * Must be called or else `getAuthToken` will never complete!
     */
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == UserAccountPrompt.CHOOSE_ACCOUNT_REQUEST_CODE) {
            userAccountPrompt?.handleOnActivityResult(requestCode, resultCode, data)
        }
    }

    private fun promptUserToChooseAccount(activity: Activity,
                                          accounts: Array<Account>?,
                                          listener: UserAccountPrompt.Companion.Listener) {
        userAccountPrompt = UserAccountPrompt(GOOGLE_ACCOUNT_TYPE, accounts)
        activity.startActivityForResult(userAccountPrompt!!.createChooseAccountIntent(listener),
                UserAccountPrompt.CHOOSE_ACCOUNT_REQUEST_CODE)
    }

    /**
     * Fetch the auth token, then invalidate it, then refetch it. This ensures that it will never expire.
     */
    private fun createUnexpiredAuthToken(activity: Activity, foundAccount: Account,
                                         tokenType: TOKEN_TYPE): Single<String> {
        return createAuthToken(activity, foundAccount, tokenType)
                .map { result ->
                    invalidateAuthToken(activity, result, tokenType)
                    result
                }
                .flatMap { createAuthToken(activity, foundAccount, tokenType) }
    }

    private fun createAuthToken(activity: Activity, account: Account, tokenType: TOKEN_TYPE): Single<String> {
        return Single.create<String> { subscriber ->
            Timber.v("Getting auth token")
            val accountManager = AccountManager.get(activity)
            val accountManagerFuture = accountManager.getAuthToken(account, tokenType.value, null,
                    activity, null, null)

            try {
                val authToken = accountManagerFuture.result.getString(AccountManager.KEY_AUTHTOKEN)
//                this.runOnUiThread({
                Timber.v("Auth token acquired!")
                subscriber.onSuccess(authToken)
//                })
            } catch (e: Exception) {
                if (e is IOException) {
                    Timber.d(e, "Failed to get auth token (network error): ${e.message}")
                } else {
                    Timber.e(e, "Failed to get auth token: ${e.message}")
                }

//                this.runOnUiThread({
                subscriber.onError(e)
//                })
            }
        }
                .subscribeOn(Schedulers.newThread())
    }

    private fun invalidateAuthToken(activity: Activity, authToken: String, tokenType: TOKEN_TYPE) {
        Timber.v("Invalidating auth token")
        val accountManager = AccountManager.get(activity)
        accountManager.invalidateAuthToken(tokenType.value, authToken)
    }
}