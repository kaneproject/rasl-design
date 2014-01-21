package com.biospyder.rasl.pojo;

import gnu.getopt.Getopt;

import java.io.BufferedReader;
import java.io.File;
import java.io.PrintWriter;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.biojava3.core.sequence.DNASequence;

import com.biospyder.rasl.common.Utilities;
import com.google.common.base.Preconditions;



/*
 * A Java POJO representing the collection of RASL runtime parameters
 */

public class RaslRuntime {
	private static Logger logger = LogManager
			.getLogger(RaslRuntime.class);
	private Integer lengthMin;
	private Integer lengthMax;
	private Integer distance;
	private Integer tmMin;
	private Integer tmMax;
	private Integer deltaTm;
	private Integer crossHyb;
	private Integer length;
	private Integer processors;
	private Integer begin;
	private Integer end;
	private Integer nbAlignment;
	private Integer maxNbOligo;
	private Integer originalLength;
	private Integer hsp;
	private Integer betweenOligo;
	private String listProhibited;
	private String seqFile;
	private String blastDB;
	private String saveAs;
	private String rejectFile;
	private String log;
	private String orf;
	private String seqName;
	private String sequence;
	private BufferedReader readBuff;
	public PrintWriter logFile;
	public PrintWriter skipped;
	public PrintWriter oligoDb;
	private DNASequence seq;
	private DNASequence oligoSequence;
	private Double concDna;
	private Double temperature;
	private Double temperatureK;
	private Double dGStruct;
	private Double minGC;
	private Double maxGC;
	public Double lambda;
	public Double K;
	public Double blastDbLength;
	public Double nbSeqBlastDb;
	private String[][] alignment;
	private Double[][] thermoData;
	public Integer[] hsp_table;
	private List<Integer> usedPos;
	public float[][] dataG;
	public float[][] dataH;
	private File workDirectory;
	private File tempDirectory;
	private final String version = "1.0.0";
	private boolean validRuntime;
	
	// public constructor using command line arguments
	
	

	private RaslRuntime() {
		
			// TODO set default values 
			processors = 1;
			maxNbOligo = 1;
			lengthMin = 45;
			lengthMax = 47;
			betweenOligo = (int) (1.5D * (lengthMax + lengthMin) / 2.0D);
			distance = 1500;
			tmMin = 85;
			tmMax = 90;
			temperature = 65.0D;
			crossHyb = 65;
			minGC = 40.0D;
			maxGC = 60.0D;
			concDna = 1.0E-06D;
			listProhibited = "";
			seqFile = "";
			blastDB = "";
			saveAs = "oligo.txt";
			rejectFile = "rejected.fas";
			log = "OligoArray.log";
			logFile = new PrintWriter(System.out);
		    validRuntime = true;

	}
	
	public RaslRuntime(String[] paramArrayOfString) {
		/*
		 * first set default values
		 */
		this();

		Getopt localGetopt = new Getopt("testprog", paramArrayOfString,
				"-:i:d:o:r:R:n:l:L:D:t:T:s:x:p:P:m:N:g:h");
		localGetopt.setOpterr(false);
		int j;
		while ((j = localGetopt.getopt()) != -1) {
			switch (j) {
			case 1:
				logger.error("There is an error in the command line near or at '"
						+ localGetopt.getOptarg() + "'");
				break;
			case 105:
				seqFile = localGetopt.getOptarg();
				break;
			case 100:
				blastDB = localGetopt.getOptarg();
				break;
			case 111:
				saveAs = localGetopt.getOptarg();
				break;
			case 114:
				rejectFile = localGetopt.getOptarg();
				break;
			case 82:
				log = localGetopt.getOptarg();
				break;
			case 110:
				maxNbOligo = new Integer(localGetopt.getOptarg());
				break;
			case 108:
				lengthMin = new Integer(localGetopt.getOptarg());
				break;
			case 76:
				lengthMax = new Integer(localGetopt.getOptarg());
				break;
			case 68:
				distance = new Integer(localGetopt.getOptarg());
				break;
			case 116:
				tmMin = new Integer(localGetopt.getOptarg());
				break;
			case 84:
				tmMax = new Integer(localGetopt.getOptarg());
				break;
			case 115:
				temperature = new Double(localGetopt.getOptarg());
				break;
			case 120:
				crossHyb = new Integer(localGetopt.getOptarg());
				break;
			case 112:
				minGC = new Double(localGetopt.getOptarg());
				break;
			case 80:
				maxGC = new Double(localGetopt.getOptarg());
				break;
			case 109:
				listProhibited = localGetopt.getOptarg();
				break;
			case 78:
				processors = new Integer(localGetopt.getOptarg());
				break;
			case 103:
				betweenOligo = new Integer(localGetopt.getOptarg());
				break;
			case 104:
				Utilities.displayHelp();
				this.setValidRuntime(false);
				break;
			case 58:
				System.out.println("You need an argument for option "
						+ (char) localGetopt.getOptopt());

				break;
			case 63:
				System.out.println("The option '"
						+ (char) localGetopt.getOptopt() + "' is not valid");
				this.setValidRuntime(false);
			}
		}
		if(this.isValidRuntime() ){
			this.validateRuntimeParameters();
		}

	}
	
	private void validateRuntimeParameters() {

		// TODO move parameter limits and ranges to a properties file
		boolean flag = true;
		if (seqFile.equals("") | blastDB.equals("")) {
			this.setValidRuntime(false);
			System.err
					.println("You should use at least options -i to select the input file and -d to select the Blast database");
		}
		if (processors < 1) {
			this.setValidRuntime(false);
			System.err
					.println("Invalid argument for option -N. The expected value is an integer > 0");
		}
		if (maxNbOligo < 1) {
			this.setValidRuntime(false);
			System.err
					.println("Invalid argument for option -n. The expected value is an integer > 0");
		}
		if ((lengthMin < 15) | (lengthMin > 75)) {
			this.setValidRuntime(false);
			System.err
					.println("Invalid argument for option -l. The expected value is an integer comprised between 15 and 75");
		}
		if ((lengthMax < 15) | (lengthMax > 75)) {
			this.setValidRuntime(false);
			System.err
					.println("Invalid argument for option -L. The expected value is an integer comprised between 15 and 75");
		}
		if (lengthMin > lengthMax) {
			this.setValidRuntime(false);
			System.err
					.println("The argument given for -l should not be bigger than the one for -L");
		}
		if (betweenOligo < 1) {
			this.setValidRuntime(false);
			System.err
					.println("Invalid argument for option -g. The expected value is an integer > 0");
		}
		if ((tmMin < 0) | (tmMin > 100)) {
			this.setValidRuntime(false);
			System.err
					.println("Invalid argument for option -t. The expected value is an integer comprised between 0 and 100");
		}
		if ((tmMax < 0) | (tmMax > 100)) {
			this.setValidRuntime(false);
			System.err
					.println("Invalid argument for option -T. The expected value is an integer comprised between 0 and 100");
		}
		if (tmMin > tmMax) {
			this.setValidRuntime(false);
			System.err
					.println("The argument given for -t should not be bigger than the one for -T");
		}
		if ((temperature < 0.0D) | (temperature > 100D)) {
			this.setValidRuntime(false);
			System.err
					.println("Invalid argument for option -s. The expected value is a real comprised between 0 and 100");
		}
		if ((crossHyb < 0) | (crossHyb > 100)) {
			this.setValidRuntime(false);
			System.err
					.println("Invalid argument for option -x. The expected value is an integer comprised between 0 and 100");
		}
		if ((minGC < 0.0D) | (minGC > 100D)) {
			this.setValidRuntime(false);
			System.err
					.println("Invalid argument for option -p. The expected value is a real comprised between 0 and 100");
		}
		if ((maxGC < 0.0D) | (maxGC > 100D)) {
			this.setValidRuntime(false);
			System.err
					.println("Invalid argument for option -P. The expected value is a real comprised between 0 and 100");
		}
		if (tmMin > tmMax) {
			this.setValidRuntime(false);
			System.err
					.println("The argument given for -p should not be bigger than the one for -P");
		}
	}
	
	public int getHSP(int paramInt) {
		Preconditions.checkArgument(paramInt < hsp_table.length,
				"index to getHSP: " + paramInt + " out of bounds");
		return hsp_table[(paramInt / 100)];

	}

	public Integer getLengthMin() {
		return lengthMin;
	}

	public void setLengthMin(Integer lengthMin) {
		this.lengthMin = lengthMin;
	}

	public Integer getLengthMax() {
		return lengthMax;
	}

	public void setLengthMax(Integer lengthMax) {
		this.lengthMax = lengthMax;
	}

	public Integer getDistance() {
		return distance;
	}

	public void setDistance(Integer distance) {
		this.distance = distance;
	}

	public Integer getTmMin() {
		return tmMin;
	}

	public void setTmMin(Integer tmMin) {
		this.tmMin = tmMin;
	}

	public Integer getTmMax() {
		return tmMax;
	}

	public void setTmMax(Integer tmMax) {
		this.tmMax = tmMax;
	}

	public Integer getDeltaTm() {
		return deltaTm;
	}

	public void setDeltaTm(Integer deltaTm) {
		this.deltaTm = deltaTm;
	}

	public Integer getCrossHyb() {
		return crossHyb;
	}

	public void setCrossHyb(Integer crossHyb) {
		this.crossHyb = crossHyb;
	}

	public Integer getLength() {
		return length;
	}

	public void setLength(Integer length) {
		this.length = length;
	}

	public Integer getProcessors() {
		return processors;
	}

	public void setProcessors(Integer processors) {
		this.processors = processors;
	}

	public Integer getBegin() {
		return begin;
	}

	public void setBegin(Integer begin) {
		this.begin = begin;
	}

	public Integer getEnd() {
		return end;
	}

	public void setEnd(Integer end) {
		this.end = end;
	}

	public Integer getNbAlignment() {
		return nbAlignment;
	}

	public void setNbAlignment(Integer nbAlignment) {
		this.nbAlignment = nbAlignment;
	}

	public Integer getMaxNbOligo() {
		return maxNbOligo;
	}

	public void setMaxNbOligo(Integer maxNbOligo) {
		this.maxNbOligo = maxNbOligo;
	}

	public Integer getOriginalLength() {
		return originalLength;
	}

	public void setOriginalLength(Integer originalLength) {
		this.originalLength = originalLength;
	}

	public Integer getHsp() {
		return hsp;
	}

	public void setHsp(Integer hsp) {
		this.hsp = hsp;
	}

	public Integer getBetweenOligo() {
		return betweenOligo;
	}

	public void setBetweenOligo(Integer betweenOligo) {
		this.betweenOligo = betweenOligo;
	}

	public String getListProhibited() {
		return listProhibited;
	}

	public void setListProhibited(String listProhibited) {
		this.listProhibited = listProhibited;
	}

	public String getSeqFile() {
		return seqFile;
	}

	public void setSeqFile(String seqFile) {
		this.seqFile = seqFile;
	}

	public String getBlastDB() {
		return blastDB;
	}

	public void setBlastDB(String blastDB) {
		this.blastDB = blastDB;
	}

	public String getSaveAs() {
		return saveAs;
	}

	public void setSaveAs(String saveAs) {
		this.saveAs = saveAs;
	}

	public String getRejectFile() {
		return rejectFile;
	}

	public void setRejectFile(String rejectFile) {
		this.rejectFile = rejectFile;
	}

	public String getLog() {
		return log;
	}

	public void setLog(String log) {
		this.log = log;
	}

	public String getOrf() {
		return orf;
	}

	public void setOrf(String orf) {
		this.orf = orf;
	}

	public String getSeqName() {
		return seqName;
	}

	public void setSeqName(String seqName) {
		this.seqName = seqName;
	}

	public String getSequence() {
		return sequence;
	}

	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

	public BufferedReader getReadBuff() {
		return readBuff;
	}

	public void setReadBuff(BufferedReader readBuff) {
		this.readBuff = readBuff;
	}

	public PrintWriter getLogFile() {
		return logFile;
	}

	public void setLogFile(PrintWriter logFile) {
		this.logFile = logFile;
	}

	public PrintWriter getSkipped() {
		return skipped;
	}

	public void setSkipped(PrintWriter skipped) {
		this.skipped = skipped;
	}

	public PrintWriter getOligoDb() {
		return oligoDb;
	}

	public void setOligoDb(PrintWriter oligoDb) {
		this.oligoDb = oligoDb;
	}

	public DNASequence getSeq() {
		return seq;
	}

	public void setSeq(DNASequence seq) {
		this.seq = seq;
	}

	public DNASequence getOligoSequence() {
		return oligoSequence;
	}

	public void setOligoSequence(DNASequence oligoSequence) {
		this.oligoSequence = oligoSequence;
	}

	public Double getConcDna() {
		return concDna;
	}

	public void setConcDna(Double concDna) {
		this.concDna = concDna;
	}

	public Double getTemperature() {
		return temperature;
	}

	public void setTemperature(Double temperature) {
		this.temperature = temperature;
	}

	public Double getTemperatureK() {
		return temperatureK;
	}

	public void setTemperatureK(Double temperatureK) {
		this.temperatureK = temperatureK;
	}

	public Double getdGStruct() {
		return dGStruct;
	}

	public void setdGStruct(Double dGStruct) {
		this.dGStruct = dGStruct;
	}

	public Double getMinGC() {
		return minGC;
	}

	public void setMinGC(Double minGC) {
		this.minGC = minGC;
	}

	public Double getMaxGC() {
		return maxGC;
	}

	public void setMaxGC(Double maxGC) {
		this.maxGC = maxGC;
	}

	public Double getLambda() {
		return lambda;
	}

	public void setLambda(Double lambda) {
		this.lambda = lambda;
	}

	public Double getK() {
		return K;
	}

	public void setK(Double k) {
		K = k;
	}

	public Double getBlastDbLength() {
		return blastDbLength;
	}

	public void setBlastDbLength(Double blastDbLength) {
		this.blastDbLength = blastDbLength;
	}

	public Double getNbSeqBlastDb() {
		return nbSeqBlastDb;
	}

	public void setNbSeqBlastDb(Double nbSeqBlastDb) {
		this.nbSeqBlastDb = nbSeqBlastDb;
	}

	public String[][] getAlignment() {
		return alignment;
	}

	public void setAlignment(String[][] alignment) {
		this.alignment = alignment;
	}

	public Double[][] getThermoData() {
		return thermoData;
	}

	public void setThermoData(Double[][] thermoData) {
		this.thermoData = thermoData;
	}

	public Integer[] getHsp_table() {
		return hsp_table;
	}

	public void setHsp_table(Integer[] hsp_table) {
		this.hsp_table = hsp_table;
	}
	
	public void setHsp_table(int[] hsp_table) {
		this.setHsp_table( new Integer[hsp_table.length]);
		int i = 0;
		for (int value : hsp_table) {
		    this.getHsp_table()[i++] = Integer.valueOf(value);
		}
	}

	public List<Integer> getUsedPos() {
		return usedPos;
	}

	public void setUsedPos(List<Integer> usedPos) {
		this.usedPos = usedPos;
	}

	public float[][] getDataG() {
		return dataG;
	}

	public void setDataG(float[][] dataG) {
		this.dataG = dataG;
	}

	public float[][] getDataH() {
		return dataH;
	}

	public void setDataH(float[][] dataH) {
		this.dataH = dataH;
	}

	public File getWorkDirectory() {
		return workDirectory;
	}

	public void setWorkDirectory(File workDirectory) {
		this.workDirectory = workDirectory;
	}

	public File getTempDirectory() {
		return tempDirectory;
	}

	public void setTempDirectory(File tempDirectory) {
		this.tempDirectory = tempDirectory;
	}

	public String getVersion() {
		return version;
	}

	public boolean isValidRuntime() {
		return validRuntime;
	}

	public void setValidRuntime(boolean validRuntime) {
		this.validRuntime = validRuntime;
	}

	public String displayParameters() {
		StringBuffer sb = new StringBuffer() ;
		sb.append("\n\n\t***\tOligoArray 2.1.3\t***");
        sb.append("\n\nOligoArray 2.1.3 will start to process sequences from the file " + seqFile + " using the following parameters :");
        sb.append("\nBlast database: '" + blastDB + "'");
        sb.append("\nOligo data will be saved in: '" + saveAs + "'");
        sb.append("\nSequence without oligo will be saved in: '" + rejectFile + "'");
        sb.append("\nThe log file will be: '" + log + "'");
        sb.append("\nMaximum number of oligo to design per input sequence: '" + maxNbOligo + "'");
        sb.append("\nSize range: '" + lengthMin + "' to '" + lengthMax + "'");
        sb.append("\nMaximum distance between the 5' end of the oligo and the 3' end of the input sequence: '" + distance + "'");
        sb.append("\nMinimum distance between the 5' ends of two adjacent oligos: '" + betweenOligo + "'");
        sb.append("\nTm range: '" + tmMin + "' to '" + tmMax + "'");
        sb.append("\nGC range: '" + minGC + "' to '" + maxGC + "'");
        sb.append("\nThreshold to reject secondary structures: '" + temperature + "'");
        sb.append("\nThreshold to start to consider cross-hybridizations: '" + crossHyb + "'");
        sb.append("\nSequence to avoid in the oligo: '" + listProhibited + "'");
        sb.append("\nNumber of sequence to run in parallel: '" + processors + "'");
        if(this.isValidRuntime()){
        	sb.append("\nThese parameter values PASSED validation tests");
        } else {
        	sb.append("\nThese parameter values FAILED validation tests");
        }
        sb.append("\n");
		
		return sb.toString();
	}

}
