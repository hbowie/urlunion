/*
 * CollectionWindow.java
 *
 * Created on August 5, 2007, 2:48 PM
 */

package com.powersurgepub.urlunion;

  import com.powersurgepub.psutils.*;
  import java.awt.event.*;

/**
 *
 * @author  hbowie
 */
public class CollectionWindow
    extends javax.swing.JFrame
      implements 
        WindowListener,
        WindowToManage {
  
  private URLCollection urls;
  
  /** Creates new form CollectionWindow */
  public CollectionWindow() {
    initComponents();
    titleText.setText ("My Bookmarks");
    // this.setBounds (100, 100, 600, 540);
  }

  public void setURLs (URLCollection urls) {
    this.urls = urls;
    fileNameText.setText(urls.getSource().toString());
    titleText.setText (urls.getTitle());
  }

  public void windowOpened      (WindowEvent e) {}
  public void windowClosing     (WindowEvent e) {}
  public void windowClosed      (WindowEvent e) {
    if (titleText.getText().length() > 0) {
      urls.setTitle (titleText.getText());
    }
  }
  public void windowIconified   (WindowEvent e) {}
  public void windowDeiconified (WindowEvent e) {}
  public void windowActivated   (WindowEvent e) {}
  public void windowDeactivated (WindowEvent e) {}
  
  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {
    java.awt.GridBagConstraints gridBagConstraints;

    titleLabel = new javax.swing.JLabel();
    titleText = new javax.swing.JTextField();
    filler = new javax.swing.JLabel();
    fileNameLabel = new javax.swing.JLabel();
    fileNameText = new javax.swing.JLabel();
    filler1 = new javax.swing.JLabel();
    okButton = new javax.swing.JButton();

    setMinimumSize(new java.awt.Dimension(400, 200));
    addComponentListener(new java.awt.event.ComponentAdapter() {
      public void componentHidden(java.awt.event.ComponentEvent evt) {
        formComponentHidden(evt);
      }
    });
    getContentPane().setLayout(new java.awt.GridBagLayout());

    titleLabel.setText("Title:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
    getContentPane().add(titleLabel, gridBagConstraints);

    titleText.setToolTipText("Title of this Wisdom collection.");
    titleText.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        titleTextActionPerformed(evt);
      }
    });
    titleText.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(java.awt.event.FocusEvent evt) {
        titleTextFocusLost(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
    getContentPane().add(titleText, gridBagConstraints);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.gridwidth = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
    getContentPane().add(filler, gridBagConstraints);

    fileNameLabel.setText("File Name:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(8, 4, 8, 4);
    getContentPane().add(fileNameLabel, gridBagConstraints);

    fileNameText.setText(" ");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(8, 4, 8, 4);
    getContentPane().add(fileNameText, gridBagConstraints);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
    getContentPane().add(filler1, gridBagConstraints);

    okButton.setText("OK");
    okButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        okButtonActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 3;
    getContentPane().add(okButton, gridBagConstraints);

    pack();
  }// </editor-fold>//GEN-END:initComponents

  private void titleTextFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_titleTextFocusLost
    if (titleText.getText().length() > 0) {
      urls.setTitle (titleText.getText());
    }
  }//GEN-LAST:event_titleTextFocusLost

  private void titleTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_titleTextActionPerformed
    if (titleText.getText().length() > 0) {
      urls.setTitle (titleText.getText());
    }
  }//GEN-LAST:event_titleTextActionPerformed

private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
  if (titleText.getText().length() > 0) {
    urls.setTitle (titleText.getText());
  }
  this.setVisible(false);
}//GEN-LAST:event_okButtonActionPerformed

private void formComponentHidden(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentHidden
  WindowMenuManager.getShared().hide(this);
}//GEN-LAST:event_formComponentHidden
  
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JLabel fileNameLabel;
  private javax.swing.JLabel fileNameText;
  private javax.swing.JLabel filler;
  private javax.swing.JLabel filler1;
  private javax.swing.JButton okButton;
  private javax.swing.JLabel titleLabel;
  private javax.swing.JTextField titleText;
  // End of variables declaration//GEN-END:variables
  
}
