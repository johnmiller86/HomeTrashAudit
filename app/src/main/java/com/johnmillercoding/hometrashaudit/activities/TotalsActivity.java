/**
 * Activity for displaying totals with graphs.
 * @author John D. Miller
 * @version 1.0.1
 * @since 12/06/2015
 */

package com.johnmillercoding.hometrashaudit.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.johnmillercoding.hometrashaudit.R;
import com.johnmillercoding.hometrashaudit.services.Config;
import com.johnmillercoding.hometrashaudit.services.SessionManager;
import com.johnmillercoding.hometrashaudit.services.VolleyController;
import com.johnmillercoding.hometrashaudit.waste.Bio;
import com.johnmillercoding.hometrashaudit.waste.Glass;
import com.johnmillercoding.hometrashaudit.waste.Metal;
import com.johnmillercoding.hometrashaudit.waste.Paper;
import com.johnmillercoding.hometrashaudit.waste.Plastic;
import com.johnmillercoding.hometrashaudit.waste.Waste;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TotalsActivity extends AppCompatActivity
{
    // Variable Declarations
    private final ArrayList<Waste> wasteList = new ArrayList<>();
    private ArrayList<Plastic> plasRec = new ArrayList<>();
    private ArrayList<Plastic> plasNonRec = new ArrayList<>();
    private ArrayList<Plastic> plasFilm = new ArrayList<>();
    private ArrayList<Paper> papRec = new ArrayList<>();
    private ArrayList<Paper> papNonRec = new ArrayList<>();
    private ArrayList<Paper> papComp = new ArrayList<>();
    private ArrayList<Metal> metRec = new ArrayList<>();
    private ArrayList<Metal> metNonRec = new ArrayList<>();
    private ArrayList<Glass> glassRec = new ArrayList<>();
    private ArrayList<Glass> glassNonRec = new ArrayList<>();
    private ArrayList<Bio> bioComp = new ArrayList<>();
    private ArrayList<Bio> bioNonComp = new ArrayList<>();
    private final ArrayList<Waste> totalsList = new ArrayList<>();
    private final ArrayList<Entry> amounts = new ArrayList<>();
    private final ArrayList<String> combinations = new ArrayList<>();
    private PieChart chart;
    private String units;

    // Session Manager
    private SessionManager session;
    private static final String TAG = TotalsActivity.class.getSimpleName();
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

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Instantiating Chart
        chart = new PieChart(this);

        // Getting units
        session = new SessionManager(getApplicationContext());
        units = session.getUnit();

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        fetchData();
    }

    private void addChart() {
        setContentView(chart);

        // Configure chart
        chart.setUsePercentValues(true);
        chart.setDescription(null);
        chart.setBackgroundResource(R.drawable.broken_bark);

        // Enable Hole and Configuration
        chart.setDrawHoleEnabled(true);
        chart.setHoleColorTransparent(true);
        chart.setHoleRadius(7);
        chart.setTransparentCircleRadius(10);

        // Enable rotation by touch
        chart.setRotationAngle(0);
        chart.setRotationEnabled(true);

        // Set a chart value selected listener
        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {

            @Override
            public void onValueSelected(Entry entry, int i, Highlight highlight) {
                // Display message
                if (entry == null) {
                    return;
                }
                if (units.contains("Box"))
                {
                    Toast.makeText(TotalsActivity.this, combinations.get(entry.getXIndex()) + " = " + entry.getVal() + " " + units + "es", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(TotalsActivity.this, combinations.get(entry.getXIndex()) + " = " + entry.getVal() + " " + units +"s", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected() {

            }
        });

        // Adding data
        addData();

        // Customize legend
        Legend l = chart.getLegend();
        l.setPosition(Legend.LegendPosition.ABOVE_CHART_LEFT);
        l.setTextColor(Color.WHITE);
        l.setTextSize(11);
        l.setXEntrySpace(7);
        l.setYEntrySpace(5);
        l.setWordWrapEnabled(true);
    }

    private void addData()
    {
        // Splitting to load into chart
        for (Waste w : totalsList)
        {
            amounts.add(new Entry(w.getAmount(), totalsList.indexOf(w)));
        }

        for (Waste w : totalsList)
        {
            combinations.add(w.getWasteMaterial() + "/" + w.getWasteCategory());
        }

        // Creating the dataset
        PieDataSet dataSet = new PieDataSet(amounts, null);
        dataSet.setSliceSpace(3);
        dataSet.setSelectionShift(5);

        // adding colors
        ArrayList<Integer> colors = new ArrayList<>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
        {
            colors.add(c);
        }

        for (int c : ColorTemplate.JOYFUL_COLORS)
        {
            colors.add(c);
        }

        for (int c: ColorTemplate.COLORFUL_COLORS)
        {
            colors.add(c);
        }

        for (int c: ColorTemplate.LIBERTY_COLORS)
        {
            colors.add(c);
        }

        for (int c: ColorTemplate.PASTEL_COLORS)
        {
            colors.add(c);
        }

        colors.add(ColorTemplate.getHoloBlue());
        dataSet.setColors(colors);

        // Instantiating
        PieData data = new PieData(combinations, dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.BLACK);

        chart.setData(data);

        // Undo highlights
        chart.highlightValues(null);

        // Updating
        chart.invalidate();
    }

    /**
     * Process arrayLists to get totals.
     */
    private void processTotals()
    {
        // Splitting
        splitList();

        // Variables
        float total = 0;
        String material = "", category = "", date = "";

        // Processing totals avoiding duplicate entries
        if (plasRec.size() != 0)
        {
            for (Plastic p : plasRec)
            {
                material = p.getWasteMaterial();
                category = p.getWasteCategory();
                date = p.getDate();
                total += p.getAmount();
            }
            totalsList.add(new Waste(date, material, category, total));
        }

        if (plasNonRec.size() != 0)
        {
            total = 0;
            for (Plastic p : plasNonRec)
            {
                material = p.getWasteMaterial();
                category = p.getWasteCategory();
                date = p.getDate();
                total += p.getAmount();
            }
            totalsList.add(new Waste(date, material, category, total));
        }

        if (plasFilm.size() != 0)
        {
            total = 0;
            for (Plastic p : plasFilm)
            {
                material = p.getWasteMaterial();
                category = p.getWasteCategory();
                date = p.getDate();
                total += p.getAmount();
            }
            totalsList.add(new Waste(date, material, category, total));
        }

        if (papRec.size() != 0)
        {
            total = 0;
            for (Paper p : papRec) {
                material = p.getWasteMaterial();
                category = p.getWasteCategory();
                date = p.getDate();
                total += p.getAmount();
            }
            totalsList.add(new Waste(date, material, category, total));
        }

        total = 0;
        if (papNonRec.size() != 0)
        {
            for (Paper p : papNonRec)
            {
                material = p.getWasteMaterial();
                category = p.getWasteCategory();
                date = p.getDate();
                total += p.getAmount();
            }
            totalsList.add(new Waste(date, material, category, total));
        }

        if (papComp.size() != 0)
        {
            total = 0;
            for (Paper p : papComp)
            {
                material = p.getWasteMaterial();
                category = p.getWasteCategory();
                date = p.getDate();
                total += p.getAmount();
            }
            totalsList.add(new Waste(date, material, category, total));
        }

        if (metRec.size() != 0)
        {
            total = 0;
            for (Metal m : metRec)
            {
                material = m.getWasteMaterial();
                category = m.getWasteCategory();
                date = m.getDate();
                total += m.getAmount();
            }
            totalsList.add(new Waste(date, material, category, total));
        }

        if (metNonRec.size() != 0)
        {
            total = 0;
            for (Metal m : metNonRec)
            {
                material = m.getWasteMaterial();
                category = m.getWasteCategory();
                date = m.getDate();
                total += m.getAmount();
            }
            totalsList.add(new Waste(date, material, category, total));
        }

        if (glassRec.size() != 0)
        {
            total = 0;
            for (Glass g : glassRec)
            {
                material = g.getWasteMaterial();
                category = g.getWasteCategory();
                date = g.getDate();
                total += g.getAmount();
            }
            totalsList.add(new Waste(date, material, category, total));
        }

        if (glassNonRec.size() != 0)
        {
            total = 0;
            for (Glass g : glassNonRec)
            {
                material = g.getWasteMaterial();
                category = g.getWasteCategory();
                date = g.getDate();
                total += g.getAmount();
            }
            totalsList.add(new Waste(date, material, category, total));
        }

        if (bioComp.size() != 0)
        {
            total = 0;
            for (Bio b : bioComp)
            {
                material = b.getWasteMaterial();
                category = b.getWasteCategory();
                date = b.getDate();
                total += b.getAmount();
            }
            totalsList.add(new Waste(date, material, category, total));
        }

        if (bioNonComp.size() != 0)
        {
            total = 0;
            for (Bio b : bioNonComp)
            {
                material = b.getWasteMaterial();
                category = b.getWasteCategory();
                date = b.getDate();
                total += b.getAmount();
            }
            totalsList.add(new Waste(date, material, category, total));
        }
        // Add chart
        addChart();
    }

    /**
     * Splits the wasteList to separate arrayLists.
     */
    private void splitList()
    {
        // ArrayLists for processing totals.
        plasRec = new ArrayList<>();
        plasNonRec = new ArrayList<>();
        plasFilm = new ArrayList<>();
        papRec = new ArrayList<>();
        papNonRec = new ArrayList<>();
        papComp = new ArrayList<>();
        metRec = new ArrayList<>();
        metNonRec = new ArrayList<>();
        glassRec = new ArrayList<>();
        glassNonRec = new ArrayList<>();
        bioComp = new ArrayList<>();
        bioNonComp = new ArrayList<>();

        // Separating Combinations for totaling.
        for (Waste w : wasteList)
        {
            switch (w.getWasteMaterial())
            {
                case "Plastic":
                    switch (w.getWasteCategory())
                    {

                        case "Recyclable":
                            plasRec.add(new Plastic(w));
                            break;
                        case "Non-Recyclable":
                            plasNonRec.add(new Plastic(w));
                            break;
                        case "Film":
                            plasFilm.add(new Plastic(w));
                            break;
                    }
                    break;
                case "Paper":
                    switch (w.getWasteCategory())
                    {
                        case "Recyclable":
                            papRec.add(new Paper(w));
                            break;
                        case "Non-Recyclable":
                            papNonRec.add(new Paper(w));
                            break;
                        case "Compostable":
                            papComp.add(new Paper(w));
                            break;
                    }
                    break;
                case "Metal":
                    switch (w.getWasteCategory())
                    {
                        case "Recyclable":
                            metRec.add(new Metal(w));
                            break;
                        case "Non-Recyclable":
                            metNonRec.add(new Metal(w));
                            break;
                    }
                    break;
                case "Glass":
                    switch (w.getWasteCategory())
                    {
                        case "Recyclable":
                            glassRec.add(new Glass(w));
                            break;
                        case "Non-Recyclable":
                            glassNonRec.add(new Glass(w));
                            break;
                    }
                    break;
                default:
                    switch (w.getWasteCategory())
                    {
                        case "Compostable":
                            bioComp.add(new Bio(w));
                            break;
                        case "Non-Compostable":
                            bioNonComp.add(new Bio(w));
                            break;
                    }
                    break;
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
                Log.d(TAG, "Fetch Response: " + response);
                hideDialog();

                try {

                    // Retrieve JSON error object
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");

                    // Retrieve JSON response array
                    JSONArray jsonArray = jsonObject.getJSONArray("response");

                    // Check for error node in json
                    if (!error) {
                        for (int i = 0; i < jsonArray.length(); i++) {

                            // Retrieve individual JSON row objects
                            JSONObject row = jsonArray.getJSONObject(i);

                            // Storing columns
                            String date = row.getString("date");
                            String material = row.getString("material");
                            String category = row.getString("category");
                            double amount = row.getDouble("amount");

                            // Configure waste object and add to List
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
                        Toast.makeText(TotalsActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
                // List filled process totals
                processTotals();
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
}