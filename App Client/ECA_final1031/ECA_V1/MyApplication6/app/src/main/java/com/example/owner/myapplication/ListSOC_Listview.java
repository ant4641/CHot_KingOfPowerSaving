package com.example.owner.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Created by Owner on 2017/9/19.
 */

public class ListSOC_Listview extends ArrayAdapter<String> {
    private final Activity context;
    int ord=1;
    private int A1=0;
    private String AccEditToString="";
    ArrayList<Integer> order = new ArrayList<>();
    ArrayList<String> Ser;
    ArrayList<String> Name;
    ArrayList<String> Sta;
    ArrayList<Integer> RPSer;

    private static Connection conn;
    private static Statement stmt;
    private static ResultSet rs;

    public ListSOC_Listview(Activity context, ArrayList<String> rSer, ArrayList<String> rName, ArrayList<String> rSta, ArrayList<Integer> rRPSer, Connection c, int a1, String acc) {
        super(context, R.layout.listsoc_listview, rName);
        this.context = context;
        this.Ser = rSer;
        this.Name = rName;
        this.Sta = rSta;
        this.RPSer = rRPSer;
        this.A1=a1;
        this.AccEditToString=acc;
        conn=c;
        System.out.println("pos.size()"+rName.size());
    }
    void setAdapter(Activity context,
                    ArrayList<String> p, ArrayList<Integer> i){
        this.Name = p;
        System.out.println("pos.size()"+Name.size());
    }


    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.listsoc_listview, null, true);

        if(!Name.get(position).equals("0")) {
            order.add(position,ord);

            TextView ser = (TextView) rowView.findViewById(R.id.list_ser);
            TextView name = (TextView) rowView.findViewById(R.id.list_name);
            TextView sta = (TextView) rowView.findViewById(R.id.list_sta);
            TextView rpser = (TextView) rowView.findViewById(R.id.list_rpser);

            ser.setText(Ser.get(position));
            name.setText(Name.get(position));
            sta.setText(Sta.get(position));
            rpser.setText(RPSer.get(position).toString());

            Button b = (Button) rowView.findViewById(R.id.button);
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("press---------------------"+position);
                    Intent intent = new Intent();
                    intent.setClass(context, SOC_Edit.class);
                    intent.putExtra("A1", A1);
                    intent.putExtra("AccEditToString", AccEditToString);
                    intent.putExtra("SOC_Serial", Ser.get(position));
                    intent.putExtra("SOC_Name", Name.get(position));
                    intent.putExtra("SOC_Sta", Sta.get(position));
                    intent.putExtra("SOC_RaspSerial", RPSer.get(position));
                    context.startActivity(intent);

                }
            });

            ord++;
        }

        return rowView;
    }
}
