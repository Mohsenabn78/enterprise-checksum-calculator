package windows

import java.io.File

fun deleteFilesAndDirectories(directory: File) {
    if (directory.exists() && directory.isDirectory) {
        val files = directory.listFiles()
        if (files != null) {
            for (file in files) {
                if (file.isDirectory) {
                    deleteFilesAndDirectories(file)
                } else {
                    file.delete()
                }
            }
        }
    }
}