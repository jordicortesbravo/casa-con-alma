package com.jcortes.deco.util.paging

import kotlin.math.max

enum class Order { ASC, DESC }

data class Sort(
    val field: String,
    val order: Order = Order.ASC
)

data class Pageable(
    val pageNumber: Int,
    val pageSize: Int,
    val sort: List<Sort>
) {
    constructor(pageNumber: Int, pageSize: Int, vararg sort: Sort) : this(pageNumber, pageSize, listOf(*sort))

    init {
        require(pageNumber >= 0) { "Page number must be greater or equal than 0" }
        require(pageSize > 0) { "Page size must be greater than 0" }

        require(pageNumber < MAX_PAGE_NUMBER) { "Page number must be less than $MAX_PAGE_NUMBER" }
        require(pageSize < MAX_PAGE_SIZE) { "Page size must be less than $MAX_PAGE_SIZE" }
    }

    fun next(): Pageable = Pageable(pageNumber + 1, pageSize, sort)
    fun previousOrFirst(): Pageable = Pageable(max(0, pageNumber - 1), pageSize, sort)
    fun sort(field: String, order: Order = Order.ASC): Pageable = copy(sort = sort + Sort(field, order))

    companion object {
        const val MAX_PAGE_SIZE = 10_000
        const val MAX_PAGE_NUMBER = 1_000_000
        const val DEFAULT_PAGE_SIZE = 26

        fun default() = Pageable(0, DEFAULT_PAGE_SIZE)
    }
}