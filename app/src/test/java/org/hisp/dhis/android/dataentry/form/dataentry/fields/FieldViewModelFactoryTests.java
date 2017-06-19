package org.hisp.dhis.android.dataentry.form.dataentry.fields;

import android.support.annotation.NonNull;
import android.text.InputType;

import org.hisp.dhis.android.core.common.ValueType;
import org.hisp.dhis.android.dataentry.form.dataentry.fields.checkbox.CheckBoxViewModel;
import org.hisp.dhis.android.dataentry.form.dataentry.fields.edittext.EditTextDoubleViewModel;
import org.hisp.dhis.android.dataentry.form.dataentry.fields.edittext.EditTextIntegerViewModel;
import org.hisp.dhis.android.dataentry.form.dataentry.fields.edittext.EditTextViewModel;
import org.hisp.dhis.android.dataentry.form.dataentry.fields.optionsrow.OptionsViewModel;
import org.hisp.dhis.android.dataentry.form.dataentry.fields.radiobutton.RadioButtonViewModel;
import org.hisp.dhis.android.dataentry.form.dataentry.fields.text.TextViewModel;
import org.junit.Before;
import org.junit.Test;

import static android.text.InputType.TYPE_CLASS_TEXT;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.fail;

public class FieldViewModelFactoryTests {
    private static final String UID = "test_uid";
    private static final String LABEL = "test_label";
    private static final String TEST_OPTION_SET = "test_option_set";

    private FieldViewModelFactory fieldViewModelFactory;

    @Before
    public void setUp() {
        fieldViewModelFactory = new FieldViewModelFactoryImpl(
                "Enter text", "Enter long text", "Enter number", "Enter integer",
                "Enter positive integer", "Enter negative integer", "Enter positive integer or zero",
                "Filter options");
    }

    @Test
    public void textTypeIsMappedToCorrectEditTextViewModel() throws Exception {
        EditTextViewModel viewModel = (EditTextViewModel) fieldViewModelFactory.create(UID,
                LABEL, ValueType.TEXT, true, null, "test_text");

        assertThat(viewModel.uid()).isEqualTo(UID);
        assertThat(viewModel.label()).isEqualTo(LABEL);
        assertThat(viewModel.mandatory()).isEqualTo(true);
        assertThat(viewModel.value()).isEqualTo("test_text");
        assertThat(viewModel.hint()).isEqualTo("Enter text");
        assertThat(viewModel.maxLines()).isEqualTo(1);
        assertThat(viewModel.inputType()).isEqualTo(TYPE_CLASS_TEXT);
    }

    @Test
    public void longTextTypeIsMappedToCorrectEditTextViewModel() throws Exception {
        EditTextViewModel viewModel = (EditTextViewModel) fieldViewModelFactory.create(UID,
                LABEL, ValueType.LONG_TEXT, true, null, "test_text");

        assertThat(viewModel.uid()).isEqualTo(UID);
        assertThat(viewModel.label()).isEqualTo(LABEL);
        assertThat(viewModel.mandatory()).isEqualTo(true);
        assertThat(viewModel.value()).isEqualTo("test_text");
        assertThat(viewModel.hint()).isEqualTo("Enter long text");
        assertThat(viewModel.maxLines()).isEqualTo(3);
        assertThat(viewModel.inputType()).isEqualTo(TYPE_CLASS_TEXT);
    }

    @Test
    public void numberTypeIsMappedToCorrectEditTextViewModel() throws Exception {
        EditTextDoubleViewModel viewModel = (EditTextDoubleViewModel) fieldViewModelFactory.create(UID,
                LABEL, ValueType.NUMBER, true, null, String.valueOf(17.0));

        assertThat(viewModel.uid()).isEqualTo(UID);
        assertThat(viewModel.label()).isEqualTo(LABEL);
        assertThat(viewModel.mandatory()).isEqualTo(true);
        assertThat(viewModel.value()).isEqualTo(17.0);
        assertThat(viewModel.hint()).isEqualTo("Enter number");
        assertThat(viewModel.maxLines()).isEqualTo(1);
        assertThat(viewModel.inputType()).isEqualTo(InputType.TYPE_CLASS_NUMBER
                | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
    }

    @Test
    public void numberTypeMappingShouldNotThrowOnNull() throws Exception {
        try {
            fieldViewModelFactory.create(UID, LABEL, ValueType.NUMBER, true, null, null);
        } catch (Exception exception) {
            fail("Exception should not be thrown", exception);
        }
    }

    @Test
    public void numberTypeMappingShouldNotThrowOnEmptyString() throws Exception {
        try {
            fieldViewModelFactory.create(UID, LABEL, ValueType.NUMBER, true, null, "");
        } catch (Exception exception) {
            fail("Exception should not be thrown", exception);
        }
    }

    @Test
    public void integerTypeIsMappedToCorrectEditTextViewModel() throws Exception {
        EditTextIntegerViewModel viewModel = (EditTextIntegerViewModel) fieldViewModelFactory.create(UID,
                LABEL, ValueType.INTEGER, true, null, String.valueOf(13));

        assertThat(viewModel.uid()).isEqualTo(UID);
        assertThat(viewModel.label()).isEqualTo(LABEL);
        assertThat(viewModel.mandatory()).isEqualTo(true);
        assertThat(viewModel.value()).isEqualTo(13);
        assertThat(viewModel.hint()).isEqualTo("Enter integer");
        assertThat(viewModel.maxLines()).isEqualTo(1);
        assertThat(viewModel.inputType()).isEqualTo(InputType.TYPE_CLASS_NUMBER
                | InputType.TYPE_NUMBER_FLAG_SIGNED);
    }

    @Test
    public void integerTypeMappingShouldNotThrowOnNull() throws Exception {
        try {
            fieldViewModelFactory.create(UID, LABEL, ValueType.INTEGER, true, null, null);
        } catch (Exception exception) {
            fail("Exception should not be thrown", exception);
        }
    }


    @Test
    public void integerTypeMappingShouldNotThrowOnEmptyString() throws Exception {
        try {
            fieldViewModelFactory.create(UID, LABEL, ValueType.INTEGER, true, null, "");
        } catch (Exception exception) {
            fail("Exception should not be thrown", exception);
        }
    }

    @Test
    public void integerNegativeTypeIsMappedToCorrectEditTextViewModel() throws Exception {
        EditTextIntegerViewModel viewModel = (EditTextIntegerViewModel) fieldViewModelFactory.create(UID,
                LABEL, ValueType.INTEGER_NEGATIVE, true, null, String.valueOf(-13));

        assertThat(viewModel.uid()).isEqualTo(UID);
        assertThat(viewModel.label()).isEqualTo(LABEL);
        assertThat(viewModel.mandatory()).isEqualTo(true);
        assertThat(viewModel.value()).isEqualTo(-13);
        assertThat(viewModel.hint()).isEqualTo("Enter negative integer");
        assertThat(viewModel.maxLines()).isEqualTo(1);
        assertThat(viewModel.inputType()).isEqualTo(InputType.TYPE_CLASS_NUMBER
                | InputType.TYPE_NUMBER_FLAG_SIGNED);
    }

    @Test
    public void integerNegativeTypeMappingShouldNotThrowOnNull() throws Exception {
        try {
            fieldViewModelFactory.create(UID, LABEL, ValueType.INTEGER_NEGATIVE, true, null, null);
        } catch (Exception exception) {
            fail("Exception should not be thrown", exception);
        }
    }

    @Test
    public void integerNegativeTypeMappingShouldNotThrowOnEmptyString() throws Exception {
        try {
            fieldViewModelFactory.create(UID, LABEL, ValueType.INTEGER_NEGATIVE, true, null, "");
        } catch (Exception exception) {
            fail("Exception should not be thrown", exception);
        }
    }

    @Test
    public void integerZeroOrPositiveTypeIsMappedToCorrectEditTextViewModel() throws Exception {
        EditTextIntegerViewModel viewModel = (EditTextIntegerViewModel) fieldViewModelFactory.create(UID,
                LABEL, ValueType.INTEGER_ZERO_OR_POSITIVE, true, null, String.valueOf(13));

        assertThat(viewModel.uid()).isEqualTo(UID);
        assertThat(viewModel.label()).isEqualTo(LABEL);
        assertThat(viewModel.mandatory()).isEqualTo(true);
        assertThat(viewModel.value()).isEqualTo(13);
        assertThat(viewModel.hint()).isEqualTo("Enter positive integer or zero");
        assertThat(viewModel.maxLines()).isEqualTo(1);
        assertThat(viewModel.inputType()).isEqualTo(InputType.TYPE_CLASS_NUMBER);
    }

    @Test
    public void integerZeroOrPositiveTypeMappingShouldNotThrowOnNull() throws Exception {
        try {
            fieldViewModelFactory.create(UID, LABEL, ValueType.INTEGER_ZERO_OR_POSITIVE, true, null, null);
        } catch (Exception exception) {
            fail("Exception should not be thrown", exception);
        }
    }

    @Test
    public void integerZeroOrPositiveTypeMappingShouldNotThrowOnEmptyString() throws Exception {
        try {
            fieldViewModelFactory.create(UID, LABEL, ValueType.INTEGER_ZERO_OR_POSITIVE, true, null, "");
        } catch (Exception exception) {
            fail("Exception should not be thrown", exception);
        }
    }

    @Test
    public void integerPositiveTypeIsMappedToCorrectEditTextViewModel() throws Exception {
        EditTextIntegerViewModel viewModel = (EditTextIntegerViewModel) fieldViewModelFactory.create(UID,
                LABEL, ValueType.INTEGER_POSITIVE, true, null, String.valueOf(13));

        assertThat(viewModel.uid()).isEqualTo(UID);
        assertThat(viewModel.label()).isEqualTo(LABEL);
        assertThat(viewModel.mandatory()).isEqualTo(true);
        assertThat(viewModel.value()).isEqualTo(13);
        assertThat(viewModel.hint()).isEqualTo("Enter positive integer");
        assertThat(viewModel.maxLines()).isEqualTo(1);
        assertThat(viewModel.inputType()).isEqualTo(InputType.TYPE_CLASS_NUMBER);
    }

    @Test
    public void integerPositiveMappingShouldNotThrowOnNull() throws Exception {
        try {
            fieldViewModelFactory.create(UID, LABEL, ValueType.INTEGER_POSITIVE, true, null, null);
        } catch (Exception exception) {
            fail("Exception should not be thrown", exception);
        }
    }

    @Test
    public void integerPositiveMappingShouldNotThrowOnEmptyString() throws Exception {
        try {
            fieldViewModelFactory.create(UID, LABEL, ValueType.INTEGER_POSITIVE, true, null, "");
        } catch (Exception exception) {
            fail("Exception should not be thrown", exception);
        }
    }

    @Test
    public void booleanTypeIsMappedToRadioButtonViewModel() throws Exception {
        RadioButtonViewModel viewModel = (RadioButtonViewModel) fieldViewModelFactory.create(UID,
                LABEL, ValueType.BOOLEAN, true, null, String.valueOf(false));

        assertThat(viewModel.uid()).isEqualTo(UID);
        assertThat(viewModel.label()).isEqualTo(LABEL);
        assertThat(viewModel.mandatory()).isEqualTo(true);
        assertThat(viewModel.value()).isEqualTo(RadioButtonViewModel.Value.NO);
    }

    @Test
    public void optionSetTypeIsMappedToOptionsViewModel() {
        OptionsViewModel optionsViewModel = (OptionsViewModel) fieldViewModelFactory.create(UID,
                LABEL, ValueType.TEXT, true, "test_option_set", "test_option_value");

        assertThat(optionsViewModel.uid()).isEqualTo(UID);
        assertThat(optionsViewModel.label()).isEqualTo(LABEL);
        assertThat(optionsViewModel.mandatory()).isTrue();
        assertThat(optionsViewModel.optionSet()).isEqualTo("test_option_set");
        assertThat(optionsViewModel.value()).isEqualTo("test_option_value");
    }

    @Test
    public void trueOnlyTypeIsMappedToCheckBoxViewModel() throws Exception {
        CheckBoxViewModel viewModel = (CheckBoxViewModel) fieldViewModelFactory.create(UID,
                LABEL, ValueType.TRUE_ONLY, true, null, String.valueOf(true));

        assertThat(viewModel.uid()).isEqualTo(UID);
        assertThat(viewModel.label()).isEqualTo(LABEL);
        assertThat(viewModel.mandatory()).isEqualTo(true);
        assertThat(viewModel.value()).isEqualTo(CheckBoxViewModel.Value.CHECKED);
    }

    @Test
    public void shouldThrowOnNullType() {
        try {
            fieldViewModelFactory.create(UID, LABEL, null, false, "test_optionset", "test_value");
            fail("IllegalArgumentException was expected, but nothing was thrown.");
        } catch (IllegalArgumentException illegalArgumentException) {
            // noop
        }
    }

    @Test
    public void shouldReturnTextViewModelOnUnknownType() {
        assertThat(create(ValueType.LETTER)).isInstanceOf(TextViewModel.class);
        assertThat(create(ValueType.DATE)).isInstanceOf(TextViewModel.class);
        assertThat(create(ValueType.DATETIME)).isInstanceOf(TextViewModel.class);
        assertThat(create(ValueType.TIME)).isInstanceOf(TextViewModel.class);
        assertThat(create(ValueType.UNIT_INTERVAL)).isInstanceOf(TextViewModel.class);
        assertThat(create(ValueType.PERCENTAGE)).isInstanceOf(TextViewModel.class);
        assertThat(create(ValueType.FILE_RESOURCE)).isInstanceOf(TextViewModel.class);
        assertThat(create(ValueType.COORDINATE)).isInstanceOf(TextViewModel.class);
        assertThat(create(ValueType.PHONE_NUMBER)).isInstanceOf(TextViewModel.class);
        assertThat(create(ValueType.EMAIL)).isInstanceOf(TextViewModel.class);
        assertThat(create(ValueType.USERNAME)).isInstanceOf(TextViewModel.class);
        assertThat(create(ValueType.ORGANISATION_UNIT)).isInstanceOf(TextViewModel.class);
        assertThat(create(ValueType.TRACKER_ASSOCIATE)).isInstanceOf(TextViewModel.class);
        assertThat(create(ValueType.AGE)).isInstanceOf(TextViewModel.class);
        assertThat(create(ValueType.URL)).isInstanceOf(TextViewModel.class);
    }

    @NonNull
    private FieldViewModel create(@NonNull ValueType valueType) {
        return fieldViewModelFactory.create(UID, LABEL, valueType, false,
                null, "test_value");
    }
}