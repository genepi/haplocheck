# Haplocheck
[![Build Status](https://travis-ci.org/genepi/haplocheck.svg?branch=master)](https://travis-ci.org/genepi/haplocheck)
[![GitHub release](https://img.shields.io/github/release/genepi/haplocheck.svg)](https://GitHub.com/genepi/haplocheck/releases/)

Haplocheck is a software package that leverages the mitochondrial phylogeny to detect contamination in targeted mtDNA sequencing studies. Haplocheck can also be used as a quick proxy for calculating the whole-genome contamination level using mtDNA only.  

## Use Haplocheck
We provide haplocheck as a standalone pipeline and as a [cloud web service](https://mitoverse.i-med.ac.at). 

## Getting started
To calculate the contamination status of all 1000 Genomes Phase3 samples, execute the following command:

    mkdir haplocheck 
    cd haplocheck
    wget https://github.com/genepi/haplocheck/raw/master/test-data/contamination/1000G/all/1000g-nobaq.vcf.gz  
    curl -s install.cloudgene.io | bash 
    ./cloudgene install https://github.com/genepi/haplocheck/releases/download/v1.0.10/haplocheck.zip 
    ./cloudgene run haplocheck@1.0.10 --files 1000g-nobaq.vcf.gz --output results  
    firefox results/report/report.html

## Documentation
Documentation can be found [here](https://mitoverse.readthedocs.io/en/latest). It also includes a section how to [interpret the final result files](https://mitoverse.readthedocs.io/en/latest/interpret/) and [frequently asked questions](https://mitoverse.readthedocs.io/en/latest/faq).

## mtDNA Blog
Check out our [blog](http://haplogrep.i-med.ac.at/blog/) regarding mtDNA topics.

## Contact
See [here](https://mitoverse.readthedocs.io/en/latest/contact/).
