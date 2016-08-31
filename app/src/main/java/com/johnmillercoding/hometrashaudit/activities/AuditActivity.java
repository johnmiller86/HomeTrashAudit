/**
 * Activity to complete trash audits.
 * @author John D. Miller
 * @version 1.0.1
 * @since 12/06/2015
 */
package com.johnmillercoding.hometrashaudit.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.johnmillercoding.hometrashaudit.R;
import com.johnmillercoding.hometrashaudit.services.SessionManager;
import com.johnmillercoding.hometrashaudit.waste.Waste;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

public class AuditActivity extends AppCompatActivity {

    // Variable Declarations
    static ListView listView;
    private static EditText dateText;
    static ArrayList<Waste> items = new ArrayList<>();
    private ArrayAdapter<Waste> listAdapter;
    private ArrayList<Waste> wasteList = new ArrayList<>();
    private final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT);

    // Session Manager
    private SessionManager session;

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
                listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
                listView.setAdapter(listAdapter);

                // Removing item
                items.remove(info.position);
                WasteSelectionActivity.setPercentages(items);
                listAdapter.notifyDataSetChanged();
                return true;

            // Default
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audit);

        // Instantiating Session
        session = new SessionManager(getApplicationContext());

        // Context Configuration
        listView = (ListView)findViewById(R.id.listView);
        registerForContextMenu(listView);

        // Adapter Configuration
        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        listView.setAdapter(listAdapter);

        // Setting current date
        Date date = new Date();
        dateText = (EditText) findViewById(R.id.dateText);
        assert dateText != null;
        dateText.setKeyListener(null);
        dateText.setText(dateFormat.format(date));

        // Pick waste button listener
        listView = (ListView) findViewById(R.id.listView);
        Button pickWasteButton = (Button) findViewById(R.id.pickWasteButton);
        assert pickWasteButton != null;
        pickWasteButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), WasteSelectionActivity.class);
                startActivity(i);
            }
        });

        // Add to journal button listener
        Button addToJournalButton = (Button) findViewById(R.id.addToJournalButton);
        assert addToJournalButton != null;
        addToJournalButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v)
            {
                // Empty Journal
                if (items.size() == 0) {
                    Toast.makeText(AuditActivity.this, "You must first complete an audit before adding to Journal!!!", Toast.LENGTH_SHORT).show();
                }

                // Checking if audit exists for selected date
                else if (dateExists())
                {
                    Toast.makeText(AuditActivity.this, "An audit for this date has already been completed!!!", Toast.LENGTH_SHORT).show();
                }

                // Appending Journal
                else
                {
                    try
                    {
//                        FileOutputStream os = openFileOutput("Journal.txt", Context.MODE_APPEND);
//                        PrintWriter output = new PrintWriter(os);
                        File file = new File(getFilesDir(), "Journal.txt");
                        PrintWriter output = new PrintWriter(new FileOutputStream(file, true));

                        String format = "%-10s%-17s%-8s%-7s";

                        // Header
                        output.println(dateText.getText().toString() + "\n");
                        output.printf(format, "Material", "Category", "Amount", "Total");
                        output.println("---------------------------------------------");

                        // Body
                        for (Waste w : items)
                        {
                            output.printf(format, w.getWasteMaterial(), w.getWasteCategory(), w.getAmount(), w.getPercentage());
                            output.println();
                        }
                        output.println("---------------------------------------------");
                        output.println();

                        // Closing PrintWriter
                        output.flush();
                        output.close();

                        // Confirmation
                        Toast.makeText(AuditActivity.this, "The journal was successfully updated!", Toast.LENGTH_SHORT).show();

                        // Resetting for additional audits
                        items = new ArrayList<>();
                        listAdapter.notifyDataSetChanged();
                        finish();
                    }
                    catch (Exception ex)
                    {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * Determines an audit has been created for the current audit.
     * @return true or false.
     */
    private boolean dateExists()
    {
        readInWasteList();
        for (Waste wa : wasteList)
        {
            System.out.println(wa.toString());
        }
        //wasteList.sort(sorter);
        for (Waste w : wasteList)
        {
            if (w.getDate().equals(dateText.getText().toString()))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if a line is a date.
     * @param line the line to be evaluated.
     * @return true or false.
     */
    private Boolean isDate(String line)
    {
        try
        {
            dateFormat.parse(line);
            return true;
        }
        catch (ParseException e)
        {
            return false;
        }
    }

    /**
     * Reads into the wasteList from the file.
     */
    private void readInWasteList()
    {
        ArrayList<String> lines = new ArrayList<>();
        String date = "";
        wasteList = new ArrayList<>();

        // Reading in file
        try
        {
            File journalFile = getBaseContext().getFileStreamPath("Journal.txt");
            Scanner reader = new Scanner(journalFile);

            // Reading File
            while (reader.hasNextLine())
            {
                lines.add(reader.nextLine());
            }
        }
        catch (FileNotFoundException ex)
        {
            // Do nothing, already handled.
        }

        // Processing information to fill list
        for (String line : lines)
        {
            if (isDate(line))
            {
                date = line;
            }

            // Empty line
            else if (line.equals("")){}

            // Header
            else if (line.contains("Material")){}

            // Audit Separator
            else if (line.contains("--")){}

            // Process data
            else
            {
                Waste waste = new Waste();
                Scanner lineReader = new Scanner(line);
                waste.setDate(date);
                waste.setWasteMaterial(lineReader.next());
                waste.setWasteCategory(lineReader.next());
                waste.setAmount(lineReader.nextFloat());
                wasteList.add(waste);
                lineReader.close();
            }
        }
    }


    @SuppressWarnings("unused")
    public void showDatePicker(@SuppressWarnings("UnusedParameters") View view) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @SuppressWarnings("unused")
    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day);
            Date date = calendar.getTime();
            DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT);
            dateText.setText(dateFormat.format(date));
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
}
