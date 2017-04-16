import java.util.*;
import java.util.regex.*;
import java.io.*;
import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SMTPServer {
	
	public ServerSocket ss =null;
	public BufferedReader in = null;  // strumienie gniazda
	public PrintWriter out = null;    // komunikacji z klientem
	public String host;
	public String log;
	public static Pattern reqPatt = Pattern.compile(" +", 2); // wzorzec do logowania ( 2 wyrazy odzielone spacja)
	public static String msg[] = { "1", "0"};
	
	public SMTPServer(ServerSocket ss){
	    this.ss = ss;
	    System.out.println("Server started");
	    System.out.println("at port: " + ss.getLocalPort());
	    System.out.println("bind address: " + ss.getInetAddress());

	    serviceConnections();  // nasłuchiwanie połączeń
	}
	
	//----------SPRAWDZANIE CZY KTOS SIE POLACZYL
	 public void serviceConnections() {
		    boolean serverRunning = true;   // serwer działa ciągle
		    while (serverRunning) {
		      try {
		        Socket conn = ss.accept();  // nasłuch i akceptaccja połączeń

		        System.out.println("Connection established");

		        serviceRequests(conn);      // obsługa zleceń dla tego połączenia

		      } catch (Exception exc) {
		          exc.printStackTrace();
		      }
		   }                               // zamknięcie gniazda serwera
		    try { ss.close(); } catch (Exception exc) {}
		  }
	 
	 
	 //---------W WYPADKU POLACZENIA OBSLUGA
	 public void serviceRequests(Socket connection)
             throws IOException {
		try {
		in = new BufferedReader(                   // utworzenie strumieni
		new InputStreamReader(
		  connection.getInputStream()));
		out = new PrintWriter(
		connection.getOutputStream(), true);
		
		System.out.println(in.readLine());
		
		// Odczytywanie zleceń (line zawiera kolejne zlecenie)
		for (String line; (line = in.readLine()) != null; ) {
		
			String resp;                           // odpowiedź
			String[] req = reqPatt.split(line, 2); // rozbiór zlecenia
			log = req[0];                   // pierwsze słowo - polecenie
			String pas = req[1];                   // pierwsze słowo - polecenie
		
			if (log.equals("adm") && pas.equals("adm1")) {        // zlecenie "bye" - koniec komunikacji
			writeResp(0, null);
		    System.out.println("Zalogowano Uzytkownika :" + log );
		    ServerRequestSecond();
					break;
					}
					else {
					    System.out.println("Blad logowania na server");
						writeResp(1, null);
					}
			}
			} catch (Exception exc) {
			exc.printStackTrace();
		
			} finally {
				try {                                // zamknięcie strumieni
				in.close();                        // i gniazda
				out.close();
				connection.close();
				connection = null;
				} catch (Exception exc) { }
			}
		}
	  
	  public void writeResp(int rc, String addMsg)
              throws IOException {
		  out.println(msg[rc]);
		  if (addMsg != null) out.println(addMsg);
	  		}

	  public void ServerRequestSecond() throws IOException{
		  while(true){
		 // System.out.println("OBSLUGA ZADANIA");
		  String ReqLine;
		  ReqLine = in.readLine();
		//  System.out.println(ReqLine);
		  int wyb = Integer.parseInt(ReqLine);
		 // System.out.println(wyb);
		  if (wyb == 1) ObslugaM();
		  if (wyb == 2) ObslugaZ();
		  if (wyb == 3) ObslugaL();
		  }
	  }
	  
	  public void ObslugaM() throws IOException{
		 String blank;
		  
		 blank = in.readLine();
		 out.println("250 OK");
		 
		 blank = in.readLine();
		 out.println("250 Accept");
		 
		 blank = in.readLine();
		 out.println("354 Enter messange");
		 
		 String data;
		 data = in.readLine();
		 System.out.println(data);
		 
		 
		 String from;
		 from = in.readLine();
		 System.out.println(from);
		 
		 String to;
		 to = in.readLine();
		 System.out.println(to);
		 
		 String Temat;
		 Temat = in.readLine();
		 System.out.println(Temat);
		
		 String MsgCont;
		 MsgCont = in.readLine();
		 System.out.println(MsgCont);
		 
		 blank = in.readLine();
		 out.println("250 OK");
		 
		  ServerRequestSecond();
	  }
	  
	  public void ObslugaZ() throws IOException{
		  int bytesRead;  
		    int current = 0;  
		   
		    ServerSocket serverSocket = null;  
		    serverSocket = new ServerSocket(13267);  
		         
		    while(true) {  
		        Socket clientSocket = null;  
		        clientSocket = serverSocket.accept();  
		           
		        InputStream in = clientSocket.getInputStream();  
		           
		        DataInputStream clientData = new DataInputStream(in);   
		           
		        String fileName = clientData.readUTF();     
		        OutputStream output = new FileOutputStream(fileName);     
		        long size = clientData.readLong();     
		        byte[] buffer = new byte[1024];     
		        while (size > 0 && (bytesRead = clientData.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1)     
		        {     
		            output.write(buffer, 0, bytesRead);     
		            size -= bytesRead;     
		        }  
		           

		        // Closing the FileOutputStream handle
		        in.close();
		        clientData.close();
		        output.close(); 
		        serverSocket.close();
		        
		        System.out.println("Recive File : " + fileName);
		       // serviceConnections();
		        ServerRequestSecond();
		    }  
	      
		// ServerRequestSecond();
		 
	  }
	  
	  public void ObslugaL(){
		  System.out.println("quit");
		  serviceConnections();
	  }
	  
	public static void main(String[] args) {
		ServerSocket ss = null;
	    try {
	    	
	    	String host = args[0];
	    	int port = Integer.parseInt(args[1]);

	      InetSocketAddress isa = new InetSocketAddress(host, port);

	      ss = new ServerSocket();             // Utworzenie gniazda serwera
	      ss.bind(isa);                         // i związanie go z adresem
	      
	      
	    } catch(Exception exc) {
	        exc.printStackTrace();
	        System.exit(1);
	    }
	    new SMTPServer(ss);
	}

}
