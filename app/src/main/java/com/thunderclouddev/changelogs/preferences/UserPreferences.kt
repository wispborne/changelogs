/*
 * Copyright (c) 2017.
 * Distributed under the GNU GPLv3 by David Whitman.
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * This source code is made available to help others learn. Please don't clone my app.
 */

package com.thunderclouddev.changelogs.preferences

import javax.inject.Inject
import javax.inject.Singleton

/**
 * The user preferences.
 *
 * Created by david on 4/9/17.
 */
@Singleton
class UserPreferences @Inject constructor() : Preferences() {
    var emailAccount by stringPref()
    var showSystemAppsPreference by booleanPref()
    val allowAnonymousLogging by booleanPref()
}