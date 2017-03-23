package de.fhg.iese.cl.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class UserConfirmationThread implements Runnable {
	
	boolean userConfirmed=false;
	String message=null;
	Shell shell=null;

	public void run() {
		MessageBox messageBox = 
			  new MessageBox(shell, SWT.YES|SWT.NO|SWT.ICON_QUESTION);
		messageBox.setMessage(message);
		messageBox.setText("Customization Layer");
			if (messageBox.open() == SWT.YES)
			{
				userConfirmed=true;
			}
			else
				userConfirmed=false;
	}

	public UserConfirmationThread(Shell shell, String message) {
		super();
		this.message = message;
		this.shell=shell;
	}

}
