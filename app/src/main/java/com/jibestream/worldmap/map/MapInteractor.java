package com.jibestream.worldmap.map;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.jibestream.worldmap.model.LocationInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapInteractor {

    private IMapPresenter mapPresenter;

    private String TAG = "MapInteractor";

    MapInteractor(IMapPresenter mapPresenter) {
        this.mapPresenter = mapPresenter;
    }

    /**
     * Searches the location string using Geocoder getFromLocationName() method.
     * @param location
     * @param context
     */
    void searchLocation(String location, Context context) {
        Geocoder geocoder = new Geocoder(context);

        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(location, 1);
        } catch (IOException e) {
            Log.e(TAG, "searchLocation: IOException: " + e.getMessage());
        }

        if (list.size() > 0) {
            Address address = list.get(0);
            LocationInfo locationInfo = new LocationInfo(address.getLatitude(), address.getLongitude(),
                    address.getAddressLine(0));
            mapPresenter.onLocationFound(locationInfo);
        } else {
            mapPresenter.onLocationNotFound();
        }
    }

    /**
     * Get device's current location
     * @param context
     */
    void getDeviceLocation(Context context) {
        FusedLocationProviderClient mFusedLocationProviderClient
                = LocationServices.getFusedLocationProviderClient(context);

        try {
            final Task location = mFusedLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Location currentLocation = (Location) task.getResult();
                        LocationInfo locationInfo = new LocationInfo(currentLocation.getLatitude(),
                                currentLocation.getLongitude(), "");
                        mapPresenter.onDeviceLocationFound(locationInfo);
                    } else {
                        mapPresenter.onDeviceLocationNotFound();
                    }
                }
            });
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }
}
