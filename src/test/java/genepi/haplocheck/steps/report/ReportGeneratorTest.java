package genepi.haplocheck.steps.report;

import org.junit.Test;

public class ReportGeneratorTest {

	@Test
	public void testGenerate() throws Exception {
		
		ReportGenerator generator = new ReportGenerator();
		generator.setContamination("test-data/report/cont.json");
		generator.setSummary("test-data/report/summary.json");
		generator.generate("output.html");
	}
	

}
