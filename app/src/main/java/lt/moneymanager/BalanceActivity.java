package lt.moneymanager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class BalanceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_balance);

        //new File(getFilesDir().getAbsolutePath() + "/financedata.json").delete();


        JSONObject financeData;
        double cashFlow;
        TextView txtCash;
        try{
            financeData = FileManager.getFinanceData(this);

            cashFlow = calculateCashflow(financeData);

            txtCash = ((TextView)findViewById(R.id.big_cash));
            txtCash.setText("£" + Double.toString(cashFlow));

            if(cashFlow > 0){
                txtCash.setTextColor(getResources().getColor(R.color.positive_cashflow));
            } else {
                txtCash.setTextColor(getResources().getColor(R.color.negative_cashflow));
            }

        } catch (Exception ex){
            Log.d("ERROR",ex.toString());
        }
    }

    public double calculateCashflow(JSONObject financeData) throws Exception{
        double total = 0;
        JSONArray arr;
        JSONObject data;
        JSONObject obj;
        SimpleDateFormat format;
        try{
            //this month
            format = new SimpleDateFormat("MM/yyyy");
            data = financeData.getJSONObject(format.format(Calendar.getInstance().getTime()));
            arr = data.getJSONArray("Income");
            for(int i = 0; i < arr.length(); i++){
                obj = arr.getJSONObject(i);
                total += obj.getDouble("cost");
            }

            arr = data.getJSONArray("Expenses");
            for(int i =0; i < arr.length(); i++){
                obj = arr.getJSONObject(i);
                total -= obj.getDouble("cost");
            }

            //recurring
            data = financeData.getJSONObject("recurring");
            arr = data.getJSONArray("Income");
            for(int i = 0; i < arr.length(); i++){
                obj = arr.getJSONObject(i);
                total += (obj.getDouble("cost") * obj.getDouble("frequency"));
            }

            arr = data.getJSONArray("Expenses");
            for(int i =0; i < arr.length(); i++){
                obj = arr.getJSONObject(i);
                total -= (obj.getDouble("cost") * obj.getDouble("frequency"));
            }
            return total;
        } catch (Exception ex){
            Log.d("Error",ex.toString());
            throw new Exception("Error calculating cash flow",ex);
        }
    }

    public void addFinance(View v){
        Intent intent = new Intent(BalanceActivity.this,FinanceActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in,R.anim.slide_out);
    }

    public void viewFinance(View v){
        Intent intent = new Intent(BalanceActivity.this, ViewIncomeExpenses.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_up,R.anim.slide_up_down);
    }

}
