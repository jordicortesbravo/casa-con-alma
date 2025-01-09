package com.jcortes.deco.tools.util.infrastructure

import com.jcortes.deco.tools.util.image.WebPConverter
import org.junit.jupiter.api.Test
import java.io.File

class WebPTest {

    @Test
    fun jpegToWebpInsideFolder() {
        File("/Users/jcortes/workspace/crawler/images/ia-generated").listFiles()?.asList()?.parallelStream()?.forEach { folder ->
            folder.listFiles()?.forEach { file ->
                if (file.extension == "jpeg") {
                    val webpFile = WebPConverter.imageFileToWebpFile(
                        file.absolutePath,
                        file.absolutePath.replace(".jpeg", ".webp"),
                        80
                    )
                }
            }
        }
    }

    @Test
    fun generateResizedVersions() {
        File("/Users/jcortes/workspace/personal/crawler/images/ia-generated").listFiles()?.forEach { folder ->
            folder.listFiles()?.toList()?.parallelStream()?.forEach { file ->
                if (file.extension == "jpeg") {
                    WebPConverter.imageFileToWebpFile(
                        file.absolutePath,
                        file.absolutePath.replace(".jpeg", "-150.webp"),
                        90,
                        150,
                        150
                    )
                    WebPConverter.imageFileToWebpFile(
                        file.absolutePath,
                        file.absolutePath.replace(".jpeg", "-480.webp"),
                        90,
                        480,
                        480
                    )
                }
            }
        }
    }

    @Test
    fun jpegToWebp() {
        val webpFile = WebPConverter.imageFileToWebpFile(
            "/Users/jcortes/workspace/personal/crawler/src/main/resources/web/static/images/logo/casa-con-alma.png",
            "/Users/jcortes/workspace/personal/crawler/src/main/resources/web/static/images/logo/casa-con-alma.webp",
            80
        )
    }

    @Test
    fun jpegToWebPResized() {
        WebPConverter.imageFileToWebpFile(
            "/private/tmp/sample.jpeg",
            "/private/tmp/sample-150.webp",
            90,
            150,
            150
        )
        WebPConverter.imageFileToWebpFile(
            "/private/tmp/sample.jpeg",
            "/private/tmp/sample-480.webp",
            90,
            480,
            480
        )
    }

}