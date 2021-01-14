package com.example.phontacts;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddContactActivity extends AppCompatActivity {
    EditText name, number;
    Button addBtn;
    User user;
    String contactName, contactNumber;
    AsyncTask<Void, Void, Void> addNewContactAsyncTask;
    ArrayList<ContactItem> contactItemArrayList;
    LoadingAnimation loadingAnimation = new LoadingAnimation(AddContactActivity.this);

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        name = findViewById(R.id.newContanctName);
        number = findViewById(R.id.newContanctNumber);
        addBtn = findViewById(R.id.addNewContact);
        Bundle bundle = getIntent().getBundleExtra("BUNDLE");

        String username = bundle.getString("username");
        user = new User(username, "", "");

        contactItemArrayList = bundle.getParcelableArrayList("list");
        if (contactItemArrayList == null) Log.v("Add contcat pref ", "null");

    }

    public void addNewContact(View view) {

        contactName = name.getText().toString();
        contactNumber = number.getText().toString();
        name.setEnabled(false);
        number.setEnabled(false);
        addBtn.setEnabled(false);
        if (contactName.isEmpty()) {
            name.setError("This field cannot be empty!");
            name.setEnabled(true);
            number.setEnabled(true);
            addBtn.setEnabled(true);
            return;
        }
        if (contactNumber.isEmpty()) {
            number.setError("This field cannot be empty!");
            name.setEnabled(true);
            number.setEnabled(true);
            addBtn.setEnabled(true);
            return;
        }
        for (int i = 0; i < contactItemArrayList.size(); i++) {
            if (contactItemArrayList.get(i).getcName().equals(contactName)) {
                name.setError("Same name already exists!");
                name.setEnabled(true);
                number.setEnabled(true);
                addBtn.setEnabled(true);
                return;
            }
        }
        loadingAnimation.startAnimation();

        addNewContactAsyncTask = new AddNewContact();
        addNewContactAsyncTask.execute();


    }

    class AddNewContact extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            String contactsNameUrl = "https://phontacts-spring.herokuapp.com/contacts/addcontact";

            final OkHttpClient okHttpClient = new OkHttpClient();


            MediaType JSONType = MediaType.parse("application/json;charset=utf-8");
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("username", user.getUsername());
                jsonObject.put("cName", contactName);
                jsonObject.put("cNum", contactNumber);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            RequestBody requestBody = RequestBody.create(jsonObject.toString(), JSONType);


            try {
                Request request = new Request.Builder().url(contactsNameUrl).post(requestBody).build();
                Response response = okHttpClient.newCall(request).execute();


            } catch (IOException e) {
                Log.v("Add contact repsonse", Objects.requireNonNull(e.getCause()).toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            loadingAnimation.endAnimation();
            AddContactActivity.this.finish();
            Toast.makeText(getApplicationContext(), "Contact added successful!", Toast.LENGTH_LONG).show();
        }
    }
}