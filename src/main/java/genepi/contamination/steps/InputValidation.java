package genepi.contamination.steps;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import genepi.hadoop.common.WorkflowContext;
import genepi.hadoop.common.WorkflowStep;
import genepi.hadoop.importer.IImporter;
import genepi.hadoop.importer.ImporterFactory;
import genepi.io.FileUtil;
import htsjdk.variant.vcf.VCFFileReader;

public class InputValidation extends WorkflowStep {

	@Override
	public boolean run(WorkflowContext context) {

		URLClassLoader cl = (URLClassLoader) InputValidation.class.getClassLoader();

		try {
			URL url = cl.findResource("META-INF/MANIFEST.MF");
			Manifest manifest = new Manifest(url.openStream());
			Attributes attr = manifest.getMainAttributes();
			String buildVesion = attr.getValue("Version");
			String buildTime = attr.getValue("Build-Time");
			String builtBy = attr.getValue("Built-By");
			context.println("Version: " + buildVesion + " (Built by " + builtBy + " on " + buildTime + ")");

		} catch (IOException E) {
			// handle
		}

		if (!importVcfFiles(context)) {
			return false;
		}

		return checkFiles(context);

	}

	private boolean checkFiles(WorkflowContext context) {
		String files = context.get("files");
		context.beginTask("Analyze files ");
		int noSamples = 0;
		try {
			String[] vcfFiles = FileUtil.getFiles(files, "*.vcf.gz$|*.vcf$|*.bam$");
			noSamples = 0;
			for (String filename : vcfFiles) {
				VCFFileReader reader = new VCFFileReader(new File(filename), false);
				noSamples += reader.getFileHeader().getGenotypeSamples().size();
				reader.close();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			context.endTask("Error" + e.getMessage(), WorkflowContext.ERROR);
			return false;
		}

		context.endTask("Samples found " + noSamples, WorkflowContext.OK);
		return true;

	}

	private boolean importVcfFiles(WorkflowContext context) {

		for (String input : context.getInputs()) {

			if (ImporterFactory.needsImport(context.get(input))) {

				context.beginTask("Importing files...");

				String[] urlList = context.get(input).split(";")[0].split("\\s+");

				String username = "";
				if (context.get(input).split(";").length > 1) {
					username = context.get(input).split(";")[1];
				}

				String password = "";
				if (context.get(input).split(";").length > 2) {
					password = context.get(input).split(";")[2];
				}

				for (String url2 : urlList) {

					String url = url2 + ";" + username + ";" + password;
					String target = FileUtil.path(context.getLocalTemp(), "importer", input);
					FileUtil.createDirectory(target);
					context.println("Import to local workspace " + target + "...");

					try {

						context.updateTask("Import " + url2 + "...", WorkflowContext.RUNNING);

						IImporter importer = ImporterFactory.createImporter(url, target);

						if (importer != null) {

							boolean successful = importer.importFiles("vcf.gz");

							if (successful) {

								context.setInput(input, target);

							} else {

								context.updateTask("Import " + url2 + " failed: " + importer.getErrorMessage(),
										WorkflowContext.ERROR);

								return false;

							}

						} else {

							context.updateTask("Import " + url2 + " failed: Protocol not supported",
									WorkflowContext.ERROR);

							return false;

						}

					} catch (Exception e) {
						context.updateTask("Import File(s) " + url2 + " failed: " + e.toString(),
								WorkflowContext.ERROR);

						return false;
					}

				}

				context.updateTask("File Import successful. ", WorkflowContext.OK);

			}

		}

		return true;

	}

}
