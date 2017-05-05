/*
 * Copyright (c) 2017.
 * Distributed under the GNU GPLv3 by David Whitman.
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * This source code is made available to help others learn. Please don't clone my app.
 */

package com.thunderclouddev.playstoreapi

import android.os.Handler
import android.os.Looper
import io.reactivex.Single
import okhttp3.*
import okhttp3.Request
import timber.log.Timber
import java.io.IOException

/**
 * Created by David Whitman on 27 Mar, 2017.
 */
class RxOkHttpClient(private val okHttpClient: OkHttpClient) {
    fun rxecute(request: Request): Single<Response> = Single.create<Response> {
        Timber.v("Calling ${request.url()}")

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, error: IOException) {
//                Handler(Looper.getMainLooper()).post {
                    Timber.d(error, "Error for ${request.url()}")
                    it.onError(error)
//                }
            }

            override fun onResponse(call: Call, response: Response) {
//                Handler(Looper.getMainLooper()).post {
                    if (response.code().toString().startsWith("2")) {
                        Timber.d("${response.code()} success for ${request.url()}")
                        it.onSuccess(response)
                    } else {
                        Timber.d("${response.code()} error for ${request.url()} with response ${response.body().string()}")
                        it.onError(HttpException(response.code(), response.message()))
//                    }
                }
            }
        })
    }
}