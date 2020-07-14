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
public class EntityResolutionTest extends AutomatedTestBase {
    private final static String TEST_NAME = "EntityResolution";
    private final static String TEST_DIR = "applications/entity_resolution/";
    private static final String TEST_CLASS_DIR = TEST_DIR + EntityResolutionTest.class.getSimpleName() + "/";
    private final double[][] adjacencyMatrix;
    private final double[][] expectedMatrix;

    @Override
    public void setUp() {
        addTestConfiguration(TEST_NAME, new TestConfiguration(TEST_CLASS_DIR, "Clustering", new String[]{"B"}));
    }

    public EntityResolutionTest(double[][] adjacencyMatrix, double[][] expectedMatrix) {
        this.adjacencyMatrix = adjacencyMatrix;
        this.expectedMatrix = expectedMatrix;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        Object[][] data = new Object[][]{
                {
                        new double[][]{
                                {0,},
                        },
                        new double[][]{
                                {0,}
                        }
                },
                {
                        new double[][]{
                                {0, 0},
                                {0, 0},
                        },
                        new double[][]{
                                {0, 0},
                                {0, 0},
                        }
                },
                {
                        new double[][]{
                                {0, 1},
                                {1, 0},
                        },
                        new double[][]{
                                {0, 1},
                                {1, 0},
                        }
                },
                {
                        new double[][]{
                                {0, 1, 0},
                                {1, 0, 1},
                                {0, 1, 0},
                        },
                        new double[][]{
                                {0, 1, 1},
                                {1, 0, 1},
                                {1, 1, 0},
                        }
                },
                {
                        new double[][]{
                                {0, 0, 1, 0, 0, 0},
                                {0, 0, 0, 1, 0, 0},
                                {1, 0, 0, 0, 1, 0},
                                {0, 1, 0, 0, 0, 0},
                                {0, 0, 1, 0, 0, 0},
                                {0, 0, 0, 0, 0, 0},
                        },
                        new double[][]{
                                {0, 0, 1, 0, 1, 0},
                                {0, 0, 0, 1, 0, 0},
                                {1, 0, 0, 0, 1, 0},
                                {0, 1, 0, 0, 0, 0},
                                {1, 0, 1, 0, 0, 0},
                                {0, 0, 0, 0, 0, 0},
                        }
                },
                {
                        new double[][]{
                                {0, 0, 0, 0, 0, 0, 0},
                                {0, 0, 0, 0, 0, 0, 0},
                                {0, 0, 0, 0, 0, 0, 0},
                                {0, 0, 0, 0, 0, 0, 0},
                                {0, 0, 0, 0, 0, 0, 0},
                                {0, 0, 0, 0, 0, 0, 0},
                                {0, 0, 0, 0, 0, 0, 0},
                        },
                        new double[][]{
                                {0, 0, 0, 0, 0, 0, 0},
                                {0, 0, 0, 0, 0, 0, 0},
                                {0, 0, 0, 0, 0, 0, 0},
                                {0, 0, 0, 0, 0, 0, 0},
                                {0, 0, 0, 0, 0, 0, 0},
                                {0, 0, 0, 0, 0, 0, 0},
                                {0, 0, 0, 0, 0, 0, 0},
                        }
                },
                {
                        new double[][]{
                                {0, 1, 0, 1, 0, 0, 0},
                                {1, 0, 1, 1, 0, 0, 0},
                                {0, 1, 0, 0, 0, 0, 0},
                                {1, 1, 0, 0, 0, 0, 0},
                                {0, 0, 0, 0, 0, 1, 1},
                                {0, 0, 0, 0, 1, 0, 1},
                                {0, 0, 0, 0, 1, 1, 0},
                        },
                        new double[][]{
                                {0, 1, 1, 1, 0, 0, 0},
                                {1, 0, 1, 1, 0, 0, 0},
                                {1, 1, 0, 1, 0, 0, 0},
                                {1, 1, 1, 0, 0, 0, 0},
                                {0, 0, 0, 0, 0, 1, 1},
                                {0, 0, 0, 0, 1, 0, 1},
                                {0, 0, 0, 0, 1, 1, 0},
                        }
                },
                {
                        new double[][]{
                                {0, 1, 1, 1, 1, 1, 1},
                                {1, 0, 1, 1, 1, 1, 1},
                                {1, 1, 0, 1, 1, 1, 1},
                                {1, 1, 1, 0, 1, 1, 1},
                                {1, 1, 1, 1, 0, 1, 1},
                                {1, 1, 1, 1, 1, 0, 1},
                                {1, 1, 1, 1, 1, 1, 0},
                        },
                        new double[][]{
                                {0, 1, 1, 1, 1, 1, 1},
                                {1, 0, 1, 1, 1, 1, 1},
                                {1, 1, 0, 1, 1, 1, 1},
                                {1, 1, 1, 0, 1, 1, 1},
                                {1, 1, 1, 1, 0, 1, 1},
                                {1, 1, 1, 1, 1, 0, 1},
                                {1, 1, 1, 1, 1, 1, 0},
                        },
                },
        };
        return Arrays.asList(data);
    }

    @Test
    public void testClusteringNormal() {
        Types.ExecMode platformOld = Types.ExecMode.HYBRID;
        disableOutAndExpectedDeletion();

        try {
            loadTestConfiguration(getTestConfiguration(TEST_NAME));

            String HOME = SCRIPT_DIR + TEST_DIR;
            fullDMLScriptName = HOME + "Clustering" + ".dml";

            programArgs = new String[]{"-nvargs",
                    "inFile=" + input("A"), "outFile=" + output("B")
            };

            writeInputMatrixWithMTD("A", this.adjacencyMatrix, false);
            writeExpectedMatrix("B", this.expectedMatrix);

            runTest(true, EXCEPTION_NOT_EXPECTED, null, -1);

            //compare matrices
            compareResults(0.01);
        } finally {
            rtplatform = platformOld;
        }
    }
}
