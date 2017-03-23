package de.fhg.iese.cl.cm;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;

public class CMAddDirDialog extends Dialog {
	
	private Shell sShell = null;
	private Display display = null;
	private String path;
	
	private Label infoLabel1 = null;
	private Label infoLabel2 = null;
	private Group optionGroup = null;
	private Button allAsCoreAssets = null;
	private Button dirOnly = null;
	private Composite buttonComposite = null;
	private Button okButton = null;
	private Button cancelButton = null;
	int selection = -1;

	public CMAddDirDialog(Shell parent, String path) {
		super(parent);
		sShell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		display = parent.getDisplay();
		this.path = path;
		initialize();
	}
	
	private void createButtonComposite() {
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.CENTER;
		gridData.grabExcessHorizontalSpace = true;
		GridLayout gridLayout1 = new GridLayout();
		gridLayout1.numColumns = 2;
		buttonComposite = new Composite(sShell, SWT.NONE);
		buttonComposite.setLayout(gridLayout1);
		buttonComposite.setLayoutData(gridData);
		okButton = new Button(buttonComposite, SWT.NONE);
		okButton.setText("Ok");
		okButton.setLayoutData(gridData);
		okButton
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				sShell.dispose();
				}
		});
		cancelButton = new Button(buttonComposite, SWT.NONE);
		cancelButton.setText("Cancel");
		cancelButton.setLayoutData(gridData);
		cancelButton
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				selection = -1;
				sShell.dispose();
				}
		});
	}
	
	private void createOptionGroup() {
		optionGroup = new Group(sShell, SWT.NONE);
		optionGroup.setLayout(new GridLayout());
		optionGroup.setText("Options");
		allAsCoreAssets = new Button(optionGroup, SWT.RADIO);
		allAsCoreAssets.setText("Add everything as a Core Asset");
		allAsCoreAssets
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				selection = 0;
				}
		});
		dirOnly = new Button(optionGroup, SWT.RADIO);
		dirOnly.setText("Add only the directory as a Core Asset - directory contents will be added but not marked as Core Assets");
		dirOnly
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				selection = 1;
				}
		});
	}
	
	private void initialize() {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		sShell.setText("Adding a Directory to the Repository");
		sShell.setLayout(gridLayout);
		infoLabel1 = new Label(sShell, SWT.NONE);
		infoLabel1.setText(path + " is a directory.");
		infoLabel2 = new Label(sShell, SWT.NONE);
		infoLabel2.setText("You have the following options");
		createOptionGroup();
		createButtonComposite();
		sShell.pack();
		
		Monitor primary = display.getPrimaryMonitor ();
		Rectangle bounds = primary.getBounds ();
		Rectangle rect = sShell.getBounds ();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		sShell.setLocation (x, y);
		sShell.addShellListener(new org.eclipse.swt.events.ShellAdapter() {
			public void shellClosed(org.eclipse.swt.events.ShellEvent e) {
				selection = -1;
			}
		});
		sShell.open();
		
		while (!sShell.isDisposed()) {
			if (!display.readAndDispatch()) display.sleep();
		}
		dispose();

	}
	
	private void dispose() {
		optionGroup.dispose();
		buttonComposite.dispose();
	}

	public int getSelection() {
		return selection;
	}

}
