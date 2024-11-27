package com.jcortes.deco.tools.util.jdbc

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class IdRepository(
    private val jdbcTemplate: JdbcTemplate
) {

    fun nextId(): Long {
        val sql = "SELECT nextval('deco.id_sequence')"
        return jdbcTemplate.queryForObject(sql, Long::class.java) ?: throw IllegalStateException("Sequence value not retrieved")
    }
}