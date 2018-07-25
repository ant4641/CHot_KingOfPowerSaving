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
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DEV_New extends AppCompatActivity {
    static final String username = "ECASystem";
    static final String password = "ECASystem";
    private int A1=0;
    private String AccEditToString="";
    static Connection conn;
    static Statement statement;
    static ResultSet rs,updateRS;

    EditText NameEdit;
    EditText SocEdit;
    FloatingActionButton ConfirmBtn;

    String sql_NameEdit=""; //假設為資料庫
    String sql_SocEdit="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev__new);
        StrictMode.ThreadPolicy policy =
                new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        overridePendingTransition(0, 0);
        Intent intent = getIntent();
        AccEditToString = intent.getStringExtra(     //接
                "AccEditToString");
        A1=intent.getIntExtra("A1",0);

        NameEdit = (EditText)findViewById(R.id.DEV_New_NameEdit);
        SocEdit = (EditText)findViewById(R.id.DEV_New_SocEdit);
        ConfirmBtn = (FloatingActionButton)findViewById(R.id.DEV_New_ConfirmBtn);
        ConfirmBtn.setOnClickListener(toConfirmBtn);

        BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBarDN);
        bottomBar.setDefaultTab(R.id.tab_dev);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                switch (tabId) {
                    case R.id.tab_rasp:
                        Intent intentR = new Intent();
                        intentR.setClass(DEV_New.this,
                                RP_List.class);
                        intentR.putExtra("A1", A1);
                        intentR.putExtra("AccEditToString", AccEditToString);  //放
                        startActivity(intentR);
                        finish();
                        break;
                    case R.id.tab_ard:
                        Intent intentA = new Intent();
                        intentA.setClass(DEV_New.this,
                                AD_List.class);
                        intentA.putExtra("A1", A1);
                        intentA.putExtra("AccEditToString", AccEditToString);  //放
                        startActivity(intentA);
                        finish();
                        break;
                    case R.id.tab_soc:
                        Intent intentS = new Intent();
                        intentS.setClass(DEV_New.this,
                                SOC_List.class);
                        intentS.putExtra("A1", A1);
                        intentS.putExtra("AccEditToString", AccEditToString);  //放
                        startActivity(intentS);
                        finish();
                        break;
                    case R.id.tab_eca:
                        Intent intentD = new Intent();
                        intentD.setClass(DEV_New.this,
                                ECA_List.class);
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
                intent.setClass(DEV_New.this,
                        DEV_List.class);
                intent.putExtra("A1", A1);
                intent.putExtra("AccEditToString", AccEditToString);  //放
                startActivity(intent);
            }
        });
    }

    private View.OnClickListener toConfirmBtn = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//            try {
//                Class.forName("org.mariadb.jdbc.Driver").newInstance();
                //try {
//                    final String URL = "jdbc:mariadb://120.105.161.89/ECASystem";
//                    System.out.println(URL);
//                    java.sql.Connection conn = DriverManager.getConnection(URL, username, password); //呼叫Connection物件，進行資料庫連線
                    statement = Login.statement;
                    System.out.println("資料庫連結成功");
                    Log.e("TEST","資料庫連結成功");
                    //Toast.makeText(RP_New.this, "資料庫連結成功",Toast.LENGTH_SHORT).show();

                    String NameEdit_str = NameEdit.getText().toString();
                    String SocEdit_str = SocEdit.getText().toString();

                    int empty=0;
                    if("".equals(NameEdit_str .toString().trim())){empty++;}
                    if("".equals(SocEdit_str .toString().trim())){empty++;}
                    //沒有空值，才執行
                    if(empty==0){
                        int q=0;
                        sql_NameEdit=NameEdit_str;
                        sql_SocEdit=SocEdit_str;
                        String sql = "INSERT INTO DEVICE(DEV_Name,DEV_SocSerial)VALUES('" + sql_NameEdit + "',(SELECT SOC_Serial FROM SOCKET WHERE SOC_Serial='" + sql_SocEdit + "'))";
                        System.out.println(sql);
                        try {
                            statement.executeUpdate(sql);
                            System.out.println("sqlcheck= " + sql);
                            Toast.makeText(DEV_New.this, "設備新增成功", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent();
                            intent.setClass(DEV_New.this,    //新增完DEV回列表
                                    DEV_List.class);
                            startActivity(intent);
                            finish();

                            intent.setClass(DEV_New.this,
                                    DEV_List.class);
                            intent.putExtra("A1", A1);
                            intent.putExtra("AccEditToString", AccEditToString);  //放
                            startActivity(intent);

                        }catch (SQLException e) {
                            System.out.println("seee= " + e);
                            Toast.makeText(DEV_New.this, "對應插座輸入錯誤", Toast.LENGTH_SHORT).show();
                        }

                    }else{
                        Toast.makeText(DEV_New.this, "不可以有空白欄位",Toast.LENGTH_SHORT).show();
                    }

//                    statement.close();
//                    conn.close();
//
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
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
