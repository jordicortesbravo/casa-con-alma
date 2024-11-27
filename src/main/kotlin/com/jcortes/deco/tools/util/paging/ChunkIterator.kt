package com.jcortes.deco.tools.util.paging

import java.io.Closeable

data class DefaultChunkIteratorState(
    val lastProcessedId: Long?,
    val prevElements: Int
)

class ChunkIterator<S, T>(
    private val next: (state: S?) -> Chunk<S, T>,
    private val finalizer: (state: S?) -> Unit
): Iterator<T>, Closeable {

    data class Chunk<S, T>(val state: S? = null, val items: List<T>? = null)

    private var chunk: Chunk<S, T>? = null
    private var iterator: Iterator<T>? = null

    init {
        loadNext()
    }

    fun reset() {
        chunk = null
        loadNext()
    }

    override fun hasNext(): Boolean {
        if(iterator == null) {
            return false
        }
        if(hasNext(iterator)) {
            return true
        }
        loadNext()

        return hasNext(iterator)
    }

    override fun next(): T {
        return iterator?.next() ?: throw NoSuchElementException()
    }

    override fun close() {
        finalizer(chunk?.state)
    }

    private fun loadNext() {
        chunk = next(chunk?.state)
        iterator = chunk?.items?.iterator()
    }

    private fun hasNext(it: Iterator<T>?): Boolean {
        return it != null && it.hasNext()
    }
}
