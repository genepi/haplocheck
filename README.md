# Haplocheck
[![Build Status](https://travis-ci.org/genepi/haplocheck.svg?branch=master)](https://travis-ci.org/genepi/haplocheck)

## Run Haplocheck as a cloud service

Haplocheck is a contamination tool using the mtDNA phylogeny and has been integrated into the [mitoverse](https://mitoverse.i-med.ac.at) mtDNA platform based on [Cloudgene](https://www.cloudgene.io). 

## Run Haplocheck locally

Using Cloudgene, the complete workflow can also be executed locally. The final report is located `outfolder/report`.

        curl -s install.cloudgene.io | bash -s 2.0.0-rc9
        ./cloudgene install https://github.com/genepi/haplocheck/releases/download/v1.0.2/haplocheck.zip
        ./cloudgene run haplocheck@1.0.2 --files <input-files> --output <folder>  

## Input File Formats
Haplocheck accepts BAM/CRAM files, vcf.gz as an input.

## Output File Formats
Haplocheck genereates a text file including summary statistics for each sample and a contamination status. It also provides a graphical report which can shared with collaborators.    

## Documentation
Please click [here](https://mitoverse.readthedocs.io/en/latest/) to get the latest documentation.

## Blog
Check out our [blog](http://haplogrep.uibk.ac.at/blog/) regarding mtDNA topics.

## Contact
[Hansi Weissensteiner](mailto:hansi.weissensteiner@i-med.ac.at) ([@haansi](https://twitter.com/whansi)) and [Sebastian Schoenherr](mailto:sebastian.schoenherr@i-med.ac.at) ([@seppinho](https://twitter.com/seppinho)); Division of Genetic Epidemiology, Medical University of Innsbruck;
