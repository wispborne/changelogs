/*
 * Copyright (c) 2017.
 * Distributed under the GNU GPLv3 by David Whitman.
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * This source code is made available to help others learn. Please don't clone my app.
 */

package com.thunderclouddev.persistence

import android.content.Context
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.requery.Persistable
import io.requery.kotlin.eq
import io.requery.reactivex.ReactiveResult
import timber.log.Timber

/**
 * Database to hold a history of apps. Both the package name and version code are used as keys.
 *
 * Created by david on 4/25/17.
 */
class AppInfoDatabase internal constructor(private val db: RequeryDatabase<Persistable>, private val debugMode: Boolean) {
    companion object {
        fun create(context: Context, debugMode: Boolean = false) = AppInfoDatabase(RequeryDatabase(context), debugMode)
    }

    /**
     * "Mergesert" a single [DbAppInfo] into the database. If it already exists, new data will be merged in.
     * Otherwise, the app will be inserted.
     */
    fun put(appInfo: DbAppInfo): Completable =
            Completable.create { s ->
                appInfo.packageName = appInfo.packageName.toLowerCase()

                val existingItem: DbAppInfoEntity? = db.data.select(DbAppInfoEntity::class)
                        .where(DbAppInfoEntity::packageName.eq(appInfo.packageName))
                        .and(DbAppInfoEntity::versionCode.eq(appInfo.versionCode))
                        .get().maybe().blockingGet()

                val operation = if (existingItem == null) {
                    Timber.v("Storing new app: ${appInfo.packageName}")
                    db.data.insert(appInfo)
                } else {
                    merge(from = appInfo, to = existingItem)
                    Timber.v("Updating existing app: ${existingItem.packageName}")
                    db.data.update(existingItem)
                }

                operation.subscribe { success, error ->
                    if (success != null) s.onComplete() else s.onError(error)
                }
            }

    /**
     * "Mergesert" a group of [DbAppInfo]s into the database. If an app already exists, new data will be merged in.
     * Otherwise, the app will be inserted.
     */
    fun put(appInfos: List<DbAppInfo>): Completable = Completable.concat(appInfos.map { put(it) })

    /**
     * Gets a single item from the database by package name.
     */
    fun get(packageName: String, versionCode: Int): Maybe<DbAppInfoEntity> =
            db.data.select(DbAppInfoEntity::class)
                    .where(DbAppInfoEntity::packageName.eq(packageName.toLowerCase()))
                    .and(DbAppInfoEntity::versionCode.eq(versionCode))
                    .get().maybe()

    /**
     * Gets a list of versions of an app from the database by package name.
     */
    fun get(packageName: String): Single<List<DbAppInfoEntity>> =
            db.data.select(DbAppInfoEntity::class)
                    .where(DbAppInfoEntity::packageName.eq(packageName.toLowerCase()))
                    .get().observable().toList()

    /**
     * Gets all [DbAppInfo]s from the database.
     */
    fun getAll(): Single<Map<String, List<DbAppInfoEntity>>> =
            Single.just(db.data.select(DbAppInfoEntity::class)
                    .get()
                    .groupBy { it.packageName })

    /**
     * Emits all changes to the database.
     */
    fun observeChanges(): Observable<ReactiveResult<DbAppInfoEntity>> = db.data.select(DbAppInfoEntity::class)
            .get()
            .observableResult()
            .doOnSubscribe { Timber.v("Subscribing to `observeChanges`") }
            .doAfterTerminate { Timber.v("Terminating `observeChanges`") }
            .doOnEach { Timber.v("Database change: ${it.value.toList().joinToString { it.packageName + ":" + it.versionCode }}") }

    /**
     * Clears all [DbAppInfoEntity] items from the database.
     */
    // There is a bug in requery where all apps aren't deleted somehow. Or, they are, but something is residual.
    fun clearAll(): Completable {
        Timber.v("Clearing all apps in database")
        return db.data.delete(DbAppInfoEntity::class).get().single().toCompletable()
    }

    fun remove(packageName: String, versionCode: Int? = null): Completable =
            if (versionCode == null) {
                db.data.delete(DbAppInfoEntity::class)
                        .where(DbAppInfoEntity::packageName.eq(packageName.toLowerCase()))
                        .get().single().toCompletable()
            } else {
                db.data.delete(DbAppInfoEntity::class)
                        .where(DbAppInfoEntity::packageName.eq(packageName.toLowerCase()))
                        .and(DbAppInfoEntity::versionCode.eq(versionCode))
                        .get().single().toCompletable()
            }


    /**
     * For each property of [AppInfo], replace the `to` value with the `from` value *iff* the `from` value is not null.
     * Warning: Uses java reflection.
     */
    private fun merge(from: DbAppInfo, to: DbAppInfo) {
        val declaredMethods = DbAppInfo::class.java.declaredMethods
        val getterPrefix = "get"
        val setterPrefix = "set"
        declaredMethods
                .filter { it.name.startsWith(getterPrefix) }
                .filter { it.invoke(from) != null }
                .forEach { getter ->
                    val setter = declaredMethods
                            .first { it.name.startsWith(setterPrefix) && it.name.substring(setterPrefix.count()) == getter.name.substring(getterPrefix.count()) }
//                    Timber.v("Setting method ${setter.name} on ${from.title} to non-null value ${getter.invoke(from)}")
                    setter.invoke(to, getter.invoke(from))
                }
    }
}