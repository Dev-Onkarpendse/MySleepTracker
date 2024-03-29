package com.onkar.mysleeptracker.receiver

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.SleepSegmentRequest
import com.onkar.mysleeptracker.MainApplication
import com.onkar.mysleeptracker.data.SleepRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Resubscribes to Sleep data if the device is rebooted.
 */
class BootReceiver : BroadcastReceiver() {

    // Used to launch coroutines (non-blocking way to insert data).
    private val scope: CoroutineScope = MainScope()

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "onReceive action: " + intent.action)

        val repository: SleepRepository = (context.applicationContext as MainApplication).repository

        scope.launch {
            // TODO: Request Sleep API upon boot complete
            val subscribedToSleepData = repository.subscribedToSleepDataFlow.first()
            if (subscribedToSleepData) {
                subscribeToSleepSegmentUpdates(
                    context = context,
                    pendingIntent = SleepReceiver.createSleepReceiverPendingIntent(context)
                )
            }
        }
    }

    /**
     * Subscribes to sleep data.
     * Note: Permission isn't missing, it's in the manifest but only for 29+ version. The lint
     * check is the 28 and below version of the activity recognition permission (needed for
     * accessing sleep data).
     */
    @SuppressLint("MissingPermission")
    private fun subscribeToSleepSegmentUpdates(context: Context, pendingIntent: PendingIntent) {
        Log.d(TAG, "subscribeToSleepSegmentUpdates()")
        if (activityRecognitionPermissionApproved(context)) {
            val task =
                ActivityRecognition.getClient(context).requestSleepSegmentUpdates(
                    pendingIntent,
                    // Registers for both [SleepSegmentEvent] and [SleepClassifyEvent] data.
                    SleepSegmentRequest.getDefaultSleepSegmentRequest()
                )

            task.addOnSuccessListener {
                Log.d(TAG, "Successfully subscribed to sleep data from boot.")
            }
            task.addOnFailureListener { exception ->
                Log.d(TAG, "Exception when subscribing to sleep data from boot: $exception")
                val repository = (context.applicationContext as MainApplication).repository
                unsubscribeStatusForSleepData(repository)
            }
        } else {
            Log.d(TAG, "Failed to subscribed to sleep data from boot; Permission removed.")
            val repository = (context.applicationContext as MainApplication).repository
            unsubscribeStatusForSleepData(repository)
        }
    }

    /**
     * Updates the app's boolean for sleep subscription status.
     *
     * Note: This happens because an exception occurred or the permission was removed, so the app
     * is no longer subscribed to sleep data.
     */
    private fun unsubscribeStatusForSleepData(repository: SleepRepository) = scope.launch {
        repository.updateSubscribedToSleepData(false)
    }

    private fun activityRecognitionPermissionApproved(context: Context): Boolean {
        return PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACTIVITY_RECOGNITION
        )
    }

    companion object {
        const val TAG = "BootReceiver"
    }
}
