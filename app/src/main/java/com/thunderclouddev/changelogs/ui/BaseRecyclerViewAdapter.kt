/*
 * Copyright (c) 2017.
 * Distributed under the GNU GPLv3 by David Whitman.
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * This source code is made available to help others learn. Please don't clone my app.
 */

package com.thunderclouddev.changelogs.ui

import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView

/**
 * Created by David Whitman on 15 Jan, 2017.
 */
abstract class BaseRecyclerViewAdapter<T : RecyclerView.ViewHolder> : RecyclerView.Adapter<T>() {
    interface OnClickListener<in T> {
        fun onItemClick(item: T)
    }

    interface ViewModel

    abstract class ViewHolder<T : ViewModel>(itemView: ViewDataBinding) : RecyclerView.ViewHolder(itemView.root) {

        var currentItem: T? = null
            private set

        fun bind(item: T) {
            currentItem = item
            performBind(item)
        }

        protected abstract fun performBind(item: T)

    }
}