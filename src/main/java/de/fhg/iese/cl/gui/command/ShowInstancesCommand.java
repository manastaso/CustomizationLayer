package de.fhg.iese.cl.gui.command;

import java.util.ArrayList;
import java.util.Iterator;

import de.fhg.iese.cl.CustomizationLayer;
import de.fhg.iese.cl.Instance;
import de.fhg.iese.cl.cm.CMException;
import de.fhg.iese.cl.gui.ConsoleGUI;
import de.fhg.iese.cl.parser.Token;

public class ShowInstancesCommand extends Command {
	
	private long revision = -1;

	public ShowInstancesCommand(ConsoleGUI gui, CustomizationLayer cl,
			Token[] arguments) {
		super(gui, cl, arguments);
	}

	@Override
	public void execute() {
		String inputRevisionStr = arguments[1] == null ? "-1" : arguments[1].toString();
		try {
			if (arguments[0] == null) {
				arguments[0] = new Token();
				arguments[0].image = null;
			}
			revision = new Long(inputRevisionStr);
			ArrayList<Instance> instances = cl.getInstances(arguments[0].toString(),revision);
			for (Iterator<Instance> iterator = instances.iterator(); iterator
			.hasNext();) {
				Instance instanceName = (Instance) iterator.next();
				outputToUI(instanceName.toString());
			}
			outputToUI(instances.size()+" instance(s) available");
		} catch (CMException e) {
			outputToUI("CM Error: "+e.getMessage().toString());
		}
	}

	@Override
	public String getCommandName() {
		return "show-instances";
	}

	@Override
	public void undo() {
		// TODO Auto-generated method stub
	}

	@Override
	public void requestStop() {
		// TODO Auto-generated method stub
		
	}

}
