package com.jibestream.worldmap.map;

import com.jibestream.worldmap.model.LocationInfo;

public interface IMapPresenter {

    void searchLocation(String location);

    void onLocationFound(LocationInfo locationInfo);

    void onLocationNotFound();

    void onDeviceLocationFound(LocationInfo locationInfo);

    void onDeviceLocationNotFound();

    void getCurrentLocation();
}
