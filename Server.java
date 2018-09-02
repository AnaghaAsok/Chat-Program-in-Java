import java.util.Scanner;
import java.net.*;
import java.io.*;


public class Server{
	
	static ServerSocket serverSocket;
	String hostname = "localhost";
	public static int port = 8000;
	private static final int maxClientsCount = 10;
	private static ClThreads[] threads = new ClThreads[maxClientsCount];
	static Scanner sc=new Scanner(System.in);
	
	
	public static void main(String args[]){
		
		try 
		{
			serverSocket = new ServerSocket(port);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	
	//public void run(){
		while(true)
		{
			try
			{
				System.out.println("Waiting for client on port " + serverSocket.getLocalPort() + "...");
				Socket serverToClient = serverSocket.accept();
        
				System.out.println("Just connected to " + serverToClient.getRemoteSocketAddress());
				int i = 0;
				for(i=0;i<maxClientsCount;i++)
				{
					if(threads[i]==null) 
					{
						(threads[i] = new ClThreads(threads, serverToClient)).start();
						break;
					}
				
				}
				if(i == maxClientsCount)
				{
					PrintWriter pw = new PrintWriter(serverToClient.getOutputStream());
					pw.println("Try later!");
					pw.close();
					serverToClient.close();
				}
			}
			catch(Exception e)
			{
				System.out.println("in catch");
				System.out.println(e.getMessage().toString());
			}
		}
	}
}

class ClThreads extends Thread{
	
	private final ClThreads[] threads;
	private int totalClients;
	public ObjectInputStream ois;
	static DataInputStream dis;
	public ObjectOutputStream oos;
	public PrintStream ps;
	public PrintStream ps1;
	private Socket clsocket = null;
	private String clName = null;
	private String blName  = null;
	byte[] arrFileInp = null;
	static Scanner sc=new Scanner(System.in);
	
	
	public ClThreads(ClThreads[] threads, Socket clsocket)
	{
		this.clsocket = clsocket;
		this.threads = threads;
		totalClients = threads.length;
	}
	
	public void run()
	{
		int totalClients = this.totalClients;
		ClThreads[] threads = this.threads;
		Message mes = new Message();
		try {
			ps = new PrintStream(clsocket.getOutputStream());
			String name = null;
			while(true)
			{
				dis = new DataInputStream(clsocket.getInputStream());
				ois = new ObjectInputStream(dis);
				ps.println("Enter your name.");
			    name = ois.readObject().toString();
			    System.out.println("Entered name: " +name);
			    if(name != null)
			    {
			    	break;
			    }
			    
			}
			ps.println("Hi " + name + ". Enter T before a message to send a text or enter F before a filepath to send a file");
			for (int i = 0; i < totalClients; i++) {
				if (threads[i] != null && threads[i] == this) 
				{
					clName = "unicast-" + name;
					blName = "blkcast-" + name;
					break;
				}
			}
			while (true) 
			{
				String line = (String)ois.readObject();
				System.out.println("line read : " +line);
				if (line.startsWith("/quit")) {
					break;
				}
   /* If the message is private sent it to the given client. */
				if (line.startsWith("unicast-")) 
				{
					String[] strUnicast = line.split("\\s", 3);
					if(strUnicast[1].equals("F"))
					{
						System.out.println("Checking if condition");
						ps.println("*Enter the filepath");
						Object obj1 = ois.readObject();
						arrFileInp = (byte[]) obj1;
						System.out.println("Read ob type: " + obj1.getClass().getTypeName());
						//String[] arrFileInp = mes.sendFile("");
						System.out.println("Checking if it gets here ?? ");
						for (int i = 0; i < totalClients; i++) 
						{
							if (threads[i] != null && threads[i] != this && threads[i].clName != null && threads[i].clName.equals(strUnicast[0])) 
							{
									System.out.println("About to broadcast the file... ");
									threads[i].ps.println("#Enter destination path to store the file");
									oos = new ObjectOutputStream(threads[i].clsocket.getOutputStream());
									oos.writeObject(arrFileInp);
									System.out.println("Is is blocked here ??? - after writing");
									//oos.flush();
							}
						}
					}
					else if (strUnicast.length > 1 && strUnicast[1] != null)
					{
						if(strUnicast[1].equals("T") && strUnicast[2] != null)
						{
							for (int i = 0; i < totalClients; i++)
							{
								if (threads[i] != null && threads[i] != this && threads[i].clName != null && threads[i].clName.equals(strUnicast[0])) {
									threads[i].ps.println(name + "> " + strUnicast[2]);
									break;
								}
							}
						}
					}
					else
					{
						ps.println("Please specify if it is a text or a file (T/F)");
					}
				}
				
/*Block cast*/
				else if (line.startsWith("blkcast-")) {
					String[] strBlockcast = line.split("\\s", 3);
					if (strBlockcast.length > 1 && strBlockcast[1] != null) 
					{
						if(strBlockcast[1].equals("T") && strBlockcast[2] != null)
						{
								for (int i = 0; i < totalClients; i++) 
								{
									if (threads[i] != null && threads[i] != this && threads[i].blName != null && !threads[i].blName.equals(strBlockcast[0])) 
									{
										threads[i].ps.println(name + "> " + strBlockcast[2]);
									}
								}
						}
						else if(strBlockcast[1].equals("F"))
						{
							ps.println("File transfer is disabled for blockcast feature.");
						}
						else
						{
							ps.println("Please specify if it is a text or a file (T/F)");
						}
					}
				}
				else 
				{
					String[] strBroadcast = line.split("\\s", 2);
					if(line.equals("F") )
					{
						System.out.println("Checking if condition");
						ps.println("*Enter the filepath");
						Object obj1 = ois.readObject();
						arrFileInp = (byte[]) obj1;
						System.out.println("Read ob type: " + obj1.getClass().getTypeName());
						//String[] arrFileInp = mes.sendFile("");
						System.out.println("Checking if it gets here ?? ");
						for (int i = 0; i < totalClients; i++) 
						{
							if (threads[i] != null && threads[i] != this && threads[i].clName != null) 
							{
									System.out.println("About to broadcast the file... ");
									threads[i].ps.println("#Enter destination path to store the file");
									oos = new ObjectOutputStream(threads[i].clsocket.getOutputStream());
									oos.writeObject(arrFileInp);
									System.out.println("Is is blocked here ??? - after writing");
									//oos.flush();
							}
						}
					}
					else if (strBroadcast.length > 1) 
					{
						System.out.println("In strBroadcast[0] we have: " + strBroadcast[0]);
						System.out.println("In strBroadcast[1] we have: " + strBroadcast[1]);
						if(strBroadcast[0].equals("T") && strBroadcast[1] != null)
						{
							for (int i = 0; i < totalClients; i++) 
							{
						
								if (threads[i] != null && threads[i] != this && threads[i].clName != null) {
									threads[i].ps.println(name + "> " + strBroadcast[1]);
								}
							}
						}
					}
					else
					{
						ps.println("Please specify if it is a text or a file (T/F)");
					}
				}
			}
		} catch (IOException e) {
			//e.printStackTrace();
		} catch (ClassNotFoundException e) {
			//System.out.println("catching exception 2...");
			//e.printStackTrace();
		}
		catch(Exception e){}
		finally
		{
			ps.close();
			try {
				//dis.close();
				//ois.close();
				//oos.close();
			} catch(NullPointerException n)
			{
				
			}
			catch(Exception e){}
		}
	}
	
	
}