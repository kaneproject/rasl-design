package com.biospyder.rasl.oligoarray;
import java.io.*;
import java.util.*;

/*
 * The class was copied from the OligoAray2 application
 */

public class Blast
{

    public Blast(String s, String s1, String s2)
    {
        db = s;
        prog = s1;
        option = s2;
    }

    public String checkBlastDb()
    {
        String s = null;
        try
        {
            BufferedReader bufferedreader = blast(">name\nATGGGTATGCCATTGCATTACGGCATCGCGCCTAGCGCGCTACGGCATCGAGCGGTGTGTGGCTTTGCGAGCGGCGCATCGG\n\n");
            for(String s1 = bufferedreader.readLine(); s1 != null && s1.indexOf(">") == -1; s1 = bufferedreader.readLine())
                if(s1.indexOf("Database:") == 0)
                    s = s1;

            bufferedreader.close();
            child.destroy();
        }
        catch(IOException ioexception)
        {
            s = null;
            System.err.println(ioexception);
            ioexception.printStackTrace();
        }
        System.out.println("checkBlastdb " +s);
        return s;
    }

    public BufferedReader blast(String s, String s1)
        throws IOException
    {
        String s2 = ">" + s + "\n" + s1;
        return blast(s2);
    }

    public BufferedReader blast(String s)
        throws IOException
    {
        s = s + "\n\n";
        String s1 = "/usr/local/ncbi/blast-2.2.26/bin/blastall -p " + prog + " -d " + db + " " + option;
        System.out.println("BLAST CMD: " +s1);
        Runtime runtime = Runtime.getRuntime();
        child = runtime.exec(s1);
        OutputStream outputstream = child.getOutputStream();
        BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(child.getInputStream()));
        outputstream.write(s.getBytes(), 0, s.getBytes().length);
        outputstream.close();
        return bufferedreader;
    }

    public int getPpid()
    {
        int i = -1;
        String s = "./PPID.script";
        try
        {
            Process process = Runtime.getRuntime().exec(s);
            BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            for(String s1 = bufferedreader.readLine(); s1 != null; s1 = bufferedreader.readLine())
                i = (new Integer(s1)).intValue();

            int j = process.waitFor();
        }
        catch(Exception exception)
        {
            System.err.println("From Blast " + exception);
        }
        return i;
    }

    public String getBlastDb()
    {
        return db;
    }

    public void setBlastDb(String s)
    {
        db = s;
    }

    public String getBlastProg()
    {
        return prog;
    }

    public void setBlastProg(String s)
    {
        prog = s;
    }

    public String getBlastOpt()
    {
        return option;
    }

    public void setBlastOpt(String s)
    {
        prog = s;
    }

    public String[][] alignmentMatrix(BufferedReader bufferedreader, int i)
        throws IOException
    {
        Vector vector = new Vector();
        String s = bufferedreader.readLine();
        int j = 0;
        for(; s != null && (s.indexOf("producing") == -1) & (s.indexOf("*** No hits found ****") == -1); s = bufferedreader.readLine())
            if(s.indexOf("Query=") == 0)
            {
                s = bufferedreader.readLine();
                String s1;
                for(s1 = s.substring(s.indexOf("(") + 1, s.indexOf("letters") - 1); s1.indexOf(",") != -1; s1 = s1.substring(0, s1.indexOf(",")) + s1.substring(s1.indexOf(",") + 1));
                j = (new Integer(s1)).intValue();
            }

        s = bufferedreader.readLine();
        for(s = bufferedreader.readLine(); s != null && (s.indexOf(">") != 0) & (s.indexOf("X1: ") != 0); s = bufferedreader.readLine())
            System.out.println(s);

        String s2 = "";
        while(s != null && s.indexOf("X1: ") != 0) 
        {
            if(s.indexOf(">") == 0)
            {
                s2 = s.substring(1);
                s = bufferedreader.readLine();
            }
            for(; s != null && s.indexOf(">") != 0 && s.indexOf("X1: ") != 0; s = bufferedreader.readLine())
            {
                System.out.println(s);
                if(s.indexOf("Identities") == 1)
                {
                    int k = (new Integer(s.substring(s.indexOf("/") + 1, s.indexOf("(") - 1))).intValue();
                    s = bufferedreader.readLine();
                    boolean flag = false;
                    String as1[] = new String[j + 1];
                    as1[0] = s2;
                    int i1 = 0;
                    for(; s != null && s.indexOf("Score") != 1 && s.indexOf(">") != 0; s = bufferedreader.readLine())
                        if(s.indexOf("Query:") == 0 && k >= i)
                        {
                            flag = true;
                            StringTokenizer stringtokenizer = new StringTokenizer(s);
                            stringtokenizer.nextToken();
                            int k1 = (new Integer(stringtokenizer.nextToken())).intValue();
                            if(i1 == 0)
                                i1 = k1 - 1;
                            String s3 = stringtokenizer.nextToken();
                            stringtokenizer.nextToken();
                            bufferedreader.readLine();
                            s = bufferedreader.readLine();
                            stringtokenizer = new StringTokenizer(s);
                            stringtokenizer.nextToken();
                            stringtokenizer.nextToken();
                            String s4 = stringtokenizer.nextToken();
                            if(s3.indexOf("-") == -1)
                            {
                                for(int l1 = 0; l1 < s3.length(); l1++)
                                    as1[l1 + k1] = s4.substring(l1, l1 + 1);

                                i1 = s3.length();
                            } else
                            {
                                int i2 = -1;
                                int j2 = s3.indexOf("-");
                                boolean flag1 = false;
                                if(j2 == 0)
                                    flag1 = true;
                                while(flag1) 
                                {
                                    as1[i1] = as1[i1] + s4.substring(j2, j2 + 1);
                                    i2 = j2;
                                    j2 = s3.indexOf("-", i2 + 1);
                                    if(i2 + 1 == j2)
                                        flag1 = true;
                                    else
                                        flag1 = false;
                                }
                                if(j2 == -1)
                                {
                                    for(int k2 = i2 + 1; k2 < s3.length(); k2++)
                                    {
                                        i1++;
                                        as1[i1] = s4.substring(k2, k2 + 1);
                                    }

                                } else
                                {
                                    for(; j2 != -1; j2 = s3.indexOf("-", j2 + 1))
                                    {
                                        if(j2 - i2 > 1)
                                        {
                                            for(int l2 = i2 + 1; l2 < j2; l2++)
                                            {
                                                i1++;
                                                as1[i1] = s4.substring(l2, l2 + 1);
                                            }

                                            as1[i1] = as1[i1] + s4.substring(j2, j2 + 1);
                                        } else
                                        {
                                            as1[i1] = as1[i1] + s4.substring(j2, j2 + 1);
                                        }
                                        i2 = j2;
                                    }

                                    for(int i3 = i2 + 1; i3 < s3.length(); i3++)
                                    {
                                        i1++;
                                        as1[i1] = s4.substring(i3, i3 + 1);
                                    }

                                }
                            }
                        }

                    if(flag)
                    {
                        for(int j1 = 0; j1 < as1.length; j1++)
                            if(as1[j1] == null || as1[j1] == "-")
                                as1[j1] = "";

                        vector.add(as1);
                    }
                }
                if(s != null && s.indexOf(">") == 0)
                    s2 = s.substring(1);
            }

        }
        bufferedreader.close();
        String as[][] = new String[vector.size()][];
        for(int l = 0; l < vector.size(); l++)
            as[l] = (String[])vector.elementAt(l);

        child.destroy();
        return as;
    }

    public void showAlignment(BufferedReader bufferedreader)
        throws IOException
    {
        for(String s = bufferedreader.readLine(); s != null; s = bufferedreader.readLine())
            System.out.println(s);

        child.destroy();
    }

    public String[][] alignmentCompactMatrix(BufferedReader bufferedreader, int i)
        throws IOException
    {
        Vector vector = new Vector();
        Vector vector1 = new Vector();
        Hashtable hashtable = new Hashtable();
        String s = bufferedreader.readLine();
        int j = 0;
        for(; s != null && (s.indexOf("producing") == -1) & (s.indexOf("*** No hits found ****") == -1); s = bufferedreader.readLine())
            if(s.indexOf("Query=") == 0)
            {
                s = bufferedreader.readLine();
                String s1;
                for(s1 = s.substring(s.indexOf("(") + 1, s.indexOf("letters") - 1); s1.indexOf(",") != -1; s1 = s1.substring(0, s1.indexOf(",")) + s1.substring(s1.indexOf(",") + 1));
                j = (new Integer(s1)).intValue();
            }

        s = bufferedreader.readLine();
        for(s = bufferedreader.readLine(); s != null && (s.indexOf(">") != 0) & (s.indexOf("X1: ") != 0); s = bufferedreader.readLine());
        String s2 = "";
        while(s != null && s.indexOf("X1: ") != 0) 
        {
            if(s.indexOf(">") == 0)
            {
                s2 = s.substring(1);
                s = bufferedreader.readLine();
            }
            while(s != null && (s.indexOf(">") != 0) & (s.indexOf("X1: ") != 0)) 
            {
                if(s.indexOf("Identities") == 1)
                {
                    int k = (new Integer(s.substring(s.indexOf("/") + 1, s.indexOf("(") - 1))).intValue();
                    s = bufferedreader.readLine();
                    boolean flag = false;
                    String as1[] = new String[j + 1];
                    int i1 = 0;
                    for(; s != null && (s.indexOf("Score") != 1) & (s.indexOf(">") != 0) & (s.indexOf("X1: ") != 0); s = bufferedreader.readLine())
                        if(s.indexOf("Query:") == 0 && k >= i)
                        {
                            flag = true;
                            StringTokenizer stringtokenizer = new StringTokenizer(s);
                            stringtokenizer.nextToken();
                            int j1 = (new Integer(stringtokenizer.nextToken())).intValue();
                            if(i1 == 0)
                                i1 = j1 - 1;
                            String s4 = stringtokenizer.nextToken();
                            stringtokenizer.nextToken();
                            bufferedreader.readLine();
                            s = bufferedreader.readLine();
                            stringtokenizer = new StringTokenizer(s);
                            stringtokenizer.nextToken();
                            stringtokenizer.nextToken();
                            String s5 = stringtokenizer.nextToken();
                            if(s4.indexOf("-") == -1)
                            {
                                for(int i2 = 0; i2 < s4.length(); i2++)
                                    as1[i2 + j1] = s5.substring(i2, i2 + 1);

                                i1 = (j1 + s4.length()) - 1;
                            } else
                            {
                                int j2 = -1;
                                int k2 = s4.indexOf("-");
                                boolean flag1 = false;
                                if(k2 == 0)
                                    flag1 = true;
                                while(flag1) 
                                {
                                    as1[i1] = as1[i1] + s5.substring(k2, k2 + 1);
                                    j2 = k2;
                                    k2 = s4.indexOf("-", j2 + 1);
                                    if(j2 + 1 == k2)
                                        flag1 = true;
                                    else
                                        flag1 = false;
                                }
                                if(k2 == -1)
                                {
                                    for(int l2 = j2 + 1; l2 < s4.length(); l2++)
                                    {
                                        i1++;
                                        as1[i1] = s5.substring(l2, l2 + 1);
                                    }

                                } else
                                {
                                    for(; k2 != -1; k2 = s4.indexOf("-", k2 + 1))
                                    {
                                        if(k2 - j2 > 1)
                                        {
                                            for(int i3 = j2 + 1; i3 < k2; i3++)
                                            {
                                                i1++;
                                                as1[i1] = s5.substring(i3, i3 + 1);
                                            }

                                            as1[i1] = as1[i1] + s5.substring(k2, k2 + 1);
                                        } else
                                        {
                                            as1[i1] = as1[i1] + s5.substring(k2, k2 + 1);
                                        }
                                        j2 = k2;
                                    }

                                    for(int j3 = j2 + 1; j3 < s4.length(); j3++)
                                    {
                                        i1++;
                                        as1[i1] = s5.substring(j3, j3 + 1);
                                    }

                                }
                            }
                        }

                    String s3 = "";
                    if(flag)
                    {
                        for(int k1 = 0; k1 < as1.length; k1++)
                        {
                            if(as1[k1] == null || as1[k1] == "-")
                                as1[k1] = "";
                            s3 = s3.concat(as1[k1] + ",");
                        }

                        Object obj = hashtable.get(s3);
                        if(obj != null)
                        {
                            int l1 = ((Integer)obj).intValue();
                            String s6 = ((String)vector1.elementAt(l1)).trim() + ", " + s2;
                            vector1.set(l1, s6);
                        } else
                        {
                            vector.add(as1);
                            vector1.add(s2);
                            hashtable.put(s3, new Integer(vector.size() - 1));
                        }
                    }
                }
                if(s != null && s.indexOf(">") == 0)
                    s2 = s.substring(1);
                if(s != null && s.indexOf("X1: ") != 0)
                    s = bufferedreader.readLine();
            }
        }
        bufferedreader.close();
        String as[][] = new String[vector.size()][];
        for(int l = 0; l < vector.size(); l++)
        {
            as[l] = (String[])vector.elementAt(l);
            as[l][0] = (String)vector1.elementAt(l);
        }

        child.destroy();
        return as;
    }

    public void alignmentUltraCompactMatrix(BufferedReader bufferedreader, int i)
        throws IOException
    {
        Vector vector = new Vector();
        Vector vector1 = new Vector();
        Hashtable hashtable = new Hashtable();
        String s = bufferedreader.readLine();
        int j = 0;
        for(; s != null && s.indexOf("producing") == -1; s = bufferedreader.readLine())
            if(s.indexOf("Query=") == 0)
            {
                s = bufferedreader.readLine();
                String s1;
                for(s1 = s.substring(s.indexOf("(") + 1, s.indexOf("letters") - 1); s1.indexOf(",") != -1; s1 = s1.substring(0, s1.indexOf(",")) + s1.substring(s1.indexOf(",") + 1));
                j = (new Integer(s1)).intValue();
            }

        s = bufferedreader.readLine();
        for(s = bufferedreader.readLine(); s != null && s.indexOf(">") != 0; s = bufferedreader.readLine());
        String s2 = "";
        while(s != null) 
        {
            if(s.indexOf(">") == 0)
            {
                s2 = s.substring(1);
                s = bufferedreader.readLine();
            }
            for(; s != null && s.indexOf(">") != 0; s = bufferedreader.readLine())
            {
                if(s.indexOf("Identities") == 1)
                {
                    int k = (new Integer(s.substring(s.indexOf("/") + 1, s.indexOf("(") - 1))).intValue();
                    s = bufferedreader.readLine();
                    boolean flag = false;
                    byte abyte0[][] = new byte[j + 1][];
                    int i1 = 0;
                    String s3 = "";
                    for(; s != null && s.indexOf("Score") != 1 && s.indexOf(">") != 0; s = bufferedreader.readLine())
                        if(s.indexOf("Query:") == 0 && k >= i)
                        {
                            flag = true;
                            StringTokenizer stringtokenizer = new StringTokenizer(s);
                            stringtokenizer.nextToken();
                            int k1 = (new Integer(stringtokenizer.nextToken())).intValue();
                            if(i1 == 0)
                                i1 = k1 - 1;
                            String s4 = stringtokenizer.nextToken();
                            bufferedreader.readLine();
                            s = bufferedreader.readLine();
                            stringtokenizer = new StringTokenizer(s);
                            stringtokenizer.nextToken();
                            stringtokenizer.nextToken();
                            String s5 = stringtokenizer.nextToken();
                            if(s4.indexOf("-") == -1)
                            {
                                for(int j2 = 0; j2 < s4.length(); j2++)
                                {
                                    s3 = s5.substring(j2, j2 + 1);
                                    abyte0[j2 + k1][0] = convertToByte(s3);
                                }

                                i1 = (k1 + s4.length()) - 1;
                            } else
                            {
                                int k2 = -1;
                                int i3 = s4.indexOf("-");
                                boolean flag1 = false;
                                if(i3 == 0)
                                    flag1 = true;
                                while(flag1) 
                                {
                                    s3 = s3 + s5.substring(i3, i3 + 1);
                                    k2 = i3;
                                    i3 = s4.indexOf("-", k2 + 1);
                                    if(k2 + 1 == i3)
                                        flag1 = true;
                                    else
                                        flag1 = false;
                                }
                                abyte0[i1] = convertToByte(s3.toCharArray());
                                s3 = "";
                                if(i3 == -1)
                                {
                                    for(int k3 = k2 + 1; k3 < s4.length(); k3++)
                                    {
                                        i1++;
                                        s3 = s5.substring(k3, k3 + 1);
                                        abyte0[i1][0] = convertToByte(s3);
                                    }

                                } else
                                {
                                    for(; i3 != -1; i3 = s4.indexOf("-", i3 + 1))
                                    {
                                        if(i3 - k2 > 1)
                                        {
                                            for(int l3 = k2 + 1; l3 < i3; l3++)
                                            {
                                                i1++;
                                                s3 = s5.substring(l3, l3 + 1);
                                                abyte0[i1][0] = convertToByte(s3);
                                            }

                                            s3 = s3 + s5.substring(i3, i3 + 1);
                                            abyte0[i1] = convertToByte(s3.toCharArray());
                                        } else
                                        {
                                            s3 = s3 + s5.substring(i3, i3 + 1);
                                            abyte0[i1] = convertToByte(s3.toCharArray());
                                        }
                                        k2 = i3;
                                    }

                                    for(int i4 = k2 + 1; i4 < s4.length(); i4++)
                                    {
                                        i1++;
                                        s3 = s5.substring(i4, i4 + 1);
                                        abyte0[i1][0] = convertToByte(s3);
                                    }

                                }
                            }
                        }

                    if(flag)
                    {
                        int j1 = 0;
                        for(int l1 = 1; l1 < abyte0.length; l1++)
                            if(abyte0[l1] != null)
                                j1 += abyte0[l1].length;

                        byte abyte1[] = new byte[j1 + 1];
                        int i2 = 0;
                        for(int l2 = 0; l2 < abyte0.length; l2++)
                            if(abyte0[l2] != null)
                                if(abyte0[l2].length == 1)
                                {
                                    abyte1[i2] = abyte0[l2][0];
                                    i2++;
                                } else
                                {
                                    for(int j3 = 0; j3 < abyte0[l2].length; j3++)
                                    {
                                        abyte1[i2] = abyte0[l2][j3];
                                        i2++;
                                    }

                                }

                        Object obj = hashtable.get(new Integer(j1));
                        if(obj != null)
                        {
                            Vector vector2 = (Vector)obj;
                            boolean flag2 = false;
                            for(int j4 = 0; j4 < vector2.size(); j4 += 2)
                            {
                                int k4 = ((Integer)vector2.elementAt(j4)).intValue();
                                byte abyte2[] = (byte[])vector2.elementAt(j4 + 1);
                                if(Arrays.equals(abyte2, abyte1))
                                {
                                    String s6 = ((String)vector1.elementAt(k4)).trim() + ", " + s2;
                                    vector1.set(k4, s6);
                                    flag2 = true;
                                }
                            }

                            if(!flag2)
                            {
                                vector.add(abyte0);
                                vector1.add(s2);
                                vector2.add(new Integer(vector.size() - 1));
                                vector2.add(abyte1);
                                hashtable.put(vector2, new Integer(j1));
                            }
                        } else
                        {
                            vector.add(abyte0);
                            vector1.add(s2);
                            Vector vector3 = new Vector();
                            vector3.add(new Integer(vector.size() - 1));
                            vector3.add(abyte1);
                            hashtable.put(vector3, new Integer(j1));
                        }
                    }
                }
                if(s != null && s.indexOf(">") == 0)
                    s2 = s.substring(1);
            }

        }
        bufferedreader.close();
        matrix = new byte[vector.size()][][];
        nameMatrix = new String[vector.size()];
        for(int l = 0; l < vector.size(); l++)
        {
            matrix[l] = (byte[][])vector.elementAt(l);
            nameMatrix[l] = (String)vector1.elementAt(l);
        }

        child.destroy();
    }

    public byte convertToByte(String s)
    {
        return convertToByte(s.charAt(0));
    }

    public byte[] convertToByte(char ac[])
    {
        byte abyte0[] = new byte[ac.length];
        for(int i = 0; i < ac.length; i++)
            abyte0[i] = convertToByte(ac[i]);

        return abyte0;
    }

    public byte convertToByte(char c)
    {
        byte byte0;
        switch(c)
        {
        case 45: // '-'
            byte0 = 0;
            break;

        case 65: // 'A'
            byte0 = 1;
            break;

        case 84: // 'T'
            byte0 = 2;
            break;

        case 67: // 'C'
            byte0 = 3;
            break;

        case 71: // 'G'
            byte0 = 4;
            break;

        default:
            byte0 = 5;
            break;
        }
        return byte0;
    }

    private String db;
    private String prog;
    private String option;
    private Process child;
    public byte matrix[][][];
    public String nameMatrix[];
}
