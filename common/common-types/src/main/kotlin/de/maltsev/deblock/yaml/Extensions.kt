package de.maltsev.deblock.yaml

import com.charleskorn.kaml.YamlList
import com.charleskorn.kaml.YamlMap
import com.charleskorn.kaml.YamlNode
import com.charleskorn.kaml.YamlNull
import com.charleskorn.kaml.YamlScalar
import kotlin.time.Duration

inline fun <reified T : YamlNode, R> YamlMap.get(
    field: String,
    extract: T.() -> R,
): R = requireNotNull(get<T>(field)) {
    "Required field `$field` is missing (line= ${location.line} column=${location.column})"
}.extract()

inline fun <reified T : YamlNode, R> YamlMap.find(
    field: String,
    extract: T.() -> R,
): R? = when (val value = get<T>(field)) {
    null, is YamlNull -> null
    else -> value.extract()
}

fun YamlMap.obj(key: String): YamlMap = get(key, asYamlMap)

fun YamlMap.list(key: String): YamlList = get(key, asYamlList)

fun YamlMap.int(key: String): Int = get(key, asInt)

fun YamlMap.string(key: String): String = get(key, asString)

fun YamlMap.duration(key: String): Duration = Duration.parse(string(key))

fun YamlMap.findString(key: String): String? = find(key, asString)

private val asYamlMap: YamlNode.() -> YamlMap = { this as YamlMap }
private val asYamlList: YamlNode.() -> YamlList = { this as YamlList }
private val asInt: YamlNode.() -> Int = { (this as YamlScalar).toInt() }
private val asString: YamlNode.() -> String = { (this as YamlScalar).content }
