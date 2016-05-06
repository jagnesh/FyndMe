package fyndme.pioneersmind.com.fyndme;

import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;

public class CurrentLocation extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, GoogleMap.OnMapClickListener {
    private GoogleMap mMap;
    boolean MAP_READY,FIRST_TIME = false;
    double lat,lng;


    SupportMapFragment mapFragment;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    Location mLastLocation;
    ArrayList<LatLng> locatoinLatLngs=new ArrayList<LatLng>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_current_location, container, false);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.locationFragment);
        mapFragment.getMapAsync(this);
        buildGoogleApiClient();

        return view;
    }

    synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }


    @Override
    public void onMapReady(GoogleMap mMap) {
        this.mMap = mMap;
        MAP_READY = true;
        mMap.setOnMapClickListener(this);
        Toast.makeText(getActivity(),"Map Ready",Toast.LENGTH_LONG).show();
        updateLocationMarker(30.90154347, 75.85173368);
    }

    public void updateLocationMarker(double lat, double lng) {
        this.lat=lat;
        this.lng=lng;

        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.mipmap.location_pin);
        LatLng city = new LatLng(lat, lng);
        locatoinLatLngs.add(city);
        mMap.addMarker(new MarkerOptions().position(city).title(getAddressFromLatLng(lat, lng)).icon(icon));
        if(FIRST_TIME)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(city, mMap.getCameraPosition().zoom));
        else
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(city,15));
        FIRST_TIME=true;

    }
    private String getAddressFromLatLng(double lat, double lng) {
        Geocoder geocoder = new Geocoder( getActivity() );

        String address = "";
        try {
            address = geocoder
                    .getFromLocation( lat, lng, 1 )
                    .get( 0 ).getAddressLine( 0 );
        } catch (IOException e ) {
        }

        return address;
    }

    @Override
    public void onLocationChanged(Location location) {
        String msg = "New Latitude: " + location.getLatitude()
                + "New Longitude: " + location.getLongitude();
        if (MAP_READY) {
            if((location.getLatitude()!=lat)||(location.getLongitude()!=lng))
            {
                updateLocationMarker(location.getLatitude(), location.getLongitude());
                Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
            }

        }

    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(100); // Update location every second
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {

            updateLocationMarker(mLastLocation.getLatitude(), mLastLocation.getLongitude());

        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onMapClick(LatLng latLng) {
        updateLocationMarker(latLng.latitude,latLng.longitude);
        Toast.makeText(getActivity(),"Lat:"+latLng.latitude+"Lng:"+latLng.longitude,Toast.LENGTH_LONG).show();
    }
}
