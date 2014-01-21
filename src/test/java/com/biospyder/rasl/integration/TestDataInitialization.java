package com.biospyder.rasl.integration;





import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.biospyder.rasl.oligoarray.OligoArray2;



public class TestDataInitialization {
	
	private static Logger logger = LogManager
			.getLogger(TestDataInitialization.class);

	public  TestDataInitialization() {}
	
	public static void main(String[] args) {
		TestDataInitialization test = new TestDataInitialization();
		//test.testDirectRead();
		test.testOldWay();
		//test.testAccesstoDataFiles();
		

	}
	
	private void testDirectRead() {
		System.out.println("Direct read");
		String directory = "/Users/fcriscuo/RASL_PROJECT/OligoArray2_1/temp/";
		String file1 = directory + "data.dg";
		String file2 = directory + "data.dh";
		try {
			ObjectInputStream ois =
			        new ObjectInputStream(new FileInputStream(file1));
			float[][] dg = (float[][]) ois.readObject();
			
			System.out.println(dg);
			float x = dg[0][0];
			System.out.println("length = " +dg.length + "value " + (new Float(x) ).toString());
			ois.close();
			
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	 byte[] readSmallBinaryFile(String aFileName) throws IOException {
		    Path path = Paths.get(aFileName);
		    return Files.readAllBytes(path);
		  }
	// validate that the newer way returns the same results as the old ways
	private void testOldWay(){
		 System.out.println("Old way  ");
		 try {
		 String s = (new OligoArray2()).getClass().getProtectionDomain().getCodeSource().getLocation().toString();
         if(s.indexOf("OligoArray2.jar") == -1)
             s = s + "OligoArray2.jar";
         URL url = new URL("jar:" + s + "!/");
         System.out.println("URL: " +url.toString());
         JarURLConnection jarurlconnection = (JarURLConnection)url.openConnection();
         JarFile jarfile = jarurlconnection.getJarFile();
        
         
         String dataDg = "com/biospyder/rasl/oligoarray/data.dg";
         String dataDh = "com/biospyder/rasl/oligoarray/data.dh";
         ZipEntry dg = jarfile.getEntry(dataDg);
         System.out.println("Looking for data.dg found " +dg.toString());
         
         ObjectInputStream objectinputstream = new ObjectInputStream(jarfile.getInputStream(jarfile.getEntry(dataDg)));
         float[][] dataG = (float[][])objectinputstream.readObject();
         objectinputstream.close();
         objectinputstream = new ObjectInputStream(jarfile.getInputStream(jarfile.getEntry(dataDh)));
         float[][] dataH = (float[][])objectinputstream.readObject();
         objectinputstream.close();
         
         
         System.out.println("DONE");
     }
     catch(Exception exception1)
     {
         System.out.println(exception1.getMessage());
         exception1.printStackTrace();
         
     }
	}
	
	/*
	 * test the ability to read the data.dg & data.dh files from the oligodata.jar file
	 * in the classpath
	 */
	
	private void testAccesstoDataFiles() {
		System.out.print("New way  ");
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		if (null == cl){
			cl = Class.class.getClassLoader();
		}
		ObjectInputStream localObjectInputStream;
		try {
			localObjectInputStream = new ObjectInputStream(
					cl.getResourceAsStream("data.dg"));
			float [][]dataG = (float[][]) localObjectInputStream.readObject();
			localObjectInputStream.close();
			System.out.println("data.dg length = " +dataG.length
					+" sample value " +dataG[0][0]);
			localObjectInputStream = new ObjectInputStream(
					cl.getResourceAsStream("data.dh"));
			float [][]dataH = (float[][]) localObjectInputStream.readObject();
			localObjectInputStream.close();
			System.out.println("data.dh length = " +dataH.length 
					+" sample value " +dataH[1][0]);
		} catch (IOException | ClassNotFoundException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
				
		
		
	}

}
