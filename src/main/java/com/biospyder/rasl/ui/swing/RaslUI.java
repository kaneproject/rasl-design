package com.biospyder.rasl.ui.swing;
// Decompiled by DJ v3.12.12.96 Copyright 2011 Atanas Neshkov  Date: 1/19/2014 7:20:31 PM
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   OligoArrayGUI.java

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

public class RaslUI extends JFrame
    implements ActionListener, DocumentListener, Runnable
{

    /**
	 * 
	 */
	private static final long serialVersionUID = -4695198160770723029L;
	public RaslUI()
    {
    }

    public static void main(String args[])
        throws IOException
    {
        processors = 1;
        maxNbOligo = 2;
        lengthMin = 45;
        lengthMax = 47;
        betweenOligo = (int)((1.5D * (double)(lengthMax + lengthMin)) / 2D);
        distance = 1500;
        tmMin = 82;
        tmMax = 88;
        temperature = 65D;
        crossHyb = 65;
        minGC = 35D;
        maxGC = 50D;
        listProhibited = "GGGGG;CCCCC;TTTTT;AAAAA";
        seqFile = "";
        blastDB = "";
        saveAs = "oligo.txt";
        rejectFile = "rejected.fas";
        logFile = "OligoArray.log";
        infoText = "";
        oligoArrayGUI = new RaslUI();
        oligoArrayGUI.init();
    }

    private static void runOligoArray()
        throws IOException
    {
       String sy = "java -Xmx1024m -jar OligoArray2.jar -i " + seqFile + " -d " + blastDB
       		+ " -o " + saveAs + " -r " + rejectFile + " -R " + logFile + " -n " + maxNbOligo
       		+ " -l " + lengthMin + " -L " + lengthMax + " -D " + distance + " -t " + tmMin 
        		+ " -T " + tmMax + " -s " + temperature + " -x " + crossHyb + " -p " + minGC + " -P " 
       		+ maxGC + " -m \"" + listProhibited + "\"" + " -N " + processors + " -g " + betweenOligo;
        String s = "java -Xmx1024m -classpath C:\\softwaredev\\rasl-design\\target\\classes"
        		+";OligoArray2.jar "
       		+" com.biospyder.rasl.design.OligoDesigner -i " 
        		+ seqFile + " -d " + blastDB
        		+ " -o " + saveAs + " -r " + rejectFile + " -R " + logFile + " -n " + maxNbOligo
        		+ " -l " + lengthMin + " -L " + lengthMax + " -D " + distance + " -t " + tmMin 
        		+ " -T " + tmMax + " -s " + temperature + " -x " + crossHyb + " -p " + minGC + " -P " 
        		+ maxGC + " -m \"" + listProhibited + "\"" + " -N " + processors + " -g " + betweenOligo;
        displayInfos("Starting OligoDesigner command : " +s);
        child = Runtime.getRuntime().exec(s);
        BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(child.getInputStream()));
        String s1;
        for(s1 = bufferedreader.readLine(); s1 != null && s1.indexOf("OligoArray has successfully") != 0; s1 = bufferedreader.readLine())
            displayInfos(s1);

        if(s1 != null)
            displayInfos(s1);
    }

    private static void displayInfos(String s)
    {
        infoArea.append(s + "\n");
    }

    public void init()
    {
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch(Exception exception) { }
        myFrame = new JFrame("RASL Oligo Designer");
       // myFrame.addWindowListener(new WindowAdapter() {

       //     public void windowClosing(WindowEvent windowevent)
        //    {
        //        System.exit(0);
         //   }

        //}
//);
        GridBagLayout gridbaglayout = new GridBagLayout();
        GridBagConstraints gridbagconstraints = new GridBagConstraints();
        Font font = new Font(null, 1, 18);
        JPanel jpanel = new JPanel();
        jpanel.setBorder(BorderFactory.createLineBorder(Color.black));
        jpanel.setLayout(gridbaglayout);
        JLabel jlabel = new JLabel("File data");
        jlabel.setForeground(Color.black);
        jlabel.setFont(font);
        makeConstraints(gridbagconstraints, 0, 0, 3, 1, "NONE", "CENTER");
        gridbagconstraints.insets = new Insets(10, 10, 30, 10);
        gridbaglayout.setConstraints(jlabel, gridbagconstraints);
        jpanel.add(jlabel);
        seq = new JButton("Select sequence input file");
        seq.addActionListener(this);
        makeConstraints(gridbagconstraints, 0, 1, 3, 1, "NONE", "CENTER");
        gridbagconstraints.insets = new Insets(10, 10, 10, 10);
        gridbaglayout.setConstraints(seq, gridbagconstraints);
        jpanel.add(seq);
        tefSeq = new JTextField("Choose file...", 20);
        tefSeq.setEditable(false);
        makeConstraints(gridbagconstraints, 0, 2, 3, 1, "NONE", "CENTER");
        gridbagconstraints.insets = new Insets(0, 10, 15, 10);
        gridbaglayout.setConstraints(tefSeq, gridbagconstraints);
        jpanel.add(tefSeq);
        blast = new JButton("Select Blast database (.nsq)");
        blast.addActionListener(this);
        makeConstraints(gridbagconstraints, 0, 3, 3, 1, "NONE", "CENTER");
        gridbagconstraints.insets = new Insets(10, 10, 10, 10);
        gridbaglayout.setConstraints(blast, gridbagconstraints);
        jpanel.add(blast);
        tefBlast = new JTextField("Choose file...", 20);
        tefBlast.setEditable(false);
        makeConstraints(gridbagconstraints, 0, 4, 3, 1, "NONE", "CENTER");
        gridbagconstraints.insets = new Insets(0, 10, 15, 10);
        gridbaglayout.setConstraints(tefBlast, gridbagconstraints);
        jpanel.add(tefBlast);
        save = new JButton("Save oligos as :");
        save.addActionListener(this);
        makeConstraints(gridbagconstraints, 0, 5, 3, 1, "NONE", "CENTER");
        gridbagconstraints.insets = new Insets(10, 10, 10, 10);
        gridbaglayout.setConstraints(save, gridbagconstraints);
        jpanel.add(save);
        tefSave = new JTextField(saveAs, 20);
        tefSave.setEditable(false);
        makeConstraints(gridbagconstraints, 0, 6, 3, 1, "NONE", "CENTER");
        gridbagconstraints.insets = new Insets(0, 10, 15, 10);
        gridbaglayout.setConstraints(tefSave, gridbagconstraints);
        jpanel.add(tefSave);
        rej = new JButton("Save rejected sequence as :");
        rej.addActionListener(this);
        makeConstraints(gridbagconstraints, 0, 7, 3, 1, "NONE", "CENTER");
        gridbagconstraints.insets = new Insets(10, 10, 10, 10);
        gridbaglayout.setConstraints(rej, gridbagconstraints);
        jpanel.add(rej);
        tefRej = new JTextField(rejectFile, 20);
        tefRej.setEditable(false);
        makeConstraints(gridbagconstraints, 0, 8, 3, 1, "NONE", "CENTER");
        gridbagconstraints.insets = new Insets(0, 10, 15, 10);
        gridbaglayout.setConstraints(tefRej, gridbagconstraints);
        jpanel.add(tefRej);
        log = new JButton("Save log file as :");
        log.addActionListener(this);
        makeConstraints(gridbagconstraints, 0, 9, 3, 1, "NONE", "CENTER");
        gridbagconstraints.insets = new Insets(10, 10, 10, 10);
        gridbaglayout.setConstraints(log, gridbagconstraints);
        jpanel.add(log);
        tefLog = new JTextField(logFile, 20);
        tefLog.setEditable(false);
        makeConstraints(gridbagconstraints, 0, 10, 3, 1, "NONE", "CENTER");
        gridbagconstraints.insets = new Insets(0, 10, 15, 10);
        gridbaglayout.setConstraints(tefLog, gridbagconstraints);
        jpanel.add(tefLog);
        run = new JButton("Run");
        run.addActionListener(this);
        makeConstraints(gridbagconstraints, 0, 11, 1, 1, "NONE", "CENTER");
        gridbagconstraints.insets = new Insets(30, 10, 10, 20);
        gridbaglayout.setConstraints(run, gridbagconstraints);
        jpanel.add(run);
        abort = new JButton("Cancel");
        abort.addActionListener(this);
        makeConstraints(gridbagconstraints, 1, 11, 1, 1, "NONE", "CENTER");
        gridbagconstraints.insets = new Insets(30, 10, 10, 20);
        gridbaglayout.setConstraints(abort, gridbagconstraints);
        jpanel.add(abort);
        exit = new JButton("Exit");
        exit.addActionListener(this);
        makeConstraints(gridbagconstraints, 2, 11, 1, 1, "NONE", "CENTER");
        gridbagconstraints.insets = new Insets(30, 10, 10, 20);
        gridbaglayout.setConstraints(exit, gridbagconstraints);
        jpanel.add(exit);
        JPanel jpanel1 = new JPanel();
        jpanel1.setBorder(BorderFactory.createLineBorder(Color.black));
        jpanel1.setLayout(gridbaglayout);
        JLabel jlabel1 = new JLabel("Oligonucleotide data");
        jlabel1.setForeground(Color.black);
        jlabel1.setFont(font);
        makeConstraints(gridbagconstraints, 0, 0, 5, 1, "NONE", "CENTER");
        gridbagconstraints.insets = new Insets(10, 10, 40, 10);
        gridbaglayout.setConstraints(jlabel1, gridbagconstraints);
        jpanel1.add(jlabel1);
        JLabel jlabel2 = new JLabel("Oligo length:");
        jlabel2.setForeground(Color.black);
        makeConstraints(gridbagconstraints, 0, 1, 1, 1, "HORIZONTAL", "EAST");
        gridbagconstraints.insets = new Insets(0, 10, 10, 5);
        gridbaglayout.setConstraints(jlabel2, gridbagconstraints);
        jpanel1.add(jlabel2);
        tefLengthMin = new JTextField((new Integer(lengthMin)).toString(), 2);
        tefLengthMin.getDocument().addDocumentListener(this);
        tefLengthMin.getDocument().putProperty("name", "tefLengthMin");
        makeConstraints(gridbagconstraints, 1, 1, 1, 1, "NONE", "CENTER");
        gridbagconstraints.insets = new Insets(0, 0, 10, 0);
        gridbaglayout.setConstraints(tefLengthMin, gridbagconstraints);
        jpanel1.add(tefLengthMin);
        JLabel jlabel3 = new JLabel("to");
        jlabel3.setForeground(Color.black);
        makeConstraints(gridbagconstraints, 2, 1, 1, 1, "NONE", "CENTER");
        gridbagconstraints.insets = new Insets(0, 5, 10, 5);
        gridbaglayout.setConstraints(jlabel3, gridbagconstraints);
        jpanel1.add(jlabel3);
        tefLengthMax = new JTextField((new Integer(lengthMax)).toString(), 2);
        tefLengthMax.getDocument().addDocumentListener(this);
        tefLengthMax.getDocument().putProperty("name", "tefLengthMax");
        makeConstraints(gridbagconstraints, 3, 1, 1, 1, "NONE", "CENTER");
        gridbagconstraints.insets = new Insets(0, 0, 10, 0);
        gridbaglayout.setConstraints(tefLengthMax, gridbagconstraints);
        jpanel1.add(tefLengthMax);
        JLabel jlabel4 = new JLabel("nt");
        jlabel4.setForeground(Color.black);
        makeConstraints(gridbagconstraints, 4, 1, 1, 1, "NONE", "WEST");
        gridbagconstraints.insets = new Insets(0, 5, 10, 5);
        gridbaglayout.setConstraints(jlabel4, gridbagconstraints);
        jpanel1.add(jlabel4);
        JLabel jlabel5 = new JLabel("Tm Range:");
        jlabel5.setForeground(Color.black);
        makeConstraints(gridbagconstraints, 0, 2, 1, 1, "HORIZONTAL", "EAST");
        gridbagconstraints.insets = new Insets(0, 10, 10, 5);
        gridbaglayout.setConstraints(jlabel5, gridbagconstraints);
        jpanel1.add(jlabel5);
        tefTmMin = new JTextField((new Integer(tmMin)).toString(), 2);
        tefTmMin.getDocument().addDocumentListener(this);
        tefTmMin.getDocument().putProperty("name", "tefTmMin");
        makeConstraints(gridbagconstraints, 1, 2, 1, 1, "NONE", "CENTER");
        gridbagconstraints.insets = new Insets(0, 0, 10, 0);
        gridbaglayout.setConstraints(tefTmMin, gridbagconstraints);
        jpanel1.add(tefTmMin);
        JLabel jlabel6 = new JLabel("to");
        jlabel6.setForeground(Color.black);
        makeConstraints(gridbagconstraints, 2, 2, 1, 1, "NONE", "CENTER");
        gridbagconstraints.insets = new Insets(0, 5, 10, 5);
        gridbaglayout.setConstraints(jlabel6, gridbagconstraints);
        jpanel1.add(jlabel6);
        tefTmMax = new JTextField((new Integer(tmMax)).toString(), 2);
        tefTmMax.getDocument().addDocumentListener(this);
        tefTmMax.getDocument().putProperty("name", "tefTmMax");
        makeConstraints(gridbagconstraints, 3, 2, 1, 1, "NONE", "CENTER");
        gridbagconstraints.insets = new Insets(0, 0, 10, 0);
        gridbaglayout.setConstraints(tefTmMax, gridbagconstraints);
        jpanel1.add(tefTmMax);
        JLabel jlabel7 = new JLabel("\260C");
        jlabel7.setForeground(Color.black);
        makeConstraints(gridbagconstraints, 4, 2, 1, 1, "NONE", "WEST");
        gridbagconstraints.insets = new Insets(0, 5, 10, 5);
        gridbaglayout.setConstraints(jlabel7, gridbagconstraints);
        jpanel1.add(jlabel7);
        JLabel jlabel8 = new JLabel("%GC Range:");
        jlabel8.setForeground(Color.black);
        makeConstraints(gridbagconstraints, 0, 3, 1, 1, "HORIZONTAL", "EAST");
        gridbagconstraints.insets = new Insets(0, 10, 10, 5);
        gridbaglayout.setConstraints(jlabel8, gridbagconstraints);
        jpanel1.add(jlabel8);
        tefGCMin = new JTextField((new Double(minGC)).toString(), 3);
        tefGCMin.getDocument().addDocumentListener(this);
        tefGCMin.getDocument().putProperty("name", "tefGCMin");
        makeConstraints(gridbagconstraints, 1, 3, 1, 1, "NONE", "CENTER");
        gridbagconstraints.insets = new Insets(0, 0, 10, 0);
        gridbaglayout.setConstraints(tefGCMin, gridbagconstraints);
        jpanel1.add(tefGCMin);
        JLabel jlabel9 = new JLabel("to");
        jlabel9.setForeground(Color.black);
        makeConstraints(gridbagconstraints, 2, 3, 1, 1, "NONE", "CENTER");
        gridbagconstraints.insets = new Insets(0, 5, 10, 5);
        gridbaglayout.setConstraints(jlabel9, gridbagconstraints);
        jpanel1.add(jlabel9);
        tefGCMax = new JTextField((new Double(maxGC)).toString(), 3);
        tefGCMax.getDocument().addDocumentListener(this);
        tefGCMax.getDocument().putProperty("name", "tefGCMax");
        makeConstraints(gridbagconstraints, 3, 3, 1, 1, "NONE", "CENTER");
        gridbagconstraints.insets = new Insets(0, 0, 10, 0);
        gridbaglayout.setConstraints(tefGCMax, gridbagconstraints);
        jpanel1.add(tefGCMax);
        JLabel jlabel10 = new JLabel("%");
        jlabel10.setForeground(Color.black);
        makeConstraints(gridbagconstraints, 4, 3, 1, 1, "NONE", "WEST");
        gridbagconstraints.insets = new Insets(0, 5, 10, 5);
        gridbaglayout.setConstraints(jlabel10, gridbagconstraints);
        jpanel1.add(jlabel10);
        JLabel jlabel11 = new JLabel("Max. Tm for structure:");
        jlabel11.setForeground(Color.black);
        makeConstraints(gridbagconstraints, 0, 4, 3, 1, "NONE", "EAST");
        gridbagconstraints.insets = new Insets(0, 10, 10, 5);
        gridbaglayout.setConstraints(jlabel11, gridbagconstraints);
        jpanel1.add(jlabel11);
        tefTmStruct = new JTextField((new Double(temperature)).toString(), 3);
        tefTmStruct.getDocument().addDocumentListener(this);
        tefTmStruct.getDocument().putProperty("name", "tefTmStruct");
        makeConstraints(gridbagconstraints, 3, 4, 1, 1, "NONE", "CENTER");
        gridbagconstraints.insets = new Insets(0, 0, 10, 0);
        gridbaglayout.setConstraints(tefTmStruct, gridbagconstraints);
        jpanel1.add(tefTmStruct);
        JLabel jlabel12 = new JLabel("\260C");
        jlabel12.setForeground(Color.black);
        makeConstraints(gridbagconstraints, 4, 4, 1, 1, "NONE", "WEST");
        gridbagconstraints.insets = new Insets(0, 5, 10, 5);
        gridbaglayout.setConstraints(jlabel12, gridbagconstraints);
        jpanel1.add(jlabel12);
        JLabel jlabel13 = new JLabel("Min Tm to consider X-hybrid.:");
        jlabel13.setForeground(Color.black);
        makeConstraints(gridbagconstraints, 0, 5, 3, 1, "NONE", "EAST");
        gridbagconstraints.insets = new Insets(0, 10, 10, 5);
        gridbaglayout.setConstraints(jlabel13, gridbagconstraints);
        jpanel1.add(jlabel13);
        tefXhybrid = new JTextField((new Integer(crossHyb)).toString(), 3);
        tefXhybrid.getDocument().addDocumentListener(this);
        tefXhybrid.getDocument().putProperty("name", "tefXhybrid");
        makeConstraints(gridbagconstraints, 3, 5, 1, 1, "NONE", "CENTER");
        gridbagconstraints.insets = new Insets(0, 0, 10, 0);
        gridbaglayout.setConstraints(tefXhybrid, gridbagconstraints);
        jpanel1.add(tefXhybrid);
        JLabel jlabel14 = new JLabel("\260C");
        jlabel14.setForeground(Color.black);
        makeConstraints(gridbagconstraints, 4, 5, 1, 1, "NONE", "WEST");
        gridbagconstraints.insets = new Insets(0, 5, 10, 5);
        gridbaglayout.setConstraints(jlabel14, gridbagconstraints);
        jpanel1.add(jlabel14);
        JLabel jlabel15 = new JLabel("Max. number of oligo :");
        jlabel15.setForeground(Color.black);
        makeConstraints(gridbagconstraints, 0, 6, 3, 1, "NONE", "EAST");
        gridbagconstraints.insets = new Insets(0, 10, 10, 5);
        gridbaglayout.setConstraints(jlabel15, gridbagconstraints);
        jpanel1.add(jlabel15);
        tefMxNbOligo = new JTextField((new Integer(maxNbOligo)).toString(), 2);
        tefMxNbOligo.getDocument().addDocumentListener(this);
        tefMxNbOligo.getDocument().putProperty("name", "tefMxNbOligo");
        makeConstraints(gridbagconstraints, 3, 6, 1, 1, "NONE", "CENTER");
        gridbagconstraints.insets = new Insets(0, 0, 10, 0);
        gridbaglayout.setConstraints(tefMxNbOligo, gridbagconstraints);
        jpanel1.add(tefMxNbOligo);
        JLabel jlabel16 = new JLabel("Distance 5'- Stop :");
        jlabel16.setForeground(Color.black);
        makeConstraints(gridbagconstraints, 0, 7, 3, 1, "NONE", "EAST");
        gridbagconstraints.insets = new Insets(0, 10, 10, 5);
        gridbaglayout.setConstraints(jlabel16, gridbagconstraints);
        jpanel1.add(jlabel16);
        tefDistance = new JTextField((new Integer(distance)).toString(), 4);
        tefDistance.getDocument().addDocumentListener(this);
        tefDistance.getDocument().putProperty("name", "tefDistance");
        makeConstraints(gridbagconstraints, 3, 7, 1, 1, "NONE", "CENTER");
        gridbagconstraints.insets = new Insets(0, 0, 10, 0);
        gridbaglayout.setConstraints(tefDistance, gridbagconstraints);
        jpanel1.add(tefDistance);
        JLabel jlabel17 = new JLabel("nt");
        jlabel17.setForeground(Color.black);
        makeConstraints(gridbagconstraints, 4, 7, 1, 1, "HORIZONTAL", "WEST");
        gridbagconstraints.insets = new Insets(0, 5, 10, 5);
        gridbaglayout.setConstraints(jlabel17, gridbagconstraints);
        jpanel1.add(jlabel17);
        JLabel jlabel18 = new JLabel("Min. distance between 2 oligos:");
        jlabel18.setForeground(Color.black);
        makeConstraints(gridbagconstraints, 0, 8, 3, 1, "NONE", "EAST");
        gridbagconstraints.insets = new Insets(0, 10, 10, 5);
        gridbaglayout.setConstraints(jlabel18, gridbagconstraints);
        jpanel1.add(jlabel18);
        tefBetween = new JTextField((new Integer(betweenOligo)).toString(), 3);
        tefBetween.getDocument().addDocumentListener(this);
        tefBetween.getDocument().putProperty("name", "tefBetween");
        makeConstraints(gridbagconstraints, 3, 8, 1, 1, "NONE", "CENTER");
        gridbagconstraints.insets = new Insets(0, 0, 10, 0);
        gridbaglayout.setConstraints(tefBetween, gridbagconstraints);
        jpanel1.add(tefBetween);
        JLabel jlabel19 = new JLabel("nt");
        jlabel19.setForeground(Color.black);
        makeConstraints(gridbagconstraints, 4, 8, 1, 1, "HORIZONTAL", "WEST");
        gridbagconstraints.insets = new Insets(0, 5, 10, 5);
        gridbaglayout.setConstraints(jlabel19, gridbagconstraints);
        jpanel1.add(jlabel19);
        JLabel jlabel20 = new JLabel("Nb of parallel process:");
        jlabel20.setForeground(Color.black);
        makeConstraints(gridbagconstraints, 0, 9, 3, 1, "NONE", "EAST");
        gridbagconstraints.insets = new Insets(0, 10, 10, 5);
        gridbaglayout.setConstraints(jlabel20, gridbagconstraints);
        jpanel1.add(jlabel20);
        tefProcess = new JTextField((new Integer(processors)).toString(), 2);
        tefProcess.getDocument().addDocumentListener(this);
        tefProcess.getDocument().putProperty("name", "tefProcess");
        makeConstraints(gridbagconstraints, 3, 9, 1, 1, "NONE", "CENTER");
        gridbagconstraints.insets = new Insets(0, 0, 10, 0);
        gridbaglayout.setConstraints(tefProcess, gridbagconstraints);
        jpanel1.add(tefProcess);
        JLabel jlabel21 = new JLabel("Prohibited sequences :");
        jlabel21.setForeground(Color.black);
        makeConstraints(gridbagconstraints, 0, 10, 5, 1, "HORIZONTAL", "WEST");
        gridbagconstraints.insets = new Insets(20, 10, 10, 5);
        gridbaglayout.setConstraints(jlabel21, gridbagconstraints);
        jpanel1.add(jlabel21);
        tefProhi = new JTextField("\"" + listProhibited + "\"", 20);
        tefProhi.getDocument().addDocumentListener(this);
        tefProhi.getDocument().putProperty("name", "tefProhi");
        makeConstraints(gridbagconstraints, 0, 11, 5, 1, "NONE", "CENTER");
        gridbagconstraints.insets = new Insets(0, 10, 10, 5);
        gridbaglayout.setConstraints(tefProhi, gridbagconstraints);
        jpanel1.add(tefProhi);
        JPanel jpanel2 = new JPanel();
        jpanel2.setBorder(BorderFactory.createLineBorder(Color.black));
        jpanel2.setLayout(gridbaglayout);
        JLabel jlabel22 = new JLabel("Status");
        jlabel22.setForeground(Color.black);
        jlabel22.setFont(font);
        makeConstraints(gridbagconstraints, 0, 0, 1, 1, "NONE", "NORTH");
        gridbagconstraints.insets = new Insets(10, 10, 10, 10);
        gridbaglayout.setConstraints(jlabel22, gridbagconstraints);
        jpanel2.add(jlabel22);
        infoArea = new TextArea(infoText, 35, 40);
        infoArea.setEditable(false);
        infoArea.setBackground(Color.white);
        makeConstraints(gridbagconstraints, 0, 1, 1, 1, "HORIZONTAL", "SOUTH");
        gridbagconstraints.insets = new Insets(0, 5, 20, 1);
        gridbaglayout.setConstraints(infoArea, gridbagconstraints);
        jpanel2.add(infoArea);
        myFrame.getContentPane().add(jpanel, "West");
        myFrame.getContentPane().add(jpanel1, "Center");
        myFrame.getContentPane().add(jpanel2, "East");
        myFrame.setBackground(Color.lightGray);
        myFrame.setSize(myFrame.getPreferredSize());
        myFrame.setResizable(true);
        myFrame.pack();
        myFrame.setVisible(true);
    }

    protected void makeConstraints(GridBagConstraints gridbagconstraints, int i, int j, int k, int l, String s, String s1)
    {
        gridbagconstraints.gridx = i;
        gridbagconstraints.gridy = j;
        gridbagconstraints.gridwidth = k;
        gridbagconstraints.gridheight = l;
        if(s.equals("NONE"))
            gridbagconstraints.fill = 0;
        else
        if(s.equals("HORIZONTAL"))
            gridbagconstraints.fill = 2;
        else
        if(s.equals("VERTICAL"))
            gridbagconstraints.fill = 3;
        else
        if(s.equals("BOTH"))
            gridbagconstraints.fill = 1;
        if(s1.equals("CENTER"))
            gridbagconstraints.anchor = 10;
        else
        if(s1.equals("NORTH"))
            gridbagconstraints.anchor = 11;
        else
        if(s1.equals("NORTHEAST"))
            gridbagconstraints.anchor = 12;
        else
        if(s1.equals("EAST"))
            gridbagconstraints.anchor = 13;
        else
        if(s1.equals("SOUTHEAST"))
            gridbagconstraints.anchor = 14;
        else
        if(s1.equals("SOUTH"))
            gridbagconstraints.anchor = 15;
        else
        if(s1.equals("SOUTHWEST"))
            gridbagconstraints.anchor = 16;
        else
        if(s1.equals("WEST"))
            gridbagconstraints.anchor = 17;
        else
        if(s1.equals("NORTHWEST"))
            gridbagconstraints.anchor = 18;
    }

    public void insertUpdate(DocumentEvent documentevent)
    {
        textChanged(documentevent);
    }

    public void removeUpdate(DocumentEvent documentevent)
    {
        textChanged(documentevent);
    }

    public void changedUpdate(DocumentEvent documentevent)
    {
        textChanged(documentevent);
    }

    public void textChanged(DocumentEvent documentevent)
    {
        Document document = documentevent.getDocument();
        if(document.getProperty("name").equals("tefLengthMin") && !tefLengthMin.getText().equals(""))
            try
            {
                lengthMin = (new Integer(tefLengthMin.getText())).intValue();
            }
            catch(Exception exception)
            {
                JOptionPane.showMessageDialog(myFrame, "Please enter only an integer number", "Error", 0);
            }
        if(document.getProperty("name").equals("tefLengthMax"))
        {
            if(!tefLengthMax.getText().equals(""))
                try
                {
                    lengthMax = (new Integer(tefLengthMax.getText())).intValue();
                }
                catch(Exception exception1)
                {
                    JOptionPane.showMessageDialog(myFrame, "Please enter only an integer number", "Error", 0);
                }
        } else
        if(document.getProperty("name").equals("tefDistance"))
        {
            if(!tefDistance.getText().equals(""))
                try
                {
                    distance = (new Integer(tefDistance.getText())).intValue();
                }
                catch(Exception exception2)
                {
                    JOptionPane.showMessageDialog(myFrame, "Please enter only an integer number", "Error", 0);
                }
        } else
        if(document.getProperty("name").equals("tefBetween"))
        {
            if(!tefBetween.getText().equals(""))
                try
                {
                    betweenOligo = (new Integer(tefBetween.getText())).intValue();
                }
                catch(Exception exception3)
                {
                    JOptionPane.showMessageDialog(myFrame, "Please enter only an integer number", "Error", 0);
                }
        } else
        if(document.getProperty("name").equals("tefTmMin"))
        {
            if(!tefTmMin.getText().equals(""))
                try
                {
                    tmMin = (new Integer(tefTmMin.getText())).intValue();
                }
                catch(Exception exception4)
                {
                    JOptionPane.showMessageDialog(myFrame, "Please enter only an integer number", "Error", 0);
                }
        } else
        if(document.getProperty("name").equals("tefTmMax"))
        {
            if(!tefTmMax.getText().equals(""))
                try
                {
                    tmMax = (new Integer(tefTmMax.getText())).intValue();
                }
                catch(Exception exception5)
                {
                    JOptionPane.showMessageDialog(myFrame, "Please enter only an integer number", "Error", 0);
                }
        } else
        if(document.getProperty("name").equals("tefGCMin"))
        {
            if(!tefTmMin.getText().equals(""))
                try
                {
                    minGC = (new Double(tefGCMin.getText())).doubleValue();
                }
                catch(Exception exception6)
                {
                    JOptionPane.showMessageDialog(myFrame, "Please enter only a real number", "Error", 0);
                }
        } else
        if(document.getProperty("name").equals("tefGCMax"))
        {
            if(!tefTmMax.getText().equals(""))
                try
                {
                    maxGC = (new Double(tefGCMax.getText())).doubleValue();
                }
                catch(Exception exception7)
                {
                    JOptionPane.showMessageDialog(myFrame, "Please enter only a real number", "Error", 0);
                }
        } else
        if(document.getProperty("name").equals("tefTmStruct"))
        {
            if(!tefTmStruct.getText().equals(""))
                try
                {
                    temperature = (new Double(tefTmStruct.getText())).doubleValue();
                }
                catch(Exception exception8)
                {
                    JOptionPane.showMessageDialog(myFrame, "Please enter only a real number", "Error", 0);
                }
        } else
        if(document.getProperty("name").equals("tefXhybrid"))
        {
            if(!tefXhybrid.getText().equals(""))
                try
                {
                    crossHyb = (new Integer(tefXhybrid.getText())).intValue();
                }
                catch(Exception exception9)
                {
                    JOptionPane.showMessageDialog(myFrame, "Please enter only an integer number", "Error", 0);
                }
        } else
        if(document.getProperty("name").equals("tefMxNbOligo"))
        {
            if(!tefMxNbOligo.getText().equals(""))
                try
                {
                    maxNbOligo = (new Integer(tefMxNbOligo.getText())).intValue();
                }
                catch(Exception exception10)
                {
                    JOptionPane.showMessageDialog(myFrame, "Please enter only an integer number", "Error", 0);
                }
        } else
        if(document.getProperty("name").equals("tefProcess"))
        {
            if(!tefProcess.getText().equals(""))
                try
                {
                    processors = (new Integer(tefProcess.getText())).intValue();
                }
                catch(Exception exception11)
                {
                    JOptionPane.showMessageDialog(myFrame, "Please enter only an integer number", "Error", 0);
                }
        } else
        if(document.getProperty("name").equals("tefProhi") && !tefProhi.getText().equals(""))
            try
            {
                listProhibited = tefProhi.getText();
            }
            catch(Exception exception12) { }
    }

    public void actionPerformed(ActionEvent actionevent)
    {
        Object obj = actionevent.getSource();
        if(obj instanceof JButton)
        {
            if(obj == seq)
            {
                FileDialog filedialog = new FileDialog(myFrame, "Select Sequence file", 0);
                filedialog.show();
                seqFile = filedialog.getDirectory() + filedialog.getFile();
                tefSeq.setText(seqFile);
            }
            if(obj == blast)
            {
                FileDialog filedialog1 = new FileDialog(myFrame, "Select Blast database", 0);
                filedialog1.show();
                blastDB = filedialog1.getDirectory() + filedialog1.getFile();
                tefBlast.setText(blastDB);
                blastDB = blastDB.substring(0, blastDB.lastIndexOf("."));
            }
            if(obj == save)
            {
                FileDialog filedialog2 = new FileDialog(myFrame, "Save oligos as", 1);
                filedialog2.show();
                saveAs = filedialog2.getDirectory() + filedialog2.getFile();
                tefSave.setText(saveAs);
            }
            if(obj == rej)
            {
                FileDialog filedialog3 = new FileDialog(myFrame, "Save rejected sequences as", 1);
                filedialog3.show();
                rejectFile = filedialog3.getDirectory() + filedialog3.getFile();
                tefRej.setText(rejectFile);
            }
            if(obj == log)
            {
                FileDialog filedialog4 = new FileDialog(myFrame, "Save log file as", 1);
                filedialog4.show();
                logFile = filedialog4.getDirectory() + filedialog4.getFile();
                tefLog.setText(logFile);
            }
            if(obj == run)
                start();
            if(obj == abort)
            {
                try
                {
                    child.destroy();
                }
                catch(Exception exception) { }
                displayInfos("Process cancelled by user");
            }
            if(obj == exit)
            {
                try
                {
                    child.destroy();
                }
                catch(Exception exception1) { }
                myFrame.dispose();
                System.exit(0);
            }
        }
    }

    public void start()
    {
        runSearch = new Thread(this);
        runSearch.start();
    }

    public void stop()
    {
    }

    public void run()
    {
        try
        {
            runOligoArray();
        }
        catch(Exception exception)
        {
            System.err.println(exception);
        }
    }

    private static RaslUI oligoArrayGUI;
    private Thread runSearch;
    private static JFrame myFrame;
    private static JButton seq;
    private static JButton blast;
    private static JButton save;
    private static JButton rej;
    private static JButton log;
    private static JButton run;
    private static JButton abort;
    private static JButton exit;
    private static JTextField tefLengthMin;
    private static JTextField tefLengthMax;
    private static JTextField tefDistance;
    private static JTextField tefTmMin;
    private static JTextField tefTmMax;
    private static JTextField tefGCMin;
    private static JTextField tefGCMax;
    private static JTextField tefTmStruct;
    private static JTextField tefXhybrid;
    private static JTextField tefTag5;
    private static JTextField tefTag3;
    private static JTextField tefProhi;
    private static JTextField tefSeq;
    private static JTextField tefBlast;
    private static JTextField tefSave;
    private static JTextField tefRej;
    private static JTextField tefLog;
    private static JTextField tefMxNbOligo;
    private static JTextField tefBetween;
    private static JTextField tefProcess;
    private static TextArea infoArea;
    private static int lengthMin;
    private static int lengthMax;
    private static int distance;
    private static int tmMin;
    private static int tmMax;
    private static int crossHyb;
    private static int processors;
    private static int maxNbOligo;
    private static int betweenOligo;
    private static String listProhibited;
    private static String seqFile;
    private static String blastDB;
    private static String saveAs;
    private static String rejectFile;
    private static String logFile;
    private static String infoText;
    private static double temperature;
    private static double minGC;
    private static double maxGC;
    private static Process child;
}
