package org.hisp.dhis.android.dataentry.create;

import org.hisp.dhis.android.dataentry.selection.SelectionArgument;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JUnit4.class)
public class CreateItemsArgumentUnitTests {

    static final int FIRST = 0;
    static final int SECOND = 1;

    static final String ARG_NAME = "arg_name";
    static final String ARG_UID = "arg_uid";
    public static final int SIZE = 2;

    @Test
    public void createWithTEI() {
        CreateItemsArgument.Type argType = CreateItemsArgument.Type.TEI;
        CreateItemsArgument arg = CreateItemsArgument.create(ARG_NAME, ARG_UID, argType);

        assertThat(arg.name()).isEqualTo(ARG_NAME);
        assertThat(arg.uid()).isEqualTo(ARG_NAME);
        assertThat(arg.type()).isEqualTo(argType);
        assertThat(arg.selectorTypes().size()).isEqualTo(SIZE);
        assertThat(arg.selectorTypes().get(FIRST)).isEqualTo(SelectionArgument.Type.ORGANISATION);
        assertThat(arg.selectorTypes().get(SECOND)).isEqualTo(SelectionArgument.Type.PROGRAM);
    }

    @Test
    public void createWithEvent() {
        CreateItemsArgument.Type argType = CreateItemsArgument.Type.EVENT;
        CreateItemsArgument arg = CreateItemsArgument.create(ARG_NAME, ARG_UID, argType);

        assertThat(arg.name()).isEqualTo(ARG_NAME);
        assertThat(arg.uid()).isEqualTo(ARG_NAME);
        assertThat(arg.type()).isEqualTo(argType);
        assertThat(arg.selectorTypes().size()).isEqualTo(SIZE);
        assertThat(arg.selectorTypes().get(FIRST)).isEqualTo(SelectionArgument.Type.ORGANISATION);
        assertThat(arg.selectorTypes().get(SECOND)).isEqualTo(SelectionArgument.Type.PROGRAM);
    }

    @Test
    public void createWithEnrollment() {
        CreateItemsArgument.Type argType = CreateItemsArgument.Type.ENROLLMENT;
        CreateItemsArgument arg = CreateItemsArgument.create(ARG_NAME, ARG_UID, argType);

        assertThat(arg.name()).isEqualTo(ARG_NAME);
        assertThat(arg.uid()).isEqualTo(ARG_NAME);
        assertThat(arg.type()).isEqualTo(argType);
        assertThat(arg.selectorTypes().size()).isEqualTo(SIZE);
        assertThat(arg.selectorTypes().get(FIRST)).isEqualTo(SelectionArgument.Type.ORGANISATION);
        assertThat(arg.selectorTypes().get(SECOND)).isEqualTo(SelectionArgument.Type.PROGRAM);
    }

    @Test
    public void createWithEnrollment_Event() {
        CreateItemsArgument.Type argType = CreateItemsArgument.Type.ENROLMENT_EVENT;
        CreateItemsArgument arg = CreateItemsArgument.create(ARG_NAME, ARG_UID, argType);

        assertThat(arg.name()).isEqualTo(ARG_NAME);
        assertThat(arg.uid()).isEqualTo(ARG_NAME);
        assertThat(arg.type()).isEqualTo(argType);
        assertThat(arg.selectorTypes().size()).isEqualTo(SIZE);
        assertThat(arg.selectorTypes().get(FIRST)).isEqualTo(SelectionArgument.Type.PROGRAM);
        assertThat(arg.selectorTypes().get(SECOND)).isEqualTo(SelectionArgument.Type.PROGRAM_STAGE);
    }
}
