# FAQ

**What is the difference b/w overall homo/heteroplasmies and number major/minor homo/heteroplasmies?**  

Overall homoplasmies/heteroplasmies **count** the number of variants found in a sample. E.g. sample **17.bam** includes 10 homoplasmic variants and 8 heteroplasmic variants. Haplocheck splits the variants into two profiles, the so called major and minor profile. The major profile includes all homoplasmies (**10**) and the major allele of each heteroplasmy. The minor profile includes again all hoomoplasmies (**10**) and the minor allele of each heteroplasmy. For both profiles, the haplogroup is calculated using Haplogrep. Ideally, all variants are used and a quality score of 1 is reached by Haplogrep. Since neither the input profile nor the phylogeny is perfect, some of the input homoplasmies or heteroplasmies are missed by Haplogrep. This results in a Haplogrep quality score between 0.5 and 1. The columns *major/minor homoplasmies/heteroplasmies* count the number of variants that have been finally used for the best haplogroup hit and can therefore differ from the overall homoplasmies/heteroplasmies.    

**What does the heteroplasmy level represent?**

The heteroplasmy level denotes the averaged allele frequency (VAF) and is calculated for both the major heteroplasmic allele (column *Major Heteroplasmy Level*) and minor heteroplasmic allele (column *Minor Heteroplasmy Level*).

**How is the heteroplasmy level calculated?**

The major and minor heteroplasmy level is calculated by averaging the allele frequency of both alleles.  Only heteroplasmies from the most previous common ancestor are used for this calculation. For example, if **H1a1** is the common ancestor of the profiles H1a1a1 (**major**) and H1a1b (**minor**), only heteroplasmies starting from H1a1 are included for calculation. Furthermore, we only add heteroplasmies with a mutation rate > 5 (as defined by HaploGrep) and excluding back mutations as well as deletions on heteroplasmies.

**How is the label contamination (YES versus NO) decided exactly?**

Haplocheck uses the mitochondrial phylogeny and the concept of haplogroups to identify contamination. It is heavily based on [Haplogrep](https://github.com/seppinho/haplogrep-cmd) and [Mutserve](https://github.com/seppinho/mutserve). Mutserve allows to detect low-level variants (or in case of mtDNA so called **heteroplasmies**) down to the variant level of 1 %. Haplocheck splits the input into two profiles and calculates the haplogroup for each profile using Haplogrep. Identical haplogroups are marked with the contamination status of **NO**. If the haplogroup between two profiles differ, high confident heteroplasmies are determined by haplocheck and the distance between the two profiles is calculated. Depending on the (a) number of heteroplasmies, (b) haplogroup quality score and (c) distance between two profiles a YES or NO label is assigned.
To assign a correct YES/NO labels contamination can be occur on (a) two separate branches (b) same major branch or (c) same minor branch. Please also have a look at the [contamination method](https://mitoverse.readthedocs.io/en/latest/method/) itself to learn about different kinds of contamination.

**How did you evaluate Haplocheck?**

To find the best setup, we created and analyzed in-silico data by mixing random profiles from the currently best available mtDNA phylogeny derived from Phylotree. In total, 6 different datasets (3 datasets with 500,000 mixtures each, 3 datasets with 100,000 mixtures each) have been created and the F1-Score (defined as `2 x precision x sensitivity / (precision + sensitivity)`) has been calculated to analyze the overall accuracy of haplocheck. To evaluate the overall performance of haplocheck on a real data set, we re-analyzed 1KGP Phase 3 (n = 2,504) and compared our results with the publicly available data regarding contamination status derived from verifyBamID.
