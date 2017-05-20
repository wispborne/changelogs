/*
 * Copyright (c) 2017.
 * Distributed under the GNU GPLv3 by David Whitman.
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * This source code is made available to help others learn. Please don't clone my app.
 */

package com.thunderclouddev.changelogs.ui.home

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.thunderclouddev.changelogs.AppUtils
import com.thunderclouddev.changelogs.BaseApp
import com.thunderclouddev.changelogs.InstalledPackages
import com.thunderclouddev.changelogs.databinding.AppInfoItemBinding
import com.thunderclouddev.changelogs.ui.BaseRecyclerViewAdapter
import com.thunderclouddev.dataprovider.AppInfosByPackage
import javax.inject.Inject

/**
 * @author David Whitman on 20 May, 2017.
 */
class AppInfosByPackageView : FrameLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    @Inject
    protected lateinit var installedPackages: InstalledPackages

    private var appInfo = AppInfosByPackage("bind.me.to.a.real.object", emptyList())

    private val binding: AppInfoItemBinding

    init {
        BaseApp.appInjector.inject(this)
        binding = AppInfoItemBinding.inflate(LayoutInflater.from(context), this, true)

    }

    fun bind(appInfosByPackage: AppInfosByPackage) {
        binding.model = AppInfoRecycler.AppInfoViewModel(appInfosByPackage, installedPackages)
    }


    data class AppInfoViewModel(val appInfosByPackage: AppInfosByPackage, private val installedPackages: InstalledPackages) : BaseRecyclerViewAdapter.ViewModel {
        val mostRecentAppInfo = appInfosByPackage.mostRecentVersion
        val installedAppInfo = AppUtils.findCurrentlyInstalledApp(appInfosByPackage, installedPackages)
        val numberOfVersionsTracked = appInfosByPackage.appInfos.size
    }
}