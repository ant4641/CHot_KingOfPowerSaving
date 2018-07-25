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

public class RP_Edit extends AppCompatActivity {
    static final String username = "ECASystem";
    static final String password = "ECASystem";
    private int A1=0;
    private String AccEditToString="";
    static Connection conn;
    static Statement statement;
    static ResultSet rs,updateRS;

    TextView ser;
    EditText name;
    EditText loc;
    Button ConfirmBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rp__edit);
        StrictMode.ThreadPolicy policy =
                new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        overridePendingTransition(0,0);

        ser = (TextView)findViewById(R.id.RP_Edit_SerText);
        name = (EditText)findViewById(R.id.RP_Edit_NameEdit);
        loc = (EditText)findViewById(R.id.RP_Edit_LocEdit);
        ConfirmBtn = (Button)findViewById(R.id.RP_Edit_ConfirmBtn);
        ConfirmBtn.setOnClickListener(toConfirm);

        Intent intent = getIntent();
        Integer serial = intent.getIntExtra("RASP_Serial",0);   //序號
        ser.setText(serial.toString());
        Intent intent2 = getIntent();
        String namename = intent2.getStringExtra("RASP_Name");  //名稱
        name.setText(namename);
        Intent intent3 = getIntent();
        String locloc = intent3.getStringExtra("RASP_Place");   //場所
        loc.setText(locloc);
        AccEditToString = intent.getStringExtra(     //接
                "AccEditToString");
        A1=intent.getIntExtra("A1",0);

        BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                switch (tabId) {
                    case R.id.tab_ard:
                        Intent intentA = new Intent();
                        intentA.setClass(RP_Edit.this,
                                AD_List.class);
                        intentA.putExtra("A1", A1);
                        intentA.putExtra("AccEditToString", AccEditToString);  //放
                        startActivity(intentA);
                        finish();
                        break;
                    case R.id.tab_soc:
                        Intent intentS = new Intent();
                        intentS.setClass(RP_Edit.this,
                                SOC_List.class);
                        intentS.putExtra("A1", A1);
                        intentS.putExtra("AccEditToString", AccEditToString);  //放
                        startActivity(intentS);
                        finish();
                        break;
                    case R.id.tab_eca:
                        Intent intentE = new Intent();
                        intentE.setClass(RP_Edit.this,
                                ECA_List.class);
                        intentE.putExtra("A1", A1);
                        intentE.putExtra("AccEditToString", AccEditToString);  //放
                        startActivity(intentE);
                        finish();
                        break;
                    case R.id.tab_dev:
                        Intent intentD = new Intent();
                        intentD.setClass(RP_Edit.this,
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
                intent.setClass(RP_Edit.this,
                        RP_List.class);
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
                    statement = RP_List.statement;
                    System.out.println("資料庫連結成功");
                    Log.e("TEST","資料庫連結成功");

                    String ss = ser.getText().toString();
                    String nn = name.getText().toString();
                    String ll = loc.getText().toString();

                    int empty=0;
                    if("".equals(nn .toString().trim())){empty++;}
                    if("".equals(ll .toString().trim())){empty++;}
                    //沒有空值，才執行
                    if(empty==0) {

                        String updatesql = "UPDATE RASP SET RASP_Name='" + nn + "', RASP_Place='" + ll + "'  WHERE RASP_Serial = '" + ss + "' ";
                        statement.executeUpdate(updatesql);
                        Toast.makeText(RP_Edit.this, "樹莓派資料編輯成功", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent();
                        intent.setClass(RP_Edit.this,
                                RP_List.class);
                        intent.putExtra("A1", A1);
                        intent.putExtra("AccEditToString", AccEditToString);  //放
                        startActivity(intent);
                        finish();
                    }else{
                        Toast.makeText(RP_Edit.this, "不可以有空白欄位", Toast.LENGTH_SHORT).show();
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
