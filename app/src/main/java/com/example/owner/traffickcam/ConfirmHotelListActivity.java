package com.example.owner.traffickcam;

import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class ConfirmHotelListActivity extends AppCompatActivity
implements com.google.android.gms.location.LocationListener{

    JSONArray hotels_json;
    ArrayList<String> nearby_hotels;
    ArrayAdapter adapter;
    ListView listView;

    int PROXIMITY_RADIUS = 10000;
    double latitude = 39.952584,longitude = -75.165222;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_hotel_list);

        listView = (ListView) findViewById(R.id.listView);

        getHotels();
    }

    void getHotels()
    {
        String url = getUrl(latitude, longitude, "lodging");
        try{
            hotels_json = new JSONArray("/test.json");
        }
        catch(Exception e){
            hotels_json = new JSONArray();
            Toast.makeText(this, "FAILED TO CREATE LIST", Toast.LENGTH_LONG).show();
        }

        nearby_hotels = new ArrayList<String>();

        for(int x = 0; x < hotels_json.length(); x++)
        {
            try{
                String hotel = hotels_json.getJSONObject(x).getString("vicinity");
                nearby_hotels.add(hotel);
            }catch(Exception e){}

        }

        adapter = new ArrayAdapter(this, R.layout.activity_confirm_hotel_list, nearby_hotels);
        listView.setAdapter(adapter);

    }

    private String getUrl(double latitude , double longitude , String nearbyPlace)
    {

        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlaceUrl.append("location="+latitude+","+longitude);
        googlePlaceUrl.append("&radius="+PROXIMITY_RADIUS);
        googlePlaceUrl.append("&type="+nearbyPlace);
        googlePlaceUrl.append("&sensor=true");
        googlePlaceUrl.append("&key="+"AIzaSyBLEPBRfw7sMb73Mr88L91Jqh3tuE4mKsE");

        return googlePlaceUrl.toString();
    }

    @Override
    public void onLocationChanged(Location location) {
        longitude = location.getLongitude();
        latitude = location.getLatitude();
    }
}
