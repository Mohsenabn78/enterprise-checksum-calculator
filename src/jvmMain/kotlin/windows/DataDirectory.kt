package windows

import java.io.File

fun appDataDirectory(): File {
    val localAppDataPath = System.getenv("LOCALAPPDATA")
    val appName = "enterprise"

    val appDataDirectory = File(localAppDataPath, appName)
    if (!appDataDirectory.exists()) {
        appDataDirectory.mkdirs()
    }
    return appDataDirectory
}