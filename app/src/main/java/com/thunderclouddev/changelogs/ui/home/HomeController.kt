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

    @Inject lateinit var playClient: PlayClient
    @Inject lateinit var database: Database
    @Inject lateinit var installedPackages: InstalledPackages

    var scanForUpdatesRequest: PublishRelay<Unit> = PublishRelay.create()
    var loadCachedItems: PublishRelay<Unit> = PublishRelay.create()
    val clearDatabase: PublishRelay<Unit> = PublishRelay.create()
    val refresh: PublishRelay<Unit> = PublishRelay.create()

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

    override fun clearDatabase(): Observable<Unit> = clearDatabase
            .debounce(500, TimeUnit.MILLISECONDS)

    override fun refresh(): Observable<Unit> = refresh
            .debounce(500, TimeUnit.MILLISECONDS)

    override fun addTestApp(): Observable<Unit> = PublishRelay.create<Unit>()
    override fun addTestApps(): Observable<Unit> = PublishRelay.create<Unit>()
    override fun removeTestApp(): Observable<Unit> = PublishRelay.create<Unit>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        BaseApp.appInjector.inject(this)
        renderer = HomeRenderer(this, AndroidSchedulers.mainThread(), Schedulers.computation())
        presenter = HomePresenter(this, this, playClient, installedPackages, database)

        val view = inflater.inflate(R.layout.home_view, container)
        view.findViewById<View>(R.id.the_button).setOnClickListener { scanForUpdatesRequest.accept(Unit) } //{ makeCall() }
        view.findViewById<View>(R.id.clearButton).setOnClickListener { clearDatabase.accept(Unit) }
        view.findViewById<View>(R.id.addButton).setOnClickListener { addTestApp() }
        view.findViewById<View>(R.id.addManyButton).setOnClickListener { addTestApps() }
        view.findViewById<View>(R.id.removeButton).setOnClickListener { removeTestApp() }
        view.findViewById<View>(R.id.refreshButton).setOnClickListener { refresh.accept(Unit) }
        val recyclerView = view.findViewById<RecyclerView>(R.id.home_recyclerview)
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
    }
}