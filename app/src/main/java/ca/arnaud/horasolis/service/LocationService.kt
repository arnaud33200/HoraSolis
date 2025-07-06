package ca.arnaud.horasolis.service

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import ca.arnaud.horasolis.domain.Response
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

data class LocationData(
    val latitude: Double,
    val longitude: Double,
)

sealed class CurrentLocationError(reason: Throwable?) : Throwable(reason) {

    data object MissingPermission : CurrentLocationError(null)
    data class LocationUnavailable(val reason: Throwable?) : CurrentLocationError(reason)
}

class LocationService(
    private val context: Context,
) {

    suspend fun getCurrentLocation(): Response<LocationData, CurrentLocationError> {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return Response.Failure(CurrentLocationError.MissingPermission)
        }

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        return suspendCancellableCoroutine { continuation ->
            val cancellationTokenSource = CancellationTokenSource()
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.token
            ).addOnSuccessListener { location ->
                val locationData = LocationData(
                    latitude = location.latitude,
                    longitude = location.longitude,
                )
                continuation.resume(Response.Success(locationData))
            }.addOnFailureListener { exception ->
                continuation.resume(
                    Response.Failure(
                        CurrentLocationError.LocationUnavailable(exception)
                    )
                )
            }
            continuation.invokeOnCancellation {
                cancellationTokenSource.cancel()
            }
        }
    }
}