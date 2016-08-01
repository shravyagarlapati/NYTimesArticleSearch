package com.shravyagarlapati.android.nytimessearch.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.shravyagarlapati.android.nytimessearch.DatePickerFragment;
import com.shravyagarlapati.android.nytimessearch.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class FilterOptionsActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, AdapterView.OnItemSelectedListener {

    Button btnPickDate;
    Spinner sortOrder;
    CheckBox checkBoxArts;
    CheckBox checkBoxSports;
    CheckBox checkBoxForeign;

    Button btnSave;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_options);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btnPickDate = (Button) findViewById(R.id.btnPickDate);
        sortOrder = (Spinner) findViewById(R.id.sortOrderSpinner);
        checkBoxArts = (CheckBox) findViewById(R.id.checkbox_arts);
        checkBoxSports = (CheckBox) findViewById(R.id.checkbox_sports);
        checkBoxForeign = (CheckBox) findViewById(R.id.checkbox_foreign);
        btnSave = (Button) findViewById(R.id.btnSave);

        // Create an ArrayAdapter using the string array and a default spinner layout
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.order_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        sortOrder.setAdapter(adapter);
        sortOrder.setOnItemSelectedListener(this);

        CompoundButton.OnCheckedChangeListener checkListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton view, boolean checked) {
                // compoundButton is the checkbox
                // boolean is whether or not checkbox is checked
                // Check which checkbox was clicked
                switch(view.getId()) {
                    case R.id.checkbox_arts:
                        if (checked) {
                            checkBoxArts.setChecked(true);
                        } else {
                            checkBoxArts.setChecked(false);
                        }
                        break;
                    case R.id.checkbox_sports:
                        if (checked) {
                            checkBoxSports.setChecked(true);
                        } else {
                           checkBoxSports.setChecked(false);
                        }
                    case R.id.checkbox_foreign:
                        if (checked) {
                            checkBoxForeign.setChecked(true);
                        } else {
                            checkBoxForeign.setChecked(false);
                        }
                        break;
                }
            }
        };

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log.d("Did I come here","Hello");


                Intent data = new Intent();
                data.putExtra("Code",200);

                /*
                if(btnPickDate.getText().toString().length()>0)
                    data.putExtra("Date", btnPickDate.getText().toString());

                data.putExtra("Order", sortOrder.getSelectedItem().toString());
                */
                StringBuffer newsDeskOption = new StringBuffer();


                if(checkBoxArts.isChecked()) {
                    newsDeskOption.append("Arts");
                    newsDeskOption.append(":");
                }

                if(checkBoxSports.isChecked()) {
                    newsDeskOption.append("Sports");
                    newsDeskOption.append(":");
                }
                if(checkBoxForeign.isChecked())
                    newsDeskOption.append("Foreign");

                Log.d("News Option",""+newsDeskOption.toString().length());
               // data.putExtra("NewsDesk",newsDeskOption.toString());

                SharedPreferences mSettings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = mSettings.edit();
                editor.putString("DATE",btnPickDate.getText().toString());
                editor.putString("NEWSDESK",newsDeskOption.toString());
                editor.putString("ORDER",sortOrder.getSelectedItem().toString());
                editor.apply();

                setResult(RESULT_OK, data);
                finish();

            }
        });

    }

    // attach to an onclick handler to show the date picker
    public void showDatePickerDialog(View v) {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    // handle the date selected
    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        // store the values selected into a Calendar instance
        final Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, monthOfYear);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.format(c.getTime());
        btnPickDate.setText(""+  dateFormat.format(c.getTime()));
        Log.d("Test",btnPickDate.getText().toString());
    }

    public void setSpinnerToValue(Spinner spinner, String value) {
        int index = 0;
        SpinnerAdapter adapter = spinner.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).equals(value)) {
                index = i;
                break; // terminate loop
            }
        }
        Log.d("Spinner Index",":"+index);
        spinner.setSelection(index);
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        String item = parent.getItemAtPosition(pos).toString();
//        Log.d("Item in Spinner",item);
//        Toast.makeText(parent.getContext(),
//                "OnItemSelectedListener : " + parent.getItemAtPosition(pos).toString(),
//                Toast.LENGTH_SHORT).show();

        setSpinnerToValue(sortOrder, item);
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_article, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}

