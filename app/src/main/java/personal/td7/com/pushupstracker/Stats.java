package personal.td7.com.pushupstracker;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.SystemClock;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Vector;

import static personal.td7.com.pushupstracker.Tracker.dt;

public class Stats extends AppCompatActivity {

    ListView l;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stats);

        Drawable dr = new ColorDrawable(Color.WHITE);
        dr.setAlpha(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(dr);

        l = (ListView) findViewById(R.id.stats_list);
        System.out.println("Stats act activated!");

        System.out.println("About to do some work!");
        new TaskHandler().execute();

    }

    static class Data{
        String day;
        int dayCount;

        Data(String day, int dayCount){
            this.day = day;
            this.dayCount = dayCount;
        }
    }

    class TaskHandler extends AsyncTask{

        Vector<Data> v = new Vector<>();
        SQLiteDatabase db = openOrCreateDatabase("data",MODE_PRIVATE,null);

        @Override
        protected Object doInBackground(Object[] params) {
            Calendar thisWeek = dt;
            Calendar currentWeek = Calendar.getInstance();
            //Date thisWeek = new Date(2017,1,2);
            int today = thisWeek.get(Calendar.DAY_OF_WEEK)-1;
            int startDate = thisWeek.get(Calendar.DATE) - today;
            int lastDate = startDate + 6;

            Log.d("Stats",startDate+" "+thisWeek.get(Calendar.DATE));
            if(thisWeek.compareTo(currentWeek) > 0){
                today = currentWeek.get(Calendar.DAY_OF_WEEK)-1;
                startDate = currentWeek.get(Calendar.DATE) - today;
                lastDate = currentWeek.get(Calendar.DATE);
                publishProgress(0,3);
            }
            else if(thisWeek.get(Calendar.DATE) - startDate < 6 && startDate > 0){
                today = thisWeek.get(Calendar.DAY_OF_WEEK)-1;
                lastDate = startDate + today;
            }

            Cursor stats = null;
            Log.d("Stats","Day: "+today+" Starts: "+startDate+" Ends: "+lastDate);
            if(startDate > 0) {
                String tableName = Tracker.getTableName(thisWeek.get(Calendar.MONTH) + 1);
                stats = db.rawQuery("select * from " + tableName + " where Day <= " + lastDate + " and Day >= " + startDate, null);
                stats.moveToFirst();
                double weekAvg = 0;
                for(int i = 0;i<=today;i++){
                    String day = "";
                    switch(i){
                        case 0: day = "Sunday"; break;
                        case 1: day = "Monday"; break;
                        case 2: day = "Tuesday"; break;
                        case 3: day = "Wednesday"; break;
                        case 4: day = "Thursday"; break;
                        case 5: day = "Friday"; break;
                        case 6: day = "Saturday"; break;
                    }
                    int count = Integer.parseInt(stats.getString(1));
                    System.out.println("Listing "+i+": "+count);
                    weekAvg += count;
                    stats.moveToNext();
                    publishProgress(new Data(day,count),0);
                }
                today++;
                weekAvg = (weekAvg/today);
                publishProgress(weekAvg,1);
                publishProgress(0,2);
            }
            else{
                int month = thisWeek.get(Calendar.MONTH);
                if(month==0) month = 12;
                int offset = 31;
                switch (month){
                    case 4: case 6: case 9: case 11:
                        offset = 30;
                        break;
                    case 2:
                        offset = 28;
                        int yr = thisWeek.get(Calendar.YEAR);
                        if(yr%4==0) offset++;
                }
                String tableName = Tracker.getTableName(month);
                System.out.println("Initial start date: "+startDate+" and day: "+today);
                startDate = offset + startDate;
                System.out.println("Final start date: "+startDate);

                //Query for prev month
                stats = db.rawQuery("select * from " + tableName + " where Day >= " + startDate, null);
                stats.moveToFirst();
                Cursor stats2 = db.rawQuery("select * from "+tableName,null);
                Log.e("Stats jhol",stats2.getCount()+"");
                double weekAvg = 0;
                int i;
                System.out.println("Current state, offset="+offset+" startdate="+startDate+" tablename="+tableName);
                for(i = 0;i<=(offset - startDate);i++){
                    System.out.println("\tIteration "+(i+1));
                    String day = "";
                    switch(i){
                        case 0: day = "Sunday"; break;
                        case 1: day = "Monday"; break;
                        case 2: day = "Tuesday"; break;
                        case 3: day = "Wednesday"; break;
                        case 4: day = "Thursday"; break;
                        case 5: day = "Friday"; break;
                        case 6: day = "Saturday"; break;
                    }
                    int count = Integer.parseInt(stats.getString(1));
                    System.out.println("1st Listing "+i+": "+count);
                    weekAvg += count;
                    publishProgress(new Data(day,count),0);
                    if(i==(offset-startDate))
                        break;
                    stats.moveToNext();
                }

                i++;
                //Query for current Month
                if(month == 12) month = 0;
                tableName = Tracker.getTableName(++month);
                System.out.println("2nd listing table="+tableName);
                stats = db.rawQuery("select * from " + tableName + " where Day <= " + thisWeek.get(Calendar.DATE), null);
                stats.moveToFirst();
                for(;i<=today;i++){
                    String day = "";
                    switch(i){
                        case 0: day = "Sunday"; break;
                        case 1: day = "Monday"; break;
                        case 2: day = "Tuesday"; break;
                        case 3: day = "Wednesday"; break;
                        case 4: day = "Thursday"; break;
                        case 5: day = "Friday"; break;
                        case 6: day = "Saturday"; break;
                    }
                    int count = Integer.parseInt(stats.getString(1));
                    System.out.println("2nd Listing "+i+": "+count);
                    weekAvg += count;
                    publishProgress(new Data(day,count),0);
                    if(i==today) break;
                    stats.moveToNext();
                }
                weekAvg = (weekAvg/today);
                publishProgress(weekAvg,1);
                publishProgress(0,2);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Object[] values) {
            if((int)values[1]==1){
                double weekAvg = (double) values[0];
                TextView t = (TextView) findViewById(R.id.avg);
                DecimalFormat df = new DecimalFormat("##.##");
                weekAvg = Double.parseDouble(df.format(weekAvg));
                t.setText("This Week's Average: "+weekAvg);
            }
            else if((int)values[1]==2){
                System.out.println("Setting adapter");
                StatAdapter adapter = new StatAdapter(getApplicationContext(),R.layout.statlist_content,v);
                l.setAdapter(adapter);
            }
            else if((int) values[1] == 3){
                Toast.makeText(getApplicationContext(),"Future week's statistics can't be shown. Showing current week's statistics",Toast.LENGTH_LONG);
            }
            else v.add((Data)values[0]);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == 16908332){
            System.out.println("Going back");
            NavUtils.navigateUpTo(this,new Intent(getApplicationContext(),Tracker.class));
        }
        return super.onOptionsItemSelected(item);
    }
}
