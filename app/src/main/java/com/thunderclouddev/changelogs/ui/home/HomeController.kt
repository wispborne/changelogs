/*
 * Copyright (c) 2017.
 * Distributed under the GNU GPLv3 by David Whitman.
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * This source code is made available to help others learn. Please don't clone my app.
 */

package com.thunderclouddev.changelogs.ui.home

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.Controller
import com.thunderclouddev.changelogs.BaseApp
import com.thunderclouddev.changelogs.InstalledPackages
import com.thunderclouddev.changelogs.R
import com.thunderclouddev.dataprovider.PlayClient
import com.thunderclouddev.utils.plusAssign
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import javax.inject.Inject

/**
 * @author David Whitman on 28 Mar, 2017.
 */
class HomeController : Controller() {
    @Inject lateinit var playClient: PlayClient
    @Inject lateinit var installedPackages: InstalledPackages

    lateinit var appInfoRecycler: AppInfoRecycler
    private val subs = CompositeDisposable()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        BaseApp.appInjector.inject(this)
        val view = inflater.inflate(R.layout.home_view, container)
        view.findViewById(R.id.the_button).setOnClickListener { makeCall() }
        view.findViewById(R.id.clearButton).setOnClickListener { playClient.clearDatabase() }
        appInfoRecycler = AppInfoRecycler(view.findViewById(R.id.home_recyclerview) as RecyclerView, installedPackages)
        return view
    }

    override fun onActivityStarted(activity: Activity) {
        super.onActivityStarted(activity)
        subscribeToDataSource()
        playClient.getApps()
    }

    override fun onActivityStopped(activity: Activity) {
        super.onActivityStopped(activity)
        subs.clear()
    }

    private fun makeCall() {
        val packageNames = listOf(
                "com.jtmcn.archwiki.viewer",
                "hu.supercluster.gameoflife",
                "eu.chainfire.recently",
                "com.RobotUnicornAttack",
                "com.habadigital.Unicorn",
                "com.discord",
                "com.simplehabit.simmplehabitapp",
                "BogusAppNameToTestErrorHandling",
                "com.runtastic.android",
                "com.thunderclouddev.changelogs",
                "com.curse.highwind")

        playClient.fetchLegacyBulkDetails(packageNames)
        playClient.fetchBulkDetails(packageNames)
        playClient.fetchDetails("com.discord")
        playClient.addTestAppToDb()
    }

    private fun subscribeToDataSource() {
        subs += playClient.bulkDetailsEvents
                .subscribe { response ->
                    when (response) {
                        is PlayClient.BulkDetailsCall.InFlight -> Timber.v("loading...")
                        is PlayClient.BulkDetailsCall.Success -> {
                            Timber.v("FdfeBulk: ${response.appInfos.joinToString { it.packageName }}")
                            playClient.getApps()
                        }
                        is PlayClient.BulkDetailsCall.Error -> Timber.e(response.error)
                    }
                }

        subs += playClient.legacyBulkDetailsEvents
                .subscribe { response ->
                    when (response) {
                        is PlayClient.LegacyBulkDetailsCall.Success -> {
                            Timber.v("MarketBulk: ${response.appInfos.joinToString { it.packageName }}")
                            playClient.getApps()
                        }
                        is PlayClient.LegacyBulkDetailsCall.Error -> Timber.e(response.error)
                    }
                }

        subs += playClient.detailsCallsEvents
                .subscribe { response ->
                    when (response) {
                        is PlayClient.DetailsCall.Success -> {
                            Timber.v("FdfeDetails: ${response.appInfo.packageName}")
                            playClient.getApps()
                        }
                        is PlayClient.DetailsCall.Error -> Timber.e(response.error)
                    }
                }

        subs += playClient.appInfoEvents.subscribe { response ->
            when (response) {
                is PlayClient.GetAppsOperation.Success -> {
                    appInfoRecycler.adapter.edit()
                            .replaceAll(response.appInfos.map { AppInfoRecycler.AppInfoViewModel(it, installedPackages) })
                            .commit()
                }
            }
        }
    }
}