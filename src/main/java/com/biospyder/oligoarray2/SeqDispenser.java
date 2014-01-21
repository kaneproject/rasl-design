package com.biospyder.oligoarray2;
// Decompiled by DJ v3.12.12.96 Copyright 2011 Atanas Neshkov  Date: 1/19/2014 7:23:33 PM
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   SeqDispenser.java

import java.io.PrintStream;

class SeqDispenser
{

    SeqDispenser()
    {
        empty = true;
    }

    synchronized void put(Sequence sequence)
    {
        while(!empty) 
            try
            {
                wait();
            }
            catch(InterruptedException interruptedexception)
            {
                System.err.println(interruptedexception);
            }
        aSeq = sequence;
        empty = false;
        notify();
    }

    synchronized Sequence get()
    {
        while(empty) 
            try
            {
                wait();
            }
            catch(InterruptedException interruptedexception)
            {
                System.err.println(interruptedexception);
            }
        empty = true;
        notifyAll();
        return aSeq;
    }

    Sequence aSeq;
    boolean empty;
}
