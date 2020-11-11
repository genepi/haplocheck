package genepi.haplocheck.steps;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipException;

import org.junit.Test;

import cloudgene.sdk.internal.WorkflowStep;
import genepi.haplocheck.steps.contamination.ContaminationDetection.Status;
import genepi.haplocheck.util.WorkflowTestContext;
import genepi.io.FileUtil;
import genepi.io.table.reader.CsvTableReader;

public class ContaminationTest {

	@Test
	public void testContaminationPipelineWithFolder() throws IOException, ZipException {
		
		String folder = "test-data/contamination/1000G/all";
		WorkflowTestContext context = buildContext(folder);
		ContaminationStep contStep = new ContaminationStep();
		boolean result = run(context, contStep);
		assertFalse(result);
	
	}
	
	@Test
	public void testContaminationPipelineWith1000GFile() throws IOException, ZipException {
		
		String folder = "test-data/contamination/1000G/all/1000g-nobaq.vcf.gz";
		WorkflowTestContext context = buildContext(folder);
		ContaminationStep contStep = new ContaminationStep();
		boolean result = run(context, contStep);
		assertTrue(result);
		
		
		CsvTableReader readerOut = new CsvTableReader("test-data/tmp/out.txt", '\t');
		int countHigh = 0;
		while (readerOut.next()) {

			if (readerOut.getString("Contamination Status").equals(Status.YES.name())) {
				countHigh++;
			}
		}

		assertEquals(127, countHigh);
	
	}
	
	@Test
	public void testContaminationPipelineWithVerifyBamExcludedSamples() throws IOException, ZipException {
		
		String folder = "test-data/contamination/1000G/1000g-filtered/chip-or-free-mix.vcf.gz";
		WorkflowTestContext context = buildContext(folder);
		ContaminationStep contStep = new ContaminationStep();
		boolean result = run(context, contStep);
		assertTrue(result);
		
		
		CsvTableReader readerOut = new CsvTableReader("test-data/tmp/out.txt", '\t');
		int countHigh = 0;
		while (readerOut.next()) {

			if (readerOut.getString("Contamination Status").equals(Status.YES.name())) {
				countHigh++;
			}
		}

		assertEquals(11, countHigh);
		
		
		File readRaw = new File("test-data/tmp/out.raw.txt");
		assertTrue(readRaw.exists());
	
	}

	protected WorkflowTestContext buildContext(String folder) {
		
		WorkflowTestContext context = new WorkflowTestContext();
		File file = new File("test-data/tmp");
		if (file.exists()) {
			FileUtil.deleteDirectory(file);
		}
		file.mkdirs();

		// currently a directory required, since I store the VCF file in the input
		// directory. change!
		context.setInput("files", folder);
		context.setConfig("output", file.getAbsolutePath() + "/out.txt");
		context.setConfig("outputReport", file.getAbsolutePath() + "/out.html");
		context.setConfig("outputRaw", file.getAbsolutePath() + "/out.raw.txt");
		boolean raw = true;
		context.setConfig("raw", String.valueOf(raw));
		return context;

	}

	protected boolean run(WorkflowTestContext context, WorkflowStep step) {
		step.setup(context);
		return step.run(context);
	}

}
