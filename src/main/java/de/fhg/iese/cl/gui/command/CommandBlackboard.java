package de.fhg.iese.cl.gui.command;

import java.util.Observable;

/**
 * This class enables the processing of Ctrl-C actions out of a commands
 * It finally leads to stopping the execution of a command
 * @author anastaso
 *
 */
public class CommandBlackboard extends Observable {
	
	String message=null;

	public void setMessage(String msg) {
		this.message = msg;
		setChanged();
		notifyObservers();
	}

}
