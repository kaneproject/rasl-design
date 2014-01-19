package com.biospyder.rasl.common;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/*
 * represents a container for static utility methods used throughout the application
 */
public class Utilities {
	
	/*
	 * Utility method from OligoArray2 to check if required hybrid-ss-min executable is available
	 */
	public static boolean checkOligoArrayAux()
	        throws Exception
	    {
	        boolean flag = false;
	        Runtime runtime = Runtime.getRuntime();
	        String s = "hybrid-ss-min -nDNA -t65.0 -T65.0 -M0 -N1 -q CATCATTATCCACATTTTGATATCTATATCTCATTCGGCGGTCCCAA";
	        Process process = runtime.exec(s);
	        BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(process.getInputStream()));
	        String s1 = bufferedreader.readLine();
	        try
	        {
	            double d = (new Double(s1)).doubleValue();
	            flag = true;
	        }
	        catch(Exception exception) { }
	        bufferedreader.close();
	        process.destroy();
	        return flag;
	    }
	
	
	public static void displayHelp()
    {
        System.out.println("\n\nNAME");
        System.out.println("     OligoArray2.1.3 - Oligonucleotide design for Microarrays\n");
        System.out.println("SYNOPSIS");
        System.out.println("     java OligoArray2");
        System.out.println("     java OligoArray2 [-h]");
        System.out.println("     java OligoArray2 [-i] [-d] [-orRnlLDtTsxpPmNg]\n\n");
        System.out.println("DESCRIPTION");
        System.out.println("     OligoArray2 is a program to design specific oligonucleotide at the genome");
        System.out.println("     scale in order to perform gene expression profiling using microarrays\n");
        System.out.println("OPTIONS");
        System.out.println("     Command line options are described below.\n");
        System.out.println("     -i    The input file that contains sequences to process. Expected format");
        System.out.println("           is FastA. A file name is expected. This option is required\n");
        System.out.println("     -d    The Blast database that will be used to compute oligo's specificity");
        System.out.println("           A database name is expected. This option is required\n");
        System.out.println("     -o    The output file that will contain oligonucleotide data. A file name");
        System.out.println("           is expected. Default is 'oligo.txt'\n");
        System.out.println("     -r    The file that will contain sequences for which the design failed. A");
        System.out.println("           file name is expected. Default is 'rejected.fas'\n");
        System.out.println("     -R    The log file that will contain informations generated during design");
        System.out.println("           A file name is expected. Default is 'OligoArray.log'\n");
        System.out.println("     -n    The maximum number of oligonucleotides expected per input sequences");
        System.out.println("           A positive integer is expected. Default is '1'\n");
        System.out.println("     -l    The minimum oligonucleotide length. An integer comprised between 15");
        System.out.println("           and 75 is expected. (Default is '45')\n");
        System.out.println("     -L    The maximum oligonucleotide length. An integer comprised between 15");
        System.out.println("           and 75 is expected. (Default is '47')\n");
        System.out.println("     -D    The maximum distance accepted between the 5' end of the oligo and");
        System.out.println("           the 3' end of the input sequence. A positive integer is expected.");
        System.out.println("           Default is '1500'\n");
        System.out.println("     -t    The minimum oligonucleotide Tm. A positive integer below 100 and");
        System.out.println("           below the maximum Tm is expected. (Default is '85')\n");
        System.out.println("     -T    The maximun oligonucleotide Tm. A positive integer below 100 and");
        System.out.println("           above the minimum Tm is expected. (Default is '90')\n");
        System.out.println("     -s    A temperature to use during secondary structure prediction. An oligo");
        System.out.println("           will be rejected if it can fold into a stable secondary structure at");
        System.out.println("           this temperature. A positive real is expected. Default is '65.0'\n");
        System.out.println("     -x    A threshold to start to consider putative cross-hybridizations. All");
        System.out.println("           targets hybridizing with this oligo with a Tm above this threshold");
        System.out.println("           will be reported. A positive integer is expected. Default is '65'\n");
        System.out.println("     -p    The minimum oligonucleotide GC content. A positive real below 100");
        System.out.println("           and below the maximum GC content is expected. Default is '40'\n");
        System.out.println("     -P    The maximun oligonucleotide GC content. A positive real below 100");
        System.out.println("           and above the minimum GC content is expected. Default is '60'\n");
        System.out.println("     -m    A list of prohibited sequences to mask in the input sequence. These");
        System.out.println("           sequences will never appear in the oligo sequence. Items are");
        System.out.println("           separated by semi-colon in the list: \"CCCCC;GGGGG\". Default is '\"\"'\n");
        System.out.println("     -N    The number of sequences to process at the same time. Depending on");
        System.out.println("           the number of processors and the memory available, you can process");
        System.out.println("           up to 3 sequences in parallel per processors. Default is '1'\n");
        System.out.println("     -g    The minimum distance between the 5' end of two adjacent oligos. If");
        System.out.println("           you want to avoid any overlaps between oligos, you should use a");
        System.out.println("           value bigger than the maximum oligo length. A positive integer is.");
        System.out.println("           expected Default is '1.5 * the average oligo size'\n");
    }

}
