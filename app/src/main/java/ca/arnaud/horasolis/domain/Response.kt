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

inline fun <Data, Error> Response<Data, Error>.onSuccess(
    action: (Data) -> Unit,
): Response<Data, Error> {
    if (this is Response.Success) {
        action(data)
    }
    return this
}

inline fun <Data, Error> Response<Data, Error>.onFailure(
    action: (Error) -> Unit,
): Response<Data, Error> {
    if (this is Response.Failure) {
        action(error)
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

inline fun <FromData, ToData, FromError, ToError> Response<FromData, FromError>.map(
    transform: (FromData) -> ToData,
    transformError: (FromError) -> ToError,
): Response<ToData, ToError> {
    return when (this) {
        is Response.Success -> Response.Success(transform(data))
        is Response.Failure -> Response.Failure(transformError(error))
    }
}

inline fun <FromData, ToData, Error> Response<FromData, Error>.flatMap(
    transform: (FromData) -> ToData,
    transformError: (Error) -> ToData,
): ToData {
    return when (this) {
        is Response.Success -> transform(data)
        is Response.Failure -> transformError(error)
    }
}

inline fun <Data, FromError, ToError> Response<Data, FromError>.mapError(
    transform: (FromError) -> ToError,
): Response<Data, ToError> {
    return when (this) {
        is Response.Success -> Response.Success(data)
        is Response.Failure -> Response.Failure(transform(error))
    }
}