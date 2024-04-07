package com.tutorials.workmanager

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.tutorials.workmanager.ui.theme.WorkManagerTheme
import java.util.UUID

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WorkManagerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WorkManagerActions()
                }
            }
        }
    }

    override fun onDestroy() {
        WorkManager.getInstance(this).cancelAllWork()
        super.onDestroy()
    }
}

@Composable
fun WorkManagerActions(modifier: Modifier = Modifier) {

    Column(
        modifier = Modifier
            .fillMaxHeight(1f)
            .fillMaxSize(1f),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        val context = LocalContext.current
        val lifecycleOwner = LocalLifecycleOwner.current // TODO lifecycleowner
        val state = remember { mutableStateOf("") }
        val id = remember { mutableStateOf(UUID.randomUUID()) }
        Button(onClick = {
            id.value = startWork(context)
            WorkManager.getInstance(context).getWorkInfoByIdLiveData(id.value)
                .observe(lifecycleOwner) { workInfo ->
                    state.value = workInfo?.state?.name!!
                    if (workInfo.state.isFinished) {
                        state.value = workInfo.outputData.getString(UploadWorker.KEY_OUTPUT).toString()
                    }
                }
        }) {
            Text(text = "Start")
        }
        if (state.value.isNotBlank()) {
            Button(onClick = { stopWorker(context, id.value) }) {
                Text(text = "stop Worker")
            }
        }

        Text(
            text = "State ${state.value}!",
            modifier = modifier
        )

    }
}

private fun startWork(context: Context): UUID {

    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
//        .setRequiresCharging(true)
        .build()

    val inputData = Data.Builder()
        .putInt(UploadWorker.KEY_INPUT, 6)
        .build()

    val workRequest = OneTimeWorkRequest.Builder(UploadWorker::class.java)
        .setConstraints(constraints)
        .setInputData(inputData)
        .addTag("OneTimeWork")
        .build()
    WorkManager.getInstance(context).enqueue(workRequest)
    return workRequest.id
//    WorkManager.getInstance(context).cancelWorkById(workRequest.id)
}

private fun stopWorker(context: Context, id: UUID) {

    WorkManager.getInstance(context).cancelAllWorkByTag("OneTimeWork")
    WorkManager.getInstance(context).cancelAllWork()
    WorkManager.getInstance(context).cancelWorkById(id)

}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    WorkManagerTheme {
        WorkManagerActions()
    }
}