package com.biospyder.rasl.design.service;

import java.util.concurrent.LinkedBlockingQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.biojava3.core.sequence.DNASequence;

import com.biospyder.oligoarray2.Sequence;
import com.google.common.base.Preconditions;

/*
 * A Singleton implemented as an enum to provide general access to a blocking queue of DNA sequence objects
 * Service clients requesting the next sequence will block until a sequence is available while clients putting 
 * sequences onto the queue will block until space is available.
 * The maximum capacity of the queue is set by a parameter.
 * This class takes the place of the SeqDispenser class used in the original OligoArray2 application.
 */

public enum SequenceQueueService {

	INSTANCE;
	private static final int QUEUE_CAPACITY = 1000;
	private static Logger logger = LogManager.getLogger("SequenceQueueService");
	private LinkedBlockingQueue<Sequence> sequenceQueue = new LinkedBlockingQueue<Sequence>(QUEUE_CAPACITY);
	
	/*
	 * public method to add to queue
	 * 
	 */
	public void addSequence(Sequence seq){
		Preconditions.checkArgument(null != seq,"A null Sequence cannot be queued");
		try {
			sequenceQueue.put(seq);
		} catch (InterruptedException e) {
			logger.info("wait for addSequence interrupted");
			e.printStackTrace();
		}
	}
	
	
	/*
	 * public method to get a Sequence from the queue
	 */
	public Sequence getSequence() {
		if(sequenceQueue.isEmpty()) {
			logger.info("client waiting on empty sequence queue");
		}
		try {
			return sequenceQueue.take();
		} catch (InterruptedException e) {
			logger.info("wait for getSequence interrupted");
			e.printStackTrace();
			return null;
		}
	}
	
	/*
	 *  public method to allow clients to see if queue is empty
	 */
	public boolean isSequenceQueueEmpty() {
		return sequenceQueue.isEmpty();
	}

}
