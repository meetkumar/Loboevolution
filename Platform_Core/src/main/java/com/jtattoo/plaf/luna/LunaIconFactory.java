/*
* Copyright (c) 2002 and later by MH Software-Entwicklung. All Rights Reserved.
*  
* JTattoo is multiple licensed. If your are an open source developer you can use
* it under the terms and conditions of the GNU General Public License version 2.0
* or later as published by the Free Software Foundation.
*  
* see: gpl-2.0.txt
* 
* If you pay for a license you will become a registered user who could use the
* software under the terms and conditions of the GNU Lesser General Public License
* version 2.0 or later with classpath exception as published by the Free Software
* Foundation.
* 
* see: lgpl-2.0.txt
* see: classpath-exception.txt
* 
* Registered users could also use JTattoo under the terms and conditions of the 
* Apache License, Version 2.0 as published by the Apache Software Foundation.
*  
* see: APACHE-LICENSE-2.0.txt
*/
 
package com.jtattoo.plaf.luna;

import com.jtattoo.plaf.AbstractIconFactory;
import javax.swing.Icon;

/**
 * A factory for creating LunaIcon objects.
 *
 * @author Michael Hagen
 */
public class LunaIconFactory implements AbstractIconFactory {

    /** The instance. */
    private static LunaIconFactory instance = null;

    private LunaIconFactory() {
    }

    /** Gets the single instance of LunaIconFactory.
	 *
	 * @return single instance of LunaIconFactory
	 */
    public static synchronized LunaIconFactory getInstance() {
        if (instance == null) {
            instance = new LunaIconFactory();
        }
        return instance;
    }

    public Icon getOptionPaneErrorIcon() {
        return LunaIcons.getOptionPaneErrorIcon();
    }

    public Icon getOptionPaneWarningIcon() {
        return LunaIcons.getOptionPaneWarningIcon();
    }

    public Icon getOptionPaneInformationIcon() {
        return LunaIcons.getOptionPaneInformationIcon();
    }

    public Icon getOptionPaneQuestionIcon() {
        return LunaIcons.getOptionPaneQuestionIcon();
    }

    public Icon getFileChooserUpFolderIcon() {
        return LunaIcons.getFileChooserUpFolderIcon();
    }

    public Icon getFileChooserHomeFolderIcon() {
        return LunaIcons.getFileChooserHomeFolderIcon();
    }

    public Icon getFileChooserNewFolderIcon() {
        return LunaIcons.getFileChooserNewFolderIcon();
    }

    public Icon getFileChooserListViewIcon() {
        return LunaIcons.getFileChooserListViewIcon();
    }

    public Icon getFileChooserDetailViewIcon() {
        return LunaIcons.getFileChooserDetailViewIcon();
    }

    public Icon getFileViewComputerIcon() {
        return LunaIcons.getFileViewComputerIcon();
    }

    public Icon getFileViewFloppyDriveIcon() {
        return LunaIcons.getFileViewFloppyDriveIcon();
    }

    public Icon getFileViewHardDriveIcon() {
        return LunaIcons.getFileViewHardDriveIcon();
    }

    public Icon getMenuIcon() {
        return LunaIcons.getMenuIcon();
    }

    public Icon getIconIcon() {
        return LunaIcons.getIconIcon();
    }

    public Icon getMaxIcon() {
        return LunaIcons.getMaxIcon();
    }

    public Icon getMinIcon() {
        return LunaIcons.getMinIcon();
    }

    public Icon getCloseIcon() {
        return LunaIcons.getCloseIcon();
    }

    public Icon getPaletteCloseIcon() {
        return LunaIcons.getPaletteCloseIcon();
    }

    public Icon getRadioButtonIcon() {
        return LunaIcons.getRadioButtonIcon();
    }

    public Icon getCheckBoxIcon() {
        return LunaIcons.getCheckBoxIcon();
    }

    public Icon getComboBoxIcon() {
        return LunaIcons.getComboBoxIcon();
    }

    public Icon getTreeOpenIcon() {
        return LunaIcons.getTreeOpenedIcon();
    }

    public Icon getTreeCloseIcon() {
        return LunaIcons.getTreeClosedIcon();
    }

    public Icon getTreeLeafIcon() {
        return LunaIcons.getTreeLeafIcon();
    }

    public Icon getTreeCollapsedIcon() {
        return LunaIcons.getTreeCollapsedIcon();
    }

    public Icon getTreeExpandedIcon() {
        return LunaIcons.getTreeExpandedIcon();
    }

    public Icon getMenuArrowIcon() {
        return LunaIcons.getMenuArrowIcon();
    }

    public Icon getMenuCheckBoxIcon() {
        return LunaIcons.getMenuCheckBoxIcon();
    }

    public Icon getMenuRadioButtonIcon() {
        return LunaIcons.getMenuRadioButtonIcon();
    }

    public Icon getUpArrowIcon() {
        return LunaIcons.getUpArrowIcon();
    }

    public Icon getDownArrowIcon() {
        return LunaIcons.getDownArrowIcon();
    }

    public Icon getLeftArrowIcon() {
        return LunaIcons.getLeftArrowIcon();
    }

    public Icon getRightArrowIcon() {
        return LunaIcons.getRightArrowIcon();
    }

    public Icon getSplitterDownArrowIcon() {
        return LunaIcons.getSplitterDownArrowIcon();
    }

    public Icon getSplitterHorBumpIcon() {
        return LunaIcons.getSplitterHorBumpIcon();
    }

    public Icon getSplitterLeftArrowIcon() {
        return LunaIcons.getSplitterLeftArrowIcon();
    }

    public Icon getSplitterRightArrowIcon() {
        return LunaIcons.getSplitterRightArrowIcon();
    }

    public Icon getSplitterUpArrowIcon() {
        return LunaIcons.getSplitterUpArrowIcon();
    }

    public Icon getSplitterVerBumpIcon() {
        return LunaIcons.getSplitterVerBumpIcon();
    }

    public Icon getThumbHorIcon() {
        return LunaIcons.getThumbHorIcon();
    }

    public Icon getThumbVerIcon() {
        return LunaIcons.getThumbVerIcon();
    }

    public Icon getThumbHorIconRollover() {
        return LunaIcons.getThumbHorIconRollover();
    }

    public Icon getThumbVerIconRollover() {
        return LunaIcons.getThumbVerIconRollover();
    }
}
