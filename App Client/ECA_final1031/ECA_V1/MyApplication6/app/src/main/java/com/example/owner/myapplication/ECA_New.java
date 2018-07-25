package com.example.owner.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class ECA_New extends AppCompatActivity {
    private int A1=0;
    private String AccEditToString="";
    private String[] chooesCon= {"選擇條件","只要時間為[_點_分]的時候，","經過[_分鐘_秒]，","家中的[選場所]，沒有人在，",
            "家中的[選場所]，濕度達[]度，", "家中的[選場所]，溫度達[]度，", "家中的[選場所]，光度達[]度，"};
    //time=1,pass=2,infra=3,wet=4,cel=5,light=6
    private String[] spinnerALList = {"檯燈", "電風扇","電視","微波爐"};
    private int[] spinnerALSerialList = new int[1];
    private String[][] ARD_Ser_Loc_Type = new String[1][3];
    int setid= Integer.MAX_VALUE - 1;
    int setRArrid=0;
    private ArrayList<int[]> setRidArr = new ArrayList<int[]>();//[0]id1,[1]id2,[2]type
    private ArrayList<Spinner> ALSpinner = new ArrayList<Spinner>();
    private ArrayList<RelativeLayout> ALRL = new ArrayList<RelativeLayout>();
    private LinearLayout LLR;
    private RelativeLayout rl,rl2;
    Spinner spinnerAL, spinnerCloseCon;
    ArrayAdapter<String> adapCCList;
    Context t;
    private TextView OR;
    private FloatingActionButton cfBtn;
    //For_DB_Use
    private int RaspSerial=1;
    private String sqlForAL="",sqlForCloseCon="",sqlForPlaces="",sqlForECA="",sqlForECACheck="";
    static final String url = "jdbc:mariadb://120.105.161.89/ECASystem";
    static final String username = "ECASystem", password = "ECASystem"; // 要調整
    private Statement statementForAL,statementForCC,statementForPlaces,statementForECA,statementForECACheck;
    private ResultSet rsAL,rsCloseCon,rsPlaces,rsECA,rsECACheck;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eca__new);
        StrictMode.ThreadPolicy policy =
                new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        overridePendingTransition(0, 0);

        Intent intent = getIntent();
        AccEditToString = intent.getStringExtra(     //接
                "AccEditToString");
        A1=intent.getIntExtra("A1",0);
        //連接DB
        try {
            statementForAL = Login.statement;
            System.out.println("連接成功");
            sqlForAL="select DEV_Serial, DEV_Name from DEVICE where DEV_SocSerial IN "+
                    "(select SOC_Serial from SOCKET where SOC_RaspSerial = '"+RaspSerial+"')";
            rsAL = statementForAL.executeQuery(sqlForAL);
            int ALNum=0;
            while(rsAL.next()){
                ALNum++;
            }
            rsAL = statementForAL.executeQuery(sqlForAL);
            spinnerALList=new String[ALNum];
            spinnerALSerialList=new int[ALNum];
            ALNum=0;
            while(rsAL.next()){
                spinnerALList[ALNum]=rsAL.getString("DEV_Name");
                spinnerALSerialList[ALNum]=rsAL.getInt("DEV_Serial");
                ALNum++;
            }

        } catch (SQLException sqlException) {
            System.out.println("連接失敗");
            sqlException.printStackTrace();
        }

        OR = (TextView)this.findViewById(R.id.or);//取得元件
        spinnerAL = (Spinner)findViewById(R.id.spinnerAL);//取得元件  電器選單
        cfBtn = (FloatingActionButton) findViewById(R.id.confirmbtn);
        cfBtn.setOnClickListener( new View.OnClickListener(){
            public void onClick(View v) {
                String appliance = spinnerAL.getSelectedItem().toString();
                int ECA_DevSerial=spinnerALSerialList[spinnerAL.getSelectedItemPosition()];

                ArrayList<String> ECA=new ArrayList<String>();
                ArrayList<String> ECAType=new ArrayList<String>();
                ArrayList<String> ECAPlace=new ArrayList<String>();
                String[] tempECA;
                String toastOut="",ECAS;
                toastOut+="Rules:\n";
                int error=0;
                int passTimeCheck=0;
                for(int i=0;i<setRidArr.size();i++){
                    ECAS="";
                    tempECA = new String[3];
                    if(error==1)break;
                    int[] RidTemp=setRidArr.get(i);
                    int pos=1;
                    String typeS="";
                    switch (RidTemp[2]){
                        case 1:
                            typeS="time";
                            break;
                        case 2:
                            typeS="pass";
                            break;
                        case 3:
                            typeS="infra";
                            break;
                        case 4:
                            typeS="wet";
                            break;
                        case 5:
                            typeS="celsius";
                            break;
                        case 6:
                            typeS="light";
                            break;
                        default:
                            break;
                    }
                    toastOut+="type:"+typeS+" ";
                    tempECA[0]=typeS;
                    int forTempECA=1;
                    for(int j=0;j<RidTemp.length-1;j++){
                        for(int k=0;k<ALSpinner.size();k++){
                            if(RidTemp[j]==ALSpinner.get(k).getId()){
                                toastOut+="position"+pos+"="+ALSpinner.get(k).getSelectedItem()+" ";
                                if(RidTemp[2]==3||RidTemp[2]==4||RidTemp[2]==5||RidTemp[2]==6){
                                    if(ALSpinner.get(k).getSelectedItemPosition()==0){
                                        error=1;
                                        break;
                                    }
                                }
                                if(RidTemp[2]==2){
                                    if(ALSpinner.get(k).getSelectedItemPosition()==0)passTimeCheck++;
                                    if(passTimeCheck==2)break;
                                }
                                tempECA[forTempECA]=ALSpinner.get(k).getSelectedItem().toString();
                                forTempECA++;
                                pos++;
                            }
                        }
                        if(error==1||passTimeCheck==2)break;
                    }
                    if(error==1||passTimeCheck==2)break;

                    //ECA 要有ECA_DevSerial=ECA_DevSerial, ECA_Con, ECA_Ps="no", ECA_RaspSerial=+RaspSerial+,
                    // ECA_Con = Type/1,2=0 else places/1,2=time else whaterver/1,2=0 else degree
                    // pass/0/00:05/0
                    // celsius/廚房/0010/27
                    // light/房間/0500/40
                    // wet/客廳/0500/40

                    if(i!=setRidArr.size()-1)toastOut+="\n";

                    if(tempECA[0].toString().equals("time")||tempECA[0].toString().equals("pass")){
                        ECAS=tempECA[0]+"/0/"+tempECA[1]+":"+tempECA[2]+"/0";
                    }else if(tempECA[0].toString().equals("infra")){
                        ECAS=tempECA[0]+"/"+tempECA[1]+"/0003/1";
                        ECAType.add(tempECA[0]);
                        ECAPlace.add(tempECA[1]);
                    }else{
                        if(tempECA[0].toString().equals("light")){
                            if(tempECA[2].toString().equals("很強")){
                                tempECA[2]="900";
                            }else if(tempECA[2].toString().equals("稍強")){
                                tempECA[2]="700";
                            }else if(tempECA[2].toString().equals("適中")){
                                tempECA[2]="500";
                            }
                        }
                        ECAS=tempECA[0]+"/"+tempECA[1]+"/0001/"+tempECA[2];
                        ECAType.add(tempECA[0]);
                        ECAPlace.add(tempECA[1]);
                    }
                    ECA.add(ECAS);
                }
                int sensorRepeat = 0;
                for(int i=0;i<ECAType.size();i++){
                    for(int j=i+1;j<ECAType.size();j++){
                        if(ECAType.get(i).toString().equals(ECAType.get(j))&&ECAPlace.get(i).toString().equals(ECAPlace.get(j)))
                            sensorRepeat=1;
                    }

                }

                if(sensorRepeat==1){
                    Toast.makeText(t, "已設定相同規則！", Toast.LENGTH_SHORT).show();
                }else if(passTimeCheck==2){
                    Toast.makeText(t, "經過時間為零！", Toast.LENGTH_SHORT).show();
                }else if(error==1) {
                    Toast.makeText(t, "有條件未選擇！", Toast.LENGTH_SHORT).show();
                }else if(!toastOut.equals("Rules:\n")){
                    try {
                        statementForECA = Login.statement;
                        statementForECACheck = Login.statement;
                        //for 如果已經存在同樣ECA，就略過，或有一些要覆蓋或修改
                        // 下方就不能新增
                        sqlForECACheck="select ECA_DevSerial, ECA_Con from ECA where ECA_RaspSerial = '"+RaspSerial+"'";
                        rsECACheck = statementForECACheck.executeQuery(sqlForECACheck);
                        ArrayList<String> ECACheckAR=new ArrayList<String>();
                        ArrayList<String> APSCheckAR = new ArrayList<String>();
                        while(rsECACheck.next()){
                            APSCheckAR.add(Integer.toString(rsECACheck.getInt("ECA_DevSerial")));
                            ECACheckAR.add(rsECACheck.getString("ECA_Con"));
                        }
                        int repeat=0;
                        String checkType="",checkPlace="";
                        String[] tempECACheck=new String[4];
                        String[] tempECAGet=new String[4];
                        for(int i=0;i<ECA.size();i++){
                            for(int j=0;j<ECACheckAR.size();j++){
                                tempECACheck=ECACheckAR.get(j).split("/");
                                tempECAGet=ECA.get(i).split("/");
                                for(int h=0;h<tempECACheck.length;h++) {
                                    System.out.println("tempECACheck\t" + tempECACheck[h]);
                                    System.out.println("tempECAGet\t" + tempECAGet[h]);
                                    System.out.println("APSCheckAR\t" + APSCheckAR.get(j));
                                    System.out.println("ECA_DevSerial\t" + ECA_DevSerial);
                                    System.out.println("--------------------");
                                    if (tempECACheck[0].equals(tempECAGet[0]) && tempECACheck[1].equals(tempECAGet[1]) && APSCheckAR.get(j).equals(Integer.toString(ECA_DevSerial))) {
                                        repeat = 1;
                                        checkType = tempECACheck[0];
                                        checkPlace = tempECACheck[1];
                                        break;
                                    } else if (tempECACheck[0].equals(tempECAGet[0]) && tempECACheck[0].equals("pass") && APSCheckAR.get(j).equals(Integer.toString(ECA_DevSerial))) {
                                        repeat = 1;
                                        checkType = tempECACheck[0];
                                        checkPlace = tempECACheck[1];
                                        break;
                                    }
                                }
                            }
                        }

                        if(repeat==0){
                            for(int i=0;i<ECA.size();i++) {
                                sqlForECA = "INSERT INTO ECA (`ECA_Num`, `ECA_DevSerial`, `ECA_Con`, `ECA_Ps`, `ECA_RaspSerial`) " +
                                        "VALUES (NULL, '" + ECA_DevSerial + "', '" + ECA.get(i) + "', 'no', '" + RaspSerial + "')";
                                statementForECA.executeUpdate(sqlForECA);
                            }
                            Toast.makeText(t, toastOut, Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent();
                            intent.setClass(ECA_New.this,
                                    ECA_List.class);
                            intent.putExtra("A1", A1);
                            intent.putExtra("AccEditToString", AccEditToString);  //放
                            startActivity(intent);
                        }else{
                            String ct="";//pass,wet,celsius,light
                            if(checkType.equals("time"))ct="時間";
                            else if(checkType.equals("pass"))ct="經過時間";
                            else if(checkType.equals("infra"))ct="沒有人在";
                            else if(checkType.equals("wet"))ct="濕度";
                            else if(checkType.equals("celsius"))ct="溫度";
                            else if(checkType.equals("light"))ct="光度";
                            Toast.makeText(t, "關於 "+ct+" 的規則重複了！", Toast.LENGTH_SHORT).show();
                        }
                    } catch (SQLException sqlException) {
                        System.out.println("設定ECA規則發生錯誤！");
                        sqlException.printStackTrace();
                    }
                }else{
                    Toast.makeText(t, "沒有ECA規則！", Toast.LENGTH_SHORT).show();
                }
            }
        });
        LLR = (LinearLayout)findViewById(R.id.LLR);
        t=this;
        //選擇電器
        ArrayAdapter<String> adapALList = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerALList);//建立一個 String 型別的 ArraryAdapter，將陣列丟給它
        adapALList.setDropDownViewResource(android.R.layout.simple_spinner_item);//將 ArrayAdapter 設為 spinner 格式
        spinnerAL.setAdapter(adapALList);//把 ArrayAdapter 設給 Spinner 元件
        spinnerAL.setOnItemSelectedListener(spnALItemSelLis);//設定 Spinner 事件發生時的 Listener:

        //adapter=new ArrayAdapter<CharSequence>(MainActivity.this, android.R.layout.simple_spinner_item, chooesCon);
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //選擇關閉條件
        spinnerCloseCon = (Spinner)findViewById(R.id.closeCon);//取得元件
        //DB 判斷有哪些感測器存在，才進行條件顯示
        //以樹莓派序號查ARD中符合的筆數，查看有哪些ARD_Ser_Loc_Type
        try {
            sqlForCloseCon="select ARD_Serial, ARD_Loc, ARD_type from ARD where ARD_RaspSerial = '"+RaspSerial+"' ";
            statementForCC = Login.statement;
            rsCloseCon = statementForCC.executeQuery(sqlForCloseCon);
            int CCNum=0;
            while(rsCloseCon.next()){
                CCNum++;
            }
            rsCloseCon = statementForCC.executeQuery(sqlForCloseCon);
            ARD_Ser_Loc_Type=new String[CCNum][3];
            CCNum=0;
            while(rsCloseCon.next()){
                ARD_Ser_Loc_Type[CCNum][0]=rsCloseCon.getString("ARD_Serial");
                ARD_Ser_Loc_Type[CCNum][1]=rsCloseCon.getString("ARD_Loc");
                ARD_Ser_Loc_Type[CCNum][2]=rsCloseCon.getString("ARD_type");
                CCNum++;
            }
        } catch (SQLException sqlException) {
            System.out.println("PROBLEM WITH ACCESSING AL DB DATA");
            sqlException.printStackTrace();
        }
        adjustCloseCon();

        adapCCList = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, chooesCon);//建立一個 String 型別的 ArraryAdapter，將陣列丟給它
        adapCCList.setDropDownViewResource(android.R.layout.simple_spinner_item);//將 ArrayAdapter 設為 spinner 格式
        spinnerCloseCon.setAdapter(adapCCList);//把 ArrayAdapter 設給 Spinner 元件
        spinnerCloseCon.setOnItemSelectedListener(spnCCItemSelLis);//設定 Spinner 事件發生時的 Listener:


        BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBarEN);
        bottomBar.setDefaultTab(R.id.tab_eca);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                switch (tabId) {
                    case R.id.tab_rasp:
                        Intent intentR = new Intent();
                        intentR.setClass(ECA_New.this,
                                RP_List.class);
                        intentR.putExtra("A1", A1);
                        intentR.putExtra("AccEditToString", AccEditToString);  //放
                        startActivity(intentR);
                        finish();
                        break;
                    case R.id.tab_ard:
                        Intent intentA = new Intent();
                        intentA.setClass(ECA_New.this,
                                AD_List.class);
                        intentA.putExtra("A1", A1);
                        intentA.putExtra("AccEditToString", AccEditToString);  //放
                        startActivity(intentA);
                        finish();
                        break;
                    case R.id.tab_soc:
                        Intent intentS = new Intent();
                        intentS.setClass(ECA_New.this,
                                SOC_List.class);
                        intentS.putExtra("A1", A1);
                        intentS.putExtra("AccEditToString", AccEditToString);  //放
                        startActivity(intentS);
                        finish();
                        break;
                    case R.id.tab_dev:
                        Intent intentD = new Intent();
                        intentD.setClass(ECA_New.this,
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
                intent.setClass(ECA_New.this,
                        ECA_List.class);
                intent.putExtra("A1", A1);
                intent.putExtra("AccEditToString", AccEditToString);  //放
                startActivity(intent);
            }
        });

    }
    private void adjustCloseCon(){
        int[] ifDeleteCC = new int[chooesCon.length];
        for(int i=0;i<ifDeleteCC.length;i++){
            if(i==0||i==1||i==2)ifDeleteCC[i]=1;
            else  ifDeleteCC[i]=0;
        }
        for(int i=0;i<ARD_Ser_Loc_Type.length;i++){
            switch (ARD_Ser_Loc_Type[i][2]){
                case "infra":
                    ifDeleteCC[3]=1;
                    break;
                case "wet":
                    ifDeleteCC[4]=1;
                    break;
                case "celsius":
                    ifDeleteCC[5]=1;
                    break;
                case "light":
                    ifDeleteCC[6]=1;
                    break;
                default:
                    break;
            }
        }
        int countCC=0;
        for(int i=0;i<ifDeleteCC.length;i++){
            if(ifDeleteCC[i]==1)countCC++;
        }
        String[] tempCC=new String[countCC];

        countCC=0;
        for(int i=0;i<ifDeleteCC.length;i++){
            if(ifDeleteCC[i]==1){
                tempCC[countCC]=chooesCon[i];
                countCC++;
            }
        }
        chooesCon=tempCC;
    }


    private Spinner.OnItemSelectedListener spnCCItemSelLis =
            new Spinner.OnItemSelectedListener () {
                public void onItemSelected(AdapterView parent, View v, int position, long id) {
                    if(position!=0){
                        switch (chooesCon[position]){
                            case "只要時間為[_點_分]的時候，":
                                newLayoutConTime(1);
                                break;
                            case "經過[_分鐘_秒]，":
                                newLayoutConTime(2);
                                break;
                            case "家中的[選場所]，沒有人在，":
                                newLayoutCon(3);
                                break;
                            case "家中的[選場所]，濕度達[]度，":
                                newLayoutCon(4);//wet 1,cel 2,light 3
                                break;
                            case"家中的[選場所]，溫度達[]度，":
                                newLayoutCon(5);
                                break;
                            case "家中的[選場所]，光度達[]度，":
                                newLayoutCon(6);
                                break;
                            default:
                                break;
                        }
                        spinnerCloseCon.setAdapter(adapCCList);//把 ArrayAdapter 設給 Spinner 元件
                        spinnerCloseCon.setOnItemSelectedListener(spnCCItemSelLis);
                    }
                    if(setRidArr.size()>=1){
                        OR.setText("或者");
                    }else{
                        OR.setText("如果");
                    }
                }
                public void onNothingSelected(AdapterView parent) {
                }
            };

    private void newLayoutConTime(int a){
        final int x=a;
        String[] setS1={"當時間為","點","分"};
        String[] setS2={"經過","分鐘","秒 後"};
        String[]setS = new String[3];
        if(a==1){
            setS=setS1;
        }else if(a==2){
            setS=setS2;
        }
        String[] clocks = {"1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20",
                "21","22","23","24"};
        String[] mins={"0","1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20",
                "21","22","23","24","25","26","27","28","29","30","31","32","33","34","35","36","37","38","39","40",
                "41","42","43","44","45","46","47","48","49","50","51","52","53","54","55","56","57","58","59"};
        String[] secs={"0","1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20",
                "21","22","23","24","25","26","27","28","29","30","31","32","33","34","35","36","37","38","39","40",
                "41","42","43","44","45","46","47","48","49","50","51","52","53","54","55","56","57","58","59"};
        Spinner clock,min,sec;
        clock = new Spinner(ECA_New.this);
        ArrayAdapter<String> adapPLList = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, clocks);//建立一個 String 型別的 ArraryAdapter，將陣列丟給它
        adapPLList.setDropDownViewResource(android.R.layout.simple_spinner_item);//將 ArrayAdapter 設為 spinner 格式
        clock.setAdapter(adapPLList);//把 ArrayAdapter 設給 Spinner 元件
        try {
            Field popup = Spinner.class.getDeclaredField("mPopup");
            popup.setAccessible(true);

            // Get private mPopup member variable and try cast to ListPopupWindow
            android.widget.ListPopupWindow popupWindow = (android.widget.ListPopupWindow) popup.get(clock);

            // Set popupWindow height to 500px
            popupWindow.setHeight(400);
        }
        catch (NoClassDefFoundError | ClassCastException | NoSuchFieldException | IllegalAccessException e) {
            // silently fail...
        }
        //place.setOnItemSelectedListener(spnPLItemSelLis);

        min = new Spinner(ECA_New.this);
        ArrayAdapter<String> adapDEList = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, mins);//建立一個 String 型別的 ArraryAdapter，將陣列丟給它
        adapDEList.setDropDownViewResource(android.R.layout.simple_spinner_item);//將 ArrayAdapter 設為 spinner 格式
        min.setAdapter(adapDEList);//把 ArrayAdapter 設給 Spinner 元件

        try {
            Field popup = Spinner.class.getDeclaredField("mPopup");
            popup.setAccessible(true);

            // Get private mPopup member variable and try cast to ListPopupWindow
            android.widget.ListPopupWindow popupWindow = (android.widget.ListPopupWindow) popup.get(min);

            // Set popupWindow height to 500px
            popupWindow.setHeight(400);
        }
        catch (NoClassDefFoundError | ClassCastException | NoSuchFieldException | IllegalAccessException e) {
            // silently fail...
        }
        //sec
        sec = new Spinner(ECA_New.this);
        ArrayAdapter<String> adapMList = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, secs);//建立一個 String 型別的 ArraryAdapter，將陣列丟給它
        adapMList.setDropDownViewResource(android.R.layout.simple_spinner_item);//將 ArrayAdapter 設為 spinner 格式
        sec.setAdapter(adapMList);//把 ArrayAdapter 設給 Spinner 元件

        try {
            Field popup = Spinner.class.getDeclaredField("mPopup");
            popup.setAccessible(true);

            // Get private mPopup member variable and try cast to ListPopupWindow
            android.widget.ListPopupWindow popupWindow = (android.widget.ListPopupWindow) popup.get(sec);

            // Set popupWindow height to 500px
            popupWindow.setHeight(400);
        }
        catch (NoClassDefFoundError | ClassCastException | NoSuchFieldException | IllegalAccessException e) {
            // silently fail...
        }


        rl = new RelativeLayout(t);
        setid--;

        TextView t1,t2,t3;
        Button cancel;
        RelativeLayout.LayoutParams param1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        rl.setLayoutParams(param1);

        param1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        t1=new TextView(ECA_New.this);
        t1.setId(setid--);
        t1.setText(setS[0]);
        //param1.addRule(RelativeLayout.LEFT_OF, RelativeLayout.TRUE);
        param1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        t1.setLayoutParams(param1);
        rl.addView(t1);

        int set1,set2;
        set1=setid;
        if(a==1){
            clock.setId(setid--);
            param1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            param1.addRule(RelativeLayout.RIGHT_OF, t1.getId());
            clock.setLayoutParams(param1);
            rl.addView(clock);
        }else if(a==2){
            min.setId(setid--);
            param1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            param1.addRule(RelativeLayout.RIGHT_OF, t1.getId());
            min.setLayoutParams(param1);
            rl.addView(min);
        }

        t2 = new TextView(ECA_New.this);
        t2.setId(setid--);
        t2.setText(setS[1]);
        param1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        if(a==1){
            param1.addRule(RelativeLayout.RIGHT_OF, clock.getId());
        }else if(a==2){
            param1.addRule(RelativeLayout.RIGHT_OF, min.getId());
        }
        t2.setLayoutParams(param1);
        rl.addView(t2);

        set2=setid;
        if(a==1){
            min.setId(setid--);
            param1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            param1.addRule(RelativeLayout.RIGHT_OF, t2.getId());
            min.setLayoutParams(param1);
            rl.addView(min);
        }else if(a==2){
            System.out.println("here------------");
            sec.setId(setid--);
            param1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            param1.addRule(RelativeLayout.RIGHT_OF, t2.getId());
            sec.setLayoutParams(param1);
            rl.addView(sec);
        }

        t3 = new TextView(ECA_New.this);
        t3.setId(setid--);
        t3.setText(setS[2]);
        param1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        if(a==1){
            param1.addRule(RelativeLayout.RIGHT_OF, min.getId());
        }else if(a==2){
            param1.addRule(RelativeLayout.RIGHT_OF, sec.getId());
        }
        t3.setLayoutParams(param1);
        rl.addView(t3);

        LLR.addView(rl);

        //rl2
        param1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        rl2 = new RelativeLayout(t);
        rl2.setLayoutParams(param1);

        param1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        cancel = new Button(ECA_New.this);
        cancel.setText("取消");
        //param1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        param1.addRule(RelativeLayout.BELOW, t3.getId());
        param1.width = 180;
        param1.height = 120;
        cancel.setLayoutParams(param1);
        rl2.addView(cancel);
        LLR.addView(rl2);

        //陣列問題
        int[] addSetRid=new int[3];
        addSetRid[2] = a;//1=Time,2=Pass
        if (a == 1) {
            ALSpinner.add(clock);
            ALSpinner.add(min);
        }else{
            ALSpinner.add(min);
            ALSpinner.add(sec);
        }
        ALRL.add(rl);
        ALRL.add(rl2);;

        addSetRid[0]=set1;
        ALRL.get(ALRL.size()-2).setId(set1);
//ALSpinner.get(ALSpinner.size()-2).setId(serial);不能這樣寫因為他已經有ID了

        addSetRid[1]=set2;
        ALRL.get(ALRL.size()-1).setId(set2);
        //ALSpinner.get(ALSpinner.size()-1).setId(serial);

        setRidArr.add(addSetRid);
        setRArrid++;

        cancel.setOnClickListener( new View.OnClickListener(){
            int[]serials=setRidArr.get(setRidArr.size()-1);
            public void onClick(View v) {
                if(ALRL.size()<=1){
                    System.out.println("c1="+0);
                    LLR.removeView(ALRL.get(0));
                    setRidArr.remove(0);
                }else{
                    for(int i=0;i<serials.length;i++){
                        for(int j=ALRL.size()-1;j>-1;j--){
                            if(ALRL.get(j).getId()==serials[i]){
                                LLR.removeView(ALRL.get(j));
                                ALSpinner.remove(j);
                                ALRL.remove(j);
                            }
                        }
                    }setRidArr.remove(serials);
                }
                addChoice(x);
                if(setRidArr.size()>=1){
                    OR.setText("或者");
                    System.out.println("setRidArr.size()="+setRidArr.size());
                }else{
                    OR.setText("如果");
                }
            }
        });
        if(setRidArr.size()>=1){
            OR = new TextView(this);
            OR = (TextView)findViewById(R.id.or);
            OR.setText("或者");
        }else{
            OR.setText("如果");
        }
        delChoice(x);
    }
    private void newLayoutCon(int a){
        String x="";
        String Type="";
        if(a==4){
            x="濕度";
            Type="wet";
        }else if(a==5){
            x="溫度";
            Type="celsius";
        }else if(a==6){
            x="光度";
            Type="light";
        }else if(a==3){
            x="";
            Type="infra";
        }
        String[] places = {"請選擇場所","客廳","書房","廚房"};
        String[] degrees={"請選擇"+x,"0","1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20",
                "21","22","23","24","25","26","27","28","29","30","31","32","33","34","35","36","37","38","39","40",
                "41","42","43","44","45","46","47","48","49","50","51","52","53","54","55","56","57","58","59","60"};

        if(a==6){
            String[] dTemp={"請選擇"+x,"很強", "稍強" ,"適中"};//900, 700, 500, 300, 100
            degrees=dTemp;
        }
        Spinner place,degree;
        place = new Spinner(ECA_New.this);
        //DB find places
        try {
            statementForPlaces = Login.statement;
            sqlForPlaces="select ARD_Loc from ARD where ARD_Type = '"+Type+"' AND ARD_RaspSerial= '"+RaspSerial+"' ;";
            rsPlaces = statementForPlaces.executeQuery(sqlForPlaces);
            int PlaceNum=0;
            while(rsPlaces.next()){
                PlaceNum++;
            }
            rsPlaces = statementForPlaces.executeQuery(sqlForPlaces);
            places=new String[PlaceNum+1];
            PlaceNum=1;
            places[0]="請選擇場所";
//if place be chosen, it shouldn't appear again.

            while(rsPlaces.next()){
                places[PlaceNum]=rsPlaces.getString("ARD_Loc");
                PlaceNum++;
            }
        } catch (SQLException sqlException) {
            System.out.println("連接失敗 DB find places");
            sqlException.printStackTrace();
        }

        ArrayAdapter<String> adapPLList = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, places);//建立一個 String 型別的 ArraryAdapter，將陣列丟給它
        adapPLList.setDropDownViewResource(android.R.layout.simple_spinner_item);//將 ArrayAdapter 設為 spinner 格式
        place.setAdapter(adapPLList);//把 ArrayAdapter 設給 Spinner 元件
        //place.setOnItemSelectedListener(spnPLItemSelLis);

        degree = new Spinner(ECA_New.this);
        ArrayAdapter<String> adapDEList = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, degrees);//建立一個 String 型別的 ArraryAdapter，將陣列丟給它
        adapDEList.setDropDownViewResource(android.R.layout.simple_spinner_item);//將 ArrayAdapter 設為 spinner 格式
        degree.setAdapter(adapDEList);//把 ArrayAdapter 設給 Spinner 元件
        //degree.setOnItemSelectedListener(spnPLItemSelLis);
        try {
            Field popup = Spinner.class.getDeclaredField("mPopup");
            popup.setAccessible(true);

            // Get private mPopup member variable and try cast to ListPopupWindow
            android.widget.ListPopupWindow popupWindow = (android.widget.ListPopupWindow) popup.get(degree);

            // Set popupWindow height to 500px
            popupWindow.setHeight(400);
        }
        catch (NoClassDefFoundError | ClassCastException | NoSuchFieldException | IllegalAccessException e) {
            // silently fail...
        }

        rl = new RelativeLayout(t);
        setid--;

        TextView t1,t2,t3,ti;
        Button cancel;
        RelativeLayout.LayoutParams param1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        rl.setLayoutParams(param1);

        param1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        t1=new TextView(ECA_New.this);
        t1.setId(setid--);
        t1.setText("家中的");
        //param1.addRule(RelativeLayout.LEFT_OF, RelativeLayout.TRUE);
        param1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        t1.setLayoutParams(param1);
        rl.addView(t1);

        int set1,set2;
        set1=setid;
        place.setId(setid--);
        param1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        //param1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        param1.addRule(RelativeLayout.RIGHT_OF, t1.getId());
        place.setLayoutParams(param1);
        rl.addView(place);
        if(a==3){
            param1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            ti=new TextView(ECA_New.this);
            ti.setId(setid--);
            ti.setText("沒有人在");
            //param1.addRule(RelativeLayout.LEFT_OF, RelativeLayout.TRUE);
            param1.addRule(RelativeLayout.RIGHT_OF, place.getId());
            ti.setLayoutParams(param1);
            rl.addView(ti);
        }


        LLR.addView(rl);


        //rl2
        param1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        rl2 = new RelativeLayout(t);
        rl2.setLayoutParams(param1);

        param1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        t2 = new TextView(ECA_New.this);
        t2.setId(setid--);
        if(a==3){
            t2.setText("沒有人在");
        }else{
            if (a == 6) {
                t2.setText("一旦光線");
            } else {
                t2.setText("一旦" + x + "達到");
            }

            rl2.addView(t2);
        }

        set2=setid;
        degree.setId(setid--);
        param1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        param1.addRule(RelativeLayout.RIGHT_OF, t2.getId());
        degree.setLayoutParams(param1);
        if(a!=3){
            rl2.addView(degree);
        }else{
            degree.setSelection(1);
        }

        t3 = new TextView(ECA_New.this);
        t3.setId(setid--);
        if(a==3){
            t3.setText("");
        }else {
            if (a == 6) {
                t3.setText("");
            } else {
                t3.setText("度");
            }
        }
        param1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        param1.addRule(RelativeLayout.RIGHT_OF, degree.getId());
        t3.setLayoutParams(param1);
        if(a!=3){
            rl2.addView(t3);
        }


        param1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        cancel = new Button(ECA_New.this);
        cancel.setText("取消");
        //param1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        param1.addRule(RelativeLayout.BELOW, t3.getId());
        param1.width = 180;
        param1.height = 120;
        cancel.setLayoutParams(param1);
        rl2.addView(cancel);
        LLR.addView(rl2);


        //陣列問題
        int[] addSetRid=new int[3];
        addSetRid[2]=a;
        ALSpinner.add(place);
        ALSpinner.add(degree);
        ALRL.add(rl);
        ALRL.add(rl2);;

        addSetRid[0]=set1;
        ALRL.get(ALRL.size()-2).setId(set1);
//ALSpinner.get(ALSpinner.size()-2).setId(serial);不能這樣寫因為他已經有ID了

        addSetRid[1]=set2;
        ALRL.get(ALRL.size()-1).setId(set2);
        //ALSpinner.get(ALSpinner.size()-1).setId(serial);

        setRidArr.add(addSetRid);
        setRArrid++;

        cancel.setOnClickListener( new View.OnClickListener(){
            int[]serials=setRidArr.get(setRidArr.size()-1);
            public void onClick(View v) {
                if(ALRL.size()<=1){
                    System.out.println("c1="+0);
                    LLR.removeView(ALRL.get(0));
                    setRidArr.remove(0);
                }else{
                    for(int i=0;i<serials.length;i++){
                        for(int j=ALRL.size()-1;j>-1;j--){
                            if(ALRL.get(j).getId()==serials[i]){
                                LLR.removeView(ALRL.get(j));
                                ALSpinner.remove(j);
                                ALRL.remove(j);
                            }
                        }
                    }setRidArr.remove(serials);
                }

                if(setRidArr.size()>=1){
                    OR.setText("或者");
                    System.out.println("setRidArr.size()="+setRidArr.size());
                }else{
                    OR.setText("如果");
                }
            }
        });

        if(setRidArr.size()>=1){
            OR = new TextView(this);
            OR = (TextView)findViewById(R.id.or);
            OR.setText("或者");
        }else{
            OR.setText("如果");
        }
    }
    private Spinner.OnItemSelectedListener spnALItemSelLis =
            new Spinner.OnItemSelectedListener () {
                public void onItemSelected(AdapterView parent, View v, int position, long id) {

                }
                public void onNothingSelected(AdapterView parent) {
                }
            };

    void addChoice(int a){//1=只要幾點幾分，2＝經過...
        String[] temp= new String[chooesCon.length+1];
        temp[0]=chooesCon[0];
        if(!chooesCon[1].equals("只要時間為[_點_分]的時候，")||!chooesCon[1].equals("經過[_分鐘_秒]，")){
            if(a==1){
                temp[1]="只要時間為[_點_分]的時候，";
            }else{
                temp[1]="經過[_分鐘_秒]，";
            }for(int i=1;i<chooesCon.length;i++){
                temp[i+1]=chooesCon[i];
            }
        }else if(chooesCon[1].equals("只要時間為[_點_分]的時候，")){
            temp[2]="經過[_分鐘_秒]，";
            temp[1]=chooesCon[1];
            for(int i=2;i<chooesCon.length;i++){
                temp[i+1]=chooesCon[i];
            }
        }else {
            temp[1] = "";
            for (int i = 1; i < chooesCon.length; i++) {
                temp[i + 1] = chooesCon[i];
            }
        }
        chooesCon=temp;

        adapCCList = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, chooesCon);//建立一個 String 型別的 ArraryAdapter，將陣列丟給它
        adapCCList.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinnerCloseCon.setAdapter(adapCCList);//把 ArrayAdapter 設給 Spinner 元件
        spinnerCloseCon.setOnItemSelectedListener(spnCCItemSelLis);
    }
    void delChoice(int d){
        String[] temp= new String[chooesCon.length-1];
        int dnum=0;
        String del;
        if(d==1){
            del="只要時間為[_點_分]的時候，";
        }else{
            del="經過[_分鐘_秒]，";
        }
        temp[0]=chooesCon[0];
        System.out.println("d="+del);
        for(int i=0;i<chooesCon.length;i++){
            if(chooesCon[i].equals(del)){

                System.out.println("chooesCon[i]="+chooesCon[i]);
                System.out.println("del="+del);
                dnum=i;
            }
        }
        int j=1;
        for(int i=1;i<temp.length;i++){
            if(dnum==i)j++;
            temp[i]=chooesCon[j];
            j++;
        }

        chooesCon=temp;

        adapCCList = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, chooesCon);//建立一個 String 型別的 ArraryAdapter，將陣列丟給它
        adapCCList.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinnerCloseCon.setAdapter(adapCCList);//把 ArrayAdapter 設給 Spinner 元件
        spinnerCloseCon.setOnItemSelectedListener(spnCCItemSelLis);
    }


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




