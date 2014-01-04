package com.couplechallenge.imagesearch;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

public class Encryption {
	//generates secure pseudo-random numbers
	static String SECURE = "SHA1PRNG";
	static String AES = "AES";
	public static String SECURE_KEY =  "test";

	public static byte[] generateKey(String password) throws Exception
	{
		byte[] keyStart = password.getBytes();

		KeyGenerator kgen = KeyGenerator.getInstance(AES);
		//http://developer.android.com/reference/java/security/SecureRandom.html
		SecureRandom sr = SecureRandom.getInstance(SECURE);
		sr.setSeed(keyStart);
		kgen.init(128, sr);
		SecretKey skey = kgen.generateKey();
		return skey.getEncoded();
	}


	public static byte[] encodeFile(byte[] key, byte[] fileData) throws Exception
	{
		SecretKeySpec skeySpec = new SecretKeySpec(key, AES);
		Cipher cipher = Cipher.getInstance(AES);
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec);

		byte[] encrypted = cipher.doFinal(fileData);

		return encrypted;
	}

	public static byte[] decodeFile(byte[] key, byte[] fileData) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException
	{
		SecretKeySpec skeySpec = new SecretKeySpec(key, AES);
		Cipher cipher = Cipher.getInstance(AES);
		cipher.init(Cipher.DECRYPT_MODE, skeySpec);

		byte[] decrypted = cipher.doFinal(fileData);
		return decrypted;
	}

	public static byte[] convertFileToByteArray(File f)
	{
		byte[] contents = null;
		int size = (int) f.length();
		contents = new byte[size];
		try {
			BufferedInputStream buf = new BufferedInputStream(
					new FileInputStream(f));
			try {
				buf.read(contents);
				buf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return contents;
	}

	//save image, and encrypt it
	public static String saveAndEncryptImage(Bitmap finalBitmap, String size, String query, String id) {
		String root = Environment.getExternalStorageDirectory().toString();
		File myDir = new File(root + "/flickr_images/"+size+"/"+query);    
		myDir.mkdirs();
		String fname = id+".jpg";
		File file = new File (myDir, fname);

		if (file.exists ()) 
			return file.getAbsolutePath();
		try {
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
			//			FileOutputStream out = new FileOutputStream(file);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
			//encrypt image with SECURE KEY
			byte[] key = Encryption.generateKey(Encryption.SECURE_KEY);
			Log.d("couplekey", key.toString());
			byte[] fileBytes = Encryption.encodeFile(key, out.toByteArray() );
			bos.write(fileBytes);
			bos.flush();
			bos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return file.getAbsolutePath();
	}


	public static byte[] decodeFile(byte[] key, File file) {
		try {
			return decodeFile(key, convertFileToByteArray(file));
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

}


