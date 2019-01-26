package genepi.haplocheck.steps.vcf;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import genepi.io.text.LineReader;
import htsjdk.variant.vcf.VCFFileReader;

public class VcfFileUtil {

	public static String TABIX_PATH = "bin/";

	public static void setTabixBinary(String binaries) {
		TABIX_PATH = binaries;
	}

	public static String getBinary() {
		return TABIX_PATH;
	}

	public static VcfFile load(String vcfFilename) throws IOException {

		Set<String> chromosomes = new HashSet<String>();
		Set<String> rawChromosomes = new HashSet<String>();
		int noSnps = 0;
		int noSamples = 0;
		boolean heteroplasmyTag = false;
		
		try {

			VCFFileReader reader = new VCFFileReader(new File(vcfFilename), false);

			noSamples = reader.getFileHeader().getGenotypeSamples().size();

			reader.close();

			LineReader lineReader = new LineReader(vcfFilename);

			while (lineReader.next()) {

				String line = lineReader.get();

				if (!line.startsWith("#")) {

					String tiles[] = line.split("\t", 10);

					if (tiles.length < 3) {
						throw new IOException("The provided VCF file is not tab-delimited");
					}

					String chromosome = tiles[0];
					rawChromosomes.add(chromosome);

					chromosomes.add(chromosome);
					if (chromosomes.size() > 1) {
						throw new IOException("The provided VCF file contains more than one chromosome.");
					}

					String ref = tiles[3];
					String alt = tiles[4];

					if (ref.equals(alt)) {
						throw new IOException("The provided VCF file is malformed at variation " + tiles[2]
								+ ": reference allele (" + ref + ") and alternate allele  (" + alt + ") are the same.");
					}
					
					String format = tiles[8];
					if(format.contains("AF")) {
						heteroplasmyTag = true;
					}

					noSnps++;

				} else {

					if (line.startsWith("#CHROM")) {

						String[] tiles = line.split("\t");

						// check sample names, stop when not unique
						HashSet<String> samples = new HashSet<>();

						for (int i = 0; i < tiles.length; i++) {

							String sample = tiles[i];

							if (samples.contains(sample)) {
								reader.close();
								throw new IOException("Two individuals or more have the following ID: " + sample);
							}
							samples.add(sample);
						}
					}

				}

			}
			lineReader.close();

			VcfFile file = new VcfFile();
			file.setVcfFilename(new File(vcfFilename).getName());
			file.setVcfFilePath(vcfFilename);
			file.setNoSnps(noSnps);
			file.setNoSamples(noSamples);
			file.setChromosomes(chromosomes);
			file.setRawChromosomes(rawChromosomes);
			file.setHeteroplasmyTag(heteroplasmyTag);
			
			return file;

		} catch (Exception e) {
			throw new IOException(e.getMessage());
		}

	}

	public static Set<String> validChromosomes = new HashSet<String>();

	static {

		validChromosomes.add("chrMT");
		validChromosomes.add("chrM");
		validChromosomes.add("MT");

	}

	public static boolean isValidChromosome(String chromosome) {
		return validChromosomes.contains(chromosome);
	}

}