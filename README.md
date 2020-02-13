# Haplocheck
[![Build Status](https://travis-ci.org/genepi/haplocheck.svg?branch=master)](https://travis-ci.org/genepi/haplocheck)
[![GitHub release](https://img.shields.io/github/release/genepi/haplocheck.svg)](https://GitHub.com/genepi/haplocheck/releases/)

Haplocheck is a software package that leverages the mitochondrial phylogeny to detect contamination in whole-genome and targeted mtDNA sequencing studies. Haplocheck can also be used as a quick and an efficient proxy for determining the whole-genome contamination level (using mtDNA only).  

## Getting started
To calculate the contamination status of all 1000 Genomes Phase3 samples (n = 2,504), execute the following command.  

### Installation

    mkdir haplocheck 
    cd haplocheck
    curl -s install.cloudgene.io | bash 
    ./cloudgene install https://github.com/genepi/haplocheck/releases/download/v1.0.11/haplocheck.zip 

### Run 
```sh
#Download Example Data
wget https://github.com/genepi/haplocheck/raw/master/test-data/contamination/1000G/all/1000g-nobaq.vcf.gz 
    
#Run haplocheck
./cloudgene run haplocheck@1.0.11 --files 1000g-nobaq.vcf.gz --output results  
    
#Open results in Browser
firefox results/report/report.html
```

## Use Haplocheck
We provide haplocheck as a standalone pipeline and as a [cloud web service](https://mitoverse.i-med.ac.at). 

## Documentation
Documentation can be found [here](https://mitoverse.readthedocs.io/en/latest). It also includes a section how to [interpret the final result files](https://mitoverse.readthedocs.io/en/latest/interpret/) and [frequently asked questions](https://mitoverse.readthedocs.io/en/latest/faq).

## mtDNA Blog
Check out our [blog](http://haplogrep.i-med.ac.at/blog/) regarding mtDNA topics.

## Contact
See [here](https://mitoverse.readthedocs.io/en/latest/contact/).
