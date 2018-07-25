import java.util.ArrayList;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.lang.Thread.*;

public class ECATest {
	//需要有樹莓派序號、使用者帳號
	int RaspSerial=1;
	String Account="";
	//NAS
	// static final String url = "jdbc:mariadb://120.105.161.89/ECASystem";
	//rpi in lab with internet
	// static final String url = "jdbc:mysql://120.105.161.80/ECASystem";
	static final String url = "jdbc:mysql://10.0.0.1/ECASystem";
	static final String username = "ECASystem"; // 要調整
	static final String password = "ECASystem";
	private Connection con;
	private Statement statementForECARule,statementForDevice,statementForReviseEca;
	private ResultSet rsIseUse,rsECA,rsDevice,rsReviseEcaPs;
	private ResultSetMetaData rsECAMetaData,rsDeviceMetaData;

	//ECA
	static public ArrayList<String[]> eca = new ArrayList<String[]>();
	static public ArrayList<ECAThread> ecaT = new ArrayList<ECAThread>();
	
	public ECATest(int rasp, String Acc){
		RaspSerial=rasp;
		Account=Acc;
		//連接DB
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(url, username, password); 
			// Class.forName("org.sqlite.JDBC");
			// con = DriverManager.getConnection("jdbc:sqlite:C:/Program Files (x86)/DaliyWorks/RPI/RPitoAD/sqlite/ecasystem.db");
			System.out.println("con.isClosed() : " +con.isClosed());
			if(con.isClosed()==true){
				System.out.println("資料庫連結失敗");
			}else{
				System.out.println("資料庫連結成功");
			}
			statementForECARule = con.createStatement();
			statementForDevice = con.createStatement();
			statementForReviseEca = con.createStatement();

		} catch (SQLException sqlException) {
			sqlException.printStackTrace();
		}catch(Exception e){
			System.out.println("error here Exception 3!");
			System.exit(0);
		}
		
		//主要迴圈START----------------------------
		while(true) {
			String sqlForIsUse="select * from RASP where RASP_Serial='"+RaspSerial+"' AND RASP_IsUse = 1;";
			try {
				rsIseUse = statementForECARule.executeQuery(sqlForIsUse);
				if(rsIseUse.next()) {
					//socket-soc_name, soc_sta
					//advice-adv_name, adv_socname
					//eca-eca_num, eca_advname, eca_con, eca_ps
					
					/*1.子查詢找開啟的socket，用以查詢相關的ECA rules：
					    若序號不存在於ECA、且非修改刪除者，放入其中*/
					String sqlForDevice="select * from DEVICE where DEV_SocSerial IN (select SOC_Serial from SOCKET where SOC_Sta='關閉' AND SOC_RaspSerial ='"+RaspSerial+"');";
					String sqlForECARule="select * from ECA where eca_DevSerial IN (select DEV_Serial from DEVICE where DEV_SocSerial IN (select SOC_Serial from SOCKET where SOC_Sta = '開啟' AND SOC_RaspSerial ='"+RaspSerial+"'));";
					try {
						rsECA = statementForECARule.executeQuery(sqlForECARule);
						rsECAMetaData = rsECA.getMetaData();
						int ecaNum = eca.size();//eca原本的size
						while (rsECA.next()){
							int ifPutIn=1;///判斷是否需要放入
							for(int i=0;i<ecaNum;i++) {
								String[] ecaCheck = eca.get(i);
								int serial = Integer.parseInt(ecaCheck[0]);
		  			    			if(Integer.parseInt(rsECA.getObject(1).toString())==serial) {//若為本來存在於eca中的serial則不放入
		  			    				ifPutIn=0;
		  			    				break;
		  			    			}
							}
							if(ifPutIn==1) {//放入非本來存在於eca之ECA rule
			    					String[] getECA = new String[rsECAMetaData.getColumnCount()];
				    				for(int j =0;j<rsECAMetaData.getColumnCount();j++) {
				    					getECA[j] = rsECA.getString(j+1);
				    				}
				    				eca.add(getECA);
				    			}
		  			    }
						
					}catch(SQLException sqlException) {
						System.out.println("error in step 1");
						sqlException.printStackTrace();
					}
					//將eca_num, eca_advname, eca_con 放入 eca
					
					//2.修改ECA－資料庫看備註是修改，且正在用該插座的（照理來說一定是正在用才寫修改）
					//找sqlForECARule中eca_ps=修改
					//對原存在ECA中相同序號之內容進行修改
					try {
						rsECA = statementForECARule.executeQuery(sqlForECARule);
						rsECAMetaData = rsECA.getMetaData();
						
						String sqlForECAPsR;
						while (rsECA.next()){
							if(rsECA.getObject(4).toString().equals("修改")) {//如果需要啟動的ECA rule需修改
								for(int i=0;i<eca.size();i++) {//找尋eca中序號一樣者，進行修改
									String[] ecaRevise = eca.get(i);
									int serial = Integer.parseInt(ecaRevise[0]);
									if(Integer.parseInt(rsECA.getObject(1).toString())==serial) {
										for(int j=1;j<rsECAMetaData.getColumnCount()-1;j++) {
											ecaRevise[j]=rsECA.getObject(j+1).toString();
										}
										ecaRevise[3]="";
										eca.set(i, ecaRevise);
										sqlForECAPsR="update ECA set ECA_Ps='' where ECA_Num='"+ecaRevise[0]+"';";//將sql中PS的修改改為空白
										statementForReviseEca.executeQuery(sqlForECAPsR);
										//看是否要用result判斷有沒有成功、是否需要Query
										break;
									}
		  			    			}
							}
		  			    }
					}catch(SQLException sqlException) {
						System.out.println("error in step 2");
						sqlException.printStackTrace();
					}
					//3.刪除ECA－
						//a.刪除已沒有在用的插座相關 ECA
					try {
						rsDevice = statementForDevice.executeQuery(sqlForDevice);//找出正關閉的電器
						rsDeviceMetaData = rsDevice.getMetaData();
						
						while (rsDevice.next()){
							int[] ifDelete = new int[eca.size()];//陣列對應各個eca
							for(int i=0;i<eca.size();i++) {
								ifDelete[i]=0;
								String[] ecaDelete = eca.get(i);
								System.out.println("ecaDelete[1]="+ecaDelete[1]);
								System.out.println("rsDevice.getObject(1)="+rsDevice.getObject(1));
								if(rsDevice.getObject(1).toString().equals(ecaDelete[1].toString())) {//若關閉的電器名等於eca中的serial，將對應的ifDelete改為1
									ifDelete[i]=1;
									System.out.println("------------ifDelete[i]="+i+" "+ifDelete[i]);
								}
							}

							for(int j=ifDelete.length-1;j>-1;j--) {//從後往前刪除關閉的電器之eca
								System.out.println("ifDelete[j]="+j+" "+ifDelete[j]);
								if(ifDelete[j]==1) {
									System.out.println("j="+j);
									eca.remove(j);
								}
							}
		  			    }
					}catch(SQLException sqlException) {
						System.out.println("error in step 3-a");
						sqlException.printStackTrace();
					}	
					
						/*b.刪除資料庫備註是已刪除，且正在用該插座的（照理來說一定是正在用才寫刪除）
						找sqlForECARule中eca_ps=刪除*/
					try {
						rsECA = statementForECARule.executeQuery(sqlForECARule);
						rsECAMetaData = rsECA.getMetaData();
						String sqlForECAPsD;
						while (rsECA.next()){
							if(rsECA.getObject(4).toString().equals("刪除")) {//如果需要啟動的ECA rule需刪除
								System.out.println("-------del-------------"+rsECA.getObject(1).toString());
								for(int i=0;i<eca.size();i++) {//找尋eca中序號一樣者，進行刪除
									String[] ecaDelete = eca.get(i);
									int serial = Integer.parseInt(ecaDelete[0]);
									if(Integer.parseInt(rsECA.getObject(1).toString())==serial) {
										eca.remove(i);
										sqlForECAPsD="delete from ECA where ECA_Num='"+ecaDelete[0]+"';";//將DB中該筆項目刪除
										statementForReviseEca.executeQuery(sqlForECAPsD);
										//看是否要用result判斷有沒有成功、是否需要Query
										break;
									}
		  			    			}
							}
		  			    }
					}catch(SQLException sqlException) {
						System.out.println("error in step 3-b");
						sqlException.printStackTrace();
					}
					System.out.println("ECAsize="+eca.size());
					
					//4.用ECA
						//a.建立ECAThread中未存在該 ECA 序號之 ECAThread
					int ECATNum = ecaT.size();
					for(int i=0;i<eca.size();i++) {
						String[] ecaGet = eca.get(i);
						int ifNewECAT=1;
						for(int j=0;j<ECATNum;j++) {
							if(Integer.parseInt(ecaGet[0])==ecaT.get(j).serial) {
								ifNewECAT=0;
								break;
							}
						}if(ifNewECAT==1) {
							ecaT.add(new ECAThread(ecaGet,con));
							ecaT.get(ecaT.size()-1).start();
						}
					}
						//b.比對在ECA中存在序號，但內容不同的做修改
					ECATNum = ecaT.size();
					for(int i=0;i<eca.size();i++){
						String[] ecaGet = eca.get(i);
						for(int j=0;j<ECATNum;j++) {
							if(Integer.parseInt(ecaGet[0])==ecaT.get(j).serial){
								if(!ecaGet[1].equals(ecaT.get(j).DEVSerial)||!ecaGet[2].equals(ecaT.get(j).con)) {
									i=1;
									ecaT.get(j).interrupt();
									ecaT.remove(j);
									ecaT.add(new ECAThread(ecaGet,con));
									ecaT.get(ecaT.size()-1).start();
								}
							}
						}
					}
						//c.比對並刪除未存在之ECA序號的ECAThread
					ECATNum = ecaT.size();
					
					for(int i=ECATNum-1;i>=0;i--){
						int ecaTSerial = ecaT.get(i).serial;
						int ifDelete=1;
						for(int j=0;j<eca.size();j++) {
							String[] ecaGet=eca.get(j);
							if(Integer.parseInt(ecaGet[0])==ecaTSerial){
								ifDelete=0;
								break;
							}
						}
						System.out.println("ifDelete="+ifDelete);
						if(ifDelete==1) {
							ecaT.get(i).interrupt();
							ecaT.remove(i);
						}
					}
				}
				//5.間隔五秒
				try { 
					Thread.currentThread().sleep(500); //每0.5秒檢測
				} catch (InterruptedException IE) { 
					IE.printStackTrace(); 
				} 
			}catch(SQLException sqlException) {
				System.out.println("error in IsUseJudge");
				sqlException.printStackTrace();
			}
		}
//主要迴圈END
	}
	public static void main(String args[]){
		ECATest ECAtest = new ECATest(1,"qqq");
	}	
}


//問題，不可直接在DB中刪除某正在執行之ECARule，會出錯
//各功能皆可使用






