package com.jcortes.deco.tools.util.image

import io.github.mojtabaJ.cwebp.CWebp
import io.github.mojtabaJ.cwebp.WebpConverter
import java.io.File

object WebPConverter {

    fun imageFileToWebpFile(imageFilePath: String, webpPathFile: String, quality: Int): File = WebpConverter.imageFileToWebpFile(imageFilePath, webpPathFile, quality)

    fun imageFileToWebpFile(imageFilePath: String, webpPathFile: String, quality: Int, width: Int, height: Int): File {
        val cwebp = CWebp().quality(quality).input(imageFilePath).output(webpPathFile).resize(width, height)
        cwebp.execute()
        return File(webpPathFile)
    }
}