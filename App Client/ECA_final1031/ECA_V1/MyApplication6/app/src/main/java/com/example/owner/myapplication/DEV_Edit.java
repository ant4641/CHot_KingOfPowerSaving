package com.example.owner.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DEV_Edit extends AppCompatActivity {
    static final String username = "ECASystem";
    static final String password = "ECASystem";
    private int A1=0;
    private String AccEditToString="";
    static Connection conn;
    static Statement statement;
    static ResultSet rs,updateRS;

    TextView ser;
    EditText name;
    TextView socser;
    Button ConfirmBtn;
    Button CancelBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev__edit);
        StrictMode.ThreadPolicy policy =
                new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        overridePendingTransition(0, 0);

        ser = (TextView)findViewById(R.id.DEV_Edit_SerText);
        name = (EditText)findViewById(R.id.DEV_Edit_NameEdit);
        socser = (TextView)findViewById(R.id.DEV_Edit_SocSerialText);
        ConfirmBtn = (Button)findViewById(R.id.DEV_Edit_ConfirmBtn);
        ConfirmBtn.setOnClickListener(toConfirm);
        CancelBtn = (Button)findViewById(R.id.DEV_Edit_CancelBtn);
        CancelBtn.setOnClickListener(toCancel);

        Intent intent = getIntent();
        intent = getIntent();
        AccEditToString = intent.getStringExtra(     //接
                "AccEditToString");
        A1 = intent.getIntExtra("A1", 0);
        Integer serial = intent.getIntExtra("DEV_Serial",1);   //序號
        ser.setText(serial.toString());
        Intent intent2 = getIntent();
        String namename = intent2.getStringExtra("DEV_Name");  //名稱
        name.setText(namename);
        Intent intent6 = getIntent();
        String socsersocser = intent6.getStringExtra("DEV_SocSerial");   //對應插座
        socser.setText(socsersocser);


        BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBarDE);
        bottomBar.setDefaultTab(R.id.tab_dev);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(int tabId) {
                switch (tabId) {
                    case R.id.tab_rasp:
                        Intent intentR = new Intent();
                        intentR.setClass(DEV_Edit.this,
                                RP_List.class);
                        intentR.putExtra("A1", A1);
                        intentR.putExtra("AccEditToString", AccEditToString);  //放
                        startActivity(intentR);
                        finish();
                        break;
                    case R.id.tab_ard:
                        Intent intentD = new Intent();
                        intentD.setClass(DEV_Edit.this,
                                AD_List.class);
                        intentD.putExtra("A1", A1);
                        intentD.putExtra("AccEditToString", AccEditToString);  //放
                        startActivity(intentD);
                        finish();
                        break;
                    case R.id.tab_soc:
                        Intent intentS = new Intent();
                        intentS.setClass(DEV_Edit.this,
                                SOC_List.class);
                        intentS.putExtra("A1", A1);
                        intentS.putExtra("AccEditToString", AccEditToString);  //放
                        startActivity(intentS);
                        finish();
                        break;
                    case R.id.tab_eca:
                        Intent intentE = new Intent();
                        intentE.setClass(DEV_Edit.this,
                                ECA_List.class);
                        intentE.putExtra("A1", A1);
                        intentE.putExtra("AccEditToString", AccEditToString);  //放
                        startActivity(intentE);
                        finish();
                        break;
                }
            }
        });

        bottomBar.setOnTabReselectListener(new OnTabReselectListener() {
            @Override
            public void onTabReSelected(@IdRes int tabId) {
                Intent intent = new Intent();
                intent.setClass(DEV_Edit.this,
                        DEV_List.class);
                intent.putExtra("A1", A1);
                intent.putExtra("AccEditToString", AccEditToString);  //放
                startActivity(intent);
            }
        });
    }

    private View.OnClickListener toConfirm = new View.OnClickListener() {
        public void onClick(View v) {
//            try {
//                Class.forName("org.mariadb.jdbc.Driver").newInstance();
                try {
//                    final String URL = "jdbc:mariadb://120.105.161.89/ECASystem";
//                    System.out.println(URL);
//                    java.sql.Connection conn = DriverManager.getConnection(URL, username, password); //呼叫Connection物件，進行資料庫連線
                    statement = Login.statement;
                    System.out.println("資料庫連結成功");
                    Log.e("TEST","資料庫連結成功");

                    String ss = ser.getText().toString();
                    String nn = name.getText().toString();
                    String rr = socser.getText().toString();

                    int empty=0;
                    if("".equals(nn .toString().trim())){empty++;}
                    if("".equals(rr .toString().trim())){empty++;}

                    //沒有空值，才執行
                    if(empty==0) {
                        Toast.makeText(DEV_Edit.this, nn, Toast.LENGTH_SHORT).show();
                        String updatesql = "UPDATE DEVICE SET DEV_Name='" + nn + "' WHERE DEV_Serial = '" + ss + "' ";
                        System.out.println(updatesql);
                        statement.executeUpdate(updatesql);
                        Toast.makeText(DEV_Edit.this, "設備編輯成功", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent();   //新增完插座回列表
                        intent.setClass(DEV_Edit.this,
                                DEV_List.class);
                        intent.putExtra("A1", A1);
                        intent.putExtra("AccEditToString", AccEditToString);
                        startActivity(intent);

                    }else{
                        Toast.makeText(DEV_Edit.this, "不可以有空白欄位", Toast.LENGTH_SHORT).show();
                    }

//                    statement.close();
//                    conn.close();

                } catch (SQLException e) {
                    e.printStackTrace();
                }
//            } catch (Exception k) {
//                k.printStackTrace();
//            }

        }
    };


    private View.OnClickListener toCancel = new View.OnClickListener() {
        public void onClick(View v) {
//            try {
//                Class.forName("org.mariadb.jdbc.Driver").newInstance();
                try {
//                    final String URL = "jdbc:mariadb://120.105.161.89/ECASystem";
//                    System.out.println(URL);
//                    java.sql.Connection conn = DriverManager.getConnection(URL, username, password); //呼叫Connection物件，進行資料庫連線
                    statement = Login.statement;
                    System.out.println("資料庫連結成功");
                    Log.e("TEST","資料庫連結成功");

                    String ss2 = ser.getText().toString();

                    String updatesql = "DELETE FROM DEVICE WHERE DEV_Serial = '" + ss2 + "' ";
                    statement.executeUpdate(updatesql);
                    Toast.makeText(DEV_Edit.this, "設備刪除成功", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent();   //刪除完插座回列表
                    intent.setClass(DEV_Edit.this,
                            DEV_List.class);
                    intent.putExtra("A1", A1);
                    intent.putExtra("AccEditToString", AccEditToString);
                    startActivity(intent);

//                    statement.close();
//                    conn.close();

                } catch (SQLException e) {
                    e.printStackTrace();
                }
//            } catch (Exception k) {
//                k.printStackTrace();
//            }

        }
    };
}
