package com.bgomes.loansched;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import java.text.NumberFormat;

public class SchedFragment extends Fragment {
	private TableLayout tbl;
	
	private SharedPreferences savedValues;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		savedValues = PreferenceManager.getDefaultSharedPreferences(getActivity());
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            				 Bundle savedInstanceState) {
			View view = inflater.inflate(R.layout.activity_sched_fragment, container, false);

			tbl = (TableLayout) view.findViewById(R.id.loanTable);

			return view;
	}
	
	@Override
    public void onResume() {
        super.onResume();

        float p = Float.parseFloat(savedValues.getString("prin", "-1"));
        float r = savedValues.getFloat("intrate", -1f);
        int t = Integer.parseInt(savedValues.getString("term", "-1"));

        buildSched(p,r,t);
    }
	
	private void buildSched(float p, float r, int t) {
		TableRow tr;
	    TextView monthView,
	             bBalView,
	             intChargedView,
	             eBalView;
	    float month,
	          bBal,
	          interestCharged,
	          eBal,
	          moRate,
	          moPayment;

	    NumberFormat c = NumberFormat.getCurrencyInstance();

	    moRate = r / 12.0f;
	    moPayment = (float) (moRate + (moRate / (Math.pow(1+moRate,  t)-1))) * p;

	    bBal = p;

	    for (int i = 0; i < t; i++) {
	    	interestCharged = bBal * moRate;
	        eBal = bBal + interestCharged - moPayment;

	        tr = new TableRow(getActivity());
	        monthView = new TextView(getActivity());
	        monthView.setText(String.valueOf(i+1));
	        monthView.setGravity(Gravity.CENTER);
	        tr.addView(monthView, new TableRow.LayoutParams(0,
	        		TableRow.LayoutParams.WRAP_CONTENT, 1f));

	        bBalView = new TextView(getActivity());
	        bBalView.setText(String.valueOf(c.format(bBal)));
	        bBalView.setGravity(Gravity.CENTER);
	        tr.addView(bBalView, new TableRow.LayoutParams(0,
	                TableRow.LayoutParams.WRAP_CONTENT, 1f));

	        intChargedView = new TextView(getActivity());
	        intChargedView.setText(String.valueOf(c.format(interestCharged)));
	        intChargedView.setGravity(Gravity.CENTER);
	        tr.addView(intChargedView, new TableRow.LayoutParams(0,
	                TableRow.LayoutParams.WRAP_CONTENT, 1f));

	        eBalView = new TextView(getActivity());
	        eBalView.setText(String.valueOf(c.format(eBal)));
	        eBalView.setGravity(Gravity.CENTER);
	        tr.addView(eBalView, new TableRow.LayoutParams(0,
	                TableRow.LayoutParams.WRAP_CONTENT, 1f));

	        tbl.addView(tr);
	        bBal = eBal;
	    }
	 }
}
