import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;


public class Message {

	static Scanner sc=new Scanner(System.in);
	BufferedInputStream bis = null;
	FileInputStream fis = null;
	FileWriter fw = null;
	BufferedReader br = null;
	BufferedWriter bw = null;
	
	public byte[] sendFile(String filepath) throws IOException{
		try{
				String strLine;
				//String[] strArray = new String[1024];
				byte[] mybytearray = new byte[1024];
				File myfile = new File(filepath);
		        FileInputStream fis = new FileInputStream(filepath);
		        BufferedInputStream bis = new BufferedInputStream(fis);
		        bis.read(mybytearray, 0, (int) myfile.length());
		        return mybytearray;
											
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			//fis.close();
			//br.close();
			//oos.close();
			//bis.close();
			
		}
		return null;
	}
	
	public void receiveFile(String filepath, byte[] myFileReceived) throws IOException{
		try{	
			System.out.println("We have called the recieve func...");
			File mynewfile = new File(filepath);
			
			
			
			/*System.out.println("New file created");
			FileOutputStream fos = new FileOutputStream(mynewfile);
			BufferedOutputStream bos = new BufferedOutputStream(fos);		
			bos.write(myFileReceived);*/
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			fw.close();
			//bos.close();
			//ois.close();
		}
	}

	
}