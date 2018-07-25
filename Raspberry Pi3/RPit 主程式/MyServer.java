import java.io.*;
import java.net.*;
import java.util.*;
import java.sql.*;
public class MyServer extends Thread {
	
	private static String RPiSerialNumber="1";
		//NAS
	// private static final String url = "jdbc:mariadb://120.105.161.89/ECASystem";
		//rpi in lab with internet
	// private static final String url = "jdbc:mysql://120.105.161.80/ECASystem";
	private static final String url = "jdbc:mysql://10.0.0.1/ECASystem";  //樹莓派的資料庫位置
	private static final String username = "ECASystem"; 
	private static final String password = "ECASystem";
	private static Connection c = null;
	private static Statement statement ;
	private static ArrayList<MyThread> clients =null ;
	private static ArrayList<String> ADon = new  ArrayList<String>();
	private static ArrayList<String> ADdrop = new  ArrayList<String>();
	
	public static void main(String[] args){
		Socket s = null;
		ServerSocket ss = null;
		try {
			ss = new ServerSocket(2000);
			System.out.println("伺服器已啟動...");
			clients = new ArrayList<MyThread>();
			coonnection();
			checkADstate();
			//ADon.add("A3");
			
			//---------------
			// try{
				// int isdele=0;
				// isdele=statement.executeUpdate("DELETE FROM ARD WHERE ARD_Serial='A20170904001' ;");
				// System.out.println("isdele= "+isdele);
				// if(isdele==1)System.out.println("Delete  successfully !!");
				
				// int isup=0;
				// isup=statement.executeUpdate("Update ECA SET ECA_Con='light/房間/0500/40' WHERE ECA_Num='2';");
				// System.out.println("isup= "+isup);
				// if(isup==1)System.out.println("Update  successfully !!");
				
			// }catch(SQLException e){
				// System.out.println("error here SQL here 1 !");
				// System.err.println(e);
			// }
			//---------------
			
				Thread CHECKADt = new Thread(new Runnable(){	public void run() {
					while(true){	
						System.out.println("== AD Steak ==");
						for(int i=0;i<ADon.size();i++){
							System.out.println("ADon: "+i+" : "+ADon.get(i));
						}
						System.out.println("==============");
						
						System.out.println("// clients Steak //");
						for(int i=0;i<clients.size();i++){
							System.out.println("clients: "+i+" : "+clients.get(i).ADserial);
						}
						System.out.println("///////////////////");
						
						getADdrop();
						getADon();
						sendMessage();
						
						
						try { 
							Thread.currentThread().sleep(500); //每0.5秒檢測
						} catch (InterruptedException IE) { 
							IE.printStackTrace(); 
						} 
					}
				}});
				CHECKADt.setDaemon(true);
				CHECKADt.start();

			try{
				while(true){
				s = ss.accept();
				MyThread t = new MyThread(s,c);
				new Thread(t).start();
				clients.add(t);
				for (int i=0 ; i<clients.size(); i++){
					 if(clients.get(i).ADserial!=null) System.out.println("第"+(i+1)+"位連接者"+clients.get(i).ADserial);
				 }
				}
			}catch(SocketTimeoutException STE){
					
					STE.printStackTrace();
					System.out.println("Error timeout ");
			}
			
		}catch (IOException e) {
			
			e.printStackTrace();
			System.out.println("Error here 4");
		} 
	}

	
	public static void getADon(){
		try{
			ResultSet rs,rs2;
			String[][] ecas = new String[0][4];
			String[] adtype =new String[0];
			String[] adLocation =new String[0];
			int count=0;
			int putin=0; //把從DB取出的值放入陣列的計數

			rs = statement.executeQuery("SELECT ECA.ECA_Num,ECA.ECA_Con FROM ECA,DEVICE,SOCKET WHERE SOCKET.SOC_Sta='開啟' AND DEVICE.DEV_SOCSerial=SOCKET.SOC_Serial AND ECA.ECA_DevSerial= DEVICE.DEV_Serial;");
			while(rs.next()){count++;}
			ecas = new String[count][4];
			adtype = new String[count];
			adLocation = new String[count];
			count=0;
			
			rs = statement.executeQuery("SELECT ECA.ECA_Num,ECA.ECA_Con FROM ECA,DEVICE,SOCKET WHERE SOCKET.SOC_Sta='開啟' AND DEVICE.DEV_SOCSerial=SOCKET.SOC_Serial AND ECA.ECA_DevSerial= DEVICE.DEV_Serial;");
			//System.out.println("ECA序號\tEC條件");
			
			while(rs.next()){
				//System.out.println(rs.getString("ECA_Num")+"\t"+rs.getString("ECA_Con"));
				ecas[putin]=rs.getString("ECA_Con").split("/");
				//System.out.println("ecas["+putin+"][0] : "+ecas[putin][0]);
				adtype[putin]=ecas[putin][0];
				adLocation[putin]=ecas[putin][1];
				putin++;
			}
			putin=0;
			rs.close();
			
			int repeat=0;
			for(int i=0;i<adtype.length;i++){
				//System.out.println("adtype: "+i+" : "+adtype[i]);
				rs=statement.executeQuery("SELECT ARD_Serial FROM ARD WHERE ARD_Type ='"+adtype[i]+"' AND ARD_Loc='"+adLocation[i]+"';");
				if(rs.next()){
					for(int k=0;k<ADon.size();k++){
						if(ADon.get(k).equals(rs.getString("ARD_Serial"))){
							repeat++;
						}
					}
					if(repeat==0){
						System.out.println("===新增的AD : "+rs.getString("ARD_Serial"));
						ADon.add(rs.getString("ARD_Serial"));

					}else{
						//System.out.println("已存在: "+rs.getString("ARD_Serial"));
					}
					repeat=0;
				}
			}
			
		}catch(SQLException e){
			System.out.println("error here SQL!");
	    	System.err.println(e);
		}
		
	}

	public static void getADdrop(){
		try{
			// System.out.println("---------------刪除不用的AD-----------------");
			// System.out.println("--------------------------------------------");
			ResultSet rs,rs2;
			String[][] ecas = new String[0][4];
			String[] adtype =new String[0];
			String[] adLocation =new String[0];
			int count=0;
			int putin=0; //把從DB取出的值放入陣列的計數
			String mas="";
			
			rs = statement.executeQuery("SELECT ECA.ECA_Num,ECA.ECA_Con FROM ECA,DEVICE,SOCKET WHERE SOCKET.SOC_Sta='關閉' AND DEVICE.DEV_SOCSerial=SOCKET.SOC_Serial AND ECA.ECA_DevSerial= DEVICE.DEV_Serial;");
			while(rs.next()){	count++;}
			ecas = new String[count][4];
			adtype =new String[count];
			adLocation = new String[count];
			count=0;
			
			rs = statement.executeQuery("SELECT ECA.ECA_Num,ECA.ECA_Con FROM ECA,DEVICE,SOCKET WHERE SOCKET.SOC_Sta='關閉' AND DEVICE.DEV_SOCSerial=SOCKET.SOC_Serial AND ECA.ECA_DevSerial= DEVICE.DEV_Serial;");
			//System.out.println("ECA序號\tEC條件");
			
			while(rs.next()){
				//System.out.println(rs.getString("ECA_Num")+"\t"+rs.getString("ECA_Con"));
				ecas[putin]=rs.getString("ECA_Con").split("/");
				//System.out.println("ecas["+putin+"][0] : "+ecas[putin][0]);
				adtype[putin]=ecas[putin][0];
				adLocation[putin]=ecas[putin][1];
				putin++;
			}
			putin=0;
			rs.close();
			
			for(int i=0;i<adtype.length;i++){
				//System.out.println("adtype: "+i+" : "+adtype[i]);
				rs=statement.executeQuery("SELECT ARD_Serial FROM ARD WHERE ARD_Type ='"+adtype[i]+"' AND ARD_Loc='"+adLocation[i]+"';");
				if(rs.next()){
					//將沒有開啟的插座關掉
					for(int k=0; k<ADon.size();k++){
						boolean isIn = ADon.contains(rs.getString("ARD_Serial"));
						if(isIn==true){
							System.out.println("===丟掉的AD : "+rs.getString("ARD_Serial"));
							rs2=statement.executeQuery("SELECT SOC_Serial FROM SOCKET WHERE SOCKET.SOC_Sta='關閉' ;" );
							while(rs2.next()){
								for (int p=0 ; p<clients.size(); p++){
									// System.out.println("陣列中該關的插座: "+clients.get(p).ADserial);
									// System.out.println("ECA 中該關的插座: "+rs2.getString("SOC_Serial"));
									if(clients.get(p).ADserial.equals(rs2.getString("SOC_Serial"))){
									checkClientOnline(p);
									clients.get(p).send("0");
									mas=clients.get(p).receive();							
									System.out.println(clients.get(p).s.getRemoteSocketAddress()+ " 傳遞的值是：" +mas);
									dealmessage2(clients.get(p).ADserial,mas);
									ADon.remove(rs2.getString("SOC_Serial"));
									Dropclients2(rs2.getString("SOC_Serial"));
									}
								}
							}
							//將無用的AD關閉
							
							String ADisDown=rs.getString("ARD_Serial");
							// int isADon = Integer.parseInt(ADisDown);
							// checkClientOnline(isADon);
							ADon.remove(rs.getString("ARD_Serial"));
						
							try{
								int isup=0;
								isup=statement.executeUpdate("Update ARD SET ARD_Status='1' , ARD_Det='0' WHERE ARD_Serial='"+ADisDown+"';");
								if(isup==1)System.out.println("Update ARD: "+ADisDown+" state = 1 successfully !!");
								
							}catch(SQLException e){
								System.out.println("error here SQL here 9711 !");
								System.err.println(e);
							}
						}
					}
				}
			}
			
		}catch(SQLException e){		
			System.out.println("error here SQL 8777!");
			e.printStackTrace();
	    	System.err.println(e);
		}
		catch(Exception e){		
			System.out.println("error here exc 8777!");
			e.printStackTrace();
	    	System.err.println(e);
		}
		
	}
		
	public static void  dealmessage(String ADserial, int msg){
		//要要求收到的AD's ADserial 才可以存進DB
		try{
			int k=0;
			//System.out.println("val: "+val);
			k=statement.executeUpdate("UPDATE ARD SET ARD_Det = '"+msg+"' WHERE ARD_Serial = '"+ADserial+"';");
			if(k==1)System.out.println(ADserial+" 偵測值更新成功");
		}catch(SQLException e){
			System.out.println("error here SQL 3!");
			System.err.println(e);
		}		
				
	}
	
	public static void  dealmessage2(String ADserial, String msg){
		switch(msg){
			case "/over": {
				System.out.println("插座 : "+ADserial+" 已經關閉");
				
			}
			default:{	
			}
		}
	}
	public static void sendMessage(){
		try{
			int value=0;
			String ADname="";
				//準備針對在陣列中的AD呼叫回傳感測值
			if(clients.size()!=0){
				for(int j=0 ; j<ADon.size(); j++){
						ADname=ADon.get(j);
					for (int i=0 ; i<clients.size(); i++){
						if(clients.get(i).ADserial.equals(ADname)){
						clients.get(i).send("1");//取得偵測值
					
						value=Integer.parseInt(clients.get(i).receive());	
						if(value<=1500){			
							System.out.println(clients.get(i).s.getRemoteSocketAddress()+ " 傳遞的值是：" +value);
							dealmessage(clients.get(i).ADserial,value);
					
						}else{
							System.out.println("等待 "+ADname+"檢測中");
							}
						}
						
					}
				}	
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void Dropclients(int ser){
		clients.remove(ser);
			for(int i=0 ; i<clients.size(); i++){
			System.out.println("Client "+i+" :"+clients.get(i));
		}
		
	}
	
	public static void Dropclients2(String ADs){
		for(int i=0 ; i<clients.size(); i++){
			if(clients.get(i).ADserial.equals(ADs)){
				System.out.println("Client "+i+" : "+clients.get(i).ADserial+"已經下線");
				clients.remove(i);
				break;
			}
		}

		for(int i=0 ; i<clients.size(); i++){
			System.out.println("Client "+i+" :"+clients.get(i));
		}
		
	}
	public static void checkClientOnline(int num){
		String value="";
		try{
			String SSname=clients.get(num).ADserial.substring(0,1);
			System.out.println("SSname : "+clients.get(num).ADserial);
			if(SSname.equals("S")){
				clients.get(num).send("2");//取得偵測值
				value=clients.get(num).receive();
				if(!value.equals("yes")){
					System.out.println("Client "+num+" : "+clients.get(num).ADserial+"已經下線");
				}
				
			}
			
		
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("Error checkClientOnline -1");
		}
		
	}
	
		public static void checkClientOnline2(int num){
		String value="";
		try{
			String SSname=clients.get(num).ADserial.substring(0,1);
			System.out.println("ADname : "+clients.get(num).ADserial);
			if(SSname.equals("A")){
				clients.get(num).send("2");//取得偵測值
				value=clients.get(num).receive();
				if(!value.equals("yes")){
					System.out.println("Client "+num+" : "+clients.get(num).ADserial+"已經下線");
				}
				
			}
			
		
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("Error checkClientOnline -1");
		}
		
	}
	
	
	public static void HandsendMessage(){ //手動傳遞訊息
		// BufferedReader br,brr;
		// String value="";
		// String Cname="";
		// String state; 	
	// try{
		// br = new BufferedReader(new InputStreamReader(System.in));
		// brr = new BufferedReader(new InputStreamReader(System.in));
		// while(true){

			// System.out.println("Please enter the Client Name : "); 
			// Cname="";
			// Cname=br.readLine().trim();

			// for (int i=0 ; i<clients.size(); i++){
				// if(clients.get(i).ADserial.equals(Cname)){
					// System.out.println("Please input a order (1/0):"); 
					// state=brr.readLine().trim();
	
					// switch(state){
					// case "1" : {
						// System.out.println("Order is : "+state);
						// clients.get(i).send(state);
						// value=clients.get(i).receive();							
						// System.out.println(clients.get(i).s.getRemoteSocketAddress()+ " 傳遞的值是：" +value);
						// dealmessage(clients.get(i).ADserial,value);
						// break;
					// }
					// case "0" : { //要求socket關電源
						// System.out.println("Order is : "+state);
						// clients.get(i).send(state);
						// value=clients.get(i).receive();
						// System.out.println(clients.get(i).s.getRemoteSocketAddress()+ " 傳遞的值是：" +value);
						//dealmessage(clients.get(i).name,value);
						// break;
					// }
					// default: 
					 // System.out.println("請輸入1/0 "); 
					// }
				// }// if over
			// }//for over
		// }//while over
	// }catch(IOException e){
		// e.printStackTrace();
	// }		
	}
	
	public static void checkADstate(){  //確認資料庫內容，可本機或RPI
		ResultSet rs;
		int k=0;
	try{
		//插座部分
		// k=0;
		// statement.executeUpdate("drop table if exists SOCKET");
		// k=0;
		// k=statement.executeUpdate("CREATE TABLE SOCKET(SOC_Serial VARCHAR(30) PRIMARY KEY NOT NULL,SOC_Name VARCHAR(50) NOT NULL,SOC_Sta VARCHAR(50) NOT NULL,SOC_RaspSerial VARCHAR(50) NOT NULL);");
		// if(k==1)System.out.println("k= "+k);
		// System.out.println("Create SOCKET successfully");
		
		// k=0;
		// k=statement.executeUpdate("insert into SOCKET values('S1','kitch','開啟','1')");
		// if(k==1)System.out.println("k= "+k);
		// System.out.println("Insert SOCKET successfully !!");
		
		// k=0;
		// k=statement.executeUpdate("insert into SOCKET values('S2','roon','開啟','1')");
		// if(k==1)System.out.println("k= "+k);
		// System.out.println("Insert SOCKET successfully !!");
		
		// k=0;
		// k=statement.executeUpdate("insert into SOCKET values('S3','living','關閉','1')");
		// if(k==1)System.out.println("k= "+k);
		// System.out.println("Insert SOCKET successfully !!");

		System.out.println("--------------------------------------------");
		System.out.println("編號\t插座\t電源\t對應樹梅派");
		// rs = statement.executeQuery("SELECT * FROM SOCKET WHERE SOC_Sta='開啟' ;");
		rs = statement.executeQuery("SELECT * FROM SOCKET ;");
		while(rs.next()){
			System.out.println(rs.getString("SOC_Serial")+"\t"+rs.getString("SOC_Name")+"\t"+rs.getString("SOC_Sta")+"\t"+rs.getInt("SOC_RaspSerial"));		
		}
		rs.close();
		System.out.println("--------------------------------------------");
		
		//會員部分
		// k=0;
		// k=statement.executeUpdate("CREATE TABLE MEMBER(MEM_Acc VARCHAR(50) PRIMARY KEY NOT NULL,MEM_Pass VARCHAR(50) NOT NULL,MEM_Name VARCHAR(50) NOT NULL,MEM_Email VARCHAR(50) NOT NULL);");
		// if(k==1)System.out.println("k= "+k);
		// System.out.println("Create MEMBER successfully");
		
		// RPI部分
		// k=0;
		// k=statement.executeUpdate("CREATE TABLE RASP(RASP_Serial INTEGER PRIMARY KEY AUTOINCREMENT,RASP_Name VARCHAR(50) NOT NULL,RASP_Place VARCHAR(50) NOT NULL,RASP_IsUse VARCHAR(50) NOT NULL,RASP_Acc VARCHAR(50) NOT NULL,FOREIGN KEY(RASP_Acc) REFERENCES MEMBER(MEM_Acc)  ON UPDATE CASCADE);");
		// if(k==1)System.out.println("k= "+k);
		// System.out.println("Create RASP successfully");
		
		//電器部分
		// k=0;
		// statement.executeUpdate("drop table if exists DEVICE");
		// k=statement.executeUpdate("CREATE TABLE DEVICE(DEV_Serial INTEGER PRIMARY KEY AUTOINCREMENT ,DEV_Name VARCHAR(50) NOT NULL,DEV_SOCSerial VARCHAR(50) NOT NULL,FOREIGN KEY(DEV_SOCSerial) REFERENCES SOCKET(SOC_Serial) ON UPDATE CASCADE);");
		// if(k==1)System.out.println("k= "+k);
		// System.out.println("Create DEVICE successfully");
		
		// k=0;
		// k=statement.executeUpdate("insert into DEVICE(DEV_Name,DEV_SOCSerial) values('Oven','S1')");
		// if(k==1)System.out.println("k= "+k);
		// System.out.println("Insert DEVICE successfully !!");
		
		// k=0;
		// k=statement.executeUpdate("insert into DEVICE(DEV_Name,DEV_SOCSerial) values('Lamp','S2')");
		// if(k==1)System.out.println("k= "+k);
		// System.out.println("Insert DEVICE successfully !!");
		
		// k=0;
		// k=statement.executeUpdate("insert into DEVICE(DEV_Name,DEV_SOCSerial) values('TV','S3')");
		// if(k==1)System.out.println("k= "+k);
		// System.out.println("Insert DEVICE successfully !!");

		System.out.println("--------------------------------------------");
		System.out.println("電器序號\t名稱\t對應插座序號");
		rs = statement.executeQuery("SELECT * FROM DEVICE  ;");
		while(rs.next()){
			System.out.println(rs.getString("DEV_Serial")+"\t\t"+rs.getString("DEV_Name")+"\t"+rs.getString("DEV_SOCSerial"));	
		}
		rs.close();
		System.out.println("--------------------------------------------");
		
		//AD部分		
		// k=0;
		// statement.executeUpdate("drop table if exists ARD");
		// k=statement.executeUpdate("CREATE TABLE ARD(ARD_Serial VARCHAR(50) PRIMARY KEY NOT NULL,ARD_Name VARCHAR(50) NOT NULL,ARD_Type VARCHAR(50) NOT NULL,ARD_Status INT NOT NULL,ARD_Loc VARCHAR(50) NOT NULL,ARD_Det INT NOT NULL,ARD_RaspSerial  VARCHAR(50) NOT NULL);");
		// if(k==1)System.out.println("k= "+k);
		// System.out.println("Create ARD successfully");
		
		// k=0;
		// k=statement.executeUpdate("insert into ARD values('A1','koo','celsius','1','廚房',0,'1')");
		// if(k==1)System.out.println("k2= "+k);
		// System.out.println("Insert ARD data1 successfully");
		// k=0;
		// k=statement.executeUpdate("insert into ARD values('A2','mimo','light','1','房間',0,'2')");
		// if(k==1)System.out.println("k3= "+k);
		// System.out.println("Insert ARD data2 successfully");
				// k=0;
		// k=statement.executeUpdate("insert into ARD values('A3','jiji','wet','1','客廳',0,'2')");
		// if(k==1)System.out.println("k3= "+k);
		// System.out.println("Insert ARD data2 successfully");
		
		System.out.println("--------------------------------------------");		
		System.out.println("ARD編號\t名稱\t類型\t位置\t狀態\t感測值");		
		rs = statement.executeQuery("SELECT * FROM ARD ;");
		while(rs.next()){
			System.out.println(rs.getString("ARD_Serial")+"\t"+rs.getString("ARD_Name")+"\t"+rs.getString("ARD_Type")+"\t"+rs.getString("ARD_Loc")+"\t"+rs.getInt("ARD_Status")+"\t"+rs.getInt("ARD_Det"));
		}
		rs.close();
		System.out.println("--------------------------------------------");

		//ECA部分		
		// k=0;
		// statement.executeUpdate("drop table if exists ECA");
		// k=statement.executeUpdate("CREATE TABLE ECA(ECA_Num INTEGER PRIMARY KEY AUTOINCREMENT,ECA_DevSerial VARCHAR(50) NOT NULL,ECA_Con VARCHAR(50) NOT NULL,ECA_Ps VARCHAR(50) NOT NULL,ECA_RaspSerial VARCHAR(50) NOT NULL,FOREIGN KEY(ECA_DevSerial) REFERENCES DEVICE(DEV_Name) ON UPDATE CASCADE);");
		// if(k==1)System.out.println("k= "+k);
		// System.out.println("Create ECA successfully");
		
		// k=0;
		// k=statement.executeUpdate("insert into ECA(ECA_DevSerial,ECA_Con,ECA_RaspSerial) values('1','celsius/廚房/0500/26','1')");
		// if(k==1)System.out.println("k= "+k);
		// System.out.println("Insert ECA1 successfully !!");
		
		// k=0;
		// k=statement.executeUpdate("insert into ECA(ECA_DevSerial,ECA_Con,ECA_RaspSerial) values('2','light/房間/0500/40','1')");
		// if(k==1)System.out.println("k= "+k);
		// System.out.println("Insert ECA2 successfully !!");
		
		// k=0;
		// k=statement.executeUpdate("insert into ECA(ECA_DevSerial,ECA_Con,ECA_RaspSerial) values('3','wet/客廳/0500/40','1')");
		// if(k==1)System.out.println("k= "+k);
		// System.out.println("Insert ECA2 successfully !!");
		
		System.out.println("--------------------------------------------");
		System.out.println("ECA序號\t電序\t條件");
		rs = statement.executeQuery("SELECT * FROM ECA ;");
		while(rs.next()){
			System.out.println(rs.getString("ECA_Num")+"\t"+rs.getString("ECA_DevSerial")+"\t"+rs.getString("ECA_Con"));	
		}
		rs.close();
		System.out.println("--------------------------------------------");

	}catch(SQLException e){
			System.out.println("error here SQL 2!");
	    	System.err.println(e);
	}
	}
	
	public static void coonnection(){
		ResultSet rs;
		try {
			Class.forName("org.sqlite.JDBC");
			// c = DriverManager.getConnection("jdbc:sqlite:C:/Program Files (x86)/DaliyWorks/RPI/RPitoAD/sqlite/ecasystem.db");
			// System.out.println("資料庫連線成功");
			c= DriverManager.getConnection(url, username, password); // 
			 
			System.out.println("c.isClosed() : " +c.isClosed());
			if(c.isClosed()==true){
				System.out.println("資料庫連結失敗");
			}else{
				System.out.println("資料庫連結成功");
			}
			 statement = c.createStatement();
			 statement.setQueryTimeout(30);  // set timeout to 30 sec.
			try{
				int isup=0;
				isup=statement.executeUpdate("Update RASP SET RASP_IsUse='1' WHERE RASP_Serial='"+RPiSerialNumber+"';");
				System.out.println("isup= "+isup);
				if(isup==1)System.out.println("Update RPi state successfully !!");
				
			}catch(SQLException e){
				System.out.println("error here SQL here 1087 !");
				System.err.println(e);
			}
		}catch(Exception e){
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.out.println("error here SQL 3!");
			System.exit(0);
		}
	}
}

// Random r = new Random();
// int upCase = r.nextInt(26)+65;//得到65-90的隨機數
// int downCase = r.nextInt(26)+97;//得到97-122的隨機數
// String up =String.valueOf((char)upCase);//得到A-Z
// String down =String.valueOf((char)downCase);得到a-z