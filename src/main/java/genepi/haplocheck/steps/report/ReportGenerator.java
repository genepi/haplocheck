package genepi.haplocheck.steps.report;

import java.io.File;
import java.io.IOException;

import genepi.io.FileUtil;
import lukfor.reports.HtmlReport;

public class ReportGenerator {

	public static final String TEMPLATE_DIRECTORY = "/reports";

	public static final String REPORT_TEMPLATE = "haplocheck.html";
	
	private String summary;
	
	private String contamination;

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getContamination() {
		return contamination;
	}

	public void setContamination(String contamination) {
		this.contamination = contamination;
	}
	
	public void generate(String output) throws IOException {
		
		assert(summary != null);
		assert(contamination != null);
		
		HtmlReport report = new HtmlReport(TEMPLATE_DIRECTORY);
		report.setMainFilename(REPORT_TEMPLATE);

		report.set("sum_data", FileUtil.readFileAsString(summary));
		report.set("cont_data", FileUtil.readFileAsString(contamination));
		
		
		report.generate(new File(output));
		
	}
	
}
