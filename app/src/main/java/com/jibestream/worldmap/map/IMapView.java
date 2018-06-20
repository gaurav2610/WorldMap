package com.jibestream.worldmap.map;

public interface IMapView {

    void removeMarkers();

    void showMarker(double latitude, double longitude, String title);

    void showNoLocationFound();

    void showEmptyStringError();

    void showCurrentLocation(double latitude, double longitude);

    void showDeviceLocationNotFound();

    void showNoNetworkAvailable();
}
