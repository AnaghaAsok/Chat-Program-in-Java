import java.io.*;
import java.net.*;
import java.util.*;

public class Client extends Thread{
	
	public static int port = 8000; 
	static String hostname = "localhost";
	static ObjectOutputStream oos;
	//ObjectInputStream ois;
	static DataInputStream in;
	FileInputStream fis = null;
	FileOutputStream fos = null;
	String name;
	static Socket socket;
	String filedest;
	Scanner input=new Scanner(System.in);
	
	//public Client(){}
	
	public static void main(String args[]){
		
	BufferedInputStream bis;
	String clientName = null;
	String blockName  = null;
	PrintStream os;
		
		try {
			socket = new Socket(hostname,port);
			in = new DataInputStream(socket.getInputStream());
			oos = new ObjectOutputStream(socket.getOutputStream());			
			//oos.flush();

			new Thread(new Client()).start();
			
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
		finally
		{
			
		}
	}

	public void run() {
		try{
			String strRead = null;
			byte[] arrFileInp = new byte[1024];
			Message mes = new Message();
			while (true) 
			{
				//ois = new ObjectInputStream(in);
				Object obj = in.readLine();
				if(obj instanceof String)
					strRead = obj.toString();
				System.out.println("server> "+ strRead);
				if(strRead.indexOf("Bye") != -1)
					;
				else if(strRead.indexOf("*Enter") != -1)
				{
					System.out.println("Checking if it enters send part");
					String filesrc = input.nextLine();
					arrFileInp = mes.sendFile(filesrc);
					System.out.println("File obtained in array");
					oos.writeObject(arrFileInp);
				}
				else if(strRead.indexOf("#Enter") != -1)
				{
					System.out.println("Checking if it enters recieve part");
					String filedest = input.nextLine();
					
					File mynewfile = new File(filedest);
					
					byte [] mybytearray  = new byte[1024];
					fos = new FileOutputStream(mynewfile);
					BufferedOutputStream bos=new BufferedOutputStream(fos);
					ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
					Object obj1 = ois.readObject();
					String objClass = obj1.getClass().getTypeName().toString();
					System.out.println(objClass);
					System.out.println("Value in obj: " +obj1.toString());
					if(objClass.equals("byte[]"))
					{
						System.out.println("Yes its a byte type!!");
						mybytearray=(byte[])obj1;
						System.out.println("Did we reach here after getting mybytearray ????? ");
						bos.write(mybytearray);
						System.out.println("Done!!");
					}
					bos.close();
					fos.close();
					ois.close();
					/*ois = new ObjectInputStream(socket.getInputStream());
					byte[] arrFileOut = (byte[]) ois.readObject();
					mes.receiveFile(filedest, arrFileOut);*/
					
				}
				String clientInput = input.nextLine();		
				System.out.println(clientInput.length());
				if(clientInput.length() != 0)
				{
					System.out.println("Did we get a client input ? If yes : " + clientInput);
					oos.writeObject(clientInput);	
				}			
			}
		}
		catch(IOException e) {
			System.err.println(e.getMessage());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}


