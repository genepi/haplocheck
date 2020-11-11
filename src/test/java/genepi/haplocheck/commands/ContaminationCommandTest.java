package genepi.haplocheck.commands;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

import genepi.haplocheck.steps.contamination.ContaminationDetection.Status;
import genepi.io.FileUtil;
import genepi.io.table.reader.CsvTableReader;

public class ContaminationCommandTest {

	@Test
	public void testContaminationPipelineWithFolder() throws Exception {
		
		
		String folder = "test-data/contamination/1000G/all";
		ContaminationCommand command = new ContaminationCommand();
		command.setVcf(folder);
		command.setOut(getOutput());
		assertEquals(1, (int) command.call());
	
	}
	
	@Test
	public void testContaminationPipelineWithFile() throws Exception {
		
		String folder = "test-data/contamination/1000G/all/1000g-nobaq.vcf.gz";
		ContaminationCommand command = new ContaminationCommand();
		command.setVcf(folder);
		command.setOut(getOutput());
		assertEquals(0, (int) command.call());
		
		
		CsvTableReader readerOut = new CsvTableReader("test-data/tmp/out.txt", '\t');
		int countHigh = 0;
		while (readerOut.next()) {

			if (readerOut.getString("Contamination Status").equals(Status.YES.name())) {
				countHigh++;
			}
		}

		assertEquals(127, countHigh);
	
	}

	protected String getOutput() {
		
		File file = new File("test-data/tmp");
		if (file.exists()) {
			FileUtil.deleteDirectory(file);
		}
		file.mkdirs();

		return file.getAbsolutePath() + "/out.txt";

	}

}
