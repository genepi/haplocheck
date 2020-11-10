package genepi.haplocheck.commands;

import java.io.File;
import java.util.concurrent.Callable;

import org.apache.commons.io.FilenameUtils;

import genepi.haplocheck.App;
import genepi.haplocheck.steps.ContaminationStep;
import genepi.haplocheck.util.WorkflowTestContext;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Help.Visibility;

@Command(name = App.APP, version = App.VERSION)
public class ContaminationCommand implements Callable<Integer> {

	@Parameters(description = "VCF File")
	private String vcf;

	@Option(names = { "--out" }, description = "Output report", required = true)
	private String out;

	@Option(names = {
			"--raw" }, description = "Write raw report", required = false, showDefaultValue = Visibility.ALWAYS)
	boolean raw = false;

	@Option(names = { "--version" }, versionHelp = true)
	boolean showVersion;

	@Option(names = { "--help" }, usageHelp = true)
	boolean showHelp;

	public Integer call() throws Exception {

		WorkflowTestContext context = new WorkflowTestContext();
		String path = FilenameUtils.getFullPath(out);
		String name = FilenameUtils.getBaseName(out);

		if (!new File(vcf).exists()) {
			System.out.println("File not found. Exit");
			System.exit(1);
		}

		context.setInput("files", vcf);
		context.setConfig("output", out);
		context.setConfig("outputRaw", path + name + ".raw.txt");
		context.setConfig("outputReport", path + name + ".html");
		context.setConfig("raw", String.valueOf(raw));
		ContaminationStep contStep = new ContaminationStep();
		contStep.setup(context);
		contStep.run(context);
		
		return 0;

	}

	public void setVcf(String vcf) {
		this.vcf = vcf;
	}

	public void setOut(String out) {
		this.out = out;
	}
}