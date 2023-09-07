package windows

import common.replaceBase64Char
import java.io.File


fun calculateSHA256(apkFile: File): String {

    val appDataDirectory = appDataDirectory()

    val apkSignerFileName = "apksigner.bat"
    val apkSignerPath = File("${appDataDirectory}/external", apkSignerFileName).absolutePath

    val grepFileName = "grep.exe"
    val grepPath = File("${appDataDirectory}/external", grepFileName).absolutePath

    val xxdFileName = "xxd.exe"
    val xxdPath = File("${appDataDirectory}/external", xxdFileName).absolutePath

    val openSSlFileName = "openssl.exe"
    val openSSlPath = File("${appDataDirectory}/external", openSSlFileName).absolutePath


    val command = """
        $apkSignerPath verify -print-certs "${apkFile.absolutePath}" | $grepPath -Po "(?<=SHA-256 digest:) .*" | $xxdPath -r -p | $openSSlPath base64"
    """.trimIndent()

    val process = Runtime.getRuntime().exec(command)
    process.waitFor()

    val exitCode = process.exitValue()
    val inputStream = process.inputStream.bufferedReader().readText()
    val errorStream = process.errorStream.bufferedReader().readText()

    return if (exitCode == 0) {
        "${inputStream.replaceBase64Char().trimIndent()}="
    } else {
        throw Throwable(errorStream)
    }

}

