package vanhacks_index5.safetybutton;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private Button b;

    private static final String TAG = "MainActivity";
    private static MqttConnection mqttConnection;
    private static PreferencesManager preferencesManager;

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
                } else {
                    System.out.println("Connection exists");
                    String thisUserID = preferencesManager.getUserID();
                    String thisNumber = preferencesManager.getNumber();
                    mqttConnection.publish(thisUserID + "|" + thisNumber);
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
