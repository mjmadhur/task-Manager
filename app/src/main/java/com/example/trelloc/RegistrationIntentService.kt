package com.example.trelloc

import com.microsoft.windowsazure.messaging.NotificationHub

import com.google.android.gms.tasks.OnSuccessListener

import android.preference.PreferenceManager

import android.content.SharedPreferences

import android.content.Intent

import android.app.IntentService
import android.icu.util.TimeUnit
import android.util.Log
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging
import java.lang.Exception


class RegistrationIntentService : IntentService(TAG) {
    var FCM_token: String? = null

    private val hub: NotificationHub? = null
    override fun onHandleIntent(intent: Intent?) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        var resultString: String? = null
        var regID: String? = null
        var storedToken: String? = null
        try {
            FirebaseMessaging.getInstance().token
                .addOnSuccessListener { instanceIdResult ->
                    FCM_token = instanceIdResult
                    Log.d(
                        TAG,
                        "FCM Registration Token: $FCM_token"
                    )
                }
            java.util.concurrent.TimeUnit.SECONDS.sleep(1)

            // Storing the registration ID that indicates whether the generated token has been
            // sent to your server. If it is not stored, send the token to your server.
            // Otherwise, your server should have already received the token.
            if (sharedPreferences.getString("registrationID", null).also { regID = it } == null) {
                val hub = NotificationHub(
                    NotificationSettings().HubName,
                    NotificationSettings().HubListenConnectionString, this
                )
                Log.d(
                    TAG,
                    "Attempting a new registration with NH using FCM token : $FCM_token"
                )
                regID = hub.register(FCM_token).registrationId

                // If you want to use tags...
                // Refer to : https://azure.microsoft.com/documentation/articles/notification-hubs-routing-tag-expressions/
                // regID = hub.register(token, "tag1,tag2").getRegistrationId();
                resultString = "New NH Registration Successfully - RegId : $regID"
                Log.d(TAG, resultString)
                sharedPreferences.edit().putString("registrationID", regID).apply()
                sharedPreferences.edit().putString("FCMtoken", FCM_token).apply()
            } else if (sharedPreferences.getString("FCMtoken", "")
                    .also { storedToken = it } != FCM_token
            ) {
                val hub = NotificationHub(
                    NotificationSettings().HubName,
                    NotificationSettings().HubListenConnectionString, this
                )
                Log.d(
                    TAG,
                    "NH Registration refreshing with token : $FCM_token"
                )
                regID = hub.register(FCM_token).registrationId

                // If you want to use tags...
                // Refer to : https://azure.microsoft.com/documentation/articles/notification-hubs-routing-tag-expressions/
                // regID = hub.register(token, "tag1,tag2").getRegistrationId();
                resultString = "New NH Registration Successfully - RegId : $regID"
                Log.d(TAG, resultString)
                sharedPreferences.edit().putString("registrationID", regID).apply()
                sharedPreferences.edit().putString("FCMtoken", FCM_token).apply()
            } else {
                resultString = "Previously Registered Successfully - RegId : $regID"
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to complete registration".also {
                resultString = it
            }, e)
            // If an exception happens while fetching the new token or updating registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
        }

        // Notify UI that registration has completed.

    }
        companion object {
            private const val TAG = "RegIntentService"
        }
    }
