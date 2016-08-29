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

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.johnmillercoding.hometrashaudit.R;
import com.johnmillercoding.hometrashaudit.services.SessionManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class JournalActivity extends AppCompatActivity {

    // Variable Declarations
    private ArrayList<String> items;
    private ArrayAdapter<String> adapter;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal);

        // Instantiating Session
        session = new SessionManager(getApplicationContext());

        // Configuring ListView and Adapter
        ListView listView = (ListView) findViewById(R.id.journalListView);
        items = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, R.layout.waste_list_item, R.id.waste_item, items);
        assert listView != null;
        listView.setAdapter(adapter);

        // Displaying
        displayJournal();
    }

    /**
     * Reads the waste file.
     */
    private void displayJournal() {
        items.clear();
        String line;
        StringBuilder sb = new StringBuilder();
        try {
            // Reading Journal
            BufferedReader reader = new BufferedReader(new InputStreamReader(openFileInput("Journal.txt")));

            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            items.add(sb.toString());
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        if (items.isEmpty()){
            items.add("A journal has not yet been created!");
        }
        adapter.notifyDataSetChanged();
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