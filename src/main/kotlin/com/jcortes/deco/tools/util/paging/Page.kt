package com.jcortes.deco.tools.util.paging

class Page<E>(
    val pageNumber: Int,
    val pageSize: Int,
    val totalElements: Int,
    val items: List<E>
) {

    init {
        require(pageNumber >= 0) { "pageNumber should be positive number" }
        require(pageSize > 0) { "pageSize should be greater than 0" }
        require(pageSize <= MAX_PAGE_SIZE) { "pageNumber should less or equal than $MAX_PAGE_SIZE" }
        require(totalElements >= 0) { "totalElements should be positive number" }

        require(items.size <= pageSize ) { "Content length should be lower or equal than pageSize" }
        require((pageNumber * pageSize) + items.size <= totalElements) { "Invalid arguments. Items size plus offset should be lower or equal than totalElements" }
    }

    val numberOfElements get() = items.size

    fun isEmpty() = items.isEmpty()
    fun isNotEmpty() = items.isNotEmpty()

    fun hasNextPage() = items.size == pageSize && (( pageNumber + 1 ) * pageSize) < totalElements
    fun <R> map(transform: (E) -> R): Page<R> = Page(pageNumber, pageSize, totalElements, items.map { transform(it) })

    companion object {
        const val MAX_PAGE_SIZE = 10_000

        fun <E> of(pageable: Pageable, totalElements: Int, items: List<E>): Page<E> {
            return Page(pageable.pageNumber, pageable.pageSize, totalElements, items)
        }
    }
}
