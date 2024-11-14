package com.jcortes.deco.util.io.storage.fs

import com.jcortes.deco.util.io.storage.StorageObject
import java.io.InputStream

class FSObject private constructor(
    private val key: FSObjectKey,
    private val objectKeyFactory: FSObjectKeyFactory,
    private val driver: FSDriver
) : StorageObject {

    override val name: String
        get() = key.name

    override fun open(): InputStream {
        return key.value.toFile().inputStream()
    }

    override fun move(target: String): StorageObject {
        val targetKey = objectKeyFactory.fromObjectName(target)
        driver.move(key, targetKey)
        return fsObject(targetKey, objectKeyFactory, driver)
    }

    override fun copy(target: String): StorageObject {
        val targetKey = objectKeyFactory.fromObjectName(target)
        driver.copy(key, targetKey)
        return fsObject(targetKey, objectKeyFactory, driver)
    }

    override fun delete() {
        driver.delete(key)
    }

    override fun toString(): String {
        return "FSObject: $name"
    }

    companion object {
        fun fsObject(key: FSObjectKey, objectKeyFactory: FSObjectKeyFactory, driver: FSDriver): FSObject {
            return FSObject(key, objectKeyFactory, driver)
        }
    }
}