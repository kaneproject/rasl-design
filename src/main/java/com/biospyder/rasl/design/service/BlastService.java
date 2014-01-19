package com.biospyder.rasl.design.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.StringTokenizer;

import com.biospyder.rasl.pojo.RaslRuntime;

public enum BlastService {
	
	INSTANCE;
	/*
	 * public method to initialize BLAST runtime code from OligoArray2 w/o
	 * modification
	 */
	public int[] intializeBlastParameters(RaslRuntime runtime, String s) {
		int ai[] = new int[0];
		System.out
				.println("Start Blast parameters initialization (It may take a while depending the value entered for the -D option)");
		try {
			String s1;
			Integer distance = runtime.getDistance();
			for (s1 = s.substring(s.indexOf("\n") + 1); s1.length() < distance + 200; s1 = s1
					+ s.substring(s.indexOf("\n") + 1))
				;
			ai = new int[(distance + 200) / 100];
			for (int i = 1; i <= (distance + 200) / 100; i++) {
				String s2 = ">seq\n" + s1.substring(0, i * 100) + "\n\n";
				String s3 = "blastall -p blastn -d " + runtime.getBlastDB()
						+ " -S 1 -F F -b 1 -v 1";
				Process process = Runtime.getRuntime().exec(s3);
				OutputStream outputstream = process.getOutputStream();
				BufferedReader bufferedreader = new BufferedReader(
						new InputStreamReader(process.getInputStream()));
				outputstream.write(s2.getBytes(), 0, s2.getBytes().length);
				outputstream.close();
				for (String s4 = bufferedreader.readLine(); s4 != null
						&& s4.indexOf("X1: ") != 0; s4 = bufferedreader
						.readLine()) {
					if (s4.indexOf("Gapped") == 0) {
						bufferedreader.readLine();
						s4 = bufferedreader.readLine();
						StringTokenizer stringtokenizer = new StringTokenizer(
								s4);
						runtime.setLambda ((new Double(stringtokenizer.nextToken()))
								.doubleValue());
						runtime.setK( (new Double(stringtokenizer.nextToken()))
								.doubleValue());
					}
					if (s4.indexOf("Number of Sequences:") == 0) {
						String s5;
						for (s5 = s4.substring(s4.indexOf(":") + 2); s5
								.indexOf(",") != -1; s5 = s5.substring(0,
								s5.indexOf(","))
								+ s5.substring(s5.indexOf(",") + 1))
							;
						runtime.setNbSeqBlastDb( (new Double(s5)).doubleValue());
					}
					if (s4.indexOf("effective HSP length:") == 0) {
						String s6;
						for (s6 = s4.substring(s4.indexOf(":") + 2); s6
								.indexOf(",") != -1; s6 = s6.substring(0,
								s6.indexOf(","))
								+ s6.substring(s6.indexOf(",") + 1))
							;
						runtime.setHsp( (new Integer(s6)).intValue());
						
					}
					if (s4.indexOf("length of database:") == 0) {
						String s7;
						for (s7 = s4.substring(s4.indexOf(":") + 2); s7
								.indexOf(",") != -1; s7 = s7.substring(0,
								s7.indexOf(","))
								+ s7.substring(s7.indexOf(",") + 1))
							;
						runtime.setBlastDbLength((new Double(s7)).doubleValue());
					}
				}

				ai[i - 1] = runtime.getHsp();
			}

		} catch (Exception exception) {
			runtime.getLogFile().println(exception);
			System.err.println("error during blast parameters initialization: "
					+ exception);
		}
		return ai;
	}


}
