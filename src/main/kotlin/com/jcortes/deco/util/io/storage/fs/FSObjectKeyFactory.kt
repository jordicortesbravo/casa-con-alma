package com.idealista.yaencontre.io.storage.fs

import com.idealista.yaencontre.io.storage.StorageObjectKeyFactory
import java.nio.file.Path

class FSObjectKeyFactory(private val basePath: Path): StorageObjectKeyFactory<Path> {

    init {
        require(basePath.toFile().isDirectory) { "basePath must be a directory" }
    }

    override fun namespace(namespace: String): String {
        return basePath.resolve(normalizeObjectName(namespace)).toString()
    }

    override fun filterKeyFromObjectNamePrefix(objectNamePrefix: String): Path {
        return basePath.resolve(normalizeObjectName(objectNamePrefix))
    }

    override fun fromObjectName(objectName: String): FSObjectKey {
        val objectKey = basePath.resolve(normalizeObjectName(objectName))
        return FSObjectKey.of(objectName, objectKey)
    }

    override fun fromObjectKey(objectKey: Path): FSObjectKey {
        val objectName = objectKey.fileName.toString().replace("__", "/")
        return FSObjectKey.of(objectName, objectKey)
    }

    private fun normalizeObjectName(objectName: String): String {
        return objectName.replace("/", "__")
    }
}