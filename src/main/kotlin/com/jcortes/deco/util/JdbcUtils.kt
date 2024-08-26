package com.jcortes.deco.util

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource

object JdbcUtils {
    fun paramsOf(vararg pairs: Pair<String, Any?>) = MapSqlParameterSource(pairs.toMap())
}