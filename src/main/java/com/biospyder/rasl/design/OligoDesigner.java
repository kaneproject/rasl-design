package com.biospyder.rasl.design;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.biojava3.core.sequence.DNASequence;
import org.biojava3.core.sequence.io.FastaReaderHelper;

import com.biospyder.rasl.common.CommonValues;
import com.biospyder.rasl.common.Utilities;
import com.biospyder.rasl.design.service.BlastService;
import com.biospyder.rasl.design.service.RuntimeEnvironmentFactory;
import com.biospyder.rasl.design.service.SequenceQueueService;
import com.biospyder.rasl.oligoarray.Sequence;
import com.biospyder.rasl.pojo.RaslRuntime;

/*
 * Java application to support the design of unique RNA target probes. 
 * This class is based on code from the OligoArray2 API
 */
public class OligoDesigner {
	private static Logger logger = LogManager.getLogger(OligoDesigner.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		if (args.length == 0) {
			int i = 0;
			Utilities.displayHelp();
		} else {
			RaslRuntime runtime = RuntimeEnvironmentFactory.INSTANCE.createRaslRuntime(args);
			if(runtime.isValidRuntime()){
				try {
					runThermoBlast(runtime);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private static synchronized void runThermoBlast(RaslRuntime runtime)
	        throws IOException
	    {
		runtime.setReadBuff( new BufferedReader(new FileReader(runtime.getSeqFile())));
		BufferedReader readBuff = runtime.getReadBuff();
	        try
	        {
	            boolean flag = true;
	            String s = runtime.getReadBuff().readLine();
	            runtime.setOrf( s.substring(1));
	            s = runtime.getReadBuff().readLine();
	            String s5 = "";
	            for(; s != null && s.indexOf('>') == -1; s = runtime.getReadBuff().readLine())
	                s5 = s5.concat(s);

	            String s6 = ">" + runtime.getOrf() + "\n" + s5;
	            runtime.setHsp_table(BlastService.INSTANCE.intializeBlastParameters(runtime,s6));
	            System.out.println("Blast parameters initialized\n");
	            readBuff.close();
	            readBuff = new BufferedReader(new FileReader(runtime.getSeqFile()));
	            String s7 = (new Long((new Date()).getTime())).toString();
	            s7 = s7.substring(s7.length() - 8);
	            runtime.setTempDirectory( new File(runtime.getWorkDirectory(), s7));
	            if(!runtime.getTempDirectory().exists())
	                runtime.getTempDirectory().mkdirs();
	            Design adesign[] = new Design[runtime.getProcessors()];
	            
	            boolean flag1 = false;
	            for(int i = 0; i < runtime.getProcessors(); i++)
	            {
	            	adesign[i] = new Design(runtime, 
	            			(new File(runtime.getTempDirectory(), (new Integer(i)).toString())).toString());
	                adesign[i].start();
	            }

	            // read in sequence file in FASTA format, instantiate Sequence objects, and place in processing queue
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
	    			a = FastaReaderHelper.readFastaDNASequence(new File(runtime.getSeqFile()),true);

	    			for (  Entry<String, DNASequence> entry : a.entrySet() ) {
	    				String seqString = ">" +entry.getValue().getOriginalHeader() +"\n" +entry.getValue().getSequenceAsString();
	    				SequenceQueueService.INSTANCE.addSequence(new Sequence(seqString));	    				
	    			}
	    			a.clear(); // free memory
	    		} catch (Exception e) {
	    			System.out.println(e.getMessage());
	    			e.printStackTrace();
	    		}
	            
	            //readBuff.close();
	            System.out.println("No more sequence to dispatch");
	            // place stop sequences into the queue
	            for(int j = 0; j < 10*runtime.getProcessors(); j++)
	            	
	            	SequenceQueueService.INSTANCE.addSequence(new Sequence(CommonValues.STOP_SEQ));

	            for(int k = 0; k < runtime.getProcessors(); k++)
	                while(adesign[k].isAlive()) ;

	            runtime.setOligoDb(new PrintWriter(new FileOutputStream(runtime.getSaveAs())));
	            runtime.setSkipped(new PrintWriter(new FileOutputStream(runtime.getRejectFile())));
	            runtime.setLogFile(new PrintWriter(new FileOutputStream(runtime.getLog())));
	            for(int l = 0; l < runtime.getProcessors(); l++)
	            {
	                File file = new File(runtime.getTempDirectory(), (new Integer(l)).toString());
	                readBuff = new BufferedReader(new FileReader(new File(file, "oligos.txt")));
	                for(String s2 = readBuff.readLine(); s2 != null; s2 = readBuff.readLine())
	                    runtime.getOligoDb().println(s2);

	                readBuff.close();
	                readBuff = new BufferedReader(new FileReader(new File(file, "rejected.fas")));
	                for(String s3 = readBuff.readLine(); s3 != null; s3 = readBuff.readLine())
	                    runtime.getSkipped().println(s3);

	                readBuff.close();
	                readBuff = new BufferedReader(new FileReader(new File(file, "OligoArray.log")));
	                for(String s4 = readBuff.readLine(); s4 != null; s4 = readBuff.readLine())
	                    runtime.getLogFile().println(s4);

	                readBuff.close();
	                if(file.exists())
	                {
	                    File afile[] = file.listFiles();
	                    for(int i1 = 0; i1 < afile.length; i1++)
	                        afile[i1].delete();

	                    file.delete();
	                    runtime.getTempDirectory().delete();
	                }
	            }

	            runtime.getOligoDb().close();
	            runtime.getSkipped().close();
	            runtime.getLogFile().close();
	            System.out.println("OligoArray has successfully processed all sequences");
	        }
	        catch(Exception exception)
	        {
	            runtime.getLogFile().println(exception);
	        }
	    }
}
