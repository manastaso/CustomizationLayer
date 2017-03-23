package de.fhg.iese.cl.gui;
//test

import java.io.File;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

import org.eclipse.swt.events.*;

import de.fhg.iese.cl.CustomizationLayer;
import de.fhg.iese.cl.gui.command.AddCoreAssetCommand;
import de.fhg.iese.cl.gui.command.Command;
import de.fhg.iese.cl.gui.command.CommandMap;
import de.fhg.iese.cl.gui.command.CommandSet;
import de.fhg.iese.cl.gui.command.HelpCommand;
import de.fhg.iese.cl.gui.command.InstantiateCoreAssetCommand;
import de.fhg.iese.cl.gui.command.IntegrateCommand;
import de.fhg.iese.cl.gui.command.RebaseCommand;
import de.fhg.iese.cl.gui.command.SetupCommand;
import de.fhg.iese.cl.gui.command.ShowCoreAssetsCommand;
import de.fhg.iese.cl.gui.command.ShowCoreDiffCommand;
import de.fhg.iese.cl.gui.command.ShowInstanceDiffCommand;
import de.fhg.iese.cl.gui.command.ShowInstancesCommand;
import de.fhg.iese.cl.gui.command.ShowPropertiesCommand;
import de.fhg.iese.cl.gui.command.SetPropertiesCommand;
import de.fhg.iese.cl.parser.CommandInterpreter;
import de.fhg.iese.cl.parser.CommandLine;
import de.fhg.iese.cl.parser.ParseException;
import de.fhg.iese.cl.parser.Token;
import de.fhg.iese.cl.parser.TokenMgrError;

/**
 * Main Controller class
 * It handles all user actions and triggers CL accordingly.
 * @author anastaso
 *
 */
public class Controller implements ShellListener, KeyListener, TraverseListener {
	
	private ConsoleGUI gui;
	private CustomizationLayer cl = null;
	
	//private field for the auto completion
	private int tabCounter = 0;
	private String partialCommmandString=null;
	private Enumeration<String> possibleCommands=null;
	
	//private fields for the history navigation
	private int arrowCounter = 0;
	private LinkedList<Command> history = new LinkedList<Command>();
	private int historyIndex=0;
	private Command activeCommand;
	
	//private fields for logging
	private Logger logger = null;
	private File logFile = null;
	private FileOutputStream logStream =null;

	public Controller(ConsoleGUI consoleGUI) {
		gui = consoleGUI;
		cl = new CustomizationLayer(gui);
		
		Token[] arguments = { new Token() };
		Command setup = new SetupCommand(gui,cl,arguments);
		setup.start();
		
		setupLogging();
	}

	private Token[] clearFromQuotes(Token[] arguments) {
		Token[] tokens = arguments;
		for (int i = 0; i < tokens.length; i++) {
			if (tokens[i] != null)
			{
			String image = tokens[i].image;
			image = image.replaceAll("\"", "");
			tokens[i].image = image;
			}
		}
		return tokens;
	}

	public void dispose() {
		for (Iterator<Command> iterator = history.iterator(); iterator.hasNext();) {
			Command c = (Command) iterator.next();
			c.requestStop();
		}
		if (cl!= null) 
			cl.finalize();
	}

	private void echoInput(String input) {
		gui.echoInput(this, input);
	}

	private void handleArrowKey(KeyEvent e) {
		e.doit = false;
		Command c=null;
		arrowCounter += 1;
		
		if (arrowCounter == 1) {
			historyIndex = history.size();
			if (historyIndex == 0) {
				arrowCounter = 0;
				return;
			}
		}
		
		switch (e.keyCode) {
		case 16777217: //up
			if (historyIndex > 0)
				historyIndex -= 1;
			break;
		case 16777218: //down
			if (historyIndex < history.size()-1)
				historyIndex += 1;
			break;
		default:
			break;
		} 
		c = history.get(historyIndex);
		gui.setInput(c.toString());
	}

	private void issueCommand(CommandLine commandLine) {
		log(commandLine);
		
		Integer commandID;
		Command command=null;
		
		//get the corresponding command ID (constant)
		commandID = CommandMap.map.get(commandLine.command.toString());
		
		Token[] clearedArguments = clearFromQuotes(commandLine.arguments);

		switch (commandID) {
		case CommandSet.HELP:
			command = new HelpCommand(gui,cl,clearedArguments);
			break;
		case CommandSet.SET_PROPERTIES:
			command = new SetPropertiesCommand(gui,cl,clearedArguments);
			break;
		case CommandSet.ADD_CORE_ASSET:
			command = new AddCoreAssetCommand(gui,cl,clearedArguments);
			break;
		case CommandSet.SHOW_CORE_ASSETS:
			command = new ShowCoreAssetsCommand(gui,cl,clearedArguments);
			break;
		case CommandSet.SHOW_INSTANCES:
			command = new ShowInstancesCommand(gui,cl,clearedArguments);
			break;
		case CommandSet.INSTANTIATE_CORE_ASSET:
			command = new InstantiateCoreAssetCommand(gui,cl,clearedArguments);
			break;
		case CommandSet.SHOW_INSTANCE_DIFF:
			command = new ShowInstanceDiffCommand(gui,cl,clearedArguments);
			break;
		case CommandSet.SHOW_CORE_ASSET_DIFF:
			command = new ShowCoreDiffCommand(gui,cl,clearedArguments);
			break;
		case CommandSet.SHOW_PROPERTIES:
			command = new ShowPropertiesCommand(gui,cl,clearedArguments);
			break;
		case CommandSet.REBASE:
			command = new RebaseCommand(gui,cl,clearedArguments);
			break;
		case CommandSet.INTEGRATE:
			command = new IntegrateCommand(gui,cl,clearedArguments);
			break;
		case CommandSet.CLEAR:
			gui.clear();
			break;
		case CommandSet.EXIT:
			gui.close();
			break;
		default:
			break;
		}
		if ((command != null) && !(command instanceof SetupCommand))
		{
			activeCommand = command;
			history.add(command);
			command.setLogger(logger);
			command.start();
		}

	}

	public void keyPressed(KeyEvent e) {		
		switch (e.keyCode) {
		case 8: //pressing the backspace key
			if (tabCounter > 0) {
				String currentString = gui.getInputText().getText();
				if (currentString.length()-1 < partialCommmandString.length())
					tabCounter = 0;	
			}
			break;
		case 16777217: //pressing the up arrow
			handleArrowKey(e);
			break;
		case 16777218: //pressing the down arrow
			handleArrowKey(e);
			break;
		case 27: //pressing ESC
			gui.setInput("");
			break;
		case 99: //pressing the C key
			if (e.stateMask == 262144) // pressing Ctrl-C
				activeCommand.requestStop();
			break;
		default:
			break;
		}
				
	}

	public void keyReleased(KeyEvent e) {
		String input = null;
		StringReader reader=null;
		CommandInterpreter myInterpreter = null;
		CommandLine commandLine=null;
		
		//if return button was pressed we get the current input
		if (e.keyCode == 13) {
			tabCounter = 0;
			arrowCounter = 0;
			input = gui.getInputText().getText();
			gui.getInputText().setText("");
			reader = new StringReader(input);

			//echo the input to the output text area
			echoInput(input);
			
			myInterpreter = new CommandInterpreter(reader);
			try {
				commandLine = myInterpreter.specification();
				issueCommand(commandLine);
			} catch (ParseException e1) {
				gui.println(e1.getMessage());
			}
			  catch (TokenMgrError e2) {
				  gui.println(e2.getMessage());
			  }
		}
		
	}
	public void keyTraversed(TraverseEvent e) {
		e.doit = false;
		switch (e.keyCode) {
		case 9: //pressing the tab key
			tabCounter += 1;
			if (tabCounter == 1) {
				possibleCommands = CommandMap.map.keys();
				partialCommmandString = gui.getInputText().getText();
			}
			if ((tabCounter > 1) && (!possibleCommands.hasMoreElements())) {
				possibleCommands = CommandMap.map.keys();
			}

			String fullCommandString = null;

			while (possibleCommands.hasMoreElements()) {
				String command = (String) possibleCommands.nextElement();
				if (command.startsWith(partialCommmandString)) {
					fullCommandString = command;
					gui.setInput(fullCommandString);
					return;
				}
			}
			break;
		default:
			break;
		}
	}
	
	private void setupLogging() {
		 logger = Logger.getLogger("de.fhg.iese.cl");
		 logFile = new File("cl.log");
		 try {
			 logFile.createNewFile();
			 logStream = new FileOutputStream(logFile,true);
			 SimpleFormatter formatter = new SimpleFormatter();
			 StreamHandler handler = new StreamHandler(logStream,formatter);
			 logger.setUseParentHandlers(false);
			 logger.addHandler(handler);
			 logger.setLevel(Level.INFO);
			 logger.info("Logger setup");
		} catch (Exception e) {
			System.err.println("[CL] Could not setup logging:"+e.getMessage());
		}
	}
	
	private void log(CommandLine commandLine) {
		try {
			String argumentsString = "[";
			if (commandLine != null)
			 for (int i = 0; i < commandLine.arguments.length; i++)
				 argumentsString += commandLine.arguments[i] + (i < commandLine.arguments.length-1 ? ",":"");
			 logger.info(commandLine.command.image + " " + argumentsString +"]");
		} catch (Exception e) {
			System.err.println("[CL] Could not write log message:"+e.getMessage());
		}
	}

	public void shellActivated(ShellEvent e) {
	}

	public void shellClosed(ShellEvent e) {
		dispose();
	}

	public void shellDeactivated(ShellEvent e) {
	}

	public void shellDeiconified(ShellEvent e) {
	}

	public void shellIconified(ShellEvent e) {		
	}

}
