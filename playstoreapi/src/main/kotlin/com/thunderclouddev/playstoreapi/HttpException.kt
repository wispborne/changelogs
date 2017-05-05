/*
 * Copyright (c) 2017.
 * Distributed under the GNU GPLv3 by David Whitman.
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * This source code is made available to help others learn. Please don't clone my app.
 */

package com.thunderclouddev.playstoreapi

/**
 * @author David Whitman on 27 Mar, 2017.
 */
data class HttpException(
    val code: Int,
    override val message: String
) : Exception(message)