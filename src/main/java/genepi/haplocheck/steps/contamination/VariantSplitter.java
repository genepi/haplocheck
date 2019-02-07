package genepi.haplocheck.steps.contamination;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import genepi.haplocheck.steps.contamination.objects.HSDEntry;
import genepi.io.table.reader.CsvTableReader;
import vcf.Sample;
import vcf.Variant;

public class VariantSplitter {
	
	static double requiredHetLevel = 0.0;

	public ArrayList<String> split(HashMap<String, Sample> samples) {

		ArrayList<String> lines = new ArrayList<String>();

		for (Sample sample : samples.values()) {

			HSDEntry majorProfile = new HSDEntry();
			HSDEntry minorProfile = new HSDEntry();
			majorProfile.setId(sample.getId() + "_maj");
			minorProfile.setId(sample.getId() + "_min");

			for (Variant variant : sample.getVariants()) {

				if (variant.getType() == 2 && variant.getLevel() < requiredHetLevel) {
					continue;
				}

				// SNP or Deletion
				if (variant.getType() == 1 || variant.getType() == 4) {
					majorProfile.appendToProfile(variant.getPos() + "" + variant.getVariant());
					minorProfile.appendToProfile(variant.getPos() + "" + variant.getVariant());
				} else if (variant.getType() == 5) {
					majorProfile.appendToProfile(variant.getInsertion());
					minorProfile.appendToProfile(variant.getInsertion());
				} else if (variant.getType() == 2) {
					majorProfile.appendToProfile(variant.getPos() + "" + variant.getMajor());
					minorProfile.appendToProfile(variant.getPos() + "" + variant.getMinor());
				}

			}
			lines.add(majorProfile.toString());
			lines.add(minorProfile.toString());
		}

		return lines;

	}

	public ArrayList<String> splitFileTmp(String variantFile) {

		CsvTableReader reader = new CsvTableReader(new File(variantFile).getAbsolutePath(), '\t');
		ArrayList<String> lines = new ArrayList<String>();

		TreeMap<String, ArrayList<HSDEntry>> profiles = new TreeMap<String, ArrayList<HSDEntry>>();

		while (reader.next()) {

			String id = reader.getString("SampleID");

			if (!profiles.containsKey(id)) {
				ArrayList<HSDEntry> list = new ArrayList<HSDEntry>();
				HSDEntry majorProfile = new HSDEntry();
				HSDEntry minorProfile = new HSDEntry();
				majorProfile.setId(id + "_maj");
				minorProfile.setId(id + "_min");
				list.add(majorProfile);
				list.add(minorProfile);
				profiles.put(id, list);
			}

			ArrayList<HSDEntry> profile = profiles.get(id);

			int pos = reader.getInteger("Pos");
			String major = reader.getString("Major/Minor").split("/")[0];
			String minor = reader.getString("Major/Minor").split("/")[1];
			int type = reader.getInteger("Variant-Type");

			if (type == 1) {
				profile.get(0).appendToProfile(pos + major);
				profile.get(1).appendToProfile(pos + major);
			}

			if (type == 2) {
				profile.get(0).appendToProfile(pos + major);
				profile.get(1).appendToProfile(pos + minor);
			}

		}

		for (Map.Entry<String, ArrayList<HSDEntry>> entry : profiles.entrySet()) {
			for (HSDEntry ent : entry.getValue()) {
				lines.add(ent.toString());
			}
		}

		return lines;

	}

}
