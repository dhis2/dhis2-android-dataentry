package org.hisp.dhis.android.dataentry.selection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JUnit4.class)
public class SelectionArgumentTests {
    private static final String UID = "argUid";
    private static final String NAME = "argName";

    @Test
    public void create() {
        SelectionArgument arg = SelectionArgument.create(UID, NAME, SelectionArgument.Type.PROGRAM);
        assertThat(arg.uid()).isEqualTo(UID);
        assertThat(arg.name()).isEqualTo(NAME);
        assertThat(arg.type()).isEqualTo(SelectionArgument.Type.PROGRAM);
    }

    @Test(expected = NullPointerException.class)
    public void create_nullName() {
        SelectionArgument.create(UID, null, SelectionArgument.Type.PROGRAM);
    }

    @Test(expected = NullPointerException.class)
    public void create_nullUid() {
        SelectionArgument.create(null, NAME, SelectionArgument.Type.PROGRAM);
    }

    @Test(expected = NullPointerException.class)
    public void create_nullType() {
        SelectionArgument.create(UID, NAME, null);
    }
}
