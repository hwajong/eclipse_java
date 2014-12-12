import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.Scanner;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;


public class SecureUtils
{
	// �迭 ��� for �����
	public static void printByteArray(String desc, byte[] A)
	{
		System.out.println("*" + desc);
		for(int i = 0; i < A.length; i++)
		{
			if(i != 0 && i % 16 == 0) System.out.println();

			System.out.print(String.format(" 0x%02x", A[i]));
		}

		System.out.println('�n');
	}

	// ���Ͼ���
	public static void writeFile(byte[] data, String fname)
	{
		try
		{
			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(fname));
			out.write(data);
			out.close();
		}
		catch(IOException e)
		{
			System.out.println("**Error - can't write file : " + fname);
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	public static void writeTextFile(String data, String fname)
	{
		try
		{
			BufferedWriter out = new BufferedWriter(new FileWriter(fname));
			out.write(data);
			out.close();
		}
		catch(IOException e)
		{
			System.out.println("**Error - can't write file : " + fname);
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	// �����б� - ȣ���ϴ� ������ �޸� ������ �ؾ��Ѵ�.
	public static byte[] readFile(String fname)
	{
		try
		{
			Path path = Paths.get(fname);
			return Files.readAllBytes(path);
		}
		catch(IOException e)
		{
			System.out.println("**Error - can't read file : " + fname);
			e.printStackTrace();
			System.exit(0);
		}
		
		return null;		
	}
	
	public static String readTextFile(String fname)
	{
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(fname));
			String line;
			StringBuilder sb = new StringBuilder();
			while((line = br.readLine()) != null)
			{
				sb.append(line).append('�n');
			}
			
			br.close();
			return sb.toString();
		}
		catch(IOException e)
		{
			System.out.println("**Error - can't read text file : " + fname);
			e.printStackTrace();
			System.exit(0);
		}
		
		return null;
	}
	
	// 1234�� srand �̿� 128bit Ű���� --> text file ����
	public static void make128bitKey(String fname)
	{
		System.out.println("*Ű���� �����ϱ� ��й�ȣ�� �Է��Ͻÿ� : ");

		Scanner in = new Scanner(System.in);
		int userKey = in.nextInt();
		in.close();
		System.out.println("*user_key : " + userKey);

		Random rand = new Random();
		rand.setSeed(userKey);

		// 128bit(16byte) Ű�� ����
		byte key[] = new byte[16];
		rand.nextBytes(key);

		SecureUtils.printByteArray("������ 128bit key ��", key);

		SecureUtils.writeFile(key, fname);
	}
	
	// HMAC_SHA2 �ؽð� ���� �� ���� write
	public static void makeHash(String fnameKey, String fnameIn, String fnameOut)
	{
		byte[] key = SecureUtils.readFile(fnameKey);
		String data = SecureUtils.readTextFile(fnameIn);
		
		System.out.println(data);

		Mac sha256_HMAC;
		try
		{
			sha256_HMAC = Mac.getInstance("HmacSHA256");
			SecretKeySpec secret_key = new SecretKeySpec(key, "HmacSHA256");
			sha256_HMAC.init(secret_key);
			
			byte hash[] = sha256_HMAC.doFinal(data.getBytes());
			SecureUtils.printByteArray("HMAC_SHA2", hash);
			SecureUtils.writeFile(hash, fnameOut);
		}
		catch(NoSuchAlgorithmException | InvalidKeyException e)
		{
			System.out.println("**Error - can't make hash");
			e.printStackTrace();
			System.exit(0);			
		}
	}	
	
	public static void aesEncrypt(String fnameKey, String fnameIn, String fnameOut)
	{
		// AES ��ȣȭ
		byte[] key = SecureUtils.readFile(fnameKey);
		String data = SecureUtils.readTextFile(fnameIn);
		AES256Util aes = new AES256Util(key);
		try
		{
			byte[] encrypted = aes.aesEncode(data);
			SecureUtils.printByteArray("*encrypted : ", encrypted);
			
			SecureUtils.writeFile(encrypted, fnameOut);

			System.out.println("*Encryption ... Success! --> " + fnameOut);		
			System.out.println();
		}
		catch(Exception e)
		{
			System.out.println("**Error - can't encrypt file : " + fnameIn);
			e.printStackTrace();
			System.exit(0);		
		}
	}
	
	public static void aesDecrypt(String fnameKey, String fnameCipher, String fnameDecodedPlain)
	{
		byte[] key = SecureUtils.readFile(fnameKey);
		byte[] cipher = SecureUtils.readFile(fnameCipher);
		
		AES256Util aes = new AES256Util(key);
		try
		{
			byte[] decrypted = aes.aesDecode(cipher);
			//printByteArray("*decrypted : ", decrypted);
			String plain = new String(decrypted, "CP949");
			SecureUtils.writeTextFile(plain, fnameDecodedPlain);
			System.out.println(plain);

			System.out.println("*Decryption ... Success! --> " + fnameDecodedPlain);			
		}
		catch(Exception e)
		{
			System.out.println("**Error - can't decrypt file : " + fnameCipher);
			e.printStackTrace();
			System.exit(0);		
		}	
	}

	public static void make_secure_msg_files(String dir)
	{
		make128bitKey(dir + Constants.fnameKey);
		
		// �ؽð� ����
		makeHash(dir + Constants.fnameKey, dir + Constants.fnamePlain, dir + Constants.fnameHash);
		
		// AES ��ȣȭ
		aesEncrypt(dir + Constants.fnameKey, dir + Constants.fnamePlain, dir + Constants.fnameCipher);
		
		// AES ��ȣȭ 
		aesDecrypt(dir + Constants.fnameKey, dir + Constants.fnameCipher, dir + Constants.fnameDecodedPlain);
		
		// �ؽð� ����
		makeHash(dir + Constants.fnameKey, dir + Constants.fnameDecodedPlain, dir + Constants.fnameRehash);
	}	
}
