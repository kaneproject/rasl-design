package com.biospyder.rasl.integration;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.biojava3.core.sequence.*;
import org.biojava3.core.sequence.io.FastaReaderHelper;

import com.biospyder.rasl.oligoarray.Sequence;
/*
 * Integration test to validate using BioJava 3 FASTA reader
 */

public class TestReadFastaFile {

	private final String fastaFileName;
	
	
	
	public TestReadFastaFile(String fileName) {
		this.fastaFileName = fileName;
	}
	
	private void testRead() {
		LinkedHashMap<String, DNASequence> a;
		try {
			/*
			 * Selecting lazySequenceLoad=true will parse the FASTA file and figure 
			 * out the accessionid and offsets and return sequence objects that can 
			 * in the future read the sequence from the disk. This allows the loading 
			 * of large fasta files where you are only interested in one sequence 
			 * based on accession id.
			 * 2nd parameter is true for lazy loading
			 */
			a = FastaReaderHelper.readFastaDNASequence(new File(fastaFileName),true);

			for (  Entry<String, DNASequence> entry : a.entrySet() ) {
				String s = ">" +entry.getValue().getOriginalHeader() +"\n" +entry.getValue().getSequenceAsString();
				Sequence seq = new Sequence(s);
				System.out.println( ">" +seq.name() + "\n" + seq.sequence() );
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		
 
 
	}
	public static void main(String[] args) {
		String fileName = "chr1.fas";
		if (args.length >0 ){
			fileName= args[0];
		}
		TestReadFastaFile test = new TestReadFastaFile(fileName);
		test.testRead();

	}

}
