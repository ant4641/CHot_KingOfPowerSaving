import java.io.*;
import java.net.*;
import java.util.*;
public class Client {
	public static Socket s ;
	public static BufferedReader r,br;
	public static int countVal=23;
    public static void main(String[] args) throws Exception  {	
		Scanner scan = new Scanner(System.in);
	
		try {
			s = new Socket(InetAddress.getByName("127.0.0.1"), 2000);
			r = new BufferedReader(new InputStreamReader(s.getInputStream()));
		
			PrintWriter out = new PrintWriter(new OutputStreamWriter(s.getOutputStream()), true);
		   
			System.out.print("��J�W��:");
			String name = scan.next();
			out.println(name); 
			System.out.println("Server say�G" + r.readLine());
		
			String order = "0"; 
			while(true){
				String value="";
				order=r.readLine();
				if(order!=null){
						
					System.out.println("�qServer�����쪺���O��: "+order);
					switch(order){
					case "0" : {
						value="";
						value="/over";
						System.out.println("�t�έn�D����");
						out.println(value);
						s.close();
						break;
					}
					case "1" : {
						value="";
						value="10";
						System.out.println("�ǳưe�X����: "+countVal);
						// out.println(value);
						out.println(countVal);
						countVal++;
						break;
					}
					case "2" : {	
						value="yes";
						out.println(value);
						break;
					}
					default: 
						 System.out.println("No Order"); 
					}
				}else{
						continue;
				}
		
			}

			} catch (Exception e) {
				e.printStackTrace();
				 System.out.println("Error here"); 
			}
    }
	
}