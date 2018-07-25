package com.example.owner.myapplication;

import android.app.Activity;
import android.content.Context;
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
 * Created by Owner on 2017/9/6.
 */

public class ListRP_Listview extends ArrayAdapter<String> {
    private final Activity context;
    private int A1=0;
    private String AccEditToString="";
    int ord=1;
    ArrayList<Integer> order = new ArrayList<>();
    ArrayList<Integer> Ser;
    ArrayList<String> Name;
    ArrayList<String> Loc;
    Context t;

    private static Connection conn;
    private static Statement stmt;
    private static ResultSet rs;

    public ListRP_Listview(Activity context, ArrayList<Integer> rSer, ArrayList<String> rName, ArrayList<String> rLoc, Connection c, int a1,String acc) {
        super(context, R.layout.listrp_listview, rName);
        this.context = context;
        this.Ser = rSer;
        this.Name = rName;
        this.Loc = rLoc;
        AccEditToString = acc;
        A1=a1;
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
        View rowView= inflater.inflate(R.layout.listrp_listview, null, true);


        if(!Name.get(position).equals("0")) {
            order.add(position,ord);

            TextView ser = (TextView) rowView.findViewById(R.id.list_ser);
            TextView name = (TextView) rowView.findViewById(R.id.list_name);
            TextView loc = (TextView) rowView.findViewById(R.id.list_loc);

            ser.setText(Ser.get(position).toString());
            name.setText(Name.get(position));
            System.out.println("ord="+ord+"  Name="+Name.get(position));
            loc.setText(Loc.get(position));


            Button b = (Button) rowView.findViewById(R.id.button);
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("press---------------------"+position);

                    Intent intent = new Intent();
                    intent.setClass(context, RP_Edit.class);
                    intent.putExtra("RASP_Serial", Ser.get(position));
                    intent.putExtra("RASP_Name", Name.get(position));
                    intent.putExtra("RASP_Place", Loc.get(position));
                    intent.putExtra("A1", A1);
                    intent.putExtra("AccEditToString", AccEditToString);  //放到A1裡
                    context.startActivity(intent);
                }
            });
            ord++;
        }
        return rowView;
    }
}
