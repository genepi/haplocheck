package genepi.haplocheck.steps.contamination.objects;

public class ContaminationObject {

	private String id;
	private String status;
	private int sampleHeteroplasmies;
	private int sampleHomoplasmies;
	private int sampleMeanCoverage;
	private String hgMajor;
	private String hgMajorQ;
	private String hgMinor;
	private String hgMinorQ;
	private int homoplasmiesMajor;
	private int homoplasmiesMinor;
	private int heteroplasmiesMajor;
	private int heteroplasmiesMinor;
	private double meanHetlevelMajor;
	private double meanHetlevelMinor;
	private int distance;
	private String heteroplasmyLevel;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public int getSampleHeteroplasmies() {
		return sampleHeteroplasmies;
	}
	public void setSampleHeteroplasmies(int sampleHeteroplasmies) {
		this.sampleHeteroplasmies = sampleHeteroplasmies;
	}
	public int getSampleHomoplasmies() {
		return sampleHomoplasmies;
	}
	public void setSampleHomoplasmies(int sampleHomoplasmies) {
		this.sampleHomoplasmies = sampleHomoplasmies;
	}
	public int getSampleMeanCoverage() {
		return sampleMeanCoverage;
	}
	public void setSampleMeanCoverage(int sampleMeanCoverage) {
		this.sampleMeanCoverage = sampleMeanCoverage;
	}
	public String getHgMajor() {
		return hgMajor;
	}
	public void setHgMajor(String hgMajor) {
		this.hgMajor = hgMajor;
	}
	public String getHgMajorQ() {
		return hgMajorQ;
	}
	public void setHgMajorQ(String hgMajorQ) {
		this.hgMajorQ = hgMajorQ;
	}
	public String getHgMinor() {
		return hgMinor;
	}
	public void setHgMinor(String hgMinor) {
		this.hgMinor = hgMinor;
	}
	public String getHgMinorQ() {
		return hgMinorQ;
	}
	public void setHgMinorQ(String hgMinorQ) {
		this.hgMinorQ = hgMinorQ;
	}
	public int getHomoplasmiesMajor() {
		return homoplasmiesMajor;
	}
	public void setHomoplasmiesMajor(int homoplasmiesMajor) {
		this.homoplasmiesMajor = homoplasmiesMajor;
	}
	public int getHomoplasmiesMinor() {
		return homoplasmiesMinor;
	}
	public void setHomoplasmiesMinor(int homoplasmiesMinor) {
		this.homoplasmiesMinor = homoplasmiesMinor;
	}
	public int getHeteroplasmiesMajor() {
		return heteroplasmiesMajor;
	}
	public void setHeteroplasmiesMajor(int heteroplasmiesMajor) {
		this.heteroplasmiesMajor = heteroplasmiesMajor;
	}
	public int getHeteroplasmiesMinor() {
		return heteroplasmiesMinor;
	}
	public void setHeteroplasmiesMinor(int heteroplasmiesMinor) {
		this.heteroplasmiesMinor = heteroplasmiesMinor;
	}
	public double getMeanHetlevelMajor() {
		return meanHetlevelMajor;
	}
	public void setMeanHetlevelMajor(double meanHetlevelMajor) {
		this.meanHetlevelMajor = meanHetlevelMajor;
	}
	public double getMeanHetlevelMinor() {
		return meanHetlevelMinor;
	}
	public void setMeanHetlevelMinor(double meanHetlevelMinor) {
		this.meanHetlevelMinor = meanHetlevelMinor;
	}
	public int getDistance() {
		return distance;
	}
	public void setDistance(int distance) {
		this.distance = distance;
	}
	public String getHeteroplasmyLevel() {
		return heteroplasmyLevel;
	}
	public void setHeteroplasmyLevel(String heteroplasmyLevel) {
		this.heteroplasmyLevel = heteroplasmyLevel;
	}
}
