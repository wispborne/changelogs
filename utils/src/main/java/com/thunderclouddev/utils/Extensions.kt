/*
 * Copyright (c) 2017.
 * Distributed under the GNU GPLv3 by David Whitman.
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * This source code is made available to help others learn. Please don't clone my app.
 */

package com.thunderclouddev.utils

import android.view.View
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlin.String.Companion


fun Throwable.hasCause(type: Class<*>): Boolean {
    var cause = this

    while (cause.cause != null) {
        cause = cause.cause!!

        if (cause.javaClass.name == type.name) {
            return true
        }
    }

    return false
}

val Any?.simpleClassName: String
    get() = this?.javaClass?.simpleName ?: String.empty

@Suppress("unused")
val Companion.empty: String
    get() = ""

fun String?.getOrNullIfBlank() = if (this.isNullOrBlank()) null else this

fun CharSequence?.isNotNullOrBlank() = !this.isNullOrBlank()

fun CharSequence?.getOrDefaultIfNullOrBlank(
        defaultValue: String) = if (this.isNullOrBlank()) this else defaultValue

val Boolean.visibleOrGone: Int
    get() = if (this) View.VISIBLE else View.GONE

var View.showing: Boolean
    get() = this.visibility == View.VISIBLE
    set(value) {
        this.visibility = value.visibleOrGone
    }

fun <T> Iterable<T>.firstOr(defaultItem: T): T {
    return this.firstOrNull() ?: defaultItem
}

operator fun <T> MutableCollection<T>.plusAssign(item: T) {
    this.add(item)
}

operator fun CompositeDisposable.plusAssign(item: Disposable) {
    this.add(item)
}

fun <T> T.asSingletonList() = listOf(this)