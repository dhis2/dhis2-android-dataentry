package org.hisp.dhis.android.dataentry.commons.tuples;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import nl.jqno.equalsverifier.EqualsVerifier;

@RunWith(JUnit4.class)
public class SextetUnitTests {

    @Test
    public void equalsAndHashcodeShouldConformToContract() {
        EqualsVerifier.forClass(Sextet.create("one", "two", "three", "four", "five", "six").getClass());
    }
}
