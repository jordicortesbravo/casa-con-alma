package com.jcortes.deco.tools.util.infrastructure

import io.github.mojtabaJ.cwebp.WebpConverter
import org.junit.jupiter.api.Test
import java.io.File

class WebPTest {

    @Test
    fun jpegToWebpInsideFolder() {
        File("/Users/jcortes/workspace/crawler/images/ia-generated").listFiles()?.asList()?.parallelStream()?.forEach { folder ->
            folder.listFiles()?.forEach { file ->
                if (file.extension == "jpeg") {
                    val webpFile = WebpConverter.imageFileToWebpFile(
                        file.absolutePath,
                        file.absolutePath.replace(".jpeg", ".webp"),
                        80
                    )
                }
            }
        }
    }

    @Test
    fun jpegToWebp() {
        val webpFile = WebpConverter.imageFileToWebpFile(
            "/Users/jcortes/workspace/personal/crawler/src/main/resources/web/static/images/logo/casa-con-alma.png",
            "/Users/jcortes/workspace/personal/crawler/src/main/resources/web/static/images/logo/casa-con-alma.webp",
            80
        )
    }
}