package personal.td7.com.pushupstracker;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.Vector;

public class StatAdapter extends ArrayAdapter {

    Context c;
    int layout;
    Vector<Stats.Data> data;

    StatAdapter(Context c,int layout, Vector<Stats.Data> data){
        super(c,layout,data);
        this.c = c;
        this.layout = layout;
        this.data = data;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        PlaceHolder p;
        if(v==null){
            LayoutInflater i = LayoutInflater.from(c);
            v = i.inflate(layout,parent,false);

            p = new PlaceHolder();

            p.dayCount = (TextView) v.findViewById(R.id.dayCount);
            p.dayView = (TextView) v.findViewById(R.id.day);

            v.setTag(p);
        }
        else{
            p = (PlaceHolder) v.getTag();
        }

        Stats.Data d = data.get(position);
        p.dayView.setText(d.day);
        p.dayCount.setText(d.dayCount+"");

        return v;
    }

    private class PlaceHolder{
        TextView dayView;
        TextView dayCount;
    }
}
