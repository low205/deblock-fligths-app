package de.maltsev.deblock.json

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

private fun fieldIsMissing(field: String): String = "Required field $field is missing"

fun <T : Any?> JsonObject.get(field: String, extract: JsonElement.() -> T): T = requireNotNull(get(field)) {
    fieldIsMissing(field)
}.extract()

fun <T : Any?> JsonObject.find(field: String, extract: JsonElement.() -> T): T? = when (val value = get(field)) {
    null, is JsonNull -> null
    else -> value.extract()
}

val asString: JsonElement.() -> String = {
    jsonPrimitive.also {
        require(it.isString) {
            "Json element must be string"
        }
    }.content
}

val asJsonObjectArray: JsonElement.() -> List<JsonObject> = {
    jsonArray.map { it.jsonObject }
}
