package me.theclashfruit.rithle.util

import android.R.attr
import android.accounts.AbstractAccountAuthenticator
import android.accounts.Account
import android.accounts.AccountAuthenticatorResponse
import android.accounts.AccountManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import me.theclashfruit.rithle.onboarding.OnboardingActivity


class AuthenticatorUtil(context: Context?) : AbstractAccountAuthenticator(context) {
    private val context: Context = context!!

    override fun editProperties(
        response: AccountAuthenticatorResponse?,
        accountType: String?
    ): Bundle {
        TODO("Not yet implemented")
    }

    override fun addAccount(
        response: AccountAuthenticatorResponse?,
        accountType: String?,
        authTokenType: String?,
        requiredFeatures: Array<out String>?,
        options: Bundle?
    ): Bundle {
        val intent =
            Intent(context, OnboardingActivity::class.java)
                .putExtra(AccountManager.KEY_ACCOUNT_TYPE, attr.accountType)
                .putExtra("authenticatorType", authTokenType)
                .putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response)
                .putExtra("requiredFeatures", requiredFeatures)
                .putExtra("accountOptions", options)

        val bundle = Bundle()

        bundle.putParcelable(AccountManager.KEY_INTENT, intent)

        return bundle
    }

    override fun confirmCredentials(
        response: AccountAuthenticatorResponse?,
        account: Account?,
        options: Bundle?
    ): Bundle {
        TODO("Not yet implemented")
    }

    override fun getAuthToken(
        response: AccountAuthenticatorResponse?,
        account: Account?,
        authTokenType: String?,
        options: Bundle?
    ): Bundle {
        TODO("Not yet implemented")
    }

    override fun getAuthTokenLabel(authTokenType: String?): String {
        TODO("Not yet implemented")
    }

    override fun updateCredentials(
        response: AccountAuthenticatorResponse?,
        account: Account?,
        authTokenType: String?,
        options: Bundle?
    ): Bundle {
        TODO("Not yet implemented")
    }

    override fun hasFeatures(
        response: AccountAuthenticatorResponse?,
        account: Account?,
        features: Array<out String>?
    ): Bundle {
        TODO("Not yet implemented")
    }
}