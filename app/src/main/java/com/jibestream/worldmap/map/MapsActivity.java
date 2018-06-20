package com.jibestream.worldmap.map;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jibestream.worldmap.R;


public class MapsActivity extends FragmentActivity implements IMapView, OnMapReadyCallback {

    private GoogleMap mMap;
    private Boolean mLocationPermissionsGranted = false;
    private MapPresenter mapPresenter;

    private static final String TAG = "MapActivity";

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static float DEFAULT_ZOOM = 10f;

    private EditText edit_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mapPresenter = new MapPresenter(this, this);
        getLocationPermission();
    }

    /**
     * Request Location Permissions from the user if they are not already granted.
     */
    private void getLocationPermission() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    /**
     * Initialize Map
     */
    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Initialize search EditTextView and perform search Action
     */
    private void init() {
        edit_search = findViewById(R.id.edit_search);
        edit_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                edit_search.setError(null);
                hideSoftKeyboard(MapsActivity.this);
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    mapPresenter.searchLocation(edit_search.getText().toString());
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (mLocationPermissionsGranted) {
            mapPresenter.getCurrentLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);

            init();
        }
    }

    /**
     * Removes all previous markers from the map
     */
    @Override
    public void removeMarkers() {
        mMap.clear();
    }

    /**
     * Show marker on the map for the given location and moves the camera to that location
     * @param latitude
     * @param longitude
     * @param title
     */
    @Override
    public void showMarker(double latitude, double longitude, String title) {
        MarkerOptions options = new MarkerOptions()
                .position(new LatLng(latitude, longitude)).title(title);
        mMap.addMarker(options);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), DEFAULT_ZOOM));
    }

    /**
     * Shows a toast message when location is not found
     */
    @Override
    public void showNoLocationFound() {
        Toast.makeText(this, R.string.no_location_found_error, Toast.LENGTH_SHORT).show();
    }

    /**
     * Shows a error message when search string is empty
     */
    @Override
    public void showEmptyStringError() {
        edit_search.setError(getString(R.string.empty_string_found_error));
    }

    /**
     * Moves the map to the current location
     */
    @Override
    public void showCurrentLocation(double latitude, double longitude) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), DEFAULT_ZOOM));
    }


    /**
     * Shows a toast message when device's current location is not found
     */
    @Override
    public void showDeviceLocationNotFound() {
        Toast.makeText(this, R.string.device_location_error, Toast.LENGTH_SHORT).show();
    }


    /**
     * Shows a toast message when network is not avilable
     */
    @Override
    public void showNoNetworkAvailable() {
        Toast.makeText(this, R.string.no_network_error, Toast.LENGTH_SHORT).show();
    }

    /**
     * Hides keyboard
     * @param activity
     */
    private void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    /**
     * callback to check for permissions result
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionsGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true;
                    //initialize our map
                    initMap();
                }
            }
        }
    }
}
