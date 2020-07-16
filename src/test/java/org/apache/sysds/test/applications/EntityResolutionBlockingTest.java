package org.apache.sysds.test.applications;

import org.apache.sysds.common.Types;
import org.apache.sysds.test.AutomatedTestBase;
import org.apache.sysds.test.TestConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(value = Parameterized.class)
public class EntityResolutionBlockingTest extends AutomatedTestBase {
    private final static String TEST_NAME = "EntityResolution";
    private final static String TEST_DIR = "applications/entity_resolution/";
    private static final String TEST_CLASS_DIR = TEST_DIR + EntityResolutionBlockingTest.class.getSimpleName() + "/";
    private final double[][] dataset;
    private final int targetNumBlocks;
    private final double[][] expectedBlockingIndices;

    @Override
    public void setUp() {
        addTestConfiguration(TEST_NAME, new TestConfiguration(TEST_CLASS_DIR, "blocking_naive", new String[]{"B"}));
    }

    public EntityResolutionBlockingTest(double[][] dataset,int targetNumBlocks, double[][] expectedBlockingIndices) {
        this.dataset = dataset;
        this.targetNumBlocks = targetNumBlocks;
        this.expectedBlockingIndices = expectedBlockingIndices;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        Object[][] data = new Object[][]{
                {
                        new double[][]{
                                {0,},
                        },
                        10,
                        new double[][]{
                                {1,},
                                {2,},
                        }
                },
                {
                        new double[][]{
                                {0,},
                                {1,},
                        },
                        1,
                        new double[][]{
                                {1,},
                                {3,},
                        }
                },
                {
                        new double[][]{
                                {0,},
                                {1,},
                        },
                        2,
                        new double[][]{
                                {1,},
                                {2,},
                                {3,},
                        }
                },
                {
                        new double[][]{
                                {0,},
                                {1,},
                                {2,},
                        },
                        2,
                        new double[][]{
                                {1,},
                                {3,},
                                {4,},
                        }
                },
        };
        return Arrays.asList(data);
    }

    @Test
    public void testNaiveBlocking() {
        Types.ExecMode platformOld = Types.ExecMode.HYBRID;

        try {
            TestConfiguration config = getTestConfiguration(TEST_NAME);
            loadTestConfiguration(config);
            fullDMLScriptName = SCRIPT_DIR + TEST_DIR + config.getTestScript() + ".dml";

            programArgs = new String[]{
                    "-nvargs", //
                    "inFile=" + input("A"), //
                    "outFile=" + output("B"), //
                    "targetNumBlocks=" + this.targetNumBlocks
            };
            writeInputMatrixWithMTD("A", this.dataset, false);
            writeExpectedMatrix("B", this.expectedBlockingIndices);
            runTest(true, EXCEPTION_NOT_EXPECTED, null, -1);
            compareResults();
        } finally {
            rtplatform = platformOld;
        }
    }
}
