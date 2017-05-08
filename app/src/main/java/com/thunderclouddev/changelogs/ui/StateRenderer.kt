/*
 * Copyright (c) 2017.
 * Distributed under the GNU GPLv3 by David Whitman.
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * This source code is made available to help others learn. Please don't clone my app.
 */

package com.thunderclouddev.changelogs.ui

/**
 * Created by David Whitman on 07 May, 2017.
 */
interface StateRenderer<in S> {
    /**
     * Accepts a POKO representing the current state of the view in order to render it on to the screen of the user.
     * @param state state to render
     */
    fun render(state: S)
}