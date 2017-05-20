/*
 * Copyright (c) 2017.
 * Distributed under the GNU GPLv3 by David Whitman.
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * This source code is made available to help others learn. Please don't clone my app.
 */

package com.thunderclouddev.dataprovider

import com.jakewharton.rxrelay2.ReplayRelay
import com.thunderclouddev.deeplink.logging.timberkt.KTimber
import com.thunderclouddev.persistence.AppInfoDatabase
import com.thunderclouddev.persistence.DbAppInfo
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Exposes the app's database while hiding implementation details.
 * @author David Whitman on 20 May, 2017.
 */
@Singleton
class Database @Inject constructor(private val database: AppInfoDatabase) {
    private val databaseChangesSubject = ReplayRelay.create<PlayClient.DatabaseChange.NewItems>(1)

    val databaseChanges = databaseChangesSubject.share()

    init {
        // Seed with initial value
        databaseChangesSubject.accept(PlayClient.DatabaseChange.NewItems(getAllBlocking()))

        database.observeChanges()
                .buffer(500, TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
                .doOnSubscribe { KTimber.v { "Subscribed to database changes" } }
                .doOnEach { KTimber.v { "Database change observed: ${it.value}" } }
                .filter { it.isNotEmpty() }
                .map { result ->
                    if (result.isEmpty() || result.toList().isEmpty()) {
                        PlayClient.DatabaseChange.NewItems(emptyList())
                    } else {
                        PlayClient.DatabaseChange.NewItems(
                                result.last()
                                        .toList()
                                        .map { it.toModel() }
                                        .groupBy { it.packageName }
                                        .map { AppInfosByPackage(it.key, it.value) })
                    }

                }
                .doOnNext { KTimber.d { "Observed: ${it.appInfos.joinToString { it.packageName }}" } }
                .subscribe {
                    databaseChangesSubject.accept(it)
                    lastSnapshot = it.appInfos
                }
    }

    var lastSnapshot = databaseChangesSubject.value.appInfos

    fun refresh(): Unit = databaseChangesSubject.accept(PlayClient.DatabaseChange.NewItems(getAllBlocking()))

    fun get(packageName: String) = database.get(packageName)

    fun getAllBlocking(): List<AppInfosByPackage> = database.getAll().blockingGet()
            .map { AppInfosByPackage(it.key, it.value.map { it.toModel() }) }

    fun put(appInfo: DbAppInfo) = database.put(appInfo)

    fun put(appInfo: List<DbAppInfo>) = database.put(appInfo)

    fun remove(packageName: String, versionCode: Int? = null) = database.remove(packageName, versionCode)

    fun clear() {
        database.clearAll().subscribe {
            //            getApps()
        }
    }
}