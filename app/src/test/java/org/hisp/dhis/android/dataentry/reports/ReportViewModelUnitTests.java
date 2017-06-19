package org.hisp.dhis.android.dataentry.reports;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(JUnit4.class)
public class ReportViewModelUnitTests {

    @Test
    public void equalsHasCodeShouldConformToContract() {
        ReportViewModel viewModel = ReportViewModel.create(
                ReportViewModel.Status.SYNCED, "test_id", "test_label_one");
        EqualsVerifier.forClass(viewModel.getClass())
                .suppress(Warning.NULL_FIELDS)
                .verify();
    }

    @Test
    public void propertiesShouldBePropagatedToModelCorrectly() {
        ReportViewModel reportViewModel = ReportViewModel.create(
                ReportViewModel.Status.SYNCED, "test_id", "test_label_one");

        assertThat(reportViewModel.id()).isEqualTo("test_id");
        assertThat(reportViewModel.status()).isEqualTo(ReportViewModel.Status.SYNCED);
        assertThat(reportViewModel.labels()).isEqualTo("test_label_one");
    }
}
