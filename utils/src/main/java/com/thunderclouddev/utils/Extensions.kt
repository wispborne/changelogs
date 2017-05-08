/*
 * Copyright (c) 2017.
 * Distributed under the GNU GPLv3 by David Whitman.
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * This source code is made available to help others learn. Please don't clone my app.
 */

package com.thunderclouddev.utils

import android.view.View
import io.reactivex.Observable
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

/**
 * From https://medium.com/@ZakTaccardi/diffutil-one-way-data-flow-with-rxjava-and-kotlin-6e17f0cdef0c
 */
fun <T, R> Observable<T>.scanMap(func2: (T?, T) -> R): Observable<R> {
    return this.startWith(null as T?) //emit a null value first, otherwise the .buffer() below won't emit at first (needs 2 emissions to emit)
            .buffer(2, 1) //buffer the previous and current emission
            .filter { it.size >= 2 } //when the buffer terminates (onCompleted/onError), the remaining buffer is emitted. When don't want those!
            .map { func2.invoke(it[0], it[1]) }
}

/**
 * From https://medium.com/@ZakTaccardi/diffutil-one-way-data-flow-with-rxjava-and-kotlin-6e17f0cdef0c
 */
fun <T, R> Observable<T>.scanMap(initialValue: T, func2: (T, T) -> R): Observable<R> {
    return this.startWith(initialValue)
            .buffer(2, 1)
            .filter { it.size >= 2 }
            .map { func2.invoke(it[0], it[1]) }
}