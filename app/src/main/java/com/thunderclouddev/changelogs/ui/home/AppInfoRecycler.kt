/*
 * Copyright (c) 2017.
 * Distributed under the GNU GPLv3 by David Whitman.
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * This source code is made available to help others learn. Please don't clone my app.
 */

package com.thunderclouddev.changelogs.ui.home

import android.databinding.DataBindingUtil
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.thunderclouddev.changelogs.AppUtils
import com.thunderclouddev.changelogs.InstalledPackages
import com.thunderclouddev.changelogs.R
import com.thunderclouddev.changelogs.databinding.AppInfoItemBinding
import com.thunderclouddev.changelogs.ui.BaseRecyclerViewAdapter
import com.thunderclouddev.changelogs.ui.IconBinder
import com.thunderclouddev.changelogs.ui.SortedListAdapter
import com.thunderclouddev.changelogs.ui.comparator.Comparators
import com.thunderclouddev.dataprovider.AppInfosByPackage

/**
* @author David Whitman on 2 April, 2017.
*/
class AppInfoRecycler(private val recyclerView: RecyclerView, installedPackages: InstalledPackages) {
    val adapter = Adapter()

    init {
        recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)
        recyclerView.adapter = adapter
    }

    class Adapter : SortedListAdapter<AppInfoViewModel>(
            AppInfoViewModel::class.java,
            Comparators.get()) {
        override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): ViewHolder<out AppInfoViewModel> {
            return AppInfoRecycler.ViewHolder(DataBindingUtil.inflate(inflater, R.layout.app_info_item, parent, false))
        }

        override fun areItemsTheSame(item1: AppInfoViewModel, item2: AppInfoViewModel) = item1.appInfosByPackage == item2.appInfosByPackage

        override fun areItemContentsTheSame(oldItem: AppInfoViewModel, newItem: AppInfoViewModel) = oldItem.appInfosByPackage.hashCode() == newItem.appInfosByPackage.hashCode()
    }

    internal data class ViewHolder(private val binding: AppInfoItemBinding)
        : BaseRecyclerViewAdapter.ViewHolder<AppInfoViewModel>(binding) {
        override fun performBind(item: AppInfoViewModel) {
            binding.model = item
            IconBinder.bindIcon(item.mostRecentAppInfo, binding.itemAppItemIcon)
        }
    }

    data class AppInfoViewModel(val appInfosByPackage: AppInfosByPackage, private val installedPackages: InstalledPackages) : BaseRecyclerViewAdapter.ViewModel {
        val mostRecentAppInfo = appInfosByPackage.mostRecentAppInfo
        val installedAppInfo = AppUtils.findCurrentlyInstalledApp(appInfosByPackage, installedPackages)
        val numberOfVersionsTracked = appInfosByPackage.appInfos.size
    }
}