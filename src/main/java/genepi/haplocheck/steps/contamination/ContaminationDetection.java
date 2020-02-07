package genepi.haplocheck.steps.contamination;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.google.common.math.Quantiles;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import core.Haplogroup;
import core.Polymorphism;
import core.TestSample;
import genepi.haplocheck.steps.contamination.objects.ContaminationObject;
import genepi.haplocheck.steps.contamination.objects.Edge;
import genepi.haplocheck.steps.contamination.objects.Font;
import genepi.haplocheck.steps.contamination.objects.Node;
import genepi.haplocheck.steps.contamination.objects.Tree;
import genepi.haplocheck.util.Jenks;
import genepi.haplocheck.util.Jenks.Breaks;
import genepi.io.table.writer.CsvTableWriter;
import phylotree.Phylotree;
import phylotree.PhylotreeManager;
import search.SearchResultTreeNode;
import vcf.Sample;
import vcf.Variant;

public class ContaminationDetection {

	public enum Status {
		YES, NO;
	}

	private int heteroplasmyDistance = 3;
	private int haplogroupDistance = 2;
	private double haplogroupQ = 0.5;

	public ArrayList<ContaminationObject> detect(HashMap<String, Sample> mutationSamples,
			ArrayList<TestSample> haplogrepSamples) {

		ArrayList<ContaminationObject> contaminationList = new ArrayList<ContaminationObject>();

		Collections.sort((List<TestSample>) haplogrepSamples);

		Phylotree phylotree = PhylotreeManager.getInstance().getPhylotree("phylotree17.xml", "weights17.txt");

		NumberFormat formatter = new DecimalFormat("#0.000");

		try {

			for (int i = 0; i < haplogrepSamples.size(); i += 2) {

				int distance = 0;
				Status status;

				TestSample haplogrepMajor = haplogrepSamples.get(i);
				TestSample haplogrepMinor = haplogrepSamples.get(i + 1);

				ArrayList<Polymorphism> foundMajor = haplogrepMajor.getTopResult().getSearchResult().getDetailedResult()
						.getFoundPolys();
				ArrayList<Polymorphism> foundMinor = haplogrepMinor.getTopResult().getSearchResult().getDetailedResult()
						.getFoundPolys();

				ContaminationObject contamination = new ContaminationObject();
				contamination.setId(haplogrepMajor.getSampleID().split("_maj")[0]);
				double hgQualityMajor = haplogrepMajor.getTopResult().getDistance();
				double hgQualityMinor = haplogrepMinor.getTopResult().getDistance();

				Sample mutserveSample = mutationSamples.get(contamination.getId());

				int sampleHomoplasmies = mutserveSample.getAmountHomoplasmies();
				int sampleHeteroplasmies = mutserveSample.getAmountHeteroplasmies();

				int meanCoverageSample = -1;
				if (mutserveSample.getAmountVariants() != 0) {
					meanCoverageSample = (int) mutserveSample.getSumCoverage() / mutserveSample.getAmountVariants();
				}

				contamination.setHgMajor(haplogrepMajor.getTopResult().getHaplogroup().toString());
				contamination.setHgMinor(haplogrepMinor.getTopResult().getHaplogroup().toString());

				int homoplasmiesMajor = countHomoplasmies(mutserveSample, foundMajor);
				int homoplasmiesMinor = countHomoplasmies(mutserveSample, foundMinor);

				// find common ancestor
				Haplogroup commonAncestor = getCommonAncestor(contamination, phylotree);

				double meanHeteroplasmyMajor = calcMedianHeteroplasmy(haplogrepMajor, mutserveSample, phylotree,
						commonAncestor, true);
				double meanHeteroplasmyMinor = calcMedianHeteroplasmy(haplogrepMinor, mutserveSample, phylotree,
						commonAncestor, false);

				double overallLevel = calcOverallLevel(meanHeteroplasmyMajor, meanHeteroplasmyMinor);

				int majorHeteroplasmies = countOverlappingHeteroplasmies(haplogrepMajor, mutserveSample, phylotree,
						commonAncestor, true);
				int minorHeteroplasmies = countOverlappingHeteroplasmies(haplogrepMinor, mutserveSample, phylotree,
						commonAncestor, false);

				Jenks jenks = new Jenks();

				calcBreaks(jenks, haplogrepMajor, mutserveSample, phylotree, commonAncestor, true);
				calcBreaks(jenks, haplogrepMinor, mutserveSample, phylotree, commonAncestor, false);

				Breaks jenkBreaks = jenks.computeBreaks();

				String clusters = jenkBreaks.printClusters();

				if (!contamination.getHgMajor().equals(contamination.getHgMinor())) {

					distance = calcDistance(contamination, phylotree);

					if ((majorHeteroplasmies + minorHeteroplasmies) >= heteroplasmyDistance
							&& distance >= haplogroupDistance && hgQualityMajor > haplogroupQ
							&& hgQualityMinor > haplogroupQ) {
						status = Status.YES;
					} else {
						status = Status.NO;
					}
				} else {
					status = Status.NO;
				}

				contamination.setStatus(status);
				contamination.setSampleHomoplasmies(sampleHomoplasmies);
				contamination.setSampleHeteroplasmies(sampleHeteroplasmies);
				contamination.setSampleMeanCoverage(meanCoverageSample);
				contamination.setHgMajorQ(formatter.format(hgQualityMajor));
				contamination.setHgMinorQ(formatter.format(hgQualityMinor));
				contamination.setHomoplasmiesMajor(homoplasmiesMajor);
				contamination.setHomoplasmiesMinor(homoplasmiesMinor);
				contamination.setClusterInfo(clusters);
				contamination.setHeteroplasmiesMajor(majorHeteroplasmies);
				contamination.setHeteroplasmiesMinor(minorHeteroplasmies);
				contamination.setOverallLevel((overallLevel > 0) ? formatter.format(overallLevel) : "n/a");
				contamination.setMeanHetlevelMajor(
						(meanHeteroplasmyMajor > 0) ? formatter.format(meanHeteroplasmyMajor) : "n/a");
				contamination.setMeanHetlevelMinor(
						(meanHeteroplasmyMinor > 0) ? formatter.format(meanHeteroplasmyMinor) : "n/a");
				contamination.setDistance(distance);

				ArrayList<TestSample> samples = new ArrayList<TestSample>();
				samples.add(haplogrepMajor);
				samples.add(haplogrepMinor);
				Tree tree = getJsonTree(mutserveSample, samples);
				contamination.setEdges(tree.getEdges());
				contamination.setNodes(tree.getNodes());
				contaminationList.add(contamination);

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

	private int calcBreaks(Jenks j, TestSample haplogrepSample, Sample mutserveSample, Phylotree phylotree,
			Haplogroup commonAncestor, boolean major) {

		int count = 0;

		ArrayList<SearchResultTreeNode> path = haplogrepSample.getTopResult().getSearchResult().getDetailedResult()
				.getPhyloTreePath();

		for (SearchResultTreeNode current : path) {
			Haplogroup node = current.getHaplogroup();

			for (Polymorphism currentPoly : current.getExpectedPolys()) {

				Variant pos = mutserveSample.getVariant(currentPoly.getPosition());

				if (pos == null) {
					continue;
				}

				if (currentPoly.isBackMutation()) {
					continue;
				}

				// check mutation rate
				if (phylotree.getMutationRate(currentPoly) < 5) {
					continue;
				}

				// count only heteroplasmies from common ancestor and later!
				if (!commonAncestor.isSuperHaplogroup(phylotree, node)) {
					continue;
				}

				if (major && (pos.getRef() == pos.getMajor())) {

					continue;
				}

				if (!major && (pos.getRef() == pos.getMinor())) {

					continue;
				}

				if (pos.getType() == 2 && pos.getVariant() != 'd') {

					if (major) {
						j.addValue(pos.getMajorLevel());
					} else {
						j.addValue(pos.getMinorLevel());
					}
					count++;
				}
			}

		}

		return count;
	}

	private int countOverlappingHeteroplasmies(TestSample haplogrepSample, Sample mutserveSample, Phylotree phylotree,
			Haplogroup commonAncestor, boolean major) {

		int count = 0;

		ArrayList<SearchResultTreeNode> path = haplogrepSample.getTopResult().getSearchResult().getDetailedResult()
				.getPhyloTreePath();

		for (SearchResultTreeNode current : path) {
			Haplogroup node = current.getHaplogroup();

			for (Polymorphism currentPoly : current.getExpectedPolys()) {

				Variant pos = mutserveSample.getVariant(currentPoly.getPosition());

				if (pos == null) {
					continue;
				}

				if (currentPoly.isBackMutation()) {
					continue;
				}

				// check mutation rate
				if (phylotree.getMutationRate(currentPoly) < 5) {
					continue;
				}

				// count only heteroplasmies from common ancestor and later!
				if (!commonAncestor.isSuperHaplogroup(phylotree, node)) {
					continue;
				}

				if (major && (pos.getRef() == pos.getMajor())) {

					continue;
				}

				if (!major && (pos.getRef() == pos.getMinor())) {

					continue;
				}

				if (pos.getType() == 2 && pos.getVariant() != 'd') {
					count++;
				}
			}

		}

		return count;
	}

	private double calcOverallLevel(double major, double minor) {

		if (major > 0) {
			return 1 - major;
		} else {
			return minor;
		}

	}

	private double calcMedianHeteroplasmy(TestSample haplogrepSample, Sample mutserveSample, Phylotree phylotree,
			Haplogroup commonAncestor, boolean major) {

		ArrayList<Double> distanceList = new ArrayList<Double>();

		ArrayList<SearchResultTreeNode> path = haplogrepSample.getTopResult().getSearchResult().getDetailedResult()
				.getPhyloTreePath();

		for (SearchResultTreeNode current : path) {
			Haplogroup node = current.getHaplogroup();

			for (Polymorphism currentPoly : current.getExpectedPolys()) {

				Variant pos = mutserveSample.getVariant(currentPoly.getPosition());

				if (pos == null) {
					continue;
				}

				if (currentPoly.isBackMutation()) {
					continue;
				}

				// check mutation rate
				if (phylotree.getMutationRate(currentPoly) < 5) {
					continue;
				}

				// count only heteroplasmies from common ancestor and later!
				if (!commonAncestor.isSuperHaplogroup(phylotree, node)) {
					continue;
				}

				if (major && (pos.getRef() == pos.getMajor())) {

					continue;
				}

				if (!major && (pos.getRef() == pos.getMinor())) {

					continue;
				}

				if (pos.getType() == 2 && pos.getVariant() != 'd') {
					if (major) {
						distanceList.add(pos.getMajorLevel());
					} else {
						distanceList.add(pos.getMinorLevel());
					}
				}
			}
		}

		return calculateMedian(distanceList);

	}

	private double calculateMedian(List<Double> distanceList) {
		Double sum = 0.0;
		if (distanceList.size() > 0) {
			return com.google.common.math.Quantiles.median().compute(distanceList);
		}

		return sum;

	}

	public int getSettingAmountHigh() {
		return heteroplasmyDistance;
	}

	public void setSettingAmountHigh(int settingAmountHigh) {
		this.heteroplasmyDistance = settingAmountHigh;
	}

	public double getSettingHgQuality() {
		return haplogroupQ;
	}

	public void setSettingHgQuality(double settingHgQuality) {
		this.haplogroupQ = settingHgQuality;
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

	public void writeReportAsJson(String outputJson, ArrayList<ContaminationObject> contaminationList)
			throws IOException {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String json = gson.toJson(contaminationList);
		FileWriter wr = new FileWriter(outputJson);
		wr.write(json);
		wr.close();
	}

	public void writeReport(String output, ArrayList<ContaminationObject> list) {

		CsvTableWriter contaminationWriter = new CsvTableWriter(output, '\t');

		String[] columnsWrite = { "Sample", "Contamination Status", "Reliable WGS Estimator", "Contamination Level", "Distance",
				"Sample Coverage", "Overall Homoplasmies", "Overall Heteroplasmies", "Major Heteroplasmy Level",
				"Minor Heteroplasmy Level", "Major Haplogroup", "Major Haplogroup Quality", "Minor Haplogroup",
				"Minor Haplogroup Quality", "Major Homoplasmies Count", "Minor Homoplasmies Count",
				"Major Heteroplasmies Count", "Minor Heteroplasmies Count", "Clusters" };

		contaminationWriter.setColumns(columnsWrite);

		ArrayList<Integer> coverageList = new ArrayList<Integer>();

		for (ContaminationObject cont : list) {
			coverageList.add(cont.getSampleMeanCoverage());
		}

		double percentile25 = Quantiles.percentiles().index(25).compute(coverageList);

		for (ContaminationObject entry : list) {
			contaminationWriter.setString("Sample", entry.getId());
			contaminationWriter.setString("Contamination Status", entry.getStatus().name());
			contaminationWriter.setString("Reliable WGS Estimator",
					entry.getSampleMeanCoverage() >= percentile25 ? "true" : " false");
			contaminationWriter.setString("Contamination Level", entry.getOverallLevel());
			contaminationWriter.setInteger("Distance", entry.getDistance());
			contaminationWriter.setInteger("Sample Coverage", entry.getSampleMeanCoverage());
			contaminationWriter.setInteger("Overall Homoplasmies", entry.getSampleHomoplasmies());
			contaminationWriter.setInteger("Overall Heteroplasmies", entry.getSampleHeteroplasmies());
			contaminationWriter.setString("Major Heteroplasmy Level", entry.getMeanHetlevelMajor());
			contaminationWriter.setString("Minor Heteroplasmy Level", entry.getMeanHetlevelMinor());
			contaminationWriter.setString("Major Haplogroup", entry.getHgMajor());
			contaminationWriter.setString("Major Haplogroup Quality", entry.getHgMajorQ());
			contaminationWriter.setString("Minor Haplogroup", entry.getHgMinor());
			contaminationWriter.setString("Minor Haplogroup Quality", entry.getHgMinorQ());
			contaminationWriter.setInteger("Major Homoplasmies Count", entry.getHomoplasmiesMajor());
			contaminationWriter.setInteger("Minor Homoplasmies Count", entry.getHomoplasmiesMinor());
			contaminationWriter.setInteger("Major Heteroplasmies Count", entry.getHeteroplasmiesMajor());
			contaminationWriter.setInteger("Minor Heteroplasmies Count", entry.getHeteroplasmiesMinor());
			contaminationWriter.setString("Clusters", entry.getClusterInfo());
			contaminationWriter.next();
		}

		contaminationWriter.close();
	}

	public static Tree getJsonTree(Sample currentSample, ArrayList<TestSample> samples) throws IOException {

		ArrayList<Node> nodes = new ArrayList<Node>();
		ArrayList<Edge> edges = new ArrayList<Edge>();
		HashMap<String, Integer> mapNodes = new HashMap<String, Integer>();
		HashSet<String> setEdges = new HashSet<String>();

		int nodeCount = 0;

		for (TestSample sample : samples) {

			int current;
			int previous = 0;

			ArrayList<SearchResultTreeNode> currentPath = sample.getTopResult().getSearchResult().getDetailedResult()
					.getPhyloTreePath();

			for (SearchResultTreeNode result : currentPath) {

				String haplogroup = result.getHaplogroup().toString();

				// create new node
				if (mapNodes.get(haplogroup) == null) {
					current = nodeCount;
					Node node = new Node();
					node.setId(current);
					node.setLabel(haplogroup);
					nodes.add(node);
					mapNodes.put(haplogroup, current);
					nodeCount++;
				} else {
					current = mapNodes.get(haplogroup);
				}

				String label = getLabel(result, sample.getSampleID(), currentSample);
				Font font = getFont(result, sample.getSampleID(), currentSample);
				String edgeName = previous + "" + current + "" + label;

				if (current != 0 && !setEdges.contains(edgeName)) {
					Edge edge = new Edge();
					edge.setFrom(previous);
					edge.setTo(current);
					edge.setLabel(label);
					edge.setFont(font);
					edges.add(edge);
					setEdges.add(edgeName);
				}

				previous = current;

			}
		}
		Tree tree = new Tree();
		tree.setNodes(nodes);
		tree.setEdges(edges);
		return tree;
	}

	private static String getLabel(SearchResultTreeNode result, String id, Sample currentSample) {
		StringBuilder builder = new StringBuilder();

		for (Polymorphism currentPoly : result.getExpectedPolys()) {

			if (result.getFoundPolys().contains(currentPoly)) {
				if (builder != null) {
					builder.append(" ");
				}

				Variant pos = currentSample.getVariant(currentPoly.getPosition());

				if (pos != null) {
					double level = 0;
					if (pos.getType() == 2 && id.contains("maj")) {
						level = pos.getMajorLevel();
					} else if (pos.getType() == 2 && id.contains("min")) {
						level = pos.getMinorLevel();
					}
					if (pos.getType() == 2) {
						builder.append(currentPoly + " (" + level + ")");
					} else {
						builder.append(currentPoly);
					}
				}
			}
		}
		return builder.toString();
	}

	private static Font getFont(SearchResultTreeNode result, String id, Sample currentSample) {

		for (Polymorphism currentPoly : result.getExpectedPolys()) {

			if (result.getFoundPolys().contains(currentPoly)) {

				Variant pos = currentSample.getVariant(currentPoly.getPosition());
				if (pos != null) {
					if (pos.getType() == 1) {
						return new Font("blue");
					} else if (pos.getType() == 2) {
						return new Font("green");
					}
				}
			}
		}
		return new Font("black");

	}

	private Haplogroup getCommonAncestor(ContaminationObject centry, Phylotree phylotree) {

		Haplogroup hgMajor = new Haplogroup(centry.getHgMajor());

		Haplogroup hgMinor = new Haplogroup(centry.getHgMinor());

		return phylotree.getCommonAncestor(hgMajor, hgMinor);

	}

}