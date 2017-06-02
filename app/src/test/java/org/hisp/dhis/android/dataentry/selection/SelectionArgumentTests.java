package org.hisp.dhis.android.dataentry.selection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JUnit4.class)
public class SelectionArgumentTests {

    public static final String UID = "argUid";
    public static final String NAME = "argName";

    @Test
    public void create() {
        SelectionArgument arg = SelectionArgument.create(UID, NAME);
        assertThat(arg.uid()).isEqualTo(UID);
        assertThat(arg.name()).isEqualTo(NAME);
    }

    @Test (expected = NullPointerException.class)
    public void create_nullName() {
        SelectionArgument arg = SelectionArgument.create(UID, null);
    }

    @Test (expected = NullPointerException.class)
    public void create_nullUid() {
        SelectionArgument arg = SelectionArgument.create(null, NAME);
    }
}
