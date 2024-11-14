package com.jcortes.deco.util.io.storage.s3

import com.jcortes.deco.util.io.AutoCloseInputStream
import com.jcortes.deco.util.io.storage.StorageObject
import java.io.InputStream
import java.nio.file.Path

class S3Object private constructor(
    private val key: S3ObjectKey,
    private val fileHandler: FileHandler,
    private val objectKeyFactory: S3ObjectKeyFactory,
    private val driver: S3Driver
) : StorageObject {

    private sealed interface FileHandler {
        fun handle(key: S3ObjectKey): Path

        class LocalFileHandler(private val path: Path) : FileHandler {
            override fun handle(key: S3ObjectKey): Path {
                return path
            }
        }

        class RemoteFileHandler(private val s3Driver: S3Driver) : FileHandler {
            override fun handle(key: S3ObjectKey): Path {
                return s3Driver.get(key)
            }
        }
    }

    override val name: String
        get() = key.name

    override fun open(): InputStream {
        val file = fileHandler.handle(key).toFile()
        return AutoCloseInputStream(file.inputStream()) { file.delete() }
    }

    override fun move(target: String): StorageObject {
        val targetKey = objectKeyFactory.fromObjectName(target)
        driver.move(key, targetKey)
        return remoteS3Object(targetKey, objectKeyFactory, driver)
    }

    override fun copy(target: String): StorageObject {
        val targetKey = objectKeyFactory.fromObjectName(target)
        driver.copy(key, targetKey)
        return remoteS3Object(targetKey, objectKeyFactory, driver)
    }

    override fun delete() {
        driver.delete(key)
    }

    override fun toString(): String {
        return "S3Object: $name"
    }

    companion object {
        fun localS3Object(key: S3ObjectKey, path: Path, objectKeyFactory: S3ObjectKeyFactory, driver: S3Driver): S3Object {
            return S3Object(key, FileHandler.LocalFileHandler(path), objectKeyFactory, driver)
        }

        fun remoteS3Object(key: S3ObjectKey, objectKeyFactory: S3ObjectKeyFactory, driver: S3Driver): S3Object {
            return S3Object(key, FileHandler.RemoteFileHandler(driver), objectKeyFactory, driver)
        }
    }
}