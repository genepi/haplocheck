# Haplocheck
[![Build Status](https://travis-ci.org/genepi/haplocheck.svg?branch=master)](https://travis-ci.org/genepi/haplocheck)
[![GitHub release](https://img.shields.io/github/release/genepi/haplocheck.svg)](https://GitHub.com/genepi/haplocheck/releases/)

Haplocheck detects in-sample contamination in mtDNA AND WGS sequencing studies by analyzing only the mitchondrial DNA. You can use our [cloud web service](http://mitoverse.i-med.ac.at) or install it locally. 

The main features of haplocheck are:
* Fast tool to detect in-sample contaminaton by analyzing only the mitochondrial content of sequencing data. 
* Works on VCF and BAM input files.
* It detects contamination by analyzing polymorphic sites in the mtDNA data and tries to classify them into mitochondrial haplogroups using [haplogrep](https://haplogrep.i-med.ac.at/).
* It can be used as a proxy tool to estimate the nDNA contamination levels. Our results show that a high concordance to the 1000G contamination levels (using Verifybamid2) can be achieved but can vary in samples showing large differences in the mtDNA copy number (e.g. due to tissue/cell type).  


## Quick Start (VCF input)

     mkdir haplocheck
     wget https://github.com/genepi/haplocheck/releases/download/v1.3.2/haplocheck.zip
     unzip haplocheck.zip
     ./haplocheck --out <out-file> <input-vcf>
     

## Quick Start (BAM input)

    curl -s install.cloudgene.io | bash -s 2.3.3
    ./cloudgene install https://github.com/genepi/haplocheck/releases/download/v1.3.2/haplocheck.zip 


## Documentation
Full documentation for haplocheck can be found [here](https://mitoverse.readthedocs.io/en/latest). 

## Citation
Weissensteiner H, Forer L, Fendt L, Kheirkhah A, Salas A, Kronenberg F, Schoenherr S. 2021. Contamination detection in sequencing studies using the mitochondrial phylogeny. Genome Research. http://dx.doi.org/10.1101/gr.256545.119.
 
 
## Contact
See [here](https://mitoverse.readthedocs.io/en/latest/contact/).

## mtDNA Blog
Check out our [blog](http://haplogrep.i-med.ac.at/blog/) regarding mtDNA topics.
 
## Data Simulation
The script on how to create in-silico mixtures of two input samples can be found [here](https://github.com/genepi/haplocheck/blob/master/simulateNGSMix/Readme.md). 



