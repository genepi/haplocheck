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

	private int settingAmountHigh = 3;
	private int settingDistance = 3;
	private double settingHgQuality = 0.5;

	public ArrayList<ContaminationObject> detect(HashMap<String, Sample> mutationSamples,
			ArrayList<TestSample> haplogrepSamples) {

		ArrayList<ContaminationObject> contaminationList = new ArrayList<ContaminationObject>();

		Collections.sort((List<TestSample>) haplogrepSamples);

		Phylotree phylotree = PhylotreeManager.getInstance().getPhylotree("phylotree17.xml", "weights17.txt");

		NumberFormat formatter = new DecimalFormat("#0.000");

		try {

			for (int i = 0; i < haplogrepSamples.size(); i += 2) {

				int distanceHG = 0;
				Status status;

				TestSample haplogrepMajor = haplogrepSamples.get(i);
				TestSample haplogrepMinor = haplogrepSamples.get(i + 1);

				ArrayList<Polymorphism> foundMajor = haplogrepMajor.getTopResult().getSearchResult().getDetailedResult()
						.getFoundPolys();
				ArrayList<Polymorphism> foundMinor = haplogrepMinor.getTopResult().getSearchResult().getDetailedResult()
						.getFoundPolys();

				ContaminationObject contObject = new ContaminationObject();
				contObject.setId(haplogrepMajor.getSampleID().split("_maj")[0]);
				double hgQualityMajor = haplogrepMajor.getTopResult().getDistance();
				double hgQualityMinor = haplogrepMinor.getTopResult().getDistance();

				Sample mutserveSample = mutationSamples.get(contObject.getId());

				int sampleHomoplasmies = mutserveSample.getAmountHomoplasmies();
				int sampleHeteroplasmies = mutserveSample.getAmountHeteroplasmies();

				int meanCoverageSample = (int) mutserveSample.getSumCoverage() / mutserveSample.getAmountVariants();

				double meanHetLevelSample = mutserveSample.getSumHeteroplasmyLevel()
						/ mutserveSample.getAmountHeteroplasmies();

				contObject.setHgMajor(haplogrepMajor.getTopResult().getHaplogroup().toString());
				contObject.setHgMinor(haplogrepMinor.getTopResult().getHaplogroup().toString());

				int homoplasmiesMajor = countHomoplasmies(mutserveSample, foundMajor);
				int homoplasmiesMinor = countHomoplasmies(mutserveSample, foundMinor);

				double meanHeteroplasmyMajor = calcMeanHeteroplasmy(mutserveSample, foundMajor, phylotree, true);
				double meanHeteroplasmyMinor = calcMeanHeteroplasmy(mutserveSample, foundMinor, phylotree, false);

				// find common ancestor
				Haplogroup commonAncestor = getCommonAncestor(contObject, phylotree);

				int majorHeteroplasmies = countOverlappingHeteroplasmies(haplogrepMajor, mutserveSample, phylotree,
						commonAncestor, true);
				int minorHeteroplasmies = countOverlappingHeteroplasmies(haplogrepMinor, mutserveSample, phylotree,
						commonAncestor, false);
				
				String clusters = "";

				if (!contObject.getHgMajor().equals(contObject.getHgMinor())) {

					distanceHG = calcDistance(contObject, phylotree);
					
					Jenks j = calcBreaks(mutserveSample);
					
					Breaks f = j.computeBreaks();
			
					clusters = f.printClusters();
					
					if ((majorHeteroplasmies >= settingAmountHigh || minorHeteroplasmies >= settingAmountHigh)
							&& distanceHG >= settingDistance && hgQualityMajor > settingHgQuality
							&& hgQualityMinor > settingHgQuality) {
						status = Status.YES;
					} else {
						status = Status.NO;
					}
				} else {
					status = Status.NO;
				}

				contObject.setStatus(status);
				contObject.setSampleHomoplasmies(sampleHomoplasmies);
				contObject.setSampleHeteroplasmies(sampleHeteroplasmies);
				contObject.setSampleMeanCoverage(meanCoverageSample);
				contObject.setHgMajorQ(formatter.format(hgQualityMajor));
				contObject.setHgMinorQ(formatter.format(hgQualityMinor));
				contObject.setHomoplasmiesMajor(homoplasmiesMajor);
				contObject.setHomoplasmiesMinor(homoplasmiesMinor);
				contObject.setClusterInfo(clusters);
				contObject.setHeteroplasmiesMajor(majorHeteroplasmies);
				contObject.setHeteroplasmiesMinor(minorHeteroplasmies);
				contObject.setMeanHetlevelMajor(formatter.format(meanHeteroplasmyMajor));
				contObject.setMeanHetlevelMinor(formatter.format(meanHeteroplasmyMinor));
				contObject.setDistance(distanceHG);

				ArrayList<TestSample> samples = new ArrayList<TestSample>();
				samples.add(haplogrepMajor);
				samples.add(haplogrepMinor);
				Tree tree = getJsonTree(mutserveSample, samples);
				contObject.setEdges(tree.getEdges());
				contObject.setNodes(tree.getNodes());
				contaminationList.add(contObject);

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

	private double calcMeanHeteroplasmy(Sample currentSample, ArrayList<Polymorphism> foundHaplogrep,
			Phylotree phylotree, boolean major) {
		double sum = 0.0;
		double count = 0;

		for (Polymorphism found : foundHaplogrep) {

			Variant variant = currentSample.getVariant(found.getPosition());

			if (variant == null) {

				continue;
			}

			if (major && (variant.getRef() == variant.getMajor())) {

				continue;
			}

			if (!major && (variant.getRef() == variant.getMinor())) {

				continue;
			}

			// check mutation rate
			if (phylotree.getMutationRate(found) < 5) {
				continue;
			}

			if (variant.getType() == 2 && variant.getVariant() != 'd') {
				if (major) {
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

	private Jenks calcBreaks(Sample currentSample) {
		Jenks j = new Jenks();
		for(Variant variant : currentSample.getVariants()) {
			if(variant.getType() == 2) {
			j.addValue(variant.getMajorLevel());
			j.addValue(variant.getMinorLevel());
			}
		}
		return j;
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

	public int getSettingAmountHigh() {
		return settingAmountHigh;
	}

	public void setSettingAmountHigh(int settingAmountHigh) {
		this.settingAmountHigh = settingAmountHigh;
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

		String[] columnsWrite = { "SampleID", "Contamination", "SampleHomoplasmies", "SampleHeteroplasmies",
				"SampleMeanCoverage", "HgMajor", "HgQualityMajor", "HgMinor", "HgQualityMinor", "HomoplasmiesMajor",
				"HomoplasmiesMinor", "HeteroplasmiesMajor", "HeteroplasmiesMinor", "MeanHetLevelMajor",
				"MeanHetLevelMinor", "HG_Distance","Clusters"};
		contaminationWriter.setColumns(columnsWrite);

		for (ContaminationObject entry : list) {
			contaminationWriter.setString(0, entry.getId());
			contaminationWriter.setString(1, entry.getStatus().name());
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
			contaminationWriter.setString(13, entry.getMeanHetlevelMajor());
			contaminationWriter.setString(14, entry.getMeanHetlevelMinor());
			contaminationWriter.setInteger(15, entry.getDistance());
			contaminationWriter.setString(16, entry.getClusterInfo());
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