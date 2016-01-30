/**
 * Activity for home screen.
 * @author John D. Miller
 * @version 1.0.2
 * @since 12/06/2015
 */
package com.example.chefj.hometrashaudit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;

public class HomeScreenActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // First time run
        File file = getBaseContext().getFileStreamPath("Settings.txt");
        if (!file.exists())
        {
            Intent i = new Intent(getApplicationContext(), FirstTimeActivity.class);
            startActivity(i);
        }

        // Button Listeners
        Button newAuditButton = (Button)findViewById(R.id.newAuditButton);
        newAuditButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), HomeTrashAuditActivity.class);
                startActivity(i);
            }
        });

        Button viewJournalButton = (Button) findViewById(R.id.viewJournalButton);
        viewJournalButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), JournalActivity.class);
                File file = getBaseContext().getFileStreamPath("Journal.txt");
                if(file.exists())
                {
                    startActivity(i);
                }
                else
                {
                    Toast.makeText(HomeScreenActivity.this, "You have not yet created a journal!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button viewTotalsButton = (Button)findViewById(R.id.viewTotalsButton);
        viewTotalsButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), TotalsActivity.class);

                File file = getBaseContext().getFileStreamPath("Journal.txt");
                if(file.exists())
                {
                    startActivity(i);
                }
                else
                {
                    Toast.makeText(HomeScreenActivity.this, "You must create a journal before viewing totals!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }
}
