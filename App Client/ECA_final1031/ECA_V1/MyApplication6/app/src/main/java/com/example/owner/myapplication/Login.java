package com.example.owner.myapplication;
//AD type part!!!
/*
    private int A1=0;
    private String AccEditToString="";
 */
/*
Intent intent = new Intent();
intent.setClass(Main_Menu.this,
        DEV_List.class);
intent.putExtra("A1", A1);
intent.putExtra("AccEditToString", AccEditToString);
startActivity(intent);
finish();
*/
/*
 Intent intent = getIntent();
 AccEditToString = intent.getStringExtra(
 "AccEditToString");
 A1=intent.getIntExtra("A1",0);
 */


import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Login extends AppCompatActivity {
    static final String username = "ECASystem";
    static final String password = "ECASystem";
    static Connection conn;
    static Statement statement;
    static ResultSet rs,updateRS;

    int checkCon=0;
    EditText AccEdit;
    EditText PasEdit;
    Button LoginBtn;
    Button ForBtn;
    Button RegBtn;
    public static final String A1="A1";
    String AccEditToString="";
    String PasEditToString="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        StrictMode.ThreadPolicy policy =
                new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        AccEdit = (EditText)findViewById(R.id.Login_AccEdit);
        PasEdit = (EditText)findViewById(R.id.Login_PasEdit);
        LoginBtn = (Button)findViewById(R.id.Login_LoginBtn);
        ForBtn = (Button)findViewById(R.id.Login_ForBtn);
        RegBtn = (Button)findViewById(R.id.Login_RegBtn);

        LoginBtn.setOnClickListener(toLogin);
        ForBtn.setOnClickListener(toForBtn);
        RegBtn.setOnClickListener(toRegBtn);

        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // 反復嘗試連接，直到連接成功後退出循環
                while (!Thread.interrupted()) {
                    try {
                        Thread.sleep(100);  // 每隔0.1秒嘗試連接
                    } catch (InterruptedException e) {
                        Log.e("TEST", e.toString());
                    }
                    // 2.設置好IP/端口/數據庫名/用戶名/密碼等必要的連接信息
                    String url = "jdbc:mysql://10.0.0.1:3306/ECASystem";
                    String user = "ECASystem";
                    String password = "ECASystem";

                    // 3.連接JDBC
                    try {
                        Class.forName("com.mysql.jdbc.Driver");
                        conn = DriverManager.getConnection(url, user, password);
                        checkCon=1;
                        Log.i("TEST", "遠程連接成功!");
                        break;
                    } catch (SQLException e) {
                        Log.e("TEST", "遠程連接失敗!");
                    }catch (Exception k) {
                        k.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }

    private View.OnClickListener toLogin = new View.OnClickListener() {
        public void onClick(View v) {
//            try {
//                Class.forName("org.mariadb.jdbc.Driver").newInstance();
            if(checkCon==1) {
                try {
//                    final String URL = "jdbc:mariadb://120.105.161.89/ECASystem";
//                    System.out.println(URL);
//                    java.sql.Connection conn = DriverManager.getConnection(URL, username, password); //呼叫Connection物件，進行資料庫連線
                    statement = conn.createStatement();
                    System.out.println("資料庫連結成功");
                    Log.e("TEST", "資料庫連結成功");
                    //Toast.makeText(Login.this, "資料庫連結成功",Toast.LENGTH_SHORT).show();

                    AccEditToString = AccEdit.getText().toString().trim();
                    PasEditToString = PasEdit.getText().toString().trim();
                    rs = statement.executeQuery("select *  from  MEMBER WHERE MEM_Acc = '" + AccEditToString + "'  AND MEM_Pass = '" + PasEditToString + "'");
                    if (rs.next()) {
                        Intent intent = new Intent();
                        intent.setClass(Login.this,
                                RP_List.class);
                        intent.putExtra("AccEditToString", AccEditToString);  //放到A1裡
                        //Toast.makeText(Login.this, AccEditToString,Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(Login.this, "帳號密碼錯誤", Toast.LENGTH_SHORT).show();
                        AccEdit.setText("");
                        PasEdit.setText("");
                    }

                    //statement.close();
                    //conn.close();

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }else{
                Toast.makeText(Login.this, "連線失敗!!", Toast.LENGTH_SHORT).show();
            }
//            } catch (Exception k) {
//                k.printStackTrace();
//           }
        }
    };

    private View.OnClickListener toForBtn = new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(Login.this,
                    Forget.class);

            startActivity(intent);
        }
    };

    private View.OnClickListener toRegBtn = new View.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(Login.this,
                    Register.class);

            startActivity(intent);
        }
    };

}
