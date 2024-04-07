package com.tutorials.workmanager

import android.content.Context
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UploadWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    companion object {
        const val KEY_INPUT: String = "input string"
        const val KEY_OUTPUT: String = "output string"
    }

    override fun doWork(): Result {

        val count: Int = inputData.getInt(KEY_INPUT, 11)
        for (i in 1..count) {
            Thread.sleep(1000)
            if (!isStopped) {
                println("worker : $i")
            }
        }

        val formatter = SimpleDateFormat("yyyy MM dd HH:mm:ss", Locale.US)
        val output = formatter.format(Date())

        val data = Data.Builder().putString(KEY_OUTPUT, output).build()

        return Result.success(data)
    }

}
