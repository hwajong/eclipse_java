import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

public class Receiver
{
	public static void main(String args[])
	{
		String dir = "recv/";
		
		ServerSocket serverSocket = null;

		try
		{
			// 서버소켓을 생성하고 5000번 포트와 결합(bind) 시킨다.
			serverSocket = new ServerSocket(7777);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
		System.out.println("*Server started!");
		System.out.println("*Now wait for message ...");

		try
		{
			Socket socket = serverSocket.accept();
			System.out.println("*Connected! - " + socket.getInetAddress());

			InputStream in = socket.getInputStream();
			DataInputStream dis = new DataInputStream(in);
			
			int len = dis.readInt();
			byte cipher[] = new byte[len];
			dis.readFully(cipher);
			SecureUtils.printByteArray("recv cipher: ", cipher);
			SecureUtils.writeFile(cipher, dir + Constants.fnameRecvCipher);
			
			len = dis.readInt();
			byte hash[] = new byte[len];
			dis.readFully(hash);
			SecureUtils.printByteArray("recv hash: ", hash);
			SecureUtils.writeFile(hash, dir + Constants.fnameRecvHash);
			
			dis.close();
			socket.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
		// Decryption !!
		SecureUtils.make128bitKey(dir + Constants.fnameKey);
		
		// AES 복호화 
		SecureUtils.aesDecrypt(dir + Constants.fnameKey, dir + Constants.fnameRecvCipher, dir + Constants.fnameDecodedPlain);
		
		// 해시값 생성
		SecureUtils.makeHash(dir + Constants.fnameKey, dir + Constants.fnameDecodedPlain, dir + Constants.fnameRehash);		


		// hash 값 비교
		byte recvHash[] = SecureUtils.readFile(dir + Constants.fnameRecvHash);
		byte makeHash[] = SecureUtils.readFile(dir + Constants.fnameRehash);
		
		SecureUtils.printByteArray("recvHash : ", recvHash);
		SecureUtils.printByteArray("makeHash : ", makeHash);
	
		boolean result = Arrays.equals(recvHash, makeHash);
		
		System.out.println("*해시값 비교 결과 : " + result);

	}
}
