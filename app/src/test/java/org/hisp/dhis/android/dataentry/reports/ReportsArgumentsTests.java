package org.hisp.dhis.android.dataentry.reports;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(JUnit4.class)
public class ReportsArgumentsTests {

    @Test
    public void propertiesShouldBePropagated() {
        ReportsArguments eventReports = ReportsArguments.createForEvents(
                "test_program_uid", "test_program_name");
        ReportsArguments enrollmentReports = ReportsArguments.createForEnrollments(
                "test_tei_uid", "test_tei_name");

        assertThat(eventReports.entityUid()).isEqualTo("test_program_uid");
        assertThat(eventReports.entityName()).isEqualTo("test_program_name");
        assertThat(eventReports.entityType()).isEqualTo(ReportsArguments.TYPE_EVENTS);

        assertThat(enrollmentReports.entityUid()).isEqualTo("test_tei_uid");
        assertThat(enrollmentReports.entityName()).isEqualTo("test_tei_name");
        assertThat(enrollmentReports.entityType()).isEqualTo(ReportsArguments.TYPE_ENROLLMENTS);
    }
}
