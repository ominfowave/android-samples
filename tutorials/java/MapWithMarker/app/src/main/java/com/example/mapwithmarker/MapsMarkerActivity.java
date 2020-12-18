// Copyright 2020 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.example.mapwithmarker;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;


import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.regex.MatchResult;

/**
 * An activity that displays a Google map with a marker (pin) to indicate a particular location.
 */
// [START maps_marker_on_map_ready]
public class MapsMarkerActivity extends AppCompatActivity
        implements OnMapReadyCallback {

    private GoogleMap map;
    private ArrayList<Marker> markerArrayList = new ArrayList<>();
    private ArrayList<MarkerDetailBeen> beenArrayList = new ArrayList<>();
    private Marker clickedMarker;
    private int isClick = 0;
    private LatLngBounds.Builder builder;
    private int size = 0;
    private ArrayList<MarkerDetailBeen> distArrayList = new ArrayList<>();
    private int lastMarkerPosition = -1;

    // [START_EXCLUDE]
    // [START maps_marker_get_map_async]
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_maps);

        // Get the SupportMapFragment and request notification when the map is ready to be used.
     SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setTiltGesturesEnabled(false);
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
/*
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            Log.e("!_!_!", "onMapReady no permission");
            return;
        }
*/
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.getUiSettings().setCompassEnabled(false);
        map.getUiSettings().setMapToolbarEnabled(false);
        createStaticMarkerDet();
        initMainView();
        LatLngBounds.Builder b = new LatLngBounds.Builder();
        for (Marker m : markerArrayList) {
            b.include(m.getPosition());
        }
        /**
         * map click, onMapClick event trigger on click outside of marker and inside of map area
         * here is code for remove big marker and set marker as normal state and hide bottom viewpager
         */
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                managePreviousMarkers();
            }
        });

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                try {
                    isClick = 1;
                    if (clickedMarker == null)
                        showMarkerDetailView(marker);
                    else if (!clickedMarker.getTag().toString().equalsIgnoreCase(marker.getTag().toString()))
                        showMarkerDetailView(marker);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        });

    }

    private void createStaticMarkerDet() {
        beenArrayList.add(new MarkerDetailBeen(40.578484648921133, 22.942840236659581, 1.299));
        beenArrayList.add(new MarkerDetailBeen(40.570251710765362, 22.973028162643004, 1.315));
        beenArrayList.add(new MarkerDetailBeen(40.5758864, 22.9692429, 1.377));
        beenArrayList.add(new MarkerDetailBeen(40.581768403514658, 22.95709663760249, 1.378));
        beenArrayList.add(new MarkerDetailBeen(40.589225294156009, 22.944906377100324, 1.378));
        beenArrayList.add(new MarkerDetailBeen(40.574890493222213, 22.970715560744338, 1.378));
        beenArrayList.add(new MarkerDetailBeen(40.5805837, 22.9714327, 1.385));
        beenArrayList.add(new MarkerDetailBeen(40.586593641154749, 22.952982206504657, 1.388));
        beenArrayList.add(new MarkerDetailBeen(40.572299043036217, 22.960758957961957, 1.388));
        beenArrayList.add(new MarkerDetailBeen(40.57254606567048, 22.952441537230698, 1.398));
        beenArrayList.add(new MarkerDetailBeen(40.5787451, 22.9683624, 1.398));
        beenArrayList.add(new MarkerDetailBeen(40.5657261, 22.9751774, 1.424));
        beenArrayList.add(new MarkerDetailBeen(40.5860361, 22.9628557, 1.468));
        beenArrayList.add(new MarkerDetailBeen(40.591618065114, 22.960363131846066, 1.498));
    }

    private void showMarkerDetailView(Marker marker) {
        int i = (marker.getTag() == null) ? 0 : (int) marker.getTag();
        if (lastMarkerPosition != -1 && distArrayList.size() > lastMarkerPosition) {
            setUpMarker(distArrayList.get(lastMarkerPosition), lastMarkerPosition);
        }
        marker.remove();
        if (markerArrayList.size() > i)
            markerArrayList.get(i).remove();
        lastMarkerPosition = i;
        focusMarker(i);
    }


    private void focusMarker(int position) {
        MarkerDetailBeen been = beenArrayList.get(position);
        if (isClick == 1) {
            isClick = 0;
            setUpMarkerClick(been, position);
        } else {
            setUpMarkerClick(been, position);
        }
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markerArrayList) {
            builder.include(marker.getPosition());
        }
        LatLng cur = new LatLng(been.lat, been.lng);
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(cur, map.getCameraPosition().zoom));
    }

    private void managePreviousMarkers() {
        if (lastMarkerPosition != -1 && clickedMarker != null) {
            if (distArrayList.size() > lastMarkerPosition)
                setUpMarker(distArrayList.get(lastMarkerPosition), lastMarkerPosition);
        }
        if (clickedMarker != null) {
            clickedMarker.remove();
            clickedMarker = null;
        }
        lastMarkerPosition = -1;
    }

    private void setUpMarkerClick(final MarkerDetailBeen been, final int i) {
        Bitmap bitMap = setMySelectedMarker(been);
        LatLng latLng1 = null;
        try {
            latLng1 = new LatLng(been.lat, been.lng);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        if (bitMap != null) {
            try {
                if (clickedMarker != null)
                    clickedMarker.remove();
                if (latLng1 != null) {
                    clickedMarker = map.addMarker(new MarkerOptions()
                            .position(latLng1)
                            .icon(BitmapDescriptorFactory.fromBitmap(bitMap))
                            .zIndex(markerArrayList.size()));
                    clickedMarker.setTag(i);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private Bitmap setMySelectedMarker(MarkerDetailBeen been) {
        Bitmap bitmap = null;
        View marker = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_big_layout, null);
        final TextView numTxt = marker.findViewById(R.id.num_txt);
        ImageView markerImg = marker.findViewById(R.id.markerImg);
        markerImg.setImageResource(R.mipmap.ap_map_pin_selected);
        String finalPriceToShow = String.valueOf(been.value) + " €";
        numTxt.setText(finalPriceToShow);

        bitmap = createDrawableFromViewSelected(this, marker);
        return bitmap;
    }

    private Bitmap setMyMarker(final MarkerDetailBeen been) {
        Bitmap bitmap = null;
        try {
            View marker = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_layout, null);
            final TextView numTxt = marker.findViewById(R.id.num_txt);
            ImageView markerImg = marker.findViewById(R.id.markerImg);
            markerImg.setImageResource(R.mipmap.ap_map_pin);
            String finalPriceToShow = String.valueOf(been.value) + " €";
            numTxt.setText(finalPriceToShow);
            bitmap = createDrawableFromViewSelected(this, marker);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private void setUpMarker(final MarkerDetailBeen been, final int i) {
        Bitmap bitmap = setMyMarker(been);
        LatLng latLng = null;
        try {
            latLng = new LatLng(been.lat, been.lng);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (bitmap != null) {
            try { //.anchor(0.5f, 1.0f)
                Marker marker = map.addMarker(new MarkerOptions()
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                        .zIndex(beenArrayList.size() - 1 - i));
                marker.setTag(i);
                if (markerArrayList.size() > i) {
                    markerArrayList.remove(i);
                    markerArrayList.add(i, marker);
                } else {
                    markerArrayList.add(marker);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void initMainView() {
        builder = new LatLngBounds.Builder();
        if (beenArrayList.size() == 0) {
        } else {
            size = beenArrayList.size();
            for (int i = 0; i < size; i++) {
                distArrayList.add(beenArrayList.get(i));
                setUpMarker(distArrayList.get(i), i);
                builder.include(new LatLng(distArrayList.get(i).lat, distArrayList.get(i).lat));
            }
            LatLngBounds bounds = builder.build();
            LatLng latLngV = new LatLng(40.572299043036217, 22.960758957961957);
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngV, 13.5f));
        }

    }

    public static Bitmap createDrawableFromViewSelected(Activity activity, View view) {
        try {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            view.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
            view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
            view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
            view.buildDrawingCache();
            Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            view.draw(canvas);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void fragmentReplace(FragmentManager manager, Fragment fragment, int fragID) {
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(fragID, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
// [END maps_marker_on_map_ready]
