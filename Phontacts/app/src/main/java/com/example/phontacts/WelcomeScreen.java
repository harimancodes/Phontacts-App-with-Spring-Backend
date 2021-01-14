package com.example.phontacts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WelcomeScreen extends AppCompatActivity {
    TextView welcomeText;
    User user;
    Bundle bundle;
    String username;
    SharedPreferences sharedPreferences;
    AsyncTask<Void, Void, Map<String, String>> contactsAsyncTask;
    AsyncTask<Void,Void,Void> startupAsyncTask,deleteContactAsyncTask;
    RecyclerView contactsRecyclerView;
    ContactsAdapter contactsAdapter;
    RecyclerView.LayoutManager contactsLayoutManager;
    FloatingActionButton floatingActionButton;
    ArrayList<ContactItem> contactItemArrayList;
    Map<String, String> contactsDetailsMap;


    public WelcomeScreen() {

    }




    public WelcomeScreen(User user) {
        this.user = user;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
setTheme(R.style.Theme_AppCompat_Light_DarkActionBar);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_welcome_scren);

        startupAsyncTask=new Startup();
        startupAsyncTask.execute();


        //bundle=getIntent().getExtras();
        //username=bundle.getString("username");

    }

    @Override
    protected void onResume() {
        super.onResume();

        sharedPreferences = getSharedPreferences("UserAuthSharedPref", MODE_PRIVATE);
        username = sharedPreferences.getString("username", "");
        if (username.isEmpty()) {
            Intent intent = new Intent(this, MainActivity.class);
            this.finish();
            startActivity(intent);

        } else {
            // welcomeText.setText("Welcome " + username + "!");



            contactsDetailsMap = new HashMap<String, String>();
            contactItemArrayList=PrefContactList.readListFromPref(WelcomeScreen.this);
            if(contactItemArrayList==null)contactItemArrayList = new ArrayList<>();

            contactsLayoutManager = new LinearLayoutManager(this);
            // contactItemArrayList.add(new ContactItem(R.drawable.contact_icon_foreground,"Enthanda","mwone"));
            contactsAsyncTask = new ContactsAysncTask(username);
            //After retrieving
            contactsRecyclerView.setHasFixedSize(true);


            try {
                contactsDetailsMap = contactsAsyncTask.execute().get();

                for (Map.Entry m : contactsDetailsMap.entrySet()) {
                    contactItemArrayList.add(new ContactItem(R.drawable.contact_icon_foreground, m.getKey().toString(), m.getValue().toString()));
                    Log.v("contactsItemArrayList ", m.getKey().toString() + ":" + m.getValue().toString());
                }

                Collections.sort(contactItemArrayList, (p1, p2) -> p1.getcName().toLowerCase().compareTo(p2.getcName().toLowerCase()));

                PrefContactList.writeListInPref(WelcomeScreen.this,contactItemArrayList);

                contactsAdapter = new ContactsAdapter(contactItemArrayList);
                contactsRecyclerView.setLayoutManager(contactsLayoutManager);
                contactsRecyclerView.setAdapter(contactsAdapter);

                Log.v("Contact List", "Succesfull");

            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.search_menu,menu);
        MenuItem searchItem=menu.findItem(R.id.search_action);
        SearchView searchView=(SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                contactsAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }
    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences sharedPreferences = getSharedPreferences("UserAuthSharedPref", MODE_PRIVATE);
        SharedPreferences.Editor myEdit
                = sharedPreferences.edit();
        myEdit.putString("username",
                username);
        myEdit.commit();
    }

    public void newContact(View view) {
        Intent intent=new Intent(WelcomeScreen.this,AddContactActivity.class);
        Bundle args=new Bundle();
        args.putParcelableArrayList("list", (ArrayList<ContactItem>) contactItemArrayList);
        args.putString("username",username);
       intent.putExtra("BUNDLE",args);
       startActivity(intent);
    }

    class Startup extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            floatingActionButton = findViewById(R.id.floatingActionButton);
            contactsRecyclerView = findViewById(R.id.contatcsRecyclerView);

            ItemTouchHelper itemTouchHelper=new ItemTouchHelper(simpleCallback);
            itemTouchHelper.attachToRecyclerView(contactsRecyclerView);


        }

        @Override
        protected Void doInBackground(Void... voids) {


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }
    }

    class ContactsAysncTask extends AsyncTask<Void, Void, Map<String, String>> {
        String username, nameResp, numberResp;
        Map<String, String> contactsMap = new HashMap<String, String>();

        public ContactsAysncTask(String username) {
            this.username = username;
        }

        public String getUsername() {
            return username;
        }


        @Override
        protected Map<String, String> doInBackground(Void... voids) {
            String contactsNameUrl = "https://phontacts-spring.herokuapp.com/contacts/getcontactnames";
            String contactsNumberUrl = "https://phontacts-spring.herokuapp.com/contacts/getcontactnumbers";
            final OkHttpClient okHttpClient = new OkHttpClient();


            MediaType JSONType = MediaType.parse("application/json;charset=utf-8");
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("username", getUsername());
                jsonObject.put("cName", "");
                jsonObject.put("cNum", "");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            RequestBody requestBody = RequestBody.create(jsonObject.toString(), JSONType);


            try {
                Request request = new Request.Builder().url(contactsNameUrl).post(requestBody).build();
                Response response = okHttpClient.newCall(request).execute();
                nameResp = Objects.requireNonNull(response.body()).string();
                Log.v("name response body", nameResp);


            } catch (IOException e) {
                Log.v("contacts names Response", Objects.requireNonNull(e.getCause()).toString());
            }

            try {
                Request request = new Request.Builder().url(contactsNumberUrl).post(requestBody).build();
                Response response = okHttpClient.newCall(request).execute();
                numberResp = Objects.requireNonNull(response.body()).string();
                Log.v("number response body", numberResp);
            } catch (IOException e) {
                Log.v("number Response", Objects.requireNonNull(e.getCause()).toString());
            }
            String[] nameTypes = nameResp.substring(1, nameResp.length() - 1).split(",");
            String[] numberTypes = numberResp.substring(1, numberResp.length() - 1).split(",");
            for (int i = 0; i < nameTypes.length; i++) {
                nameTypes[i] = nameTypes[i].substring(1, nameTypes[i].length() - 1);
                numberTypes[i] = numberTypes[i].substring(1, numberTypes[i].length() - 1);
                contactsMap.put(nameTypes[i], numberTypes[i]);

            }
            return contactsMap;
        }

        @Override
        protected void onPostExecute(Map<String, String> stringStringMap) {
            super.onPostExecute(stringStringMap);

        }
    }

            ItemTouchHelper.SimpleCallback simpleCallback=new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT) {
                @Override
                public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                    return false;
                }
                String conName="";
                String conNum="";
                int pos;
                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                    pos=viewHolder.getAdapterPosition();
                    conName=contactItemArrayList.get(pos).getcName();
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    Log.v("Dialog ","Yes button clicked");
                                    deleteContactAsyncTask=new DeleteContact(conName,conNum);
                                    deleteContactAsyncTask.execute();
                                    contactsAdapter.remove(pos);
                                    contactsAdapter.notifyDataSetChanged();
                                    Toast.makeText(WelcomeScreen.this,"\'"+conName+"\""+" was deleted successfully",Toast.LENGTH_SHORT).show();
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    Log.v("Dialog ","No button clicked");
                                    contactsAdapter.notifyDataSetChanged();
                                    break;
                            }
                        }
                    };



                    AlertDialog.Builder builder = new AlertDialog.Builder(WelcomeScreen.this);
                    builder.setMessage("Are you sure you want to delete \""+conName+"\"?").setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();
                }
            };
class DeleteContact extends AsyncTask<Void,Void,Void>{
String cName;
String cNum;

    public DeleteContact(String cName, String CNum) {
        this.cName = cName;
        this.cNum = CNum;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        String contactsNameUrl = "https://phontacts-spring.herokuapp.com/contacts/deletecontact";

        final OkHttpClient okHttpClient = new OkHttpClient();


        MediaType JSONType = MediaType.parse("application/json;charset=utf-8");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", username);
            jsonObject.put("cName", cName);
            jsonObject.put("cNum", cNum);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), JSONType);


        try {
            Request request = new Request.Builder().url(contactsNameUrl).delete(requestBody).build();
            Response response = okHttpClient.newCall(request).execute();


        } catch (IOException e) {
            Log.v("Delete contact response", Objects.requireNonNull(e.getCause()).toString());
        }



        return null;
    }
}
}