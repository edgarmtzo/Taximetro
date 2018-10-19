package com.unam.taximetro;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.security.PublicKey;

public class MainActivity extends AppCompatActivity implements LocationListener {

    public Handler mHandler=new Handler();

    int seguir;
    double lat, lon, lat1, lon1, lat2, lon2, mtrs, banderazo, precioxmetro, totalapagar;
    double earth_radius = 6371;
    LocationManager gps;
    EditText editBanderazo, editPrecio;
    TextView txtTotal;
    Button botonIniciar, botonParar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gps = (LocationManager) getSystemService(LOCATION_SERVICE);
        editBanderazo = (EditText) findViewById(R.id.editBanderazo);
        editPrecio = (EditText) findViewById(R.id.editPrecio);
        txtTotal = (TextView) findViewById(R.id.txtTotal);
        botonIniciar = (Button) findViewById(R.id.botonIniciar);
        botonParar = (Button) findViewById(R.id.botonParar);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        gps.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);

        botonIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHandler.postDelayed(fun2, 0);
            }
        });

        botonParar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                banderazo=Double.parseDouble(editBanderazo.getText().toString());
                precioxmetro=Double.parseDouble(editPrecio.getText().toString());
                totalapagar=banderazo+(precioxmetro * mtrs);
                txtTotal.setText("El total a pagar es: $"+String.format("%.2f", totalapagar));
                seguir=1;
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location.getLongitude()!=0.0){
            lat = (float) location.getLatitude();
            lon = (float) location.getLongitude();
        }
        else {

            txtTotal.setText("Localizando...");
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast msjgpson= Toast.makeText(getApplicationContext(), "El GPS esta encendido", Toast.LENGTH_LONG);
        msjgpson.show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast msjgpsoff= Toast.makeText(getApplicationContext(), "Enciende tu GPS por favor", Toast.LENGTH_LONG);
        msjgpsoff.show();
    }

    public Runnable fun0=new Runnable() {
        @Override
        public void run() {
            lat1=lat;
            lon1=lon;
        }
    };

    public Runnable fun1=new Runnable() {
        @Override
        public void run() {
            lat2=lat;
            lon2=lon;
        }
    };

    public Runnable fun2=new Runnable() {
        @Override
        public void run() {
            mHandler.postDelayed(fun0,0);
            mHandler.postDelayed(fun1, 1000);

            double dLat = Math.toRadians(lat2-lat1);
            double dLon = Math.toRadians(lon2-lon1);
            double a = Math.sin(dLat/2) * Math.sin(dLat/2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon/2) * Math.sin(dLon/2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            double d = earth_radius * c;
            mtrs = mtrs + d;

            if (seguir==0){
                mHandler.postDelayed(fun2, 0);
            }
            else {
                Toast.makeText(getApplicationContext(), "Se ha detenido la aplicacion", Toast.LENGTH_LONG).show();
            }
        }
    };
}
