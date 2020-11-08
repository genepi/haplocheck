package genepi.haplocheck.commands;

import java.util.List;
import java.util.concurrent.Callable;

import genepi.haplocheck.App;
import genepi.haplocheck.steps.ContaminationStep;
import genepi.haplocheck.util.WorkflowTestContext;
import picocli.CommandLine.Command;
import picocli.CommandLine.Help.Visibility;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = App.APP, version = App.VERSION)
public class ContaminationCommand implements Callable<Integer> {

	@Parameters(description = "VCF folder")
	String vcf;

	@Option(names = { "--out" }, description = "Output filename", required = true)
	String out;

	public Integer call() throws Exception {

		WorkflowTestContext context = new WorkflowTestContext();

		//currently a directory required, since I store the VCF file in the input directory. change!
		context.setInput("files", vcf);
		context.setConfig("output", out);
		context.setConfig("summary", out +".summary");
		context.setConfig("outputCont", out +".cont");
		context.setConfig("outputHsd", out+".hsd");

		ContaminationStep contStep = new ContaminationStep();
		contStep.setup(context);
		contStep.run(context);
		System.out.println("Done");

		return 0;

	}
}