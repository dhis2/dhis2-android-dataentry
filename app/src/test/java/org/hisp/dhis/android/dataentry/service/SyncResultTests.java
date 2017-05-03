package org.hisp.dhis.android.dataentry.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.Assert.fail;

@RunWith(JUnit4.class)
public class SyncResultTests {

    @Test
    public void successShouldReturnInstanceWithCorrectFlags() {
        SyncResult success = SyncResult.success();

        assertThat(success.isSuccess()).isTrue();
        assertThat(success.inProgress()).isFalse();
        assertThat(success.isIdle()).isFalse();
        assertThat(success.message()).isEmpty();
    }

    @Test
    public void failureShouldReturnInstanceWithCorrectFlagsAndMessage() {
        SyncResult failure = SyncResult.failure("test");

        assertThat(failure.isSuccess()).isFalse();
        assertThat(failure.inProgress()).isFalse();
        assertThat(failure.isIdle()).isFalse();
        assertThat(failure.message()).isEqualTo("test");
    }

    @Test
    public void idleShouldReturnInstanceWithCorrectFlags() {
        SyncResult idle = SyncResult.idle();

        assertThat(idle.isIdle()).isTrue();
        assertThat(idle.isSuccess()).isFalse();
        assertThat(idle.inProgress()).isFalse();
        assertThat(idle.message()).isEmpty();
    }

    @Test
    public void inProgressShouldReturnInstanceWithCorrectFlags() {
        SyncResult inProgress = SyncResult.progress();

        assertThat(inProgress.inProgress()).isTrue();
        assertThat(inProgress.isIdle()).isFalse();
        assertThat(inProgress.isSuccess()).isFalse();
        assertThat(inProgress.message()).isEmpty();
    }

    @Test
    public void failureShouldThrowOnNullMessage() {
        try {
            SyncResult.failure(null);
            fail("Expected NullPointerException, but nothing was thrown.");
        } catch (NullPointerException nullPointerException) {
            // noop
        }
    }

    @Test
    public void equalsAndHashcodeShouldConformToContract() {
        EqualsVerifier.forClass(SyncResult.idle().getClass())
                .suppress(Warning.NULL_FIELDS)
                .verify();
    }
}
