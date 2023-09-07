package windows

import java.io.File
import java.io.FileInputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

fun extractZipFile(outputDirectory: File) {

    val path = "external.zip"
    val zipResource = object {}.javaClass.classLoader.getResourceAsStream(path)
    val zipFile = File.createTempFile("temp", ".zip")
    zipResource.use { inputStream ->
        zipFile.outputStream().use { outputStream ->
            inputStream.copyTo(outputStream)
        }
    }

    val buffer = ByteArray(1024)
    val zipInputStream = ZipInputStream(FileInputStream(zipFile))
    var entry: ZipEntry? = zipInputStream.nextEntry
    while (entry != null) {
        val outputFile = File(outputDirectory, entry.name)
        if (!outputFile.parentFile.exists()) {
            outputFile.parentFile.mkdirs()
        }
        if (!entry.isDirectory) {
            val outputStream = outputFile.outputStream()
            var len = zipInputStream.read(buffer)
            while (len > 0) {
                outputStream.write(buffer, 0, len)
                len = zipInputStream.read(buffer)
            }
            outputStream.close()
        }
        entry = zipInputStream.nextEntry
    }
    zipInputStream.closeEntry()
    zipInputStream.close()

    zipFile.deleteRecursively()
}