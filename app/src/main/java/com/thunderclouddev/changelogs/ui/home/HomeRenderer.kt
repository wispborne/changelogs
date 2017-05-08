/*
 * Copyright (c) 2017.
 * Distributed under the GNU GPLv3 by David Whitman.
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * This source code is made available to help others learn. Please don't clone my app.
 */

package com.thunderclouddev.changelogs.ui.home

import android.support.v7.util.DiffUtil
import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import com.thunderclouddev.changelogs.ui.StateRenderer
import com.thunderclouddev.dataprovider.AppInfosByPackage
import com.thunderclouddev.utils.plusAssign
import com.thunderclouddev.utils.scanMap
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable

class HomeRenderer constructor(
        val uiActions: HomeUi.Actions,
        val main: Scheduler,
        val comp: Scheduler
) : StateRenderer<HomeUi.State> {
    val disposables = CompositeDisposable()

    val state: Relay<HomeUi.State> = PublishRelay.create<HomeUi.State>()//.toSerialized()

    init {
        start()
    }

    fun start() {
        disposables += state
                .map { it.appInfos }
                .distinctUntilChanged()
                .scanMap(
                        emptyList<AppInfosByPackage>(),
                        { old: List<AppInfosByPackage>, new: List<AppInfosByPackage> -> calculateDiff(old, new) })
                .subscribeOn(comp)
                .observeOn(main)
                .subscribe { diff ->
                    uiActions.displayItems(diff)
                }
    }

    override fun render(state: HomeUi.State) {
        this.state.accept(state)
    }

    companion object {
        fun calculateDiff(old: List<AppInfosByPackage>, new: List<AppInfosByPackage>): RecyclerViewBinding<AppInfosByPackage> {
            val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    val oldItem = old[oldItemPosition]
                    val newItem = new[newItemPosition]
                    return oldItem.packageName == newItem.packageName
                }

                override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    val oldItem = old[oldItemPosition]
                    val newItem = new[newItemPosition]
                    return oldItem.hashCode() == newItem.hashCode() // todo double-check this
                }

                override fun getOldListSize(): Int = old.size

                override fun getNewListSize(): Int = new.size

            })

            return RecyclerViewBinding(new = new, diff = diff)
        }
    }
}