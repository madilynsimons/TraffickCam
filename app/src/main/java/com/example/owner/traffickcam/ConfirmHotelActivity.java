package com.example.owner.traffickcam;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;


public class ConfirmHotelActivity extends AppCompatActivity {

    final static int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_hotel);
        PickPlace();
    }

    protected void PickPlace()
    {

        Intent intent;
        AutocompleteFilter filter;

        try
        {
            filter = new AutocompleteFilter.Builder()
                    .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                    .build();
            intent =  new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                        .setFilter(filter)
                        .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE  );
        }
        catch(GooglePlayServicesRepairableException e)
        {
        // TODO
        }
        catch(GooglePlayServicesNotAvailableException e)
        {
            // TODO
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                // TODO
            }
        }
    }

}
