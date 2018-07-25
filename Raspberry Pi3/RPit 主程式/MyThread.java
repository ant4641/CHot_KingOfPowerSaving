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
			System.out.println(s.getRemoteSocketAddress() + "�@�w�s�u...");
			r = new BufferedReader(new InputStreamReader(s.getInputStream()));
			out = new PrintWriter(new OutputStreamWriter(s.getOutputStream()), true);
			ADserial=r.readLine();
			out.println("�A�n�D�D�D�ڬO���A���A�o�O���A�����T��");
					
			String type=ADserial.substring(0,1);
			//System.out.print(type);
			int isup=0;
			switch(type){
				case "S":{
					rs = statement.executeQuery("SELECT SOC_Serial FROM SOCKET WHERE SOC_Serial='"+ADserial+"';");
					if(!rs.next()){
						isup=0;
						isup=statement.executeUpdate("Insert into SOCKET values('"+ADserial+"','noname','����','1')");
						System.out.println("isup= "+isup);
						System.out.println("Insert SOCKET successfully !!");
					}else{
						isup=0;
						isup=statement.executeUpdate("UPDATE SOCKET SET SOC_Sta='�}��' WHERE SOC_Serial='"+ADserial+"';");
						//System.out.println("isup= "+isup);
						if(isup==1)System.out.println("SOCKET "+ADserial+" �w�}��");
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
						//���J�Ȭ����եΡA�n�ݧ令���㪩
						System.out.println("New AD's TYPE : "+k[1]);
						isup=statement.executeUpdate("insert into ARD values('"+k[0]+"','noname','"+k[1]+"','1','���U',0,'1')");
						System.out.println("isup= "+isup);
						System.out.println("Insert ARD successfully !!");
					}else{
						System.out.println("Arduino : "+k[0]+" �w�s�u");
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
// Arduino�Ǹ�PK(ARD_Serial)�B
// Arduino�W��(ARD_Name)�B
// Arduino�˴�����(ARD_Type [wet�Bcelsius�Blight])�B
// Arduino�s�u���A(ARD_Status[�ݾ�(1)�B�ǳ��˴�(2)�B�˴���(3)])
// Arduino�\�]��m(ARD_Loc)�B
// �˴���(ARD_Det)�B
// ���������ARD_RaspSerial

// 4.SOCKET: //ARD����Scoket
// ���y�Ǹ�PK(SOC_Serial)�B
//���y�W��PK(SOC_Name #TEXT)�B
// �O�_�}��(SOC_Sta ����/�}��)�B
// SOC_RaspSerial(���������)