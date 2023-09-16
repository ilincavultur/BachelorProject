package com.example.digitaldiaryba.util

import android.net.Uri
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

// START https://stackoverflow.com/questions/70951701/alternative-method-for-deprecated-encode-foruri-method
fun encodeUri(uri: Uri) : String {
    return URLEncoder.encode(
        uri.toString(),
        StandardCharsets.UTF_8.toString()
    )
}
// END https://stackoverflow.com/questions/70951701/alternative-method-for-deprecated-encode-foruri-method

fun decodeUri(uri: String) : String {
    return URLDecoder.decode(
        uri.toString(),
        StandardCharsets.UTF_8.toString()
    )
}