package ca.arnaud.horasolis.domain

sealed interface Response<out Data : Any?, Error> {

    data class Success<Data, Error>(
        val data: Data,
    ) : Response<Data, Error>

    data class Failure<Error>(
        val error: Error,
    ) : Response<Nothing, Error> {
        /**
         * Maps a failures error type
         */
        inline fun <ErrorOut> map(transform: (Error) -> ErrorOut): Failure<ErrorOut> {
            return Failure(transform(error))
        }
    }

    fun getDataOrNull(): Data? {
        return when (this) {
            is Success -> data
            is Failure -> null
        }
    }
}

/**
 * Maps the data type to a new value while leaving the error intact
 */
inline fun <FromData, ToData, Error> Response<FromData, Error>.map(
    transform: (FromData) -> ToData,
): Response<ToData, Error> {
    return when (this) {
        is Response.Success -> Response.Success(transform(data))
        is Response.Failure -> this
    }
}
