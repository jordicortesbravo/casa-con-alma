package com.idealista.yaencontre.io.storage.fs

import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

class FSDriver(private val bucketPath: Path) {

    init {
        require(!bucketPath.toFile().exists() || bucketPath.toFile().isDirectory) { "bucketPath must be a directory" }
        this.bucketPath.toFile().mkdirs()
    }

    fun exists(key: FSObjectKey): Boolean {
        return key.value.toFile().exists()
    }

    fun get(key: FSObjectKey): Path {
        if(!key.value.toFile().exists()) {
            throw FileNotFoundException("${key.name} not found")
        }
        return key.value
    }

    fun list(keyPrefix: Path): List<Path> {
        return bucketPath.toFile().walk()
            .filter { it.isFile }
            .map(File::toPath)
            .filter { it.toString().startsWith(keyPrefix.toString()) }
            .toList()
    }

    fun put(key: FSObjectKey, inputStream: InputStream): Path {
        inputStream.use {
            Files.copy(it, key.value, StandardCopyOption.REPLACE_EXISTING)
        }
        return key.value
    }

    fun copy(sourceKey: FSObjectKey, targetKey: FSObjectKey): Path {
        return Files.copy(sourceKey.value, targetKey.value)
    }

    fun move(sourceKey: FSObjectKey, targetKey: FSObjectKey): Path {
        return Files.move(sourceKey.value, targetKey.value)
    }

    fun delete(key: FSObjectKey) {
        Files.delete(key.value)
    }
}