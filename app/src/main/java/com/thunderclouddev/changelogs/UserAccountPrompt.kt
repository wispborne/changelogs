/*
 * Copyright (c) 2017.
 * Distributed under the GNU GPLv3 by David Whitman.
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * This source code is made available to help others learn. Please don't clone my app.
 */

package com.thunderclouddev.changelogs

import android.accounts.Account
import android.accounts.AccountManager
import android.app.Activity
import android.content.Intent
import com.google.android.gms.common.AccountPicker
import com.thunderclouddev.utils.empty
import timber.log.Timber
import java.util.*

/**
 * @author David Whitman
 */
class UserAccountPrompt(val accountType: String, val accounts: Array<Account>?) {
    companion object {
        val CHOOSE_ACCOUNT_REQUEST_CODE = 0
        private val REQUEST_AUTHORIZATION = 1

        interface Listener {
            fun onAccountSelected(account: Account)

            fun onFailure(errorReason: String)
        }
    }

    var listener = object : Listener {
        override fun onFailure(errorReason: String) {
        }

        override fun onAccountSelected(account: Account) {
        }
    }

    fun createChooseAccountIntent(listener: Listener): Intent {
        this.listener = listener
        return createChooseAccountIntent(accounts)
    }

    fun handleOnActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CHOOSE_ACCOUNT_REQUEST_CODE) {
            if (data != null) {
                val accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)
                val account = Account(accountName, accountType)
                listener.onAccountSelected(account)
            } else {
                val errorMessage = "Intent data was null. Perhaps user dismissed the user account prompt."
                Timber.w(NullPointerException(), errorMessage)
                listener.onFailure(errorMessage)
            }
        } else if (requestCode == REQUEST_AUTHORIZATION) {
            // wtf is this?
        }
    }

    private fun createChooseAccountIntent(accountChoices: Array<Account>?): Intent {
        val accountChoicesArray = if (accountChoices != null) ArrayList(Arrays.asList(*accountChoices)) else null
        return AccountPicker.newChooseAccountIntent(null, accountChoicesArray,
            arrayOf(accountType), true, null, null, null, null)
    }
}