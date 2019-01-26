package genepi.haplocheck;

import java.lang.reflect.InvocationTargetException;

import genepi.base.Toolbox;

public class App extends Toolbox {

	public App(String command, String[] args) {
		super(command, args);
	}

	public static void main(String[] args) throws InstantiationException, IllegalAccessException, SecurityException,
			NoSuchMethodException, IllegalArgumentException, InvocationTargetException {

		App main = new App("contamination.jar", args);
		main.start();
	}
}
