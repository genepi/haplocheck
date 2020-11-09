package genepi.haplocheck.steps.report;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.junit.Test;

public class ReportGeneratorTest {

	@Test
	public void testGenerate() throws Exception {
		
		ReportGenerator generator = new ReportGenerator();
		
		generator.setContamination(readFile("test-data/report/cont.json"));
		generator.setSummary(readFile("test-data/report/summary.json"));
		generator.generate("output2.html");
	}
	
	
	 private static String readFile(String filePath) 
	    {
	        StringBuilder contentBuilder = new StringBuilder();
	 
	        try (Stream<String> stream = Files.lines( Paths.get(filePath), StandardCharsets.UTF_8)) 
	        {
	            stream.forEach(s -> contentBuilder.append(s).append("\n"));
	        }
	        catch (IOException e) 
	        {
	            e.printStackTrace();
	        }
	 
	        return contentBuilder.toString();
	    }
	

}
