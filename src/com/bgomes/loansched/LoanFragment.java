package com.bgomes.loansched;

import java.text.NumberFormat;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class LoanFragment extends Fragment
			 implements OnClickListener, OnEditorActionListener {
	
	// define variables for the widgets
	private EditText prin;
	private EditText term;
	private TextView rate;
	private TextView mopmt;
	private Button rateUp;
	private Button rateDown;
	private Button schedule;
		
	private float ratedefault = .0525f;
	private float rateincrement = .0025f;
	private float ratecurrent = ratedefault;
	NumberFormat pct = NumberFormat.getPercentInstance();
	NumberFormat curr = NumberFormat.getCurrencyInstance();
	
	private SharedPreferences savedValues;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		savedValues = PreferenceManager.getDefaultSharedPreferences(getActivity());
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater,
							 ViewGroup container,
							 Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_loan_fragment);

        View view = inflater.inflate(R.layout.activity_loan_fragment,
                                     container, false);

        pct.setMinimumFractionDigits(3);
        
        // get references to the widgets
        prin = (EditText) view.findViewById(R.id.prinEditText);
        term = (EditText) view.findViewById(R.id.termEditText);
        rate = (TextView) view.findViewById(R.id.rateTextView);
        mopmt = (TextView) view.findViewById(R.id.mopmtTextView);
        rateUp = (Button) view.findViewById(R.id.rateUpButton);
        rateDown = (Button) view.findViewById(R.id.rateDownButton);
        schedule = (Button) view.findViewById(R.id.schedButton);
        
        // format rate to percentage
        rate.setText(pct.format(ratedefault));
        
        // set the listeners
        term.setOnEditorActionListener(this);
        rateUp.setOnClickListener(this);
        rateDown.setOnClickListener(this);
        schedule.setOnClickListener(this);

        //  savedValues = getSharedPreferences("SavedValues", MODE_PRIVATE);
        return view;
	}
	
	@Override
    public void onPause() {
        // save the instance variables       
        Editor editor = savedValues.edit();        
        editor.putString("prin", prin.getText().toString());
        editor.putFloat("intrate", ratecurrent);
        editor.putString("term", term.getText().toString());
        editor.commit();        

        super.onPause();      
    }
    
    @Override
    public void onResume() {
        super.onResume();
        
        prin.setText(savedValues.getString("prin", ""));
        ratecurrent = savedValues.getFloat("intrate", ratedefault);
        rate.setText(pct.format(ratecurrent));
        term.setText(savedValues.getString("term", ""));
        
        // calculate and display
        calcDisplay();
    }
    
    @Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.rateUpButton:
				// Error Checking Interest Rate (Max)
				try {
					ratecurrent += rateincrement;
					if (ratecurrent > 0.400f) {
						ratecurrent -= rateincrement;
						throw new Exception();
					}
				} catch (Exception e) {
					Toast t = Toast.makeText(getActivity(), "Int. Rate Too High, Avoid Loan Sharks", Toast.LENGTH_SHORT);
					t.show();
				}
				calcDisplay();
				break;
			case R.id.rateDownButton:
				// Error Checking Interest Rate (Min)
				try {
					ratecurrent -= rateincrement;
					if (ratecurrent < 0.000f) {
						ratecurrent += rateincrement;
						throw new Exception();
					}
				} catch (Exception e) {
					Toast t = Toast.makeText(getActivity(), "Int. Rate Must Be A Positive Number", Toast.LENGTH_SHORT);
					t.show();
				}
				calcDisplay();
				break;
            case R.id.schedButton:
                getFragmentManager().beginTransaction()
                        .replace(android.R.id.content, new SchedFragment()).commit();
                break;
		}
	}
    
    private void calcDisplay() {
		// Error Checking Principle Amount (Min)
		try {
			float principle = Float.parseFloat(prin.getText().toString());
			if (principle < 0.00f) {	
				throw new Exception();
			}
		} catch (Exception e) {
			Toast t = Toast.makeText(getActivity(), "Principle Amount Must Be A Positive Number", Toast.LENGTH_SHORT);
			t.show();
		}
		// Error Checking Term Amount (Min, Max)
		try {
			int termLength = Integer.parseInt(term.getText().toString());
			if (termLength < 1) {	
				throw new Exception();
			}
		} catch (Exception e) {
			Toast t = Toast.makeText(getActivity(), "The Term Can Not Be Less Than One Month", Toast.LENGTH_SHORT);
			t.show();
		}
		try {
			int termLength = Integer.parseInt(term.getText().toString());
			if (termLength > 360) {	
				throw new Exception();
			}
		} catch (Exception e) {
			Toast t = Toast.makeText(getActivity(), "Loan's Term Is Too Long, Don't Make Poor Decisions", Toast.LENGTH_SHORT);
			t.show();
		}
		
		// Error Checking The Monthly Payment Calculation
		try {
			rate.setText(pct.format(ratecurrent));
			int t = Integer.parseInt(term.getText().toString());
			float p = Float.parseFloat(prin.getText().toString());
			float mointrate = (float) (ratecurrent / 12.0f);
			float mp = (float) (mointrate + (mointrate / (Math.pow(1+mointrate,  t)-1))) * p;
			
			mopmt.setText(curr.format(mp));
			
		} catch (Exception e) {
			Toast t = Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT);
			t.show();
		}
	}

    @Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if (actionId == EditorInfo.IME_ACTION_DONE ||
			actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
			calcDisplay();
		}
		return false;
	}
}
