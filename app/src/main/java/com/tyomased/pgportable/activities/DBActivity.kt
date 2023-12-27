package com.tyomased.pgportable.activities

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.tyomased.pgportable.R

class DBActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_db)

        val authCallback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                Log.i(javaClass.simpleName, "Fingerprint auth succeeded.")
                Toast.makeText(this@DBActivity, "Auth success", Toast.LENGTH_SHORT).show()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                Log.e(javaClass.simpleName, "Fingerprint auth failed.")
                finishAffinity()
            }
        }
        val biometricPrompt = BiometricPrompt(
            this, ContextCompat.getMainExecutor(this), authCallback
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Login to MyApp").setSubtitle("Log in using finger")
            .setNegativeButtonText("Cancel").build()

        val biometricManager = BiometricManager.from(this)
        if (biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS)
            biometricPrompt.authenticate(promptInfo)
    }
}
