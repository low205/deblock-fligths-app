package de.maltsev.deblock.json

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime

private fun fieldIsMissing(field: String): String = "Required field `$field` is missing"

fun <T : Any?> JsonObject.get(field: String, extract: JsonElement.() -> T): T = requireNotNull(get(field)) {
    fieldIsMissing(field)
}.extract()

fun <T : Any?> JsonObject.find(field: String, extract: JsonElement.() -> T): T? = when (val value = get(field)) {
    null, is JsonNull -> null
    else -> value.extract()
}

fun JsonObject.string(field: String): String = get(field, asString)
fun JsonObject.findString(field: String): String? = find(field, asString)
fun JsonObject.int(field: String): Int = get(field, asInt)
fun JsonObject.uInt(field: String): UInt = get(field, asInt).toUInt()
fun JsonObject.objectArray(field: String): List<JsonObject> = get(field, asJsonObjectArray)
fun JsonObject.localDate(field: String): LocalDate = get(field, asString).let(LocalDate::parse)
fun JsonObject.localDateTime(field: String): LocalDateTime = get(field, asString).let(LocalDateTime::parse)
fun JsonObject.instant(field: String): Instant = get(field, asString).let(Instant::parse)

private val asString: JsonElement.() -> String = {
    jsonPrimitive.also {
        require(it.isString) {
            "Json element must be string"
        }
    }.content
}

private val asInt: JsonElement.() -> Int = { jsonPrimitive.int }

private val asJsonObjectArray: JsonElement.() -> List<JsonObject> = {
    jsonArray.map { it.jsonObject }
}
