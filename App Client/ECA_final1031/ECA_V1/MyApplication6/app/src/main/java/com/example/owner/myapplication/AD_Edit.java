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

public class AD_Edit extends AppCompatActivity {
    static final String username = "ECASystem";
    static final String password = "ECASystem";
    private int A1=0;
    private String AccEditToString="";
    static Connection conn;
    static Statement statement;
    static ResultSet rs,updateRS;

    TextView ser;
    EditText name;
    TextView type,statust;
    EditText loc;
    TextView det;
    TextView rpser;
    Button ConfirmBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad__edit);
        StrictMode.ThreadPolicy policy =
                new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        overridePendingTransition(0,0);
        Intent intent = getIntent();
        AccEditToString = intent.getStringExtra(     //接
                "AccEditToString");
        A1 = intent.getIntExtra("A1", 0);

        ser = (TextView)findViewById(R.id.AD_Edit_SerText);
        name = (EditText)findViewById(R.id.AD_Edit_NameEdit);
        type = (TextView)findViewById(R.id.AD_text_Typetext);
        statust = (TextView)findViewById(R.id.AD_Edit_Status);
        loc = (EditText)findViewById(R.id.AD_Edit_LocEdit);
        det = (TextView)findViewById(R.id.AD_Edit_Det);
        rpser = (TextView)findViewById(R.id.AD_Edit_RaspSerialText);
        ConfirmBtn = (Button)findViewById(R.id.AD_Edit_ConfirmBtn);
        ConfirmBtn.setOnClickListener(toConfirm);

        intent = getIntent();
        String serial = intent.getStringExtra("ARD_Serial");   //序號
        ser.setText(serial.toString());
        Intent intent2 = getIntent();
        String namename = intent2.getStringExtra("ARD_Name");  //名稱
        name.setText(namename);
        Intent intent7 = getIntent();
        String getType = intent7.getStringExtra("ARD_Type");  //檢測類型
        String typeC="";
        switch(getType){
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
        Intent intent3 = getIntent();
        Integer statusstatus = intent3.getIntExtra("ARD_Status",1);  //連線狀態
        String sta="";
        if(statusstatus==1){
            sta="待機";
        }else{
            sta="運行";
        }
        statust.setText(statust.getText()+"  "+sta);
        Intent intent4 = getIntent();
        String locloc = intent4.getStringExtra("ARD_Loc");   //擺設位置
        loc.setText(locloc);
        Intent intent5 = getIntent();
        Integer detdet = intent5.getIntExtra("ARD_Det",0);   //檢測值
        det.setText(det.getText()+"  "+detdet.toString());
        Intent intent6 = getIntent();
        Integer rpserpser = intent6.getIntExtra("ARD_RaspSerial",0);   //對應樹莓派
        rpser.setText(rpserpser.toString());


        BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBarAE);
        bottomBar.setDefaultTab(R.id.tab_ard);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(int tabId) {

                switch (tabId) {
                    case R.id.tab_rasp:
                        Intent intentR = new Intent();
                        intentR.setClass(AD_Edit.this,
                                RP_List.class);
                        intentR.putExtra("A1", A1);
                        intentR.putExtra("AccEditToString", AccEditToString);  //放
                        startActivity(intentR);
                        finish();
                        break;
                    case R.id.tab_soc:
                        Intent intentS = new Intent();
                        intentS.setClass(AD_Edit.this,
                                SOC_List.class);
                        intentS.putExtra("A1", A1);
                        intentS.putExtra("AccEditToString", AccEditToString);  //放
                        startActivity(intentS);
                        finish();
                        break;
                    case R.id.tab_eca:
                        Intent intentE = new Intent();
                        intentE.setClass(AD_Edit.this,
                                ECA_List.class);
                        intentE.putExtra("A1", A1);
                        intentE.putExtra("AccEditToString", AccEditToString);  //放
                        startActivity(intentE);
                        finish();
                        break;
                    case R.id.tab_dev:
                        Intent intentD = new Intent();
                        intentD.setClass(AD_Edit.this,
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
                intent.setClass(AD_Edit.this,
                        AD_List.class);
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
                    //String tt = type.getText().toString();
                    String ll = loc.getText().toString();
                    String rr = rpser.getText().toString();

                    int empty=0;
                    if("".equals(nn .toString().trim())){empty++;}
                    //if("".equals(tt .toString().trim())){empty++;}
                    if("".equals(ll .toString().trim())){empty++;}
                    if("".equals(rr .toString().trim())){empty++;}

                    //沒有空值，才執行
                    if(empty==0) {
                        //ARD_Type='" + tt + "',
                        String updatesql = "UPDATE ARD SET ARD_Name='" + nn + "', ARD_Loc='" + ll + "'  ,  ARD_RaspSerial='" + rr + "' WHERE ARD_Serial = '" + ss + "' ";
                        System.out.println("updatesql= "+updatesql);
                        statement.executeUpdate(updatesql);
                        Toast.makeText(AD_Edit.this, "ARDUINO編輯成功", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent();
                        intent.setClass(AD_Edit.this,
                                AD_List.class);
                        intent.putExtra("A1", A1);
                        intent.putExtra("AccEditToString", AccEditToString);
                        startActivity(intent);
                        finish();
                    }else{
                        Toast.makeText(AD_Edit.this, "不可以有空白欄位", Toast.LENGTH_SHORT).show();
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
