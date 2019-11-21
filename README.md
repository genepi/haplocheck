# Haplocheck
[![Build Status](https://travis-ci.org/genepi/haplocheck.svg?branch=master)](https://travis-ci.org/genepi/haplocheck)

Haplocheck is a software that leverages the mitochondrial phylogeny to detect contamination in sequencing studies. We provide haplocheck as a standalone pipeline for local usage and as a [cloud web service](https://mitoverse.i-med.ac.at). 

## Getting started
To calculate the contamination status of all 1000 Genomes Phase3 samples, execute the following command:

    mkdir haplocheck 
    cd haplocheck
    wget https://github.com/genepi/haplocheck/raw/master/test-data/contamination/1000G/all/1000g-nobaq.vcf.gz  
    curl -s install.cloudgene.io | bash 
    ./cloudgene install https://github.com/genepi/haplocheck/releases/download/v1.0.7/haplocheck.zip 
    ./cloudgene run haplocheck@1.0.7 --files 1000g-nobaq.vcf.gz --output results  
    firefox results/report/report.html

## Documentation
Documentation can be found [here](https://mitoverse.readthedocs.io/en/latest). This documentation also includes a section how  to [interpret the result files](https://mitoverse.readthedocs.io/en/latest/interpret/).

## mtDNA Blog
Check out our [blog](http://haplogrep.uibk.ac.at/blog/) regarding mtDNA topics.

## Contact
See [here](https://mitoverse.readthedocs.io/en/latest/contact/).
