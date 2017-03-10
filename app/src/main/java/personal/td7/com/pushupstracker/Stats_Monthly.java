package personal.td7.com.pushupstracker;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Vector;

import static personal.td7.com.pushupstracker.Tracker.dt;

public class Stats_Monthly extends AppCompatActivity{
    ListView l;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
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

    class TaskHandler extends AsyncTask {

        Vector<Stats.Data> v = new Vector<>();
        SQLiteDatabase db = openOrCreateDatabase("data",MODE_PRIVATE,null);

        @Override
        protected Object doInBackground(Object[] params) {
            Calendar thisMonth = dt;
            Calendar actualMonth = Calendar.getInstance();

            int m = thisMonth.get(Calendar.MONTH)+1;
            int d = thisMonth.get(Calendar.DATE);
            int maxdays = 31;
            switch (m){
                case 2:
                    boolean isLeapYear = false;
                    int yr = thisMonth.get(Calendar.YEAR);
                    if(yr%4==0) isLeapYear = true;
                    maxdays = 28;
                    if(isLeapYear) maxdays++;
                    break;
                case 4: case 6: case 9: case 11:
                    maxdays = 30;
                    break;
            }

            if(thisMonth.get(Calendar.MONTH) < actualMonth.get(Calendar.MONTH)){
                d = maxdays;
            }
            else if(thisMonth.get(Calendar.MONTH) == actualMonth.get(Calendar.MONTH)){
                d = actualMonth.get(Calendar.DATE);
            }

            String tableName = Tracker.getTableName(m);
            Cursor res = db.rawQuery("select * from "+tableName,null);
            res.moveToFirst();

            double monthAvg = 0;
            for (int i=1;i<=d;i++){
                int count = Integer.parseInt(res.getString(1));
                publishProgress(new Stats.Data(tableName.substring(0,tableName.length()-2)+" "+i,count),0);
                res.moveToNext();
                monthAvg += count;
            }

            monthAvg = monthAvg / d;
            publishProgress(monthAvg,1);
            publishProgress(0,2);
            return null;
        }

        @Override
        protected void onProgressUpdate(Object[] values) {
            if((int)values[1]==1){
                double monthAvg = (double) values[0];
                TextView t = (TextView) findViewById(R.id.avg);
                DecimalFormat df = new DecimalFormat("##.##");
                monthAvg = Double.parseDouble(df.format(monthAvg));
                t.setText("This Month's Average: "+monthAvg);
            }
            else if((int)values[1]==2){
                System.out.println("Setting adapter");
                StatAdapter adapter = new StatAdapter(getApplicationContext(),R.layout.statlist_content,v);
                l.setAdapter(adapter);
            }
            else v.add((Stats.Data)values[0]);
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
