import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.text.*;

public class SMTPKlient {

	public Socket sock = null;
	public PrintWriter out = null;
	public BufferedReader in = null;
	public String hostip;
	public String log;
	public String pass;
	
	public SMTPKlient(String host, int port) {
		hostip = host;
		
	    try {
	      sock = new Socket(host, port);
	      out = new PrintWriter(sock.getOutputStream(), true);
	      in = new BufferedReader(
	               new InputStreamReader(
	                   sock.getInputStream()));
	      System.out.print("Podaj login :");
	      Scanner scan = new Scanner(System.in);  
	      log = scan.nextLine();
	      
	      System.out.print("Podaj haslo :"); 
	      pass = scan.nextLine();
	      String line;
	      line = log + " " + pass;
	      
	      
	      makeRequest(line);
	      in.close();
	      out.close();
	      sock.close();
	    } catch (UnknownHostException e) {
	        System.exit(2);
	    } catch (IOException e) {
	        System.exit(3);
	    } catch (Exception exc) {
	        exc.printStackTrace();
	        System.exit(4);
	    }
	  }

	  public boolean makeRequest(String req) throws IOException {
	   // System.out.println("Request: " + req);
		  
		out.println("helo");  
		  
	    out.println(req);
	    String resp = in.readLine();
	    int wyb = Integer.parseInt(resp);
	    if (wyb==1) System.out.println("ZALOGOWANO");
	    if (wyb==0) System.out.println("BLAD LOGOWANIA");
	    boolean ok = resp.startsWith("0");
    	if(wyb == 1)makeRequestSecond();
	    
	    if (req.startsWith("get") && ok){
	    	System.out.println(in.readLine());
	    }
	    return ok;
	  }
	  
	  public void makeRequestSecond() throws IOException{
		  System.out.println("1 - Wyslij mail");
		  System.out.println("2 - Wyslij zalacznik do poprzedniego odbiorcy");
		  System.out.println("3 - Wyloguj");
		  
		  int wb;
		  Scanner scan = new Scanner(System.in); 
	      wb = scan.nextInt();
	      if (wb == 1 ) Mail();
	      if (wb == 2 ) Zalacznik();
	      if (wb == 3 ) LogOut();
	      if (wb!= 1 && wb != 2 && wb!= 3){
	    	  System.out.println("Zly wybor");
	    	  makeRequestSecond();
	      	}
	  }
	  
	  public void Mail() throws IOException{
		  String Type = "1";
		  out.println(Type);
		  String Content;	  
		  String Reciver;
		  String Subject;
		  String blank ;
		  Scanner scan = new Scanner(System.in); 
		  
		  out.println("mail from " + log + "@mojmail.pl");
		  blank = in.readLine();
		  
		  System.out.print("Odbiorca : ");
		  Reciver = scan.nextLine();
	      out.println("rcp to : " + Reciver);
	      blank = in.readLine();
	      
	      out.println("data");
	      blank = in.readLine();
	      
	      DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		  Date date = new Date();
		  String time;
		  time = dateFormat.format(date);
		  time = "Date: " + time;
		  out.println(time);
		  
		  out.println("From : " + log + "@mojmail.pl");
		  
		  out.println("To : " + Reciver);
		  
		  System.out.print("Temat : ");
		  Subject = scan.nextLine();
	      out.println("Subject : " + Subject);
	      
	      System.out.print("Tresc : ");
	      Content = scan.nextLine();
	      out.println(Content);
	      
	      out.println(".");
	      blank = in.readLine();
	      
	      makeRequestSecond();
	  }
	 
	  public void Zalacznik() throws IOException{
		  String Type = "2";
		  out.println(Type);
		 
	        Socket sockx = new Socket(hostip, 13267);  
	        
	        String FName;
	        System.out.print("Nazwa pliku : ");
	        Scanner scan = new Scanner(System.in); 
			FName = scan.nextLine();
	        
	        //Send file  
	        File myFile = new File(FName);  
	        byte[] mybytearray = new byte[(int) myFile.length()];  
	           
	        FileInputStream fis = new FileInputStream(myFile);  
	        BufferedInputStream bis = new BufferedInputStream(fis);  
	        //bis.read(mybytearray, 0, mybytearray.length);  
	           
	        DataInputStream dis = new DataInputStream(bis);     
	        dis.readFully(mybytearray, 0, mybytearray.length);  
	           
	        OutputStream os = sockx.getOutputStream();  
	           
	        //Sending file name and file size to the server  
	        DataOutputStream dos = new DataOutputStream(os);     
	        dos.writeUTF(myFile.getName());     
	        dos.writeLong(mybytearray.length);     
	        dos.write(mybytearray, 0, mybytearray.length);     
	        dos.flush();  
	           
	        //Sending file data to the server  
	        os.write(mybytearray, 0, mybytearray.length);  
	        os.flush();  
	           
	        //Closing socket
	        os.close();
	        dos.close();  
	        sockx.close();  
	      
	        makeRequestSecond();
	  }
	  
	
	  public void LogOut(){
		  String Type = "3";
		  out.println(Type);
		  System.out.println("zakonczono prace"); 
	  }
	  
	  
	public static void main(String[] args) {
		new SMTPKlient(args[0], Integer.parseInt(args[1]));

	}

}
