package lt.moneymanager;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by ltaylor on 11/10/2017.
 */

public class FileManager {

    private FileManager(){}

    public static JSONObject getFinanceData(Context context) throws Exception {
        JSONObject data;
        File directory;
        FileInputStream fis;
        StringBuffer fileContent;
        byte[] buffer;
        int n;
        try{
            directory = context.getFilesDir();
            fileContent = new StringBuffer("");
            buffer = new byte[1024];

            File f = new File(directory.getAbsolutePath() + "/financedata.json");
            if(f.exists()) {
                fis = new FileInputStream(new File(directory.getAbsolutePath() + "/financedata.json"));
                while ((n = fis.read(buffer)) != -1) {
                    fileContent.append(new String(buffer, 0, n));
                }
                fis.close();

                data = new JSONObject(fileContent.toString());
            } else {
                data = new JSONObject();
            }
            data = updateMonthly(data,context);
            return data;
        } catch (Exception ex){
            Log.d("Error",ex.toString());
            throw new Exception("Error getting data",ex);
        }
    }

    public static void saveFinanceData(JSONObject data, Context context) throws Exception{
        File directory;
        FileOutputStream fos;

        try{
            directory = context.getFilesDir();

            fos = new FileOutputStream(new File(directory.getAbsolutePath() + "/financedata.json"),false);
            fos.write(data.toString().getBytes());
            fos.close();

        } catch (Exception ex){
            Log.d("Error_save",ex.toString());
            throw new Exception("Error saving data",ex);
        }
    }

    private static JSONObject updateMonthly(JSONObject data, Context _context) throws Exception{
        SimpleDateFormat format;
        JSONObject month;
        JSONObject obj;
        JSONArray arr;
        String currMonth;
        double freq;
        Calendar c;
        try{
            format = new SimpleDateFormat("MM/yyyy");
            currMonth = format.format(Calendar.getInstance().getTime());
            if(!data.has(currMonth)){
                //update recurring data

                if(!data.has("recurring")){
                    month = new JSONObject();
                    arr = new JSONArray();
                    month.put("Income",arr);
                    month.put("Expenses",arr);
                    data.put("recurring",month);
                } else {
                    month = data.getJSONObject("recurring");
                    c = Calendar.getInstance();
                    arr = month.getJSONArray("Income");
                    for(int i =0; i < arr.length(); i++) {
                        obj = arr.getJSONObject(i);
                        freq = obj.getDouble("frequency");
                        if (freq == 1) { //monthly
                            continue;
                        } else if (freq < 10) { //weekly
                            freq = c.getActualMaximum(Calendar.WEEK_OF_MONTH);
                        } else { //daily
                            freq = c.getActualMaximum(Calendar.DAY_OF_MONTH);
                        }
                        obj.put("frequency", freq);
                    }
                }

                //create new containers for the month
                month = new JSONObject();
                arr = new JSONArray();
                month.put("Income",arr);
                month.put("Expenses",arr);
                data.put(currMonth,month);

                saveFinanceData(data,_context);
            }
            return data;
        } catch (Exception ex){
            Log.d("ERROR",ex.toString());
            throw new Exception("Error doing monthly update",ex);
        }
    }
}
