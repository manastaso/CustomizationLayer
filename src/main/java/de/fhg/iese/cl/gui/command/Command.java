package de.fhg.iese.cl.gui.command;

import java.util.ArrayList;
import java.util.logging.Logger;

import de.fhg.iese.cl.CustomizationLayer;
import de.fhg.iese.cl.gui.ConsoleGUI;
import de.fhg.iese.cl.parser.Token;

/**
 * This is the abstract Command class
 * All CL commands are inheriting from this class
 * @author anastaso
 *
 */
public abstract class Command extends Thread
{
	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	protected ConsoleGUI gui;
	protected CustomizationLayer cl;
	protected String commandName;
	protected Token[] arguments;
	protected volatile boolean stop = false;
	protected ArrayList<String> outputList;
	private String outputToUI;
	
	private Logger logger = null;
	
	protected CommandBlackboard blackboard=null;

	public Command(ConsoleGUI gui, CustomizationLayer cl, Token[] arguments) {
		super("Command Thread");
		this.gui = gui;
		this.cl = cl;
		this.arguments = arguments;
		blackboard = new CommandBlackboard();
	}
	
	abstract public void execute();
	
	abstract public String getCommandName();
	
	public String toString() {
		String result=getCommandName();
		for (int i = 0; i < arguments.length; i++) {
			if (arguments[i] != null) 
				if (arguments[i].image != null)
					result += " " + arguments[i];
		}
		return result;
	}
	
	@Override
	public void run() {
		execute();
	}

	abstract public void undo();
	
	 public void requestStop() {
		 stop = true;
		 blackboard.setMessage("Stop requested");
	 }
	
	protected void outputToUI(String output) {
		if (output != null)
			output = output.replaceAll("\n", System.getProperty("line.separator"));
		outputToUI = output;
		if (!gui.getDisplay().isDisposed())
			gui.getDisplay().syncExec(new Runnable() {
				public void run() {
					String msg="[" + getCommandName() + "] " + outputToUI;
					gui.println(msg);
					if (logger != null)
						logger.info(msg);
				}
			});
		else
			System.out.println(output);
	}
	
}
