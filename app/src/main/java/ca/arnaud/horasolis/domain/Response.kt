package ca.arnaud.horasolis.domain

import ca.arnaud.horasolis.domain.Response.Success

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

inline fun <Data, Error> Response<Data, Error>.onSuccess(
    action: (Data) -> Unit,
): Response<Data, Error> {
    if (this is Success) {
        action(data)
    }
    return this
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
