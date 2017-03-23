package de.fhg.iese.cl.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.PopupList;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import java.io.InputStream;

public class ConsoleGUI {

	private Shell sShell = null;

	private Display display = null;
	
	private Controller controller;

	private Composite outputComposite = null;
	private Composite inputComposite = null;
	private StyledText outputTextArea = null;
	private Label inputLabel = null;
	private Text inputText = null;
	private Font boldFont=null;
	private Font systemFont=null;
	private ImageData dataSmall;
	private ImageData dataLarge;
	private Image small;
	private Image large;
	
	protected static final String[] popupItems = {
	    "Select All", "Cut", "Copy", "Paste"
	};

	private void addListener() {
		inputText.addKeyListener(controller);
		inputText.addTraverseListener(controller);
		sShell.addShellListener(controller);
	}

	public void clear() {
		getOutputTextArea().setText("");
	}

	public void close() {
		sShell.close();
	}

	/**
	 * This method initializes inputComposite	
	 *
	 */
	private void createInputComposite() {
		GridData inputTextGridData = new GridData();
		inputTextGridData.horizontalAlignment = GridData.FILL;
		inputTextGridData.grabExcessHorizontalSpace = true;
		inputTextGridData.verticalAlignment = GridData.CENTER;
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = GridData.CENTER;
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		inputComposite = new Composite(sShell, SWT.NONE);
		inputComposite.setLayout(gridLayout);
		inputComposite.setLayoutData(gridData);
		inputLabel = new Label(inputComposite, SWT.NONE);
		inputLabel.setText("cl>");
		inputText = new Text(inputComposite, SWT.BORDER);
		inputText.setLayoutData(inputTextGridData);
	}

	private void createOutputArea() {
		GridData outputTextAreaGridData = new GridData();
		outputTextAreaGridData.horizontalAlignment = GridData.FILL;
		outputTextAreaGridData.grabExcessHorizontalSpace = true;
		outputTextAreaGridData.grabExcessVerticalSpace = true;
		outputTextAreaGridData.verticalAlignment = GridData.FILL;
		outputTextArea = new StyledText(outputComposite, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		outputTextArea.setEditable(false);
		outputTextArea.setBackground(new Color((Device) display, new RGB(217,208,200)));
		outputTextArea.setLayoutData(outputTextAreaGridData);
		outputTextArea.setFont(systemFont);
		
		outputTextArea.addMouseListener(new MouseAdapter() {
		    public void mouseDown(MouseEvent e) {
		        if (e.button == 3) {
		            processPopup();
		        }
		    }});
	}

	protected void processPopup() {
		PopupList popup = new PopupList(sShell);
		popup.setItems(popupItems);

		Point p = display.getCursorLocation();
		p = display.map(sShell, getOutputTextArea(), p.x, p.y);
		
		String choice = popup.open(new Rectangle(p.x, p.y-200, 100, 200));
		if (choice != null) {
		    if      (popupItems[0].equals(choice)) {
		    	getOutputTextArea().selectAll();
		    }
		    else if (popupItems[1].equals(choice)) {
		    	getOutputTextArea().cut();
		    }
		    else if (popupItems[2].equals(choice)) {
		    	getOutputTextArea().copy();
		    }
		    else if (popupItems[3].equals(choice)) {
		    	getOutputTextArea().paste();
		    }
		}
	}
	/**
	 * This method initializes outputComposite	
	 *
	 */
	private void createOutputComposite() {
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessVerticalSpace = true;
		outputComposite = new Composite(sShell, SWT.BORDER);
		outputComposite.setLayout(new GridLayout());
		outputComposite.setLayoutData(gridData);
		createOutputArea();
	}

	/**
	 * This method initializes sShell
	 */
	private void createSShell(Display d) {
		sShell = new Shell(d);
		sShell.setLayout(new GridLayout());
		sShell.setText("Customization Layer");
		
		systemFont = Display.getDefault().getSystemFont();

		FontData boldFontData[] = systemFont.getFontData();
		for (int i = 0; i < boldFontData.length; i++) {
			if (boldFontData[i] != null) {
				boldFontData[i].setStyle(SWT.BOLD);
				boldFontData[i].setHeight(9);
			}
		}
		boldFont = new Font(sShell.getDisplay(),boldFontData);
		
		createOutputComposite();
		createInputComposite();
		getInputText().setFocus();
		sShell.setSize(new Point(626, 464));
		
		Monitor primary = d.getPrimaryMonitor ();
		Rectangle bounds = primary.getBounds ();
		Rectangle rect = sShell.getBounds ();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		sShell.setLocation (x, y);
		
		InputStream is1 = getClass().getResourceAsStream("/icons/CLSmall.ico");
		InputStream is2 = getClass().getResourceAsStream("/icons/CLLarge.ico");
		dataSmall = new ImageData(is1);
		dataLarge = new ImageData(is2);
		
		small = new Image(d,dataSmall);
		large = new Image(d,dataLarge);
		sShell.setImages(new Image[] {small, large});
	}

	protected void dispose() {
		boldFont.dispose();	
		small.dispose();
		large.dispose();
		sShell.dispose();
	}

	public Display getDisplay() {
		return display;
	}

	public Text getInputText() {
		return inputText;
	}

	public synchronized StyledText getOutputTextArea() {
		return outputTextArea;
	}

	public Shell getSShell() {
		return sShell;
	}
	
	
	public boolean getUserConfirmation(String string) {
		UserConfirmationThread uct = new UserConfirmationThread(sShell,string);
		getDisplay().syncExec(uct);
		return uct.userConfirmed;
	}

	public void print(String string) {
		getOutputTextArea().append(string);
		int index = getOutputTextArea().getLineCount();
		getOutputTextArea().setTopIndex(index);
	}
	
	public void println(String string) {
		if (getOutputTextArea().getText().length() > 0)
			getOutputTextArea().append((System.getProperty("line.separator")+string));
		else
			getOutputTextArea().append(string);
		int index = getOutputTextArea().getLineCount();
		getOutputTextArea().setTopIndex(index);
	}
	
	public void run() {
		display = new Display();
		createSShell(display);
		sShell.open();
		printlnBold("Fraunhofer IESE Customization Layer");
		controller = new Controller(this);
		addListener();
		
		while (!sShell.isDisposed()) {
			if(!display.readAndDispatch()) {
				display.sleep();
			}
		}
		dispose();
		display.dispose();
	}

	public void setInput(String inputString) {
		getInputText().setText(inputString);
		getInputText().append("");
	}

	public void setInputText(Text inputText) {
		this.inputText = inputText;
	}

	public void setOutputTextArea(StyledText outputTextArea) {
		this.outputTextArea = outputTextArea;
	}
	void echoInput(Controller controller, String input) {
		String echoString = "cl>"+input; 
		printlnBold(echoString);
	}
	private void printlnBold(String echoString) {
		println(echoString);
		StyleRange style = new StyleRange();
		int lineCount = getOutputTextArea().getLineCount();
		int currentOffset = getOutputTextArea().getOffsetAtLine(lineCount-1);
		style.start = currentOffset;
		style.length = echoString.length();
		style.underline = true;
		style.font = boldFont;
		getOutputTextArea().setStyleRange(style);
		
	}
}
