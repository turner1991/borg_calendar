/*
This file is part of BORG.

    BORG is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    BORG is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with BORG; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

Copyright 2003 by ==Quiet==
*/
package net.sf.borg.ui;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Vector;

import javax.swing.DefaultListSelectionModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelEvent;

import net.sf.borg.common.ui.TableSorter;
import net.sf.borg.common.util.Errmsg;
import net.sf.borg.common.util.Resource;
import net.sf.borg.common.util.Version;
import net.sf.borg.model.Appointment;
import net.sf.borg.model.AppointmentModel;

/*
 * srchgui.java
 *
 */

/**
 *
 * @author  MBERGER
 */
// the srchgui shows the results of a calendar search
class SearchView extends View{
        static {
                Version.addVersion("$Id$");
        }
        
        private String s_;          // search string
        
        // searhc results do not refresh when the data model changes
        public void refresh(){}
        public void destroy(){ this.dispose(); }
        
        SearchView(String skey ) {
                super();
                
                addModel( AppointmentModel.getReference() );
                initComponents();
                s_ = skey;
                
                // show the search results as a 2 column sortable table
                // showing the appt date and text
                jTable1.setModel(new TableSorter(
                new String []{ Resource.getResourceString("Date"), Resource.getResourceString("Item") },
                new Class []{ Date.class,java.lang.String.class}));
                
                // do the search
                load();
                
        }
        
        
        private void load() {
                
                // call the data model to do the actual search 
                AppointmentModel cal_ = AppointmentModel.getReference();
                Vector res = cal_.get_srch(s_);
                
                TableSorter tm = (TableSorter) jTable1.getModel();
                tm.addMouseListenerToHeaderInTable(jTable1);
                tm.setRowCount(0);
                
                // load the search results into the table
                for( int i = 0; i < res.size(); i++ ) {
                        Object [] ro = new Object[2];
                        
                        // get a single appt row
                        Appointment r = (Appointment) res.elementAt(i);
                        try{
                                // get the date and text for the table
                                ro[0] = r.getDate();
                                ro[1] = r.getText();
                        }
                        catch( Exception e ) {
                                Errmsg.errmsg(e);
                                return;
                        }
                        
                        // load the appt into the table
                        tm.addRow(ro);
                        tm.tableChanged(new TableModelEvent(tm));
                }
                
                // sort the table by date
                tm.sortByColumn(0);
                
        }
        
        /** This method is called from within the constructor to
         * initialize the form.
         * WARNING: Do NOT modify this code. The content of this method is
         * always regenerated by the Form Editor.
         */
    private void initComponents()//GEN-BEGIN:initComponents
    {
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        exitMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(java.util.ResourceBundle.getBundle("resource/borg_resource").getString("Search_Results"));
        addWindowListener(new java.awt.event.WindowAdapter()
        {
            public void windowClosing(java.awt.event.WindowEvent evt)
            {
                exitForm(evt);
            }
        });

        jTable1.setBorder(new javax.swing.border.EtchedBorder());
        jTable1.setGridColor(java.awt.Color.blue);
        DefaultListSelectionModel mylsmodel = new DefaultListSelectionModel();
        mylsmodel.setSelectionMode( ListSelectionModel.SINGLE_SELECTION);
        jTable1.setSelectionModel(mylsmodel
        );
        jTable1.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                jTable1MouseClicked(evt);
            }
        });

        jScrollPane1.setViewportView(jTable1);

        getContentPane().add(jScrollPane1, java.awt.BorderLayout.CENTER);

        fileMenu.setText(java.util.ResourceBundle.getBundle("resource/borg_resource").getString("Action"));
        exitMenuItem.setText(java.util.ResourceBundle.getBundle("resource/borg_resource").getString("Exit"));
        exitMenuItem.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                exitMenuItemActionPerformed(evt);
            }
        });

        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        setJMenuBar(menuBar);

        pack();
    }//GEN-END:initComponents

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
      // ask controller to bring up appt editor on double click
      if( evt.getClickCount() < 2 ) return;
      
      // get task number from column 0 of selected row
      int row = jTable1.getSelectedRow();
      if( row == -1 ) return;
      TableSorter tm = (TableSorter) jTable1.getModel();
      Date d = (Date) tm.getValueAt(row,0);
      
      GregorianCalendar cal = new GregorianCalendar();
      cal.setTime(d);
      
      //bring up an appt editor window
      AppointmentListView ag = new AppointmentListView(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));
      ag.show();
      
      CalendarView cv = CalendarView.getReference();
      if( cv != null ) cv.goTo( cal );
      
    }//GEN-LAST:event_jTable1MouseClicked
    
    
    private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuItemActionPerformed
            this.dispose();
    }//GEN-LAST:event_exitMenuItemActionPerformed
    
    private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
            this.dispose();
    }//GEN-LAST:event_exitForm
    

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JMenuBar menuBar;
    // End of variables declaration//GEN-END:variables
    
}
