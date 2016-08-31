///**
// * Activity to view journal.
// * @author John D. Miller
// * @version 1.0.1
// * @since 12/06/2015
// */
//package com.example.chefj.hometrashaudit;
//
//import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
//import android.util.Log;
//import android.widget.ArrayAdapter;
//import android.widget.ListView;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.util.ArrayList;
//import java.util.Scanner;
//
//public class JournalActivity extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_journal);
//
//        ArrayList<String> items = new ArrayList<>();
//        ArrayAdapter listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
//        ListView lv = (ListView) findViewById(R.id.journalListView);
//        lv.setAdapter(listAdapter);
//        try
//        {
//            // Reading Journal
//            BufferedReader reader = new BufferedReader(new InputStreamReader(openFileInput("Journal.txt")));
//            String line;
//            StringBuffer sB = new StringBuffer();
//            int i = 0;
//
//            while ((line = reader.readLine()) != null)
//            {
//                i++;
//                sB.append(line + "\n");
//                Log.i("line" + i, line);
//            }
//            items.add(sB.toString());
//        }
//
//        catch (IOException ex)
//        {
//            ex.printStackTrace();
//        }
//    }
//}

/**
 * Activity to view journal.
 * @author John D. Miller
 * @version 1.0.1
 * @since 12/06/2015
 */
package com.johnmillercoding.hometrashaudit.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.johnmillercoding.hometrashaudit.R;
import com.johnmillercoding.hometrashaudit.services.Config;
import com.johnmillercoding.hometrashaudit.services.CustomAdapter;
import com.johnmillercoding.hometrashaudit.services.SessionManager;
import com.johnmillercoding.hometrashaudit.services.VolleyController;
import com.johnmillercoding.hometrashaudit.waste.Waste;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class JournalActivity extends AppCompatActivity {

    // Variable Declarations
    private final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT);
    private final ArrayList<Waste> wasteList = new ArrayList<>();
    private CustomAdapter adapter;
    private ListView listView;


    // Session Manager
    private SessionManager session;
    private static final String TAG = LoginActivity.class.getSimpleName();
    private ProgressDialog pDialog;
    //Options Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    // Options Item Selection
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        int id = item.getItemId();
        if (id == R.id.menuitem_logout){
            logoutUser();
        }
        return super.onOptionsItemSelected(item);
    }

    // Context Menu
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.listview_context_menu, menu);
    }

    // Context Item Selection
    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId())
        {
            // Remove option
            case R.id.remove:

                // Ensuring adapter is referencing the listView
                adapter = new CustomAdapter(this);
                listView.setAdapter(adapter);

                // Removing item
                //items.remove(info.position);
                if (isDate(adapter.getItem(info.position))){
                    wasteList.remove(info.position);
                }
                else{
                    wasteList.remove(info.position);
                }
                adapter = new CustomAdapter(this);
                configureAdapter();
                return true;

            // Default
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal);

        // Instantiating Session
        session = new SessionManager(getApplicationContext());

        // Configuring ListView and Adapter
        listView = (ListView) findViewById(R.id.journalListView);
        adapter = new CustomAdapter(this);
        assert listView != null;
        listView.setAdapter(adapter);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Displaying
        fetchData();
    }

    /**
     * Displays the journal.
     */
    private void displayJournal() {
        configureAdapter();
        adapter.notifyDataSetChanged();
    }

    /**
     * Adds date headers and data to the adapter.
     */
    public void configureAdapter(){
        String date = wasteList.get(0).getDate();
        adapter.addSectionHeaderItem(date);
        adapter.addItem(wasteList.get(0).getWasteMaterial() + " " + wasteList.get(0).getWasteCategory()
        + " " + wasteList.get(0).getAmount());
        for (Waste waste : wasteList){
            if (waste.getDate().equals(date)){
                adapter.addItem(waste.getWasteMaterial() + " " + waste.getWasteCategory()
                        + " " + waste.getAmount());
            }else{
                date = waste.getDate();
                adapter.addSectionHeaderItem(waste.getDate());
                adapter.addItem(waste.getWasteMaterial() + " " + waste.getWasteCategory()
                        + " " + waste.getAmount());
            }
        }
    }

    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from SQLite users table
     * */
    private void logoutUser() {

        session.setLoggedIn(false);
        session.setUnit("");
        session.setUsername("");

        // Launching the login activity
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * function to fetch the waste list in mysql db
     * */
    private void fetchData() {

        // Tag used to cancel the request
        String tag_string_req = "req_waste_list";

        pDialog.setMessage("Fetching...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                Config.URL_FETCH, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //response = response.replace("\\", "");
                Log.d(TAG, "Fetch Response: " + response);
                hideDialog();

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");

                    // Retrieve JSON
                    JSONArray jsonArray = jsonObject.getJSONArray("response");
                    //Iterator<String> iterator = jsonObject.keys();

                    // Check for error node in json
                    if (!error) {
                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject row = jsonArray.getJSONObject(i);

                            // Data retrieved
                            String date = row.getString("date");
                            String material = row.getString("material");
                            String category = row.getString("category");
                            double amount = row.getDouble("amount");

                            // Add to List
                            Waste waste = new Waste();
                            waste.setDate(date);
                            waste.setWasteMaterial(material);
                            waste.setWasteCategory(category);
                            waste.setAmount((float)amount);
                            wasteList.add(waste);
                        }
                    } else {
                        // Error fetching data. Get the error message
                        String errorMsg = jsonObject.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
                // List filled process totals
                displayJournal();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                params.put("username", session.getUsername());
                return params;
            }
        };

        // Adding request to request queue
        VolleyController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    /**
     * Shows the progress dialog.
     */
    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    /**
     * Hides the progress dialog.
     */
    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    /**
     * Determines if a line is a date.
     * @param line the line to be evaluated.
     * @return true or false.
     */
    private Boolean isDate(String line){
        try{
            dateFormat.parse(line);
            return true;
        }catch (ParseException e){
            return false;
        }
    }
}