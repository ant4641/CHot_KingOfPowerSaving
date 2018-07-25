import java.io.*;
import java.net.*;
import java.util.*;
public class ServerR {
	
	private MyServer clone;
	ServerR(MyServer c){
		clone=c;
	}
	
	public void run{
		Socket s = null;
		ServerSocket ss = null;
		try {
        ss = new ServerSocket(2000);
        System.out.println("伺服器已啟動...");
		clients = new ArrayList<MyThread>();
        while (true) {
            s = ss.accept();
            MyThread t = new MyThread(s);
            new Thread(t).start();
			System.out.println("----------------------");
			for (MyThread st : clients){
				System.out.println("st.name: "+st.name);
			}
			System.out.println("----------------------");

		}
		} catch (IOException e) {
			e.printStackTrace();
			// System.out.println("!!!!!!!!!!!!!!"); 
		} finally {
			try {
				ss.close(); 
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
}