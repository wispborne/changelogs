/*
 * Copyright (c) 2017.
 * Distributed under the GNU GPLv3 by David Whitman.
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * This source code is made available to help others learn. Please don't clone my app.
 */

package com.thunderclouddev.persistence;

//import com.nhaarman.mockito_kotlin.mock
//import com.thunderclouddev.utils.anyKt
//import io.requery.Persistable
//import io.requery.kotlin.Selection
//import io.requery.kotlin.WhereAndOr
//import io.requery.query.Condition
//import io.requery.query.Expression
//import io.requery.query.Result
//import io.requery.reactivex.KotlinReactiveEntityStore
//import io.requery.reactivex.ReactiveResult
//import org.jetbrains.spek.api.Spek
//import org.jetbrains.spek.api.dsl.context
//import org.jetbrains.spek.api.dsl.given
//import org.junit.platform.runner.JUnitPlatform
//import org.junit.runner.RunWith
//import org.mockito.Mockito.`when`


//@RunWith(JUnitPlatform::class)
//object AppInfoDatabaseTest : Spek({
//    given("a database") {
//
//        var database: AppInfoDatabase
//        var requeryDb: RequeryDatabase<Persistable>
//        val debugMode = false
//        var dbStore: KotlinReactiveEntityStore<Persistable> = mock()
//
//        beforeEachTest {
//            requeryDb = mock()
//            dbStore = mock()
//            database = AppInfoDatabase(requeryDb, debugMode)
//
//            `when`(requeryDb.data).thenReturn(dbStore)
//        }
//
//        context("is that there are items in the database") {
//            var items: ReactiveResult<Persistable>
//
//            beforeEachTest {
//                items = ReactiveResult<Persistable>(Result<Persistable>(listOf(DbAppInfoEntity().apply {
//                    packageName = "com.test.app1"
//                }, DbAppInfoEntity().apply {
//                    packageName = "com.test.app2"
//                })))
//
//                val selection = mock<Selection<ReactiveResult<DbAppInfoEntity>>>()
//                val where = mock<WhereAndOr<ReactiveResult<DbAppInfoEntity>>>()
//                `when`(dbStore.select(DbAppInfoEntity::class)).thenReturn(selection)
//                `when`(selection.where(anyKt<Condition<Expression<String>, *>>())).thenReturn(where)
//                `when`(where.get()).thenReturn(items)
//            }
//        }
//    }
//})