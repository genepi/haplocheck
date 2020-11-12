package genepi.haplocheck;

import genepi.haplocheck.commands.ContaminationCommand;
import picocli.CommandLine;

public class App {

	public static final String APP = "haplocheck";

	public static final String VERSION = "1.3.2";

	public static final String URL = "https://github.com/genepi/haplocheck";

	public static final String COPYRIGHT = "(c) 2020 Sebastian Schoenherr, Hansi Weissensteiner, Lukas Forer";

	public static String[] ARGS = new String[0];

	public static void main(String[] args) {

		System.out.println();
		System.out.println(APP + " " + VERSION);
		if (URL != null && !URL.isEmpty()) {
			System.out.println(URL);
		}
		if (COPYRIGHT != null && !COPYRIGHT.isEmpty()) {
			System.out.println(COPYRIGHT);
		}
		System.out.println();

		ARGS = args;

		new CommandLine(new ContaminationCommand()).execute(args);

	}

}