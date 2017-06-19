package org.hisp.dhis.android.dataentry.commons.tuples;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import nl.jqno.equalsverifier.EqualsVerifier;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.fail;

@RunWith(JUnit4.class)
public class SingleUnitTests {

    @Test
    public void equalsAndHashcodeShouldConformToContract() {
        EqualsVerifier.forClass(Single.create("one").getClass());
    }

    @Test
    public void createMustThrowOnNullVal() {
        try {
            Single.create(null);
            fail("NullPointerException was expected, but nothing was thrown");
        } catch (NullPointerException nullPointerException) {
            // noop
        }
    }

    @Test
    public void createMustPropagatePropertiesCorrectly() {
        Single single = Single.create("one");
        assertThat(single.val0()).isEqualTo("one");
    }
}
