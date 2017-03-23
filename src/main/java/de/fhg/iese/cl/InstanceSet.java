package de.fhg.iese.cl;

import java.util.ArrayList;
import java.util.Iterator;

public class InstanceSet extends ArrayList<Instance> {
	
	@Override
	public boolean add(Instance e) {
		boolean instanceExists = false;
		for (Iterator<Instance> iterator = this.iterator(); iterator.hasNext();) {
			Instance i = (Instance) iterator.next();
			if (i.getCopyPath().equals(e.getPath()))
				{
				 instanceExists = true;
				 i.setCopyPath(e.getCopyPath());
				 break;
				}
		}
		if (instanceExists)
			return false;
		else
			return super.add(e);
	}

	private static final long serialVersionUID = 1L;

}
