package com.biospyder.rasl.oligoarray;

public class SeqDispenser {
	Sequence aSeq;
	boolean empty;

	SeqDispenser() {
		this.empty = true;
	}

	synchronized void put(Sequence paramSequence) {
		while (!this.empty) {
			try {
				wait();
			} catch (InterruptedException localInterruptedException) {
				System.err.println(localInterruptedException);
			}
		}
		this.aSeq = paramSequence;
		this.empty = false;
		notify();
	}

	synchronized Sequence get() {
		while (this.empty) {
			try {
				wait();
			} catch (InterruptedException localInterruptedException) {
				System.err.println(localInterruptedException);
			}
		}
		this.empty = true;
		notifyAll();

		return this.aSeq;
	}
}
