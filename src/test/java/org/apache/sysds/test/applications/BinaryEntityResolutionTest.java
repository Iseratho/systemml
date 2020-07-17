package org.apache.sysds.test.applications;

import org.apache.sysds.common.Types;
import org.apache.sysds.runtime.matrix.data.FrameBlock;
import org.apache.sysds.test.AutomatedTestBase;
import org.apache.sysds.test.TestConfiguration;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

@RunWith(value = Parameterized.class)
public class BinaryEntityResolutionTest extends AutomatedTestBase {
    private final static String TEST_NAME = "EntityResolution";
    private final static String TEST_DIR = "applications/entity_resolution/";

    private final int numLshHashtables;
    private final int numLshHyperplanes;


    @Override
    public void setUp() {
        addTestConfiguration(TEST_DIR, TEST_NAME);
    }

    public BinaryEntityResolutionTest(int numLshHashtables, int numLshHyperplanes) {
        this.numLshHashtables = numLshHashtables;
        this.numLshHyperplanes = numLshHyperplanes;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        Object[][] data = new Object[][]{
                {
                        1,
                        1,
                },
                {
                        1,
                        3,
                },
                {
                        3,
                        1,
                },
                {
                        5,
                        5,
                },
        };
        return Arrays.asList(data);
    }

    @Test
    public void testScriptEndToEnd() {
        Types.ExecMode platformOld = Types.ExecMode.HYBRID;

        try {
            TestConfiguration config = getTestConfiguration(TEST_NAME);
            loadTestConfiguration(config);
            fullDMLScriptName = "./scripts/staging/entity-resolution/binary-entity-resolution.dml";;

            programArgs = new String[]{
                    "-nvargs", //
                    "FX=" + sourceDirectory + "input.csv", //
                    "FY=" + sourceDirectory + "input.csv", //
                    "OUT=" + output("B"), //
                    "num_hashtables=" + this.numLshHashtables,
                    "num_hyperplanes=" + this.numLshHyperplanes,
            };

            runTest(true, EXCEPTION_NOT_EXPECTED, null, -1);

            // LSH is not deterministic, so in this test we just assert that it runs and produces a file
            Assert.assertTrue(Files.exists(Paths.get(output("B"))));
            tearDown();
        } finally {
            rtplatform = platformOld;
        }
    }
}
