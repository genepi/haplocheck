package genepi.haplocheck.steps;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;

import core.SampleFile;
import genepi.hadoop.common.WorkflowContext;
import genepi.hadoop.common.WorkflowStep;
import genepi.haplocheck.steps.contamination.HaplogroupClassifier;
import genepi.haplocheck.util.Utils;
import importer.VcfImporter;
import phylotree.Phylotree;
import phylotree.PhylotreeManager;
import util.ExportUtils;
import vcf.Sample;

public class HaplogrepStep extends WorkflowStep {

	@Override
	public boolean run(WorkflowContext context) {
		return calculateHaplogroups(context);

	}
	
	private boolean calculateHaplogroups(WorkflowContext context) {

		try {
			Phylotree phylotree = PhylotreeManager.getInstance().getPhylotree("phylotree17.xml", "weights17.txt");

			String input = context.get("files");
			String output = context.getConfig("outputHaplogroups");

			Collection<File> out = Utils.getVcfFiles(input);
			
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
