package genepi.haplocheck.steps.contamination;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import core.Haplogroup;
import core.Polymorphism;
import core.TestSample;
import genepi.haplocheck.steps.contamination.objects.ContaminationObject;
import genepi.io.table.writer.CsvTableWriter;
import phylotree.Phylotree;
import phylotree.PhylotreeManager;
import vcf.Sample;
import vcf.Variant;

public class ContaminationDetection {

	public enum Status {
		YES, NO;
	}

	private int settingAmountHigh = 3;
	private int settingAmountLow = 2;
	private double settingHgQuality = 0.5;

	public ArrayList<ContaminationObject> detect(HashMap<String, Sample> mutationSamples, ArrayList<TestSample> haplogrepSamples) {

		int countEntries = 0;
		int countPossibleContaminated = 0;
		int countContaminated = 0;
		int countNone = 0;
		
		ArrayList<ContaminationObject> contaminationList = new ArrayList<ContaminationObject>();
		
		Collections.sort((List<TestSample>) haplogrepSamples);

		Phylotree phylotree = PhylotreeManager.getInstance().getPhylotree("phylotree17.xml", "weights17.txt");

		NumberFormat formatter = new DecimalFormat("#0.000");

		try {

			for (int i = 0; i < haplogrepSamples.size(); i += 2) {

				countEntries++;
				int distanceHG = 0;
				Status status;

				TestSample majorSample = haplogrepSamples.get(i);
				TestSample minorSample = haplogrepSamples.get(i + 1);

				ArrayList<Polymorphism> foundMajor = majorSample.getTopResult().getSearchResult().getDetailedResult().getFoundPolys();
				ArrayList<Polymorphism> expectedMajor = majorSample.getTopResult().getSearchResult().getDetailedResult().getExpectedPolys();
				ArrayList<Polymorphism> foundMinor = minorSample.getTopResult().getSearchResult().getDetailedResult().getFoundPolys();
				ArrayList<Polymorphism> expectedMinor = minorSample.getTopResult().getSearchResult().getDetailedResult().getExpectedPolys();

				int notFoundMajor = countNotFound(foundMajor, expectedMajor);
				int notFoundMinor = countNotFound(foundMinor, expectedMinor);

				ContaminationObject centry = new ContaminationObject();
				centry.setId(majorSample.getSampleID().split("_maj")[0]);
				double hgQualityMajor = majorSample.getTopResult().getDistance();
				double hgQualityMinor = minorSample.getTopResult().getDistance();

				Sample currentSample = mutationSamples.get(centry.getId());

				int sampleHomoplasmies = currentSample.getAmountHomoplasmies();
				int sampleHeteroplasmies = currentSample.getAmountHeteroplasmies();

				int meanCoverageSample = (int) currentSample.getSumCoverage() / currentSample.getAmountVariants();
				double meanHetLevelSample = currentSample.getSumHeteroplasmyLevel() / currentSample.getAmountHeteroplasmies();

				centry.setHgMajor(majorSample.getTopResult().getHaplogroup().toString());
				centry.setHgMinor(minorSample.getTopResult().getHaplogroup().toString());

				int homoplasmiesMajor = countHomoplasmies(currentSample, foundMajor);
				int homoplasmiesMinor = countHomoplasmies(currentSample, foundMinor);

				int heteroplasmiesMajor = countHeteroplasmiesMajor(currentSample, foundMajor);
				int heteroplasmiesMinor = countHeteroplasmiesMinor(currentSample, foundMinor);

				double meanHeteroplasmyMajor = calcMeanHeteroplasmy(currentSample, foundMajor, true);
				double meanHeteroplasmyMinor = calcMeanHeteroplasmy(currentSample, foundMinor, false);

				ArrayList<Polymorphism> diffMajorMinor = calculateHaplogroupDifference(expectedMajor, expectedMinor);
				ArrayList<Polymorphism> diffMinorMajor = calculateHaplogroupDifference(expectedMinor, expectedMajor);

				if (!centry.getHgMajor().equals(centry.getHgMinor())) {

					distanceHG = calcDistance(centry, phylotree);

					if ((heteroplasmiesMajor >= settingAmountHigh || heteroplasmiesMinor >= settingAmountHigh) && distanceHG >= settingAmountHigh
							&& hgQualityMajor > settingHgQuality && hgQualityMinor > settingHgQuality) {
						countContaminated++;
						status = Status.YES;
						// TODO check mutation rate if heteroplasmies > 5
					} /*
						 * else if ((heteroplasmiesMinor >= settingAmountLow || distanceHG >=
						 * settingAmountLow) && hgQualityMajor > settingHgQuality && hgQualityMinor >
						 * settingHgQuality) { countPossibleContaminated++; status = Status.LOW; }
						 */ else {
						countNone++;
						status = Status.NO;
					}
				} else {
					countNone++;
					status = Status.NO;
				}
				
				centry.setStatus(status.toString());
				centry.setSampleHomoplasmies(sampleHomoplasmies);
				centry.setSampleHeteroplasmies(sampleHeteroplasmies);
				centry.setSampleMeanCoverage(meanCoverageSample);
				centry.setHgMajorQ(formatter.format(hgQualityMajor));
				centry.setHgMinorQ(formatter.format(hgQualityMinor));
				centry.setHomoplasmiesMajor(homoplasmiesMajor);
				centry.setHomoplasmiesMinor(homoplasmiesMinor);
				centry.setHeteroplasmiesMajor(heteroplasmiesMajor);
				centry.setHeteroplasmiesMinor(heteroplasmiesMinor);
				centry.setMeanHetlevelMajor(meanHeteroplasmyMajor);
				centry.setMeanHetlevelMinor(meanHeteroplasmyMinor);
				centry.setDistance(distanceHG);
				
				contaminationList.add(centry);
				
			}

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return contaminationList;
	}

	private int calcDistance(ContaminationObject centry, Phylotree phylotree) {

		Haplogroup hgMajor = new Haplogroup(centry.getHgMajor());

		Haplogroup hgMinor = new Haplogroup(centry.getHgMinor());

		return phylotree.getDistanceBetweenHaplogroups(hgMajor, hgMinor);
	}

	private int calcDistanceOld(ContaminationObject centry, Phylotree phylotree) {

		int distanceHG;

		Haplogroup hgMajor = new Haplogroup(centry.getHgMajor());

		Haplogroup hgMinor = new Haplogroup(centry.getHgMinor());

		if (hgMajor.isSuperHaplogroup(phylotree, hgMinor)) {

			distanceHG = hgMajor.distanceToSuperHaplogroup(phylotree, hgMinor);

		} else if (hgMinor.isSuperHaplogroup(phylotree, hgMajor)) {

			distanceHG = hgMinor.distanceToSuperHaplogroup(phylotree, hgMajor);

		} else {

			distanceHG = hgMajor.distanceToSuperHaplogroup(phylotree, hgMinor);

		}
		return distanceHG;
	}

	private int countNotFound(ArrayList<Polymorphism> found, ArrayList<Polymorphism> expected) {
		int count = 0;
		for (Polymorphism currentPoly : expected) {
			if (!found.contains(currentPoly))
				count++;
		}
		return count;
	}

	private ArrayList<Polymorphism> calculateHaplogroupDifference(ArrayList<Polymorphism> list1, ArrayList<Polymorphism> list2) {

		ArrayList<Polymorphism> newList = new ArrayList<Polymorphism>(list1);

		newList.removeAll(list2);

		return newList;
	}

	private double calcMeanHeteroplasmy(Sample currentSample, ArrayList<Polymorphism> foundHaplogrep, boolean majorComponent) {

		double sum = 0.0;
		double count = 0;

		for (Polymorphism found : foundHaplogrep) {

			Variant variant = currentSample.getVariant(found.getPosition());
			if (variant != null && variant.getType() == 2) {
				if (majorComponent) {
					sum += variant.getMajorLevel();
				} else {
					sum += variant.getMinorLevel();
				}
				count++;
			}
		}
		if (count > 0) {
			return sum / count;
		} else {
			return 0.0;
		}
	}

	private int countHomoplasmies(Sample currentSample, ArrayList<Polymorphism> foundHaplogrep) {

		int count = 0;

		for (Polymorphism found : foundHaplogrep) {

			Variant variant = currentSample.getVariant(found.getPosition());

			if (variant != null && (variant.getType() == 1 || variant.getType() == 4)) {
				count++;
			}

		}
		return count;
	}

	private int countHeteroplasmiesMajor(Sample currentSample, ArrayList<Polymorphism> foundHaplogrep) {
		int count = 0;

		for (Polymorphism found : foundHaplogrep) {

			Variant variant = currentSample.getVariant(found.getPosition());

			if (variant != null && variant.getType() == 2 && (variant.getRef() != variant.getMajor())) {
				count++;
			}

		}
		return count;
	}

	private int countHeteroplasmiesMinor(Sample currentSample, ArrayList<Polymorphism> foundHaplogrep) {
		int count = 0;

		for (Polymorphism found : foundHaplogrep) {

			Variant variant = currentSample.getVariant(found.getPosition());

			if (variant != null && variant.getType() == 2 && (variant.getRef() != variant.getMinor())) {
				count++;
			}

		}
		return count;
	}

	public int getSettingAmountHigh() {
		return settingAmountHigh;
	}

	public void setSettingAmountHigh(int settingAmountHigh) {
		this.settingAmountHigh = settingAmountHigh;
	}

	public int getSettingAmountLow() {
		return settingAmountLow;
	}

	public void setSettingAmountLow(int settingAmountLow) {
		this.settingAmountLow = settingAmountLow;
	}

	public double getSettingHgQuality() {
		return settingHgQuality;
	}

	public void setSettingHgQuality(double settingHgQuality) {
		this.settingHgQuality = settingHgQuality;
	}

	public static String readInReference(String file) {
		StringBuilder stringBuilder = null;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = null;
			stringBuilder = new StringBuilder();

			while ((line = reader.readLine()) != null) {

				if (!line.startsWith(">"))
					stringBuilder.append(line);

			}

			reader.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return stringBuilder.toString();
	}
	
	
	public void writeFile(ArrayList<ContaminationObject> list, String output) {
		
		CsvTableWriter contaminationWriter = new CsvTableWriter(output, '\t');
		
		String[] columnsWrite = { "SampleID", "Contamination", "SampleHomoplasmies", "SampleHeteroplasmies", "SampleMeanCoverage", "HgMajor", "HgQualityMajor",
				"HgMinor", "HgQualityMinor", "HomoplasmiesMajor", "HomoplasmiesMinor", "HeteroplasmiesMajor", "HeteroplasmiesMinor", "MeanHetLevelMajor",
				"MeanHetLevelMinor", "HG_Distance"};
		//, "DiffSnpsMajorMinor", "DiffSnpsMinorMajor", "HeteroplasmyLevelTotal" 
		contaminationWriter.setColumns(columnsWrite);
		
		for(ContaminationObject entry : list) {
			contaminationWriter.setString(0, entry.getId());
			contaminationWriter.setString(1, entry.getStatus());
			contaminationWriter.setInteger(2, entry.getSampleHomoplasmies());
			contaminationWriter.setInteger(3, entry.getSampleHeteroplasmies());
			contaminationWriter.setInteger(4, entry.getSampleMeanCoverage());
			contaminationWriter.setString(5, entry.getHgMajor());
			contaminationWriter.setString(6, entry.getHgMajorQ());
			contaminationWriter.setString(7, entry.getHgMinor());
			contaminationWriter.setString(8, entry.getHgMinorQ());
			contaminationWriter.setInteger(9, entry.getHomoplasmiesMajor());
			contaminationWriter.setInteger(10, entry.getHomoplasmiesMinor());
			contaminationWriter.setInteger(11, entry.getHeteroplasmiesMajor());
			contaminationWriter.setInteger(12, entry.getHeteroplasmiesMinor());
			contaminationWriter.setDouble(13, entry.getMeanHetlevelMajor());
			contaminationWriter.setDouble(14, entry.getMeanHetlevelMinor());
			contaminationWriter.setInteger(15, entry.getDistance());
			//contaminationWriter.setInteger(16, diffMajorMinor.size());
			//contaminationWriter.setInteger(17, diffMinorMajor.size());
			//contaminationWriter.setString(18, formatter.format(meanHeteroplasmyMajor + meanHeteroplasmyMinor));
			contaminationWriter.next();
		}
		
		contaminationWriter.close();
	}

}