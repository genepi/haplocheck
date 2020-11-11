package genepi.haplocheck.steps;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import com.google.common.math.Quantiles;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import core.SampleFile;
import cloudgene.sdk.internal.WorkflowContext;
import cloudgene.sdk.internal.WorkflowStep;
import genepi.haplocheck.steps.contamination.ContaminationDetection;
import genepi.haplocheck.steps.contamination.ContaminationDetection.Status;
import genepi.haplocheck.steps.contamination.HaplogroupClassifier;
import genepi.haplocheck.steps.contamination.VariantSplitter;
import genepi.haplocheck.steps.contamination.objects.ContaminationObject;
import genepi.haplocheck.steps.report.ReportGenerator;
import genepi.haplocheck.util.Utils;
import importer.VcfImporter;
import phylotree.Phylotree;
import phylotree.PhylotreeManager;
import util.ExportUtils;
import vcf.Sample;

public class ContaminationStep extends WorkflowStep {

	@Override
	public boolean run(WorkflowContext context) {
		return detectContamination(context);

	}

	private boolean detectContamination(WorkflowContext context) {

		try {
			Phylotree phylotree = PhylotreeManager.getInstance().getPhylotree("phylotree17.xml", "weights17.txt");

			String input = context.get("files");
			String output = context.getConfig("output");
			String outputReport = context.getConfig("outputReport");
			String outputRaw = context.getConfig("outputRaw");
			String raw = context.getConfig("raw");

			Collection<File> out = Utils.getVcfFiles(input);
			
			context.beginTask("Check for Contamination.. ");
			
			if (out.size() > 1) {
				context.endTask("Currently only single VCF file upload is supported!", WorkflowContext.ERROR);
				return false;
			}

			File file = out.iterator().next();

			VariantSplitter splitter = new VariantSplitter();

			VcfImporter reader = new VcfImporter();

			context.updateTask("Load file...", WorkflowContext.RUNNING);
			HashMap<String, Sample> mutationServerSamples = reader.load(file, false);

			context.updateTask("Split Profile into Major/Minor Profile...", WorkflowContext.RUNNING);
			ArrayList<String> profiles = splitter.split(mutationServerSamples);

			context.updateTask("Classify Haplogroups...", WorkflowContext.RUNNING);
			HaplogroupClassifier classifier = new HaplogroupClassifier();
			SampleFile haplogrepSamples = classifier.calculateHaplogroups(phylotree, profiles);

			ContaminationDetection contamination = new ContaminationDetection();
			context.updateTask("Detect Contamination...", WorkflowContext.RUNNING);

			ArrayList<ContaminationObject> result = contamination.detect(mutationServerSamples,
					haplogrepSamples.getTestSamples());

			// ExportUtils.createHsdInput(haplogrepSamples.getTestSamples(), "");

			context.updateTask("Write Contamination Report...", WorkflowContext.RUNNING);

			contamination.writeTextualReport(output, result);

			if (raw != null && Boolean.valueOf(raw)) {
				contamination.writeTextualRawReport(outputRaw, result);
			}

			ReportGenerator generator = new ReportGenerator();
			generator.setContaminationList(result);
			generator.generate(outputReport);

			context.endTask("Execution successful.", WorkflowContext.OK);
			return true;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			context.endTask("Contamination failed", WorkflowContext.ERROR);
			return false;
		}
	}
}
