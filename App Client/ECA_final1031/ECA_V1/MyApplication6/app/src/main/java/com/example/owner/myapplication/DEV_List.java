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
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
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
import java.util.ArrayList;

public class DEV_List extends AppCompatActivity {
    static final String username = "ECASystem";
    static final String password = "ECASystem";
    private int A1=0;
    private String AccEditToString="";
    static Connection conn;
    static Statement statement;
    static ResultSet rs,updateRS;

    FloatingActionButton ConfirmBtn;
    ListDEV_Listview adapter;
    ListView lvone;

    ArrayList<Integer> Ser = new ArrayList<Integer>();   //動態陣列，放rp資料
    ArrayList<String> Name = new ArrayList<String>();
    ArrayList<String> SocSer = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev__list);
        StrictMode.ThreadPolicy policy =
                new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        overridePendingTransition(0, 0);
        Intent intent = getIntent();
        AccEditToString = intent.getStringExtra(     //接
                "AccEditToString");
        A1=intent.getIntExtra("A1",0);

        ConfirmBtn = (FloatingActionButton)findViewById(R.id.DEV_ConfirmBtn);
        ConfirmBtn.setOnClickListener(toConfirm);
//        try {
//            Class.forName("org.mariadb.jdbc.Driver").newInstance();
            try {
//                final String URL = "jdbc:mariadb://120.105.161.89/ECASystem";
//                System.out.println(URL);
//                Connection conn = DriverManager.getConnection(URL, username, password); //呼叫Connection物件，進行資料庫連線
                statement = Login.statement;
                System.out.println("資料庫連結成功");
                Log.e("TEST","資料庫連結成功");
                //Toast.makeText(RP_List.this, "資料庫連結成功",Toast.LENGTH_SHORT).show();

                int re = 0;

                rs = statement.executeQuery("select DEV_Serial, DEV_Name, DEV_SocSerial from  DEVICE ");
                while(rs.next()){
                    Ser.add(re,rs.getInt("DEV_Serial"));
                    Name.add(re,rs.getString("DEV_Name"));
                    SocSer.add(re,rs.getString("DEV_SocSerial"));
                    re++;
                }
                System.out.println("DEVSer="+Ser.get(re-1));
                adapter = new ListDEV_Listview(DEV_List.this, Ser, Name, SocSer,conn, A1, AccEditToString);
                lvone=(ListView)findViewById(R.id.lvone);
                lvone.setAdapter((ListAdapter) adapter);
                lvone.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {

                    }
                });

//                statement.close();
//                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
//        } catch (Exception k) {
//            k.printStackTrace();
//        }

        BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        bottomBar.setDefaultTab(R.id.tab_dev);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                switch (tabId) {
                    case R.id.tab_rasp:
                        Intent intentR = new Intent();
                        intentR.setClass(DEV_List.this,
                                RP_List.class);
                        intentR.putExtra("A1", A1);
                        intentR.putExtra("AccEditToString", AccEditToString);  //放
                        startActivity(intentR);
                        finish();
                        break;
                    case R.id.tab_ard:
                        Intent intentA = new Intent();
                        intentA.setClass(DEV_List.this,
                                AD_List.class);
                        intentA.putExtra("A1", A1);
                        intentA.putExtra("AccEditToString", AccEditToString);  //放
                        startActivity(intentA);
                        finish();
                        break;
                    case R.id.tab_soc:
                        Intent intentS = new Intent();
                        intentS.setClass(DEV_List.this,
                                SOC_List.class);
                        intentS.putExtra("A1", A1);
                        intentS.putExtra("AccEditToString", AccEditToString);  //放
                        startActivity(intentS);
                        finish();
                        break;
                    case R.id.tab_eca:
                        Intent intentE = new Intent();
                        intentE.setClass(DEV_List.this,
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
                Toast.makeText(getApplicationContext(), TabMessage.get(tabId, true), Toast.LENGTH_LONG).show();
            }
        });

    }

    private View.OnClickListener toConfirm = new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(DEV_List.this,
                    DEV_New.class);
            intent.putExtra("A1", A1);
            intent.putExtra("AccEditToString", AccEditToString);  //放到A1裡
            startActivity(intent);
            finish();
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
