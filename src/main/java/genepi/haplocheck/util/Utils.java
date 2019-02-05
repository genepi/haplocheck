package genepi.haplocheck.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;

import core.Polymorphism;
import core.SampleRanges;
import core.TestSample;
import search.ranking.results.RankedResult;

public class Utils {

	public static Collection<File> getVcfFiles(String directoryName) {
		File directory = new File(directoryName);
		return FileUtils.listFiles(directory, new WildcardFileFilter(Arrays.asList("*.vcf.gz", "*.vcf")), null);
	}

	public static void createFakeReport(List<TestSample> sampleCollection, File out) throws IOException {

		StringBuffer result = new StringBuffer();

		Collections.sort((List<TestSample>) sampleCollection);

		result.append(
				"SampleID\tRange\tHaplogroup\tOverall_Rank\tNot_Found_Polys\tFound_Polys\tRemaining_Polys\tAAC_In_Remainings\t Input_Sample\n");

		if (sampleCollection != null) {

			for (TestSample sample : sampleCollection) {

				result.append(sample.getSampleID() + "\t");

				for (RankedResult currentResult : sample.getResults()) {

					SampleRanges range = sample.getSample().getSampleRanges();

					ArrayList<Integer> startRange = range.getStarts();

					ArrayList<Integer> endRange = range.getEnds();

					String resultRange = "";

					for (int i = 0; i < startRange.size(); i++) {
						if (startRange.get(i).equals(endRange.get(i))) {
							resultRange += startRange.get(i) + ";";
						} else {
							resultRange += startRange.get(i) + "-" + endRange.get(i) + ";";
						}
					}
					result.append(resultRange);

					result.append("\t" + currentResult.getHaplogroup());

					result.append("\t" + String.format(Locale.ROOT, "%.4f", currentResult.getDistance()));

					result.append("\t");

					ArrayList<Polymorphism> found = currentResult.getSearchResult().getDetailedResult().getFoundPolys();

					ArrayList<Polymorphism> expected = currentResult.getSearchResult().getDetailedResult()
							.getExpectedPolys();

					Collections.sort(found);

					Collections.sort(expected);

					for (Polymorphism currentPoly : expected) {
						if (!found.contains(currentPoly))
							result.append(" " + currentPoly);
					}

					result.append("\t");

					for (Polymorphism currentPoly : found) {
						result.append(" " + currentPoly);

					}

					result.append("\t");
					ArrayList<Polymorphism> allChecked = currentResult.getSearchResult().getDetailedResult()
							.getRemainingPolysInSample();
					Collections.sort(allChecked);

					for (Polymorphism currentPoly : allChecked) {
						result.append(" " + currentPoly);
					}

					result.append("\t");

					ArrayList<Polymorphism> aac = currentResult.getSearchResult().getDetailedResult()
							.getRemainingPolysInSample();
					Collections.sort(aac);

					result.append("\t");

					ArrayList<Polymorphism> input = sample.getSample().getPolymorphisms();

					Collections.sort(input);

					for (Polymorphism currentPoly : input) {
						result.append(" " + currentPoly);
					}
					result.append("\n");

				}
			}
		}

		FileWriter fileWriter = new FileWriter(out);

		fileWriter.write(result.toString().replace("\t ", "\t"));

		fileWriter.close();

	}

}
