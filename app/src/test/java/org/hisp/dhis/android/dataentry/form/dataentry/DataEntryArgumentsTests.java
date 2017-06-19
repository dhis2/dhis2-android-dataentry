package org.hisp.dhis.android.dataentry.form.dataentry;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.Assert.fail;

@RunWith(JUnit4.class)
public class DataEntryArgumentsTests {


    @Test
    public void forEventShouldThrowOnNullEvent() {
        try {
            DataEntryArguments.forEvent(null);
            fail("NullPointerException was expected, but nothing was thrown");
        } catch (NullPointerException nullPointerException) {
            // noop
        }
    }

    @Test
    public void forEventWithSectionShouldThrowOnNullEvent() {
        try {
            DataEntryArguments.forEventSection(null, "section");
            fail("NullPointerException was expected, but nothing was thrown");
        } catch (NullPointerException nullPointerException) {
            // noop
        }
    }

    @Test
    public void forEventWithSectionShouldThrowOnNullSection() {
        try {
            DataEntryArguments.forEventSection("event", null);
            fail("NullPointerException was expected, but nothing was thrown");
        } catch (NullPointerException nullPointerException) {
            // noop
        }
    }

    @Test
    public void forEnrollmentShouldThrowOnNullEnrollment() {
        try {
            DataEntryArguments.forEnrollment(null);
            fail("NullPointerException was expected, but nothing was thrown");
        } catch (NullPointerException nullPointerException) {
            // noop
        }
    }

    @Test
    public void forEventShouldPropagatePropertiesCorrectly() {
        DataEntryArguments dataEntryArguments = DataEntryArguments.forEvent("event");

        assertThat(dataEntryArguments.event()).isEqualTo("event");
        assertThat(dataEntryArguments.enrollment()).isEmpty();
        assertThat(dataEntryArguments.section()).isEmpty();
    }

    @Test
    public void forEventWithSectionShouldPropagatePropertiesCorrectly() {
        DataEntryArguments dataEntryArguments = DataEntryArguments
                .forEventSection("event", "section");

        assertThat(dataEntryArguments.event()).isEqualTo("event");
        assertThat(dataEntryArguments.section()).isEqualTo("section");
        assertThat(dataEntryArguments.enrollment()).isEmpty();
    }

    @Test
    public void forEnrollmentShouldPropagatePropertiesCorrectly() {
        DataEntryArguments dataEntryArguments = DataEntryArguments
                .forEnrollment("enrollment");

        assertThat(dataEntryArguments.enrollment()).isEqualTo("enrollment");
        assertThat(dataEntryArguments.event()).isEmpty();
        assertThat(dataEntryArguments.section()).isEmpty();
    }
}
