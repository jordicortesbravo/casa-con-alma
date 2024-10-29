package com.idealista.yaencontre.io.storage

import java.io.InputStream

interface StorageObject {

    val name: String
    fun open(): InputStream
    fun move(target: String): StorageObject
    fun copy(target: String): StorageObject
    fun delete()
}
