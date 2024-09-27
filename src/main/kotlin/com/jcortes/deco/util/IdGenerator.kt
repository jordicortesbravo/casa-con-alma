package com.jcortes.deco.util

import java.time.Instant
import java.util.concurrent.atomic.AtomicLong
import kotlin.math.pow


interface IdGenerator {
    fun nextId(): Long
}

class SnowflakeIdGenerator(private val nodeId: Int): IdGenerator {

    // Variables concurrentes
    private val lastTimestamp = AtomicLong(-1L)
    private val sequence = AtomicLong(0L)

    init {
        require(!(nodeId < 0 || nodeId > MAX_NODE_ID)) { "NodeId must be between 0 and $MAX_NODE_ID" }
    }

    override fun nextId(): Long {
        var currentTimestamp = timestamp()

        // Acceder de forma atómica a lastTimestamp
        val lastTs = lastTimestamp.get()

        // Si el reloj retrocedió, ajustar el timestamp
        if (currentTimestamp < lastTs) {
            currentTimestamp = handleClockBackward(lastTs - currentTimestamp)
        }

        if (currentTimestamp == lastTs) {
            // Incrementar la secuencia de forma atómica
            val newSeq = (sequence.incrementAndGet() and MAX_SEQUENCE.toLong())

            // Si agotamos la secuencia en el mismo milisegundo, esperamos el siguiente milisegundo
            if (newSeq == 0L) {
                currentTimestamp = waitNextMillis(currentTimestamp)
            }
        } else {
            // Reiniciar la secuencia de forma atómica
            sequence.set(0L)
        }

        // Actualizar la última marca de tiempo de forma atómica
        lastTimestamp.set(currentTimestamp)

        // Generar el ID combinando timestamp, nodeId y sequence
        var id = (currentTimestamp shl (TOTAL_BITS - EPOCH_BITS))
        id = id or ((nodeId shl (TOTAL_BITS - EPOCH_BITS - NODE_ID_BITS)).toLong())
        id = id or sequence.get()

        return id
    }

    // Manejar retrocesos del reloj
    private fun handleClockBackward(offset: Long): Long {
        println("WARNING: Clock moved backwards by $offset milliseconds. Waiting to recover...")
        Thread.sleep(offset)
        return timestamp()
    }

    // Esperar hasta el siguiente milisegundo
    private fun waitNextMillis(currentTimestamp: Long): Long {
        var newTimestamp = timestamp()
        while (newTimestamp == currentTimestamp) {
            newTimestamp = timestamp()
        }
        return newTimestamp
    }

    companion object {
        private const val TOTAL_BITS = 63
        private const val EPOCH_BITS = 41
        private const val NODE_ID_BITS = 10
        private const val SEQUENCE_BITS = 12

        private val MAX_NODE_ID = (2.0.pow(NODE_ID_BITS.toDouble()) - 1).toInt()
        private val MAX_SEQUENCE = (2.0.pow(SEQUENCE_BITS.toDouble()) - 1).toInt()

        private const val CUSTOM_EPOCH = 1420070400000L // 1 de enero de 2015

        // Obtener el timestamp actual en milisegundos desde la epoch personalizada
        private fun timestamp(): Long {
            return Instant.now().toEpochMilli() - CUSTOM_EPOCH
        }
    }
}

