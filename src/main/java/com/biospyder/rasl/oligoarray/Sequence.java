package com.biospyder.rasl.oligoarray;



public class Sequence
{

	
    public Sequence(String s)
    {
        if(s.indexOf(">") == 0)
        {
            name = s.substring(s.indexOf(">") + 1, s.indexOf("\n"));
            s = s.substring(s.indexOf("\n") + 1);
        } else
        {
            name = null;
        }
        l = s.length();
        seqArray = s.toCharArray();
        basesWereCounted = false;
        isThermodynamicDataSet = false;
    }

    public Sequence(String s, String s1)
    {
        this(">" + s + "\n" + s1);
    }

    public Sequence(int i)
    {
        name = "random";
        l = i;
        String s = "";
        for(int j = 0; j < l; j++)
        {
            double d = Math.random();
            if(d < 0.25D)
                s = s.concat("G");
            else
            if(d < 0.5D)
                s = s.concat("A");
            else
            if(d < 0.75D)
                s = s.concat("T");
            else
                s = s.concat("C");
        }

        seqArray = s.toCharArray();
        basesWereCounted = false;
        isThermodynamicDataSet = false;
    }

    public String name()
    {
        return name;
    }

    public String complement()
    {
        char ac[] = new char[l];
        for(int i = 0; i < l; i++)
        {
            char c = seqArray[i];
            switch(c)
            {
            case 65: // 'A'
                ac[i] = 'T';
                break;

            case 84: // 'T'
                ac[i] = 'A';
                break;

            case 67: // 'C'
                ac[i] = 'G';
                break;

            case 71: // 'G'
                ac[i] = 'C';
                break;

            case 97: // 'a'
                ac[i] = 't';
                break;

            case 116: // 't'
                ac[i] = 'a';
                break;

            case 99: // 'c'
                ac[i] = 'g';
                break;

            case 103: // 'g'
                ac[i] = 'c';
                break;

            case 110: // 'n'
                ac[i] = 'n';
                break;

            default:
                ac[i] = 'N';
                break;
            }
        }

        return new String(ac);
    }

    public int length()
    {
        return l;
    }

    public String reverse()
    {
        char ac[] = new char[l];
        for(int i = 0; i < l; i++)
            ac[l - 1 - i] = seqArray[i];

        return new String(ac);
    }

    public String reverseComplement()
    {
        char ac[] = new char[l];
        for(int i = 0; i < l; i++)
        {
            char c = seqArray[i];
            switch(c)
            {
            case 65: // 'A'
                ac[l - 1 - i] = 'T';
                break;

            case 84: // 'T'
                ac[l - 1 - i] = 'A';
                break;

            case 67: // 'C'
                ac[l - 1 - i] = 'G';
                break;

            case 71: // 'G'
                ac[l - 1 - i] = 'C';
                break;

            case 97: // 'a'
                ac[l - 1 - i] = 't';
                break;

            case 116: // 't'
                ac[l - 1 - i] = 'a';
                break;

            case 99: // 'c'
                ac[l - 1 - i] = 'g';
                break;

            case 103: // 'g'
                ac[l - 1 - i] = 'c';
                break;

            case 110: // 'n'
                ac[l - 1 - i] = 'n';
                break;

            default:
                ac[l - 1 - i] = 'N';
                break;
            }
        }

        return new String(ac);
    }

    public String sequence()
    {
        return new String(seqArray);
    }

    public String sequence(int i, int j)
    {
        return new String(seqArray, i, j - i);
    }

    public String toString()
    {
        return ">" + name + "\n" + new String(seqArray);
    }

    public boolean contains(Sequence sequence1)
    {
        boolean flag;
        if(sequence().indexOf(sequence1.sequence()) != -1)
            flag = true;
        else
            flag = false;
        return flag;
    }

    public boolean containsDiRepeat(int i)
    {
        boolean flag = false;
        for(int j = 0; (j <= l - i) & (!flag);)
        {
            for(int k = 1; k < i / 2; k++)
            {
                if((seqArray[j] == seqArray[j + 2 * k]) & (seqArray[j + 1] == seqArray[j + 1 + 2 * k]))
                {
                    flag = true;
                    continue;
                }
                flag = false;
                j = j + 1 + 2 * (k - 1);
                break;
            }

        }

        return flag;
    }

    public boolean containsTriRepeat(int i)
    {
        boolean flag = false;
        for(int j = 0; (j <= l - i) & (!flag);)
        {
            for(int k = 1; k < i / 3; k++)
            {
                if((seqArray[j] == seqArray[j + 3 * k]) & (seqArray[j + 1] == seqArray[j + 1 + 3 * k]) & (seqArray[j + 2] == seqArray[j + 2 + 3 * k]))
                {
                    flag = true;
                    continue;
                }
                flag = false;
                j = j + 1 + 3 * (k - 1);
                break;
            }

        }

        return flag;
    }

    public int numberGC()
    {
        if(!basesWereCounted)
            countBases();
        return nbG + nbC;
    }

    public double percentGC()
    {
        if(!basesWereCounted)
            countBases();
        return (double)((100 * (nbG + nbC)) / l);
    }

    public double percentAT()
    {
        if(!basesWereCounted)
            countBases();
        return (double)((100 * (nbA + nbT)) / l);
    }

    public double percentC()
    {
        if(!basesWereCounted)
            countBases();
        return (double)((100 * nbC) / l);
    }

    public double percentG()
    {
        if(!basesWereCounted)
            countBases();
        return (double)((100 * nbG) / l);
    }

    public double percentA()
    {
        if(!basesWereCounted)
            countBases();
        return (double)((100 * nbA) / l);
    }

    public double percentT()
    {
        if(!basesWereCounted)
            countBases();
        return (double)((100 * nbT) / l);
    }

    public double tm(double d, double d1)
    {
        return ((81.5D - 16.600000000000001D * (Math.log(d) / Math.log(10D))) + 0.40999999999999998D * percentGC()) - 0.63D * d1 - (double)(600 / l);
    }

    public double tmNN(double d, int i)
    {
        if(!isThermodynamicDataSet)
            setThermodynamicData();
        return (1000D * dH) / (dS + 1.9872000000000001D * Math.log(d / (double)i)) - 273.14999999999998D;
    }

    public double tmNN(double d)
    {
        if(!isThermodynamicDataSet)
            setThermodynamicData();
        return (1000D * dH) / (dS + 1.9872000000000001D * Math.log(d / 4D)) - 273.14999999999998D;
    }

    public double deltaG(double d)
    {
        if(!isThermodynamicDataSet)
            setThermodynamicData();
        return dH - (d * dS) / 1000D;
    }

    public double deltaG37()
    {
        if(!isThermodynamicDataSet)
            setThermodynamicData();
        return dG;
    }

    public double deltaH()
    {
        if(!isThermodynamicDataSet)
            setThermodynamicData();
        return dH;
    }

    public double deltaS()
    {
        if(!isThermodynamicDataSet)
            setThermodynamicData();
        return dS;
    }

    public char[] seqArray()
    {
        return seqArray;
    }

    private boolean setThermodynamicData()
    {
        double d = 0.0D;
        double d1 = 0.0D;
        double d2 = 0.0D;
        for(int i = 0; i < l - 1; i++)
        {
            char c = seqArray[i];
            char c2 = seqArray[i + 1];
            if((c == 'A') | (c == 'a'))
            {
                if((c2 == 'A') | (c2 == 'a'))
                {
                    d += -1D;
                    d1 += -7.9000000000000004D;
                    d2 += -22.199999999999999D;
                } else
                if((c2 == 'T') | (c2 == 't'))
                {
                    d += -0.88D;
                    d1 += -7.2000000000000002D;
                    d2 += -20.399999999999999D;
                } else
                if((c2 == 'C') | (c2 == 'c'))
                {
                    d += -1.4399999999999999D;
                    d1 += -8.4000000000000004D;
                    d2 += -22.399999999999999D;
                } else
                if((c2 == 'G') | (c2 == 'g'))
                {
                    d += -1.28D;
                    d1 += -7.7999999999999998D;
                    d2 += -21D;
                }
            } else
            if((c == 'T') | (c == 't'))
            {
                if((c2 == 'A') | (c2 == 'a'))
                {
                    d += -0.57999999999999996D;
                    d1 += -7.2000000000000002D;
                    d2 += -21.300000000000001D;
                } else
                if((c2 == 'T') | (c2 == 't'))
                {
                    d += -1D;
                    d1 += -7.9000000000000004D;
                    d2 += -22.199999999999999D;
                } else
                if((c2 == 'C') | (c2 == 'c'))
                {
                    d += -1.3D;
                    d1 += -8.1999999999999993D;
                    d2 += -22.199999999999999D;
                } else
                if((c2 == 'G') | (c2 == 'g'))
                {
                    d += -1.45D;
                    d1 += -8.5D;
                    d2 += -22.699999999999999D;
                }
            } else
            if((c == 'C') | (c == 'c'))
            {
                if((c2 == 'A') | (c2 == 'a'))
                {
                    d += -1.45D;
                    d1 += -8.5D;
                    d2 += -22.699999999999999D;
                } else
                if((c2 == 'T') | (c2 == 't'))
                {
                    d += -1.28D;
                    d1 += -7.7999999999999998D;
                    d2 += -21D;
                } else
                if((c2 == 'C') | (c2 == 'c'))
                {
                    d += -1.8400000000000001D;
                    d1 += -8D;
                    d2 += -19.899999999999999D;
                } else
                if((c2 == 'G') | (c2 == 'g'))
                {
                    d += -2.1699999999999999D;
                    d1 += -10.6D;
                    d2 += -27.199999999999999D;
                }
            } else
            if((c == 'G') | (c == 'g'))
                if((c2 == 'A') | (c2 == 'a'))
                {
                    d += -1.3D;
                    d1 += -8.1999999999999993D;
                    d2 += -22.199999999999999D;
                } else
                if((c2 == 'T') | (c2 == 't'))
                {
                    d += -1.4399999999999999D;
                    d1 += -8.4000000000000004D;
                    d2 += -22.399999999999999D;
                } else
                if((c2 == 'C') | (c2 == 'c'))
                {
                    d += -2.2400000000000002D;
                    d1 += -9.8000000000000007D;
                    d2 += -24.399999999999999D;
                } else
                if((c2 == 'G') | (c2 == 'g'))
                {
                    d += -1.8400000000000001D;
                    d1 += -8D;
                    d2 += -19.899999999999999D;
                }
        }

        char c1 = seqArray[0];
        if((c1 == 'A') | (c1 == 'a') || (c1 == 'T') | (c1 == 't'))
        {
            d += 1.03D;
            d1 += 2.2999999999999998D;
            d2 += 4.0999999999999996D;
        } else
        if((c1 == 'C') | (c1 == 'c') || (c1 == 'G') | (c1 == 'g'))
        {
            d += 0.97999999999999998D;
            d1 += 0.10000000000000001D;
            d2 += -2.7999999999999998D;
        }
        c1 = seqArray[l - 1];
        if((c1 == 'A') | (c1 == 'a') || (c1 == 'T') | (c1 == 't'))
        {
            d += 1.03D;
            d1 += 2.2999999999999998D;
            d2 += 4.0999999999999996D;
        } else
        if((c1 == 'C') | (c1 == 'c') || (c1 == 'G') | (c1 == 'g'))
        {
            d += 0.97999999999999998D;
            d1 += 0.10000000000000001D;
            d2 += -2.7999999999999998D;
        }
        dG37 = d;
        dS = d2;
        dH = d1;
        isThermodynamicDataSet = true;
        return isThermodynamicDataSet;
    }

    private boolean countBases()
    {
        int i = 0;
        int j = 0;
        int k = 0;
        int i1 = 0;
        for(int j1 = 0; j1 < l; j1++)
        {
            char c = seqArray[j1];
            switch(c)
            {
            case 67: // 'C'
                j++;
                break;

            case 71: // 'G'
                i++;
                break;

            case 99: // 'c'
                j++;
                break;

            case 103: // 'g'
                i++;
                break;

            case 65: // 'A'
                k++;
                break;

            case 84: // 'T'
                i1++;
                break;

            case 97: // 'a'
                k++;
                break;

            case 116: // 't'
                i1++;
                break;
            }
        }

        nbA = k;
        nbT = i1;
        nbC = j;
        nbG = i;
        basesWereCounted = true;
        return basesWereCounted;
    }

    private String name;
    private int l;
    private char seqArray[];
    private double dG37;
    private double dS;
    private double dH;
    private double dG;
    private int nbA;
    private int nbT;
    private int nbC;
    private int nbG;
    private boolean basesWereCounted;
    private boolean isThermodynamicDataSet;
    public static final double dGAA = -1D;
    public static final double dHAA = -7.9000000000000004D;
    public static final double dSAA = -22.199999999999999D;
    public static final double dGAT = -0.88D;
    public static final double dHAT = -7.2000000000000002D;
    public static final double dSAT = -20.399999999999999D;
    public static final double dGAC = -1.4399999999999999D;
    public static final double dHAC = -8.4000000000000004D;
    public static final double dSAC = -22.399999999999999D;
    public static final double dGAG = -1.28D;
    public static final double dHAG = -7.7999999999999998D;
    public static final double dSAG = -21D;
    public static final double dGTA = -0.57999999999999996D;
    public static final double dHTA = -7.2000000000000002D;
    public static final double dSTA = -21.300000000000001D;
    public static final double dGTT = -1D;
    public static final double dHTT = -7.9000000000000004D;
    public static final double dSTT = -22.199999999999999D;
    public static final double dGTC = -1.3D;
    public static final double dHTC = -8.1999999999999993D;
    public static final double dSTC = -22.199999999999999D;
    public static final double dGTG = -1.45D;
    public static final double dHTG = -8.5D;
    public static final double dSTG = -22.699999999999999D;
    public static final double dGCA = -1.45D;
    public static final double dHCA = -8.5D;
    public static final double dSCA = -22.699999999999999D;
    public static final double dGCT = -1.28D;
    public static final double dHCT = -7.7999999999999998D;
    public static final double dSCT = -21D;
    public static final double dGCC = -1.8400000000000001D;
    public static final double dHCC = -8D;
    public static final double dSCC = -19.899999999999999D;
    public static final double dGCG = -2.1699999999999999D;
    public static final double dHCG = -10.6D;
    public static final double dSCG = -27.199999999999999D;
    public static final double dGGA = -1.3D;
    public static final double dHGA = -8.1999999999999993D;
    public static final double dSGA = -22.199999999999999D;
    public static final double dGGT = -1.4399999999999999D;
    public static final double dHGT = -8.4000000000000004D;
    public static final double dSGT = -22.399999999999999D;
    public static final double dGGC = -2.2400000000000002D;
    public static final double dHGC = -9.8000000000000007D;
    public static final double dSGC = -24.399999999999999D;
    public static final double dGGG = -1.8400000000000001D;
    public static final double dHGG = -8D;
    public static final double dSGG = -19.899999999999999D;
    public static final double dGInitGC = 0.97999999999999998D;
    public static final double dHInitGC = 0.10000000000000001D;
    public static final double dSInitGC = -2.7999999999999998D;
    public static final double dGInitAT = 1.03D;
    public static final double dHInitAT = 2.2999999999999998D;
    public static final double dSInitAT = 4.0999999999999996D;
}
