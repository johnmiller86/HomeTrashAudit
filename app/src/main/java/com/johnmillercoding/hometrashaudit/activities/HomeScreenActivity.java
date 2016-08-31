/**
 * Activity for home screen.
 * @author John D. Miller
 * @version 1.0.2
 * @since 12/06/2015
 */
package com.johnmillercoding.hometrashaudit.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.johnmillercoding.hometrashaudit.R;
import com.johnmillercoding.hometrashaudit.services.Config;
import com.johnmillercoding.hometrashaudit.services.SessionManager;
import com.johnmillercoding.hometrashaudit.services.VolleyController;
import com.johnmillercoding.hometrashaudit.waste.Waste;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class HomeScreenActivity extends AppCompatActivity
{
    private static final String TAG = LoginActivity.class.getSimpleName();

    // Session Manager
    private SessionManager session;
    private ProgressDialog pDialog;
    private final ArrayList<Waste> wasteList = new ArrayList<>();
    private TextView textView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        // Instantiating Session
        session = new SessionManager(getApplicationContext());

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Greeting user
        textView = (TextView) findViewById(R.id.greetingTextView);
        textView.setText("Hi " + session.getUsername() + "!");
        // Attempting to fetch data
        fetchData();
    }

    /**
     * Button handler.
     */
    private void createButtons() {
        Button newAuditButton = (Button)findViewById(R.id.newAuditButton);
        assert newAuditButton != null;
        newAuditButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), AuditActivity.class);
                startActivity(i);
            }
        });

        Button viewJournalButton = (Button) findViewById(R.id.viewJournalButton);
        assert viewJournalButton != null;
        viewJournalButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), JournalActivity.class);
                if(wasteList.size() == 0){
                    Toast.makeText(HomeScreenActivity.this, "You haven't created a journal!", Toast.LENGTH_SHORT).show();
                }else{
                    startActivity(i);
                }
            }
        });

        Button viewTotalsButton = (Button)findViewById(R.id.viewTotalsButton);
        assert viewTotalsButton != null;
        viewTotalsButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), TotalsActivity.class);

                if(wasteList.size() == 0){
                    Toast.makeText(HomeScreenActivity.this, "Create a journal before viewing totals!", Toast.LENGTH_SHORT).show();
                }else{
                    startActivity(i);
                }
            }
        });
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
                // List filled, activate buttons.
                createButtons();
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

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
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
}
