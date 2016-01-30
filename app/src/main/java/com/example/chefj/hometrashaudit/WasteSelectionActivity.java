/**
 * Activity for waste selection.
 * @author John D. Miller
 * @version 1.0.1
 * @since 12/06/2015
 */
package com.example.chefj.hometrashaudit;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

public class WasteSelectionActivity extends AppCompatActivity {

    private Spinner materialSpinner, categorySpinner;
    private ArrayAdapter<String> adapter;
    private ArrayAdapter<Waste> listAdapter;
    public static final NumberFormat percentFormatter = new DecimalFormat("#.##%");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waste_selection);

        // Creating and Applying ListView Adapter
        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, HomeTrashAuditActivity.items);
        HomeTrashAuditActivity.listView.setAdapter(listAdapter);

        // Adding Listener to the material Spinner
        materialSpinner = (Spinner) findViewById(R.id.materialSpinner);
        materialSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fillCategorySpinner();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do Nothing
            }
        });

        // Add to audit button listener
        Button addButton = (Button) findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                EditText amountField = (EditText) findViewById(R.id.amountField);

                // Empty Field
                if (amountField.getText().toString().equals("")) {
                    Toast.makeText(WasteSelectionActivity.this, "Amount cannot be left blank!!!", Toast.LENGTH_SHORT).show();
                }

                // Updating existing rows
                else if (rowExists(materialSpinner.getSelectedItem().toString(), categorySpinner.getSelectedItem().toString(), HomeTrashAuditActivity.items)) {
                    // Getting location of row
                    int location = combinationFoundAt(materialSpinner.getSelectedItem().toString(), categorySpinner.getSelectedItem().toString(), HomeTrashAuditActivity.items);

                    // Updating amount
                    Waste w = HomeTrashAuditActivity.items.get(location);
                    w.setAmount(w.getAmount() + Float.parseFloat((amountField.getText().toString())));
                    HomeTrashAuditActivity.items.set(location, w);
                    setPercentages(HomeTrashAuditActivity.items);
                    listAdapter.notifyDataSetChanged();
                    finish();
                }

                // Adding new rows
                else
                {
                    Waste w = new Waste();
                    w.setWasteMaterial(materialSpinner.getSelectedItem().toString());
                    w.setWasteCategory(categorySpinner.getSelectedItem().toString());
                    w.setAmount(Float.parseFloat(amountField.getText().toString()));
                    HomeTrashAuditActivity.items.add(w);
                    setPercentages(HomeTrashAuditActivity.items);
                    listAdapter.notifyDataSetChanged();
                    finish();
                }
            }
        });
    }

    /**
     * Fills the category spinner based on material spinner selection.
     */
    public void fillCategorySpinner() {
        String[] array;
        String item = (String) materialSpinner.getSelectedItem();
        categorySpinner = (Spinner) findViewById(R.id.categorySpinner);
        switch (item) {
            case "Plastic":
                array = getResources().getStringArray(R.array.plastic_categories);
                adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, array);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                categorySpinner.setAdapter(adapter);
                break;
            case "Paper":
                array = getResources().getStringArray(R.array.paper_categories);
                adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, array);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                categorySpinner.setAdapter(adapter);
                break;
            case "Metal":
                array = getResources().getStringArray(R.array.metal_categories);
                adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, array);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                categorySpinner.setAdapter(adapter);
                break;
            case "Bio":
                array = getResources().getStringArray(R.array.bio_categories);
                adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, array);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                categorySpinner.setAdapter(adapter);
                break;
            case "Glass":
                array = getResources().getStringArray(R.array.glass_categories);
                adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, array);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                categorySpinner.setAdapter(adapter);
        }
    }

    /**
     * Determines if a material/category combination exists in the items list.
     *
     * @param aMaterial the material.
     * @param aCategory the category.
     * @param list      the ArrayList of items.
     * @return true or false.
     */
    private Boolean rowExists(String aMaterial, String aCategory, ArrayList<Waste> list) {

        for (Waste w: list)
        {
            if (w.getWasteMaterial().equals(aMaterial) && w.getWasteCategory().equals(aCategory))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Finds the row where the combination is contained in the items list.
     *
     * @param aMaterial the material.
     * @param aCategory the category.
     * @param list      the ArrayList of items.
     * @return the rows index.
     */
    private int combinationFoundAt(String aMaterial, String aCategory, ArrayList<Waste> list) {

        for (int i = 0; i < list.size(); i++)
        {
            Waste w = list.get(i);
            if (w.getWasteMaterial().equals(aMaterial) && w.getWasteCategory().equals(aCategory))
            {
                return i;
            }
        }
        return 0;
    }

    /**
     * Calculates and sets percentages for the listView.
     * @param list the items list.
     */
    public static void setPercentages(ArrayList<Waste> list)
    {
        double total = 0;

        // Calculating total
        for (Waste w : list)
        {
            // Calculating total
            total += w.getAmount();
        }

        // Setting percentages
        for (Waste w : list)
        {
            w.setPercentage(percentFormatter.format(w.getAmount() / total));
        }
    }
}
