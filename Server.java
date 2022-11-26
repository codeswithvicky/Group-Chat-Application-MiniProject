import java.io.*;
import java.net.*;
import java.util.*;
import java.text.*;
public class Server
{
	private ArrayList clientOutputStream;
   	private int clientCounter = 0;
	FileWriter file;
   	SimpleDateFormat time=new SimpleDateFormat("hh:mm aa");
	SimpleDateFormat date=new SimpleDateFormat("dd/MM/yyyy");
	public Server()
	{
		try
		{
			file=new FileWriter("logHistory.txt",true);
		}
		catch(IOException e)
		{}
	}
	public class ClientHandle implements Runnable
	{
		String userName,ip,ct,dt;
      		BufferedReader read;
      		Socket socket;
      		public ClientHandle(Socket cSocket)
		{
        		try
			{
            			socket = cSocket;
            			InputStreamReader in = new InputStreamReader(socket.getInputStream());
            			read = new BufferedReader(in);
				userName=read.readLine();
				ip=(((InetSocketAddress) socket.getRemoteSocketAddress()).getAddress()).toString().replace("/","");
				ct=time.format(new Date());
				System.out.println("\n\t\tNew User Has Connected");
				System.out.println("Username\t:"+userName);
				System.out.println("IP Address\t:"+ip);
				System.out.println("Connected time\t:"+ct);
				logHistory(userName,ip,"Connected",ct);
            	
         		}
			catch(Exception ex)
			{}
      		}
      
      		public void run()
		{
         		String message;
         		
			try
			{
            			while((message = read.readLine()) != null)
				{
      					System.out.println("Encrypted MSG: " + message);
               				Forward(message);
            	  		}
         		}
			catch(Exception ex)
			{}
			System.out.println("\n"+userName+" has disconnected");
			dt=time.format(new Date());
			logHistory(userName,ip,"Disconnected",dt);
         		clientCounter--;
         		System.out.println("\nCLIENT HAS DISCONNECTED || TOTAL CLIENTS: " + clientCounter +" \n");
      		}
	}  //End inner class ClientHandle
   
	public void connect()
	{
      		clientOutputStream = new ArrayList();
      
      		try
		{
         		ServerSocket serverSock = new ServerSocket(5000);
         		System.out.println("\n\nSERVER HAS STARTED");
         		System.out.println("----------------------------------------------------\n");
         
         		while(true)
			{
            			Socket clientSock = serverSock.accept();
            			PrintWriter write = new PrintWriter(clientSock.getOutputStream());
            			clientOutputStream.add(write);
            			
            			Thread t = new Thread(new ClientHandle(clientSock));
            			t.start();
            
            			clientCounter++;
            			System.out.println("\nCLIENT CONNECTED || TOTAL CLIENTS: " + clientCounter + "\n");
         		}
      		}
		catch(Exception ex)
		{
         		//System.out.println("Client has disconnected");
      		}
   	}
   
   	public void Forward(String msg)
	{
      		Iterator it = clientOutputStream.iterator();
      
      		while(it.hasNext())
		{
         		try
			{
				
            			PrintWriter write = (PrintWriter) it.next();
            			write.println(msg+"\n");
            			write.flush();
            
         		}	
			catch(Exception ex)
			{ }
      		}
   	}  //end Forward
   	synchronized public void logHistory(String name,String ip,String status,String time)  
	{
		String d=date.format(new Date());;
		String log="\n"+d+"\t"+name+"\t\t"+ip+"\t"+status+"\t"+time+"\n";
		try
		{
			file.write(log);
			file.flush();
		}
		catch(IOException e)
		{}
	
	}
   	public static void main(String args[])
	{
      		new Server().connect();
   	}
} // End Server Class