package com.example.owner.myapplication;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class User extends AppCompatActivity {
    static final String username = "ECASystem";
    static final String password = "ECASystem";
    private int A1=0;
    private String AccEditToString="";
    static Connection conn;
    static Statement statement;
    static ResultSet rs,updateRS;

    TextView emailText;
    TextView accText;
    EditText nameEdit;
    EditText pasEdit;
    Button ConfirmBtn;
    String getemail;
    String getename;
    String getepas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        StrictMode.ThreadPolicy policy =
                new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        overridePendingTransition(0, 0);

//        try {
//            Class.forName("org.mariadb.jdbc.Driver").newInstance();
            try {
//                final String URL = "jdbc:mariadb://120.105.161.89/ECASystem";
//                System.out.println(URL);
//                java.sql.Connection conn = DriverManager.getConnection(URL, username, password); //呼叫Connection物件，進行資料庫連線
                statement = Login.statement;
                System.out.println("資料庫連結成功");
                Log.e("TEST","資料庫連結成功");
                //Toast.makeText(User.this, "資料庫連結成功",Toast.LENGTH_SHORT).show();

                emailText=(TextView)findViewById(R.id.User_EmailText);
                accText=(TextView)findViewById(R.id.User_AccText);
                nameEdit = (EditText)findViewById(R.id.User_NameEdit);
                pasEdit = (EditText)findViewById(R.id.User_PasEdit);
                ConfirmBtn = (Button)findViewById(R.id.User_ConfirmBtn);

                ConfirmBtn.setOnClickListener(toConfirm);


                Intent intent = getIntent();
                AccEditToString = intent.getStringExtra(     //接收Account的資料 (登入者)
                        "AccEditToString");
                A1=intent.getIntExtra("A1",0);
                accText.setText(accText.getText()+AccEditToString);

                rs = statement.executeQuery("SELECT MEM_Email,  MEM_Name, MEM_Pass FROM MEMBER WHERE MEM_Acc = '"+AccEditToString+"' ");
                while (rs.next()) {
                    getemail=rs.getString("MEM_Email");  //取得MEM_Email欄位，放入宣告的getemail裡
                    getename=rs.getString("MEM_Name");
                    getepas=rs.getString("MEM_Pass");
                    emailText.setText(emailText.getText()+getemail);
                    nameEdit.setText(nameEdit.getText()+getename);
                    pasEdit.setText(pasEdit.getText()+getepas);
                }

//                statement.close();
//                conn.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
//        } catch (Exception k) {
//            k.printStackTrace();
//        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public Intent getSupportParentActivityIntent() {
        finish();
        return null;
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
                    //Toast.makeText(User.this, "資料庫連結成功",Toast.LENGTH_SHORT).show();

                    String nameEdit_str = nameEdit.getText().toString();
                    String pasEdit_str = pasEdit.getText().toString();

                    int empty=0;
                    if("".equals(nameEdit_str .toString().trim())){empty++;}
                    if("".equals(pasEdit_str .toString().trim())){empty++;}
                    //沒有空值，才執行
                    if(empty==0) {

                        String updatesql = "UPDATE MEMBER SET MEM_Name='" + nameEdit_str + "', MEM_Pass='" + pasEdit_str + "'  WHERE MEM_Email = '" + getemail + "' ";
                        statement.executeUpdate(updatesql);
                        Toast.makeText(User.this, "使用者資料編輯成功", Toast.LENGTH_SHORT).show();

                    }else{
                        Toast.makeText(User.this, "不可以有空白欄位", Toast.LENGTH_SHORT).show();
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
}




