/*
 * Copyright 2014 Colby Skeggs, Gregor Peach (Added Folders)
 * 
 * This file is part of the CCRE, the Common Chicken Runtime Engine.
 * 
 * The CCRE is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * The CCRE is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with the CCRE.  If not, see <http://www.gnu.org/licenses/>.
 */
package intelligence.monitor;

import ccre.channel.BooleanInput;
import ccre.channel.BooleanStatus;
import ccre.channel.EventOutput;
import ccre.channel.FloatInput;
import ccre.channel.FloatStatus;
import ccre.cluck.Cluck;
import ccre.ctrl.BooleanMixing;
import ccre.log.Logger;
import ccre.util.LineCollectorOutputStream;
import intelligence.IndicatorLight;
import static intelligence.monitor.PhidgetMonitor.ANALOG_COUNT;
import static intelligence.monitor.PhidgetMonitor.INPUT_COUNT;
import static intelligence.monitor.PhidgetMonitor.LCD_LINES;
import static intelligence.monitor.PhidgetMonitor.OUTPUT_COUNT;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class VirtualPhidgetMonitor extends javax.swing.JFrame implements IPhidgetMonitor {

    private static final long serialVersionUID = -7676442704909536104L;
    private final BooleanStatus attached = new BooleanStatus();
    private EventOutput wantClose;

    /**
     * Creates new form VirtualPhidgetDevice
     */
    public VirtualPhidgetMonitor() {
        initComponents();
    }
    
    public void setCloseEvent(EventOutput wantClose) {
        this.wantClose = wantClose; 
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lcdLine0 = new javax.swing.JTextField();
        lcdLine1 = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        btn0 = new javax.swing.JToggleButton();
        btn1 = new javax.swing.JToggleButton();
        btn2 = new javax.swing.JToggleButton();
        btn3 = new javax.swing.JToggleButton();
        btn4 = new javax.swing.JToggleButton();
        btn5 = new javax.swing.JToggleButton();
        btn6 = new javax.swing.JToggleButton();
        btn7 = new javax.swing.JToggleButton();
        out0 = new intelligence.IndicatorLight();
        out1 = new intelligence.IndicatorLight();
        out2 = new intelligence.IndicatorLight();
        out3 = new intelligence.IndicatorLight();
        out6 = new intelligence.IndicatorLight();
        out7 = new intelligence.IndicatorLight();
        out5 = new intelligence.IndicatorLight();
        out4 = new intelligence.IndicatorLight();
        ana0 = new javax.swing.JSlider();
        ana1 = new javax.swing.JSlider();
        ana2 = new javax.swing.JSlider();
        ana3 = new javax.swing.JSlider();
        ana4 = new javax.swing.JSlider();
        ana5 = new javax.swing.JSlider();
        ana6 = new javax.swing.JSlider();
        ana7 = new javax.swing.JSlider();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        lcdLine0.setColumns(20);
        lcdLine0.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N

        lcdLine1.setColumns(20);
        lcdLine1.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N

        btn0.setText("0");
        btn0.setMargin(new java.awt.Insets(2, 2, 2, 2));

        btn1.setText("1");
        btn1.setMargin(new java.awt.Insets(2, 2, 2, 2));

        btn2.setText("2");
        btn2.setMargin(new java.awt.Insets(2, 2, 2, 2));

        btn3.setText("3");
        btn3.setMargin(new java.awt.Insets(2, 2, 2, 2));

        btn4.setText("4");
        btn4.setMargin(new java.awt.Insets(2, 2, 2, 2));

        btn5.setText("5");
        btn5.setMargin(new java.awt.Insets(2, 2, 2, 2));

        btn6.setText("6");
        btn6.setMargin(new java.awt.Insets(2, 2, 2, 2));

        btn7.setText("7");
        btn7.setMargin(new java.awt.Insets(2, 2, 2, 2));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false).addComponent(btn0, javax.swing.GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE).addComponent(btn4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false).addComponent(btn1, javax.swing.GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE).addComponent(btn5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false).addComponent(btn6, javax.swing.GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE).addComponent(btn2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false).addComponent(btn3, javax.swing.GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE).addComponent(btn7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))));
        jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(btn0).addComponent(btn1).addComponent(btn2).addComponent(btn3)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(btn4).addComponent(btn5).addComponent(btn6).addComponent(btn7))));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addGap(20, 20, 20).addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING).addComponent(out0, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(out4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false).addGroup(layout.createSequentialGroup().addComponent(out1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(out2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(out3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)).addGroup(layout.createSequentialGroup().addComponent(out5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(out6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(out7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))).addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false).addComponent(ana7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE).addComponent(ana6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE).addComponent(ana5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE).addComponent(ana4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE).addComponent(ana3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE).addComponent(ana2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE).addComponent(ana1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE).addComponent(ana0, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE).addComponent(lcdLine1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE).addComponent(lcdLine0, javax.swing.GroupLayout.Alignment.LEADING).addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))).addGap(0, 0, 0)));
        layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(lcdLine0, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(lcdLine1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING).addComponent(out0, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(out1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(out2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(out3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(out6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(out4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(out5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(out7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(ana0, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(ana1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(ana2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(ana3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(ana4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(ana5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(ana6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(ana7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(0, 0, Short.MAX_VALUE)));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        if (wantClose == null) {
            System.exit(0);
        } else {
            wantClose.event();
        }
    }//GEN-LAST:event_formWindowClosing

    @Override
    public void share() {
        attached.set(true);
        IndicatorLight[] lights = new IndicatorLight[] { out0, out1, out2, out3, out4, out5, out6, out7 };
        for (int i = 0; i < OUTPUT_COUNT; i++) {
            Cluck.publish("phidget-bo" + i, BooleanMixing.invert(lights[i])); // Invert because that's what happens with the real Phidget.
        }
        JTextField[] lcd = new JTextField[] { lcdLine0, lcdLine1 };
        for (int i = 0; i < LCD_LINES; i++) {
            final JTextField lcdl = lcd[i];
            Cluck.publish("phidget-lcd" + i, new LineCollectorOutputStream() {
                @Override
                protected void collect(final String str) {
                    EventQueue.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            lcdl.setText((str + "                    ").substring(0, 20));
                        }
                    });
                }
            });
        }
        Cluck.publish("phidget-attached", (BooleanInput) attached);
        JToggleButton[] btns = new JToggleButton[] { btn0, btn1, btn2, btn3, btn4, btn5, btn6, btn7 };
        for (int i = 0; i < INPUT_COUNT; i++) {
            final BooleanStatus stat = new BooleanStatus();
            final JToggleButton btn = btns[i];
            if (btn.getActionListeners().length != 0) {
                Logger.warning("Bad listener count on unattached button!");
            }
            btn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    stat.set(btn.isSelected());
                }
            });
            Cluck.publish("phidget-bi" + i, (BooleanInput) stat);
        }
        JSlider[] anas = new JSlider[] { ana0, ana1, ana2, ana3, ana4, ana5, ana6, ana7 };
        for (int i = 0; i < ANALOG_COUNT; i++) {
            final FloatStatus stat = new FloatStatus();
            final JSlider ana = anas[i];
            if (ana.getChangeListeners().length != 0) {
                Logger.warning("Bad listener count on unattached analog!");
            }
            ana.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    stat.set((ana.getValue() - 50) / 50f);
                }
            });
            Cluck.publish("phidget-ai" + i, (FloatInput) stat);
        }
        Cluck.getNode().notifyNetworkModified();
        this.setVisible(true);
    }

    @Override
    public void unshare() {
        this.setVisible(false);
        attached.set(false);
        Cluck.getNode().removeLink("phidget-attached");
        for (int i = 0; i < OUTPUT_COUNT; i++) {
            Cluck.getNode().removeLink("phidget-bo" + i);
        }
        for (int i = 0; i < LCD_LINES; i++) {
            Cluck.getNode().removeLink("phidget-lcd" + i);
        }
        JToggleButton[] btns = new JToggleButton[] { btn0, btn1, btn2, btn3, btn4, btn5, btn6, btn7 };
        for (int i = 0; i < INPUT_COUNT; i++) {
            final JToggleButton btn = btns[i];
            if (btn.getActionListeners().length != 1) {
                Logger.warning("Bad listener count on unattached button!");
            }
            for (ActionListener listener : btn.getActionListeners()) {
                btn.removeActionListener(listener);
            }
            Cluck.getNode().removeLink("phidget-bi" + i);
        }
        JSlider[] anas = new JSlider[] { ana0, ana1, ana2, ana3, ana4, ana5, ana6, ana7 };
        for (int i = 0; i < ANALOG_COUNT; i++) {
            final JSlider ana = anas[i];
            if (ana.getChangeListeners().length != 1) {
                Logger.warning("Bad listener count on unattached button!");
            }
            for (ChangeListener listener : ana.getChangeListeners()) {
                ana.removeChangeListener(listener);
            }
            Cluck.getNode().removeLink("phidget-ai" + i);
        }
        Cluck.getNode().notifyNetworkModified();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSlider ana0;
    private javax.swing.JSlider ana1;
    private javax.swing.JSlider ana2;
    private javax.swing.JSlider ana3;
    private javax.swing.JSlider ana4;
    private javax.swing.JSlider ana5;
    private javax.swing.JSlider ana6;
    private javax.swing.JSlider ana7;
    private javax.swing.JToggleButton btn0;
    private javax.swing.JToggleButton btn1;
    private javax.swing.JToggleButton btn2;
    private javax.swing.JToggleButton btn3;
    private javax.swing.JToggleButton btn4;
    private javax.swing.JToggleButton btn5;
    private javax.swing.JToggleButton btn6;
    private javax.swing.JToggleButton btn7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField lcdLine0;
    private javax.swing.JTextField lcdLine1;
    private intelligence.IndicatorLight out0;
    private intelligence.IndicatorLight out1;
    private intelligence.IndicatorLight out2;
    private intelligence.IndicatorLight out3;
    private intelligence.IndicatorLight out4;
    private intelligence.IndicatorLight out5;
    private intelligence.IndicatorLight out6;
    private intelligence.IndicatorLight out7;

    // End of variables declaration//GEN-END:variables

    @Override
    public void connectionUp() {
        if ("  Connection lost.  ".equals(lcdLine0.getText())) {
            lcdLine0.setText("  .  .  .  .  .  .  ");
        }
        if ("       Sorry.       ".equals(lcdLine1.getText())) {
            lcdLine1.setText("  .  .  .  .  .  .  ");
        }
    }

    @Override
    public void connectionDown() {
        lcdLine0.setText("  Connection lost.  ");
        lcdLine1.setText("       Sorry.       ");
    }

    @Override
    public void displayClosing() {
        lcdLine0.setText("Poultry Inspector is");
        lcdLine1.setText("     now closed.    ");
    }

    private Object writeReplace() {
        return new SerializedMonitor(attached.get(), wantClose);
    }

    private static class SerializedMonitor implements Serializable {

        private static final long serialVersionUID = -6097101016789921164L;
        private final boolean isAttached;
        private final EventOutput wantClose;

        private SerializedMonitor(boolean isAttached, EventOutput wantClose) {
            this.isAttached = isAttached;
            this.wantClose = wantClose;
        }

        private Object readResolve() {
            VirtualPhidgetMonitor out = new VirtualPhidgetMonitor();
            out.setCloseEvent(wantClose);
            if (isAttached) {
                out.share();
            }
            return out;
        }
    }
}