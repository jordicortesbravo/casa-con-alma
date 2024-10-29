package com.idealista.yaencontre.io.storage

class StorageObjectKey<T> private constructor(val name: String, val value: T) {

    init {
        validateObjectName(name)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is StorageObjectKey<*>) return false

        if (name != other.name) return false
        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + (value?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return value.toString()
    }

    companion object {

        private val VALID_OBJECT_NAME_REGEX = Regex("""^([a-zA-Z0-9\-./()\s]_?)+$""")

        fun <T> of(name: String, value: T): StorageObjectKey<T> {
            return StorageObjectKey(name, value)
        }

        fun validateObjectName(objectName: String) {
            if(!objectName.matches(VALID_OBJECT_NAME_REGEX)) {
                throw IllegalArgumentException("Invalid name: '$objectName'")
            }
        }
    }
}