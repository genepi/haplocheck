# Haplocheck
[![Build Status](https://travis-ci.org/genepi/haplocheck.svg?branch=master)](https://travis-ci.org/genepi/haplocheck)

Haplocheck is a software that leverages the mitochondrial phylogeny to detect contamination in mtDNA sequencing studies and in whole genome sequencing studies. We provide haplocheck as a standalone pipeline for local usage and as a cloud web service (via https://mitoverse.i-med.ac.at). 

## Getting started
` mkdir haplocheck-test
  wget https://github.com/genepi/haplocheck/raw/master/test-data/contamination/1000G/all/1000g-nobaq.vcf.gz
  curl -s install.cloudgene.io | bash
  ./cloudgene install https://github.com/genepi/haplocheck/releases/download/v1.0.6/haplocheck.zip
  ./cloudgene run haplocheck@1.0.6 --files 1000g-nobaq.vcf.gz --output results 
  firefox results/report/report.html`

## Run Haplocheck locally

Using Cloudgene, the complete workflow can also be executed locally. The final HTML report is located at `<out-folder>/report`. Haplocheck requires Java 8 or higher.

        curl -s install.cloudgene.io | bash
        ./cloudgene install https://github.com/genepi/haplocheck/releases/download/v1.0.6/haplocheck.zip
        ./cloudgene run haplocheck@1.0.6 --files <input-files> --output <out-folder>  

## Run Haplocheck as a cloud service

Haplocheck is a contamination tool using the mtDNA phylogeny and has been integrated into the [mitoverse](https://mitoverse.i-med.ac.at) mtDNA platform based on [Cloudgene](https://www.cloudgene.io). 

## Haplocheck Documentation
Documentation can be found [here](https://mitoverse.readthedocs.io/en/latest).

## Blog
Check out our [blog](http://haplogrep.uibk.ac.at/blog/) regarding mtDNA topics.

## Contact
See [here](https://mitoverse.readthedocs.io/en/latest/contact/).
