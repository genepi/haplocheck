# Haplocheck
[![Build Status](https://travis-ci.org/genepi/haplocheck.svg?branch=master)](https://travis-ci.org/genepi/haplocheck)
[![GitHub release](https://img.shields.io/github/release/genepi/haplocheck.svg)](https://GitHub.com/genepi/haplocheck/releases/)

## Getting started
Haplocheck is a software that uses the mitochondrial phylogeny to detect contamination in mtDNA sequencing studies and whole-genome sequencing studies. You can use our [cloud web service](http://mitoverse.i-med.ac.at) or install it locally. 

## Download and Install 

    mkdir haplocheck 
    cd haplocheck
    curl -s install.cloudgene.io | bash 
    ./cloudgene install https://github.com/genepi/haplocheck/releases/download/v1.1.2/haplocheck.zip 


## Run 
To calculate the contamination status of all 1000 Genomes Phase3 samples (n = 2,504) locally, execute the following command.  
```sh
#Download 1000G Phase3 Data
wget https://github.com/genepi/haplocheck/raw/master/test-data/contamination/1000G/all/1000g-nobaq.vcf.gz 
    
#Run haplocheck
./cloudgene run haplocheck@1.1.2 --files 1000g-nobaq.vcf.gz --output results  
    
#Open results in Browser
firefox results/report/report.html
```

## Documentation
Documentation can be found [here](https://mitoverse.readthedocs.io/en/latest). 

## Data Simulation

The script on how to create in-silico mixtures of two input samples can be found [here](https://github.com/genepi/haplocheck/blob/master/simulateNGSMix/Readme.md). 

## mtDNA Blog
Check out our [blog](http://haplogrep.i-med.ac.at/blog/) regarding mtDNA topics.

## Contact
See [here](https://mitoverse.readthedocs.io/en/latest/contact/).
