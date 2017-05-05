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
        object InFlight : BulkDetailsCall()
        data class Error(val error: Throwable) : BulkDetailsCall()
        data class Success(val appInfos: List<AppInfo>) : BulkDetailsCall()
    }

    sealed class LegacyBulkDetailsCall {
        object InFlight : LegacyBulkDetailsCall()
        data class Error(val error: Throwable) : LegacyBulkDetailsCall()
        data class Success(val appInfos: List<AppInfo>) : LegacyBulkDetailsCall()
    }

    sealed class DetailsCall {
        object InFlight : DetailsCall()
        data class Error(val error: Throwable) : DetailsCall()
        data class Success(val appInfo: AppInfo) : DetailsCall()
    }

    sealed class GetAppsOperation {
        object InFlight : GetAppsOperation()
        data class Error(val error: Throwable) : GetAppsOperation()
        data class Success(val appInfos: List<AppInfosByPackage>) : GetAppsOperation()
    }

    val bulkDetailsEvents: BehaviorRelay<BulkDetailsCall> = BehaviorRelay.create()
    val legacyBulkDetailsEvents: BehaviorRelay<LegacyBulkDetailsCall> = BehaviorRelay.create()
    val detailsCallsEvents: BehaviorRelay<DetailsCall> = BehaviorRelay.create()
    val appInfoEvents: BehaviorRelay<GetAppsOperation> = BehaviorRelay.create()

    fun getApps() {
        appInfoEvents.accept(GetAppsOperation.InFlight)

        database.getAll()
                .map { items -> items.map { AppInfosByPackage(it.key, it.value.map { it.toModel() }) } }
                .subscribe({ result ->
                    appInfoEvents.accept(GetAppsOperation.Success(result))
                }, { error ->
                    appInfoEvents.accept(GetAppsOperation.Error(error))
                })
    }

    fun fetchBulkDetails(packageNames: List<String>) {
        bulkDetailsEvents.accept(BulkDetailsCall.InFlight)
        playApiClient.rxecute(PlayRequest.BulkDetailsRequest(packageNames))
                .flatMap { response ->
                    database.put(response.map { it.toModel().toDatabaseModel() })
                            .toSingleDefault(response)
                }
                .subscribe { response, error ->
                    if (error == null) {
                        bulkDetailsEvents.accept(BulkDetailsCall.Success(response.map { it.toModel() }))
                    } else {
                        bulkDetailsEvents.accept(BulkDetailsCall.Error(error))
                    }
                }
    }

    fun fetchLegacyBulkDetails(packageNames: List<String>) {
        legacyBulkDetailsEvents.accept(LegacyBulkDetailsCall.InFlight)
        legacyClient.rxecute(MarketRequest.AppsRequest(packageNames))
                .flatMap { response ->
                    database.put(response.map { it.toModel().toDatabaseModel() })
                            .toSingleDefault(response)
                }
                .subscribe { response, error ->
                    if (error == null) {
                        legacyBulkDetailsEvents.accept(LegacyBulkDetailsCall.Success(response.map { it.toModel() }))
                    } else {
                        legacyBulkDetailsEvents.accept(LegacyBulkDetailsCall.Error(error))
                    }
                }
    }

    fun fetchDetails(packageName: String) {
        detailsCallsEvents.accept(DetailsCall.InFlight)
        playApiClient.rxecute(PlayRequest.DetailsRequest(packageName))
                .flatMap { response ->
                    database.put(response.toModel().toDatabaseModel())
                            .toSingleDefault(response)
                }
                .subscribe { response, error ->
                    if (error == null) {
                        detailsCallsEvents.accept(DetailsCall.Success(response.toModel()))
                    } else {
                        detailsCallsEvents.accept(DetailsCall.Error(error))
                    }
                }
    }

    fun addTestAppToDb() {
        database.get("com.thunderclouddev.changelogs")
                .subscribe { result ->
                    database.put(result.apply { ++versionCode })
                            .subscribe {
                                getApps()
                            }
                }
    }

    fun clearDatabase() {
        database.clearAll().subscribe {
            getApps()
        }
    }

}