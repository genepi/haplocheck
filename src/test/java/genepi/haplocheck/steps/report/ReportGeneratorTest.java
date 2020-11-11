package genepi.haplocheck.steps.report;

import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;

import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import genepi.haplocheck.steps.contamination.objects.ContaminationObject;

public class ReportGeneratorTest {

	@Test
	public void testGenerate() throws Exception {

		Gson gson = new Gson();

		Type type = new TypeToken<ArrayList<ContaminationObject>>() {
		}.getType();

		ArrayList<ContaminationObject> contaminationList = gson.fromJson(new FileReader("test-data/report/cont.json"),
				type);

		ReportGenerator generator = new ReportGenerator();
		generator.setContaminationList(contaminationList);
		generator.generate("output2.html");
	}

}
