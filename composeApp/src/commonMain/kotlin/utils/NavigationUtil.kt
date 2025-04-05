package utils

import androidx.core.bundle.Bundle
import androidx.navigation.NavType
import kotlinx.serialization.json.Json

/**
 * A utility function to create a [NavType] for serializable types.
 * link: https://medium.com/mercadona-tech/type-safety-in-navigation-compose-23c03e3d74a5
 */
inline fun <reified T : Any> serializableType(
    isNullableAllowed: Boolean = false,
    json: Json = Json,
) = object : NavType<T>(isNullableAllowed = isNullableAllowed) {
    override fun get(bundle: Bundle, key: String) =
        bundle.getString(key)?.let<String, T>(json::decodeFromString)

    override fun parseValue(value: String): T = json.decodeFromString(value)

    override fun serializeAsValue(value: T): String = json.encodeToString(value)

    override fun put(bundle: Bundle, key: String, value: T) {
        bundle.putString(key, json.encodeToString(value))
    }
}