/*
 * Copyright (c) 2017.
 * Distributed under the GNU GPLv3 by David Whitman.
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * This source code is made available to help others learn. Please don't clone my app.
 */

package com.thunderclouddev.dataprovider

/**
 * @author David Whitman on 17 June, 2017.
 */
data class Offer(
        val micros: Long? = null,
        val currencyCode: String? = null,
        val formattedAmount: String? = null,
        val offerType: Int? = null // Unsure about this field
)