package genepi.mitocloud.steps;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import contamination.Contamination;
import contamination.HaplogroupClassifier;
import contamination.VariantSplitter;
import contamination.objects.Sample;
import core.SampleFile;
import genepi.hadoop.common.WorkflowContext;
import genepi.hadoop.common.WorkflowStep;
import importer.VcfImporter;
import phylotree.Phylotree;
import phylotree.PhylotreeManager;

public class ContaminationStep extends WorkflowStep {

	@Override
	public boolean run(WorkflowContext context) {
		return detectContamination(context);

	}

	private boolean detectContamination(WorkflowContext context) {

		try {
			Phylotree phylotree = PhylotreeManager.getInstance().getPhylotree("phylotree17.xml", "weights17.txt");

			String input = context.get("files");
			String output = context.get("contaminationReport");

			File file = new File(input);

			context.beginTask("Check for Contamination.. ");

			VariantSplitter splitter = new VariantSplitter();

			VcfImporter reader = new VcfImporter();

			context.updateTask("Load file...", WorkflowContext.RUNNING);
			HashMap<String, Sample> mutationServerSamples = reader.load(file, false);

			context.updateTask("Split Profile into Haplogroups...", WorkflowContext.RUNNING);
			ArrayList<String> profiles = splitter.split(mutationServerSamples);

			context.updateTask("Apply HaploGrep2...", WorkflowContext.RUNNING);
			HaplogroupClassifier classifier = new HaplogroupClassifier();
			SampleFile haplogrepSamples = classifier.calculateHaplogrops(phylotree, profiles);

			Contamination contamination = new Contamination();
			context.updateTask("Detect Contamination...", WorkflowContext.RUNNING);
			contamination.detect(mutationServerSamples, haplogrepSamples.getTestSamples(), output);
			context.endTask("Execution successful.", WorkflowContext.OK);
			return true;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
}