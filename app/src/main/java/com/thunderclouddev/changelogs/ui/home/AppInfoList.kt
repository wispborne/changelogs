/*
 * Copyright (c) 2017.
 * Distributed under the GNU GPLv3 by David Whitman.
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * This source code is made available to help others learn. Please don't clone my app.
 */

package com.thunderclouddev.changelogs.ui.home

import com.nextfaze.poweradapters.ViewFactory
import com.nextfaze.poweradapters.binding.BindViewFunction
import com.nextfaze.poweradapters.data.ArrayData
import com.nextfaze.poweradapters.data.DataBindingAdapter
import com.thunderclouddev.changelogs.InstalledPackages
import com.thunderclouddev.dataprovider.AppInfosByPackage
import com.thunderclouddev.dataprovider.Database
import com.thunderclouddev.deeplink.logging.timberkt.KTimber

/**
 * @author David Whitman on 20 May, 2017.
 */
class AppInfoList(private val database: Database,
                  private val installedPackages: InstalledPackages) {
    val binder = com.nextfaze.poweradapters.binding.Binder.create(
            ViewFactory { parent -> AppInfosByPackageView(parent.context) },
            BindViewFunction<AppInfosByPackage, AppInfosByPackageView> { container, item, v, holder ->
                v.bind(item)
            })

    var data = object : ArrayData<AppInfosByPackage>() {
        override fun load(): List<AppInfosByPackage> {
            KTimber.v { "Loading adapter with ${database.lastSnapshot.count()} items." }
            return database.lastSnapshot
        }
    }

    val adapter = DataBindingAdapter(binder, data)

    init {
        // Refresh the adapter with new list whenever database changes are observed
        database.databaseChanges
                .subscribe {
                    data.refresh()
                }
    }
}