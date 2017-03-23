package de.fhg.iese.cl.cm.svn;

import java.util.Observable;
import java.util.Observer;

import org.tmatesoft.svn.core.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.SVNCancelException;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNProperty;
import org.tmatesoft.svn.core.wc.ISVNEventHandler;
import org.tmatesoft.svn.core.wc.SVNEvent;
import org.tmatesoft.svn.core.wc.SVNEventAction;

import de.fhg.iese.cl.CustomizationLayer;

public class CommitEventHandler implements ISVNLogEntryHandler, ISVNEventHandler, Observer  {
	
	boolean stop=false;
	CustomizationLayer cl;

	public CommitEventHandler(CustomizationLayer cl) {
		this.cl = cl;
	}

	public void handleEvent(SVNEvent event, double progress) throws SVNCancelException{
        /*
         * Gets the current action. An action is represented by SVNEventAction.
         * In case of a commit  an  action  can  be  determined  via  comparing 
         * SVNEvent.getAction() with SVNEventAction.COMMIT_-like constants. 
         */
        SVNEventAction action = event.getAction();
        if (action == SVNEventAction.COMMIT_MODIFIED) {
        	cl.outputToUI("Sending   " + event.getFile());
        } else if (action == SVNEventAction.COMMIT_DELETED) {
        	cl.outputToUI("Deleting   " + event.getFile());
        } else if (action == SVNEventAction.COMMIT_REPLACED) {
        	cl.outputToUI("Replacing   " + event.getFile());
        } else if (action == SVNEventAction.COMMIT_DELTA_SENT) {
        	cl.outputToUI("Transmitting file data....");
        } else if (action == SVNEventAction.COMMIT_ADDED) {
            /*
             * Gets the MIME-type of the item.
             */
            String mimeType = event.getMimeType();
            if (SVNProperty.isBinaryMimeType(mimeType)) {
                /*
                 * If the item is a binary file
                 */
            	cl.outputToUI("Adding  (bin)  "
                        + event.getFile());
            } else {
            	cl.outputToUI("Adding         "
                        + event.getFile());
            }
        }
        checkCancelled();
    }
    
    /*
     * Should be implemented to check if the current operation is canceled. If 
     * it is, this method should throw an SVNCancelException. 
     */
    public void checkCancelled() throws SVNCancelException {
    	if (stop) throw new SVNCancelException();
    }

	public void update(Observable arg0, Object arg1) {
		stop = true;
	}

	public void handleLogEntry(SVNLogEntry logEntry) throws SVNException {
	}

}
