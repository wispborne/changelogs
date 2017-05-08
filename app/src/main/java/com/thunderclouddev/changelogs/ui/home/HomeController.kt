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
import com.jakewharton.rxrelay2.PublishRelay
import com.thunderclouddev.changelogs.BaseApp
import com.thunderclouddev.changelogs.InstalledPackages
import com.thunderclouddev.changelogs.R
import com.thunderclouddev.changelogs.ui.Progress
import com.thunderclouddev.changelogs.ui.StateRenderer
import com.thunderclouddev.dataprovider.AppInfosByPackage
import com.thunderclouddev.dataprovider.PlayClient
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * The home screen. Displays a list of apps and their changelogs. Clicking on an app will go to the Details screen.
 * Sort, filter, and more are supported (well, they will be).
 *
 * @author David Whitman on 28 Mar, 2017.
 */
class HomeController @Inject constructor() : Controller(), HomeUi, HomeUi.Actions, HomeUi.Intentions, StateRenderer<HomeUi.State> {

    override var state: HomeUi.State = HomeUi.State.EMPTY

    lateinit var renderer: HomeRenderer
    lateinit var presenter: HomePresenter

    override fun render(state: HomeUi.State) {
        this.state = state
        renderer.render(state)
    }


    var scanForUpdatesRequest: PublishRelay<Unit> = PublishRelay.create()
    var loadCachedItems: PublishRelay<Unit> = PublishRelay.create()

    override fun displayItems(diffResult: RecyclerViewBinding<AppInfosByPackage>) {
        appInfoRecycler.adapter.edit()
                .replaceAll(diffResult.new.map { AppInfoRecycler.AppInfoViewModel(it, installedPackages) })
                .commit()
    }

    override fun showLoading(marketBulkDetailsProgress: Progress) {
        Timber.d("Loading - $marketBulkDetailsProgress")
    }

    override fun setRefreshEnabled(enabled: Boolean) {
        Timber.d("Refreshing - $enabled")
    }

    override fun showError(error: String) {
        Timber.e(error)
    }

    override fun hideError() {
        Timber.d("Hiding error")
    }

    override fun scanForUpdatesRequest(): Observable<Unit> =
            scanForUpdatesRequest
                    .debounce(500, TimeUnit.MILLISECONDS)
                    .filter { !state.isLoading() }

    override fun loadCachedItems(): Observable<Unit> =
            loadCachedItems
                    .filter { !state.isLoading() }

    @Inject lateinit var playClient: PlayClient
    @Inject lateinit var installedPackages: InstalledPackages

    lateinit var appInfoRecycler: AppInfoRecycler

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        BaseApp.appInjector.inject(this)
        renderer = HomeRenderer(this, AndroidSchedulers.mainThread(), Schedulers.computation())
        presenter = HomePresenter(this, this, playClient)

        val view = inflater.inflate(R.layout.home_view, container)
        view.findViewById(R.id.the_button).setOnClickListener { scanForUpdatesRequest.accept(Unit) } //{ makeCall() }
        view.findViewById(R.id.clearButton).setOnClickListener { playClient.clearDatabase() }
        appInfoRecycler = AppInfoRecycler(view.findViewById(R.id.home_recyclerview) as RecyclerView, installedPackages)
        return view
    }

    override fun onActivityStarted(activity: Activity) {
        super.onActivityStarted(activity)
        presenter.start()

        if (state == HomeUi.State.EMPTY) {
            loadCachedItems.accept(Unit)
        }
    }

    override fun onActivityStopped(activity: Activity) {
        super.onActivityStopped(activity)
        presenter.stop()
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
        playClient.bulkDetailsEvents
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

        playClient.legacyBulkDetailsEvents
                .subscribe { response ->
                    when (response) {
                        is PlayClient.LegacyBulkDetailsCall.Success -> {
                            Timber.v("MarketBulk: ${response.appInfos.joinToString { it.packageName }}")
                            playClient.getApps()
                        }
                        is PlayClient.LegacyBulkDetailsCall.Error -> Timber.e(response.error)
                    }
                }

        playClient.detailsCallsEvents
                .subscribe { response ->
                    when (response) {
                        is PlayClient.DetailsCall.Success -> {
                            Timber.v("FdfeDetails: ${response.appInfo.packageName}")
                            playClient.getApps()
                        }
                        is PlayClient.DetailsCall.Error -> Timber.e(response.error)
                    }
                }

        playClient.appInfoEvents.subscribe { response ->
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