package personal.td7.com.pushupstracker;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tracker);

        Date date = new Date();
        if(date.getDate() == 1){
            createNewMonthsTable();
        }

        View v = findViewById(R.id.mainContent);

        Button btn = (Button) v.findViewById(R.id.showStats);

        System.out.println("Set onclick for btn");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Pressed");
                Intent in = new Intent(getApplicationContext(),Stats.class);
                startActivity(in);
            }
        });

        final TextView counts = (TextView) findViewById(R.id.displayCount);
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        CalendarView c = (CalendarView) findViewById(R.id.cal);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                final Dialog d = new Dialog(Tracker.this);
                d.setContentView(R.layout.edit_count);
                d.findViewById(R.id.reg).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TextView t = (TextView) d.findViewById(R.id.count);
                        count = Integer.parseInt(t.getText()+"");

                        SQLiteDatabase db = openOrCreateDatabase("data",MODE_PRIVATE,null);

                        Date date = new Date();
                        String tableName = getTableName(date.getMonth()+1);
                        db.execSQL("update "+tableName+" set Count = ? where Day = ?",new String[]{count+"",date.getDate()+""});

                        if(count>10) {
                            Snackbar.make(view, "Keep it up!", Snackbar.LENGTH_LONG)
                                    .setAction("Keep it up!", null).show();
                        }
                        else {
                            Snackbar.make(view, "Dont lose track! Keep doing push ups!!",Snackbar.LENGTH_LONG)
                                    .setAction("Dont lose track! Keep doing push ups!!",null).show();
                        }
                        d.hide();
                        counts.setText(count+"");
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
                try {
                    Date date = new Date();
                    if (dayOfMonth != date.getDate()) {
                        fab.setVisibility(View.INVISIBLE);
                    } else fab.setVisibility(View.VISIBLE);
                    SQLiteDatabase db = openOrCreateDatabase("data", MODE_PRIVATE, null);

                    String tableName = getTableName(month + 1);
                    Cursor res = db.rawQuery("select * from " + tableName + " where Day = " + dayOfMonth, null);
                    res.moveToFirst();
                    count = 0;
                    if (!res.isNull(1)) {
                        count = Integer.parseInt(res.getString(1));
                    }
                    counts.setText(count + "");
                    res.close();
                }
                catch (Exception e){
                    e.printStackTrace();
                    counts.setText(0+"");
                }
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
}
