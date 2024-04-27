package com.grappim.docuvault.utils.files

import android.net.Uri
import java.io.File

data class CameraTakePictureData(
    val uri: Uri,
    val file: File
) {
    companion object {
        fun empty(): CameraTakePictureData = CameraTakePictureData(
            uri = Uri.EMPTY,
            file = File("")
        )
    }
}
