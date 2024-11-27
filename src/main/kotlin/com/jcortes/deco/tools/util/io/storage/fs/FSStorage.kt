package com.jcortes.deco.tools.util.io.storage.fs

import com.jcortes.deco.tools.util.io.storage.Storage
import com.jcortes.deco.tools.util.io.storage.StorageObject
import java.io.FileNotFoundException
import java.io.InputStream
import java.nio.file.Path

class FSStorage(
    private val basePath: Path
) : Storage {

    class FSStorageBuilder private constructor(
        private var basePath: Path? = null,
        private var namespace: String? = null,
    ) {
        constructor() : this(null, null)

        fun basePath(basePath: Path) = apply { this.basePath = basePath }

        fun namespace(namespace: String?) = apply { this.namespace = namespace }

        fun build(): FSStorage {
            return FSStorage(
                basePath = calculateBasePath(
                    basePath = requireNotNull(basePath) { "basePath must be set" },
                    namespace = namespace
                )
            )
        }

        private fun calculateBasePath(basePath: Path, namespace: String?): Path {
            return namespace?.let {
                FSObjectKey.validateObjectName(namespace)
                basePath.resolve(namespace)
            } ?: basePath
        }
    }

    private val driver = FSDriver(basePath)
    private val objectKeyFactory = FSObjectKeyFactory(basePath)

    override fun partition(partitionName: String): Storage {
        return builder().basePath(basePath).namespace(partitionName).build()
    }

    override fun exists(objectName: String): Boolean {
        return driver.exists(objectKeyFactory.fromObjectName(objectName))
    }

    override fun get(objectName: String): StorageObject {
        val key = objectKeyFactory.fromObjectName(objectName)
        if (!driver.exists(key)) {
            throw FileNotFoundException("$objectName not found")
        }
        return fsObject(key)
    }

    override fun list(prefix: String): List<StorageObject> {
        return driver.list(objectKeyFactory.filterKeyFromObjectNamePrefix(prefix))
            .map { path -> fsObject(objectKeyFactory.fromObjectKey(path)) }
            .sortedBy { it.name }
    }

    override fun put(objectName: String, inputStream: InputStream, metadata: Map<String, String>?): StorageObject {
        val key = objectKeyFactory.fromObjectName(objectName)
        driver.put(key, inputStream)
        return fsObject(key)
    }

    override fun move(sourceObjectName: String, targetObjectName: String): StorageObject {
        val targetKey = objectKeyFactory.fromObjectName(targetObjectName)
        driver.move(objectKeyFactory.fromObjectName(sourceObjectName), targetKey)
        return fsObject(targetKey)
    }

    override fun copy(sourceObjectName: String, targetObjectName: String): StorageObject {
        val targetKey = objectKeyFactory.fromObjectName(targetObjectName)
        driver.copy(objectKeyFactory.fromObjectName(sourceObjectName), targetKey)
        return fsObject(targetKey)
    }

    override fun delete(objectName: String) {
        driver.delete(objectKeyFactory.fromObjectName(objectName))
    }

    private fun fsObject(key: FSObjectKey): StorageObject {
        return FSObject.fsObject(key, objectKeyFactory, driver)
    }

    companion object {
        fun builder(): FSStorageBuilder {
            return FSStorageBuilder()
        }
    }
}