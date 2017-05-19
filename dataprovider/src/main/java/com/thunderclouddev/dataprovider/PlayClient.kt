/*
 * Copyright (c) 2017.
 * Distributed under the GNU GPLv3 by David Whitman.
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * This source code is made available to help others learn. Please don't clone my app.
 */

package com.thunderclouddev.dataprovider

import com.jakewharton.rxrelay2.BehaviorRelay
import com.thunderclouddev.persistence.AppInfoDatabase
import com.thunderclouddev.playstoreapi.PlayApiClientWrapper
import com.thunderclouddev.playstoreapi.PlayRequest
import com.thunderclouddev.playstoreapi.legacyMarketApi.LegacyApiClientWrapper
import com.thunderclouddev.playstoreapi.legacyMarketApi.MarketRequest
import com.thunderclouddev.playstoreapi.model.ApiAppInfo
import io.reactivex.Single
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.coroutines.experimental.buildSequence

/**
 * Exposes data streams that return results from the Google Play api based on the public methods called.
 * Created by david on 4/17/17.
 */
class PlayClient constructor(
        private val playApiClient: PlayApiClientWrapper,
        private val legacyClient: LegacyApiClientWrapper,
        private val database: AppInfoDatabase
) {
    sealed class BulkDetailsCall {
        data class InFlight(val progress: Progress) : BulkDetailsCall()
        data class Error(val error: Throwable) : BulkDetailsCall()
        data class Success(val appInfos: List<AppInfo>) : BulkDetailsCall()
    }

    sealed class LegacyBulkDetailsCall {
        data class InFlight(val progress: Progress) : LegacyBulkDetailsCall()
        data class Error(val error: Throwable) : LegacyBulkDetailsCall()
        data class Success(val appInfos: List<AppInfo>) : LegacyBulkDetailsCall()
    }

    sealed class DetailsCall {
        data class InFlight(val progress: Progress) : DetailsCall()
        data class Error(val error: Throwable) : DetailsCall()
        data class Success(val appInfo: AppInfo) : DetailsCall()
    }

    sealed class DatabaseChange {
        data class ItemChanged(val appInfo: AppInfo) : DatabaseChange()
        data class ItemRemoved(val appInfo: AppInfo) : DatabaseChange()
        data class ItemAdded(val appInfo: AppInfo) : DatabaseChange()
        data class NewItems(val appInfos: List<AppInfosByPackage>) : DatabaseChange()
    }

    val bulkDetailsEvents: BehaviorRelay<BulkDetailsCall> = BehaviorRelay.create()
    val legacyBulkDetailsEvents: BehaviorRelay<LegacyBulkDetailsCall> = BehaviorRelay.create()
    val detailsCallsEvents: BehaviorRelay<DetailsCall> = BehaviorRelay.create()
    val appInfoEvents: BehaviorRelay<DatabaseChange> = BehaviorRelay.create()

    fun getApps() {
        database.observeChanges()
                .buffer(500, TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
                .doOnSubscribe { Timber.v("Subscribed to database changes") }
                .doOnEach { Timber.v("Database change observed: ${it.value}") }
                .subscribe({ results ->
                    results.forEach { result ->
                        val mapped = result
                                .toList()
                                .map { it.toModel() }
                                .groupBy { it.packageName }
                                .map { AppInfosByPackage(it.key, it.value) }
                        appInfoEvents.accept(DatabaseChange.NewItems(mapped))
                        Timber.d("Observed: ${mapped.joinToString { it.packageName }}")
                    }
                    //                    appInfoEvents.accept(DatabaseChange.Success(result))
                }, { error ->
                    Timber.e(error)
                    //                    appInfoEvents.accept(DatabaseChange.Error(error))
                })
    }

    fun scanForUpdates(packageNames: List<String>) {
        fetchFdfeBulkDetailsOnly(packageNames)
                .map { list ->
                    list.filter { (packageName, versionCode) ->
                        // Remove apps where the database already contains the same package and version code
                        database.get(packageName)
                                .blockingGet()
                                .maxBy { it.versionCode }
                                ?.versionCode?.equals(versionCode)
                                ?: true
                    }
                }
                .flatMap { newApps -> fetchLegacyBulkDetailsOnly(newApps.map { it.packageName }) }
                .subscribe({}, { error -> Timber.e(error) })
    }

    fun fetchDetails(packageName: String) {
        detailsCallsEvents.accept(DetailsCall.InFlight(Progress(max = 1, inProgress = true)))
        fetchDetailsOnly(packageName)
                .subscribe { response, error ->
                    if (error == null) {
                        detailsCallsEvents.accept(DetailsCall.Success(response.toModel()))
                    } else {
                        detailsCallsEvents.accept(DetailsCall.Error(error))
                    }
                }
    }

    fun clearDatabase() {
        database.clearAll().subscribe {
            //            getApps()
        }
    }

    private fun fetchFdfeBulkDetails(packageNames: List<String>) {
        bulkDetailsEvents.accept(BulkDetailsCall.InFlight(Progress(max = packageNames.size, inProgress = true)))
        fetchFdfeBulkDetailsOnly(packageNames)
                .subscribe { response, error ->
                    if (error == null) {
                        bulkDetailsEvents.accept(BulkDetailsCall.Success(response.map { it.toModel() }))
                    } else {
                        bulkDetailsEvents.accept(BulkDetailsCall.Error(error))
                    }
                }
    }

    private fun fetchLegacyBulkDetails(packageNames: List<String>) {
        legacyBulkDetailsEvents.accept(LegacyBulkDetailsCall.InFlight(Progress(max = packageNames.size, inProgress = true)))
        fetchLegacyBulkDetailsOnly(packageNames)
                .subscribe { response, error ->
                    if (error == null) {
                        legacyBulkDetailsEvents.accept(LegacyBulkDetailsCall.Success(response.map { it.toModel() }))
                    } else {
                        legacyBulkDetailsEvents.accept(LegacyBulkDetailsCall.Error(error))
                    }
                }
    }

    private fun fetchLegacyBulkDetailsOnly(packageNames: List<String>): Single<List<ApiAppInfo>> {
        return legacyClient.rxecute(MarketRequest.AppsRequest(packageNames))
                .flatMap { response ->
                    database.put(response.map { it.toModel().toDatabaseModel() })
                            .toSingleDefault(response)
                }
    }

    private fun fetchFdfeBulkDetailsOnly(packageNames: List<String>): Single<List<ApiAppInfo>> {
        return playApiClient.rxecute(PlayRequest.BulkDetailsRequest(packageNames))
                .flatMap { response ->
                    database.put(response.map { it.toModel().toDatabaseModel() })
                            .toSingleDefault(response)
                }
    }

    private fun fetchDetailsOnly(packageName: String): Single<ApiAppInfo> {
        return playApiClient.rxecute(PlayRequest.DetailsRequest(packageName))
                .flatMap { response ->
                    database.put(response.toModel().toDatabaseModel())
                            .toSingleDefault(response)
                }
    }

    fun addTestAppToDb() {
        playApiClient.rxecute(PlayRequest.DetailsRequest("com.thunderclouddev.changelogs"))
                .map { it.toModel().toDatabaseModel() }
                .subscribe({
                    database.put(it.apply { versionCode = Random().nextInt() })
                            .subscribe {
                                //                                getApps()
                            }
                }, { Timber.e(it) })
    }

    fun addTestAppsToDb() {
        playApiClient.rxecute(PlayRequest.DetailsRequest("com.thunderclouddev.changelogs"))
                .map {
                    buildSequence {
                        for (i in 0..250) {
                            yield(it.copy(packageName = it.packageName.plus(i), versionCode = Random().nextInt()))
                        }
                    }.toList()
                }
                .map { it.map { it.toModel().toDatabaseModel() } }
                .subscribe({
                    database.put(it)
                            .subscribe {
                                //                                getApps()
                            }
                }, { Timber.e(it) })
    }

    fun removeAppFromDb() {
        database.remove("com.thunderclouddev.changelogs")
                .subscribe()
    }
}