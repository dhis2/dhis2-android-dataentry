package org.hisp.dhis.android.dataentry.commons.utils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(JUnit4.class)
public class CodeGeneratorTests {

    private CodeGenerator codeGenerator;

    @Before
    public void setUp() throws Exception {
        codeGenerator = new CodeGeneratorImpl();
    }

    @Test
    public void testCode() {
        int numberOfCodes = 500;

        Set<String> codes = new HashSet<>();
        for (int n = 0; n < numberOfCodes; ++n) {
            String code = codeGenerator.generate();

            // Test syntax
            assertThat(code.substring(0, 1).matches("[a-zA-Z]")).isTrue();
            assertThat(code.matches("[0-9a-zA-Z]{11}")).isTrue();

            // Test uniqueness
            assertThat(codes.add(code)).isTrue();
        }
    }
}