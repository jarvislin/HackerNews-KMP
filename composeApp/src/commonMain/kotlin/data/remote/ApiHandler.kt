package data.remote

import domain.models.NetworkError
import domain.models.ParseError
import domain.models.UnknownError
import io.github.aakira.napier.Napier
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

/**
 * Handler to run API requests.
 */
object ApiHandler {
    /**
     * Run a suspend block and return the response body as a string.
     */
    suspend fun run(
        block: suspend () -> HttpResponse
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val result = block()
            if (result.status.value in 200..299) {
                Result.success(result.bodyAsText())
            } else {
                Result.failure(UnknownError)
            }
        } catch (exception: Exception) {
            Napier.e(exception.message ?: "Failed to fetch data", exception)
            Result.failure(NetworkError)
        }
    }

    /**
     * Run a suspend block and parse the response body to a type.
     */
    suspend fun <T> runAndParse(
        json: Json,
        type: KSerializer<T>,
        block: suspend () -> HttpResponse
    ): Result<T> {
        val result = run(block)
        return if (result.isSuccess) {
            try {
                Result.success(json.decodeFromString(type, result.getOrThrow()))
            } catch (exception: Exception) {
                Napier.e(exception.message ?: "Failed to parse data", exception)
                Result.failure(ParseError)
            }
        } else {
            Result.failure(result.exceptionOrNull() ?: UnknownError)
        }
    }
}
