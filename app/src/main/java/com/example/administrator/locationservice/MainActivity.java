package com.example.administrator.locationservice;

import android.app.FragmentManager;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    // 위치 관리자 객체
    LocationManager locationManager;
    TextView tv_info;
    Button btn_get_location;
    TextView tv_my_location;
    LocationListener locationListener;
    GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 구글 맵 띄우기
        FragmentManager fragmentManager = getFragmentManager();
        MapFragment mapFragment
                = (MapFragment) fragmentManager.findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        tv_info = findViewById(R.id.tv_info);
        btn_get_location = findViewById(R.id.btn_get_location);
        tv_my_location = findViewById(R.id.tv_my_location);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // 제공자 목록 얻기
        //providerList(인자값): false면 사용못해도 일단 얻기. gps안켜놨을때
        List<String> providerList = locationManager.getProviders(false);

        String result = "";
        for (String provider : providerList) {
            result += "Provider: " + provider + "\n";
            // ex) GPS, network, WIFI
            //      Provider: GPS
            //      Provider: network
            //      Provider: WIFI

        }

        // 프로바이더 조건 설정
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.NO_REQUIREMENT);
        criteria.setPowerRequirement(criteria.NO_REQUIREMENT);
        criteria.setAltitudeRequired(false); // 고도

        String best = locationManager.getBestProvider(criteria, true);
        result += "\n\n\n\n\n";
        result += "best: " + best;

        // 화면에 출력
        tv_info.setText(result);
        // 위치 얻기 버튼에 이벤트 부여
        btn_get_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 내가 실내인데 provider가 GPS라면? 위치 정보 불가능
                // 둘중 하나는 값이 들어올거니깐
                try {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, locationListener);
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 0, locationListener);
                    Toast.makeText(MainActivity.this, "위치 정보 수신 성공", Toast.LENGTH_SHORT).show();
                    // 안드로이드에는 SecurityException이 있다.
                } catch (SecurityException e) {
                    Toast.makeText(MainActivity.this, "위치 정보 수신 실패", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });

        // 위치 구해서 표시하기
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                tv_my_location.setText(
                        "위도: " + location.getLatitude() + "\n"
                                + "경도: " + location.getLongitude()

                );
                // 내 위치로 이동하기
                LatLng loadPoint = new LatLng(location.getLatitude(), location.getLongitude());
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loadPoint, 15));
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {


            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;


    }
}
