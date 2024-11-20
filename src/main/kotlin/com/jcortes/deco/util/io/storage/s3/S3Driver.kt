package com.jcortes.deco.util.io.storage.s3

import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.*
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path


class S3Driver(private val bucket: String, private val s3Client: S3Client, localTempDir: Path? = null) {

    private val tempDir: Path = localTempDir ?: Files.createTempDirectory("S3temp")

    init {
        require(bucket.isNotBlank()) { "bucket name is mandatory" }
        checkConnection()
    }

    fun exists(key: S3ObjectKey): Boolean {
        return try {
            s3Client.headObject { it.bucket(bucket).key(key.value) }
            true
        } catch (e: NoSuchKeyException) {
            false
        }
    }

    fun get(key: S3ObjectKey): Path {
        return try {
            val destinationTempPath = localTempPath(key)
            s3Client.getObject({ it.bucket(bucket).key(key.value) }, destinationTempPath)
            destinationTempPath
        } catch (e: NoSuchKeyException) {
            throw FileNotFoundException("$key not found")
        }
    }

    fun list(keyPrefix: String): List<String> {
        return try {
            s3Client.listObjectsV2Paginator { it.bucket(bucket).prefix(keyPrefix) }
                .contents()
                .map { it.key() }
        } catch (e: Exception) {
            throw IOException("Failed to list blob with prefix '$keyPrefix'", e)
        }
    }

    fun put(key: S3ObjectKey, inputStream: InputStream, metadata: Map<String, String>? = null) {
        val localSourceTempPath = localTempPath(key)
        try {
            inputStream.use { Files.copy(it, localSourceTempPath) }
            s3Client.putObject({ it.bucket(bucket).key(key.value).contentType(metadata?.get("Content-Type")).cacheControl(metadata?.get("Cache-Control")) }, RequestBody.fromFile(localSourceTempPath))
        } catch (e: Exception) {
            throw IOException("Failed to upload data to $key", e)
        } finally {
            localSourceTempPath.toFile().delete()
        }
    }

    fun move(sourceKey: S3ObjectKey, targetKey: S3ObjectKey) {
        try {
            copy(sourceKey, targetKey)
            delete(sourceKey)
        } catch (e: Exception) {
            throw IOException("Failed to move blob from $sourceKey to $targetKey", e)
        }
    }

    fun copy(sourceKey: S3ObjectKey, targetKey: S3ObjectKey) {
        try {
            s3Client.copyObject {
                it.sourceBucket(bucket).sourceKey(sourceKey.value)
                    .destinationBucket(bucket).destinationKey(targetKey.value)
            }
        } catch (e: Exception) {
            throw IOException("Failed to copy blob from $sourceKey to $targetKey", e)
        }
    }

    fun delete(key: S3ObjectKey) {
        try {
            s3Client.deleteObject { it.bucket(bucket).key(key.value) }
        } catch (e: Exception) {
            throw IOException("Failed to delete blob $key", e)
        }
    }

    private fun checkConnection() {
        try {
            s3Client.listObjects { it.bucket(bucket).maxKeys(1).prefix("") }
        } catch (e: Exception) {
            throw RuntimeException("Not able to connect to S3. Failed during initialization", e)
        }
    }

    private fun localTempPath(key: S3ObjectKey): Path {
        return tempDir.resolve(System.nanoTime().toString() + key.value.replace("""[^a-zA-Z0-9.\-]""".toRegex(), "-"))
    }
}