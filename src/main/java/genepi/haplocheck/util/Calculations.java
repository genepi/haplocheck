package genepi.haplocheck.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import genepi.io.table.reader.CsvTableReader;

public class Calculations {

	public static void calc() throws IOException {
		CsvTableReader readerVerifyBam = new CsvTableReader("test-data/contamination/1000G/verifybam/verifybamWith1000GStatus.txt", '\t');

		String input = "test-data/contamination/1000G/final-samples/1000g-report.txt";
		input = "test-data/contamination/1000G/high-chip-mix/chip-mix-report.txt";
		String output = "test-data/contamination/1000G/final-samples/report-verifybam.txt";
		CsvTableReader readerContamination = new CsvTableReader(input, '\t');

		FileWriter writer = new FileWriter(output);
		writer.write("SAMPLE" + "\t" + "CONT_FREE" + "\t" + "CONT_MIX" + "\t" + "MINOR_LEVEL" + "\t" + "STATUS" + "\n");
		HashMap<String, String> samples = new HashMap<String, String>();

		while (readerContamination.next()) {
			String id = readerContamination.getString("SampleID");
			id = id.split("\\.", 2)[0];
			String level = readerContamination.getString("MinorMeanHetLevel");
			String status = readerContamination.getString("Contamination");

			if (status.contains("HIGH")) {
				samples.put(id, level + "\t" + status);
			}
		}

		while (readerVerifyBam.next()) {
			String id = readerVerifyBam.getString("SampleID").split("\\.", 2)[0];
			String free = readerVerifyBam.getString("free_contam");
			String chip = readerVerifyBam.getString("chip_contam");
			String vcf = readerVerifyBam.getString("vcf");
			String included = readerVerifyBam.getString("1000g");
			String add = samples.get(id);

			// verifyBamID includes also second chip affy
			if (add != null && vcf.contains("omni")) {
				writer.write(id + "\t" + free + "\t" + chip + "\t" + included + "\t" + add + "\n");
			}
		}
		writer.close();
		
		System.out.println("file written to " + new File(output).getAbsolutePath());
	}

	public static void main(String[] args) throws IOException {
		calc();
		// TODO Auto-generated method stub

	}

}
