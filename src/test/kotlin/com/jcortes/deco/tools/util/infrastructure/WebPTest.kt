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
                        file.absolutePath.replace(".jpeg", "-100.webp"),
                        90,
                        100,
                        100
                    )
                    WebPConverter.imageFileToWebpFile(
                        file.absolutePath,
                        file.absolutePath.replace(".jpeg", "-400.webp"),
                        90,
                        400,
                        400
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
            "/private/tmp/8813717249435397062.jpeg",
            "/private/tmp/8813717249435397062.webp",
            90
        )
//        WebPConverter.imageFileToWebpFile(
//            "/private/tmp/8813717249435397062.jpeg",
//            "/private/tmp/8813717249435397062-100.webp",
//            90,
//            100,
//            100
//        )
//        WebPConverter.imageFileToWebpFile(
//            "/private/tmp/8813717249435397062.jpeg",
//            "/private/tmp/8813717249435397062-400.webp",
//            90,
//            400,
//            400
//        )
//        WebPConverter.imageFileToWebpFile(
//            "/private/tmp/8813717249435397062.jpeg",
//            "/private/tmp/8813717249435397062-380.webp",
//            90,
//            380,
//            380
//        )
//        WebPConverter.imageFileToWebpFile(
//            "/private/tmp/8813717249435397062.jpeg",
//            "/private/tmp/8813717249435397062-150.webp",
//            90,
//            150,
//            150
//        )
//        WebPConverter.imageFileToWebpFile(
//            "/private/tmp/8813717249435397062.jpeg",
//            "/private/tmp/8813717249435397062-480.webp",
//            90,
//            480,
//            480
//        )
    }

}