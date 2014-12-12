import java.io.DataOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Sender
{
	public static void main(String args[])
	{
		String dir = "send/";
		SecureUtils.make_secure_msg_files(dir);
		
		System.out.println("*Now Connecting to Receiver...");
		
		try 
		{
			Socket socket = new Socket("localhost", 7777);

			OutputStream out = socket.getOutputStream(); 
			DataOutputStream dos = new DataOutputStream(out);

			// 암호문 전송
			byte[] cipher = SecureUtils.readFile(dir + Constants.fnameCipher);
			dos.writeInt(cipher.length);
			dos.write(cipher);
			
			// 해시 전송
			byte[] hash = SecureUtils.readFile(dir + Constants.fnameHash);
			dos.writeInt(hash.length);
			dos.write(hash);
			
			dos.close();
			socket.close();

		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

}


















