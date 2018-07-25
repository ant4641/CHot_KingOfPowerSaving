import java.io.*;
import java.net.*;
import java.util.*;
import java.sql.*;
public class ShowHowCold extends Thread {
	private static final String url = "jdbc:mysql://10.0.0.1/ECASystem";
	private static final String username = "ECASystem";
	private static final String password = "ECASystem";
	private static Connection c = null;
	private static Statement statement ;
	
	public static void main(String[] args){
	
			BufferedReader br;
			String ADname;
			try {
			Class.forName("com.mysql.jdbc.Driver");
			c= DriverManager.getConnection(url, username, password); // 
			 
			//System.out.println("c.isClosed() : " +c.isClosed());
			if(c.isClosed()==true){
				System.out.println("資料庫連結失敗");
			}else{
				System.out.println("資料庫連結成功");
			}
			 statement = c.createStatement();
			 
			}catch(Exception e){
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.out.println("error here Connection 1!");
			//System.exit(0);
			}
			
			// br = new BufferedReader(new InputStreamReader(System.in));
			// System.out.println("要感測的數據類型? (1 : 溫度, 2 : 紅外線)");
			// ADname=br.readLine().trim();
			
			Thread getTem = new Thread(new Runnable(){	public void run() {
						ResultSet rs;
			while(true){	
		
				try{
						rs=statement.executeQuery("SELECT ARD_Det FROM ARD WHERE ARD_Type ='celsius' ;");
						while(rs.next()){
						System.out.println("---------");
						System.out.println("檢測 溫度 是 : "+rs.getString("ARD_Det"));
						System.out.println("---------");
						}
						
					// switch(ADname){
						// case "1" : {

						// }	
							// break;
						// }
						// case "2 : {
						// rs=statement.executeQuery("SELECT ARD_Det FROM ARD WHERE ARD_Type ='infra' ;");
						// while(rs.next()){
						// System.out.println("---------");
						// String ispeople=null;
						// ispeople=rs.getString("ARD_Det");
						// if(ispeople.equalus("0")){
							// System.out.println("檢測 紅外線 是 : 前方有人");
						// }else{
							// System.out.println("檢測 紅外線 是 : 前方無人");
						// }
						// System.out.println("---------");
						// }
								// break;
						// }
						// }
					
						
						
						
				}catch(SQLException e){		
					System.out.println("error here SQL 2!");
					e.printStackTrace();
					System.err.println(e);
				}catch (Exception ee) {
			
					ee.printStackTrace();
					System.out.println("Error here 4");
				} 
				
				try { 
				Thread.currentThread().sleep(1000); //每0.5秒檢測
				} catch (InterruptedException IE) { 
					IE.printStackTrace(); 
				}
					
			
			}
			}});
			getTem.setDaemon(true);
			getTem.start();
				
			
			while(true){
				try { 
				Thread.currentThread().sleep(1000); //每0.5秒檢測
				} catch (InterruptedException IE) { 
					IE.printStackTrace(); 
				}
			}
	}
	
}