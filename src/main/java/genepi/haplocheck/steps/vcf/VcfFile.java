package genepi.haplocheck.steps.vcf;

import java.util.Set;

public class VcfFile {

	private Set<String> chromosomes;

	private Set<String> rawChromosomes;
	
	private String vcfFilename;
	
	private String vcfFilePath;

	private int noSamples = -1;

	private int noSnps = -1;
	
	private boolean heteroplasmyTag = false;

	public int getNoSnps() {
		return noSnps;
	}

	public void setNoSnps(int noSnps) {
		this.noSnps = noSnps;
	}

	public Set<String> getChromosomes() {
		return chromosomes;
	}

	public String getChromosome() {
		return chromosomes.iterator().next();
	}

	public String getRawChromosome() {
		return rawChromosomes.iterator().next();
	}
	
	public String getVcfFilename() {
		return vcfFilename;
	}

	public int getNoSamples() {
		return noSamples;
	}

	public void setVcfFilename(String vcfFilename) {
		this.vcfFilename = vcfFilename;
	}

	public void setNoSamples(int noSamples) {
		this.noSamples = noSamples;
	}

	public void setChromosomes(Set<String> chromosomes) {
		this.chromosomes = chromosomes;
	}
	
	public void setRawChromosomes(Set<String> rawChromosomes) {
		this.rawChromosomes = rawChromosomes;
	}
	
	public String toString() {
		return "Chromosome: " + getChromosome() + "\n Samples: "
				+ getNoSamples() + "\n Snps: " + getNoSnps();
	}

	public String[] getFilenames() {
		return new String[] { getVcfFilename()};
	}
	
	public boolean isHeteroplasmyTag() {
		return heteroplasmyTag;
	}

	public void setHeteroplasmyTag(boolean heteroplasmyTag) {
		this.heteroplasmyTag = heteroplasmyTag;
	}

	public String getVcfFilePath() {
		return vcfFilePath;
	}

	public void setVcfFilePath(String vcfFilePath) {
		this.vcfFilePath = vcfFilePath;
	}


}
