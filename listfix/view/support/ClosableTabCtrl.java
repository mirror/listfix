/*
 * listFix() - Fix Broken Playlists!
 *
 * This file is part of listFix().
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, please see http://www.gnu.org/licenses/
 */

package listfix.view.support;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JTabbedPane;
import javax.swing.plaf.basic.BasicButtonUI;

public class ClosableTabCtrl extends javax.swing.JPanel
{
	private Component _comp = null;

    public ClosableTabCtrl(ICloseableTabManager tabMgr, JTabbedPane tabPane, String title)
    {
        _tabPane = tabPane;
        _tabMgr = tabMgr;
		// _comp = comp;
        initComponents();

        // need to set the title here but not the tooltip (tooltip setting messes up tab tooltip)
        jLabel1.setText(title);
        //jLabel1.setToolTipText(tooltip);
    }

    public void setText(String text)
    {
        jLabel1.setText(text);
    }
    
    JTabbedPane _tabPane;
    ICloseableTabManager _tabMgr;

    private JButton createTabButton()
    {
        return new TabButton();
    }

    private int getTabIx()
    {
        return _tabPane.indexOfTabComponent(this);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        rightClickMenu = new javax.swing.JPopupMenu();
        closeAllTabsMenuItem = new javax.swing.JMenuItem();
        closeAllOtherTabsMenuItem = new javax.swing.JMenuItem();
        _miRepairAllTabs = new javax.swing.JMenuItem();
        jLabel1 = new javax.swing.JLabel();
        jButton1 = createTabButton();

        closeAllTabsMenuItem.setText("Close All");
        closeAllTabsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeAllTabsMenuItemActionPerformed(evt);
            }
        });
        rightClickMenu.add(closeAllTabsMenuItem);

        closeAllOtherTabsMenuItem.setText("Close All Other Tabs");
        closeAllOtherTabsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeAllOtherTabsMenuItemActionPerformed(evt);
            }
        });
        rightClickMenu.add(closeAllOtherTabsMenuItem);

        _miRepairAllTabs.setText("Repair Open Playlists");
        _miRepairAllTabs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                _miRepairAllTabsActionPerformed(evt);
            }
        });
        rightClickMenu.add(_miRepairAllTabs);

        setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4));
        setFocusable(false);
        setMaximumSize(null);
        setMinimumSize(null);
        setOpaque(false);
        setPreferredSize(null);
        setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));

        jLabel1.setText("playlist");
        jLabel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 5));
        add(jLabel1);
        add(jButton1);
    }// </editor-fold>//GEN-END:initComponents

	private void closeAllTabsMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_closeAllTabsMenuItemActionPerformed
	{//GEN-HEADEREND:event_closeAllTabsMenuItemActionPerformed
		_tabMgr.tryCloseAllTabs();
	}//GEN-LAST:event_closeAllTabsMenuItemActionPerformed

	private void closeAllOtherTabsMenuItemActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_closeAllOtherTabsMenuItemActionPerformed
	{//GEN-HEADEREND:event_closeAllOtherTabsMenuItemActionPerformed
		_tabMgr.closeAllOtherTabs(getTabIx());
	}//GEN-LAST:event_closeAllOtherTabsMenuItemActionPerformed
	
	public void showRightClickMenu(int x, int y)
	{	  
		rightClickMenu.show(this, 5, 2);
	}
	
	public void closeMe()
	{
		_tabMgr.tryCloseTab(this);
	}
		
	private void _miRepairAllTabsActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event__miRepairAllTabsActionPerformed
	{//GEN-HEADEREND:event__miRepairAllTabsActionPerformed
		_tabMgr.repairAllTabs();
	}//GEN-LAST:event__miRepairAllTabsActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem _miRepairAllTabs;
    private javax.swing.JMenuItem closeAllOtherTabsMenuItem;
    private javax.swing.JMenuItem closeAllTabsMenuItem;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPopupMenu rightClickMenu;
    // End of variables declaration//GEN-END:variables

    // <editor-fold defaultstate="collapsed" desc="TabButton">
    /*
     * *** TabButton copied from Sun's TabComponentsDemo ***
     *
     * Copyright (c) 1995 - 2008 Sun Microsystems, Inc.  All rights reserved.
     *
     * Redistribution and use in source and binary forms, with or without
     * modification, are permitted provided that the following conditions
     * are met:
     *
     *   - Redistributions of source code must retain the above copyright
     *     notice, this list of conditions and the following disclaimer.
     *
     *   - Redistributions in binary form must reproduce the above copyright
     *     notice, this list of conditions and the following disclaimer in the
     *     documentation and/or other materials provided with the distribution.
     *
     *   - Neither the name of Sun Microsystems nor the names of its
     *     contributors may be used to endorse or promote products derived
     *     from this software without specific prior written permission.
     *
     * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
     * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
     * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
     * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
     * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
     * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
     * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
     * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
     * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
     * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
     * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
     */

    private class TabButton extends JButton implements ActionListener
    {
        public TabButton()
        {
            int size = 17;
            setPreferredSize(new Dimension(size, size));
            setToolTipText("close this tab");
            //Make the button looks the same for all Laf's
            setUI(new BasicButtonUI());
            //Make it transparent
            setContentAreaFilled(false);
            //No need to be focusable
            setFocusable(false);
            setBorder(BorderFactory.createEtchedBorder());
            setBorderPainted(false);
            //Making nice rollover effect
            //we use the same listener for all buttons
            addMouseListener(buttonMouseListener);
            setRolloverEnabled(true);
            //Close the proper tab by clicking the button
            addActionListener(this);
        }

        public void actionPerformed(ActionEvent e)
        {
            int tabIx = getTabIx();
            if (tabIx != -1)
                _tabMgr.tryCloseTab(ClosableTabCtrl.this);
                //_tabPane.remove(tabIx);
        }

        //we don't want to update UI for this button
        @Override
        public void updateUI()
        {
        }

        //paint the cross
        @Override
        protected void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            //shift the image for pressed buttons
            if (getModel().isPressed())
            {
                g2.translate(1, 1);
            }
            g2.setStroke(new BasicStroke(1.5f));
            g2.setColor(Color.BLACK);
            if (getModel().isRollover())
            {
                g2.setColor(Color.RED);
            }
            int delta = 5;
            g2.drawLine(delta, delta, getWidth() - delta - 1, getHeight() - delta - 1);
            g2.drawLine(getWidth() - delta - 1, delta, delta, getHeight() - delta - 1);
            g2.dispose();
        }
    }

    private final static MouseListener buttonMouseListener = new MouseAdapter()
    {
        @Override
        public void mouseEntered(MouseEvent e)
        {
            Component component = e.getComponent();
            if (component instanceof AbstractButton)
            {
                AbstractButton button = (AbstractButton) component;
                button.setBorderPainted(true);
            }
        }

        @Override
        public void mouseExited(MouseEvent e)
        {
            Component component = e.getComponent();
            if (component instanceof AbstractButton)
            {
                AbstractButton button = (AbstractButton) component;
                button.setBorderPainted(false);
            }
        }
    };
    // </editor-fold>

}
