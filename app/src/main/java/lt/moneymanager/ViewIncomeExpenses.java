package lt.moneymanager;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static android.R.attr.typeface;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class ViewIncomeExpenses extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view_income_expenses);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        JSONObject financeData;
        JSONObject obj;
        JSONArray arr;
        SimpleDateFormat format;
        try{
            financeData = FileManager.getFinanceData(this);
            obj = financeData.getJSONObject("recurring");

            arr = obj.getJSONArray("Income");
            generateTable(arr,R.id.tableMonthlyIn);

            arr = obj.getJSONArray("Expenses");
            generateTable(arr,R.id.tableMonthlyEx);

            format = new SimpleDateFormat("MM/yyyy");
            obj = financeData.getJSONObject(format.format(Calendar.getInstance().getTime()));

            arr = obj.getJSONArray("Income");
            generateTable(arr,R.id.tableThisMonthIn);

            arr = obj.getJSONArray("Expenses");
            generateTable(arr,R.id.tableThisMonthEx);

        } catch (Exception ex){
            Log.d("Error", ex.toString());
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button.
            NavUtils.navigateUpFromSameTask(this);
            finish();
            overridePendingTransition(R.anim.slide_down_up,R.anim.slide_down);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void generateTable(JSONArray arr, int tableID) throws Exception{
        TableLayout table;
        TableRow row;
        TextView cost;
        TextView description;
        TextView frequency;
        TableRow.LayoutParams params;
        JSONObject data;
        double freq;
        double amount;
        double total = 0;
NumberFormat curr;
        try{
            params = new TableRow.LayoutParams();
            params.weight = 1;

            curr = NumberFormat.getCurrencyInstance();

            table = (TableLayout)findViewById(tableID);

            row = new TableRow(this);
            cost = new TextView(this);

            cost.setText("Amount");
            //params = (TableRow.LayoutParams)cost.getLayoutParams();
            //params.weight = 1;
            cost.setLayoutParams(params);
            cost.setTypeface(null, Typeface.BOLD);

            description = new TextView(this);
            description.setText("Description");
            //params = (TableRow.LayoutParams)description.getLayoutParams();
            //params.weight = 1;
            description.setLayoutParams(params);
            description.setTypeface(null, Typeface.BOLD);

            frequency = new TextView(this);
            frequency.setText("Frequency");
            //params = (TableRow.LayoutParams)frequency.getLayoutParams();
            //params.weight = 1;
            frequency.setLayoutParams(params);
            frequency.setTypeface(null, Typeface.BOLD);

            row.addView(frequency);
            row.addView(description);
            row.addView(cost);

            table.addView(row);

            for(int i = 0; i < arr.length(); i++){
                row = new TableRow(this);
                cost = new TextView(this);
                description = new TextView(this);
                frequency = new TextView(this);
                data = arr.getJSONObject(i);

                amount = data.getDouble("cost");
             //   params = (TableRow.LayoutParams)cost.getLayoutParams();
              //  params.weight = 1;

                description.setText(data.getString("description"));
               // params = (TableRow.LayoutParams)description.getLayoutParams();
               // params.weight = 1;
                description.setLayoutParams(params);


                if(data.has("frequency")) {
                    freq = data.getDouble("frequency");
                    total += amount * freq;
                    if (freq == 1) {
                        frequency.setText("Monthly");
                    } else if (freq < 10) {
                        frequency.setText("Weekly");
                        cost.setText(curr.format(amount) + " (" + curr.format(amount * freq) + ")");
                    } else { //daily
                        frequency.setText("Daily");
                        cost.setText(curr.format(amount) + " (" + curr.format(amount * freq) + ")");
                    }
                } else {
                    frequency.setText("-");
                    total += amount;
                }
                frequency.setLayoutParams(params);

                if(amount > 0){
                    cost.setTextColor(getResources().getColor(R.color.positive_cashflow));
                } else {
                    cost.setTextColor(getResources().getColor(R.color.negative_cashflow));
                    amount = -amount;
                }
                cost.setText(curr.format(amount));
                //params = (TableRow.LayoutParams)frequency.getLayoutParams();
                //params.weight = 1;
                cost.setLayoutParams(params);

                row.addView(frequency);
                row.addView(description);
                row.addView(cost);

                table.addView(row);
            }

            row = new TableRow(this);
            cost = new TextView(this);
            description = new TextView(this);
            frequency = new TextView(this);


            frequency.setText("");
//            params = (TableRow.LayoutParams)cost.getLayoutParams();
 //           params.weight = 1;
            frequency.setLayoutParams(params);

            description.setText("Total cashflow: ");
    //        params = (TableRow.LayoutParams)description.getLayoutParams();
  //          params.weight = 1;
            description.setLayoutParams(params);
            description.setTypeface(null, Typeface.BOLD);

            if(total > 0){
                cost.setTextColor(getResources().getColor(R.color.positive_cashflow));
            } else {
                cost.setTextColor(getResources().getColor(R.color.negative_cashflow));
                if(total < 0) {
                    total = -total;
                }
            }
            cost.setText(curr.format(total));
     //       params = (TableRow.LayoutParams)frequency.getLayoutParams();
     //       params.weight = 1;
            cost.setLayoutParams(params);
            cost.setTypeface(null, Typeface.BOLD);

            row.addView(frequency);
            row.addView(description);
            row.addView(cost);

            table.addView(row);

        } catch (Exception ex){
            Log.d("Error",ex.toString());
            throw new Exception("Error generating table",ex);
        }
    }

}
