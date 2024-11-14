package com.jcortes.deco.util.io.storage.s3

import com.jcortes.deco.util.io.storage.Storage
import com.jcortes.deco.util.io.storage.StorageObject
import software.amazon.awssdk.services.s3.S3Client
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.nio.file.Path


/**
 * @see 'https://docs.aws.amazon.com/AmazonS3/latest/userguide/object-keys.html'
 */
class S3Storage private constructor(
    private val bucket: String,
    private val s3Client: S3Client,
    private val localTempPath: Path?,
    private val objectKeyFactory: S3ObjectKeyFactory,
) : Storage {

    class S3ObjectStorageBuilder private constructor(
        private var s3Client: S3Client? = null,
        private var bucket: String? = null,
        private var namespace: String? = null,
        private var localTempPath: Path? = null
    ) {
        constructor() : this(null, null, null, null)

        fun s3Client(s3Client: S3Client) = apply { this.s3Client = s3Client }
        fun bucket(bucket: String) = apply { this.bucket = bucket }
        fun namespace(namespace: String?) = apply { this.namespace = namespace }
        fun localTempPath(localTempPath: Path?) = apply { this.localTempPath = localTempPath }
        fun build(): S3Storage {
            return S3Storage(
                bucket = requireNotNull(bucket) { "bucket is mandatory" },
                s3Client = requireNotNull(s3Client) { "s3Client is mandatory" },
                localTempPath = localTempPath,
                objectKeyFactory = S3ObjectKeyFactory(namespace)
            )
        }
    }

    private val driver = S3Driver(bucket, s3Client, localTempPath)

    override fun partition(partitionName: String): Storage {
        return builder().bucket(bucket).s3Client(s3Client).localTempPath(localTempPath).namespace(objectKeyFactory.namespace(partitionName)).build()
    }

    override fun exists(objectName: String): Boolean {
        return driver.exists(objectKeyFactory.fromObjectName(objectName))
    }

    override fun get(objectName: String): StorageObject {
        val key = objectKeyFactory.fromObjectName(objectName)
        return try {
            val localTempPath = driver.get(key)
            S3Object.localS3Object(key, localTempPath, objectKeyFactory, driver)
        } catch (e: FileNotFoundException) {
            throw FileNotFoundException("$objectName not found due to ${e.message}")
        }
    }

    override fun list(prefix: String): List<StorageObject> {
        return try {
            driver.list(objectKeyFactory.filterKeyFromObjectNamePrefix(prefix))
                .map { objectKey -> remoteS3Object(objectKeyFactory.fromObjectKey(objectKey)) }
                .sortedBy { it.name }
        } catch (e: Exception) {
            throw IOException("Failed to list files with prefix '$prefix'", e)
        }
    }

    override fun put(objectName: String, inputStream: InputStream, metadata: Map<String, String>?): StorageObject {
        val key = objectKeyFactory.fromObjectName(objectName)
        try {
            driver.put(key, inputStream, metadata)
            return remoteS3Object(key)
        } catch (e: Exception) {
            throw IOException("Failed to upload data to $objectName", e)
        }
    }

    override fun move(sourceObjectName: String, targetObjectName: String): StorageObject {
        val targetKey = objectKeyFactory.fromObjectName(targetObjectName)
        driver.move(objectKeyFactory.fromObjectName(sourceObjectName), targetKey)
        return remoteS3Object(targetKey)
    }

    override fun copy(sourceObjectName: String, targetObjectName: String): StorageObject {
        val targetKey = objectKeyFactory.fromObjectName(targetObjectName)
        driver.copy(objectKeyFactory.fromObjectName(sourceObjectName), targetKey)
        return remoteS3Object(targetKey)
    }

    override fun delete(objectName: String) {
        driver.delete(objectKeyFactory.fromObjectName(objectName))
    }

    private fun remoteS3Object(key: S3ObjectKey): S3Object {
        return S3Object.remoteS3Object(key, objectKeyFactory, driver)
    }

    companion object {
        fun builder() = S3ObjectStorageBuilder()
    }
}