package genepi.contamination.reader;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.junit.Test;

import contamination.objects.Variant;
import importer.VcfImporter;
import contamination.objects.Sample;

public class MutationServerReaderTest {

	@Test
	public void testReadVariantFile() throws Exception {

		VcfImporter reader = new VcfImporter();

		HashMap<String, Sample> samples = reader.load(new File("test-data/contamination/lab-mixture/variants-mixture.vcf"), false);
		ArrayList<Integer> posArray = new ArrayList<>();
		
		for (Sample sample : samples.values()) {
			Collection<Variant> variants = sample.getVariants();
			int count = 0;

			for (Variant pos : variants) {
				posArray.add(pos.getPos());
				count++;

				if (pos.getPos() == 16270) {
					assertEquals('T',pos.getMinor());
					assertEquals('C',pos.getMajor());
					assertEquals(3848, pos.getCoverage());
					assertEquals(pos.getType(), 2);
					assertEquals(pos.getVariant(), 'T');
					assertEquals(pos.getRef(), 'C');
					assertEquals(pos.getLevel(), 0.013, 0.0);
					assertEquals(pos.getMajorLevel(), 0.987, 0.0);
					assertEquals(pos.getMinorLevel(), 0.013, 0.0);
				}
			}

			assertEquals(25, count);
			assertEquals(7, sample.getAmountHomoplasmies());
			assertEquals(18, sample.getAmountHeteroplasmies());
			assertEquals(true, posArray.contains(11719));
			assertEquals(true, posArray.contains(15236));
		}
	}

}
