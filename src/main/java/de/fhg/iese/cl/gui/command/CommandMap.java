package de.fhg.iese.cl.gui.command;

import java.util.Hashtable;

/**
 * This class maps string to Command constants
 * @author anastaso
 *
 */
public class CommandMap {
	
	public static Hashtable<String, Integer> map = new Hashtable<String,Integer>();
	
	static {
		map.put("help",CommandSet.HELP);
		map.put("add-core-asset", CommandSet.ADD_CORE_ASSET);
		map.put("show-core-assets", CommandSet.SHOW_CORE_ASSETS);
		map.put("show-properties", CommandSet.SHOW_PROPERTIES);
		map.put("show-instances", CommandSet.SHOW_INSTANCES);
		map.put("set-properties", CommandSet.SET_PROPERTIES);
		map.put("instantiate-core-asset", CommandSet.INSTANTIATE_CORE_ASSET);
		map.put("show-core-diff", CommandSet.SHOW_CORE_ASSET_DIFF);
		map.put("show-instance-diff", CommandSet.SHOW_INSTANCE_DIFF);
		map.put("rebase", CommandSet.REBASE);
		map.put("integrate", CommandSet.INTEGRATE);
		map.put("clear", CommandSet.CLEAR);
		map.put("exit", CommandSet.EXIT);
	}
}
