package com.jcortes.deco.util.jdbc

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource

object JdbcUtils {
    fun paramsOf(vararg pairs: Pair<String, Any?>) = MapSqlParameterSource(pairs.toMap())
}