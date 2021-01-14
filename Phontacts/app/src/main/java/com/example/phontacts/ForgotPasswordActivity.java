package com.example.phontacts;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ForgotPasswordActivity extends AppCompatActivity {
    EditText email;
    Button sendBtn;

    AsyncTask<Void, Void, Void> chck;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        email = findViewById(R.id.fpEmail);
        sendBtn = findViewById(R.id.sendButton);
    }

    public void sendEmail(View view) {
        String emailid = email.getText().toString();

        if (emailid.isEmpty()) {
            email.setError("This field can't be empty!");
            return;
        } else {

            chck = new MailChecker(emailid);
            chck.execute();


        }
    }


    public class MailChecker extends AsyncTask<Void, Void, Void> {
        String res;
        Boolean valid=false;
        String email;

        public MailChecker(String email) {

            this.email = email;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public void mailOkSender(String mailid) {
            Log.v("call", "mailOkSender CAlled!");
            String url = R.string.domain+"/forgot/password";
            OkHttpClient okHttpClient = new OkHttpClient();


            MediaType JSONType = MediaType.parse("application/json;charset=utf-8");
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("username", "");
                jsonObject.put("password", "");
                jsonObject.put("email", mailid);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            RequestBody requestBody = RequestBody.create(JSONType, jsonObject.toString());
            Log.v("mailSenderRequestBody", requestBody.toString());
            Request request = new Request.Builder().url(url).post(requestBody).build();
           runOnUiThread(new Runnable() {

                public void run() {

                    Toast.makeText(ForgotPasswordActivity.this, "Password has been sent to your mail!", Toast.LENGTH_SHORT).show();

                }
            });



            try {
                Response response = okHttpClient.newCall(request).execute();
                Log.v("MailSenderResponse", response.body().string());

              //  Toast.makeText(ForgotPasswordActivity.this,"Password has been sent to your mail!",Toast.LENGTH_SHORT);
            } catch (IOException e) {
                Log.v("mailSenderResponse", e.getMessage());
            }


        }

        public void mailOkCheck(final String email) {

            String url = R.string.domain+"/users/emails";
            final OkHttpClient okHttpClient = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();

            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Log.i("HttpClientRespons", e.getMessage());
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    // Log.i("HttpClientResponse",response.body().string());
                    res = response.body().string();
                    Log.i("HttpClientResponse", res);
                    String[] types = res.substring(1, res.length() - 1).split(",");
                    for (int i = 0; i < types.length; i++) {
                        types[i] = types[i].substring(1, types[i].length() - 1);
                    }
                    for (int i = 0; i < types.length; i++) {

                        if (types[i].equals(email)) {
                            Log.i("checkedcSuccesful", types[i]);
                            valid = true;

                            Log.i("checkedcSuccesful", valid+"");


                        }
                    }

                    if(valid){mailOkSender(email);
                    }else {
                        runOnUiThread(new Runnable() {

                            public void run() {

                                Toast.makeText(ForgotPasswordActivity.this, "Email doesn't match!", Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                }
            });

        }

        @Override
        protected Void doInBackground(Void... voids) {
            mailOkCheck(email);
            return null;
        }
    }


}