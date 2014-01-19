package com.biospyder.rasl.oligoarray;

import gnu.getopt.Getopt;
import java.io.*;
import java.net.JarURLConnection;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.*;
import java.util.jar.JarFile;

public class OligoArray2
{

    public OligoArray2()
    {
    }

    public static void main(String args[])
        throws IOException
    {
        boolean flag = true;
        processors = 1;
        maxNbOligo = 1;
        lengthMin = 45;
        lengthMax = 47;
        betweenOligo = (int)((1.5D * (double)(lengthMax + lengthMin)) / 2D);
        distance = 1500;
        tmMin = 85;
        tmMax = 90;
        temperature = 65D;
        crossHyb = 65;
        minGC = 40D;
        maxGC = 60D;
        concDna = 9.9999999999999995E-007D;
        listProhibited = "";
        seqFile = "";
        blastDB = "";
        saveAs = "oligo.txt";
        rejectFile = "rejected.fas";
        log = "OligoArray.log";
        logFile = new PrintWriter(System.out);
        if(args.length == 0)
        {
            flag = false;
            displayHelp();
        } else
        {
            Getopt getopt = new Getopt("testprog", args, "-:i:d:o:r:R:n:l:L:D:t:T:s:x:p:P:m:N:g:h");
            getopt.setOpterr(false);
            int i;
            while((i = getopt.getopt()) != -1) 
                switch(i)
                {
                case 1: // '\001'
                    System.err.println("There is an error in the command line near or at '" + getopt.getOptarg() + "'");
                    break;

                case 105: // 'i'
                    seqFile = getopt.getOptarg();
                    break;

                case 100: // 'd'
                    blastDB = getopt.getOptarg();
                    break;

                case 111: // 'o'
                    saveAs = getopt.getOptarg();
                    break;

                case 114: // 'r'
                    rejectFile = getopt.getOptarg();
                    break;

                case 82: // 'R'
                    log = getopt.getOptarg();
                    break;

                case 110: // 'n'
                    maxNbOligo = (new Integer(getopt.getOptarg())).intValue();
                    break;

                case 108: // 'l'
                    lengthMin = (new Integer(getopt.getOptarg())).intValue();
                    break;

                case 76: // 'L'
                    lengthMax = (new Integer(getopt.getOptarg())).intValue();
                    break;

                case 68: // 'D'
                    distance = (new Integer(getopt.getOptarg())).intValue();
                    break;

                case 116: // 't'
                    tmMin = (new Integer(getopt.getOptarg())).intValue();
                    break;

                case 84: // 'T'
                    tmMax = (new Integer(getopt.getOptarg())).intValue();
                    break;

                case 115: // 's'
                    temperature = (new Double(getopt.getOptarg())).doubleValue();
                    break;

                case 120: // 'x'
                    crossHyb = (new Integer(getopt.getOptarg())).intValue();
                    break;

                case 112: // 'p'
                    minGC = (new Double(getopt.getOptarg())).doubleValue();
                    break;

                case 80: // 'P'
                    maxGC = (new Double(getopt.getOptarg())).doubleValue();
                    break;

                case 109: // 'm'
                    listProhibited = getopt.getOptarg();
                    break;

                case 78: // 'N'
                    processors = (new Integer(getopt.getOptarg())).intValue();
                    break;

                case 103: // 'g'
                    betweenOligo = (new Integer(getopt.getOptarg())).intValue();
                    break;

                case 104: // 'h'
                    flag = false;
                    displayHelp();
                    break;

                case 58: // ':'
                    System.out.println("You need an argument for option " + (char)getopt.getOptopt());
                    break;

                case 63: // '?'
                    System.out.println("The option '" + (char)getopt.getOptopt() + "' is not valid");
                    break;
                }
            if(seqFile.equals("") | blastDB.equals(""))
            {
                flag = false;
                System.err.println("You should use at least options -i to select the input file and -d to select the Blast database");
            }
            if(processors < 1)
            {
                flag = false;
                System.err.println("Invalid argument for option -N. The expected value is an integer > 0");
            }
            if(maxNbOligo < 1)
            {
                flag = false;
                System.err.println("Invalid argument for option -n. The expected value is an integer > 0");
            }
            if((lengthMin < 15) | (lengthMin > 75))
            {
                flag = false;
                System.err.println("Invalid argument for option -l. The expected value is an integer comprised between 15 and 75");
            }
            if((lengthMax < 15) | (lengthMax > 75))
            {
                flag = false;
                System.err.println("Invalid argument for option -L. The expected value is an integer comprised between 15 and 75");
            }
            if(lengthMin > lengthMax)
            {
                flag = false;
                System.err.println("The argument given for -l should not be bigger than the one for -L");
            }
            if(betweenOligo < 1)
            {
                flag = false;
                System.err.println("Invalid argument for option -g. The expected value is an integer > 0");
            }
            if((tmMin < 0) | (tmMin > 100))
            {
                flag = false;
                System.err.println("Invalid argument for option -t. The expected value is an integer comprised between 0 and 100");
            }
            if((tmMax < 0) | (tmMax > 100))
            {
                flag = false;
                System.err.println("Invalid argument for option -T. The expected value is an integer comprised between 0 and 100");
            }
            if(tmMin > tmMax)
            {
                flag = false;
                System.err.println("The argument given for -t should not be bigger than the one for -T");
            }
            if((temperature < 0.0D) | (temperature > 100D))
            {
                flag = false;
                System.err.println("Invalid argument for option -s. The expected value is a real comprised between 0 and 100");
            }
            if((crossHyb < 0) | (crossHyb > 100))
            {
                flag = false;
                System.err.println("Invalid argument for option -x. The expected value is an integer comprised between 0 and 100");
            }
            if((minGC < 0.0D) | (minGC > 100D))
            {
                flag = false;
                System.err.println("Invalid argument for option -p. The expected value is a real comprised between 0 and 100");
            }
            if((maxGC < 0.0D) | (maxGC > 100D))
            {
                flag = false;
                System.err.println("Invalid argument for option -P. The expected value is a real comprised between 0 and 100");
            }
            if(tmMin > tmMax)
            {
                flag = false;
                System.err.println("The argument given for -p should not be bigger than the one for -P");
            }
        }
        if(flag)
        {
            System.out.println("\n\t***\tOligoArray 2.1.3\t***");
            System.out.println("\nOligoArray 2.1.3 will start to process sequences from the file " + seqFile + " using the following parameters :");
            System.out.println("Blast database: '" + blastDB + "'");
            System.out.println("Oligo data will be saved in: '" + saveAs + "'");
            System.out.println("Sequence without oligo will be saved in: '" + rejectFile + "'");
            System.out.println("The log file will be: '" + log + "'");
            System.out.println("Maximum number of oligo to design per input sequence: '" + maxNbOligo + "'");
            System.out.println("Size range: '" + lengthMin + "' to '" + lengthMax + "'");
            System.out.println("Maximum distance between the 5' end of the oligo and the 3' end of the input sequence: '" + distance + "'");
            System.out.println("Minimum distance between the 5' ends of two adjacent oligos: '" + betweenOligo + "'");
            System.out.println("Tm range: '" + tmMin + "' to '" + tmMax + "'");
            System.out.println("GC range: '" + minGC + "' to '" + maxGC + "'");
            System.out.println("Threshold to reject secondary structures: '" + temperature + "'");
            System.out.println("Threshold to start to consider cross-hybridizations: '" + crossHyb + "'");
            System.out.println("Sequence to avoid in the oligo: '" + listProhibited + "'");
            System.out.println("Number of sequence to run in parallel: '" + processors + "'");
            System.out.println();
            System.out.print("Can OligoArray read/write specified files?  ");
            try
            {
                boolean flag1 = true;
                File file = new File(seqFile);
                flag1 &= file.exists() && file.isFile() && file.canRead();
                File file1 = new File(blastDB);
                flag1 &= file1.exists() && file1.isFile() && file1.canRead();
                File file2 = new File(saveAs);
                if(!file2.exists())
                    file2.createNewFile();
                workDirectory = file2.getAbsoluteFile().getParentFile();
                flag1 &= workDirectory.canWrite();
                if(flag1)
                {
                    System.out.println("YES");
                } else
                {
                    System.out.println("NO");
                    flag = false;
                }
            }
            catch(Exception exception)
            {
                System.out.println("NO" + exception);
                flag = false;
            }
            try
            {
                System.out.print("Data initialization: ");
                
                String s = (new OligoArray2()).getClass().getProtectionDomain().getCodeSource().getLocation().toString();
                if(s.indexOf("OligoArray2.jar") == -1)
                    s = s + "OligoArray2.jar";
                
                URL url = new URL("jar:" + s + "!/");
               
                String dataDg = "com/biospyder/rasl/oligoarray/data.dg";
                String dataDh = "com/biospyder/rasl/oligoarray/data.dh";
                JarURLConnection jarurlconnection = (JarURLConnection)url.openConnection();
                JarFile jarfile = jarurlconnection.getJarFile();
                ObjectInputStream objectinputstream = new ObjectInputStream(jarfile.getInputStream(jarfile.getEntry(dataDg)));
                dataG = (float[][])objectinputstream.readObject();
                objectinputstream.close();
                objectinputstream = new ObjectInputStream(jarfile.getInputStream(jarfile.getEntry(dataDh)));
                dataH = (float[][])objectinputstream.readObject();
                objectinputstream.close();
                
                
                System.out.println("DONE");
            }
            catch(Exception exception1)
            {
                logFile.println(exception1);
                System.err.println("FAILED (" + exception1 + ")");
                flag = false;
            }
            System.out.print("Is " + blastDB + " a valid Blast database?  ");
            String s1 = (new Blast(blastDB, "blastn", "-W 7 -F F -S 1")).checkBlastDb();
            if(s1 != null)
            {
                System.out.println("YES");
            } else
            {
                System.out.println("NO");
                flag = false;
            }
            System.out.print("Is OligoArrayAux installed?  ");
            try
            {
                boolean flag2 = checkOligoArrayAux();
                if(flag2)
                {
                    System.out.println("YES");
                } else
                {
                    System.out.println("NO");
                    flag = false;
                }
            }
            catch(Exception exception2)
            {
                System.out.println("NO");
                flag = false;
            }
            if(flag)
            {
                System.out.println("\n");
                runThermoBlast();
            } else
            {
                System.out.println("\nDesign aborted due to a failure in the test above");
            }
        }
    }

    private static boolean checkOligoArrayAux()
        throws Exception
    {
        boolean flag = false;
        Runtime runtime = Runtime.getRuntime();
        String s = "/usr/local/bin/hybrid-ss-min -nDNA -t65.0 -T65.0 -M0 -N1 -q CATCATTATCCACATTTTGATATCTATATCTCATTCGGCGGTCCCAA";
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

    private static synchronized void runThermoBlast()
        throws IOException
    {
        readBuff = new BufferedReader(new FileReader(seqFile));
        try
        {
            boolean flag = true;
            String s = readBuff.readLine();
            orf = s.substring(1);
            s = readBuff.readLine();
            String s5 = "";
            for(; s != null && s.indexOf('>') == -1; s = readBuff.readLine())
                s5 = s5.concat(s);

            String s6 = ">" + orf + "\n" + s5;
            hsp_table = intializeBlastParameters(s6);
            System.out.println("Blast parameters initialized\n");
            readBuff.close();
            readBuff = new BufferedReader(new FileReader(seqFile));
            String s7 = (new Long((new Date()).getTime())).toString();
            s7 = s7.substring(s7.length() - 8);
            tempDirectory = new File(workDirectory, s7);
            if(!tempDirectory.exists())
                tempDirectory.mkdirs();
            Design adesign[] = new Design[processors];
            SeqDispenser seqdispenser = new SeqDispenser();
            boolean flag1 = false;
            for(int i = 0; i < processors; i++)
            {
                adesign[i] = new Design(seqdispenser, blastDB, maxNbOligo, lengthMin, lengthMax, distance, betweenOligo, tmMin, tmMax, temperature, crossHyb, minGC, maxGC, listProhibited, concDna, dataG, dataH, (new File(tempDirectory, (new Integer(i)).toString())).toString());
                adesign[i].start();
            }

            for(String s1 = readBuff.readLine(); s1 != null;)
            {
                String s8 = s1 + "\n";
                for(s1 = readBuff.readLine(); s1 != null && s1.indexOf('>') == -1; s1 = readBuff.readLine())
                    s8 = s8.concat(s1.toUpperCase());

                seqdispenser.put(new Sequence(s8));
            }

            readBuff.close();
            System.out.println("No more sequence to dispatch");
            for(int j = 0; j < processors; j++)
                seqdispenser.put(null);

            for(int k = 0; k < processors; k++)
                while(adesign[k].isAlive()) ;

            oligoDb = new PrintWriter(new FileOutputStream(saveAs));
            skipped = new PrintWriter(new FileOutputStream(rejectFile));
            logFile = new PrintWriter(new FileOutputStream(log));
            for(int l = 0; l < processors; l++)
            {
                File file = new File(tempDirectory, (new Integer(l)).toString());
                readBuff = new BufferedReader(new FileReader(new File(file, "oligos.txt")));
                for(String s2 = readBuff.readLine(); s2 != null; s2 = readBuff.readLine())
                    oligoDb.println(s2);

                readBuff.close();
                readBuff = new BufferedReader(new FileReader(new File(file, "rejected.fas")));
                for(String s3 = readBuff.readLine(); s3 != null; s3 = readBuff.readLine())
                    skipped.println(s3);

                readBuff.close();
                readBuff = new BufferedReader(new FileReader(new File(file, "OligoArray.log")));
                for(String s4 = readBuff.readLine(); s4 != null; s4 = readBuff.readLine())
                    logFile.println(s4);

                readBuff.close();
                if(file.exists())
                {
                    File afile[] = file.listFiles();
                    for(int i1 = 0; i1 < afile.length; i1++)
                        afile[i1].delete();

                    file.delete();
                    tempDirectory.delete();
                }
            }

            oligoDb.close();
            skipped.close();
            logFile.close();
            System.out.println("OligoArray has successfully processed all sequences");
        }
        catch(Exception exception)
        {
            logFile.println(exception);
        }
    }

    public static synchronized Sequence getSequence()
        throws IOException
    {
        String s = readBuff.readLine();
        String s2 = null;
        s2 = s + "\n";
        for(String s1 = readBuff.readLine(); s1 != null && s1.indexOf('>') == -1; s1 = readBuff.readLine())
        {
            s2 = s2.concat(s1.toUpperCase());
            readBuff.mark(2 * s1.length());
        }

        readBuff.reset();
        Sequence sequence1;
        if(s2 != null)
            sequence1 = new Sequence(s2);
        else
            sequence1 = null;
        return sequence1;
    }

    public static int[] intializeBlastParameters(String s)
    {
        int ai[] = new int[0];
        System.out.println("Start Blast parameters initialization (It may take a while depending the value entered for the -D option)");
        System.out.println("Input: " +s);
        try
        {
            String s1;
            for(s1 = s.substring(s.indexOf("\n") + 1); s1.length() < distance + 200; s1 = s1 + s.substring(s.indexOf("\n") + 1));
            ai = new int[(distance + 200) / 100];
            for(int i = 1; i <= (distance + 200) / 100; i++)
            {
                String s2 = ">seq\n" + s1.substring(0, i * 100) + "\n\n";
                String s3 = "/usr/local/ncbi/blast-2.2.26/bin/blastall -p blastn -d " + blastDB + " -S 1 -F F -b 1 -v 1";
                Process process = Runtime.getRuntime().exec(s3);
                OutputStream outputstream = process.getOutputStream();
                BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                outputstream.write(s2.getBytes(), 0, s2.getBytes().length);
                outputstream.close();
                for(String s4 = bufferedreader.readLine(); s4 != null && s4.indexOf("X1: ") != 0; s4 = bufferedreader.readLine())
                {
                    if(s4.indexOf("Gapped") == 0)
                    {
                        bufferedreader.readLine();
                        s4 = bufferedreader.readLine();
                        StringTokenizer stringtokenizer = new StringTokenizer(s4);
                        lambda = (new Double(stringtokenizer.nextToken())).doubleValue();
                        K = (new Double(stringtokenizer.nextToken())).doubleValue();
                    }
                    if(s4.indexOf("Number of Sequences:") == 0)
                    {
                        String s5;
                        for(s5 = s4.substring(s4.indexOf(":") + 2); s5.indexOf(",") != -1; s5 = s5.substring(0, s5.indexOf(",")) + s5.substring(s5.indexOf(",") + 1));
                        nbSeqBlastDb = (new Double(s5)).doubleValue();
                    }
                    if(s4.indexOf("effective HSP length:") == 0)
                    {
                        String s6;
                        for(s6 = s4.substring(s4.indexOf(":") + 2); s6.indexOf(",") != -1; s6 = s6.substring(0, s6.indexOf(",")) + s6.substring(s6.indexOf(",") + 1));
                        hsp = (new Integer(s6)).intValue();
                    }
                    if(s4.indexOf("length of database:") == 0)
                    {
                        String s7;
                        for(s7 = s4.substring(s4.indexOf(":") + 2); s7.indexOf(",") != -1; s7 = s7.substring(0, s7.indexOf(",")) + s7.substring(s7.indexOf(",") + 1));
                        blastDbLength = (new Double(s7)).doubleValue();
                    }
                }

                ai[i - 1] = hsp;
            }

        }
        catch(Exception exception)
        {
            logFile.println(exception);
            System.err.println("error during blast parameters initialization: " + exception);
        }
        return ai;
    }

    public static int getHSP(int i)
    {
        int j = 0;
        j = hsp_table[i / 100];
        return j;
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
        System.out.println("           Defaul is '1500'\n");
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

    private static int lengthMin;
    private static int lengthMax;
    private static int distance;
    private static int tmMin;
    private static int tmMax;
    private static int deltaTm;
    private static int crossHyb;
    private static int length;
    private static int processors;
    private static int begin;
    private static int end;
    private static int nbAlignment;
    private static int maxNbOligo;
    private static int originalLength;
    private static int hsp;
    private static int betweenOligo;
    private static String listProhibited;
    private static String seqFile;
    private static String blastDB;
    private static String saveAs;
    private static String rejectFile;
    private static String log;
    private static String orf;
    private static String seqName;
    private static String sequence;
    private static BufferedReader readBuff;
    public static PrintWriter logFile;
    public static PrintWriter skipped;
    public static PrintWriter oligoDb;
    private static Sequence seq;
    private static Sequence oligoSequence;
    private static double concDna;
    private static double temperature;
    private static double temperatureK;
    private static double dGStruct;
    private static double minGC;
    private static double maxGC;
    public static double lambda;
    public static double K;
    public static double blastDbLength;
    public static double nbSeqBlastDb;
    private static String alignment[][];
    private static double thermoData[][];
    public static int hsp_table[];
    private static Vector usedPos;
    public static float dataG[][];
    public static float dataH[][];
    private static File workDirectory;
    private static File tempDirectory;
    private static final String version = "2.1.3";
}
