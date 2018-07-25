package com.example.owner.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

public class SOC_Edit extends AppCompatActivity {
    static final String username = "ECASystem";
    static final String password = "ECASystem";
    private int A1=0;
    private String AccEditToString="";
    static Connection conn;
    static Statement statement;
    static ResultSet rs,updateRS;

    TextView ser;
    EditText name;
    EditText sta;
    TextView rpser;
    Button ConfirmBtn;
    Button CancelBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soc__edit);
        StrictMode.ThreadPolicy policy =
                new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        overridePendingTransition(0,0);

        ser = (TextView)findViewById(R.id.SOC_Edit_SerText);
        name = (EditText)findViewById(R.id.SOC_Edit_NameEdit);
        sta = (EditText)findViewById(R.id.SOC_Edit_StaEdit);
        rpser = (TextView)findViewById(R.id.SOC_Edit_RaspSerialText);
        ConfirmBtn = (Button)findViewById(R.id.SOC_Edit_ConfirmBtn);
        ConfirmBtn.setOnClickListener(toConfirm);
        CancelBtn = (Button)findViewById(R.id.SOC_Edit_CancelBtn);
        CancelBtn.setOnClickListener(toCancel);

        Intent intent = getIntent();
        AccEditToString = intent.getStringExtra(     //接
                "AccEditToString");
        A1=intent.getIntExtra("A1",0);
        String serial = intent.getStringExtra("SOC_Serial");   //序號
        ser.setText(serial);
        Intent intent2 = getIntent();
        String namename = intent2.getStringExtra("SOC_Name");  //名稱
        name.setText(namename);
        Intent intent3 = getIntent();
        String stasta = intent3.getStringExtra("SOC_Sta");  //是否開啟
        sta.setText(stasta);
        Intent intent6 = getIntent();
        Integer rpserpser = intent6.getIntExtra("SOC_RaspSerial",0);   //對應樹莓派
        rpser.setText(rpserpser.toString());

        BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBarSE);
        bottomBar.setDefaultTab(R.id.tab_soc);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(int tabId) {

                switch (tabId) {
                    case R.id.tab_rasp:
                        Intent intentR = new Intent();
                        intentR.setClass(SOC_Edit.this,
                                RP_List.class);
                        intentR.putExtra("A1", A1);
                        intentR.putExtra("AccEditToString", AccEditToString);  //放
                        startActivity(intentR);
                        finish();
                        break;
                    case R.id.tab_ard:
                        Intent intentA = new Intent();
                        intentA.setClass(SOC_Edit.this,
                                AD_List.class);
                        intentA.putExtra("A1", A1);
                        intentA.putExtra("AccEditToString", AccEditToString);  //放
                        startActivity(intentA);
                        finish();
                        break;
                    case R.id.tab_eca:
                        Intent intentE = new Intent();
                        intentE.setClass(SOC_Edit.this,
                                ECA_List.class);
                        intentE.putExtra("A1", A1);
                        intentE.putExtra("AccEditToString", AccEditToString);  //放
                        startActivity(intentE);
                        finish();
                        break;
                    case R.id.tab_dev:
                        Intent intentD = new Intent();
                        intentD.setClass(SOC_Edit.this,
                                DEV_List.class);
                        intentD.putExtra("A1", A1);
                        intentD.putExtra("AccEditToString", AccEditToString);  //放
                        startActivity(intentD);
                        finish();
                        break;
                }
            }
        });

        bottomBar.setOnTabReselectListener(new OnTabReselectListener() {
            @Override
            public void onTabReSelected(@IdRes int tabId) {
                Intent intent = new Intent();
                intent.setClass(SOC_Edit.this,
                        SOC_List.class);
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
                    String sstt = sta.getText().toString();
                    String rr = rpser.getText().toString();

                    int empty=0;
                    if("".equals(nn .toString().trim())){empty++;}
                    if("".equals(sstt .toString().trim())){empty++;}
                    if("".equals(rr .toString().trim())){empty++;}

                    //沒有空值，才執行
                    if(empty==0) {

                        String updatesql = "UPDATE SOCKET SET SOC_Name='" + nn + "',  SOC_Sta='" + sstt + "',   SOC_RaspSerial='" + rr + "' WHERE SOC_Serial = '" + ss + "' ";
                        statement.executeUpdate(updatesql);
                        Toast.makeText(SOC_Edit.this, "插座資料編輯成功", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent();   //新增完RP回列表
                        intent.setClass(SOC_Edit.this,
                                SOC_List.class);
                        intent.putExtra("A1", A1);
                        intent.putExtra("AccEditToString", AccEditToString);
                        startActivity(intent);

                    }else{
                        Toast.makeText(SOC_Edit.this, "不可以有空白欄位", Toast.LENGTH_SHORT).show();
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
//                    Class.forName("org.mariadb.jdbc.Driver").newInstance();
                    try {
//                    final String URL = "jdbc:mariadb://120.105.161.89/ECASystem";
//                    System.out.println(URL);
//                    java.sql.Connection conn = DriverManager.getConnection(URL, username, password); //呼叫Connection物件，進行資料庫連線
                    statement = Login.statement;
                    System.out.println("資料庫連結成功");
                    Log.e("TEST","資料庫連結成功");

                    String ss2 = ser.getText().toString();

                    String updatesql = "DELETE FROM SOCKET WHERE SOC_Serial = '" + ss2 + "' ";
                    statement.executeUpdate(updatesql);
                    Toast.makeText(SOC_Edit.this, "插座刪除成功", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent();   //刪除完RP回列表
                    intent.setClass(SOC_Edit.this,
                            SOC_List.class);
                    intent.putExtra("A1", A1);
                    intent.putExtra("AccEditToString", AccEditToString);
                    startActivity(intent);
                } catch (SQLException e) {
                    Toast.makeText(SOC_Edit.this, "該插座被設備使用中！", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
//            } catch (Exception k) {
//                k.printStackTrace();
//            }

        }
    };



    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.UserInfo_title:
                Toast.makeText(this, "使用者資料", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setClass(this,User.class);
                intent.putExtra("A1", A1);
                intent.putExtra("AccEditToString", AccEditToString);
                startActivity(intent);
                finish();
                return true;
            case R.id.Explain_title:
                Toast.makeText(this, "說明", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.LogOut_title:
                Toast.makeText(this, "已登出", Toast.LENGTH_SHORT).show();
                intent = new Intent();
                intent.setClass(this,Login.class);
                intent.putExtra("A1", A1);
                intent.putExtra("AccEditToString", AccEditToString);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
