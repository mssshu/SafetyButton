package vanhacks_index5.safetybutton;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LogIn extends AppCompatActivity {
    private final OkHttpClient client = new OkHttpClient();
    private EditText name;
    private EditText email;
    private EditText pass;
    private Button submit;
    public static final String PREFS_NAME = "AOP_PREFS";
    SharedPreferences settings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        name = (EditText) findViewById(R.id.editName);
        email = (EditText) findViewById(R.id.emailEdit);
        pass = (EditText) findViewById(R.id.passEdit);
        settings = this.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        submit = (Button) findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                new PostTask().execute();
            }
        });
    }
    private class PostTask extends AsyncTask<Void,Void,Void> {

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
        RequestBody formBody = new FormBody.Builder()
                .add("name", name.getText().toString())
                .add("email",email.getText().toString())
                .add("password",pass.getText().toString())
                .build();
        Request request = new Request.Builder()
                .url("http://199.116.240.37/api/user")
                .post(formBody)
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

        getToken(response.body().string());
    }
    public void getToken(String s){
        try {
            JSONObject jsonObj;
            jsonObj = new JSONObject(s);
            String token = jsonObj.getString("remember_token");
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("remember_token", token);
            editor.apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
