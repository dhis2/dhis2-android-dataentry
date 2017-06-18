package org.hisp.dhis.android.dataentry.form.dataentry.fields.optionsrow;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(JUnit4.class)
public class OptionsViewModeTests {

    @Test
    public void createMustPropagateArgumentsCorrectly() {
        OptionsViewModel optionsViewModel = OptionsViewModel.create("test_uid", "test_label",
                "test_hint", true, "test_optionset", "test_value");

        assertThat(optionsViewModel.uid()).isEqualTo("test_uid");
        assertThat(optionsViewModel.label()).isEqualTo("test_label");
        assertThat(optionsViewModel.hint()).isEqualTo("test_hint");
        assertThat(optionsViewModel.mandatory()).isTrue();
        assertThat(optionsViewModel.optionSet()).isEqualTo("test_optionset");
        assertThat(optionsViewModel.value()).isEqualTo("test_value");
    }
}
