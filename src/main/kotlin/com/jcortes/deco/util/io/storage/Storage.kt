package com.jcortes.deco.util.io.storage

import com.jcortes.deco.util.io.storage.StorageObject
import java.io.InputStream

interface Storage {

    fun partition(partitionName: String): Storage

    fun exists(objectName: String): Boolean

    fun get(objectName: String): StorageObject

    fun list(prefix: String = ""): List<StorageObject>

    fun put(objectName: String, inputStream: InputStream, metadata: Map<String, String>? = null): StorageObject

    fun move(sourceObjectName: String, targetObjectName: String): StorageObject

    fun copy(sourceObjectName: String, targetObjectName: String): StorageObject

    fun delete(objectName: String)
}