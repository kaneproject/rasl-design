package com.biospyder.rasl.design.service;

import gnu.getopt.Getopt;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.List;
import java.util.StringTokenizer;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.biojava3.core.sequence.DNASequence;

import com.biospyder.rasl.common.Utilities;
import com.biospyder.rasl.oligoarray.Blast;
import com.biospyder.rasl.oligoarray.OligoArray2;
import com.biospyder.rasl.pojo.RaslRuntime;
import com.google.common.base.Preconditions;

/*
 * A singleton responsible for setting the runtime parameters. Sets default values as well as
 * specific values from the command line. Provides a common access point for run time values.
 * 
 */
public enum RuntimeEnvironmentFactory {
	INSTANCE;
	private static Logger logger = LogManager
			.getLogger(RuntimeEnvironmentFactory.class);
	
	
	public RaslRuntime createRaslRuntime(String[] parameters){
		RaslRuntime runtime = new RaslRuntime(parameters);
		this.validateRuntimeEnvironment(runtime);
		if(runtime.isValidRuntime()) {
			this.displayParameters(runtime);
			
		}
		return runtime;
	}
	
	private void validateRuntimeEnvironment(RaslRuntime runtime) {
		boolean valid = runtime.isValidRuntime();
		if(valid) {
			this.displayParameters(runtime);
			valid = valid & this.checkFiles(runtime);
			valid = valid & this.dataInitialization(runtime);
			valid = valid & this.isValidBlastdb(runtime);
		}
		runtime.setValidRuntime(valid);
	}

	private void displayParameters( RaslRuntime runtime) {
			System.out.println(runtime.displayParameters());
	}

	/*
	 * private method to determine if sequence, balstdb, and saveas files,
	 * specified in the parameters are valid.
	 */
	private boolean checkFiles(RaslRuntime runtime) {
		System.out.print("Can OligoArray read/write specified files?  ");
		boolean validFiles = true;
		
		try {
            boolean flag1 = true;
            File file = new File(runtime.getSeqFile());
            flag1 &= file.exists() && file.isFile() && file.canRead();
            File file1 = new File(runtime.getBlastDB());
            flag1 &= file1.exists() && file1.isFile() && file1.canRead();
            File file2 = new File(runtime.getSaveAs());
            if(!file2.exists())
                file2.createNewFile();
            runtime.setWorkDirectory(file2.getAbsoluteFile().getParentFile());
            flag1 &= runtime.getWorkDirectory().canWrite();
            if(flag1)
            {
                System.out.println("YES");
            } else
            {
                System.out.println("NO");
               validFiles = false;
            }
        }
        catch(Exception exception)
        {
            System.out.println("NO" + exception);
            return false;
        }
		return validFiles;
	}

	private boolean dataInitialization(RaslRuntime runtime) {
		try {
			String s = (new OligoArray2()).getClass().getProtectionDomain()
					.getCodeSource().getLocation().toString();
			if (s.indexOf("OligoArray2.jar") == -1)
				s = s + "OligoArray2.jar";
			URL url = new URL("jar:" + s + "!/");
			System.out.println("URL: " + url.toString());
			JarURLConnection jarurlconnection = (JarURLConnection) url
					.openConnection();
			JarFile jarfile = jarurlconnection.getJarFile();

			String dataDg = "com/biospyder/rasl/oligoarray/data.dg";
			String dataDh = "com/biospyder/rasl/oligoarray/data.dh";
			ZipEntry dg = jarfile.getEntry(dataDg);
			ObjectInputStream objectinputstream = new ObjectInputStream(
					jarfile.getInputStream(jarfile.getEntry(dataDg)));
			runtime.setDataG((float[][]) objectinputstream.readObject());
			objectinputstream.close();
			objectinputstream = new ObjectInputStream(
					jarfile.getInputStream(jarfile.getEntry(dataDh)));
			runtime.setDataH( (float[][]) objectinputstream.readObject());
			objectinputstream.close();

			System.out.println("DONE");
			return true;
		} catch (Exception exception1) {
			System.out.println(exception1.getMessage());
			exception1.printStackTrace();
			return false;

		}

	}

	
	private boolean isValidBlastdb(RaslRuntime runtime) {

		System.out.print("Is " + runtime.getBlastDB() + " a valid Blast database?  ");
		String s1 = (new Blast(runtime.getBlastDB(), "blastn", "-W 7 -F F -S 1"))
				.checkBlastDb();
		if (s1 != null) {
			System.out.println("YES");
			return true;
		} else {
			System.out.println("NO");
			return false;
		}
	}


}