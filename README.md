# Haplocheck
[![Build Status](https://travis-ci.org/genepi/haplocheck.svg?branch=master)](https://travis-ci.org/genepi/haplocheck)
[![GitHub release](https://img.shields.io/github/release/genepi/haplocheck.svg)](https://GitHub.com/genepi/haplocheck/releases/)

Haplocheck detects contamination in mtDNA AND WGS sequencing studies by analyzing only the mitchondrial DNA. You can use our [cloud web service](http://mitoverse.i-med.ac.at) or install it locally. 


## Install Haplocheck Standalone (VCF only)

     mkdir haplocheck
     wget https://github.com/genepi/haplocheck/releases/download/v1.3.2/haplocheck.zip
     unzip haplocheck.zip
     ./haplocheck --out <out-file> <input-vcf>
     

## Install Haplocheck Workflow (for BAM support with mutserve)

    curl -s install.cloudgene.io | bash -s 2.3.3
    ./cloudgene install https://github.com/genepi/haplocheck/releases/download/v1.3.2/haplocheck.zip 


## Run Haplocheck Workflow 

### VCF input (1000 Genomes Phase3 VCF file; n=2,504):  
```sh
#Download 1000G Phase3 Data
wget https://github.com/genepi/haplocheck/raw/master/test-data/contamination/1000G/all/1000g-nobaq.vcf.gz 
    
#Run haplocheck
./cloudgene run haplocheck@1.3.2 --files 1000g-nobaq.vcf.gz --format vcf --output results  
    
#Open results in Browser
firefox results/report/report.html
```
### BAM input (2x HG00096 sample from the 1000 Genomes Phase3 project)
```sh
#Create folder
mkdir bam-input
cd bam-input

#Download 1st BAM file
wget https://github.com/seppinho/mutserve/raw/master/test-data/mtdna/bam/input/HG00096.mapped.ILLUMINA.bwa.GBR.low_coverage.20101123.bam 
wget https://github.com/seppinho/mutserve/raw/master/test-data/mtdna/bam/input/HG00096.mapped.ILLUMINA.bwa.GBR.low_coverage.20101123.bam.bai

#Download 2nd BAM file
wget https://github.com/seppinho/mutserve/raw/master/test-data/mtdna/bam/input/HG00096.mapped.ILLUMINA.bwa.GBR.low_coverage.20101123_2.bam
wget https://github.com/seppinho/mutserve/raw/master/test-data/mtdna/bam/input/HG00096.mapped.ILLUMINA.bwa.GBR.low_coverage.20101123_2.bam.bai

cd ..

#Run haplocheck with 2 threads
./cloudgene run haplocheck@1.2.2 --files bam-input --format bam --output results  --threads 2
    
#Open results in Browser
firefox results/report/report.html
```


Haplocheck uses [mutserve](https://github.com/seppinho/mutserve) for variant calling and [haplogrep](https://github.com/seppinho/haplogrep-cmd) for haplogroup classification. 

## Documentation
Documentation can be found [here](https://mitoverse.readthedocs.io/en/latest). 

## Data Simulation

The script on how to create in-silico mixtures of two input samples can be found [here](https://github.com/genepi/haplocheck/blob/master/simulateNGSMix/Readme.md). 

## mtDNA Blog
Check out our [blog](http://haplogrep.i-med.ac.at/blog/) regarding mtDNA topics.

## Contact
See [here](https://mitoverse.readthedocs.io/en/latest/contact/).
