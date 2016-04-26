package com.example.admin.voxpopuli;
import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import android.content.DialogInterface;

import android.app.AlertDialog;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewDebug;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class MainActivity extends Activity implements LocationListener{
    SharedPreferences sharedPref;//=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    //= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    SharedPreferences.Editor editor;
    private static final int REQUEST_ENABLE_BT = 1;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 20 * 1000; // 1 minute
    protected LocationManager locationManager;
    protected Context context;
    protected boolean gps_enabled, network_enabled;
    double lat;double longitude;
    String location;
    Intent i;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    int flag;
    Button b1,b2,b3,b4;
    private BluetoothAdapter mBluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_PORTRAIT) {
            setContentView(R.layout.activity_main);
        } else {
            setContentView(R.layout.layoutl);
        }

   this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        sharedPref= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor= sharedPref.edit();
        //put your value
        //editor.putInt("flag", 0);

        //commits your edits
        //editor.commit();
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);}

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
// getting GPS status
        gps_enabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
// getting network status
        network_enabled = locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (gps_enabled) {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
        } else if (network_enabled) {
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
        };


    b1 = (Button) findViewById(R.id.button1);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent find = new Intent(MainActivity.this, FindActivity.class);
                startActivity(find);
            }
        });





        b2 = (Button) findViewById(R.id.button2);
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent track=new Intent(MainActivity.this,TrackActivity.class);
                startActivity(track);
            }});


        b3 = (Button) findViewById(R.id.button3);

        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent multiple=new Intent(MainActivity.this,MultipleActivity.class);
                startActivity(multiple);
            }});
        b4 = (Button) findViewById(R.id.button4);
        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                //Intent map=new Intent(MainActivity.this,MapsDemoActivity.class);
                startActivity(i);
            }});







    }
    @Override
    public void onLocationChanged(Location location) {
        Log.e("function","refresh");
        //flag=0;

        //now get Editor

        findBT();

        i=new Intent(getBaseContext(),MapsDemoActivity.class);
        Log.e("value of flag on loc", Integer.toString(flag));//(sharedPref.getInt("flag", 8))));
       // SharedPreferences sharedPref= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
       // if(sharedPref.getInt("flag", 8)==1)
        if(flag==1)
        {Log.e("checking for flag","found to be a 1");
            lat = location.getLatitude();
            longitude = location.getLongitude();
            getAddress();
            //i.putExtra("lat", lat);
            //i.putExtra("long", longitude);
            //i.putExtra("loc", location);
            Calendar c = Calendar.getInstance();
            int seconds = c.get(Calendar.SECOND);
            int minutes = c.get(Calendar.MINUTE);
            int hours = c.get(Calendar.HOUR_OF_DAY);
            int date=c.get(Calendar.DATE);
            int month=c.get(Calendar.MONTH);
            //SharedPreferences.Editor editor= sharedPref.edit();
            editor.putString("lat", Double.toString(lat));
            editor.putString("long", Double.toString(longitude));
            editor.putInt("hrs", hours);
            editor.putInt("min", minutes);
            editor.putInt("sec", seconds);
            editor.putInt("date",date);
            editor.putInt("month",month);
            //editor.putString("res",result);

            editor.commit();
            flag=0;
            //i.putExtra("s",seconds);
            //  i.putExtra("m",minutes);
            //i.putExtra("h",hours);}
            // startActivity(i);
        }

    }
    void findBT()

    {
        //flag=0;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(mBluetoothAdapter == null)

        {
            Toast.makeText(getApplicationContext(), "No bluetooth adapter available", Toast.LENGTH_SHORT).show();


        }

        if(!mBluetoothAdapter.isEnabled())

        {

            Intent enableBluetooth = new

                    Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

            startActivityForResult(enableBluetooth, 0);

        }

        mBluetoothAdapter.startDiscovery();

        final BroadcastReceiver mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                //SharedPreferences.Editor editor= sharedPref.edit();
                //put your value

                String action = intent.getAction();
                // When discovery finds a device
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {

                    Toast.makeText(getApplicationContext(),"received broadcast",Toast.LENGTH_SHORT).show();
                    // Get the bluetoothDevice object from the Intent
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (device.getName().equals("HC"))

                    {flag=1;
                        //now get Editor
                        //SharedPreferences.Editor editor= sharedPref.edit();
                        //put your value
                        //editor.putInt("flag", 1);

                        //commits your edits
                        //editor.commit();
                        //flag= 1;

                        Toast.makeText(getApplicationContext(), "hc found "+Integer.toString(flag), Toast.LENGTH_SHORT).show();

                    }
                    else
                        Log.e("device","hc not found");



                }
                Log.e("flag inside rx", Integer.toString(flag));

            }
        };
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);
        Log.e("flag outside receiver",Integer.toString(flag));
       // return flag;


    }
    void getAddress() { StringBuilder result = new StringBuilder();

        try {

            Geocoder gcd = new Geocoder(MainActivity.this, Locale.getDefault());

            List<Address> addresses = gcd.getFromLocation(lat, longitude, 100);





            if (addresses.size() > 0) {


                Address address = addresses.get(1);

                int maxIndex = address.getMaxAddressLineIndex();

                for (int x = 0; x <= maxIndex; x++) {

                    result.append(address.getAddressLine(x));

                    result.append(",");



                }
                //i.putExtra("result",(Serializable)result);
                editor.putString("res",result.toString());
                editor.commit();
                //Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();

            }



        } catch (Exception e) {
            Log.e("exception", e.getMessage());
        }


    }
    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude", "disable");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude", "enable");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude", "status");
    }
    public void find(View v) {

        }
    public void multiple(View v) {

    }




    public void track(View v)
    {
       }


    public void stop(View v){}
    public void onPause(){

        super.onPause();
    }
    public void onResume(){super.onResume();

    }





    public void infofn() {
        final AlertDialog.Builder alert2 = new AlertDialog.Builder(this);

        final EditText edittext = new EditText(MainActivity.this);
        alert2.setMessage("This app uses bluetooth to conveniently locate all your frequently used objects in matter of seconds by tagging them.the app works in two modes :Track and Find.\n Track:continuously monitors the object of interest by geo-fencing it,i.e whenever the object goes outside a predefined range,the app lights up an LED and sounds a buzzer helping you locate it .\nFind:this mode is used when you have lost an object and you want to find it.");
        alert2.setTitle("About the app");

        alert2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });
        alert2.show();
    }

    public void pairfn(){
        Intent track=new Intent(MainActivity.this,PairActivity.class);
        startActivity(track);


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        int id = item.getItemId();
        switch (item.getItemId()) {

            case R.id.info:
                infofn();
                return super.onOptionsItemSelected(item);


            case R.id.pair:
                pairfn();
                return super.onOptionsItemSelected(item);
            default:return super.onOptionsItemSelected(item);
                }




        }


    }
