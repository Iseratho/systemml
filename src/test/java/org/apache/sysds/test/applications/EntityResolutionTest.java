package org.apache.sysds.test.applications;

import org.apache.commons.io.FileUtils;
import org.apache.sysds.common.Types;
import org.apache.sysds.runtime.matrix.data.FrameBlock;
import org.apache.sysds.test.AutomatedTestBase;
import org.apache.sysds.test.TestConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@RunWith(value = Parameterized.class)
public class EntityResolutionTest extends AutomatedTestBase {
    private final static String TEST_NAME = "EntityResolution";
    private final static String TEST_DIR = "applications/entity_resolution/";

    private final double threshold;
    private final int numBlocks;

    @Override
    public void setUp() {
        addTestConfiguration(TEST_DIR, TEST_NAME);
    }

    public EntityResolutionTest(double threshold, int numBlocks) {
        this.threshold = threshold;
        this.numBlocks = numBlocks;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        Object[][] data = new Object[][]{
                {
                        0.01,
                        1
                }//,
//                {
//                        0.9,
//                        1
//                },
//                {
//                        0.1,
//                        1
//                },
//                {
//                        0.1,
//                        2
//                },
//                {
//                        0.1,
//                        3
//                },
        };
        return Arrays.asList(data);
    }

    @Test
    public void testScriptEndToEnd() throws IOException {
        Types.ExecMode platformOld = Types.ExecMode.HYBRID;
        disableOutAndExpectedDeletion();

        try {
            TestConfiguration config = getTestConfiguration(TEST_NAME);
            loadTestConfiguration(config);
            fullDMLScriptName = "./scripts/algorithms/entity-resolution/entity-clustering.dml";;

            programArgs = new String[]{
                    "-nvargs", //
                    "FX=" + sourceDirectory + "input.csv", //
                    "OUT=" + output("B"), //
                    "threshold=" + this.threshold,
                    "num_blocks=" + this.numBlocks
            };

            runTest(true, EXCEPTION_NOT_EXPECTED, null, -1);

            Files.copy(Paths.get(sourceDirectory + "expected.csv"), Paths.get(output("expected.csv")), StandardCopyOption.REPLACE_EXISTING);
            Files.copy(Paths.get(sourceDirectory + "expected.csv.mtd"), Paths.get(output("expected.csv.mtd")), StandardCopyOption.REPLACE_EXISTING);

            Thread.sleep(100);

            FrameBlock expectedPairs = readDMLFrameFromHDFS("expected.csv", Types.FileFormat.CSV);
            FrameBlock predictedPairs = readDMLFrameFromHDFS("B", Types.FileFormat.CSV);

            Iterator<Object[]> expectedIter = expectedPairs.getObjectRowIterator();
            Iterator<Object[]> predictedIter = predictedPairs.getObjectRowIterator();

            int row = 0;
            while (expectedIter.hasNext()) {
                Assert.assertTrue(predictedIter.hasNext());
                Object[] expected = expectedIter.next();
                Object[] predicted = predictedIter.next();
                Assert.assertArrayEquals("Row " + row + " differs.", expected, predicted);
                row++;
            }
            Assert.assertEquals(expectedPairs.getNumRows(), predictedPairs.getNumRows());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            rtplatform = platformOld;
        }
    }
}
