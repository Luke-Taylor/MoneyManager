package lt.moneymanager;

import android.annotation.SuppressLint;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FinanceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_finance);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        Spinner spinner = (Spinner)findViewById(R.id.spinType);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.pTypes,R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button.
            NavUtils.navigateUpFromSameTask(this);
            finish();
            overridePendingTransition(R.anim.slide_out_in,R.anim.slide_out_out);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void saveDetails(View v){
        JSONObject financeData;
        JSONObject newData;
        JSONObject obj;
        JSONArray arr;
        Spinner freq;
        SimpleDateFormat format;
        try{
            financeData = FileManager.getFinanceData(this);
            freq = (Spinner)findViewById(R.id.spinType);

            newData = new JSONObject();
            newData.put("cost",Double.parseDouble(((EditText)findViewById(R.id.numAmount)).getText().toString()));
            newData.put("name",((EditText)findViewById(R.id.txtName)).getText().toString());

            if(freq.getSelectedItemPosition() == 0){
                format = new SimpleDateFormat("MM/yyyy");
                obj = financeData.getJSONObject(format.format(Calendar.getInstance().getTime()));
                if(((RadioButton)findViewById(R.id.radExpense)).isChecked()){
                    arr = obj.getJSONArray("Expenses");
                } else {
                    arr = obj.getJSONArray("Income");
                }
                arr.put(newData);
            } else {

                Calendar c = Calendar.getInstance();
                switch (freq.getSelectedItemPosition()){
                    case 1: //daily
                        newData.put("frequency",c.getActualMaximum(Calendar.DAY_OF_MONTH));
                        break;

                    case 2: //weekly
                        newData.put("frequency",c.getActualMaximum(Calendar.WEEK_OF_MONTH));
                        break;

                    case 3: //monthly
                        newData.put("frequency",1);
                        break;
                }
                obj = financeData.getJSONObject("recurring");
                if(((RadioButton)findViewById(R.id.radExpense)).isChecked()){
                    arr = obj.getJSONArray("Expenses");
                } else {
                    arr = obj.getJSONArray("Income");
                }
                arr.put(newData);
            }

            FileManager.saveFinanceData(financeData,this);
            finish();
            overridePendingTransition(R.anim.slide_out_in,R.anim.slide_out_out);
        } catch (Exception ex){
            Log.d("ERROR",ex.toString());
        }
    }
}
