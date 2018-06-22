package com.example.android.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.InputDevice;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;


import com.example.android.models.SharedData;
import com.example.android.viewModels.MapModel;

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.CopyrightOverlay;


public class MapFragment extends Fragment {
   private MapView map = null;
   private MapModel mapModel;
   private SharedData[] sharedDataList = {};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

   @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

       map = new MapView(inflater.getContext());

       mapModel = ViewModelProviders.of(getActivity()).get(MapModel.class);

       mapModel.lastHour.observe(this, s -> {
           mapModel.syncMapData();
       });
       mapModel.getLastHour();


       mapModel.liveSharedDataArrayList.observe(this, arrayList -> {
           arrayList.toArray(sharedDataList);
           for (int i = 0; i < sharedDataList.length; i++) {
               SharedData sharedData = sharedDataList[i];
               Log.d(MapFragment.class.toString(), "SharedData: "+sharedData.getType()+", "+sharedData.getLatitude()+
                       ", "+sharedData.getLongitude()+", "+sharedData.getDate()+", "+sharedData.getValue());
           }
       });

       map.setOnGenericMotionListener(new View.OnGenericMotionListener() {
           /**
            * mouse wheel zooming ftw
            * http://stackoverflow.com/questions/11024809/how-can-my-view-respond-to-a-mousewheel
            *
            * @param v
            * @param event
            * @return
            */
           @Override
           public boolean onGenericMotion(View v, MotionEvent event) {
               if (0 != (event.getSource() & InputDevice.SOURCE_CLASS_POINTER)) {
                   switch (event.getAction()) {
                       case MotionEvent.ACTION_SCROLL:
                           if (event.getAxisValue(MotionEvent.AXIS_VSCROLL) < 0.0f)
                               map.getController().zoomOut();
                           else {
                               map.getController().zoomIn();
                           }
                           return true;
                   }
               }
               return false;
           }
       });
       map.setTileSource(TileSourceFactory.MAPNIK);
       return map;
   }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (map != null) {
            addOverlays();

            final Context context = this.getActivity();
            final DisplayMetrics dm = context.getResources().getDisplayMetrics();

            CopyrightOverlay copyrightOverlay = new CopyrightOverlay(getActivity());
            copyrightOverlay.setTextSize(10);

            map.getOverlays().add(copyrightOverlay);
            map.setBuiltInZoomControls(true);
            map.setMultiTouchControls(true);
            map.setTilesScaledToDpi(true);
        }
    }


    @Override
    public void onPause(){
        if (map != null) {
            map.onPause();
        }
        super.onPause();
    }

    @Override
    public void onResume(){
        super.onResume();
        if (map != null) {
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
        //
    }
}
