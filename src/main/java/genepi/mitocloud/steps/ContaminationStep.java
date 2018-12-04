package genepi.mitocloud.steps;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;

import com.google.gson.Gson;

import contamination.ContaminationDetection;
import contamination.HaplogroupClassifier;
import contamination.VariantSplitter;
import contamination.objects.ContaminationObject;
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
			String output = context.getConfig("output");
			String outputJson = context.getConfig("outputCont");

			File file = new File(input);

			context.beginTask("Check for Contamination.. ");

			VariantSplitter splitter = new VariantSplitter();

			VcfImporter reader = new VcfImporter();

			context.updateTask("Load file...", WorkflowContext.RUNNING);
			HashMap<String, Sample> mutationServerSamples = reader.load(file, false);

			context.updateTask("Split Profile into Major/Minor Profile...", WorkflowContext.RUNNING);
			ArrayList<String> profiles = splitter.split(mutationServerSamples);

			context.updateTask("Classify Haplogroups...", WorkflowContext.RUNNING);
			HaplogroupClassifier classifier = new HaplogroupClassifier();
			SampleFile haplogrepSamples = classifier.calculateHaplogrops(phylotree, profiles);

			ContaminationDetection contamination = new ContaminationDetection();
			context.updateTask("Detect Contamination...", WorkflowContext.RUNNING);
			ArrayList<ContaminationObject> contaminationList = contamination.detect(mutationServerSamples,
					haplogrepSamples.getTestSamples());

			contamination.writeFile(contaminationList, output);

			String json = new Gson().toJson(contaminationList);
			FileWriter wr = new FileWriter(outputJson);
			wr.write(json);
			wr.close();

			context.endTask("Execution successful.", WorkflowContext.OK);
			return true;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
}
