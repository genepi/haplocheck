package genepi.haplocheck.steps;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.junit.Test;

import core.SampleFile;
import genepi.haplocheck.steps.contamination.ContaminationDetection;
import genepi.haplocheck.steps.contamination.HaplogroupClassifier;
import genepi.haplocheck.steps.contamination.VariantSplitter;
import genepi.haplocheck.steps.contamination.ContaminationDetection.Status;
import genepi.haplocheck.steps.contamination.objects.ContaminationObject;
import genepi.io.FileUtil;
import genepi.io.table.reader.CsvTableReader;
import importer.VcfImporter;
import phylotree.Phylotree;
import phylotree.PhylotreeManager;
import util.ExportUtils;
import vcf.Sample;

public class LabMixturesTest {

	@Test
	public void testPGMs() throws Exception {

		Phylotree phylotree = PhylotreeManager.getInstance().getPhylotree("phylotree17.xml", "weights17.txt");
		String folder = "test-data/contamination/lab-mixture/";
		String variantFile = folder + "mixtures.vcf.gz";
		String output = folder + "mixtures.out";

		VariantSplitter splitter = new VariantSplitter();

		VcfImporter reader = new VcfImporter();

		HashMap<String, Sample> mutationServerSamples = reader.load(new File(variantFile), false);

		ArrayList<String> profiles = splitter.split(mutationServerSamples);

		ContaminationDetection contamination = new ContaminationDetection();

		HaplogroupClassifier classifier = new HaplogroupClassifier();
		SampleFile haplogrepSamples = classifier.calculateHaplogroups(phylotree, profiles);

		ArrayList<ContaminationObject> list = contamination.detect(mutationServerSamples, haplogrepSamples.getTestSamples());

		contamination.writeTextualReport(output, list);

		CsvTableReader readerOut = new CsvTableReader(output, '\t');

		int count = 0;
		while (readerOut.next()) {
			if (readerOut.getString("Contamination Status").equals(Status.YES.name())) {
				count++;

			}
		}

		assertEquals(4, count);

		FileUtil.deleteFile(output);
		
		//ExportUtils.createHsdInput(haplogrepSamples.getTestSamples(), "/home/seb/Desktop/mixtures.hsd");

	}
}
