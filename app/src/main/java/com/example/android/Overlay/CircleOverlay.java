package com.example.android.Overlay;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import com.example.android.activities.R;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow;

public class CircleOverlay extends Overlay {
    private Drawable circleIcon;
    private GeoPoint location;
    private String title;
    private String informations;
    private String date;
    private Marker m = null;

    public CircleOverlay(Drawable icon, GeoPoint pos, String name, String infos, String d) {
        super();
        circleIcon = icon;
        location = pos;
        title = name;
        informations = infos;
        date = d;
    }

    @Override
    public void draw(Canvas canvas, MapView map, boolean shadow) {
        if (shadow) {
            return;
        }

        if (m == null && circleIcon != null) {

            //just in case the point is off the map, let's fix the coordinates
            if (location.getLongitude() < -180)
                location.setLongitude(location.getLongitude() + 360);
            if (location.getLongitude() > 180)
                location.setLongitude(location.getLongitude() - 360);
            //latitude is a bit harder. see https://en.wikipedia.org/wiki/Mercator_projection
            if (location.getLatitude() > 85.05112877980659)
                location.setLatitude(85.05112877980659);
            if (location.getLatitude() < -85.05112877980659)
                location.setLatitude(-85.05112877980659);

            m = new Marker(map);
            m.setInfoWindow(new MarkerInfoWindow(R.layout.infowindow_map, map));
            m.setPosition(location);
            m.setIcon(circleIcon);
            m.setTitle(title);
            m.setSnippet(informations);
            m.setSubDescription(date);
            map.getOverlayManager().add(m);
            map.invalidate();
        }
    }

}
