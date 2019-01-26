package genepi.haplocheck.steps;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;

import contamination.HaplogroupClassifier;
import contamination.objects.Sample;
import core.SampleFile;
import genepi.hadoop.common.WorkflowContext;
import genepi.hadoop.common.WorkflowStep;
import importer.VcfImporter;
import phylotree.Phylotree;
import phylotree.PhylotreeManager;
import util.ExportUtils;

public class HaplogrepStep extends WorkflowStep {

	@Override
	public boolean run(WorkflowContext context) {
		return calculateHaplogroups(context);

	}
	
	Collection<File>  getVcfFiles(String directoryName)
	{
	    File directory = new File(directoryName);
	    return FileUtils.listFiles(directory, new WildcardFileFilter("*.vcf.gz"), null);
	}

	private boolean calculateHaplogroups(WorkflowContext context) {

		try {
			Phylotree phylotree = PhylotreeManager.getInstance().getPhylotree("phylotree17.xml", "weights17.txt");

			String input = context.get("files");
			String output = context.getConfig("outputHaplogroups");

			Collection<File> out = getVcfFiles(input);
			
			if(out.size() > 1) {
				context.endTask("Currently only 1 VCF file is supported!", WorkflowContext.ERROR);
			}
			
			File file = out.iterator().next();

			context.beginTask("Run Haplogrep2");

			VcfImporter reader = new VcfImporter();

			context.updateTask("Load file...", WorkflowContext.RUNNING);
			
			HashMap<String, Sample> mutationServerSamples = reader.load(file, false);

			context.updateTask("Classify Haplogroups...", WorkflowContext.RUNNING);
			
			HaplogroupClassifier classifier = new HaplogroupClassifier();
			
			ArrayList<String> lines = ExportUtils.samplesMapToHsd(mutationServerSamples);
			
			SampleFile haplogrepSamples = classifier.calculateHaplogrops(phylotree, lines);
			
			ExportUtils.createReport(haplogrepSamples.getTestSamples(), output, true);

			context.endTask("Execution successful.", WorkflowContext.OK);
			
			return true;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			context.endTask("Execution failed.", WorkflowContext.ERROR);
			return false;
		}
	}
}
