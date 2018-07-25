package com.example.owner.myapplication;

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

public class Forget extends AppCompatActivity {
    static final String username = "ECASystem";
    static final String password = "ECASystem";
    static Connection conn;
    static Statement statement;
    static ResultSet rs,updateRS;

    EditText EmailEdit;
    EditText AccEdit;
    Button ConfirmBtn;

//    String defaultE="test@gmail.com";
//    String defaultA="test1";
//    String defaultP="test2";

    public static final String Email="Email";
    public static final String Account="Account";
    String sql_Pass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);
        StrictMode.ThreadPolicy policy =
                new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        EmailEdit = (EditText)findViewById(R.id.For_EmailEdit);
        AccEdit = (EditText)findViewById(R.id.For_AccEdit);

        ConfirmBtn = (Button)findViewById(R.id.For_ConfirmBtn);
        ConfirmBtn.setOnClickListener(toConfirmBtn);

        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // 反復嘗試連接，直到連接成功後退出循環
                while (!Thread.interrupted()) {
                    try {
                        Thread.sleep(1000);  // 每隔0.1秒嘗試連接
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
                        Log.i("TEST", "遠程連接成功!");
                        return;
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
    private View.OnClickListener toConfirmBtn = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//            try {
//                Class.forName("org.mariadb.jdbc.Driver").newInstance();
                try {
//                    final String URL = "jdbc:mariadb://120.105.161.89/ECASystem";
//                    System.out.println(URL);
//                    java.sql.Connection conn = DriverManager.getConnection(URL, username, password); //呼叫Connection物件，進行資料庫連線
                    statement = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                            ResultSet.CONCUR_UPDATABLE);
                    System.out.println("資料庫連結成功");
                    Log.e("TEST","資料庫連結成功");
                    //Toast.makeText(Forget.this, "資料庫連結成功",Toast.LENGTH_SHORT).show();

                    String EmailEdit_str = EmailEdit.getText().toString().trim();
                    String AccEdit_str = AccEdit.getText().toString().trim();

                    rs = statement.executeQuery("select MEM_Pass  from  MEMBER WHERE MEM_Email = '"+EmailEdit_str+"'  AND MEM_Acc = '"+AccEdit_str+"'");

                    if(rs.next()) {

                        sql_Pass=rs.getString("MEM_Pass");  //取得MEM_Pass欄位，放入宣告的sql_Pass裡
                        Toast.makeText(Forget.this, "您的密碼是"+"   "+sql_Pass ,Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent();
                        intent.setClass(Forget.this,
                                Login.class);
                        intent.putExtra(Email, EmailEdit_str);
                        intent.putExtra(Account, AccEdit_str);
                        startActivity(intent);
                    }
                    else{
                        Toast.makeText(Forget.this, "信箱或帳號錯誤",Toast.LENGTH_SHORT).show();
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                }
//            } catch (Exception k) {
//                k.printStackTrace();
//            }
        }
    };
}
