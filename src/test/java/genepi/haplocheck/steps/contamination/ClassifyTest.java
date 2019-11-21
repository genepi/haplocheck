package genepi.haplocheck.steps.contamination;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import org.junit.Test;

import core.SampleFile;
import genepi.haplocheck.steps.contamination.ContaminationDetection;
import genepi.haplocheck.steps.contamination.ContaminationDetection.Status;
import genepi.haplocheck.steps.contamination.HaplogroupClassifier;
import genepi.haplocheck.steps.contamination.VariantSplitter;
import genepi.haplocheck.steps.contamination.objects.ContaminationObject;
import genepi.io.FileUtil;
import genepi.io.table.reader.CsvTableReader;
import importer.VcfImporter;
import phylotree.Phylotree;
import phylotree.PhylotreeManager;
import vcf.Sample;

public class ClassifyTest {

	@Test
	public void testHG00097() throws Exception {

		Phylotree phylotree = PhylotreeManager.getInstance().getPhylotree("phylotree17.xml", "weights17.txt");

		VcfImporter reader = new VcfImporter();

		HashMap<String, Sample> mutationServerSamples = reader.load(new File("test-data/contamination/1000g-sample/HG00097.vcf"), false);
		
		String out = "test-data/contamination/1000g-sample/1000g-sample-report.txt";
		String outJson = "test-data/contamination/1000g-sample/1000g-sample-report.json";
		
		VariantSplitter splitter = new VariantSplitter();

		ArrayList<String> profiles = splitter.split(mutationServerSamples);
		
		HaplogroupClassifier classifier = new HaplogroupClassifier();

		SampleFile haplogrepSamples = classifier.calculateHaplogrops(phylotree, profiles);
		
		ContaminationDetection contamination = new ContaminationDetection();

		ArrayList<ContaminationObject> list = contamination.detect(mutationServerSamples, haplogrepSamples.getTestSamples());
	
		contamination.writeReport(out, list);
		
		contamination.writeReportAsJson(outJson, list);
		
		assertEquals("T2f1a1", haplogrepSamples.getTestSamples().get(0).getTopResult().getHaplogroup().toString());
		
		CsvTableReader readerContamination = new CsvTableReader(out, '\t');
		readerContamination.next();
		
		assertEquals(Status.NO.name(), readerContamination.getString("Contamination Status"));
		assertEquals("39", readerContamination.getString("Overall Homoplasmies"));
		assertEquals("0", readerContamination.getString("Overall Heteroplasmies"));
		assertEquals("36", readerContamination.getString("Amount Major Homoplasmies"));
		assertEquals(" ", readerContamination.getString("Major Heteroplasmy Level"));
		assertEquals("T2f1a1", readerContamination.getString("Major Haplogroup"));
		assertEquals(0.919, readerContamination.getDouble("Minor Haplogroup Quality"),0.01);
		
		FileUtil.deleteFile(out);

	}
	
	@Test
	public void testContaminatedSample() throws Exception {

		Phylotree phylotree = PhylotreeManager.getInstance().getPhylotree("phylotree17.xml", "weights17.txt");

		VcfImporter reader = new VcfImporter();

		HashMap<String, Sample> mutationServerSamples = reader.load(new File("test-data/contamination/lab-mixture/mixtures.vcf.gz"), false);
		
		String out = "test-data/contamination/lab-mixture/report.txt";
		String outJson = "test-data/contamination/lab-mixture/report.json";
		
		VariantSplitter splitter = new VariantSplitter();

		ArrayList<String> profiles = splitter.split(mutationServerSamples);
		
		HaplogroupClassifier classifier = new HaplogroupClassifier();

		SampleFile haplogrepSamples = classifier.calculateHaplogrops(phylotree, profiles);
		
		ContaminationDetection contamination = new ContaminationDetection();

		ArrayList<ContaminationObject> list = contamination.detect(mutationServerSamples, haplogrepSamples.getTestSamples());
	
		contamination.writeReport(out, list);
		
		contamination.writeReportAsJson(outJson, list);
		
		//FileUtil.deleteFile(out);

	}

	@Test
	public void testSplitAndClassify() throws Exception {

		Phylotree phylotree = PhylotreeManager.getInstance().getPhylotree("phylotree17.xml", "weights17.txt");

		VcfImporter reader = new VcfImporter();

		HashMap<String, Sample> mutationServerSamples = reader.load(new File("test-data/contamination/lab-mixture/variants-mixture.vcf"), false);

		String out = "test-data/contamination/lab-mixture/variants-mixture-report.txt";
		
		VariantSplitter splitter = new VariantSplitter();

		ArrayList<String> profiles = splitter.split(mutationServerSamples);

		HashSet<String> set = new HashSet<String>();

		String[] splits = profiles.get(0).split("\t");

		int count = 0;

		for (int i = 3; i < splits.length; i++) {
			count++;
			set.add(splits[i]);
		}

		assertEquals(25, count);

		HaplogroupClassifier classifier = new HaplogroupClassifier();

		SampleFile haplogrepSamples = classifier.calculateHaplogrops(phylotree, profiles);

		assertEquals("H1", haplogrepSamples.getTestSamples().get(0).getTopResult().getHaplogroup().toString());

		assertEquals("U5a2e", haplogrepSamples.getTestSamples().get(1).getTopResult().getHaplogroup().toString());

		ContaminationDetection contamination = new ContaminationDetection();

		ArrayList<ContaminationObject> list = contamination.detect(mutationServerSamples, haplogrepSamples.getTestSamples());
		
		contamination.writeReport(out, list);


		CsvTableReader readerContamination = new CsvTableReader(out, '\t');
		// get first line
		readerContamination.next();

		assertEquals(Status.YES.name(), readerContamination.getString("Contamination Status"));
		assertEquals("6", readerContamination.getString("Amount Major Homoplasmies"));
		assertEquals("7", readerContamination.getString("Overall Homoplasmies"));
		assertEquals("0.987", readerContamination.getString("Major Heteroplasmy Level"));
		assertEquals("6", readerContamination.getString("Amount Minor Homoplasmies"));
		assertEquals(0.011, readerContamination.getDouble("Minor Heteroplasmy Level"),0.001);
		assertEquals("10", readerContamination.getString("Amount Minor Heteroplasmies"));
		assertEquals("18", readerContamination.getString("Overall Heteroplasmies"));
		assertEquals("H1", readerContamination.getString("Major Haplogroup"));
		assertEquals("U5a2e", readerContamination.getString("Minor Haplogroup"));

		FileUtil.deleteFile(out);
	}

}
