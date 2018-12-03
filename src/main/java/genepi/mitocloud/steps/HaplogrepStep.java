package genepi.mitocloud.steps;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import contamination.HaplogroupClassifier;
import contamination.objects.Sample;
import core.SampleFile;
import genepi.hadoop.common.WorkflowContext;
import genepi.hadoop.common.WorkflowStep;
import importer.VcfImporter;
import phylotree.Phylotree;
import phylotree.PhylotreeManager;
import util.FormatConverter;

public class HaplogrepStep extends WorkflowStep {

	@Override
	public boolean run(WorkflowContext context) {
		return calculateHaplogroups(context);

	}

	private boolean calculateHaplogroups(WorkflowContext context) {

		try {
			Phylotree phylotree = PhylotreeManager.getInstance().getPhylotree("phylotree17.xml", "weights17.txt");

			String input = context.get("files");
			String output = context.get("haplogrep");

			File file = new File(input);

			context.beginTask("Run Haplogrep2");

			VcfImporter reader = new VcfImporter();

			context.updateTask("Load file...", WorkflowContext.RUNNING);
			
			HashMap<String, Sample> mutationServerSamples = reader.load(file, false);

			context.updateTask("Run HaploGrep2...", WorkflowContext.RUNNING);
			
			HaplogroupClassifier classifier = new HaplogroupClassifier();
			
			ArrayList<String> lines = FormatConverter.samplesMapToHsd(mutationServerSamples);
			
			SampleFile haplogrepSamples = classifier.calculateHaplogrops(phylotree, lines);
			
			FormatConverter.createReport(haplogrepSamples.getTestSamples(), output, false);

			return true;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
}
