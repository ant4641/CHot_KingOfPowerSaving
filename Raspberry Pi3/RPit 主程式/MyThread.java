import java.io.*;
import java.net.*;
import java.util.*;
import java.sql.*;
public class MyThread  extends Thread {
    public Socket s = null;
	public PrintWriter out ;
	public String ADserial = null;
	private static Connection c = null;
	private static Statement statement ;
	public BufferedReader r,br,brr;

	MyThread(Socket soc,Connection co){
		this.s = soc;
		c=co;
		coonnection();
	}
	
	public void send(String msg){
		out.println(msg);
	}
	
	public String receive(){
		String str="";
		try{
			str=r.readLine();
		}catch(IOException e){
			MyServer.Dropclients2(ADserial);
			e.printStackTrace();
			System.out.println("Error MT 1");
		}
		return str;
	}
	public void stoplink(){
		try{
			s.close();
		}catch(IOException e){
			e.printStackTrace();
			System.out.println("Error MT 2");
		}
	}
	
    public void run() {
		ResultSet rs,rs2;
		try{
			System.out.println(s.getRemoteSocketAddress() + "　已連線...");
			r = new BufferedReader(new InputStreamReader(s.getInputStream()));
			out = new PrintWriter(new OutputStreamWriter(s.getOutputStream()), true);
			ADserial=r.readLine();
			out.println("你好．．．我是伺服器，這是伺服器的訊息");
					
			String type=ADserial.substring(0,1);
			//System.out.print(type);
			int isup=0;
			switch(type){
				case "S":{
					rs = statement.executeQuery("SELECT SOC_Serial FROM SOCKET WHERE SOC_Serial='"+ADserial+"';");
					if(!rs.next()){
						isup=0;
						isup=statement.executeUpdate("Insert into SOCKET values('"+ADserial+"','noname','關閉','1')");
						System.out.println("isup= "+isup);
						System.out.println("Insert SOCKET successfully !!");
					}else{
						isup=0;
						isup=statement.executeUpdate("UPDATE SOCKET SET SOC_Sta='開啟' WHERE SOC_Serial='"+ADserial+"';");
						//System.out.println("isup= "+isup);
						if(isup==1)System.out.println("SOCKET "+ADserial+" 已開啟");
					}
					break;
				}
				case "A":{
					String[] k =new String[2];
					k=ADserial.split("/");
					ADserial=k[0];
					rs = statement.executeQuery("SELECT ARD_Serial FROM ARD WHERE ARD_Serial='"+k[0]+"';");
					if(!rs.next()){
						isup=0;
						//插入值為測試用，要待改成完整版
						System.out.println("New AD's TYPE : "+k[1]);
						isup=statement.executeUpdate("insert into ARD values('"+k[0]+"','noname','"+k[1]+"','1','客廳',0,'1')");
						System.out.println("isup= "+isup);
						System.out.println("Insert ARD successfully !!");
					}else{
						System.out.println("Arduino : "+k[0]+" 已連線");
						try{
							rs2=statement.executeQuery("SELECT ARD_Status FROM ARD WHERE ARD_Serial ='"+rs.getString("ARD_Serial")+"' AND ARD_Status='1' ;");
							if(rs2.next()){
								isup=0;	
								isup=statement.executeUpdate("Update ARD SET ARD_Status='2' WHERE ARD_Serial='"+rs.getString("ARD_Serial")+"';");
								if(isup==1)System.out.println("Update ARD: "+rs.getString("ARD_Serial")+" state = 2 successfully !!");
							}
						}catch(SQLException e){
							System.out.println("error here SQL here 971 !");
							System.err.println(e);
						}
					}
					break;
				}
				default: {
					break;
				}
			}
		}catch(Exception e) {
			System.out.println("Error MT 3");
			e.printStackTrace();
		}
    }

	public static void coonnection(){
		try {
			statement = c.createStatement();
			//statement.setQueryTimeout(30);  // set timeout to 30 sec.
		}catch(Exception e){
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.out.println("Error MT 4");
			System.exit(0);
		}
	}
}	
// 3.ARD:
// Arduino序號PK(ARD_Serial)、
// Arduino名稱(ARD_Name)、
// Arduino檢測類型(ARD_Type [wet、celsius、light])、
// Arduino連線狀態(ARD_Status[待機(1)、準備檢測(2)、檢測中(3)])
// Arduino擺設位置(ARD_Loc)、
// 檢測值(ARD_Det)、
// 對應樹梅派ARD_RaspSerial

// 4.SOCKET: //ARD版的Scoket
// 插座序號PK(SOC_Serial)、
//插座名稱PK(SOC_Name #TEXT)、
// 是否開啟(SOC_Sta 關閉/開啟)、
// SOC_RaspSerial(對應樹梅派)