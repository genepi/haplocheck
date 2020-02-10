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

		SampleFile haplogrepSamples = classifier.calculateHaplogroups(phylotree, profiles);
		
		ContaminationDetection contamination = new ContaminationDetection();

		ArrayList<ContaminationObject> list = contamination.detect(mutationServerSamples, haplogrepSamples.getTestSamples());
	
		contamination.writeTextualReport(out, list);
		
		CsvTableReader readerContamination = new CsvTableReader(out, '\t');
		readerContamination.next();
		
		assertEquals("0", readerContamination.getString("Major Homoplasmies Count"));
		assertEquals("-1", readerContamination.getString("Sample Coverage"));
		
		FileUtil.deleteFile(out);
	}	
	
	@Test
	public void testSpaceinInput() throws Exception {

		Phylotree phylotree = PhylotreeManager.getInstance().getPhylotree("phylotree17.xml", "weights17.txt");

		VcfImporter reader = new VcfImporter();

		HashMap<String, Sample> mutationServerSamples = reader.load(new File("test-data/contamination/test-files/HG00097-with-spaces.vcf"), false);
		
		String out = "test-data/contamination/test-files/report.txt";
		
		VariantSplitter splitter = new VariantSplitter(); 
		ArrayList<String> profiles = splitter.split(mutationServerSamples);
		
		HaplogroupClassifier classifier = new HaplogroupClassifier();

		SampleFile haplogrepSamples = classifier.calculateHaplogroups(phylotree, profiles);
		
		ContaminationDetection contamination = new ContaminationDetection();

		ArrayList<ContaminationObject> list = contamination.detect(mutationServerSamples, haplogrepSamples.getTestSamples());
	
		contamination.writeTextualReport(out, list);
		
		CsvTableReader readerContamination = new CsvTableReader(out, '\t');
		readerContamination.next();
		
		assertEquals(Status.NO.name(), readerContamination.getString("Contamination Status"));
		assertEquals("39", readerContamination.getString("Overall Homoplasmies"));
		assertEquals("0", readerContamination.getString("Overall Heteroplasmies"));
		assertEquals("36", readerContamination.getString("Major Homoplasmies Count"));
		assertEquals("0.000", readerContamination.getString("Major Heteroplasmy Level"));
		assertEquals("T2f1a1", readerContamination.getString("Major Haplogroup"));
		assertEquals(0.919, readerContamination.getDouble("Minor Haplogroup Quality"),0.01);
		
		//FileUtil.deleteFile(out);
	}	

}
