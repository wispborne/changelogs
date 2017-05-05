/*
 * Copyright (c) 2017.
 * Distributed under the GNU GPLv3 by David Whitman.
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * This source code is made available to help others learn. Please don't clone my app.
 */

package com.thunderclouddev.changelogs.ui.onboarding

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.thunderclouddev.changelogs.BaseApp
import com.thunderclouddev.changelogs.R
import com.thunderclouddev.changelogs.ResourceWrapper
import com.thunderclouddev.changelogs.auth.AuthTokenProvider
import com.thunderclouddev.changelogs.databinding.IntroViewBinding
import com.thunderclouddev.playstoreapi.AuthenticationProvider
import com.thunderclouddev.playstoreapi.PlayApiClientWrapper
import com.thunderclouddev.playstoreapi.legacyMarketApi.LegacyApiClientWrapper
import io.reactivex.Completable
import timber.log.Timber
import javax.inject.Inject

/**
 * Displays information to the user on why we are going to request auth tokens,
 * and then requests any tokens that are needed (as specified by `fdfeAuthState` and `legacyAuthState`).
 * TODO: Too much state in here.
 * @author David Whitman on 29 Mar, 2017.
 */
class AuthActivity : AppCompatActivity() {
    companion object {
        val RESULT_SUCCESS = 0
        val RESULT_ERROR = 1
        private var fdfeAuthState = AuthState.NothingNeeded
        private var legacyAuthState = AuthState.NothingNeeded

        fun promptUserForFdfeToken(context: Context) {
            if (fdfeAuthState == AuthState.NothingNeeded) {
                fdfeAuthState = AuthState.Requested

                // Only launch new activity if we're not current waiting on a legacy auth token prompt
                // Don't wanna end up with the activity twice
                if (legacyAuthState == AuthState.NothingNeeded) {
                    context.startActivity(Intent(context, AuthActivity::class.java).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) })
                }
            }
        }

        fun promptUserForLegacyToken(context: Context) {
            if (legacyAuthState == AuthState.NothingNeeded) {
                legacyAuthState = AuthState.Requested

                // Only launch new activity if we're not current waiting on a fdfe auth token prompt
                // Don't wanna end up with the activity twice
                if (fdfeAuthState == AuthState.NothingNeeded) {
                    context.startActivity(Intent(context, AuthActivity::class.java).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) })
                }
            }
        }

        private enum class AuthState {
            Requested,
            InProgress,
            NothingNeeded
        }
    }

    @Inject lateinit var authTokenProvider: AuthTokenProvider
    @Inject lateinit var playApiClientWrapper: PlayApiClientWrapper
    @Inject lateinit var legacyApiClientWrapper: LegacyApiClientWrapper
    private lateinit var model: IntroViewModel
    private var authSucceeded = false

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        authTokenProvider.onActivityResult(requestCode, resultCode, data)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BaseApp.appInjector.inject(this)
        model = IntroViewModel(ResourceWrapper(this@AuthActivity))

        model.nextButtonClicks
                .subscribe {
                    var createClientChain = Completable.complete()

                    if (fdfeAuthState == AuthState.Requested) {
                        createClientChain = createClientChain
                                .doOnComplete { fdfeAuthState = AuthState.InProgress }
                                .andThen(createPlayApiClient(this))
                                .doOnError { fdfeAuthState = AuthState.Requested }
                                .doOnComplete { fdfeAuthState = AuthState.NothingNeeded }
                    }

                    if (legacyAuthState == AuthState.Requested) {
                        createClientChain = createClientChain
                                .doOnComplete { legacyAuthState = AuthState.InProgress }
                                .andThen(createMarketApiClient(this))
                                .doOnError { legacyAuthState = AuthState.Requested }
                                .doOnComplete { legacyAuthState = AuthState.NothingNeeded }
                    }

                    createClientChain
                            .subscribe({
                                setResult(RESULT_SUCCESS)
                                authSucceeded = true
                                finish()
                            }, { error ->
                                Timber.e(error)
                                Toast.makeText(this, error.message, Toast.LENGTH_LONG).show()
                            })
                }

        setContentView(DataBindingUtil.inflate<IntroViewBinding>(layoutInflater, R.layout.intro_view, null, false)
                .apply { this.model = this@AuthActivity.model }
                .root)
    }

    override fun onDestroy() {
        if (!authSucceeded) {
            playApiClientWrapper.notifyAuthCanceled()
            legacyApiClientWrapper.notifyAuthCanceled()
        }

        fdfeAuthState = AuthState.NothingNeeded
        legacyAuthState = AuthState.NothingNeeded
        super.onDestroy()
    }

    private fun createPlayApiClient(activity: Activity) =
            playApiClientWrapper.build(object : AuthenticationProvider {
                override fun getUserAuthToken() =
                        authTokenProvider.getAuthToken(activity, AuthTokenProvider.TOKEN_TYPE.TOKEN_TYPE_FDFE)
            })

    private fun createMarketApiClient(activity: Activity) =
            legacyApiClientWrapper.build(object : AuthenticationProvider {
                override fun getUserAuthToken() =
                        authTokenProvider.getAuthToken(activity, AuthTokenProvider.TOKEN_TYPE.TOKEN_TYPE_OLD_MARKET)
            })
}