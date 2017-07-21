package com.rodafleets.rodadriver.custom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.rodafleets.rodadriver.R;
import com.rodafleets.rodadriver.model.VehicleRequest;
import com.rodafleets.rodadriver.model.VehicleType;

import java.util.ArrayList;
import java.util.HashSet;

public class VehicleRequestListAdapter extends ArrayAdapter<VehicleRequest> {

    // Initialise custom font, for example:
    private Context context;
    private Typeface font;

    private Bitmap greenIcon;
    private Bitmap redIcon;

    private ArrayList<VehicleRequest> vehicleRequests;

    private final HashSet<MapView> mMaps = new HashSet<MapView>();

    public VehicleRequestListAdapter(Context context, int resource, ArrayList<VehicleRequest> items, Typeface font) {
        super(context, resource, items);
        this.context = context;
        this.vehicleRequests = items;
        this.font = font;

        initMarkerBitmaps();
    }


    private void initMarkerBitmaps(){

        int height = 45;
        int width = 32;

        greenIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.marker_green);
        redIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.marker_red);

        greenIcon = Bitmap.createScaledBitmap(greenIcon, width, height, false);
        redIcon = Bitmap.createScaledBitmap(redIcon, width, height, false);
    }

    /*private view holder class*/
    private class ViewHolder implements OnMapReadyCallback {
        MapView mapView;
        TextView customerName;
        GoogleMap map;

        @Override
        public void onMapReady(GoogleMap googleMap) {

            MapsInitializer.initialize(context);
            map = googleMap;

            VehicleRequest data = (VehicleRequest) mapView.getTag();

            if (data != null) {
                setMapLocation(map, data);
            }
        }

        /**
         * Initialises the MapView by calling its lifecycle methods.
         */
        public void initializeMapView() {
            if (mapView != null) {
                // Initialise the MapView
                mapView.onCreate(null);
                // Set the map ready callback to receive the GoogleMap object
                mapView.getMapAsync(this);
                mapView.setClickable(false);
            }
        }
    }

    private void setMapLocation(GoogleMap map, VehicleRequest vehicleRequest) {
        // Add a marker for this item and set the camera
//        map.moveCamera(CameraUpdateFactory.newLatLngZoom(data.location, 13f));

//        map.addMarker(new MarkerOptions().position(data.location));

        // Set the map type back to normal.

        if (vehicleRequest != null) {

            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            LatLng originLatLng = new LatLng(vehicleRequest.getPickupPointLat(), vehicleRequest.getPickupPointLng());

            MarkerOptions originMarkerOptions = new MarkerOptions();

            originMarkerOptions.position(originLatLng);
//            originMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_green));
            originMarkerOptions.icon(BitmapDescriptorFactory.fromBitmap(greenIcon));

            Marker originMarker = map.addMarker(originMarkerOptions);

            builder.include(originMarker.getPosition());

            LatLng destinationLatLng = new LatLng(vehicleRequest.getDropPointLat(), vehicleRequest.getDropPointLng());

            Log.i("RODA", vehicleRequest.getDropPointLat() + "");
            Log.i("RODA", vehicleRequest.getDropPointLng() + "");

            MarkerOptions destinationMarkerOptions = new MarkerOptions();

            destinationMarkerOptions.position(destinationLatLng);
//            destinationMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_red));
            destinationMarkerOptions.icon(BitmapDescriptorFactory.fromBitmap(redIcon));

            Marker destinationMarker = map.addMarker(destinationMarkerOptions);
            builder.include(destinationMarker.getPosition());

            LatLngBounds bounds = builder.build();

            int padding = 20; // offset from edges of the map in pixels

            map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));

        }

        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        Log.i("RODA", "POSITION = " + position);
        VehicleRequest request = vehicleRequests.get(position);

        if(request == null) {
            Log.i("RODA", "request is null");
        } else {
            Log.i("RODA", "request >>>>>>> " + request.getCustomerName());
        }

        View rowView = convertView;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(rowView == null) {
            rowView = inflater.inflate(R.layout.vehicle_request_list_view_item, parent, false);

            holder = new ViewHolder();
            holder.customerName = (TextView) rowView.findViewById(R.id.vehicle_request_customer_name);
            holder.mapView = (MapView) rowView.findViewById(R.id.vehicle_request_map);

            rowView.setTag(holder);

            // Initialise the MapView
            holder.initializeMapView();

            // Keep track of MapView
            mMaps.add(holder.mapView);

        } else {
            holder = (ViewHolder) rowView.getTag();
        }

//        holder.customerName.setText(request.getCustomerName());
        holder.customerName.setText(request.getDestinationAddress());
        holder.mapView.setTag(request);

        if (holder.map != null) {
            // The map is already ready to be used
            setMapLocation(holder.map, request);
        }

        return rowView;
    }
}

//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//
//        View row = convertView;
//        ViewHolder holder;
//
//        // Check if a view can be reused, otherwise inflate a layout and set up the view holder
//        if (row == null) {
//            // Inflate view from layout file
//            row = layoutInflater.inflate(R.layout.vehicle_request_list_view_item, null);
//
//            // Set up holder and assign it to the View
//            holder = new ViewHolder();
//            holder.mapView = (MapView) row.findViewById(R.id.vehicle_request_map);
//            holder.title = (TextView) row.findViewById(R.id.vehicle_request_customer_name);
//            // Set holder as tag for row for more efficient access.
//            row.setTag(holder);
//
//            // Initialise the MapView
//            holder.initializeMapView();
//
//            // Keep track of MapView
//            mMaps.add(holder.mapView);
//        } else {
//            // View has already been initialised, get its holder
//            holder = (ViewHolder) row.getTag();
//        }
//
//        VehicleRequest request = getItem(position);
//        holder.mapView.setTag(request);
//
//        // Ensure the map has been initialised by the on map ready callback in ViewHolder.
//        // If it is not ready yet, it will be initialised with the NamedLocation set as its tag
//        // when the callback is received.
//        if (holder.map != null) {
//            // The map is already ready to be used
//            //setMapLocation(holder.map, request);
//        }
//
//        // Set the text label for this item
//        holder.title.setText(request.getCustomerName());
//
//        return row;
//    }
//
//    /**
//     * Retuns the set of all initialised {@link MapView} objects.
//     *
//     * @return All MapViews that have been initialised programmatically by this adapter
//     */
//    public HashSet<MapView> getMaps() {
//        return mMaps;
//    }
//
//}
//
//class ViewHolder implements OnMapReadyCallback {
//
//    MapView mapView;
//
//    TextView title;
//
//    GoogleMap map;
//
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//
//        MapsInitializer.initialize(getApplicationContext());
//        map = googleMap;
//        VehicleRequest vehicleRequest = (VehicleRequest) mapView.getTag();
//        if (vehicleRequest != null) {
//
//            LatLngBounds.Builder builder = new LatLngBounds.Builder();
//
//            LatLng originLatLng = new LatLng(vehicleRequest.getPickupPointLat(), vehicleRequest.getPickupPointLng());
//            MarkerOptions originMarkerOptions = new MarkerOptions();
//            originMarkerOptions.position(originLatLng);
//            originMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_green));
//            Marker originMarker = map.addMarker(originMarkerOptions);
//            builder.include(originMarker.getPosition());
//
//            LatLng destinationLatLng = new LatLng(vehicleRequest.getDropPointLat(), vehicleRequest.getDropPointLng());
//            MarkerOptions destinationMarkerOptions = new MarkerOptions();
//            destinationMarkerOptions.position(destinationLatLng);
//            destinationMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_red));
//            Marker destinationMarker = map.addMarker(destinationMarkerOptions);
//            builder.include(destinationMarker.getPosition());
//
//            LatLngBounds bounds = builder.build();
//            int padding = 20; // offset from edges of the map in pixels
//            map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
//
//            // Set the map type back to normal.
//            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//        }
//    }
//
//    /**
//     * Initialises the MapView by calling its lifecycle methods.
//     */
//    public void initializeMapView() {
//        if (mapView != null) {
//            // Initialise the MapView
//            mapView.onCreate(null);
//            // Set the map ready callback to receive the GoogleMap object
//            mapView.getMapAsync(this);
//        }
//    }
//
//}
