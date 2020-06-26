# simulateNGSMix

## Getting started
simulateNGSMix is a bash-script taking 2 fasta files as input, together with a NGS Sequencing Device name and emits mixed bam files, ready to be evaluated with haplocheck

## Prerequisites 

The bash script relies on 3 different tools:
1. ART NGS Read simulator: https://www.niehs.nih.gov/research/resources/software/biostatistics/art/index.cfm
2. Samtools: http://samtools.github.io/
3. BWA MEM: https://github.com/lh3/bwa

Adapt reference file in bash script:

    PATHBWAINDEX="/ref/chrM.fasta"

## Run 
Generate a mixture 
```sh
# for generating HiSeq 2500 data:
sh simulateNGSMix data/Lab011.fasta data/Lab002.fasta HS25

# for generating MiSeq v3 data:
sh simulateNGSMix data/Lab011.fasta data/Lab002.fasta MSv3

#Run haplocheck on all bam files in folder data
./cloudgene run haplocheck@1.1.2 --files data --output Result.vcf  
    
```
