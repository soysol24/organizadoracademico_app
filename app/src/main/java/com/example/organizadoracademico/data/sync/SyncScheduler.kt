package com.example.organizadoracademico.data.sync

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

interface SyncScheduler {
    fun scheduleNow()
    fun schedulePeriodic()
}

class WorkManagerSyncScheduler(
    private val context: Context
) : SyncScheduler {

    override fun scheduleNow() {
        WorkManager.getInstance(context).enqueueUniqueWork(
            ONE_TIME_SYNC,
            ExistingWorkPolicy.KEEP,
            OneTimeWorkRequestBuilder<SyncWorker>()
                .setConstraints(defaultConstraints())
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 15, TimeUnit.SECONDS)
                .build()
        )
    }

    override fun schedulePeriodic() {
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            PERIODIC_SYNC,
            ExistingPeriodicWorkPolicy.KEEP,
            PeriodicWorkRequestBuilder<SyncWorker>(15, TimeUnit.MINUTES)
                .setConstraints(defaultConstraints())
                .build()
        )
    }

    private fun defaultConstraints(): Constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    companion object {
        private const val ONE_TIME_SYNC = "organizador_sync_now"
        private const val PERIODIC_SYNC = "organizador_sync_periodic"
    }
}

