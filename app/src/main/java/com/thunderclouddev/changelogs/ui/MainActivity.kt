/*
 * Copyright (c) 2017.
 * Distributed under the GNU GPLv3 by David Whitman.
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * This source code is made available to help others learn. Please don't clone my app.
 */

package com.thunderclouddev.changelogs.ui

import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.ViewGroup
import com.bluelinelabs.conductor.Conductor
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.thunderclouddev.changelogs.BaseApp
import com.thunderclouddev.changelogs.R
import com.thunderclouddev.changelogs.ui.home.HomeController
import com.thunderclouddev.deeplink.ui.ActionBarProvider
import javax.inject.Inject

/**
 * @author David Whitman on 28 Mar, 2017.
 */
class MainActivity : AppCompatActivity(), ActionBarProvider {
    override val actionBar: ActionBar
        get() = supportActionBar!!

    @Inject lateinit var homeController: HomeController

    private var router: Router? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BaseApp.appInjector.inject(this)

        setContentView(R.layout.main_activity)
        setSupportActionBar(findViewById(R.id.toolbar) as Toolbar)

        router = Conductor.attachRouter(this, findViewById(R.id.controller_container) as ViewGroup, savedInstanceState)
        if (!router!!.hasRootController()) {
//            if (!PlayApiClientWrapper(this).hasCachedAuthToken) {
            router!!.setRoot(RouterTransaction.with(homeController))
//            }
        }
    }

    override fun onBackPressed() {
        if (!router!!.handleBack()) {
            super.onBackPressed()
        }
    }
}