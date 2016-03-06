package vanhacks_index5.safetybutton;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class InfoPage extends AppCompatActivity {
    private final OkHttpClient client = new OkHttpClient();
    private int[] textEdit = {
            R.id.nameEdit,
            R.id.homeAddressEdit,
            R.id.workAddressEdit,
            R.id.partnerHomeAddressEdit,
            R.id.partnerWorkAddressEdit,
            R.id.partnerEdit,
            R.id.partnerPlateEdit,
            R.id.plateEdit,
            R.id.childrenEdit,
            R.id.legalEdit,
            R.id.threatsEdit,
            R.id.weaponsEdit,
            R.id.commentEdit
    };
    private int[] text = {
            R.id.name,
            R.id.homeAddress,
            R.id.workAddress,
            R.id.partnerHomeAddress,
            R.id.partnerWorkAddress,
            R.id.partner,
            R.id.partnerPlate,
            R.id.plate,
            R.id.children,
            R.id.legal,
            R.id.threats,
            R.id.weapons,
            R.id.comment
    };
    private String[] textValues;
    private String[] textEditValues;
    private Button b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        textValues = new String[text.length];
        textEditValues = new String[textEdit.length];
        b = (Button) findViewById(R.id.infoButton);

//        for (int i = 0; i < text.length; i++) {
//            EditText edit = (EditText) findViewById(textEdit[i]);
//            textEditValues[i] = edit.toString();
//            TextView textView = (TextView) findViewById(text[i]);
//            textValues[i] = textView.toString();
//        }
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PostTask().execute();
            }
        });
    }
    private class PostTask extends AsyncTask<Void,Void,Void>{

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
                .add("User_ID","1").add("Name", "jllp")
                .build();
        Request request = new Request.Builder()
                .url("http://199.116.240.37/api/userinfos?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOjEsImlzcyI6Imh0dHA6XC9cL3d3dy5zYWZldHlidXR0b24ubG9jYWxcL2FwaVwvYXV0aGVudGljYXRlIiwiaWF0IjoxNDU3MjIzOTg2LCJleHAiOjE0NTcyMjc1ODYsIm5iZiI6MTQ1NzIyMzk4NiwianRpIjoiZjUwNTAzM2ZiNjNiN2MyMGQxYjRjMTM1NzE1N2ZjZDYifQ.RbLZX4ktYXtvM8nUwQpSzZTaFcK-W1v-muYYgT1Mz2k")
                .post(formBody)
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

        System.out.println(response.body().string());
    }

}
