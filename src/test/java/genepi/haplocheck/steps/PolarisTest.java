package genepi.haplocheck.steps;

import static org.junit.Assert.*;

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
import util.ExportUtils;
import vcf.Sample;

public class PolarisTest {

	@Test
	public void testPolarisBaq() throws Exception {

		Phylotree phylotree = PhylotreeManager.getInstance().getPhylotree("phylotree17.xml", "weights17.txt");
		String folder = "test-data/contamination/polaris/";
		String variantFile = folder + "150g_baq.vcf.gz";
		String output = folder + "150-report-baq.txt";

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

		contamination.writeReport(output, list);

		CsvTableReader readerOut = new CsvTableReader(output, '\t');

		int count = 0;
		while (readerOut.next()) {
			if (readerOut.getString("Contamination").equals(Status.YES.name())) {
				count++;

			}
		}

		assertEquals(2, count);

		FileUtil.deleteFile(output);

	}
	
	@Test
	public void testPolarisNoBaq() throws Exception {

		Phylotree phylotree = PhylotreeManager.getInstance().getPhylotree("phylotree17.xml", "weights17.txt");
		String folder = "test-data/contamination/polaris/";
		String variantFile = folder + "150g_nobaq.vcf.gz";
		String output = folder + "150-report-nobaq.txt";

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

		contamination.writeReport(output, list);

		CsvTableReader readerOut = new CsvTableReader(output, '\t');

		int count = 0;
		while (readerOut.next()) {
			if (readerOut.getString("Contamination").equals(Status.YES.name())) {
				count++;

			}
		}

		assertEquals(2, count);

		FileUtil.deleteFile(output);

	}
	}
