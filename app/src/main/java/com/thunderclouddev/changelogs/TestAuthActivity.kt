/*
 * Copyright (c) 2017.
 * Distributed under the GNU GPLv3 by David Whitman.
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * This source code is made available to help others learn. Please don't clone my app.
 */

package com.thunderclouddev.changelogs

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.thunderclouddev.changelogs.auth.AuthTokenProvider
import com.thunderclouddev.changelogs.auth.GsfId
import com.thunderclouddev.playstoreapi.DeviceInfo
import com.thunderclouddev.playstoreapi.PlayApiClientWrapper
import com.thunderclouddev.utils.empty
import io.reactivex.Single
import timber.log.Timber
import java.util.*
import javax.inject.Inject

/**
 * @author David Whitman on 27 Mar, 2017.
 */
class TestAuthActivity : AppCompatActivity() {

    companion object {
    }

    @Inject lateinit var gsfId: GsfId
    @Inject lateinit var authTokenProvider: AuthTokenProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BaseApp.appInjector.inject(this)
    }

    override fun onResume() {
        super.onResume()

//        PlayApiClientWrapper(this).build(object : AuthenticationProvider {
//            override fun getUserAuthToken() = authTokenProvider.getAuthToken(this@TestAuthActivity)
//
//            override fun getGsfId() = Single.just(
//                    gsfId.get ?: String.empty)
//
//            override fun getDeviceInfo(): Single<DeviceInfo> {
//                val nativeDeviceInfo = NativeDeviceInfo(this@TestAuthActivity, Locale.US.toString())
//                return Single.just(DeviceInfo(
//                        nativeDeviceInfo.generateAndroidCheckinRequest(),
//                        nativeDeviceInfo.deviceConfigurationProto,
//                        DeviceInfo.generateUserAgentString(Build.VERSION.SDK_INT, Build.DEVICE, Build.HARDWARE, Build.PRODUCT),
//                        Build.VERSION.SDK_INT
//                ))
//            }
//        })
//                .subscribe { api, error ->
//                    if (error != null) {
//                        Timber.e(error, "Failed to create GooglePlayClient")
//                    } else {
//                        api.getBulkDetails(
//                                listOf("com.thunderclouddev.changelogs", "com.jtmcn.archwiki.viewer",
//                                        "hu.supercluster.gameoflife", "eu.chainfire.recently",
//                                        "com.RobotUnicornAttack",
//                                        "com.habadigital.Unicorn"))
//                                .subscribe { response, error ->
//                                    Timber.e(error,
//                                            error?.message ?: response.entryList.joinToString { it.doc.title })
//                                }
//                    }
//                }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        authTokenProvider.onActivityResult(requestCode, resultCode, data)
    }
}