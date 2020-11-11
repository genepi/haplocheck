package genepi.haplocheck.steps.report;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.google.common.math.Quantiles;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import genepi.haplocheck.App;
import genepi.haplocheck.steps.contamination.ContaminationDetection.Status;
import genepi.haplocheck.steps.contamination.objects.ContaminationObject;
import lukfor.reports.HtmlReport;

public class ReportGenerator {

	public static final String TEMPLATE_DIRECTORY = "/reports";

	public static final String REPORT_TEMPLATE = "haplocheck.html";

	private ArrayList<ContaminationObject> contaminationList;

	public void setContaminationList(ArrayList<ContaminationObject> contaminationList) {
		this.contaminationList = contaminationList;
	}

	public void generate(String output) throws IOException {

		assert (contaminationList != null);

		HtmlReport report = new HtmlReport(TEMPLATE_DIRECTORY);
		report.setMainFilename(REPORT_TEMPLATE);
		report.set("sum_data", getSummary(contaminationList));
		report.set("cont_data", contaminationList);
		report.set("version", App.VERSION);

		report.generate(new File(output));

	}

	public String toJson(ArrayList<ContaminationObject> contaminationList) throws IOException {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		return gson.toJson(contaminationList);
	}

	private JsonObject getSummary(ArrayList<ContaminationObject> contaminationList) throws IOException {
		int countYes = 0;
		int countNo = 0;
		ArrayList<Integer> coverageList = new ArrayList<Integer>();

		for (ContaminationObject cont : contaminationList) {

			if (cont.getStatus() == Status.YES) {
				countYes++;
			} else if (cont.getStatus() == Status.NO) {
				countNo++;
			}
			coverageList.add(cont.getSampleMeanCoverage());
		}

		JsonObject result = new JsonObject();
		result.add("Yes", new JsonPrimitive(countYes));
		result.add("No", new JsonPrimitive(countNo));
		double coverageMedian = com.google.common.math.Quantiles.median().compute(coverageList);
		double percentile25 = Quantiles.percentiles().index(25).compute(coverageList);
		double percentile75 = Quantiles.percentiles().index(75).compute(coverageList);
		double IQR = percentile75 - percentile25;
		result.add("Coverage", new JsonPrimitive(coverageMedian));
		result.add("Q1", new JsonPrimitive(percentile25));
		result.add("Q3", new JsonPrimitive(percentile75));

		return result;

	}

}
