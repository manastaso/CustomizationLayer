package de.fhg.iese.cl.gui.command;

/**
 * This interface defines the set of currently available CL commands
 * @author anastaso
 *
 */
public interface CommandSet {
	int
    HELP = 1,
	ADD_CORE_ASSET = 3,
	SHOW_CORE_ASSETS = 4,
	SHOW_INSTANCES = 5,
	INSTANTIATE_CORE_ASSET= 6,
	CLEAR = 7,
	EXIT = 8,
	SHOW_PROPERTIES = 9,
	SHOW_INSTANCE_DIFF = 10,
	SHOW_CORE_ASSET_DIFF = 11,
	SET_PROPERTIES = 12,
	REBASE = 13,
	INTEGRATE = 14;
}
