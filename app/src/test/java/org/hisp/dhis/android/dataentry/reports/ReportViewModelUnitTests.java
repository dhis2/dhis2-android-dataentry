package org.hisp.dhis.android.dataentry.reports;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.Assert.fail;

@RunWith(JUnit4.class)
public class ReportViewModelUnitTests {

    @Test
    public void equalsHasCodeShouldConformToContract() {
        EqualsVerifier.forClass(ReportViewModel.create("test_id", ReportViewModel.Status.SYNCED,
                Arrays.asList("test_label_one", "test_label_two")).getClass())
                .suppress(Warning.NULL_FIELDS)
                .verify();
    }

    @Test
    public void labelsShouldBeImmutable() {
        ReportViewModel reportViewModel = ReportViewModel.create("test_id",
                ReportViewModel.Status.SYNCED, Arrays.asList("test_label_one", "test_label_two"));

        try {
            reportViewModel.labels().add("another_label");
            fail("list of labels should be immutable");
        } catch (UnsupportedOperationException exception) {
            // expected
        }
    }

    @Test
    public void propertiesShouldBePropagatedToModelCorrectly() {
        ReportViewModel reportViewModel = ReportViewModel.create("test_id",
                ReportViewModel.Status.SYNCED, Arrays.asList("test_label_one", "test_label_two"));

        assertThat(reportViewModel.id()).isEqualTo("test_id");
        assertThat(reportViewModel.status()).isEqualTo(ReportViewModel.Status.SYNCED);
        assertThat(reportViewModel.labels().size()).isEqualTo(2);
        assertThat(reportViewModel.labels().get(0)).isEqualTo("test_label_one");
        assertThat(reportViewModel.labels().get(1)).isEqualTo("test_label_two");
    }
}
