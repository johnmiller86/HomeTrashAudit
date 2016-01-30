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
package com.example.chefj.hometrashaudit;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class JournalActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal);

        TextView lv = (TextView) findViewById(R.id.journalTextView);
        ArrayList<String> items = new ArrayList<>();
        try
        {
            // Reading Journal
            BufferedReader reader = new BufferedReader(new InputStreamReader(openFileInput("Journal.txt")));
            String line;

            while ((line = reader.readLine()) != null)
            {
                items.add(line + "\n");
            }
            for (String s : items)
            {
                lv.append(s);
            }
        }

        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }
}