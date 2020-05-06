# Haplocheck
[![Build Status](https://travis-ci.org/genepi/haplocheck.svg?branch=master)](https://travis-ci.org/genepi/haplocheck)
[![GitHub release](https://img.shields.io/github/release/genepi/haplocheck.svg)](https://GitHub.com/genepi/haplocheck/releases/)

## Getting started
Haplocheck is a software that uses the mitochondrial phylogeny to detect contamination in mtDNA sequencing studies and whole-genome sequencing studies. You can use our [cloud web service](http://mitoverse.i-med.ac.at/) or download haplocheck for local usage. 

## Installation

    mkdir haplocheck 
    cd haplocheck
    curl -s install.cloudgene.io | bash 
    ./cloudgene install https://github.com/genepi/haplocheck/releases/download/v1.1.2/haplocheck.zip 


## Run 
To calculate the contamination status of all 1000 Genomes Phase3 samples (n = 2,504), execute the following command.  
```sh
#Download 1000G Phase3 Data
wget https://github.com/genepi/haplocheck/raw/master/test-data/contamination/1000G/all/1000g-nobaq.vcf.gz 
    
#Run haplocheck
./cloudgene run haplocheck@1.1.2 --files 1000g-nobaq.vcf.gz --output results  
    
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
