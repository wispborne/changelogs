/*
 * Copyright (c) 2017.
 * Distributed under the GNU GPLv3 by David Whitman.
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * This source code is made available to help others learn. Please don't clone my app.
 */

package com.thunderclouddev.playstoreapi.model

/**
 * Created by david on 4/15/17.
 */
data class Offer(
        val micros: Long? = null,
        val currencyCode: String? = null,
        val formattedAmount: String? = null,
        val offerType: Int? = null // Unsure about this field
)