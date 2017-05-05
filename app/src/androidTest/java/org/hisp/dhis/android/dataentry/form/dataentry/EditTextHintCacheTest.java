package org.hisp.dhis.android.dataentry.form.dataentry;

import android.support.test.InstrumentationRegistry;

import org.hisp.dhis.android.dataentry.R;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class EditTextHintCacheTest {

    private EditTextHintCacheImpl editTextHintCache;

    @Before
    public void setUp() throws Exception {
        editTextHintCache = new EditTextHintCacheImpl(InstrumentationRegistry.getTargetContext());
    }

    @Test
    public void returnEditTextHintsMatchingResourceId() throws Exception {
        assertThat(editTextHintCache.hint(R.string.enter_text)).isEqualTo("Enter text");
        assertThat(editTextHintCache.hint(R.string.enter_long_text)).isEqualTo("Enter long text");
        assertThat(editTextHintCache.hint(R.string.enter_number)).isEqualTo("Enter number");
        assertThat(editTextHintCache.hint(R.string.enter_integer)).isEqualTo("Enter integer");
        assertThat(editTextHintCache.hint(R.string.enter_negative_integer)).isEqualTo("Enter negative integer");
        assertThat(editTextHintCache.hint(R.string.enter_positive_integer_or_zero))
                .isEqualTo("Enter positive integer or zero");
        assertThat(editTextHintCache.hint(R.string.enter_positive_integer)).isEqualTo("Enter positive integer");
        assertThat(editTextHintCache.hint(R.string.enter_date)).isEqualTo("Enter date");
        assertThat(editTextHintCache.hint(R.string.enter_coordinates)).isEqualTo("Enter coordinates");
        assertThat(editTextHintCache.hint(R.string.enter_option_set)).isEqualTo("Select or search from the list");
    }

}