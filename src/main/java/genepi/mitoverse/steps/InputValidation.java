package genepi.mitoverse.steps;

import java.io.File;
import java.util.List;
import java.util.Vector;

import genepi.hadoop.common.WorkflowContext;
import genepi.hadoop.common.WorkflowStep;
import genepi.hadoop.importer.IImporter;
import genepi.hadoop.importer.ImporterFactory;
import genepi.io.FileUtil;
import genepi.mitoverse.steps.vcf.VcfFile;
import genepi.mitoverse.steps.vcf.VcfFileUtil;
import htsjdk.samtools.SamReader;
import htsjdk.samtools.SamReaderFactory;
import htsjdk.samtools.ValidationStringency;

public class InputValidation extends WorkflowStep {

	@Override
	public boolean run(WorkflowContext context) {
		context.beginTask("Analyze files ");

		if (!importVcfFiles(context)) {
			return false;
		}

		return checkFiles(context);

	}

	private boolean checkFiles(WorkflowContext context) {
		String files = context.get("files");
		int noSamples = 0;

		int noSnps = 0;
		try {
			noSamples = 0;
			String infos = null;
			List<VcfFile> validVcfFiles = new Vector<VcfFile>();

			String[] vcfFilenames = FileUtil.getFiles(files, "*.vcf.gz$|*.vcf$");

			String[] bamFilenames = FileUtil.getFiles(files, "*.bam$|*.cram$");

			if (bamFilenames.length == 0 && vcfFilenames.length == 0) {
				context.endTask("No valid files detected! Only CRAM/BAM and vcf/vcf.gz files are supported.",
						WorkflowContext.ERROR);
				return false;
			}

			if (bamFilenames.length > 0 && vcfFilenames.length > 0) {
				context.endTask("Please upload either CRAM/BAM or VCF files!", WorkflowContext.ERROR);
				return false;
			}

			if (vcfFilenames.length > 1) {
				context.endTask("Currently only single VCF upload is supported.", WorkflowContext.ERROR);
				return false;
			}

			if (vcfFilenames.length > 0) {

				for (String filename : vcfFilenames) {

					context.updateTask("Analyze file " + FileUtil.getFilename(filename) + "...",
							WorkflowContext.RUNNING);

					VcfFile vcfFile = VcfFileUtil.load(filename);

					if (!VcfFileUtil.isValidChromosome(vcfFile.getChromosome())) {
						context.endTask("VCF includes " + vcfFile.getChromosome() + ". Not a valid chromosome",
								WorkflowContext.ERROR);
						return false;
					}

					noSamples += vcfFile.getNoSamples();

					noSnps += vcfFile.getNoSnps();

					validVcfFiles.add(vcfFile);

					infos = "Total Samples: " + noSamples + "\n" + " Total SNPs: " + noSnps + "\n";

					if (validVcfFiles.size() > 0) {
						infos += "File includes heteroplasmies: " + vcfFile.isHeteroplasmyTag();
						context.endTask(validVcfFiles.size() + " valid VCF file(s) found.\n\n" + infos,
								WorkflowContext.OK);
						return true;

					} else {
						context.endTask(
								"The provided files are not valid  (see <a href=\"/start.html#!pages/help\">Help</a>)",
								WorkflowContext.ERROR);
						return false;
					}
				}
			}

			else if (bamFilenames.length > 0) {

				int count = 0;

				for (String file : bamFilenames) {

					SamReader reader = null;

					try {
						reader = SamReaderFactory.makeDefault().validationStringency(ValidationStringency.STRICT)
								.open(new File(file));
						reader.close();
						count++;
					} catch (Exception e) {
						context.endTask("Please check CRAM/BAM file: " + e.getMessage(), WorkflowContext.ERROR);
						reader.close();
						return false;
					}
				}

				if (count > 0) {
					context.endTask(count + " valid BAM/CRAM file(s) found.\n\n", WorkflowContext.OK);
					return true;

				} else {
					context.endTask("No valid files found  (see <a href=\"/start.html#!pages/help\">Help</a>)",
							WorkflowContext.ERROR);
					return false;
				}

			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			context.endTask("Error: " + e.getMessage(), WorkflowContext.ERROR);
			return false;
		}

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
