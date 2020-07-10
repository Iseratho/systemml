package org.apache.sysds.test.applications;

import org.apache.sysds.common.Types;
import org.apache.sysds.test.AutomatedTestBase;
import org.apache.sysds.test.TestConfiguration;
import org.junit.Test;

public class EntityResolutionTest extends AutomatedTestBase {
    private final static String TEST_NAME = "EntityResolution";
    private final static String TEST_DIR = "applications/entity_resolution/";
    private static final String TEST_CLASS_DIR = TEST_DIR + EntityResolutionTest.class.getSimpleName() + "/";

    @Override public void setUp() {
        addTestConfiguration(TEST_NAME, new TestConfiguration(TEST_CLASS_DIR, "Clustering", new String[] {"B"}));
    }

    @Test
    public void testClustering() {
        Types.ExecMode platformOld = Types.ExecMode.HYBRID;
        disableOutAndExpectedDeletion();

        try
        {
            loadTestConfiguration(getTestConfiguration(TEST_NAME));

            String HOME = SCRIPT_DIR + TEST_DIR;
            fullDMLScriptName = HOME + "Clustering" + ".dml";

            programArgs = new String[]{"-nvargs",
                    "inFile=" + input("A"), "outFile=" + output("B")
            };

            //generate actual dataset
            double[][] inputMat = new double[][] {{0, 1, 0, 1, 0, 0, 0},
                                                  {1, 0, 1, 1, 0, 0, 0},
                                                  {0, 1, 0, 0, 0, 0, 0},
                                                  {1, 1, 0, 0, 0, 0, 0},
                                                  {0, 0, 0, 0, 0, 1, 1},
                                                  {0, 0, 0, 0, 1, 0, 1},
                                                  {0, 0, 0, 0, 1, 1, 0}};

            double[][] expected = new double[][] {{0, 1, 1, 1, 0, 0, 0},
                                                  {1, 0, 1, 1, 0, 0, 0},
                                                  {1, 1, 0, 1, 0, 0, 0},
                                                  {1, 1, 1, 0, 0, 0, 0},
                                                  {0, 0, 0, 0, 0, 1, 1},
                                                  {0, 0, 0, 0, 1, 0, 1},
                                                  {0, 0, 0, 0, 1, 1, 0}};

            writeInputMatrixWithMTD("A", inputMat, true);
            writeExpectedMatrix("B", expected);

            runTest(true, EXCEPTION_NOT_EXPECTED, null, -1);

            //compare matrices
            compareResults(0.01);
        }
        finally {
            rtplatform = platformOld;
        }
    }
}
