/*
 * Copyright (c) 2017.
 * Distributed under the GNU GPLv3 by David Whitman.
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * This source code is made available to help others learn. Please don't clone my app.
 */

package com.thunderclouddev.changelogs.ui.home

import android.app.Activity
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.Controller
import com.jakewharton.rxrelay2.PublishRelay
import com.nextfaze.poweradapters.recyclerview.toRecyclerAdapter
import com.thunderclouddev.changelogs.BaseApp
import com.thunderclouddev.changelogs.InstalledPackages
import com.thunderclouddev.changelogs.R
import com.thunderclouddev.changelogs.ui.StateRenderer
import com.thunderclouddev.dataprovider.AppInfosByPackage
import com.thunderclouddev.dataprovider.Database
import com.thunderclouddev.dataprovider.PlayClient
import com.thunderclouddev.dataprovider.Progress
import com.thunderclouddev.deeplink.logging.timberkt.KTimber
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
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

    var scanForUpdatesRequest: PublishRelay<Unit> = PublishRelay.create()
    var loadCachedItems: PublishRelay<Unit> = PublishRelay.create()

    override fun render(state: HomeUi.State) {
        this.state = state
        renderer.render(state)
    }

    override fun displayItems(diffResult: RecyclerViewBinding<AppInfosByPackage>) {
//        appInfoRecycler.adapter.edit()
//                .replaceAll(diffResult.new.map { AppInfoRecycler.AppInfoViewModel(it, installedPackages) })
//                .commit()
    }

    override fun showLoading(marketBulkDetailsProgress: Progress) {
        KTimber.d { "Loading - $marketBulkDetailsProgress" }
    }

    override fun setRefreshEnabled(enabled: Boolean) {
        KTimber.d { "Refreshing - $enabled" }
    }

    override fun showError(error: String) {
        KTimber.e { error }
    }

    override fun hideError() {
        KTimber.d { "Hiding error" }
    }

    override fun scanForUpdatesRequest(): Observable<Unit> =
            scanForUpdatesRequest
                    .debounce(500, TimeUnit.MILLISECONDS)
                    .filter { !state.isLoading() }

    override fun loadCachedItems(): Observable<Unit> =
            loadCachedItems
                    .filter { !state.isLoading() }

    @Inject lateinit var playClient: PlayClient
    @Inject lateinit var database: Database
    @Inject lateinit var installedPackages: InstalledPackages

    lateinit var appInfoRecycler: AppInfoRecycler

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        BaseApp.appInjector.inject(this)
        renderer = HomeRenderer(this, AndroidSchedulers.mainThread(), Schedulers.computation())
        presenter = HomePresenter(this, this, playClient, installedPackages)

        val view = inflater.inflate(R.layout.home_view, container)
        view.findViewById(R.id.the_button).setOnClickListener { scanForUpdatesRequest.accept(Unit) } //{ makeCall() }
        view.findViewById(R.id.clearButton).setOnClickListener { database.clear() }
        view.findViewById(R.id.addButton).setOnClickListener { playClient.addTestAppToDb() }
        view.findViewById(R.id.addManyButton).setOnClickListener { playClient.addTestAppsToDb() }
        view.findViewById(R.id.removeButton).setOnClickListener { playClient.removeAppFromDb() }
        view.findViewById(R.id.refreshButton).setOnClickListener { database.refresh() }
        val recyclerView = view.findViewById(R.id.home_recyclerview) as RecyclerView
        val appInfoList = AppInfoList(database, installedPackages)
        val linearLayoutManager = LinearLayoutManager(recyclerView.context)
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = appInfoList.adapter.toRecyclerAdapter()
        recyclerView.addItemDecoration(DividerItemDecoration(activity, linearLayoutManager.orientation)
                .apply { this.setDrawable(ContextCompat.getDrawable(activity!!, R.drawable.list_divider)) })
        return view
    }

    override fun onActivityStarted(activity: Activity) {
        super.onActivityStarted(activity)
        presenter.start()
        subscribeToDataSource()

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

        playClient.scanForUpdates(packageNames)
//        playClient.fetchBulkDetails(packageNames)
//        playClient.fetchDetails("com.discord")
//        playClient.addTestAppToDb()
    }

    private fun subscribeToDataSource() {
//        database.databaseChanges
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe { response ->
//                    KTimber.v {"Updating recyclerview: $response"}
//
//                    when (response) {
//                        is PlayClient.DatabaseChange.NewItems -> {
////                            ArrayData()
////                            appInfoRecycler.adapter.edit()
////                                    .replaceAll(response.appInfos.map { AppInfoRecycler.AppInfoViewModel(it, installedPackages) })
////                                    .commit()
//                        }
////                        is PlayClient.DatabaseChange.ItemAdded -> {
////                            appInfoRecycler.adapter.edit()
////                                    .add(AppInfoRecycler.AppInfoViewModel(AppInfosByPackage(response.appInfo.packageName, listOf(response.appInfo)), installedPackages))
////                                    .commit()
////
////                        }
////                        is PlayClient.DatabaseChange.ItemChanged -> {
////                            val item = AppInfoRecycler.AppInfoViewModel(AppInfosByPackage(response.appInfo.packageName, listOf(response.appInfo)), installedPackages)
////                            appInfoRecycler.adapter.edit()
////                                    .remove(item)
////                                    .add(item)
////                                    .commit()
////                        }
////                        is PlayClient.DatabaseChange.ItemRemoved -> {
////                            appInfoRecycler.adapter.edit()
////                                    .remove(AppInfoRecycler.AppInfoViewModel(AppInfosByPackage(response.appInfo.packageName, listOf(response.appInfo)), installedPackages))
//////                            .replaceAll(response.appInfos.map { AppInfoRecycler.AppInfoViewModel(it, installedPackages) })
////                                    .commit()
////                        }
//                    }
//                }
    }
}