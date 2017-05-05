/*
 * Copyright (c) 2017.
 * Distributed under the GNU GPLv3 by David Whitman.
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * This source code is made available to help others learn. Please don't clone my app.
 */

package com.thunderclouddev.dataprovider

/**
 * Created by david on 4/26/17.
 */
sealed class Filter<T> {
//    abstract class DatabaseFilter<T> : Filter<T>() {
//
//    }

    abstract class BooleanFilter<T> : Filter<T>() {
        abstract fun test(itemToTest: T): Boolean
    }
}