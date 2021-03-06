/*
 * QuickQuiz
 * Copyright (C) 2005 David C. Briccetti
 * www.davebsoft.com
 *
 * This program is free software; you can redistribute it and/or modify 
 * it under the terms of the GNU General Public License as published by 
 * the Free Software Foundation; either version 2 of the License, or 
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License 
 * along with this program; if not, write to the Free Software Foundation, 
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package com.dbschools.gui;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;

/**
 * Displays a progress bar of time remaining, and fires property change
 * events as time passes.
 * 
 * @author David C. Briccetti
 */
public final class CountdownMeter extends javax.swing.JPanel implements Runnable {
    private static final long serialVersionUID = 1084437262815860408L;
    public static final String TIME_LEFT_PROPERTY_NAME = "timeLeft"; //$NON-NLS-1$
    private boolean active;
	private int timeLimit;
    private Double timeLeft;
    
    public static interface CountdownFinishListener {
        void countdownFinished();
    }
    private Set<CountdownFinishListener> listeners = 
            new HashSet<CountdownFinishListener>();
    
    /** Creates new form CountdownMeter */
    public CountdownMeter() {
        initComponents();
    }
    
    public void addCountdownFinishListener(CountdownFinishListener cdfl) {
        listeners.add(cdfl);
    }
    
    public void removeCountdownFinishListener(CountdownFinishListener cdfl) {
        listeners.remove(cdfl);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        progressBar = new javax.swing.JProgressBar();

        setLayout(new java.awt.BorderLayout());

        add(progressBar, java.awt.BorderLayout.CENTER);

    }//GEN-END:initComponents

    public void countDown(final int seconds) {
        timeLimit = seconds;
		setActive(true);
        new Thread(this, "Countdown Timer").start();
    }    
    
    /**
     * @see java.lang.Runnable#run()
     */
    public void run() {
        progressBar.setMinimum(0);
        progressBar.setMaximum(timeLimit * 10);
        progressBar.setStringPainted(true);
        final GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(new Date());
        cal.add(Calendar.SECOND, timeLimit);
        final Date timeAtLimit = cal.getTime();
		final long timeAtLimitAsLong = timeAtLimit.getTime();
        final NumberFormat fmt = NumberFormat.getInstance();
        fmt.setMinimumFractionDigits(1);
        fmt.setMaximumFractionDigits(1);
        try {
            while (active && new Date().before(timeAtLimit)) {
                final double secsLeft = (timeAtLimitAsLong - new Date().getTime()) / 1000.0;
                setTimeLeft(new Double(secsLeft));
                progressBar.setValue((int)(secsLeft * 10));
                progressBar.setString("Secs left: " + fmt.format(secsLeft));
                Thread.sleep(((int) (secsLeft * 1000)) % 100);
            }
        } catch (InterruptedException e) {
                e.printStackTrace();
        }
        setTimeLeft(new Double(0));
        progressBar.setValue(0);
        progressBar.setString("");
        setActive(false);
    }
    
    public void setActive(final boolean active) {
        this.active = active;
        if (! active) {
            for (CountdownFinishListener cdfl : listeners) {
                cdfl.countdownFinished();
            }
        }
    }

    private void setTimeLeft(final Double timeLeft) {
        final Double oldTimeLeft = this.timeLeft;
        this.timeLeft = timeLeft;
        firePropertyChange(TIME_LEFT_PROPERTY_NAME, oldTimeLeft, timeLeft);
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JProgressBar progressBar;
    // End of variables declaration//GEN-END:variables

}
