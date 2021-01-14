package com.example.phontacts;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.SSLContext;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    ImageView blueLineUsername, blueLinePassword, signinbtn, loginSelectionLine, registerSelectionLine,
            registerUsernameBlueLine, registerEmailBlueLine, registerPasswordBlueLine, registerRetypePasswordBlueLine, signupBtn;
    TextView login, register, forgotPassword;
    EditText lEmail, lPassword, rUsername, rEmail, rPassword, rRetypePassword;
    AsyncTask<Void, Void, Void> asyncTask, newUserAsyncTask, loginSuccesfullAsyncTask;
    AsyncTask<Void, Void, String> loginAuthAsyncTask;

    Animation fadeInAnime, fadeOutAnime, slideRight, slideLeft;
    Boolean valid = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        asyncTask = new Intiate();
        asyncTask.execute();
        // loginClick();
        // login.setEnabled(false);


    }

    public void registerClick() {

/*registerRetypePasswordBlueLine.startAnimation(fadeInAnime);
registerPasswordBlueLine.startAnimation(fadeInAnime);
registerEmailBlueLine.startAnimation(fadeInAnime);
registerUsernameBlueLine.startAnimation(fadeInAnime);
blueLinePassword.startAnimation(fadeOutAnime);
blueLineUsername.startAnimation(fadeOutAnime);


 */

        blueLineUsername.setVisibility(View.INVISIBLE);
        blueLinePassword.setVisibility(View.INVISIBLE);
        registerSelectionLine.setVisibility(View.VISIBLE);
        loginSelectionLine.setVisibility(View.INVISIBLE);
        registerEmailBlueLine.setVisibility(View.VISIBLE);
        registerUsernameBlueLine.setVisibility(View.VISIBLE);
        registerPasswordBlueLine.setVisibility(View.VISIBLE);
        registerRetypePasswordBlueLine.setVisibility(View.VISIBLE);
        lEmail.setVisibility(View.INVISIBLE);

        lPassword.setVisibility(View.INVISIBLE);

        rUsername.setVisibility(View.VISIBLE);
        rEmail.setVisibility(View.VISIBLE);
        rPassword.setVisibility(View.VISIBLE);
        rRetypePassword.setVisibility(View.VISIBLE);
        signinbtn.setVisibility(View.INVISIBLE);
        signupBtn.setVisibility(View.VISIBLE);
        forgotPassword.setVisibility(View.INVISIBLE);
        login.setEnabled(true);
    }

    public void loginClick() {
      /*  registerRetypePasswordBlueLine.startAnimation(fadeOutAnime);
        registerPasswordBlueLine.startAnimation(fadeOutAnime);
        registerEmailBlueLine.startAnimation(fadeOutAnime);
        registerUsernameBlueLine.startAnimation(fadeOutAnime);
        blueLinePassword.startAnimation(fadeInAnime);
        blueLineUsername.startAnimation(fadeInAnime);

       */
        registerSelectionLine.setVisibility(View.INVISIBLE);
        loginSelectionLine.setVisibility(View.VISIBLE);
        registerEmailBlueLine.setVisibility(View.INVISIBLE);
        registerUsernameBlueLine.setVisibility(View.INVISIBLE);
        registerPasswordBlueLine.setVisibility(View.INVISIBLE);
        registerRetypePasswordBlueLine.setVisibility(View.INVISIBLE);
        lEmail.setVisibility(View.VISIBLE);
        lPassword.setVisibility(View.VISIBLE);
        rUsername.setVisibility(View.INVISIBLE);
        rEmail.setVisibility(View.INVISIBLE);
        rPassword.setVisibility(View.INVISIBLE);
        rRetypePassword.setVisibility(View.INVISIBLE);
        signinbtn.setVisibility(View.VISIBLE);
        signupBtn.setVisibility(View.INVISIBLE);
        blueLineUsername.setVisibility(View.VISIBLE);
        blueLinePassword.setVisibility(View.VISIBLE);
        forgotPassword.setVisibility(View.VISIBLE);
        register.setEnabled(true);
    }

    public void mailCheck(View view) {
        Intent intent = new Intent(this, ForgotPasswordActivity.class);
        startActivity(intent);
    }

    public void signupBtn(View view) {
        String username = rUsername.getText().toString();

        String nPass = rPassword.getText().toString();
        String rePass = rRetypePassword.getText().toString();
        if (nPass.equals(rePass)) {
            String password = rRetypePassword.getText().toString();
            String email = rEmail.getText().toString();

            newUserAsyncTask = new RegsiterUser(username, password, email);
            newUserAsyncTask.execute();

        } else {
            rRetypePassword.setError("Password doesn't match!");
            return;

        }
    }

    public void signInBtn(View view) {
        String email = lEmail.getText().toString();
        String password = lPassword.getText().toString();

        User user = new User("", password, email);
        loginAuthAsyncTask = new LoginAuth(user);

        try {
            user.setUsername(loginAuthAsyncTask.execute().get());
            Log.v("username retrived ",user.getUsername() );

        } catch (ExecutionException e) {
            e.printStackTrace();

            // Log.v("Reason ",e.getMessage());

        } catch (InterruptedException e) {
            e.printStackTrace();
            //Log.v("Reason ",e.getMessage());

        }

        if (user.getUsername().equals("oi")) {

            Toast.makeText(MainActivity.this, "Invalid login credentials!", Toast.LENGTH_SHORT).show();

        } else {

loginSuccesfullAsyncTask=new loginSuccesfull(user);
loginSuccesfullAsyncTask.execute();

        }
    }



    class Intiate extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            fadeInAnime = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_in);
            fadeOutAnime = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_out);
            slideLeft = AnimationUtils.loadAnimation(MainActivity.this, R.anim.slide_left);
            slideRight = AnimationUtils.loadAnimation(MainActivity.this, R.anim.slide_right);
            blueLineUsername = findViewById(R.id.blueLineUsername);
            blueLinePassword = findViewById(R.id.blueLinePassword);
            signinbtn = findViewById(R.id.signinBtn);
            signupBtn = findViewById(R.id.signupBtn);
            loginSelectionLine = findViewById(R.id.login_selection_line);
            registerSelectionLine = findViewById(R.id.register_selection_line2);
            registerUsernameBlueLine = findViewById(R.id.registerUsernameBlueLine);
            registerEmailBlueLine = findViewById(R.id.registerEmailBlueLine);
            registerPasswordBlueLine = findViewById(R.id.registerPasswordBlueLine);
            registerRetypePasswordBlueLine = findViewById(R.id.registerReTypeBlueLine);
            forgotPassword = findViewById(R.id.forgotPasswordTextView);
            login = findViewById(R.id.loginTxtView);
            register = findViewById(R.id.registerTxtView);
            lEmail = findViewById(R.id.loginEmail);
            lPassword = findViewById(R.id.loginPassword);
            rUsername = findViewById(R.id.registerUserName);
            rEmail = findViewById(R.id.registerEmail);
            rPassword = findViewById(R.id.registerPassword);
            rRetypePassword = findViewById(R.id.registerRetypePassword);

            loginClick();
            login.setEnabled(false);
            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //  loginSelectionLine.startAnimation(slideLeft);
                    loginClick();
                }
            });
            register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //loginSelectionLine.startAnimation(slideRight);
                    registerClick();
                }
            });
            return null;
        }
    }

    class loginSuccesfull extends AsyncTask<Void, Void, Void> {
        User user;

        public loginSuccesfull(User user) {
            this.user = user;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(MainActivity.this, "Welcome back!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, WelcomeScreen.class);


            intent.putExtra("username", user.getUsername());

            Log.v("Welcome Screen Text", user.getUsername());
            SharedPreferences sharedPreferences = getSharedPreferences("UserAuthSharedPref", MODE_PRIVATE);
            SharedPreferences.Editor myEdit
                    = sharedPreferences.edit();
            myEdit.putString("username",
                    user.getUsername());
            myEdit.commit();
            MainActivity.this.finish();
            startActivity(intent);

        }
    }

    class RegsiterUser extends AsyncTask<Void, Void, Void> {
        String username, password, email;
        User user;

        public RegsiterUser(String username, String password, String email) {
            this.username = username;
            this.password = password;
            this.email = email;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            user = new User(username, password, email);
            addUser(user);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(getApplicationContext(), "Registration succesfull", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, WelcomeScreen.class);
            intent.putExtra("username", user.getUsername());

            Log.v("Welcome Screen Text", username);
            SharedPreferences sharedPreferences = getSharedPreferences("UserAuthSharedPref", MODE_PRIVATE);
            SharedPreferences.Editor myEdit
                    = sharedPreferences.edit();
            myEdit.putString("username",
                    username);
            myEdit.commit();
            MainActivity.this.finish();
            startActivity(intent);


        }

        public void addUser(final User user) {
            Log.v("call", "addUser method Called!");
            String url = "https://" + R.string.domain + "/users/add";
            OkHttpClient okHttpClient = new OkHttpClient();


            MediaType JSONType = MediaType.parse("application/json;charset=utf-8");
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("username", user.getUsername());
                jsonObject.put("password", user.getPassword());
                jsonObject.put("email", user.getEmail());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            RequestBody requestBody = RequestBody.create(JSONType, jsonObject.toString());
            Log.v("NewUserRequestBody", requestBody.toString());
            try {
                Request request = new Request.Builder().url(url).post(requestBody).build();
                Response response = okHttpClient.newCall(request).execute();
                Log.v("NewUserResponse", response.body().string());


            } catch (IOException e) {
                Log.v("newUserResponse", e.getMessage());
            }


        }
    }

    class LoginAuth extends AsyncTask<Void, Void, String> {
        User user;


        public LoginAuth(User user) {
            this.user = user;
        }

        @Override
        protected String doInBackground(Void... voids) {

            return loginAuthCheck(user);
        }

        public String loginAuthCheck(final User user) {
            String url = "https://phontacts-spring.herokuapp.com/users/passwordcheck";


            OkHttpClient okHttpClient = new OkHttpClient();

            MediaType JSONType = MediaType.parse("application/json;charset=utf-8");
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("username", user.getUsername());
                jsonObject.put("password", user.getPassword());
                jsonObject.put("email", user.getEmail());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            RequestBody requestBody = RequestBody.create(jsonObject.toString(),JSONType );
            Log.v("paswrdChckRequestBody", requestBody.toString());


            try {
                Request request = new Request.Builder().url(url).post(requestBody).build();
                Response response = okHttpClient.newCall(request).execute();
                String resp = response.body().string();
                Log.v("username resp", resp);
                if (resp.equals("oi")) return "oi";
                else return resp;


            } catch (IOException e) {
                Log.v("passwordCheckResponse", e.getMessage());
            }
            return "oi";

        }


    }

}