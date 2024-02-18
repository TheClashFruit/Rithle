package me.theclashfruit.rithle.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import me.theclashfruit.rithle.util.AuthenticatorUtil

class AuthenticatorService : Service() {
    override fun onBind(intent: Intent): IBinder {
        return AuthenticatorUtil(this).iBinder
    }
}