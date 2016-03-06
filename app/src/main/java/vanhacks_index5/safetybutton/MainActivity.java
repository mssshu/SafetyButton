package vanhacks_index5.safetybutton;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private final OkHttpClient client = new OkHttpClient();
    private String LOG_TAG = MainActivity.class.getSimpleName();

    protected GoogleApiClient mGoogleApiClient;
    private Button b;

    private static final String TAG = "MainActivity";
    private static MqttConnection mqttConnection;
    private static PreferencesManager preferencesManager;

    private String lat = "none";
    private String lng = "none2";

    public Location mLastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        b = (Button) findViewById(R.id.button);

        PreferencesManager.initializeInstance(getApplicationContext());
        preferencesManager = PreferencesManager.getInstance();
        MqttConnection.initializeInstance(getApplicationContext());
        mqttConnection = MqttConnection.getInstance();
        System.out.println(preferencesManager.toString());

        //build googleapiclient
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        if (preferencesManager.getToken().equals("")) {
            Log.v(TAG, "No token, directing to Login.");
            Intent intent = new Intent(this, LogIn.class);
            startActivity(intent);
        } else {
            Log.v(TAG, "Token found.");
        }

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isNetworkConnected()) {
                    sendSMS("6044499444", "Please call 911");
                    b.setText("Emergency Mode Is On");
                } else {
                    System.out.println("Connection exists");
                    String thisUserID = preferencesManager.getUserID();
                    String thisNumber = preferencesManager.getNumber();
                    String thisName = preferencesManager.getName();
                    SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String dateString = fmt.format(Calendar.getInstance().getTime());
                    mqttConnection.publish(thisUserID + "|" + thisNumber + "|" + thisName + "|" + dateString);
                    b.setText("Emergency Mode Is On");

                    String location = thisUserID + "|" + lat + "|" + lng;
                    mqttConnection.publishGPS(location);
                    new PostTask().execute();
                    b.setEnabled(false);
                }
            }
        });
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    private void sendSMS(String phoneNumber, String message) {

        try {

            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(phoneNumber, null, message, null, null);
            Toast.makeText(getApplicationContext(), "SMS sent.", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "SMS failed, please try again.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
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
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient
        );
        if (mLastLocation != null){
            lat = String.valueOf(mLastLocation.getLatitude());
            lng = String.valueOf(mLastLocation.getLongitude());
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(LOG_TAG, "Connection Suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(LOG_TAG, "Connection Failed");
    }

    private class PostTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                run();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public void run() throws Exception {
        FormBody.Builder formBodyBuilder = new FormBody.Builder()
                .add("lat", lat)
                .add("lng", lng);

        FormBody formBody = formBodyBuilder.build();
        Request request = new Request.Builder()
                .url("http://199.116.240.37/api/userinfos?token=" + "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOjIsImlzcyI6Imh0dHA6XC9cLzE5OS4xMTYuMjQwLjM3XC9hcGlcL2F1dGhlbnRpY2F0ZSIsImlhdCI6MTQ1NzIzOTY3MCwiZXhwIjoxNDg4Nzc1NjcwLCJuYmYiOjE0NTcyMzk2NzAsImp0aSI6IjM1YTA3MGVkY2Y4MTI4N2VmNTM0ZDNhZGZlMTE4ZGZhIn0.IVGIOPLwUmVErU2V5QM51v0OvsgKA4lEqUEZCvzXx0A")
                .post(formBody)
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
        System.out.println(response.body().string());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            final Intent i = new Intent(this, InfoPage.class);

            startActivity(i);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
