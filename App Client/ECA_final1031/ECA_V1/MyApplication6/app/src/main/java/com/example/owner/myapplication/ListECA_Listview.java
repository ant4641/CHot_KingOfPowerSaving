package com.example.owner.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Created by Owner on 2017/9/19.
 */

public class ListECA_Listview extends ArrayAdapter<String>{
    static final String username = "ECASystem";
    static final String password = "ECASystem";

    private final Activity context;
    int ord=1;
    private int A1=0;
    private String AccEditToString="";
    ArrayList<Integer> order = new ArrayList<>();
    ArrayList<Integer> Ser;
    ArrayList<Integer> Devser;
    ArrayList<String> Con;
    ArrayList<String> Ps;
    ArrayList<Integer> RPSer;

    private static Connection conn;
    private static Statement statement;
    private static ResultSet rs;

    public ListECA_Listview(Activity context, ArrayList<Integer> rSer, ArrayList<Integer> rDevser, ArrayList<String> rCon, ArrayList<String> rPs, ArrayList<Integer> rRPSer, Connection c, int a1, String acc) {
        super(context, R.layout.listeca_listview, rCon);
        this.context = context;
        this.Ser = rSer;
        this.Devser = rDevser;
        this.Con = rCon;
        this.Ps = rPs;
        this.RPSer = rRPSer;
        this.A1=a1;
        this.AccEditToString=acc;
        conn=c;
        System.out.println("pos.size()"+rCon.size());
    }
    void setAdapter(Activity context,
                    ArrayList<String> p, ArrayList<Integer> i){
        this.Con = p;
        System.out.println("pos.size()"+Con.size());
    }


    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.listeca_listview, null, true);

        if(!Con.get(position).equals("0")) {
            order.add(position,ord);

            TextView ser = (TextView) rowView.findViewById(R.id.list_ser);
            TextView devser = (TextView) rowView.findViewById(R.id.list_devser);
            TextView con = (TextView) rowView.findViewById(R.id.list_con);
            //TextView ps = (TextView) rowView.findViewById(R.id.list_ps);
            TextView rpser = (TextView) rowView.findViewById(R.id.list_rpser);

            ser.setText(Ser.get(position).toString());

            String DEVName="";
            try {
                try {
                    statement = Login.statement;
                    System.out.println("資料庫連結成功");
                    String sqlrs="select DEV_Name from  DEVICE WHERE DEV_Serial= '" + Devser.get(position).toString() + "'";
                    rs=statement.executeQuery(sqlrs);
                    System.out.println(sqlrs);
                    while(rs.next()){
                        DEVName=rs.getString("DEV_Name");
                    }
                    System.out.println(sqlrs);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } catch (Exception k) {
                k.printStackTrace();
            }

            devser.setText(DEVName);
            String []cons=Con.get(position).split("/");
            String typeC="";
            switch (cons[0]){
                case "celsius":
                    typeC="若"+cons[1]+"的溫度低於"+cons[3]+"度";
                    break;
                case "time":
                    String []times=cons[2].split(":");
                    typeC="若時間為"+times[0]+"點"+times[1]+"分的時候";
                    break;
                case "pass":
                    String []timesP=cons[2].split(":");
                    typeC="若經過"+timesP[0]+"分"+timesP[1]+"秒";
                    break;
                case "infra":
                    typeC="若沒有人在"+cons[1];
                    break;
                case "wet":
                    typeC="若"+cons[1]+"的濕度低於"+cons[3]+"度";
                    break;
                case "light":
                    String strong="";
                    if(cons[3].equals("900")){
                        strong="很強";
                    }else if(cons[3].equals("700")){
                        strong="稍強";
                    }else if(cons[3].equals("500")){
                        strong="適中";
                    }
                    typeC="若"+cons[1]+"的亮度"+strong;
                    break;
            }
            con.setText(typeC);

            //ps.setText(Ps.get(position));
            rpser.setText(RPSer.get(position).toString());

            Button b = (Button) rowView.findViewById(R.id.button);
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        try {
                            statement = Login.statement;
                            System.out.println("資料庫連結成功");

                            int re = 0;
                            String sqlrs="Delete from ECA WHERE ECA_Num = '" + Ser.get(position) + "'";
                            statement.executeUpdate(sqlrs);
                            System.out.println(sqlrs);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    } catch (Exception k) {
                        k.printStackTrace();
                    }
                    System.out.println("press---------------------"+position);
                    Intent intent = new Intent();
                    intent.setClass(context,
                            ECA_List.class);
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
