package genepi.haplocheck.steps.contamination;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.junit.Test;

import core.SampleFile;
import genepi.haplocheck.steps.contamination.ContaminationDetection.Status;
import genepi.haplocheck.steps.contamination.objects.ContaminationObject;
import genepi.io.FileUtil;
import genepi.io.table.reader.CsvTableReader;
import importer.VcfImporter;
import phylotree.Phylotree;
import phylotree.PhylotreeManager;
import vcf.Sample;

public class ContaminationDetectionTest {
	
	@Test
	public void testNoVariantsIncluded() throws Exception {

		Phylotree phylotree = PhylotreeManager.getInstance().getPhylotree("phylotree17.xml", "weights17.txt");

		VcfImporter reader = new VcfImporter();

		HashMap<String, Sample> mutationServerSamples = reader.load(new File("test-data/contamination/test-files/no-variants.vcf"), false);
		
		String out = "test-data/contamination/test-files/no-variants.txt";
		
		VariantSplitter splitter = new VariantSplitter(); 
		ArrayList<String> profiles = splitter.split(mutationServerSamples);
		
		HaplogroupClassifier classifier = new HaplogroupClassifier();

		SampleFile haplogrepSamples = classifier.calculateHaplogrops(phylotree, profiles);
		
		ContaminationDetection contamination = new ContaminationDetection();

		ArrayList<ContaminationObject> list = contamination.detect(mutationServerSamples, haplogrepSamples.getTestSamples());
	
		contamination.writeReport(out, list);
		
		CsvTableReader readerContamination = new CsvTableReader(out, '\t');
		readerContamination.next();
		
		assertEquals("0", readerContamination.getString("HomoplasmiesMajor"));
		assertEquals("-1", readerContamination.getString("SampleMeanCoverage"));
		
		FileUtil.deleteFile(out);
	}	

}
