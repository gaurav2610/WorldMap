package com.jibestream.worldmap.map;

import android.content.Context;
import android.net.NetworkInfo;

import com.jibestream.worldmap.model.LocationInfo;
import com.jibestream.worldmap.network.NetworkConnection;

public class MapPresenter implements IMapPresenter{

    private IMapView mapView;
    private MapInteractor mapInteractor;
    private Context context;

    MapPresenter(IMapView mapView, Context context){
        this.mapView = mapView;
        this.context = context;

        mapInteractor = new MapInteractor(this);
    }

    /**
     * Passes the location string to MapInteractor.
     * Also checks for empty strign and network availability
     * @param location
     */
    @Override
    public void searchLocation(String location) {
        if(location.length()>0) {
            if(new NetworkConnection().isNetworkAvailable(context))
                mapInteractor.searchLocation(location, context);
            else
                mapView.showNoNetworkAvailable();
        }
        else
            mapView.showEmptyStringError();
    }

    /**
     * Calls Map Interactor's getCurrentLocation() method
     */
    @Override
    public void getCurrentLocation() {
        mapInteractor.getDeviceLocation(context);
    }

    /**
     * Calls view's removeMarkers() and showMarker() methods
     * @param locationInfo
     */
    @Override
    public void onLocationFound(LocationInfo locationInfo) {
        mapView.removeMarkers();
        mapView.showMarker(locationInfo.getLatitude(), locationInfo.getLongitude(), locationInfo.getTitle());
    }

    /**
     * Calls view's showNoLocationFound() method
     */
    @Override
    public void onLocationNotFound() {
        mapView.showNoLocationFound();
    }

    /**
     * Calls view's showCurrentLocation() method
     */
    @Override
    public void onDeviceLocationFound(LocationInfo locationInfo) {
        mapView.showCurrentLocation(locationInfo.getLatitude(), locationInfo.getLongitude());
    }

    /**
     * Calls view's showDeviceLocationNotFound() method
     */
    @Override
    public void onDeviceLocationNotFound() {
        mapView.showDeviceLocationNotFound();
    }
}
