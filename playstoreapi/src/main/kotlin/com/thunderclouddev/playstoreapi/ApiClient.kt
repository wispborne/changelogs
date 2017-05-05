/*
 * Copyright (c) 2017.
 * Distributed under the GNU GPLv3 by David Whitman.
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * This source code is made available to help others learn. Please don't clone my app.
 */

package com.thunderclouddev.playstoreapi

import io.reactivex.Single

/**
 * Created by david on 4/17/17.
 */
interface ApiClient<R : Request<R, *>> {
    fun <T> rxecute(request: Request<R, T>): Single<T>
}