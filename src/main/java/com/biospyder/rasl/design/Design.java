package com.biospyder.rasl.design;

import java.io.*;
import java.util.*;

import com.biospyder.rasl.common.CommonValues;
import com.biospyder.rasl.design.service.SequenceQueueService;
import com.biospyder.rasl.oligoarray.Blast;
import com.biospyder.rasl.oligoarray.Sequence;
import com.biospyder.rasl.pojo.RaslRuntime;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class Design extends Thread {

	

	Design(RaslRuntime runtime, String s2)
	// Design(SeqDispenser seqdispenser, String s, int i, int j, int k, int l,
	// int i1,
	// int j1, int k1, double d, int l1, double d1,
	// // double d2, String s1, double d3, float af[][], float af1[][],
	// String s2)
	{

		this.runtime = runtime;
		blastDB = runtime.getBlastDB();
		maxNbOligo = runtime.getMaxNbOligo();
		lengthMin = runtime.getLengthMin();
		lengthMax = runtime.getLengthMin();
		distance = runtime.getDistance();
		betweenOligo = runtime.getBetweenOligo();
		tmMin = runtime.getTmMin();
		tmMax = runtime.getTmMax();
		temperature = runtime.getTemperature();
		temperatureK = temperature + 273.14999999999998D;
		crossHyb = runtime.getCrossHyb();
		minGC = runtime.getMinGC();
		maxGC = runtime.getMaxGC();
		listProhibited = runtime.getListProhibited();
		concDna = runtime.getConcDna();
		Design _tmp = this;
		dataG = runtime.getDataG();

		dataH = runtime.getDataH();
		currentDir = s2;
		directory = new File(currentDir);
		if (directory.exists()) {
			File afile[] = directory.listFiles();
			for (int i2 = 0; i2 < afile.length; i2++)
				afile[i2].delete();

		} else {
			directory.mkdir();
		}
		try {
			logFile = new PrintWriter(new FileOutputStream(new File(s2,
					"OligoArray.log")));
			skipped = new PrintWriter(new FileOutputStream(new File(s2,
					"rejected.fas")));
			oligoDb = new PrintWriter(new FileOutputStream(new File(s2,
					"oligos.txt")));
		} catch (Exception exception) {
			System.err.println(exception);
		}
	}

	public void run() {
		try {
			while (true) {

				Sequence sequence1 = SequenceQueueService.INSTANCE
						.getSequence();
				seqName = sequence1.name();

				if (!seqName.equalsIgnoreCase(CommonValues.INSTANCE.STOP_SIGNAL)) {
					System.out.println("Start " + seqName);
					logFile.println("Start " + seqName);
					logFile.flush();
					originalLength = sequence1.length();
					if (originalLength >= distance) {
						sequence = sequence1.sequence(
								originalLength - distance, originalLength);
						deletedLength = originalLength - distance;
					} else {
						sequence = sequence1.sequence();
						deletedLength = 0;
					}
					seqLength = sequence.length();
					sequence = maskSequence(sequence, listProhibited);
					String s = ">" + seqName + "\n" + sequence;
					try {
						if (!analizeSeq(s)) {
							skipped.println(s);
							skipped.flush();
							logFile.println("No oligo for sequence " + seqName);
						}
					} catch (OutOfMemoryError outofmemoryerror) {
						logFile.println("For sequence " + seqName
								+ ", the following exception was caugth: "
								+ outofmemoryerror);
						logFile.flush();
						System.err
								.println("The Blast crashed because there is not enought memory allowed to the application ("
										+ outofmemoryerror
										+ ").\nYou may want to allow more memory to java by using the java option -Xmx256m (will allow 256 M RAM)");
					} catch (Exception exception1) {
						logFile.println("For sequence " + seqName
								+ ", the following exception was caugth: "
								+ exception1);
						logFile.flush();
						System.err
								.println("The process crashed due to an error ("
										+ exception1 + ").");
						exception1.printStackTrace();
					}
					logFile.flush();
					skipped.flush();
					oligoDb.flush();
				} else {
					logFile.println( " Sequence queue is empty Thread: " + Thread.currentThread().getId() +" is terminating.");
					return;
				}
			}
			

		} catch (Exception exception) {
			logFile.println(exception);
			System.err.println("**" + exception);
			logFile.close();
			skipped.close();
			oligoDb.close();
		} finally {

			logFile.close();
			skipped.close();
			oligoDb.close();
		}
	}

	private boolean analizeSeq(String s) throws IOException {
		alignment = new String[1][1];
		thermoData = new double[1][1];
		Map<Integer, double[][]> map00 = Maps.newHashMap();
		Map<Integer, List<Integer>> map01 = Maps.newHashMap();
		Map<Integer, String> map02 = Maps.newHashMap();

		Map<Integer, String> map03 = Maps.newHashMap();
		boolean flag = false;
		hsp = runtime.getHSP(seqLength);

		expected = runtime.getK()
				* (runtime.getBlastDbLength() - runtime.getNbSeqBlastDb()
						* (double) hsp) * (double) (seqLength - hsp)
				* Math.exp(-13D * runtime.getLambda()) * 1.1000000000000001D;
		logFile.print("Running Blast (-e " + expected + ")... ");
		logFile.flush();
		Blast blast = new Blast(blastDB, "blastn",
				"-S 1 -F F -W 7 -v 5 -b 10000 -e " + expected);
		BufferedReader bufferedreader = blast.blast(s);
		alignment = blast.alignmentCompactMatrix(bufferedreader, 13);
		nbAlignment = alignment.length;
		logFile.println("DONE");
		logFile.flush();
		begin = seqLength - lengthMin;
		end = seqLength;
		oligoSequence = new Sequence(sequence.substring(begin, end));
		int i = lengthMin;
		int j = 0;
		usedPos = Lists.newArrayList();
		while ((begin > 0) & (seqLength - begin < distance) & (j < maxNbOligo)) {
			double d = oligoSequence.percentGC();
			double d1 = oligoSequence.tmNN(concDna);
			if (containsProhibited(oligoSequence, "N")) {
				logFile.println(seqName + "\t" + (begin + 1)
						+ "\trejected due to prohibited sequences");
				end = end - 1;
				begin = begin - 1;
			} else if (oligoSequence.containsDiRepeat(10)
					|| oligoSequence.containsTriRepeat(10)) {
				logFile.println(seqName + "\t" + (begin + 1)
						+ "\trejected due to repeated sequences");
				end = end - 1;
				begin = begin - 1;
			} else if (maxGC < d) {
				logFile.println(seqName + "\t" + (begin + 1)
						+ "\trejected due to high percent of GC: " + d);
				end = end - 1;
				begin = begin - 1;
			} else if (minGC > d) {
				logFile.println(seqName + "\t" + (begin + 1)
						+ "\trejected due to low percent of GC: " + d);
				end = end - 1;
				begin = begin - 1;
			} else if (oligoSequence.tmNN(concDna) < (double) tmMin) {
				if (i >= lengthMax) {
					logFile.println(seqName + "\t" + (begin + 1)
							+ "\trejected due to low Tm: "
							+ oligoSequence.tmNN(concDna));
					end--;
					begin--;
				} else {
					begin--;
				}
			} else if (oligoSequence.tmNN(concDna) >= (double) tmMax) {
				if (i > lengthMin) {
					end--;
				} else {
					logFile.println(seqName + "\t" + (begin + 1)
							+ "\trejected due to high Tm: "
							+ oligoSequence.tmNN(concDna));
					end--;
					begin--;
				}
			} else if (i < lengthMin)
				begin--;
			else if (i > lengthMax) {
				end--;
			} else {
				thermoData = new double[nbAlignment][4];
				logFile.print("Updating thermodata... ");
				logFile.flush();
				updateThermoData();
				logFile.println("DONE");
				logFile.print("Folding " + oligoSequence.sequence() + "... ");
				logFile.flush();
				dGStruct = dGStructure(oligoSequence.toString(), temperature);
				logFile.println("DONE");
				logFile.flush();
				if (0.0D <= dGStruct) {
					logFile.print("Testing specificity... ");
					int l = 0;
					for (int j1 = 0; j1 < thermoData.length; j1++)
						if (thermoData[j1][3] >= (double) crossHyb)
							l++;

					logFile.println("DONE");
					if (l == 1) {
						formatOligo(begin, thermoData,
								oligoSequence.sequence(),
								(new Double(dGStruct)).toString());
						logFile.println(seqName + "\t" + (begin + 1)
								+ "\tOligo selected");
						usedPos.add(new Integer(begin));
						flag = true;
						j++;
						end = end - betweenOligo;
						begin = begin - betweenOligo;
					} else if (!flag) {
						map02.put(new Integer(begin), oligoSequence.sequence());
						map00.put(new Integer(begin), thermoData);
						map03.put(new Integer(begin),
								(new Double(dGStruct)).toString());
						List<Integer> vector1;
						if (map01.containsKey(new Integer(l)))
							vector1 =  map01.get(new Integer(l));
						else
							vector1 = Lists.newArrayList();
						vector1.add(new Integer(begin));
						map01.put(new Integer(l), vector1);
						logFile.println(seqName + "\t" + (begin + 1)
								+ "\tNon specific oligo ignored at this time");
						end = end - 1;
						begin = begin - 1;
					} else {
						logFile.println(seqName + "\t" + (begin + 1)
								+ "\tNon specific oligo rejected");
						end = end - 1;
						begin = begin - 1;
					}
				} else {
					logFile.println(seqName
							+ "\t"
							+ (begin + 1)
							+ "\trejected due to a secondary structure (deltaG = "
							+ dGStruct + " @ " + temperature + " degrees)");
					end = end - 1;
					begin = begin - 1;
				}
				logFile.flush();
			}
			if (!((begin > 0) & (j < maxNbOligo)))
				break;
			oligoSequence = new Sequence(">" + seqName + begin + "\n"
					+ sequence.substring(begin, end));
			i = end - begin;
		}
		if ((!flag) | (j < maxNbOligo)) {
			logFile.println("Picking non specific oligos");
			Set set = map01.keySet();
			Integer ainteger[] = new Integer[set.size()];
			ainteger = (Integer[]) set.toArray(ainteger);
			Arrays.sort(ainteger);
			for (int k = 0; (k < ainteger.length) & (j < maxNbOligo); k++) {
				Vector vector = (Vector) map01.get(ainteger[k]);
				for (int i1 = 0; (i1 < vector.size()) & (j < maxNbOligo); i1++) {
					int k1 = ((Integer) vector.elementAt(i1)).intValue();
					if (isBeginValid(k1, usedPos)) {
						formatOligo(k1,
								(double[][]) map00.get(new Integer(k1)), map02,
								map03);
						logFile.println(seqName + "\t" + (k1 + 1)
								+ "\tNon specific oligos selected");
						flag = true;
						j++;
						usedPos.add(new Integer(k1));
					}
				}

			}

		}
		logFile.flush();
		return flag;
	}

	private boolean isBeginValid(int i, List<Integer> list) {
		
		for (Integer j : list) {
			int k = j.intValue() - betweenOligo;
			int l = k + betweenOligo * 2;
			if (k < i && i < l) {
				return false;
			}
		}

		return true;
	}

	private static boolean containsProhibited(Sequence sequence1, String s) {
		boolean flag = false;
		String s1 = sequence1.sequence().toUpperCase();
		for (StringTokenizer stringtokenizer = new StringTokenizer(s, ";"); stringtokenizer
				.hasMoreTokens() & (!flag);)
			if (s1.indexOf(stringtokenizer.nextToken().toUpperCase()) != -1)
				flag = true;

		return flag;
	}

	private static String maskSequence(String s, String s1) {
		for (StringTokenizer stringtokenizer = new StringTokenizer(s1, ";"); stringtokenizer
				.hasMoreTokens();) {
			String s2 = stringtokenizer.nextToken().toUpperCase();
			int i = s2.length();
			char c = s2.charAt(0);
			boolean flag = true;
			for (int j = 1; j < s2.length(); j++)
				flag &= c == s2.charAt(j);

			int k;
			for (; s.indexOf(s2) != -1; s = s.substring(0, s.indexOf(s2))
					+ generateN(k) + s.substring(s.indexOf(s2) + k)) {
				k = i;
				if (flag) {
					for (int l = s.indexOf(s2); l + k < s.length()
							&& c == s.charAt(l + k); k++)
						;
				}
			}

		}

		return s;
	}

	private static String generateN(int i) {
		String s = "";
		for (int j = 0; j < i; j++)
			s = s.concat("N");

		return s;
	}

	private void formatOligo(int i, double ad[][], String s, String s1) {
		Map<Integer, String> hashtable = Maps.newHashMap();
		Map<Integer, String> hashtable1 = Maps.newHashMap();
		hashtable.put(new Integer(i), s);
		hashtable1.put(new Integer(i), s1);
		formatOligo(i, ad, hashtable, hashtable1);
	}

	private void formatOligo(int i, double ad[][], Map<Integer, String> hashtable,
			Map<Integer,String> hashtable1) {
		String s = "";
		String s1 = "";
		String s2 = "";
		String s3 = "";
		String s4 = "";
		String s5 = (String) hashtable1.get(new Integer(i));
		for (int j = 0; j < ad.length; j++) {
			String s6 = alignment[j][0].trim();
			if (s6.equals(seqName))
				s = s6 + "; ";
			else if (ad[j][3] >= (double) crossHyb) {
				String s7 = (new Double(ad[j][0])).toString();
				if (s7.length() - s7.indexOf(".") > 3)
					s7 = s7.substring(0, s7.indexOf(".") + 3);
				String s8 = (new Double(ad[j][1])).toString();
				if (s8.length() - s8.indexOf(".") > 3)
					s8 = s8.substring(0, s8.indexOf(".") + 3);
				String s9 = (new Double(ad[j][2])).toString();
				if (s9.length() - s9.indexOf(".") > 3)
					s9 = s9.substring(0, s9.indexOf(".") + 3);
				String s10 = (new Double(ad[j][3])).toString();
				if (s10.length() - s10.indexOf(".") > 3)
					s10 = s10.substring(0, s10.indexOf(".") + 3);
				String s11 = "";
				for (int k = i; k < i
						+ ((String) hashtable.get(new Integer(i))).length(); k++)
					s11 = s11 + alignment[j][k + 1];

				s = s + s6 + " (" + s7 + " " + s8 + " " + s9 + " " + s10 + " "
						+ s11 + ")" + "; ";
			}
		}

		if (s.indexOf(";") != -1)
			s = s.substring(0, s.lastIndexOf(";"));
		Sequence sequence1 = new Sequence(
				(String) hashtable.get(new Integer(i)));
		s1 = (new Double(sequence1.deltaG(temperature))).toString();
		s2 = (new Double(sequence1.deltaH())).toString();
		s3 = (new Double(sequence1.deltaS())).toString();
		s4 = (new Double(sequence1.tmNN(concDna))).toString();
		oligoDb.print(seqName + "\t" + (i + deletedLength + 1) + "\t"
				+ sequence1.length() + "\t");
		if (s1.length() - s1.indexOf(".") >= 3)
			oligoDb.print(s1.substring(0, s1.indexOf(".") + 3) + "\t");
		else
			oligoDb.print(s1 + "\t");
		if (s2.length() - s2.indexOf(".") >= 3)
			oligoDb.print(s2.substring(0, s2.indexOf(".") + 3) + "\t");
		else
			oligoDb.print(s2 + "\t");
		if (s3.length() - s3.indexOf(".") >= 3)
			oligoDb.print(s3.substring(0, s3.indexOf(".") + 3) + "\t");
		else
			oligoDb.print(s3 + "\t");
		if (s4.length() - s4.indexOf(".") >= 3)
			oligoDb.print(s4.substring(0, s4.indexOf(".") + 3) + "\t");
		else
			oligoDb.print(s4 + "\t");
		oligoDb.println(s + "\t" + (String) hashtable.get(new Integer(i)));
		oligoDb.flush();
	}

	private void updateThermoData() {
		int i = oligoSequence.length();
		String as[] = new String[i];
		String s = "";
		for (int j = 0; j < i; j++) {
			as[j] = oligoSequence.sequence(j, j + 1).toUpperCase();
			s = s + as[j];
		}

		for (int k = 0; k < nbAlignment; k++) {
			String as1[] = new String[i];
			String s1 = "";
			int l = 0;
			for (int i1 = begin + 1; i1 <= end; i1++) {
				as1[l] = alignment[k][i1].toUpperCase();
				s1 = s1 + as1[l];
				l++;
			}

			float f = 0.0F;
			float f1 = 0.0F;
			float f2 = 0.0F;
			float f4 = 0.0F;
			if (s1.length() > 12) {
				int j1 = 0;
				int k1 = j1;
				for (; j1 < i && as1[j1].equals(""); j1++)
					;
				for (; j1 + 2 < i
						&& !(as1[j1].equals(as[j1])
								& as1[j1 + 1].equals(as[j1 + 1]) & as1[j1 + 2]
								.equals(as[j1 + 2])); j1++)
					;
				if (j1 + 1 < i)
					if (as1[j1].equals("A") | as1[j1].equals("T")) {
						f = 1.03F;
						f1 = 2.3F;
					} else if (as1[j1].equals("G") | as1[j1].equals("C")) {
						f = 0.98F;
						f1 = 0.1F;
					}
				for (j1++; j1 < i; j1++)
					if (as1[j1].equals(as[j1])) {
						int l1 = convertCode(as[j1 - 1] + as[j1]);
						int j2 = convertCode(as1[j1 - 1] + as1[j1]);
						f += dataG[l1][j2];
						f1 += dataH[l1][j2];
						k1 = j1;
					} else if (as1[j1].equals("A") & as[j1].equals("G")
							| as1[j1].equals("C") & as[j1].equals("T")) {
						int i2 = convertCode(as[j1 - 1] + as[j1]);
						int k2 = convertCode(as1[j1 - 1] + as1[j1]);
						f += dataG[i2][k2];
						f1 += dataH[i2][k2];
						k1 = j1;
					} else {
						String s2 = as1[j1 - 1];
						String s3 = as[j1 - 1];
						for (; j1 + 1 < i
								&& (!as1[j1].equals(as[j1]) || !as1[j1 + 1]
										.equals(as[j1 + 1])); j1++) {
							if (!as1[j1].equals("-"))
								s2 = s2 + as1[j1];
							s3 = s3 + as[j1];
						}

						if (j1 + 1 != i) {
							s2 = s2 + as1[j1];
							s3 = s3 + as[j1];
							if (s2.length() == 2) {
								int j3 = (s2.length() + s3.length()) - 4;
								int i4 = 0;
								i4 = convertCode(s2);
								f += dataG[625][i4 * 20 + j3];
								f1 += dataH[625][i4 * 20 + j3];
							} else if ((s3.length() == 3)
									& (s2.length() > 3)
									& (s3.substring(1, 2).equals(
											s2.substring(1, 2)) | s3.substring(
											1, 2).equals(
											s2.substring(s2.length() - 2,
													s2.length() - 1)))) {
								int k3 = convertCode(s3.substring(0, 2));
								f += dataG[k3][k3];
								f1 += dataH[k3][k3];
								s3 = s3.substring(1);
								s2 = s2.substring(1);
								int j4 = (s2.length() + s3.length()) - 4;
								k3 = convertCode(s3);
								f += dataG[625][k3 * 20 + j4];
								f1 += dataH[625][k3 * 20 + j4];
							} else if ((s2.length() <= 4) & (s3.length() <= 4)) {
								int l2 = convertCode(s3);
								int i3 = convertCode(s2);
								f += dataG[l2][i3];
								f1 += dataH[l2][i3];
							} else {
								int l3 = (s2.length() + s3.length()) - 4;
								int k4 = Math.abs(s2.length() - s3.length());
								f = f
										+ dataG[625][l3]
										+ dataG[convertCode(s3.substring(0, 2))][convertCode(s2
												.substring(0, 2))]
										+ dataG[convertCode(s3.substring(s3
												.length() - 2))][convertCode(s2
												.substring(s2.length() - 2))]
										+ (float) k4 * 0.16F;
								f1 = f1
										+ dataH[625][l3]
										+ dataH[convertCode(s3.substring(0, 2))][convertCode(s2
												.substring(0, 2))]
										+ dataH[convertCode(s3.substring(s3
												.length() - 2))][convertCode(s2
												.substring(s2.length() - 2))];
							}
						}
					}

				if (as1[k1].equals("A") | as1[k1].equals("T")) {
					f += 1.03F;
					f1 += 2.3F;
				} else {
					f += 0.98F;
					f1 += 0.1F;
				}
				temperatureK = temperature + 273.14999999999998D;
				float f3 = ((f1 - f) / 310.15F) * 1000F;
				float f5 = (1000F * f1)
						/ (f3 + (float) (1.9872000217437744D * Math
								.log(concDna / 4D))) - 273.15F;
				f = f1 - (float) (temperatureK * (double) f3) / 1000F;
				thermoData[k][0] = f;
				thermoData[k][1] = f1;
				thermoData[k][2] = f3;
				thermoData[k][3] = f5;
			}
		}

	}

	private void newupdateThermoData() {
		int i = oligoSequence.length();
		String as[] = new String[i];
		String s = "";
		for (int j = 0; j < i; j++) {
			as[j] = oligoSequence.sequence(j, j + 1).toUpperCase();
			s = s + as[j];
		}

		for (int k = 0; k < nbAlignment; k++) {
			String as1[] = new String[i];
			String s1 = "";
			int l = 0;
			for (int i1 = begin + 1; i1 <= end; i1++) {
				as1[l] = alignment[k][i1].toUpperCase();
				s1 = s1 + as1[l];
				l++;
			}

			float f = 0.0F;
			float f1 = 0.0F;
			float f2 = 0.0F;
			float f4 = 0.0F;
			if (s1.length() > 12) {
				int j1 = 0;
				int k1 = j1;
				for (; j1 < i && as1[j1].equals(""); j1++)
					;
				for (; j1 + 2 < i
						&& !(as1[j1].equals(as[j1])
								& as1[j1 + 1].equals(as[j1 + 1]) & as1[j1 + 2]
								.equals(as[j1 + 2])); j1++)
					;
				if (j1 + 1 < i)
					if (as1[j1].equals("A") | as1[j1].equals("T")) {
						f = 1.03F;
						f1 = 2.3F;
					} else if (as1[j1].equals("G") | as1[j1].equals("C")) {
						f = 0.98F;
						f1 = 0.1F;
					}
				for (j1++; j1 < i; j1++)
					if (as1[j1].equals(as[j1])) {
						int l1 = convertCode(as[j1 - 1] + as[j1]);
						int j2 = convertCode(as1[j1 - 1] + as1[j1]);
						f += dataG[l1][j2];
						f1 += dataH[l1][j2];
						k1 = j1;
					} else if (as1[j1].equals("A") & as[j1].equals("G")
							| as1[j1].equals("C") & as[j1].equals("T")) {
						int i2 = convertCode(as[j1 - 1] + as[j1]);
						int k2 = convertCode(as1[j1 - 1] + as1[j1]);
						f += dataG[i2][k2];
						f1 += dataH[i2][k2];
						k1 = j1;
					} else {
						String s2 = as1[j1 - 1];
						String s3 = as[j1 - 1];
						for (; j1 + 1 < i
								&& (!as1[j1].equals(as[j1]) || !as1[j1 + 1]
										.equals(as[j1 + 1])); j1++) {
							if (!as1[j1].equals("-"))
								s2 = s2 + as1[j1];
							s3 = s3 + as[j1];
						}

						if (j1 + 1 != i) {
							s2 = s2 + as1[j1];
							s3 = s3 + as[j1];
							if (s2.length() == 2) {
								int j3 = (s2.length() + s3.length()) - 4;
								int i4 = 0;
								i4 = convertCode(s2);
								f += dataG[625][i4 * 20 + j3];
								f1 += dataH[625][i4 * 20 + j3];
							} else if ((s3.length() == 3)
									& (s2.length() > 3)
									& (s3.substring(1, 2).equals(
											s2.substring(1, 2)) | s3.substring(
											1, 2).equals(
											s2.substring(s2.length() - 2,
													s2.length() - 1)))) {
								int k3 = convertCode(s3.substring(0, 2));
								f += dataG[k3][k3];
								f1 += dataH[k3][k3];
								s3 = s3.substring(1);
								s2 = s2.substring(1);
								int j4 = (s2.length() + s3.length()) - 4;
								k3 = convertCode(s3);
								f += dataG[625][k3 * 20 + j4];
								f1 += dataH[625][k3 * 20 + j4];
							} else if ((s2.length() <= 4) & (s3.length() <= 4)) {
								int l2 = convertCode(s3);
								int i3 = convertCode(s2);
								f += dataG[l2][i3];
								f1 += dataH[l2][i3];
							} else {
								int l3 = (s2.length() + s3.length()) - 4;
								int k4 = Math.abs(s2.length() - s3.length());
								f = f
										+ dataG[625][l3]
										+ dataG[convertCode(s3.substring(0, 2))][convertCode(s2
												.substring(0, 2))]
										+ dataG[convertCode(s3.substring(s3
												.length() - 2))][convertCode(s2
												.substring(s2.length() - 2))]
										+ (float) k4 * 0.16F;
								f1 = f1
										+ dataH[625][l3]
										+ dataH[convertCode(s3.substring(0, 2))][convertCode(s2
												.substring(0, 2))]
										+ dataH[convertCode(s3.substring(s3
												.length() - 2))][convertCode(s2
												.substring(s2.length() - 2))];
							}
						}
					}

				if (as1[k1].equals("A") | as1[k1].equals("T")) {
					f += 1.03F;
					f1 += 2.3F;
				} else {
					f += 0.98F;
					f1 += 0.1F;
				}
				temperatureK = temperature + 273.14999999999998D;
				float f3 = ((f1 - f) / 310.15F) * 1000F;
				float f5 = (1000F * f1)
						/ (f3 + (float) (1.9872000217437744D * Math
								.log(concDna / 4D))) - 273.15F;
				f = f1 - (float) (temperatureK * (double) f3) / 1000F;
				thermoData[k][0] = f;
				thermoData[k][1] = f1;
				thermoData[k][2] = f3;
				thermoData[k][3] = f5;
			}
		}

	}

	public static int convertCode(String s) {
		s = s.toUpperCase();
		double d = 0.0D;
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			switch (c) {
			case 71: // 'G'
				d += 1.0D * Math.pow(5D, (new Double(i)).doubleValue());
				break;

			case 65: // 'A'
				d += 2D * Math.pow(5D, (new Double(i)).doubleValue());
				break;

			case 84: // 'T'
				d += 3D * Math.pow(5D, (new Double(i)).doubleValue());
				break;

			case 67: // 'C'
				d += 4D * Math.pow(5D, (new Double(i)).doubleValue());
				break;
			}
		}

		return (new Double(d)).intValue();
	}

	public static double dGStructure(String s, double d) throws IOException {
		if (s.indexOf(">") != -1)
			s = s.substring(s.indexOf("\n") + 1);
		for (; s.indexOf(">") != -1; s = s.substring(0, s.indexOf("\n"))
				+ s.substring(s.indexOf("\n") + 1))
			;
		Runtime runtime = Runtime.getRuntime();
		String s1 = "hybrid-ss-min -nDNA -t" + d + " -T" + d + " -M0 -N1 -q "
				+ s;
		Process process = runtime.exec(s1);
		BufferedReader bufferedreader = new BufferedReader(
				new InputStreamReader(process.getInputStream()));
		String s2 = bufferedreader.readLine();
		double d1 = 1.0D;
		try {
			d1 = (new Double(s2)).doubleValue();
		} catch (Exception exception) {
		}
		bufferedreader.close();
		process.destroy();
		return d1;
	}

	private int lengthMin;
	private int lengthMax;
	private int distance;
	private int tmMin;
	private int tmMax;
	private int deltaTm;
	private int crossHyb;
	private int seqLength;
	private int begin;
	private int end;
	private int nbAlignment;
	private int maxNbOligo;
	private int originalLength;
	private int deletedLength;
	private int hsp;
	private int betweenOligo;
	private String listProhibited;
	private String blastDB;
	private String seqName;
	private String sequence;
	private String currentDir;
	private Sequence seq;
	private Sequence oligoSequence;
	private List<Integer> usedPos;
	private double concDna;
	private double temperature;
	private double temperatureK;
	private double dGStruct;
	private double minGC;
	private double maxGC;
	private double expected;
	private double lambda;
	private double K;
	private double blastDbLength;
	private double nbSeqBlastDb;
	private String alignment[][];
	private double thermoData[][];
	private static float dataG[][];
	private static float dataH[][];
	private PrintWriter logFile;
	private PrintWriter skipped;
	private PrintWriter oligoDb;
	private File directory;
	private RaslRuntime runtime;
}
