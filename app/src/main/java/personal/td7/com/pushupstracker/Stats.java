package personal.td7.com.pushupstracker;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Date;
import java.util.Vector;

public class Stats extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.stats);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ListView lv = (ListView) findViewById(R.id.stats_list);

        Date thisWeek = new Date();

        int today = thisWeek.getDay();
        Vector<Data> v = new Vector<>();
        SQLiteDatabase db = openOrCreateDatabase("data",MODE_PRIVATE,null);

        String tableName = Tracker.getTableName(thisWeek.getMonth() + 1);
        int startDate = thisWeek.getDate() - today;
        int lastDate = thisWeek.getDate() + (6 - today);
        Cursor stats = db.rawQuery("select * from "+tableName+" where Day <= "+lastDate+" and Day >= "+startDate,null);
        stats.moveToFirst();
        int weekAvg = 0;
        for(int i = 0;i<7;i++){
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
            weekAvg += count;
            v.add(new Data(day,count));
            stats.moveToNext();
        }

        weekAvg /= 7;

        StatAdapter adapter = new StatAdapter(getApplicationContext(),R.layout.statlist_content,v);
        lv.setAdapter(adapter);

        TextView t = (TextView) findViewById(R.id.avg);
        t.setText("This Week's Average: "+weekAvg);
    }

    class Data{
        String day;
        int dayCount;

        Data(String day, int dayCount){
            this.day = day;
            this.dayCount = dayCount;
        }
    }
}
