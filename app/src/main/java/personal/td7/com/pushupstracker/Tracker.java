package personal.td7.com.pushupstracker;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Date;

public class Tracker extends AppCompatActivity {

    static int count = 0;
    TextView counts;
    FloatingActionButton fab;
    CalendarView c;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tracker);

        Date date = new Date();
        if(date.getDate() == 1){
            createNewMonthsTable();
        }

        /*View v = findViewById(R.id.mainContent);

        Button btn = (Button) v.findViewById(R.id.showStats);*/


        counts = (TextView) findViewById(R.id.displayCount);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        c = (CalendarView) findViewById(R.id.cal);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final Dialog d = new Dialog(Tracker.this);
                d.setContentView(R.layout.edit_count);
                d.findViewById(R.id.reg).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new TaskHandler().execute(1,view,d);
                    }
                });
                d.show();
            }
        });

        count = 0;
        counts.setText(count+"");

        c.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                new TaskHandler().execute(0,month,dayOfMonth);
            }
        });

        /*System.out.println("Set onclick for btn");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Pressed");
                Intent in = new Intent(getApplicationContext(),Stats.class);
                startActivity(in);
            }
        });*/

        FloatingActionButton f = (FloatingActionButton) findViewById(R.id.fab_stat);
        f.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Pressed");
                Intent in = new Intent(getApplicationContext(),Stats.class);
                startActivity(in);
            }
        });

        //initDBCreation();

    }

    static String getTableName(int m){
        Date today = new Date();
        String tableName = today.getYear()+"";
        tableName = tableName.substring(1);
        int monthno = m;
        System.out.println(tableName+" "+today.getYear()+" "+monthno);
        switch (monthno){
            case 1:
                tableName = "January"+tableName;
                break;
            case 2:
                tableName = "February"+tableName;
                break;
            case 3:
                tableName = "March"+tableName;
                break;
            case 4:
                tableName = "April"+tableName;
                break;
            case 5:
                tableName = "May"+tableName;
                break;
            case 6:
                tableName = "June"+tableName;
                break;
            case 7:
                tableName = "July"+tableName;
                break;
            case 8:
                tableName = "August"+tableName;
                break;
            case 9:
                tableName = "September"+tableName;
                break;
            case 10:
                tableName = "October"+tableName;
                break;
            case 11:
                tableName = "November"+tableName;
                break;
            case 12:
                tableName = "December"+tableName;
                break;
        }
        return tableName;
    }

    private void createNewMonthsTable(){
        SQLiteDatabase db = openOrCreateDatabase("data",MODE_PRIVATE,null);
        Date today = new Date();
        int monthno = today.getMonth();
        String tableName = today.getYear()+"";
        int maxdays = 31;
        switch (monthno){
            case 1:
                tableName = "January"+tableName;
                break;
            case 2:
                boolean isLeapYear = false;
                int yr = Integer.parseInt(tableName);
                if(yr%4==0) isLeapYear = true;
                tableName = "February"+tableName;
                maxdays = 28;
                if(isLeapYear) maxdays++;
                break;
            case 3:
                tableName = "March"+tableName;
                break;
            case 4:
                tableName = "April"+tableName;
                maxdays = 30;
                break;
            case 5:
                tableName = "May"+tableName;
                break;
            case 6:
                tableName = "June"+tableName;
                maxdays = 30;
                break;
            case 7:
                tableName = "July"+tableName;
                break;
            case 8:
                tableName = "August"+tableName;
                break;
            case 9:
                tableName = "September"+tableName;
                maxdays = 30;
                break;
            case 10:
                tableName = "October"+tableName;
                break;
            case 11:
                tableName = "November"+tableName;
                maxdays = 30;
                break;
            case 12:
                tableName = "December"+tableName;
                break;
        }
        db.execSQL("CREATE TABLE IF NOT EXISTS "+tableName+"(Day int, Count int NOT NULL)");
        for(int i=1;i<maxdays;i++){
            db.execSQL("INSERT INTO "+tableName+" VALUES("+i+",0)");
        }
    }

    private void initDBCreation(){
        new TaskHandler().execute(99);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tracker, menu);
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

    private class TaskHandler extends AsyncTask{
        @Override
        protected Object doInBackground(Object[] params) {
            int m = (int) params[0];

            //On Date change listener task handling here
            if(m==0){
                try {
                    int month = (int) params[1];
                    int dayOfMonth = (int) params[2];
                    Date date = new Date();
                    if (dayOfMonth != date.getDate()) {
                        publishProgress(0,1);
                    } else publishProgress(0,2);
                    SQLiteDatabase db = openOrCreateDatabase("data", MODE_PRIVATE, null);

                    String tableName = getTableName(month + 1);
                    Cursor res = db.rawQuery("select * from " + tableName + " where Day = " + dayOfMonth, null);
                    res.moveToFirst();
                    count = 0;
                    if (!res.isNull(1)) {
                        count = Integer.parseInt(res.getString(1));
                    }
                    publishProgress(0,3);
                    res.close();
                }
                catch (Exception e){
                    e.printStackTrace();
                    publishProgress(0,4);
                }
            }

            //Task handling for fab press
            else if(m==1){
                Dialog d = (Dialog) params[2];
                View view = (View) params[1];
                TextView t = (TextView) d.findViewById(R.id.count);
                publishProgress(1,1,t);

                SQLiteDatabase db = openOrCreateDatabase("data",MODE_PRIVATE,null);

                Date date = new Date();
                String tableName = getTableName(date.getMonth()+1);
                db.execSQL("update "+tableName+" set Count = ? where Day = ?",new String[]{count+"",date.getDate()+""});

                publishProgress(1,3,view);
                publishProgress(1,4,d);
                publishProgress(1,2);
            }

            //Initial DB Creation task handling
            else if(m==99){
                SQLiteDatabase trackdb = openOrCreateDatabase("data",MODE_PRIVATE,null);

                trackdb.execSQL("CREATE TABLE IF NOT EXISTS January17(Day int, Count int NOT NULL)");
                for(int i=1;i<31;i++){
                    trackdb.execSQL("INSERT INTO January17 VALUES("+i+",0)");
                }

                Cursor res = trackdb.rawQuery("SELECT * FROM January17 where Day = 1",null);
                res.moveToFirst();

                System.out.println("Jan 1: "+res.getString(1));
                res.close();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Object[] values) {
            int m = (int)values[0];
            if(m==0){
                int task = (int) values[1];
                switch (task){
                    case 1:
                        fab.setVisibility(View.INVISIBLE);
                        break;
                    case 2:
                        fab.setVisibility(View.VISIBLE);
                        break;
                    case 3:
                        counts.setText(count + "");
                        break;
                    case 4:
                        counts.setText(0+"");
                        break;
                }
            }
            else if(m==1){
                int task = (int) values[1];
                switch (task){
                    case 1:
                        TextView t = (TextView) values[2];
                        count = Integer.parseInt(t.getText()+"");
                        break;
                    case 2:
                        counts.setText(count+"");
                        break;
                    case 3:
                        View view = (View) values[2];
                        if(count>10) {
                            Snackbar.make(view, "Keep it up!", Snackbar.LENGTH_LONG)
                                    .setAction("Keep it up!", null).show();
                        }
                        else {
                            Snackbar.make(view, "Dont lose track! Keep doing push ups!!",Snackbar.LENGTH_LONG)
                                    .setAction("Dont lose track! Keep doing push ups!!",null).show();
                        }
                        break;
                    case 4:
                        Dialog d = (Dialog) values[2];
                        d.hide();
                }
            }
        }
    }
}
