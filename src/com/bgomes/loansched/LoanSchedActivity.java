package com.bgomes.loansched;

/*
 * Ben Gomes
 * IS 295 / Daniel
 * Loan Schedule Application created with Eclipse
 */
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


public class LoanSchedActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        getFragmentManager().beginTransaction().replace(android.R.id.content, 
        					 new LoanFragment()).commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.loan_sched, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.return_main) {
        	getFragmentManager().beginTransaction().replace(android.R.id.content, 
					 new LoanFragment()).commit();
        	return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
