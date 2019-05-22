library("ggplot2")
library("tidyverse")
library("ggpubr")
setwd("/data2/eclipse/mtdna/haplocheck/paper-data/");
file = "simulated-data/output/simulation_3_All_500k_kulczynski.txt";
simAll = read.table(file, header = TRUE, sep=",");
simAllFilter = filter(simAll,simAll$HaplogrepFilter == 0.5  & simAll$Setup <= 7 & simAll$Group<=12);
simAllFilter$F1 = 2*(simAllFilter$Sensitivity * simAllFilter$Precision/(simAllFilter$Sensitivity + simAllFilter$Precision));
p <- ggplot(simAllFilter, aes(x = Group, y = F1,colour=factor(Setup))) + 
  geom_line();
p <- p + xlab('Variant Noise') +   ylab('F1 Score') + labs(colour = "Haplocheck Setup") + 
  scale_x_continuous(breaks = c(0,4,8,12,16), labels = c("0", "4", "8", "12","16")) 
p <- p + scale_colour_brewer(palette = "Set1")
all_final <- p + theme_light() +  theme(legend.position="top")


filterAllSetup = filter(simAll,simAll$HaplogrepFilter == 0.5  & simAll$Setup ==3);
filterAllSetup$F1 = 2*(filterAllSetup$Sensitivity * filterAllSetup$Precision/(filterAllSetup$Sensitivity + filterAllSetup$Precision));
filterAllSetup

filterAllSetup = filter(simAll,simAll$HaplogrepFilter == 0.5  & simAll$Setup ==4);
filterAllSetup$F1 = 2*(filterAllSetup$Sensitivity * filterAllSetup$Precision/(filterAllSetup$Sensitivity + filterAllSetup$Precision));
filterAllSetup

file = "simulated-data/output/simulation_2_H_100K_kulczynski.txt";
simH = read.table(file, header = TRUE, sep=",");
simHFilter = filter(simH,simH$HaplogrepFilter == 0.5  & simH$Setup <= 7 & simH$Group<=8);
simHFilter$F1 = 2*(simHFilter$Sensitivity * simHFilter$Precision/(simHFilter$Sensitivity + simHFilter$Precision));
p <- ggplot(simHFilter, aes(x = Group, y = F1,colour=factor(Setup))) + 
  geom_line();
p <- p + xlab('Variant Noise') +   ylab('F1 Score') + labs(colour = "Haplocheck Setup") + 
  scale_x_continuous(breaks = c(0,4,8,12,16), labels = c("0", "4", "8", "12","16")) 
h_final <- p + scale_colour_brewer(palette = "Set1") + theme_light() +  theme(legend.position="top")


filterHSetup = filter(simH,simH$HaplogrepFilter == 0.5  & simH$Setup ==3);
filterHSetup$F1 = 2*(filterHSetup$Sensitivity * filterHSetup$Precision/(filterHSetup$Sensitivity + filterHSetup$Precision));
filterHSetup

filterHSetup = filter(simH,simH$HaplogrepFilter == 0.5  & simH$Setup ==4);
filterHSetup$F1 = 2*(filterHSetup$Sensitivity * filterHSetup$Precision/(filterHSetup$Sensitivity + filterHSetup$Precision));
filterHSetup

ggarrange(all_final, h_final, labels = c("A", "B"),
          ncol = 2);

file = "haplogroups.csv";
hg = read.table(file, header = TRUE, sep=",")

hg$Population= str_sub(hg$HG.mutserve, 1, 1)

p = ggplot(hg, aes(x=Quality.calmom, y=Quality.mutserve, color=Population)) + geom_point() +  xlab('Quality Mutserve') +   ylab('Quality calmom')
haplogroups_final <- p + theme_light() +  theme(legend.position="top")
haplogroups_final
