package ar.com.sebas.compass;

import android.Manifest;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class CompassActivity extends FragmentActivity implements OnMapReadyCallback, ICompassListener, GoogleMap.OnMapClickListener {

    private GoogleMap mMap;
    private Compass compass;
    private LocationHelper locationHelper;
    private android.support.v4.app.FragmentManager fm;
    private double angle;
    private double latitude;
    private double longitude;
    private Location lastLocation;
    private ImageView arrow;
    private float lastAngle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);

        arrow = (ImageView)findViewById(R.id.arrow);

        fm = getSupportFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, 200);
        }
        if (ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{ACCESS_COARSE_LOCATION}, 200);
        }
        compass = new Compass(this, this);
        compass.setAzimuthMinimumDiff(1f);
    }

    @Override
    protected void onStart() {
        compass.start();
        super.onStart();
        //inputLocationContent.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onStop() {
        compass.stop();
        super.onStop();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
        }
        locationHelper = new LocationHelper(this);
        mMap.setMyLocationEnabled(false);
        mMap.setOnMapClickListener(this);
    }

    @Override
    public void onChangeDirection(float bearing) {
        if(locationHelper != null) {
            lastLocation = locationHelper.getLastLocation();
            if(lastLocation != null) {
                lastLocation.setBearing(bearing);
                LatLng current = new LatLng(lastLocation.getLatitude(),
                        lastLocation.getLongitude());

                CameraPosition googlePlex = CameraPosition.builder()
                        .target(current)
                        .zoom(15)
                        .bearing(bearing)
                        .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex));
                updateArrow();
            }
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
   }

   public void onChangeInputLocation(double latitude, double longitude){
       this.latitude = latitude;
       this.longitude = longitude;
       updateArrow();
   }

   void updateArrow()
   {
       double radH1 = lastLocation.getLatitude() / 180.0 * Math.PI;
       double radV1 = lastLocation.getLongitude() / 180.0 * Math.PI;
       double radH2 = latitude / 180.0 * Math.PI;
       double radV2 = longitude / 180.0 * Math.PI;

       double tg = (radH2 - radH1) / (radV2 - radV1);
       angle = Math.atan(tg) / Math.PI * 180.0;
       Location targetLocation = new Location("");
       targetLocation.setLatitude(latitude);
       targetLocation.setLongitude(longitude);
       //lastLocation.bearingTo(targetLocation
//(float)angle;//
       float destAngle = lastLocation.bearingTo(targetLocation)- lastLocation.getBearing();
       Log.d("onChangeLocation from:", String.valueOf(lastLocation.getBearing()));
       Log.d("onChangeLocation to:", String.valueOf(lastLocation.bearingTo(targetLocation)));
       Log.d("onChangeLocation angle", String.valueOf(angle));

       Animation an = new RotateAnimation(lastAngle, destAngle,
               Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
               0.5f);
       lastAngle = destAngle;
       an.setDuration(500);
       an.setRepeatCount(0);
       an.setFillAfter(true);

       arrow.startAnimation(an);
   }

    public void openSearch(View view) {
        InputLocationDialog dialogFragment = new InputLocationDialog ();
        dialogFragment.show(fm, "Input Location");
    }
}
