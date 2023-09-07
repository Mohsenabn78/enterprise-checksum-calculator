package windows

import androidx.compose.runtime.MutableState
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.io.File

fun execute(applicationPath: String, taskState: MutableState<String>, checksum: MutableState<String>) {
    runBlocking {
        val appDataDirectory = appDataDirectory()
        taskState.value = TaskState.START
        delay(2000)

        taskState.value = TaskState.CLEAR
        deleteFilesAndDirectories(appDataDirectory)

        taskState.value = TaskState.EXTRACT
        extractZipFile(appDataDirectory)

        taskState.value = TaskState.CHECKSUM
        checksum.value = calculateSHA256(File(applicationPath))

        taskState.value = TaskState.FINISH
    }
}

object TaskState {
    const val START = "Start Task"
    const val CLEAR = "Clear All Files"
    const val EXTRACT = "Extract All Files"
    const val CHECKSUM = "Calculate Checksum"
    const val FINISH = "Finish Task"
}

