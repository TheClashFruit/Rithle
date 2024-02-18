package me.theclashfruit.rithle.modrinth.models

import com.google.gson.annotations.SerializedName

data class UserPayoutData(
    @SerializedName("balance")            val balance: Float,
    @SerializedName("payout_wallet")      val payoutWallet: String,
    @SerializedName("payout_wallet_type") val payoutWalletType: String,
    @SerializedName("payout_address")     val payoutAddress: String,
)
