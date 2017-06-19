package org.hisp.dhis.android.dataentry.commons.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(JUnit4.class)
public class PreconditionsTests {

    @Test
    public void equalsShouldReturnTrueWhenBothValuesNull() {
        assertThat(Preconditions.equals(null, null)).isTrue();
    }

    @Test
    public void equalsShouldReturnFalseWhenEitherOfObjectsAreNull() {
        assertThat(Preconditions.equals("val_one", null)).isFalse();
        assertThat(Preconditions.equals(null, "val_two")).isFalse();
    }

    @Test
    public void equalsShouldReturnTrueOnTheSameReference() {
        String val = "val";
        assertThat(Preconditions.equals(val, val)).isTrue();
    }

    @Test
    public void equalsShouldReturnTrueOnTheSameValues() {
        assertThat(Preconditions.equals("val", "val")).isTrue();
    }
}
