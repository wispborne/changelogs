/*
 * Copyright (c) 2017.
 * Distributed under the GNU GPLv3 by David Whitman.
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * This source code is made available to help others learn. Please don't clone my app.
 */

package com.thunderclouddev.persistence

import android.content.Context
import io.requery.reactivex.KotlinReactiveEntityStore
import io.requery.sql.KotlinEntityDataStore
import javax.inject.Inject


internal class RequeryDatabase<T : Any> @Inject constructor(context: Context) {
    companion object {
        private val DATABASE_VERSION = 3
        var debugMode = false
    }

    val data: KotlinReactiveEntityStore<T> by lazy {
        // override onUpgrade to handle migrating to a new version
        val source = io.requery.android.sqlite.DatabaseSource(context, Models.DEFAULT, DATABASE_VERSION)

        if (debugMode) {
            // use this in development mode to drop and recreate the tables on every upgrade
            source.setTableCreationMode(io.requery.sql.TableCreationMode.DROP_CREATE)
        }

        val configuration = source.configuration
        KotlinReactiveEntityStore<T>(KotlinEntityDataStore(configuration))
    }
}