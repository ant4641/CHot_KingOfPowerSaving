import java.util.Timer;
import java.util.TimerTask;
import java.util.Date; 
import java.util.Calendar;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;

public class ECAThread extends Thread {
	Timer countTimer;
	Timer timer;
	int serial;
	String DEVSerial;//用以找SOCName
	String con;
	String[] conContext;
	String SOCSerial;
	
	private Connection connection;
	private Statement statement;//SOC_Sta對應符合SOCName的調成否
	private String sql;
	private ResultSet rsECA;
	
	ECAThread (String[] threadTask,Connection c){
		super(); //產生thread物件並給予sum初值
		connection=c;
		serial=Integer.parseInt(threadTask[0]);
		DEVSerial=threadTask[1];
		con=threadTask[2];
		conContext = con.split("/");
		
		try {
			statement = connection.createStatement();
			sql="select DEV_SocSerial from DEVICE where DEV_Serial = '"+DEVSerial+"';";
			System.out.println(sql);
			rsECA = statement.executeQuery(sql);
			if(rsECA.next()){
				SOCSerial = rsECA.getString(1);
			}
		}catch(SQLException sqlException) {
			System.out.println("error in Thread get SocketName");
			sqlException.printStackTrace();
		}
		
		System.out.println("START "+DEVSerial+"by pass. Rule: "+ con);
	}
	public void revise(String[] re) {
		
	}
	
	public void run() {
		switch(conContext[0]){
		case "celsius":
			try {
				sql="update ARD set ARD_Det = 50 where ARD_Type = '"+conContext[0]+"' and ARD_Loc = '"+conContext[1]+"';";
				statement.executeUpdate(sql);
			}catch(SQLException sqlException) {
				sqlException.printStackTrace();
			}
			while(true){
				if(detect()==true)break;
			}
			this.setClose();
			break;
		case "light":
			while(true){
				if(detect()==true)break;
			}
			this.setClose();
			break;
		case "wet":
			while(true){
				if(detect()==true)break;
			}
			this.setClose();
			break;
		case "pass":
			String[] setTime = conContext[2].split(":");
			int countTime = (Integer.valueOf(setTime[0]))*60+Integer.valueOf(setTime[1]);
			try{ 
				Thread.currentThread().sleep(Integer.valueOf(countTime) * 1000); 
				System.out.println("END "+DEVSerial+" shut off by pass. Rule: "+ con);
				this.setClose();
			}catch (InterruptedException e) { 
				System.out.println(e);}

			break;
		case "time":
			int time = getTime(conContext[2]);
			try{ 
				Thread.currentThread().sleep(Integer.valueOf(time) * 1000); 
				System.out.println("END "+DEVSerial+" shut off by time. Rule: "+ con);
				this.setClose();
			}catch (InterruptedException e) { 
				System.out.println(e);} 
			break;
		case "infra":
			while(true){
				if(detect()==true)break;
			}
			this.setClose();
			break;
		default:
			break;
		}
	}
	boolean detect() {
		try {
			sql="select ARD_Det from ARD where ARD_Type = '"+conContext[0]+"' and ARD_Loc = '"+conContext[1]+"';";
			rsECA = statement.executeQuery(sql);
			if(rsECA.next()){
				System.out.println("type="+conContext[0]+", Det="+rsECA.getDouble("ARD_Det")+", con="+Double.parseDouble(conContext[3]));
				if(conContext[0].equals("celsius")){
					if(rsECA.getDouble("ARD_Det")<=Double.parseDouble(conContext[3])){
						return true;
					}else{
						return false;
					}
				}else if (rsECA.getDouble("ARD_Det")>=Double.parseDouble(conContext[3])){
					return true;
				}else return false;
			}else return false;
		}catch(SQLException sqlException) {
			System.out.println("error in step 1 with eca="+con);
			sqlException.printStackTrace();
			return false;
		}
	}
	void setClose() {
		try {
			sql="update SOCKET set SOC_Sta='關閉' where SOC_Serial='"+SOCSerial+"';";//將sql中PS的修改改為空白
			System.out.println("sql----"+sql);
			statement.executeQuery(sql);
		} catch (SQLException sqlException) {
			sqlException.printStackTrace();
		}
	}
	int getTime(String t){
		int time=0;
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		Date curDate = new Date(System.currentTimeMillis()) ; // 獲取當前時間
		String str = formatter.format(curDate);
		String[] curTime = str.split(":");
		int curTotalSec= Integer.valueOf(curTime[0])*60*60+Integer.valueOf(curTime[1])*60+Integer.valueOf(curTime[2]);
		
		String[] setTime = t.split(":");
		int setTotalSec= Integer.valueOf(setTime[0])*60*60+Integer.valueOf(setTime[1])*60+Integer.valueOf(setTime[2]);
		
		if(setTotalSec-curTotalSec<0) {
			time=60*60*24-curTotalSec+setTotalSec;
		}else{
			time=setTotalSec-curTotalSec;
		}
        return time;
	}
}


