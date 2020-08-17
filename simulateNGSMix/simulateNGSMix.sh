#!/bin/bash
# ------------------------------------------------------------------
#i [Hansi] simulateNGSMix
#          generate in-silico mixtures for haplocheck
# ------------------------------------------------------------------

VERSION=0.1.0
USAGE="Usage: simulateNGSMix -hv file1 file2 sequenceDevice"
EXAMPLE="Example: sh simulateMix.sh file1.fasta file2.fasta HS25"
SUBJECT=simulateNGS-v0
PATHBWAINDEX="ref/chrM.fasta"
OUTPUTFOLDER="out"

# --- Options processing -------------------------------------------

if [ $# -ne 3 ] ;
    then echo $USAGE
	 echo $EXAMPLE
    exit 1;
fi

FullSampleA=$1
FullSampleB=$2

baseNameA=$(basename $1)
baseNameB=$(basename $2)

Sample1=${baseNameA%.*}
Sample2=${baseNameB%.*}

NGSDev=$3

# --- Body --------------------------------------------------------

if [ ! -e $OUTPUTFOLDER ]; then
    mkdir $OUTPUTFOLDER
elif [ ! -d $OUTPUTFOLDER ]; then
    echo "$OUTPUTFOLDER already exists but is not a directory" 1>&2
fi

echo $Sample1

# values in permille e.g. 5 = 0.5%, 10 = 1%,...
for p in 5 7 10 20 30 50  100 250 300 400 450 470 480 490 500
do
prom=$p
postfix=00
# depth/coverage here
for i in  5000 3000 2500 2000 1500 1250 1000 750 500 250 100 75 50 25 10
do
max="$(($i - $(( ($i+1)*$prom/1000 ))))"
min="$(( ($i+1)*$prom/1000 ))"
echo $max $min
echo $i

#simulate reads / install ART NGS read simulator first
art_illumina -i $FullSampleA -p -l 150 -na -ss $NGSDev -f  $max -m 200 -s 10 -o "$postfix""$prom"_"$Sample1"_"$max"
art_illumina -i $FullSampleB -p -l 150 -na -ss $NGSDev -f  $min -m 200 -s 10 -o "$postfix""$prom"_"$Sample2"_"$min"

#mapping
bwa mem -t 4 $PATHBWAINDEX "$postfix""$prom"_"$Sample1"_"$max"1.fq "$postfix""$prom"_"$Sample1"_"$max"2.fq -o "$postfix""$prom"_"$Sample1"_"$max".sam
samtools view -bS "$postfix""$prom"_"$Sample1"_"$max".sam -o "$postfix""$prom"_"$Sample1"_"$max".bam
bwa mem -t 4 $PATHBWAINDEX "$postfix""$prom"_"$Sample2"_"$min"1.fq "$postfix""$prom"_"$Sample2"_"$min"2.fq -o "$postfix""$prom"_"$Sample2"_"$min".sam
samtools view -bS "$postfix""$prom"_"$Sample2"_"$min".sam -o "$postfix""$prom"_"$Sample2"_"$min".bam


#merge
samtools merge "$postfix""$prom"_"$Sample1"_"$max"_"$Sample2"_"$min".bam "$postfix""$prom"_"$Sample1"_"$max".bam  "$postfix""$prom"_"$Sample2"_"$min".bam

#cleanup
rm "$postfix""$prom"_"$Sample1"_"$max"1.fq ""$postfix"$prom"_"$Sample1"_"$max"2.fq "$postfix""$prom"_"$Sample1"_"$max".sam  "$postfix""$prom"_"$Sample1"_"$max".bam
rm "$postfix""$prom"_"$Sample2"_"$min"1.fq "$postfix""$prom"_"$Sample2"_"$min"2.fq "$postfix""$prom"_"$Sample2"_"$min".sam "$postfix""$prom"_"$Sample2"_"$min".bam

mv "$postfix""$prom"_"$Sample1"_"$max"_"$Sample2"_"$min".bam "$OUTPUTFOLDER"/"$postfix""$prom"_"$Sample1"_"$max"_"$Sample2"_"$min".bam 

done
done

