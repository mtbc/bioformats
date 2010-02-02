/*
 * org.openmicroscopy.shoola.env.ui.UserNotifierImpl
 *
 *------------------------------------------------------------------------------
 *  Copyright (C) 2006 University of Dundee. All rights reserved.
 *
 *
 * 	This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License along
 *  with this program; if not, write to the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *------------------------------------------------------------------------------
 */

package org.openmicroscopy.shoola.env.ui;

//Java imports
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.Icon;
import javax.swing.JFrame;

//Third-party libraries

//Application-internal dependencies
import org.openmicroscopy.shoola.env.Container;
import org.openmicroscopy.shoola.env.data.model.AnalysisActivityParam;
import org.openmicroscopy.shoola.env.data.model.ApplicationData;
import org.openmicroscopy.shoola.env.data.model.DownloadActivityParam;
import org.openmicroscopy.shoola.env.data.model.ExportActivityParam;
import org.openmicroscopy.shoola.env.data.model.FigureActivityParam;
import org.openmicroscopy.shoola.env.data.model.MovieActivityParam;
import org.openmicroscopy.shoola.env.data.model.ScriptObject;
import org.openmicroscopy.shoola.util.ui.MessengerDialog;
import org.openmicroscopy.shoola.util.ui.NotificationDialog;
import org.openmicroscopy.shoola.util.ui.UIUtilities;
import pojos.ExperimenterData;

/** 
 * Implements the {@link UserNotifier} interface. 
 *
 * @author  Jean-Marie Burel &nbsp;&nbsp;&nbsp;&nbsp;
 *              <a href="mailto:j.burel@dundee.ac.uk">j.burel@dundee.ac.uk</a>
 * @author  Andrea Falconi &nbsp;&nbsp;&nbsp;&nbsp;
 *              <a href="mailto:a.falconi@dundee.ac.uk">
 *              a.falconi@dundee.ac.uk</a>
 * @version 2.2 
 * <small>
 * (<b>Internal version:</b> $Revision$ $Date$)
 * </small>
 * @since OME2.2
 */
public class UserNotifierImpl 
    implements UserNotifier
{

    /** Default title for the error dialog. */
    private static final String     DEFAULT_ERROR_TITLE = "Error";
 
	/** Default title for the warning dialog. */	
	private static final String     DEFAULT_WARNING_TITLE = "Warning";
											
	/** Default title for the info dialog. */														    
    private static final String     DEFAULT_INFO_TITLE = "Information";
    
    /**
     * This is the common parent frame that we use to build every notification
     * dialog.
     * We don't use the one already provided by <i>Swing</i> because we need
     * to set the <i>OME</i> icon in the title bar, so that notification dialogs
     * can inherit it.
     */
    private static JFrame			SHARED_FRAME = null;
	
    /** Reference to the manager. */
    private UserNotifierManager		manager;
    
    /**
	 * Utility method to print the error message
	 * 
	 * @param e The exception to handle.
	 * @return  See above.
	 */
	private String printErrorText(Throwable e) 
	{
		if (e == null) return "";
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		return sw.toString();
	}
	
    /**
     * Brings up a notification dialog.
     * 
     * @param title     The dialog title.
     * @param message   The dialog message.
     * @param icon      The icon to show by the message.
     */
    private void showNotificationDialog(String title, String message, Icon icon)
    {
        NotificationDialog dialog = new NotificationDialog(
                                                SHARED_FRAME, title, message, 
                                                icon);
        dialog.pack();  
        UIUtilities.centerAndShow(dialog);
    }
    
    /**
     * Returns the e-mail address of the user currently logged in, if 
     * no address specified.
     * 
     * @param email The address to check.
     * @return See above.
     */
    private String getEmail(String email)
    {
    	if (email != null && email.trim().length() != 0) return email;
    	ExperimenterData exp = manager.getExperimenter();
		if (exp != null) email = exp.getEmail();
		if (email == null) email = "";
		return email;
    }
    
    /**
     * Brings up a messenger dialog.
     * 
     * @param title		The dialog title.
     * @param summary	The dialog message.
     * @param detail	The detailed error message.
     * @param email		The e-mail address of the user.
     * @param toSubmit 	The version of the software.
     */
    private void showErrorDialog(String title, String summary, String detail, 
    		String email)
    {
    	Exception e;
    	if (detail == null) e = new Exception(summary);
    	else e = new Exception(detail);
    	if (title == null || title.length() == 0) 
    		title = DEFAULT_ERROR_TITLE;
    	MessengerDialog d = new MessengerDialog(SHARED_FRAME, title, 
    			getEmail(email), e); 
    	d.setServerVersion(manager.getServerVersion());
    	d.addPropertyChangeListener(manager);
    	d.setModal(true);
    	UIUtilities.centerAndShow(d);
    }
    
    /** 
     * Creates a new instance. 
     * @param c	Reference to the singleton {@link Container}.
     */
    UserNotifierImpl(Container c)
    {
    	manager = new UserNotifierManager(this, c);
    	if (SHARED_FRAME == null) {
			SHARED_FRAME = new JFrame();
			SHARED_FRAME.setIconImage(AbstractIconManager.getOMEImageIcon());
    	}
    }
    
	/** 
     * Implemented as specified by {@link UserNotifier}. 
     * @see UserNotifier#notifyError(String, String)
     */     
	public void notifyError(String title, String summary)
	{
		if (title == null || title.length() == 0) title = DEFAULT_ERROR_TITLE;
		showNotificationDialog(title, summary, 
								IconManager.getDefaultErrorIcon());
	}
	
	/** 
     * Implemented as specified by {@link UserNotifier}. 
     * @see UserNotifier#notifyError(String, String, Throwable)
     */       
    public void notifyError(String title, String summary, Throwable detail)
    {
		notifyError(title, summary, 
						detail == null ? null : printErrorText(detail));
    }

	/** 
     * Implemented as specified by {@link UserNotifier}. 
     * @see UserNotifier#notifyError(String, String, String, Map)
     */     
	public void notifyError(String title, String summary, String email,
			Map toSubmit)
	{
		if (title == null || title.length() == 0) 
			title = DEFAULT_ERROR_TITLE;
    	if (email == null) email = "";
    	MessengerDialog d = new MessengerDialog(SHARED_FRAME, title, 
    			getEmail(email), toSubmit); 
    	d.setServerVersion(manager.getServerVersion());
    	d.addPropertyChangeListener(manager);
    	d.setModal(true);
    	UIUtilities.centerAndShow(d);
	}

	/** 
     * Implemented as specified by {@link UserNotifier}. 
     * @see UserNotifier#notifyError(String, String, String)
     */     
	public void notifyError(String title, String summary, String detail)
	{
		showErrorDialog(title, summary, detail, null);
	}
    
	/**
     * Implemented as specified by {@link UserNotifier}. 
     * @see UserNotifier#notifyWarning(String, String)
     */ 
	public void notifyWarning(String title, String message)
	{
		if (title == null || title.length() == 0)
			title = DEFAULT_WARNING_TITLE;
		showNotificationDialog(title, message, 
								IconManager.getDefaultWarnIcon());
	}
	
	/** 
     * Implemented as specified by {@link UserNotifier}. 
     * @see UserNotifier#notifyWarning(String, String, String)
     */ 
	public void notifyWarning(String title, String summary, String detail) 
	{
		if (title == null || title.length() == 0)
			title = DEFAULT_WARNING_TITLE;
		showErrorDialog(title, summary, detail, null);
	}

	/** 
     * Implemented as specified by {@link UserNotifier}. 
     * @see UserNotifier#notifyWarning(String, String, Throwable)
     */ 
	public void notifyWarning(String title, String summary, Throwable detail) 
	{
		notifyWarning(title, summary, 
						detail == null ? null : detail.getMessage());
	}

	/** 
     * Implemented as specified by {@link UserNotifier}. 
     * @see UserNotifier#notifyInfo(String, String)
     */ 
	public void notifyInfo(String title, String message)
	{  
		if (title == null || title.length() == 0)
			title = DEFAULT_INFO_TITLE;
		showNotificationDialog(title, message, 
								IconManager.getDefaultInfoIcon());
	}
    
    /** 
     * Implemented as specified by {@link UserNotifier}. 
     * @see UserNotifier#notifyInfo(String, String, Icon)
     */ 
    public void notifyInfo(String title, String message, Icon icon)
    {  
        if (title == null || title.length() == 0)
            title = DEFAULT_INFO_TITLE;
        if (icon == null) icon = IconManager.getDefaultInfoIcon();
        showNotificationDialog(title, message, icon);
    }

    /** 
     * Implemented as specified by {@link UserNotifier}. 
     * @see UserNotifier#submitMessage(String)
     */ 
	public void submitMessage(String email)
	{
		MessengerDialog d = manager.getCommentDialog(SHARED_FRAME, 
				getEmail(email));
		UIUtilities.centerAndShow(d);
	}

	/** 
	 * Implemented as specified by {@link UserNotifier}. 
	 * @see UserNotifier#notifyActivity(Object)
	 */ 
	public void notifyActivity(Object activity)
	{
		if (activity == null) return;
		ActivityComponent comp = null;
		boolean register = true;
		if (activity instanceof MovieActivityParam) {
			MovieActivityParam p = (MovieActivityParam) activity;
			comp = new MovieActivity(this, manager.getRegistry(), p);
		} else if (activity instanceof ExportActivityParam) {
			ExportActivityParam p = (ExportActivityParam) activity;
			comp = new ExportActivity(this, manager.getRegistry(), p);
		} else if (activity instanceof DownloadActivityParam) {
			DownloadActivityParam p = (DownloadActivityParam) activity;
			register = (p.getApplicationData() == null);
			comp = new DownloadActivity(this, manager.getRegistry(), p);
		} else if (activity instanceof FigureActivityParam) {
			FigureActivityParam p = (FigureActivityParam) activity;
			comp = new FigureActivity(this, manager.getRegistry(), p);
		} else if (activity instanceof AnalysisActivityParam) {
			AnalysisActivityParam p = (AnalysisActivityParam) activity;
			comp = new AnalysisActivity(this, manager.getRegistry(), p);
		} else if (activity instanceof ScriptObject) {
			ScriptObject script = (ScriptObject) activity;
			int index = ScriptActivity.UPLOAD;
			if (script.getScriptID() > 0) index = ScriptActivity.RUN; 
			comp = new ScriptActivity(this, manager.getRegistry(),
					script, index);
		}
		if (comp != null) {
			UserNotifierLoader loader = comp.createLoader();
			if (loader == null) return;
			if (register) {
				manager.registerActivity(comp);
				comp.startActivity();
			}
			loader.load();
		}
	}
	
	/** 
	 * Implemented as specified by {@link UserNotifier}. 
	 * @see UserNotifier#notifyActivity(Object)
	 */ 
	public void openApplication(ApplicationData data, String path)
	{
		if (data == null && path == null) return;
		Runtime run = Runtime.getRuntime();
		try {
			String[] values;
			/*
			if (data == null) {
				Ap
				values = new String[2];
				values[0] = "open";
				values[1] = path;
			} else {
				List<String> l = data.getArguments();
				Iterator<String> i = l.iterator();
				int index = 0;
				if (path == null || path.length() == 0) {
					values = new String[l.size()];
					while (i.hasNext()) {
						values[index] = i.next();
						index++;
					}
				} else {
					values = new String[l.size()+1];
					while (i.hasNext()) {
						values[index] = i.next();
						index++;
					}
					values[index] = path;
				}
			}
			*/
			if (data == null) data = new ApplicationData("");
			List<String> l = data.getArguments();
			Iterator<String> i = l.iterator();
			int index = 0;
			if (path == null || path.length() == 0) {
				values = new String[l.size()];
				while (i.hasNext()) {
					values[index] = i.next();
					index++;
				}
			} else {
				values = new String[l.size()+1];
				while (i.hasNext()) {
					values[index] = i.next();
					index++;
				}
				values[index] = path;
			}
			run.exec(values);
		} catch (Exception e) {
			
		}
	}
	
	/** Displays the activity. */
	void showActivity() { manager.showActivity(); }

}
