package com.jcortes.deco.tools.util.io.storage



interface StorageObjectKeyFactory<T: Any> {

    fun namespace(namespace: String): String

    fun filterKeyFromObjectNamePrefix(objectNamePrefix: String): T

    fun fromObjectName(objectName: String): StorageObjectKey<T>

    fun fromObjectKey(objectKey: T): StorageObjectKey<T>
}