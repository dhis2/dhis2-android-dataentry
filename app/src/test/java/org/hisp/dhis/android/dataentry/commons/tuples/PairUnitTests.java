package org.hisp.dhis.android.dataentry.commons.tuples;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import nl.jqno.equalsverifier.EqualsVerifier;

@RunWith(JUnit4.class)
public class PairUnitTests {

    @Test
    public void equalsAndHashcodeShouldConformToContract() {
        EqualsVerifier.forClass(Pair.create("one", "two").getClass());
    }
}
