package vanhacks_index5.safetybutton;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.regex.Pattern;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LogIn extends AppCompatActivity {
    private static boolean isRegistration = true;
    private final OkHttpClient client = new OkHttpClient();
    private EditText name;
    private EditText email;
    private EditText pass;
    private EditText number;
    private TextView loginText;
    private TextInputLayout nameLabel;
    private Button submit;

    // REGEX to confirm valid email address, thanks K9mail
    public static final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
    );

    /**
     * Checks if an email conforms to a valid format
     *
     * @param email
     * @return
     */
    private boolean checkEmail(String email) {
        return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        name = (EditText) findViewById(R.id.input_name);
        nameLabel = (TextInputLayout) findViewById(R.id.name_label);
        email = (EditText) findViewById(R.id.input_email);
        pass = (EditText) findViewById(R.id.input_password);
        number = (EditText) findViewById(R.id.input_number);
        loginText = (TextView) findViewById(R.id.link_login);

        submit = (Button) findViewById(R.id.btn_signup);
        submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                boolean errorFlag = false;
                if (isRegistration && name.getText().toString().equals("")) {
                    Toast.makeText(
                            getApplicationContext(),
                            "Please enter your name.",
                            Toast.LENGTH_LONG
                    ).show();
                    errorFlag = true;
                } else if (email.getText().toString().equals("")) {
                    Toast.makeText(
                            getApplicationContext(),
                            "You must enter an email address.",
                            Toast.LENGTH_LONG
                    ).show();
                    errorFlag = true;
                } else if (pass.getText().toString().equals("")) {
                    Toast.makeText(
                            getApplicationContext(),
                            "You must enter a password.",
                            Toast.LENGTH_LONG
                    ).show();
                    errorFlag = true;
                } else if (!checkEmail(email.getText().toString())) {
                    Toast.makeText(
                            getApplicationContext(),
                            "Must use a valid email!",
                            Toast.LENGTH_LONG
                    ).show();
                    errorFlag = true;
                } else {
                    new PostTask().execute();
                }
            }
        });

        loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name.setHint("");
                name.setVisibility(View.INVISIBLE);
                nameLabel.setVisibility(View.INVISIBLE);
                submit.setText("Login");
                loginText.setVisibility(View.INVISIBLE);
                isRegistration = false;
            }
        });

        setNumber();
    }

    private void setNumber() {
        TelephonyManager tMgr = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        String mPhoneNumber = tMgr.getLine1Number();
        number.setText(mPhoneNumber);
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
                .add("email", email.getText().toString())
                .add("password", pass.getText().toString());

        // We only add the name field if this is a registration
        if (isRegistration) {
            formBodyBuilder.add("name", name.getText().toString());
            formBodyBuilder.add("number", number.getText().toString());
        }

        FormBody formBody = formBodyBuilder.build();
        Request request = new Request.Builder()
                .url("http://199.116.240.37/api/user")
                .post(formBody)
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

        getToken(response.body().string());
    }

    public void getToken(String s) {
        try {
            JSONObject jsonObj;
            jsonObj = new JSONObject(s);
            String token = jsonObj.getString("remember_token");
            String UserID = jsonObj.getString("id");
            String Number = jsonObj.getString("number");

            PreferencesManager.getInstance().setToken(token);
            PreferencesManager.getInstance().setUserID(UserID);
            PreferencesManager.getInstance().setNumber(Number);

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
