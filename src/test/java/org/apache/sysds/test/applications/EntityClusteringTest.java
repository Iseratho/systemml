package org.apache.sysds.test.applications;

import org.apache.sysds.common.Types;
import org.apache.sysds.runtime.matrix.data.FrameBlock;
import org.apache.sysds.test.AutomatedTestBase;
import org.apache.sysds.test.TestConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@RunWith(value = Parameterized.class)
public class EntityClusteringTest extends AutomatedTestBase {
    private final static String TEST_NAME = "EntityResolution";
    private final static String TEST_DIR = "applications/entity_resolution/";

    enum BlockingMethod {
        NAIVE,
        LSH,
    };

    private final double threshold;
    private final int numBlocks;
    private final BlockingMethod blockingMethod;
    private final int numLshHashtables;
    private final int numLshHyperplanes;


    @Override
    public void setUp() {
        addTestConfiguration(TEST_DIR, TEST_NAME);
    }

    public EntityClusteringTest(double threshold, int numBlocks, BlockingMethod blockingMethod, int numLshHashtables, int numLshHyperplanes) {
        this.threshold = threshold;
        this.numBlocks = numBlocks;
        this.blockingMethod = blockingMethod;
        this.numLshHashtables = numLshHashtables;
        this.numLshHyperplanes = numLshHyperplanes;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        Object[][] data = new Object[][]{
                {
                        0.3,
                        1,
                        BlockingMethod.NAIVE,
                        0,
                        0,
                },
                {
                        0.3,
                        1,
                        BlockingMethod.LSH,
                        1,
                        1,
                },
                {
                        0.3,
                        1,
                        BlockingMethod.LSH,
                        1,
                        3,
                },
                {
                        0.3,
                        1,
                        BlockingMethod.LSH,
                        3,
                        1,
                },
                {
                        0.3,
                        1,
                        BlockingMethod.LSH,
                        5,
                        5,
                },
        };
        return Arrays.asList(data);
    }

    @Test
    public void testScriptEndToEnd() throws IOException {
        Types.ExecMode platformOld = Types.ExecMode.HYBRID;

        try {
            TestConfiguration config = getTestConfiguration(TEST_NAME);
            loadTestConfiguration(config);
            fullDMLScriptName = "./scripts/algorithms/entity-resolution/entity-clustering.dml";;

            programArgs = new String[]{
                    "-nvargs", //
                    "FX=" + sourceDirectory + "input.csv", //
                    "OUT=" + output("B"), //
                    "threshold=" + this.threshold,
                    "num_blocks=" + this.numBlocks,
                    "blocking_method=" + (this.blockingMethod == BlockingMethod.LSH ? "lsh" : "naive"),
                    "num_hashtables=" + this.numLshHashtables,
                    "num_hyperplanes=" + this.numLshHyperplanes,
            };

            runTest(true, EXCEPTION_NOT_EXPECTED, null, -1);

            // LSH is not deterministic, so in this test we just assert that it runs and produces a file
            if (blockingMethod == BlockingMethod.LSH) {
                Assert.assertTrue(Files.exists(Paths.get(output("B"))));
                return;
            }

            Files.copy(Paths.get(sourceDirectory + "expected.csv"), Paths.get(output("expected.csv")), StandardCopyOption.REPLACE_EXISTING);
            Files.copy(Paths.get(sourceDirectory + "expected.csv.mtd"), Paths.get(output("expected.csv.mtd")), StandardCopyOption.REPLACE_EXISTING);

            FrameBlock expectedPairs = readDMLFrameFromHDFS("expected.csv", Types.FileFormat.CSV);
            FrameBlock predictedPairs = readDMLFrameFromHDFS("B", Types.FileFormat.CSV);



            Iterator<Object[]> expectedIter = expectedPairs.getObjectRowIterator();
            Iterator<Object[]> predictedIter = predictedPairs.getObjectRowIterator();

            int row = 0;
            while (expectedIter.hasNext()) {
                Assert.assertTrue(predictedIter.hasNext());
                Object[] expected = Arrays.copyOfRange(expectedIter.next(), 0, 2);
                Object[] predicted = Arrays.copyOfRange(predictedIter.next(), 0, 2);
                Assert.assertArrayEquals("Row " + row + " differs.", expected, predicted);
                row++;
            }
            Assert.assertEquals(expectedPairs.getNumRows(), predictedPairs.getNumRows());
        } finally {
            rtplatform = platformOld;
        }
    }
}
