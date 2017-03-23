package de.fhg.iese.cl.gui.command;

import de.fhg.iese.cl.CustomizationLayer;
import de.fhg.iese.cl.cm.CMPropertyDialog;
import de.fhg.iese.cl.gui.ConsoleGUI;
import de.fhg.iese.cl.parser.Token;

public class SetPropertiesCommand extends Command {
	
	String result=null;

	public SetPropertiesCommand(ConsoleGUI gui, CustomizationLayer cl, Token[] arguments) {
		super(gui, cl, arguments);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute() {
		gui.getDisplay().syncExec(new Runnable() {
			public void run() {
				CMPropertyDialog d = new CMPropertyDialog(gui.getSShell(),cl.getCmConnector());
				result = d.getResult();
				if (result == null)
					gui.println(getCommandName()+" cancelled by user");
				else
					gui.println(result);
			}
		});
		
		if ( result != null )
		{
			Token[] arguments = { new Token() };
			Command setup = new SetupCommand(gui,cl,arguments);
			setup.start();
		}
	}

	@Override
	public String getCommandName() {
		return "set-properties";
	}

	@Override
	public void undo() {
	}

}
