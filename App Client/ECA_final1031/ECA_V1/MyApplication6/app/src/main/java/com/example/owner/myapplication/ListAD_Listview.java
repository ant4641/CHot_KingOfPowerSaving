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

public class ListAD_Listview extends ArrayAdapter<String> {
    private final Activity context;
    int ord=1;
    private int A1=0;
    private String AccEditToString="";
    ArrayList<Integer> order = new ArrayList<>();
    ArrayList<String> Ser;
    ArrayList<String> Name;
    ArrayList<String> Type;
    ArrayList<Integer> Status;
    ArrayList<String> Loc;
    ArrayList<String> Det;
    ArrayList<Integer> RPSer;

    private static Connection conn;
    private static Statement stmt;
    private static ResultSet rs;

    public ListAD_Listview(Activity context, ArrayList<String> rSer, ArrayList<String> rName, ArrayList<String> rType, ArrayList<Integer> rStatus, ArrayList<String> rLoc, ArrayList<String> rDet, ArrayList<Integer> rRPSer, Connection c, int a1, String acc) {
        super(context, R.layout.listad_listview, rName);
        this.context = context;
        this.Ser = rSer;
        this.Name = rName;
        this.Type = rType;
        this.Status = rStatus;
        this.Loc = rLoc;
        this.Det = rDet;
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
        View rowView= inflater.inflate(R.layout.listad_listview, null, true);
        if(!Name.get(position).equals("0")) {
            order.add(position,ord);

            TextView ser = (TextView) rowView.findViewById(R.id.list_ser);
            TextView name = (TextView) rowView.findViewById(R.id.list_name);
            TextView type = (TextView) rowView.findViewById(R.id.list_type);
            TextView status = (TextView) rowView.findViewById(R.id.list_status);
            TextView loc = (TextView) rowView.findViewById(R.id.list_loc);
            TextView det = (TextView) rowView.findViewById(R.id.list_det);
            TextView rpser = (TextView) rowView.findViewById(R.id.list_rpser);


            ser.setText(Ser.get(position));
            name.setText(Name.get(position));
            String typeC="";
            switch(Type.get(position)){
                case"celsius":
                    typeC="溫度";
                    break;
                case"time":
                    typeC="到達時間";
                    break;
                case"pass":
                    typeC="經過時間";
                    break;
                case"infra":
                    typeC="有無人在";
                    break;
                case"wet":
                    typeC="濕度";
                    break;
                case"light":
                    typeC="亮度";
                    break;
            }

            type.setText(typeC);
            System.out.println("ord="+ord+"  Name="+Name.get(position).toString());
            if (Status.get(position)==1) {
                status.setText("待機");
            }else{
                status.setText("運行");
            }
            loc.setText(Loc.get(position));
            det.setText(Det.get(position));
            rpser.setText(RPSer.get(position).toString());

            Button b = (Button) rowView.findViewById(R.id.button);
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(context, AD_Edit.class);
                    intent.putExtra("A1", A1);
                    intent.putExtra("AccEditToString", AccEditToString);
                    intent.putExtra("ARD_Serial", Ser.get(position));
                    intent.putExtra("ARD_Name", Name.get(position));
                    intent.putExtra("ARD_Type", Type.get(position));
                    intent.putExtra("ARD_Status", Status.get(position));
                    intent.putExtra("ARD_Loc", Loc.get(position));
                    intent.putExtra("ARD_Det", Det.get(position));
                    intent.putExtra("ARD_RaspSerial", RPSer.get(position));
                    context.startActivity(intent);
                }
            });
            ord++;
        }
        return rowView;
    }
}
