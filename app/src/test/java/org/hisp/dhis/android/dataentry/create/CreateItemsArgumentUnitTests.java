package org.hisp.dhis.android.dataentry.create;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JUnit4.class)
public class CreateItemsArgumentUnitTests {
    private static final String ARG_NAME = "arg_name";
    private static final String ARG_UID = "arg_uid";

    @Test
    public void createWithTEI() {
        CreateItemsArgument.Type argType = CreateItemsArgument.Type.TEI;
        CreateItemsArgument arg = CreateItemsArgument.create(ARG_UID, ARG_NAME, argType);

        assertThat(arg.uid()).isEqualTo(ARG_UID);
        assertThat(arg.name()).isEqualTo(ARG_NAME);
        assertThat(arg.type()).isEqualTo(argType);
    }

    @Test
    public void createWithEvent() {
        CreateItemsArgument.Type argType = CreateItemsArgument.Type.EVENT;
        CreateItemsArgument arg = CreateItemsArgument.create(ARG_UID, ARG_NAME, argType);

        assertThat(arg.uid()).isEqualTo(ARG_UID);
        assertThat(arg.name()).isEqualTo(ARG_NAME);
        assertThat(arg.type()).isEqualTo(argType);
    }

    @Test
    public void createWithEnrollment() {
        CreateItemsArgument.Type argType = CreateItemsArgument.Type.ENROLLMENT;
        CreateItemsArgument arg = CreateItemsArgument.create(ARG_UID, ARG_NAME, argType);

        assertThat(arg.uid()).isEqualTo(ARG_UID);
        assertThat(arg.name()).isEqualTo(ARG_NAME);
        assertThat(arg.type()).isEqualTo(argType);
    }

    @Test
    public void createWithEnrollmentEvent() {
        CreateItemsArgument.Type argType = CreateItemsArgument.Type.ENROLLMENT_EVENT;
        CreateItemsArgument arg = CreateItemsArgument.create(ARG_UID, ARG_NAME, argType);

        assertThat(arg.uid()).isEqualTo(ARG_UID);
        assertThat(arg.name()).isEqualTo(ARG_NAME);
        assertThat(arg.type()).isEqualTo(argType);
    }
}
