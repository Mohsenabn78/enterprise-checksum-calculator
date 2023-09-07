package common

fun String.removeAllSpaces(): String {
    return this.replace(Regex("\\s+"), "")
}

fun String.replaceBase64Char(): String {
    return this.replace("=", "").replace("+", "-").replace("/", "_")
}