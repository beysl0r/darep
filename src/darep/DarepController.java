package darep;

import java.util.HashMap;
import java.util.Map;

import repos.Repository;
import darep.Command.ActionType;
import darep.parser.ArgConstraint;
import darep.parser.CommandSyntax;
import darep.parser.ParseException;
import darep.parser.Parser;

/**
 * Controls the flow of the program. Contains the main-method.
 * 
 */
public class DarepController {

	/**
	 * The CommandSyntax-Array given to the parser. Defines the allowed syntax
	 * when calling the program. The Format for a Command is: (Name,#Options
	 * with Value,Flags)
	 */
	private static final CommandSyntax[] syntax = new CommandSyntax[] {
		new CommandSyntax(Command.ActionType.add, 1, new String[] { "r","n", "d" }, new String[] { "m" }),
		new CommandSyntax(Command.ActionType.delete, 1,	new String[] { "r" }, new String[0]),
		new CommandSyntax(Command.ActionType.replace, 2, new String[] { "r", "d" }, new String[] { "m" }),
		new CommandSyntax(Command.ActionType.help, 0, new String[] {},new String[0]),
		new CommandSyntax(Command.ActionType.list, 0, new String[] { "r" },	new String[] { "p" }),
		new CommandSyntax(Command.ActionType.export, 2, new String[] { "r" }, new String[0])
	};
	
	private static final Map<String, ArgConstraint> constraints = new HashMap<String, ArgConstraint>();
	
	static {
		constraints.put("n", new ArgConstraint() {
			@Override
			public boolean isValid(String arg) {
				// arg only consists of word chars (digit, letter, _) and -
				// and is no longer than 40 chars long
				return (arg.matches("[\\w-]*") && arg.length() <= 40);
			}

			@Override
			public String getDescription() {
				return "Argument must only contain" +
						" digits, letters, \"_\" or \"-\"" +
						" and must not be longer than 40 characters";
			}
		});
		constraints.put("d", new ArgConstraint() {
			@Override
			public boolean isValid(String arg) {
				// arg does not contain control characters and is no longer
				// than 1000 chars
				return (arg.matches("[^\\p{Cntrl}]*") && arg.length() <= 1000);
			}

			@Override
			public String getDescription() {
				return "Argument must not contain ISO control characters" +
						" and must not be longer than 1000 characters";
			}
		});
	}

	private Parser parser;
	private Repository repository;

	public static void main(String[] args) {
		DarepController controller = new DarepController();
		controller.processCommand(args);
	}

	private void processCommand(String[] args) {
		try {
			Command command = parser.parse(args);
			System.out.println(command);

			// check if command=help => dont need to load the repo, just print
			// help
			if (command.getAction() == ActionType.help) {
				printHelp();
				System.exit(0);
			}

			// check if option -r is set or default repos, and setup repos.
			if (command.isSet("r"))
				repository = new Repository(command.getOptionParam("r"));
			else
				repository = new Repository();

			switch (command.getAction()) {
			case add:
				repository.add(command);
				break;
			case delete:
				repository.delete(command);
				break;
			case replace:
				repository.replace(command);
				break;
			case list:
			case export:
				System.out.println(command.getAction()
						+ ": not yet implemented");
			}
		} catch (ParseException ex) {
			System.err.println("ERROR: " + ex.getMessage());
		}
	}

	public DarepController() {
		this.parser = new Parser(DarepController.syntax, DarepController.constraints);
	}

	private void printHelp() {
		System.out.println("sorry i cant help you yet, see specifications.pdf");
	}
}