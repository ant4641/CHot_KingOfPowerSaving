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

public class Register extends AppCompatActivity {
    static final String username = "ECASystem";
    static final String password = "ECASystem";
    private static Connection conn;
    private static Statement statement;
    private static ResultSet rs,updateRS;

    EditText Name;
    EditText Email;
    EditText AccEdit;
    EditText PasEdit;
    Button ConfirmBtn;

    String sql_name=""; //假設為資料庫
    String sql_email="";
    String sql_account="";
    String sql_password="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        StrictMode.ThreadPolicy policy =
                new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Name = (EditText)findViewById(R.id.Reg_NameEdit);
        Email = (EditText)findViewById(R.id.Reg_EmailEdit);
        AccEdit = (EditText)findViewById(R.id.Reg_AccEdit);
        PasEdit = (EditText)findViewById(R.id.Reg_PasEdit);

        ConfirmBtn = (Button)findViewById(R.id.Reg_ConfirmBtn);
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
//          try {
//                Class.forName("org.mariadb.jdbc.Driver").newInstance();
                try {
//                    final String URL = "jdbc:mariadb://120.105.161.89/ECASystem";
//                    System.out.println(URL);
//                    java.sql.Connection conn = DriverManager.getConnection(URL, username, password); //呼叫Connection物件，進行資料庫連線
                    statement = conn.createStatement();
                    System.out.println("資料庫連結成功");
                    Log.e("TEST","資料庫連結成功");
                    //Toast.makeText(Register.this, "資料庫連結成功",Toast.LENGTH_SHORT).show();

                    String Name_str = Name.getText().toString();
                    String Email_str = Email.getText().toString();
                    String AccEdit_str = AccEdit.getText().toString();
                    String PasEdit_str = PasEdit.getText().toString();

                    int empty=0;
                    if("".equals(Name_str .toString().trim())){empty++;}
                    if("".equals(Email_str .toString().trim())){empty++;}
                    if("".equals(AccEdit_str .toString().trim())){empty++;}
                    if("".equals(PasEdit_str .toString().trim())){empty++;}
                    //沒有空值，才執行
                    if(empty==0){

                        if(Email_str.trim().matches("^[0-9a-z_-]+([.][0-9a-z_-]+)*@[0-9a-z_-]+([.][0-9a-z_-]+)*$")) {

                            if((sql_email.equals( Email.getText().toString().trim())) ){
                                //Toast.makeText(Register.this, "此信箱已註冊過"+sql_email+" "+sql_account,Toast.LENGTH_SHORT).show();
                                Toast.makeText(Register.this, "此信箱已註冊過",Toast.LENGTH_SHORT).show();
                            }else if((sql_account.equals( AccEdit.getText().toString().trim()))) {
                                //Toast.makeText(Register.this, "此帳號已有人使用"+sql_email+" "+sql_account,Toast.LENGTH_SHORT).show();
                                Toast.makeText(Register.this, "此帳號已有人使用",Toast.LENGTH_SHORT).show();

                            }else{
                                sql_name=Name_str; //存入資料庫
                                sql_email=Email_str;
                                sql_account=AccEdit_str;
                                sql_password=PasEdit_str;

                                String sql = "INSERT INTO MEMBER( MEM_Acc, MEM_Pass, MEM_Name, MEM_Email) VALUES ('" + sql_account + "', '" + sql_password + "' , '" + sql_name + "' , '" + sql_email + "')";
                                statement.executeUpdate(sql);
                                //Toast.makeText(Register.this, "註冊成功"+sql_email+" "+sql_account,Toast.LENGTH_SHORT).show();
                                Toast.makeText(Register.this, "註冊成功",Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent();
                                intent.setClass(Register.this,
                                        Login.class);

                                startActivity(intent);
                            }
                        }else{
                            Toast.makeText(Register.this, "信箱格式錯誤",Toast.LENGTH_SHORT).show();
                        }

                    }else{
                        Toast.makeText(Register.this, "不可以有空白欄位",Toast.LENGTH_SHORT).show();
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


