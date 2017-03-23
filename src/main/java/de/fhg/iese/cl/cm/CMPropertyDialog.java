package de.fhg.iese.cl.cm;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;

public class CMPropertyDialog extends Dialog{

	private Composite propertyComposite = null;
	private Label repoLabel = null;
	private Label proxyLabel = null;
	private Label portLabel = null;
	private Label userLabel = null;
	private Text userText = null;
	private Text repoText = null;
	private Text proxyText = null;
	private Text portText = null;
	private Label passwordLabel = null;
	private Text passwordText = null;
	private Composite buttonComposite = null;
	private Button okButton = null;
	private Button cancelButton = null;
	private Shell myShell = null;
	private Display display = null;
	private String result = null;
	private CMAbstractionLayer cm = null;
	private RepositoryProperties existingProps = new RepositoryProperties("not set", "", "", "", "");
	
	public CMPropertyDialog(Shell parent, CMAbstractionLayer cm) {
		super(parent);
		myShell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		display = parent.getDisplay();
		this.cm = cm;
		
		try {
			existingProps = cm.readProperties(true);
			if (existingProps == null)
				existingProps = new RepositoryProperties("", "", "", "", "");
		} catch (CMException e) {
			//we do not need to do anything here properties will be set by the user
		}
		
		initialize();
	}

	/**
	 * This method initializes buttonComposite	
	 *
	 */
	private void createButtonComposite() {
		GridData gridData5 = new GridData();
		gridData5.horizontalAlignment = GridData.FILL;
		gridData5.verticalAlignment = GridData.CENTER;
		GridData gridData4 = new GridData();
		gridData4.horizontalAlignment = GridData.FILL;
		gridData4.grabExcessHorizontalSpace = true;
		gridData4.verticalAlignment = GridData.CENTER;
		GridData gridData1 = new GridData();
		gridData1.horizontalAlignment = GridData.FILL;
		gridData1.grabExcessHorizontalSpace = true;
		gridData1.verticalAlignment = GridData.CENTER;
		GridLayout gridLayout2 = new GridLayout();
		gridLayout2.numColumns = 2;
		buttonComposite = new Composite(myShell, SWT.NONE);
		buttonComposite.setLayout(gridLayout2);
		buttonComposite.setLayoutData(gridData5);
		okButton = new Button(buttonComposite, SWT.NONE);
		okButton.setText("Ok");
		okButton.setLayoutData(gridData1);
		okButton.setToolTipText("Store properties to disk and initialize repository access");
		okButton.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				RepositoryProperties props = new RepositoryProperties( userText.getText(),
																	   passwordText.getText(),
																	   repoText.getText(),
																	   proxyText.getText(),
																	   portText.getText());
				try {
					result = cm.storeProperties(props);
				} catch (CMException e1) {
					result = "Error while storing properties:"+e1.getMessage();
				}
				myShell.dispose();
			}
		});
		cancelButton = new Button(buttonComposite, SWT.NONE);
		cancelButton.setText("Cancel");
		cancelButton.setLayoutData(gridData4);
		cancelButton
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
						myShell.dispose();
						}
				});
	}

	/**
	 * This method initializes userComposite	
	 * @param repositoryURL2 
	 *
	 */
	private void createPropertyComposite() {
		GridLayout gridLayout1 = new GridLayout();
		gridLayout1.numColumns = 2;
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = false;
		gridData.verticalAlignment = GridData.CENTER;
		propertyComposite = new Composite(myShell, SWT.NONE);
		propertyComposite.setLayoutData(gridData);
		propertyComposite.setLayout(gridLayout1);
		repoLabel = new Label(propertyComposite, SWT.NONE);
		repoLabel.setText("Repository URL");
		repoText = new Text(propertyComposite, SWT.BORDER);
		repoText.setText(existingProps.repositoryURL);
		repoText.setLayoutData(gridData);
		proxyLabel = new Label(propertyComposite, SWT.NONE);
		proxyLabel.setText("Proxy URL");
		proxyText = new Text(propertyComposite, SWT.BORDER);
		proxyText.setText(existingProps.repositoryProxyURL);
		proxyText.setLayoutData(gridData);
		portLabel = new Label(propertyComposite, SWT.NONE);
		portLabel.setText("Proxy Port");
		portText = new Text(propertyComposite, SWT.BORDER | SWT.ALPHA);
		portText.setText(existingProps.repositoryProxyPort);
		portText.setLayoutData(gridData);
		portText.addListener(SWT.Verify, new Listener() {
		      public void handleEvent(Event e) {
		        String string = e.text;
		        char[] chars = new char[string.length()];
		        string.getChars(0, chars.length, chars, 0);
		        for (int i = 0; i < chars.length; i++) {
		          if (!('0' <= chars[i] && chars[i] <= '9')) {
		            e.doit = false;
		            return;
		          }
		        }
		      }
		    });
		
		userLabel = new Label(propertyComposite, SWT.NONE);
		userLabel.setText("User Name");
		userText = new Text(propertyComposite, SWT.BORDER);
		userText.setText(existingProps.username);
		userText.setLayoutData(gridData);
		passwordLabel = new Label(propertyComposite, SWT.NONE);
		passwordLabel.setText("Password");
		passwordText = new Text(propertyComposite, SWT.BORDER | SWT.PASSWORD);
		passwordText.setText(existingProps.password);
		passwordText.setLayoutData(gridData);
	}

	private void dispose() {
		propertyComposite.dispose();
		buttonComposite.dispose();
	}

	public String getResult() {
		return result;
	}

	private void initialize() {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		createPropertyComposite();
		myShell.setLayout(gridLayout);
		createButtonComposite();
		myShell.setText("Set repository access properties");
		myShell.setSize(new Point(300,198));
		
		Monitor primary = display.getPrimaryMonitor ();
		Rectangle bounds = primary.getBounds ();
		Rectangle rect = myShell.getBounds ();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		myShell.setLocation (x, y);
		myShell.open();
		
		while (!myShell.isDisposed()) {
			if (!display.readAndDispatch()) display.sleep();
		}
		dispose();

	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
