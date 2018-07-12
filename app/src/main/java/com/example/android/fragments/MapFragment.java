package com.example.android.fragments;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.InputDevice;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.Overlay.CircleOverlay;
import com.example.android.activities.BuildConfig;
import com.example.android.activities.R;
import com.example.android.models.RPI;
import com.example.android.models.SharedData;
import com.example.android.viewModels.MapModel;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.CopyrightOverlay;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class MapFragment extends Fragment {

    private Context mContext;
    DisplayMetrics dm;

    private SharedPreferences mPrefs;

    private MapView map = null;

    private CopyrightOverlay mCopyrightOverlay;
    private ScaleBarOverlay mScaleBarOverlay;

    private MapModel mapModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mapModel = ViewModelProviders.of(getActivity()).get(MapModel.class);
        mapModel.syncMapData();
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        map = rootView.findViewById(R.id.mapView);

        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);
        Log.d("Event", "avant avant");

        map.setOnGenericMotionListener((v, event) -> {
            Log.d("Event", "avant");
            if (0 != (event.getSource() & InputDevice.SOURCE_CLASS_POINTER)) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_SCROLL:
                        if (event.getAxisValue(MotionEvent.AXIS_VSCROLL) < 0.0f)
                            map.getController().zoomOut();
                        else {
                            // Centers the map on the current mouse location before zooming in
                            IGeoPoint iGeoPoint = map.getProjection().fromPixels(
                                    (int) event.getX(), (int) event.getY());
                            map.getController().animateTo(iGeoPoint);
                            map.getController().zoomIn();
                        }
                        Log.d("event2", "scroll");
                        return true;
                    // @TODO : setup other eventlistener pour naviguer dans la map
                    case MotionEvent.ACTION_BUTTON_PRESS:
                        Log.d("event", "click");
                        MarkerInfoWindow.closeAllInfoWindowsOn(map);
                        return true;
                    case MotionEvent.ACTION_DOWN:
                        Log.d("event", "down");
                        return true;
                    default:
                        Log.d("event", "autre");
                }
            }
            return false;
        });
        map.setTileSource(TileSourceFactory.MAPNIK);
        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("Map", "OnActivityCreated");

        mContext = this.getActivity();
        dm = mContext.getResources().getDisplayMetrics();
        mPrefs = mContext.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);


        map.setBuiltInZoomControls(false);
        map.setMaxZoomLevel(12.0);
        //needed for pinch zooms :
        map.setMultiTouchControls(true);
        //scales tiles to the current screen's DPI, helps with readability of labels :
        map.resetTilesScaleFactor();
        //map.setTilesScaledToDpi(true);

        //the rest of this is restoring the last map location the user looked at
        final float zoomLevel = mPrefs.getFloat(getString(R.string.prefs_zoom_level_double),
                mPrefs.getInt(getString(R.string.prefs_zoom_level), 1));
        map.getController().setZoom(zoomLevel);
        final float orientation = mPrefs.getFloat(getString(R.string.prefs_orientation), 0);
        map.setMapOrientation(orientation, false);
        final String latitudeString = mPrefs.getString(getString(R.string.prefs_latitude_string), "45.188529");
        final String longitudeString = mPrefs.getString(getString(R.string.prefs_longitude_string), "5.724523999999974");
        if (latitudeString == null || longitudeString == null) {
            // case handled for historical reasons only
            final int scrollX = mPrefs.getInt(getString(R.string.prefs_scroll_x), 0);
            final int scrollY = mPrefs.getInt(getString(R.string.prefs_scroll_y), 0);
            map.scrollTo(scrollX, scrollY);
        } else {
            final double latitude = Double.valueOf(latitudeString);
            final double longitude = Double.valueOf(longitudeString);
            map.setExpectedCenter(new GeoPoint(latitude, longitude));
        }

        addOverlays();

        mapModel.liveRpiArrayList.observe(this, rpiArrayList -> {
            SimpleDateFormat ft = new SimpleDateFormat("EEEE, d MMM, yyyy HH'h'mm",
                    Locale.getDefault());
            map.getOverlays().clear();
            addOverlays();
            for (RPI rpi : rpiArrayList) {
                StringBuilder txt = new StringBuilder();
                txt.append("<br>");
                for (SharedData sharedData : rpi.getSharedDataArrayList()) {
                    txt.append(sharedData.getType().toUpperCase())
                            .append(" : ")
                            .append(sharedData.getValue())
                            .append("<br>");
                }
                CircleOverlay circleOverlay = new CircleOverlay(
                        this.getResources().getDrawable(R.drawable.icon_circle_100px),
                        rpi.getPosition(), rpi.getName(), txt.toString(),
                        ft.format(rpi.getSharedDataArrayList().get(0).getDate()));
                map.getOverlays().add(circleOverlay);
            }
        });
    }

    @Override
    public void onPause() {
        if (map != null) {
            //save the current location
            final SharedPreferences.Editor edit = mPrefs.edit();
            edit.putString(getString(R.string.prefs_tile_source), map.getTileProvider().getTileSource().name());
            edit.putFloat(getString(R.string.prefs_orientation), map.getMapOrientation());
            edit.putString(getString(R.string.prefs_latitude_string), String.valueOf(map.getMapCenter().getLatitude()));
            edit.putString(getString(R.string.prefs_longitude_string), String.valueOf(map.getMapCenter().getLongitude()));
            edit.putFloat(getString(R.string.prefs_zoom_level_double), (float) map.getZoomLevelDouble());
            edit.apply();

            map.onPause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (map != null) {
            final String tileSourceName = mPrefs.getString(getString(R.string.prefs_tile_source),
                    TileSourceFactory.DEFAULT_TILE_SOURCE.name());
            try {
                final ITileSource tileSource = TileSourceFactory.getTileSource(tileSourceName);
                map.setTileSource(tileSource);
            } catch (final IllegalArgumentException e) {
                map.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
            }
            map.onResume();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (map != null)
            map.onDetach();
        map = null;
    }

    /**
     * An appropriate place to override and add overlays.
     */
    protected void addOverlays() {
        //Copyright overlay
        mCopyrightOverlay = new CopyrightOverlay(mContext);
        mCopyrightOverlay.setAlignRight(true);
        map.getOverlays().add(mCopyrightOverlay);

        //map scale
        mScaleBarOverlay = new ScaleBarOverlay(map);
        mScaleBarOverlay.setCentred(true);
        mScaleBarOverlay.setScaleBarOffset(dm.widthPixels / 2, 5);
        map.getOverlays().add(mScaleBarOverlay);
    }
}

