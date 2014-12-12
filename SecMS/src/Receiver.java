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
			// ���������� �����ϰ� 5000�� ��Ʈ�� ����(bind) ��Ų��.
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
		
		// AES ��ȣȭ 
		SecureUtils.aesDecrypt(dir + Constants.fnameKey, dir + Constants.fnameRecvCipher, dir + Constants.fnameDecodedPlain);
		
		// �ؽð� ����
		SecureUtils.makeHash(dir + Constants.fnameKey, dir + Constants.fnameDecodedPlain, dir + Constants.fnameRehash);		


		// hash �� ��
		byte recvHash[] = SecureUtils.readFile(dir + Constants.fnameRecvHash);
		byte makeHash[] = SecureUtils.readFile(dir + Constants.fnameRehash);
		
		SecureUtils.printByteArray("recvHash : ", recvHash);
		SecureUtils.printByteArray("makeHash : ", makeHash);
	
		boolean result = Arrays.equals(recvHash, makeHash);
		
		System.out.println("*�ؽð� �� ��� : " + result);

	}
}
