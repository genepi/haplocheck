package genepi.haplocheck.steps.contamination;

import java.util.ArrayList;

import core.SampleFile;
import exceptions.parse.HsdFileException;
import phylotree.Phylotree;
import search.ranking.HammingRanking;
import search.ranking.JaccardRanking;
import search.ranking.KulczynskiRanking;
import search.ranking.RankingMethod;

public class HaplogroupClassifier {

	public SampleFile calculateHaplogrops(Phylotree phylotree, ArrayList<String> profiles) {

		return calculateHaplogrops(phylotree, profiles, "kulczynski");
	}

	public SampleFile calculateHaplogrops(Phylotree phylotree, ArrayList<String> profiles, String metric) {

		RankingMethod newRanker = null;

		switch (metric) {

		case "kulczynski":
			newRanker = new KulczynskiRanking(1);
			break;

		case "hamming":
			newRanker = new HammingRanking(1);
			break;

		case "jaccard":
			newRanker = new JaccardRanking(1);
			break;

		default:
			newRanker = new KulczynskiRanking(1);

		}

		SampleFile samples = null;
		try {
			samples = new SampleFile(profiles);

			samples.updateClassificationResults(phylotree, newRanker);

			return samples;

		} catch (HsdFileException e) {
			e.printStackTrace();
		}
		return samples;

	}
}
