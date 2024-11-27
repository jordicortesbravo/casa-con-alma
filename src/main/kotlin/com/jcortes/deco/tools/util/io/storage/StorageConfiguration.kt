package com.jcortes.deco.tools.util.io.storage

import com.jcortes.deco.tools.util.io.storage.fs.FSStorage
import com.jcortes.deco.tools.util.io.storage.s3.S3Storage
import org.springframework.context.ApplicationContext
import software.amazon.awssdk.services.s3.S3Client
import java.net.URI
import java.nio.file.Paths

class StorageConfiguration {

    companion object {

        /**
         * Creates a storage from a URI
         *
         * Examples:
         *    - file:///tmp/path
         *    - s3://bucket-name
         */
        fun createStorage(uri: URI, applicationContext: ApplicationContext): Storage {
            return when (uri.scheme) {
                "file" -> FSStorage.builder()
                    .basePath(Paths.get(uri))
                    .build()

                "s3" -> {
                    val s3Client = applicationContext.getBean(S3Client::class.java)
                    S3Storage.builder()
                        .bucket(uri.host)
                        .s3Client(s3Client)
                        .namespace(uri.path.substringAfter('/'))
                        .build()
                }

                else -> throw IllegalArgumentException("Invalid storage type: ${uri.scheme}")
            }
        }
    }
}