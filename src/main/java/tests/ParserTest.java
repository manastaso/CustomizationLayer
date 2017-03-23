package tests;

import java.io.StringReader;

import de.fhg.iese.cl.parser.CommandLine;
import de.fhg.iese.cl.parser.ParseException;
import de.fhg.iese.cl.parser.CommandInterpreter;
import junit.framework.TestCase;

public class ParserTest extends TestCase {
	
	private CommandInterpreter p;
	private StringReader reader;
	
	protected void setUp() throws Exception {
		super.setUp();
		reader = new StringReader("add-core-asset c:\\u");
	}

	public void testSetRepositoryParserInputStream() {
		p = new CommandInterpreter(reader);
		assertNotNull(p);
	}
	
	public void testInput() throws ParseException {
		p = new CommandInterpreter(reader) ;
		CommandLine s = p.specification();
		assertNotNull(s);
		System.out.println(s.command.toString());
		for (int i = 0; i < s.arguments.length; i++) {
			if (s.arguments[i] != null)
				System.out.println(s.arguments[i].toString());
		}
	}

}
