package personal.td7.com.pushupstracker;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Date;
import java.util.Vector;

public class Stats extends AppCompatActivity {

    ListView l;
    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.stats);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        l = (ListView) findViewById(R.id.stats_list);

        StatAdapter adapter = new StatAdapter(getApplicationContext(),R.layout.statlist_content,new Vector<Data>());
        l.setAdapter(adapter);

        new TaskHandler().execute();

    }

    class Data{
        String day;
        int dayCount;

        Data(String day, int dayCount){
            this.day = day;
            this.dayCount = dayCount;
        }
    }

    class TaskHandler extends AsyncTask{

        Vector<Data> v = new Vector<>();
        StatAdapter adapter;
        SQLiteDatabase db = openOrCreateDatabase("data",MODE_PRIVATE,null);
        @Override
        protected void onPreExecute() {
            adapter = (StatAdapter) l.getAdapter();
        }

        @Override
        protected Object doInBackground(Object[] params) {
            Date thisWeek = new Date();
            int today = thisWeek.getDay();
            String tableName = Tracker.getTableName(thisWeek.getMonth() + 1);
            int startDate = thisWeek.getDate() - today;
            int lastDate = thisWeek.getDate() + (6 - today);
            Cursor stats = db.rawQuery("select * from "+tableName+" where Day <= "+lastDate+" and Day >= "+startDate,null);
            stats.moveToFirst();
            int weekAvg = 0;
            for(int i = 0;i<7;i++){
                String day = "";
                System.out.println("Listing "+i);
                switch(i){
                    case 0: day = "Sunday"; break;
                    case 1: day = "Monday"; break;
                    case 2: day = "Tuesday"; break;
                    case 3: day = "Wednesday"; break;
                    case 4: day = "Thursday"; break;
                    case 5: day = "Friday"; break;
                    case 6: day = "Saturday"; break;
                }
                int count = Integer.parseInt(stats.getString(1),0);
                weekAvg += count;
                stats.moveToNext();
                publishProgress(new Data(day,count));
            }
            publishProgress(weekAvg,1);
            return null;
        }

        @Override
        protected void onProgressUpdate(Object[] values) {
            if((int)values[1]!=0){
                int weekAvg = (int) values[0];
                weekAvg /= 7;
                TextView t = (TextView) findViewById(R.id.avg);
                t.setText("This Week's Average: "+weekAvg);
            }
            else adapter.add(values[0]);
        }
    }
}
