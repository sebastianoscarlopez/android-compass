package ar.com.sebas.compass;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by sebas on 2/11/2017.
 *
 * Simplify getting the current location
 *
 */

class LocationHelper implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private final GoogleApiClient mGoogleApiClient;
    private Location mLastLocation = null;

    LocationHelper(Context context)
    {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    /**
     * Crea una solicitud de localización
     */
    private void createLocationRequest(){
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(1000);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);
    }

    Location getLastLocation() {
        return mLastLocation;
    }

    /**
     * Cuando se conecta GoogleApiClient
     * @param bundle Desconozco el uso
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        createLocationRequest();
        Log.d("MyLog", "onConnected");
    }

    /**
     * Cuando cambia de ubicación, se guarda para ser consultada
     * @param location Nueva ubicación
     */
    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        Log.d("MyLog", "onLocationChanged");
    }

    /**
     * Cuando se suspende GoogleApiClient
     * @param i Desconozco el uso
     */
    @Override
    public void onConnectionSuspended(int i) {
        Log.d("MyLog", "onConnectionSuspended");
    }

    /**
     * Cuando falla la conexión
     * @param connectionResult Motivo de la falla
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("MyLog", "onConnectionFailed");
    }
}
