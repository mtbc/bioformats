/*
 * org.openmicroscopy.shoola.agents.hiviewer.actions.RemoveAction
 *
 *------------------------------------------------------------------------------
 *
 *  Copyright (C) 2004 Open Microscopy Environment
 *      Massachusetts Institute of Technology,
 *      National Institutes of Health,
 *      University of Dundee
 *
 *
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with this library; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *------------------------------------------------------------------------------
 */

package org.openmicroscopy.shoola.agents.hiviewer.actions;



//Java imports
import java.awt.event.ActionEvent;
import java.util.Set;
import javax.swing.Action;

//Third-party libraries

//Application-internal dependencies
import org.openmicroscopy.shoola.agents.hiviewer.IconManager;
import org.openmicroscopy.shoola.agents.hiviewer.browser.ImageDisplay;
import org.openmicroscopy.shoola.agents.hiviewer.cmd.RemoveCmd;
import org.openmicroscopy.shoola.agents.hiviewer.view.HiViewer;
import org.openmicroscopy.shoola.util.ui.UIUtilities;
import pojos.CategoryData;
import pojos.CategoryGroupData;
import pojos.DataObject;
import pojos.DatasetData;
import pojos.ImageData;
import pojos.ProjectData;

/** 
 * Action to remove the selected nodes.
 *
 * @author  Jean-Marie Burel &nbsp;&nbsp;&nbsp;&nbsp;
 * 				<a href="mailto:j.burel@dundee.ac.uk">j.burel@dundee.ac.uk</a>
 * @author	Donald MacDonald &nbsp;&nbsp;&nbsp;&nbsp;
 * 	<a href="mailto:donald@lifesci.dundee.ac.uk">donald@lifesci.dundee.ac.uk</a>
 * @version 3.0
 * <small>
 * (<b>Internal version:</b> $Revision: $ $Date: $)
 * </small>
 * @since OME2.2
 */
public class RemoveAction
    extends HiViewerAction
{

    /** Name of the action. */
    private static final String NAME = "Remove";
    
    /** Name of the action if the selected items are Datasets. */
    private static final String NAME_PROJECT = "Remove from current project";
    
    /** Name of the action if the selected items are Datasets. */
    private static final String NAME_CATEGORYGROUP = 
            "Remove from current categoryGroup";
    
    /** 
     * Name of the action if the selected items are images and 
     * the parent is a dataset.
     */
    private static final String NAME_DATASET = "Remove from current dataset";
    
    /** 
     * Name of the action if the selected items are images and
     * the parent is a dataset. 
     */
    private static final String NAME_CATEGORY = "Remove from current category";
    
    
    /** Description of the action. */
    private static final String DESCRIPTION = "Remove the selected element " +
            "from the current container.";
    
    /**
     * Sets the action enabled depending on the currently selected display
     * @see HiViewerAction#onDisplayChange(ImageDisplay)
     */
    protected void onDisplayChange(ImageDisplay selectedDisplay)
    {
        if (selectedDisplay.getParentDisplay() == null) setEnabled(false);
        else {
            Set nodes = model.getBrowser().getSelectedDisplays();
            if (nodes.size() > 1) setEnabled(false);
            else {
                Object ho = selectedDisplay.getHierarchyObject();
                ImageDisplay parent = selectedDisplay.getParentDisplay();
                Object po = parent.getHierarchyObject();
                if ((ho instanceof ProjectData) ||
                        (ho instanceof CategoryGroupData)) {
                    putValue(Action.NAME, NAME);
                    setEnabled(model.isObjectWritable((DataObject) ho));
                } else if (ho instanceof DatasetData) {
                    putValue(Action.NAME, NAME_PROJECT);
                    if (po instanceof String) setEnabled(false); //root
                    else setEnabled(model.isObjectWritable((DataObject) ho));
                } else if (ho instanceof CategoryData) {
                    putValue(Action.NAME, NAME_CATEGORYGROUP);
                    if (po instanceof String) setEnabled(false); //root
                    else setEnabled(model.isObjectWritable((DataObject) ho));
                } else if (ho instanceof ImageData) {
                    if (po instanceof DatasetData) 
                        putValue(Action.NAME, NAME_DATASET);
                    else putValue(Action.NAME, NAME_CATEGORY);
                    if (po instanceof String) setEnabled(false); //root
                    else setEnabled(model.isObjectWritable((DataObject) ho));
                } else setEnabled(false);
            }
        }
    }
    
    /**
     * Creates a new instance.
     * 
     * @param model Reference to the Model. Mustn't be <code>null</code>.
     */
    public RemoveAction(HiViewer model)
    {
        super(model);
        putValue(Action.NAME, NAME);
        putValue(Action.SHORT_DESCRIPTION, 
                UIUtilities.formatToolTipText(DESCRIPTION));
        IconManager icons = IconManager.getInstance();
        putValue(Action.SMALL_ICON, icons.getIcon(IconManager.DELETE));
        
    }
    
    /**
     * Creates a {@link RemoveCmd} to execute the action.
     * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
     */
    public void actionPerformed(ActionEvent e)
    {
        RemoveCmd cmd = new RemoveCmd(model);
        cmd.execute();
    }
    
}
