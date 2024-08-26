package com.jcortes.deco.util

import java.net.NetworkInterface
import java.security.SecureRandom
import java.time.Instant
import java.util.*
import kotlin.math.pow


interface IdGenerator {
    fun nextId(): Long
}


/**
 * @see "https://github.com/callicoder/java-snowflake"
 */
class SnowflakeIdGenerator(private val nodeId: Int) : IdGenerator {

    @Volatile
    private var lastTimestamp = -1L

    @Volatile
    private var sequence = 0L

    // Let SequenceGenerator generate a nodeId
    constructor() : this(createNodeId())

    // Create SequenceGenerator with a nodeId
    init {
        require(!(nodeId < 0 || nodeId > MAX_NODE_ID)) { "NodeId must be between 0 and $MAX_NODE_ID" }
    }

    @Synchronized
    override fun nextId(): Long {
        var currentTimestamp = timestamp()
        check(currentTimestamp >= lastTimestamp) { "Invalid System Clock!" }
        if (currentTimestamp == lastTimestamp) {
            sequence = sequence + 1 and MAX_SEQUENCE.toLong()
            if (sequence == 0L) {
                // Sequence Exhausted, wait till next millisecond.
                currentTimestamp = waitNextMillis(currentTimestamp)
            }
        } else {
            // reset sequence to start with zero for the next millisecond
            sequence = 0
        }
        lastTimestamp = currentTimestamp
        var id = currentTimestamp shl TOTAL_BITS - EPOCH_BITS
        id = id or (nodeId shl TOTAL_BITS - EPOCH_BITS - NODE_ID_BITS).toLong()
        id = id or sequence
        return id
    }

    fun parse(id: Long): LongArray {
        val maskNodeId = (1L shl NODE_ID_BITS) - 1 shl SEQUENCE_BITS
        val maskSequence = (1L shl SEQUENCE_BITS) - 1
        val timestamp: Long = (id shr NODE_ID_BITS + SEQUENCE_BITS) + CUSTOM_EPOCH
        val nodeId = id and maskNodeId shr SEQUENCE_BITS
        val sequence = id and maskSequence
        return longArrayOf(timestamp, nodeId, sequence)
    }

    // Block and wait till next millisecond
    private fun waitNextMillis(currentTimestamp: Long): Long {
        var currentTimestamp = currentTimestamp
        while (currentTimestamp == lastTimestamp) {
            currentTimestamp = timestamp()
        }
        return currentTimestamp
    }

    companion object {
        private const val TOTAL_BITS = 53
        private const val EPOCH_BITS = 42
        private const val NODE_ID_BITS = 5
        private const val SEQUENCE_BITS = 6
        private val MAX_NODE_ID = (2.0.pow(NODE_ID_BITS.toDouble()) - 1).toInt()
        private val MAX_SEQUENCE = (2.0.pow(SEQUENCE_BITS.toDouble()) - 1).toInt()

        // Custom Epoch (January 1, 2015, Midnight UTC = 2015-01-01T00:00:00Z)
        private const val CUSTOM_EPOCH = 1420070400000L

        // Get current timestamp in milliseconds, adjust for the custom epoch.
        private fun timestamp(): Long {
            return Instant.now().toEpochMilli() - CUSTOM_EPOCH
        }

        private fun createNodeId(): Int {
            val newNodeId: Int = try {
                val sb = StringBuilder()
                val networkInterfaces: Enumeration<NetworkInterface> = NetworkInterface.getNetworkInterfaces()
                while (networkInterfaces.hasMoreElements()) {
                    val networkInterface: NetworkInterface = networkInterfaces.nextElement()
                    networkInterface.hardwareAddress?.forEach { byte -> sb.append(String.format("%02X", byte)) }
                }
                sb.toString().hashCode()
            } catch (ex: Exception) {
                SecureRandom().nextInt()
            }
            return newNodeId and MAX_NODE_ID
        }
    }
}
