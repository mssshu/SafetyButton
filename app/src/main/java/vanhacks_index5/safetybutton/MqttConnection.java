package vanhacks_index5.safetybutton;

import android.content.Context;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Created by Luda on 2016-03-05.
 */
public class MqttConnection {

    private static MqttConnection mInstance;
    private static MqttAndroidClient client;
    private static final String TAG = "MqttConnection";
    private Context thisContext;

    public MqttConnection(Context context) {
        thisContext = context;
        run();
    }

    public static synchronized void initializeInstance(Context context) {
        if (mInstance == null) {
            mInstance = new MqttConnection(context);
        }
    }

    public static synchronized MqttConnection getInstance() {
        if (mInstance == null) {
            throw new IllegalStateException(MqttConnection.class.getSimpleName() +
                    " is not initialized, call initializeInstance(..) method first.");
        }
        return mInstance;
    }

    public void run() {
        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(
                thisContext,
                "tcp://kbme.ca:1883",
                clientId
        );

        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d(TAG, "onSuccess");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d(TAG, "onFailure");

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public boolean publish(String content) {
        System.out.println("Publishing message: " + content);
        MqttMessage message = new MqttMessage(content.getBytes());
        message.setQos(2);
        try {
            client.publish("EmergencyChannel", message);
        } catch (MqttException e) {
            e.printStackTrace();
        }
        System.out.println("Message published");
        return true;
    }

}
