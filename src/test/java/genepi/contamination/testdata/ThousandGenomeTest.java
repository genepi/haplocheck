package genepi.contamination.testdata;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import org.junit.Test;

import contamination.ContaminationDetection;
import contamination.ContaminationDetection.Status;
import contamination.HaplogroupClassifier;
import contamination.VariantSplitter;
import contamination.objects.ContaminationObject;
import contamination.objects.Sample;
import core.SampleFile;
import genepi.io.FileUtil;
import genepi.io.table.reader.CsvTableReader;
import importer.VcfImporter;
import phylotree.Phylotree;
import phylotree.PhylotreeManager;
import util.ExportUtils;

public class ThousandGenomeTest {

	@Test
	public void testHighChipMix1000G() throws Exception {

		Phylotree phylotree = PhylotreeManager.getInstance().getPhylotree("phylotree17.xml", "weights17.txt");
		String folder = "test-data/contamination/1000G/high-chip-mix/";
		String variantFile = folder + "high-chip-mix-1000G.vcf.gz";
		String output = folder + "chip-mix-report.txt";

		VariantSplitter splitter = new VariantSplitter();

		VcfImporter reader = new VcfImporter();

		HashMap<String, Sample> mutationServerSamples = reader.load(new File(variantFile), false);

		ArrayList<String> profiles = splitter.split(mutationServerSamples);

		HashSet<String> set = new HashSet<String>();

		String[] splits = profiles.get(0).split("\t");

		for (int i = 3; i < splits.length; i++) {
			set.add(splits[i]);
		}

		ContaminationDetection contamination = new ContaminationDetection();

		HaplogroupClassifier classifier = new HaplogroupClassifier();
		SampleFile haplogrepSamples = classifier.calculateHaplogrops(phylotree, profiles);

		ArrayList<ContaminationObject> list = contamination.detect(mutationServerSamples, haplogrepSamples.getTestSamples());

		contamination.writeFile(list, output);

		CsvTableReader readerOut = new CsvTableReader(output, '\t');

		int count = 0;
		while (readerOut.next()) {
			if (readerOut.getString("Contamination").equals(Status.YES.name())) {
				count++;

			}
		}

		assertEquals(26, count);

		FileUtil.deleteFile(output);

	}

	@Test
	public void testHighFreeMix1000G() throws Exception {

		Phylotree phylotree = PhylotreeManager.getInstance().getPhylotree("phylotree17.xml", "weights17.txt");
		String folder = "test-data/contamination/1000G/high-free-mix/";
		String variantFile = folder + "high-free-mix-1000G.vcf.gz";
		String output = folder + "chip-mix-report.txt";

		VcfImporter reader = new VcfImporter();

		HashMap<String, Sample> mutationServerSamples = reader.load(new File(variantFile), false);

		VariantSplitter splitter = new VariantSplitter();
		ArrayList<String> profiles = splitter.split(mutationServerSamples);

		HashSet<String> set = new HashSet<String>();

		String[] splits = profiles.get(0).split("\t");

		for (int i = 3; i < splits.length; i++) {
			set.add(splits[i]);
		}

		HaplogroupClassifier classifier = new HaplogroupClassifier();
		SampleFile haplogrepSamples = classifier.calculateHaplogrops(phylotree, profiles);

		ContaminationDetection contamination = new ContaminationDetection();

		ArrayList<ContaminationObject> list = contamination.detect(mutationServerSamples, haplogrepSamples.getTestSamples());

		contamination.writeFile(list, output);

		CsvTableReader readerOut = new CsvTableReader(output, '\t');
		int count = 0;
		while (readerOut.next()) {
			if (readerOut.getString("Contamination").equals(Status.YES.name())) {
				count++;

			}
		}

		assertEquals(7, count);

		FileUtil.deleteFile(output);

	}

	@Test
	public void testNoContamination1000G() throws Exception {

		Phylotree phylotree = PhylotreeManager.getInstance().getPhylotree("phylotree17.xml", "weights17.txt");
		String folder = "test-data/contamination/1000G/no-contamination/";
		String variantFile = folder + "no-contamination-1000G.vcf.gz";
		String output = folder + "no-contamination-report.txt";

		VcfImporter reader = new VcfImporter();

		HashMap<String, Sample> mutationServerSamples = reader.load(new File(variantFile), false);

		ContaminationDetection contamination = new ContaminationDetection();

		VariantSplitter splitter = new VariantSplitter();
		ArrayList<String> profiles = splitter.split(mutationServerSamples);

		HashSet<String> set = new HashSet<String>();

		String[] splits = profiles.get(0).split("\t");

		for (int i = 3; i < splits.length; i++) {
			set.add(splits[i]);
		}

		HaplogroupClassifier classifier = new HaplogroupClassifier();
		SampleFile haplogrepSamples = classifier.calculateHaplogrops(phylotree, profiles);

		ArrayList<ContaminationObject> list = contamination.detect(mutationServerSamples, haplogrepSamples.getTestSamples());

		contamination.writeFile(list, output);

		CsvTableReader readerOut = new CsvTableReader(output, '\t');
		int countHigh = 0;
		int countLow = 0;
		while (readerOut.next()) {
			if (readerOut.getString("Contamination").equals(Status.YES.name())) {
				countHigh++;

			}
			if (readerOut.getString("Contamination").equals(Status.YES.name())) {
				countLow++;

			}
		}

		assertEquals(0, countHigh);
		assertEquals(0, countLow);
		FileUtil.deleteFile(output);

	}

	@Test
	public void testPossibleSwap() throws Exception {

		Phylotree phylotree = PhylotreeManager.getInstance().getPhylotree("phylotree17.xml", "weights17.txt");
		String folder = "test-data/contamination/1000G/possible-swap/";
		String variantFile = folder + "possible-swap-1000G.vcf.gz";
		String output = folder + "possible-swap-report.txt";

		VcfImporter reader = new VcfImporter();
		HashMap<String, Sample> mutationServerSamples = reader.load(new File(variantFile), false);

		VariantSplitter splitter = new VariantSplitter();
		ArrayList<String> profiles = splitter.split(mutationServerSamples);

		HashSet<String> set = new HashSet<String>();

		String[] splits = profiles.get(0).split("\t");

		for (int i = 3; i < splits.length; i++) {
			set.add(splits[i]);
		}

		ContaminationDetection contamination = new ContaminationDetection();

		HaplogroupClassifier classifier = new HaplogroupClassifier();
		SampleFile haplogrepSamples = classifier.calculateHaplogrops(phylotree, profiles);

		ArrayList<ContaminationObject> list = contamination.detect(mutationServerSamples, haplogrepSamples.getTestSamples());

		contamination.writeFile(list, output);

		CsvTableReader readerOut = new CsvTableReader(output, '\t');
		int count = 0;
		while (readerOut.next()) {
			if (readerOut.getString("Contamination").equals(Status.YES.name())) {
				count++;

			}
		}

		assertEquals(0, count);

		FileUtil.deleteFile(output);

	}

	@Test
	public void testBaq1000G() throws Exception {

		Phylotree phylotree = PhylotreeManager.getInstance().getPhylotree("phylotree17.xml", "weights17.txt");
		String folder = "test-data/contamination/1000G/final-samples/";
		String variantFile = folder + "1000g_baq.vcf.gz";
		String output = folder + "1000g-report.txt";

		VcfImporter reader2 = new VcfImporter();
		HashMap<String, Sample> mutationServerSamples = reader2.load(new File(variantFile), false);

		VariantSplitter splitter = new VariantSplitter();

		ArrayList<String> profiles = splitter.split(mutationServerSamples);

		HashSet<String> set = new HashSet<String>();

		String[] splits = profiles.get(0).split("\t");

		for (int i = 3; i < splits.length; i++) {
			set.add(splits[i]);
		}

		ContaminationDetection contamination = new ContaminationDetection();

		HaplogroupClassifier classifier = new HaplogroupClassifier();
		SampleFile haplogrepSamples = classifier.calculateHaplogrops(phylotree, profiles);

		ExportUtils.createHsdInput(haplogrepSamples.getTestSamples(), "/home/seb/Desktop/contaminated.hsd");

		ArrayList<ContaminationObject> list = contamination.detect(mutationServerSamples, haplogrepSamples.getTestSamples());

		contamination.writeFile(list, output);

		CsvTableReader readerOut = new CsvTableReader(output, '\t');
		int countHigh = 0;
		while (readerOut.next()) {

			if (readerOut.getString("Contamination").equals(Status.YES.name())) {
				countHigh++;
			}
		}

		assertEquals(91, countHigh);
		FileUtil.deleteFile(output);
	}
	
	@Test
	public void testNoBaq1000G() throws Exception {

		Phylotree phylotree = PhylotreeManager.getInstance().getPhylotree("phylotree17.xml", "weights17.txt");
		String folder = "test-data/contamination/1000G/final-samples/";
		String variantFile = folder + "1000g_nobaq.vcf.gz";
		String output = folder + "1000g-report.txt";

		VcfImporter reader2 = new VcfImporter();
		HashMap<String, Sample> mutationServerSamples = reader2.load(new File(variantFile), false);

		VariantSplitter splitter = new VariantSplitter();

		ArrayList<String> profiles = splitter.split(mutationServerSamples);

		HashSet<String> set = new HashSet<String>();

		String[] splits = profiles.get(0).split("\t");

		for (int i = 3; i < splits.length; i++) {
			set.add(splits[i]);
		}

		ContaminationDetection contamination = new ContaminationDetection();

		HaplogroupClassifier classifier = new HaplogroupClassifier();
		SampleFile haplogrepSamples = classifier.calculateHaplogrops(phylotree, profiles);

		ExportUtils.createHsdInput(haplogrepSamples.getTestSamples(), "/home/seb/Desktop/contaminated.hsd");

		ArrayList<ContaminationObject> list = contamination.detect(mutationServerSamples, haplogrepSamples.getTestSamples());

		contamination.writeFile(list, output);

		CsvTableReader readerOut = new CsvTableReader(output, '\t');
		int countHigh = 0;
		while (readerOut.next()) {

			if (readerOut.getString("Contamination").equals(Status.YES.name())) {
				countHigh++;
			}
		}

		assertEquals(89, countHigh);
		FileUtil.deleteFile(output);
	}

	@Test
	public void testBaq1000GYeContaminated() throws Exception {

		Phylotree phylotree = PhylotreeManager.getInstance().getPhylotree("phylotree17.xml", "weights17.txt");
		String folder = "test-data/contamination/1000G/final-samples/";
		String variantFile = folder + "1000g_baq.vcf.gz";
		String output = folder + "1000g-report.txt";

		VcfImporter reader2 = new VcfImporter();
		HashMap<String, Sample> mutationServerSamples = reader2.load(new File(variantFile), false);

		VariantSplitter splitter = new VariantSplitter();

		ArrayList<String> profiles = splitter.split(mutationServerSamples);

		HashSet<String> set = new HashSet<String>();

		String[] splits = profiles.get(0).split("\t");

		for (int i = 3; i < splits.length; i++) {
			set.add(splits[i]);
		}

		ContaminationDetection contamination = new ContaminationDetection();

		HaplogroupClassifier classifier = new HaplogroupClassifier();
		SampleFile haplogrepSamples = classifier.calculateHaplogrops(phylotree, profiles);

		ArrayList<ContaminationObject> list = contamination.detect(mutationServerSamples, haplogrepSamples.getTestSamples());

		contamination.writeFile(list, output);

		CsvTableReader readerResult = new CsvTableReader(output, '\t');

		CsvTableReader readerYe = new CsvTableReader("test-data/contamination/1000G/final-samples/1000g_ye.txt", '\t');
		HashSet<String> contSamples = new HashSet<String>();
		while (readerYe.next()) {
			contSamples.add(readerYe.getString("Sample"));
		}

		int found = 0;
		while (readerResult.next()) {
			String sample = readerResult.getString("SampleID").split("\\.")[0];
			String status = readerResult.getString("Contamination");

			if (status.equals(Status.YES.name()) || status.equals(Status.YES.name())) {
				if (contSamples.contains(sample)) {
					found++;
				}
			}

		}

		readerYe.close();
		readerResult.close();

		System.out.println(found);
	}

}
