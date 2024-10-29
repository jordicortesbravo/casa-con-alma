package com.idealista.yaencontre.io

import java.io.InputStream

class AutoCloseInputStream(private val inputStream: InputStream, private val autoClose: Boolean = false, private val closeAction: (() -> Unit)? = null) : InputStream() {

    override fun read(): Int {
        val byte = inputStream.read()
        if(byte == EOF && autoClose) {
            close()
        }
        return byte
    }

    override fun read(b: ByteArray): Int {
        val bytesRead = inputStream.read(b)
        if(bytesRead == EOF && autoClose) {
            close()
        }
        return bytesRead
    }

    override fun read(b: ByteArray, off: Int, len: Int): Int {
        val bytesRead = inputStream.read(b, off, len)
        if(bytesRead == EOF && autoClose) {
            close()
        }
        return bytesRead
    }

    override fun skip(n: Long): Long {
        return inputStream.skip(n)
    }

    override fun available(): Int {
        return inputStream.available()
    }

    override fun close() {
        inputStream.close()
        closeAction?.invoke()
    }

    override fun mark(readlimit: Int) {
        inputStream.mark(readlimit)
    }

    override fun reset() {
        inputStream.reset()
    }

    override fun markSupported(): Boolean {
        return inputStream.markSupported()
    }

    private companion object {
        const val EOF = -1
    }
}