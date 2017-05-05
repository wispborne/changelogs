/*
 * Copyright (c) 2017.
 * Distributed under the GNU GPLv3 by David Whitman.
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * This source code is made available to help others learn. Please don't clone my app.
 */

package com.thunderclouddev.changelogs.ui.onboarding

import android.view.View
import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import com.thunderclouddev.changelogs.R
import com.thunderclouddev.changelogs.ResourceWrapper
import javax.inject.Inject

/**
 * @author David Whitman on 29 Mar, 2017.
 */
class IntroViewModel @Inject constructor(resourceWrapper: ResourceWrapper) {
    val explanation = resourceWrapper.getString(R.string.intro_explanation)
    val exitButtonClickListener = View.OnClickListener { android.os.Process.killProcess(android.os.Process.myPid()) }
    val nextButtonClicks: Relay<Unit> = PublishRelay.create<Unit>()
    val nextButtonClickListener = View.OnClickListener { nextButtonClicks.accept(Unit) }
}