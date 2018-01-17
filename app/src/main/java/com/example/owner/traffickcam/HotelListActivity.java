package com.example.owner.traffickcam;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * More info, see:
 * https://www.androidtutorialpoint.com/intermediate/google-maps-search-nearby-displaying-nearby-places-using-google-places-api-google-maps-api-v2/
 */

public class HotelListActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {

    ListView mList; // ListView used to show nearby hotels
    List<String> nearbyHotels; // list of nearby hotels
    List<String> hotelIDs; // the IDs of the nearby hotels
    private ArrayAdapter<String> mAdapter; // adapter used to assign nearbyHotels to mList
    int PROXIMITY_RADIUS = 10000; // max distance a hotel can be from the user
    double latitude,longitude; // latitude and longitude of the user
    String url; // url to JSON file with info on all of the nearbyhotels
    Context context; // this activity -- used to call HotelInfoActivity
    private GoogleApiClient client; // google API client
    EditText mEditText; // edit text used to get keywords from the user
    List<HashMap<String, String>> googleHotels; // nearby hotels and all their info

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_list);

        checkLocationPermission();

        initmList();

        mEditText = (EditText) findViewById(R.id.locationText);
        nearbyHotels = new ArrayList<String>();
        hotelIDs = new ArrayList<String>();
        context = this;

        buildGoogleApiClient();
    }

    private void initmList()
    {
        mList = (ListView) findViewById(R.id.hotel_list);
        mList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Object listItem = mList.getItemAtPosition(position);
                Intent intent = new Intent(context, HotelInfoActivity.class);
                intent.putExtra("HOTEL", (String)listItem);
                startActivity(intent);
            }
        });
    }

    public void searchOnClick(View view)
    {
        String keywords = mEditText.getText().toString().replace(" ", "%20");
        searchNearbyHotels(keywords);
    }

    private void searchNearbyHotels(String keywords)
    {
        HotelData getNearbyPlacesData = new HotelData();

        url = getUrl(keywords, "lodging");

        getNearbyPlacesData.execute();
    }

    private String getUrl(String keywords, String nearbyPlace)
    {

        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlaceUrl.append("location="+latitude+","+longitude);
        googlePlaceUrl.append("&radius="+PROXIMITY_RADIUS);
        googlePlaceUrl.append("&type="+nearbyPlace);
        googlePlaceUrl.append("&sensor=true");
        googlePlaceUrl.append("&keyword=" + keywords);
        googlePlaceUrl.append("&key="+"AIzaSyDlTwcKRDanIkhKboghxUS22O79AlF0kfM");

        Log.d("MapsActivity", "url = "+googlePlaceUrl.toString());

        return googlePlaceUrl.toString();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationRequest locationRequest;
        locationRequest = new LocationRequest();
        locationRequest.setInterval(100);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(client, locationRequest, this);
        }
    }

    public boolean checkLocationPermission()
    {
        int REQUEST_LOCATION_CODE = 99;
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)  != PackageManager.PERMISSION_GRANTED )
        {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION))
            {
                ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION },REQUEST_LOCATION_CODE);
            }
            else
            {
                ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION },REQUEST_LOCATION_CODE);
            }
            return false;

        }
        else
            return true;
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }


    protected synchronized void buildGoogleApiClient() {
        client = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        client.connect();

    }

    public class HotelData extends AsyncTask<Object, String, String> {

        private String googlePlacesData;
        public List<HashMap<String, String>> nearbyPlaceList;

        @Override
        protected String doInBackground(Object... objects)
        {
            DownloadURL downloadURL = new DownloadURL();
            try
            {
                googlePlacesData = downloadURL.readUrl(url);
            }catch(IOException e)
            {
                // TODO
            }
            return googlePlacesData;
        }

        @Override
        protected void onPostExecute(String s){
            DataParser parser = new DataParser();
            nearbyPlaceList = parser.parse(s);
            showNearbyPlaces(nearbyPlaceList);
        }

        private void showNearbyPlaces(List<HashMap<String, String>> nearbyPlaceList)
        {
            googleHotels = nearbyPlaceList;
            nearbyHotels.clear();
            hotelIDs.clear();
            for(int i = 0; i < nearbyPlaceList.size(); i++)
            {
                HashMap<String, String> googlePlace = nearbyPlaceList.get(i);

                String placeName = googlePlace.get("place_name");
                String placeID = googlePlace.get("place_id");
                nearbyHotels.add(placeName);
                hotelIDs.add(placeID);
            }
            mAdapter = new ArrayAdapter<String>
                    (context, android.R.layout.simple_list_item_1, nearbyHotels);
            mList.setAdapter(mAdapter);
        }
    }
}
