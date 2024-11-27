package com.jcortes.deco.tools.util.io.storage.s3

import com.jcortes.deco.tools.util.io.storage.StorageObjectKeyFactory

class S3ObjectKeyFactory(namespace: String?): StorageObjectKeyFactory<String> {

    private val namespace: String? = namespace?.ifBlank { null }

    init {
        this.namespace?.let { S3ObjectKey.validateObjectName(it) }
    }

    override fun namespace(namespace: String): String {
        return this.namespace?.let { "$it/$namespace" } ?: namespace
    }

    override fun filterKeyFromObjectNamePrefix(objectNamePrefix: String): String {
        return namespace?.let { "$it/$objectNamePrefix" } ?: objectNamePrefix
    }

    override fun fromObjectName(objectName: String): S3ObjectKey {
        val objectKey = namespace?.let { "$it/$objectName" } ?: objectName
        return S3ObjectKey.of(objectName, objectKey)
    }

    override fun fromObjectKey(objectKey: String): S3ObjectKey {
        val objectName = namespace?.let { objectKey.substringAfter("$it/") } ?: objectKey
        return S3ObjectKey.of(objectName, objectKey)
    }
}