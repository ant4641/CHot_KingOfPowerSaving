import java.util.ArrayList;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.lang.Thread.*;

public class ECATest {
	//�ݭn��������Ǹ��B�ϥΪ̱b��
	int RaspSerial=1;
	String Account="";
	//NAS
	// static final String url = "jdbc:mariadb://120.105.161.89/ECASystem";
	//rpi in lab with internet
	// static final String url = "jdbc:mysql://120.105.161.80/ECASystem";
	static final String url = "jdbc:mysql://10.0.0.1/ECASystem";
	static final String username = "ECASystem"; // �n�վ�
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
		//�s��DB
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(url, username, password); 
			// Class.forName("org.sqlite.JDBC");
			// con = DriverManager.getConnection("jdbc:sqlite:C:/Program Files (x86)/DaliyWorks/RPI/RPitoAD/sqlite/ecasystem.db");
			System.out.println("con.isClosed() : " +con.isClosed());
			if(con.isClosed()==true){
				System.out.println("��Ʈw�s������");
			}else{
				System.out.println("��Ʈw�s�����\");
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
		
		//�D�n�j��START----------------------------
		while(true) {
			String sqlForIsUse="select * from RASP where RASP_Serial='"+RaspSerial+"' AND RASP_IsUse = 1;";
			try {
				rsIseUse = statementForECARule.executeQuery(sqlForIsUse);
				if(rsIseUse.next()) {
					//socket-soc_name, soc_sta
					//advice-adv_name, adv_socname
					//eca-eca_num, eca_advname, eca_con, eca_ps
					
					/*1.�l�d�ߧ�}�Ҫ�socket�A�ΥH�d�߬�����ECA rules�G
					    �Y�Ǹ����s�b��ECA�B�B�D�ק�R���̡A��J�䤤*/
					String sqlForDevice="select * from DEVICE where DEV_SocSerial IN (select SOC_Serial from SOCKET where SOC_Sta='����' AND SOC_RaspSerial ='"+RaspSerial+"');";
					String sqlForECARule="select * from ECA where eca_DevSerial IN (select DEV_Serial from DEVICE where DEV_SocSerial IN (select SOC_Serial from SOCKET where SOC_Sta = '�}��' AND SOC_RaspSerial ='"+RaspSerial+"'));";
					try {
						rsECA = statementForECARule.executeQuery(sqlForECARule);
						rsECAMetaData = rsECA.getMetaData();
						int ecaNum = eca.size();//eca�쥻��size
						while (rsECA.next()){
							int ifPutIn=1;///�P�_�O�_�ݭn��J
							for(int i=0;i<ecaNum;i++) {
								String[] ecaCheck = eca.get(i);
								int serial = Integer.parseInt(ecaCheck[0]);
		  			    			if(Integer.parseInt(rsECA.getObject(1).toString())==serial) {//�Y�����Ӧs�b��eca����serial�h����J
		  			    				ifPutIn=0;
		  			    				break;
		  			    			}
							}
							if(ifPutIn==1) {//��J�D���Ӧs�b��eca��ECA rule
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
					//�Neca_num, eca_advname, eca_con ��J eca
					
					//2.�ק�ECA�и�Ʈw�ݳƵ��O�ק�A�B���b�θӴ��y���]�Ӳz�ӻ��@�w�O���b�Τ~�g�ק�^
					//��sqlForECARule��eca_ps=�ק�
					//���s�bECA���ۦP�Ǹ������e�i��ק�
					try {
						rsECA = statementForECARule.executeQuery(sqlForECARule);
						rsECAMetaData = rsECA.getMetaData();
						
						String sqlForECAPsR;
						while (rsECA.next()){
							if(rsECA.getObject(4).toString().equals("�ק�")) {//�p�G�ݭn�Ұʪ�ECA rule�ݭק�
								for(int i=0;i<eca.size();i++) {//��Meca���Ǹ��@�˪̡A�i��ק�
									String[] ecaRevise = eca.get(i);
									int serial = Integer.parseInt(ecaRevise[0]);
									if(Integer.parseInt(rsECA.getObject(1).toString())==serial) {
										for(int j=1;j<rsECAMetaData.getColumnCount()-1;j++) {
											ecaRevise[j]=rsECA.getObject(j+1).toString();
										}
										ecaRevise[3]="";
										eca.set(i, ecaRevise);
										sqlForECAPsR="update ECA set ECA_Ps='' where ECA_Num='"+ecaRevise[0]+"';";//�Nsql��PS���ק�אּ�ť�
										statementForReviseEca.executeQuery(sqlForECAPsR);
										//�ݬO�_�n��result�P�_���S�����\�B�O�_�ݭnQuery
										break;
									}
		  			    			}
							}
		  			    }
					}catch(SQLException sqlException) {
						System.out.println("error in step 2");
						sqlException.printStackTrace();
					}
					//3.�R��ECA��
						//a.�R���w�S���b�Ϊ����y���� ECA
					try {
						rsDevice = statementForDevice.executeQuery(sqlForDevice);//��X���������q��
						rsDeviceMetaData = rsDevice.getMetaData();
						
						while (rsDevice.next()){
							int[] ifDelete = new int[eca.size()];//�}�C�����U��eca
							for(int i=0;i<eca.size();i++) {
								ifDelete[i]=0;
								String[] ecaDelete = eca.get(i);
								System.out.println("ecaDelete[1]="+ecaDelete[1]);
								System.out.println("rsDevice.getObject(1)="+rsDevice.getObject(1));
								if(rsDevice.getObject(1).toString().equals(ecaDelete[1].toString())) {//�Y�������q���W����eca����serial�A�N������ifDelete�אּ1
									ifDelete[i]=1;
									System.out.println("------------ifDelete[i]="+i+" "+ifDelete[i]);
								}
							}

							for(int j=ifDelete.length-1;j>-1;j--) {//�q�᩹�e�R���������q����eca
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
					
						/*b.�R����Ʈw�Ƶ��O�w�R���A�B���b�θӴ��y���]�Ӳz�ӻ��@�w�O���b�Τ~�g�R���^
						��sqlForECARule��eca_ps=�R��*/
					try {
						rsECA = statementForECARule.executeQuery(sqlForECARule);
						rsECAMetaData = rsECA.getMetaData();
						String sqlForECAPsD;
						while (rsECA.next()){
							if(rsECA.getObject(4).toString().equals("�R��")) {//�p�G�ݭn�Ұʪ�ECA rule�ݧR��
								System.out.println("-------del-------------"+rsECA.getObject(1).toString());
								for(int i=0;i<eca.size();i++) {//��Meca���Ǹ��@�˪̡A�i��R��
									String[] ecaDelete = eca.get(i);
									int serial = Integer.parseInt(ecaDelete[0]);
									if(Integer.parseInt(rsECA.getObject(1).toString())==serial) {
										eca.remove(i);
										sqlForECAPsD="delete from ECA where ECA_Num='"+ecaDelete[0]+"';";//�NDB���ӵ����اR��
										statementForReviseEca.executeQuery(sqlForECAPsD);
										//�ݬO�_�n��result�P�_���S�����\�B�O�_�ݭnQuery
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
					
					//4.��ECA
						//a.�إ�ECAThread�����s�b�� ECA �Ǹ��� ECAThread
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
						//b.���bECA���s�b�Ǹ��A�����e���P�����ק�
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
						//c.���çR�����s�b��ECA�Ǹ���ECAThread
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
				//5.���j����
				try { 
					Thread.currentThread().sleep(500); //�C0.5���˴�
				} catch (InterruptedException IE) { 
					IE.printStackTrace(); 
				} 
			}catch(SQLException sqlException) {
				System.out.println("error in IsUseJudge");
				sqlException.printStackTrace();
			}
		}
//�D�n�j��END
	}
	public static void main(String args[]){
		ECATest ECAtest = new ECATest(1,"qqq");
	}	
}


//���D�A���i�����bDB���R���Y���b���椧ECARule�A�|�X��
//�U�\��ҥi�ϥ�






