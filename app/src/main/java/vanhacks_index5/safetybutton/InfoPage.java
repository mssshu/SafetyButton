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

    private String[] textValues;
    private String[] textEditValues;

    public EditText nameEdit;
    public EditText homeAddressEdit;
    public EditText workAddressEdit;
    public EditText partnerHomeAddressEdit;
    public EditText partnerWorkAddressEdit;
    public EditText partnerNameEdit;
    public EditText childrenNameEdit;
    public EditText legalOrdersEdit;
    public EditText plateEdit;
    public EditText partnerPlateEdit;
    public EditText commentEdit;

    private Button b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        b = (Button) findViewById(R.id.infoButton);
        nameEdit = (EditText) findViewById(R.id.nameEdit);
        homeAddressEdit = (EditText) findViewById(R.id.homeAddressEdit);
        workAddressEdit = (EditText) findViewById(R.id.workAddressEdit);
        partnerHomeAddressEdit = (EditText) findViewById(R.id.partnerHomeAddressEdit);
        partnerWorkAddressEdit = (EditText) findViewById(R.id.partnerWorkAddressEdit);
        partnerNameEdit = (EditText) findViewById(R.id.partnerNameEdit);
        childrenNameEdit = (EditText) findViewById(R.id.childrenNameEdit);
        legalOrdersEdit = (EditText) findViewById(R.id.legalOrdersEdit);
        plateEdit = (EditText) findViewById(R.id.plateEdit);
        partnerPlateEdit = (EditText) findViewById(R.id.partnerPlateEdit);
        commentEdit = (EditText) findViewById(R.id.commentEdit);

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
                .add("User_ID", "1")
                .add("Name", nameEdit.getText().toString())
                .add("Home_Address", homeAddressEdit.getText().toString())
                .add("Work_Address", workAddressEdit.getText().toString())
                .add("Partner_Home_Address", partnerHomeAddressEdit.getText().toString())
                .add("Partner_Work_Address", partnerWorkAddressEdit.getText().toString())
                .add("Partner_Name", partnerNameEdit.getText().toString())
                .add("Partner_License_Plate", partnerPlateEdit.getText().toString())
                .add("ChildrenNames", childrenNameEdit.getText().toString())
                .add("Legal_Orders", legalOrdersEdit.getText().toString())
                .add("License_Plate", plateEdit.getText().toString())
                .add("Other", commentEdit.getText().toString())
                .build();


        Request request = new Request.Builder()
                .url("http://199.116.240.37/api/userinfos?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOjIsImlzcyI6Imh0dHA6XC9cLzE5OS4xMTYuMjQwLjM3XC9hcGlcL2F1dGhlbnRpY2F0ZSIsImlhdCI6MTQ1NzIzMTgwNSwiZXhwIjoxNDU3MjM1NDA1LCJuYmYiOjE0NTcyMzE4MDUsImp0aSI6IjI1MjIzYzJlMDdkN2UxOWY4MjJjODdlODI1MGU0Mjg0In0.cZ-NM1-V5monRedVXN6Gp0-6P50LV_hF_iinDKoN2_c")
                .post(formBody)
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

        System.out.println(response.body().string());
    }

}
