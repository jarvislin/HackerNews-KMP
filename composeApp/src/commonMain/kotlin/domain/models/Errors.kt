package domain.models

object NetworkError : Throwable("No network connection.")
object UnknownError : Throwable("Something went wrong.")
object ParseError : Throwable("Failed to parse data.")