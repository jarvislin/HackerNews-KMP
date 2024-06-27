package domain.models

sealed class AppError(override val message: String) : Throwable(message)
data object NetworkError : AppError("No network connection.")
data object UnknownError : AppError("Something went wrong.")
data object ParseError : AppError("Failed to parse data.")