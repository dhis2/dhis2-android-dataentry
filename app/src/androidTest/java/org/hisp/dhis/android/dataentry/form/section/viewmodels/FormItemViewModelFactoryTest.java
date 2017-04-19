package org.hisp.dhis.android.dataentry.form.section.viewmodels;

import android.text.InputType;

import org.hisp.dhis.android.core.common.ValueType;
import org.hisp.dhis.android.dataentry.R;
import org.hisp.dhis.android.dataentry.form.section.EditTextHintCache;
import org.hisp.dhis.android.dataentry.form.section.viewmodels.checkbox.CheckBoxViewModel;
import org.hisp.dhis.android.dataentry.form.section.viewmodels.coordinate.CoordinateViewModel;
import org.hisp.dhis.android.dataentry.form.section.viewmodels.date.DateViewModel;
import org.hisp.dhis.android.dataentry.form.section.viewmodels.edittext.EditTextViewModel;
import org.hisp.dhis.android.dataentry.form.section.viewmodels.optionset.OptionSetViewModel;
import org.hisp.dhis.android.dataentry.form.section.viewmodels.radiobutton.RadioButtonViewModel;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;

import static android.text.InputType.TYPE_CLASS_TEXT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.MockitoAnnotations.initMocks;

public class FormItemViewModelFactoryTest {

    @Mock
    EditTextHintCache editTextHintCache;

    // The class we are testing
    private FormItemViewModelFactory formItemViewModelFactory;

    @Before
    public void setUp() {
        initMocks(this);

        Mockito.when(editTextHintCache.hint(R.string.enter_text)).thenReturn("Enter text");
        Mockito.when(editTextHintCache.hint(R.string.enter_long_text)).thenReturn("Enter long text");
        Mockito.when(editTextHintCache.hint(R.string.enter_number)).thenReturn("Enter number");
        Mockito.when(editTextHintCache.hint(R.string.enter_integer)).thenReturn("Enter integer");
        Mockito.when(editTextHintCache.hint(R.string.enter_negative_integer)).thenReturn("Enter negative integer");
        Mockito.when(editTextHintCache.hint(R.string.enter_positive_integer_or_zero)).thenReturn(
                "Enter positive integer or zero");
        Mockito.when(editTextHintCache.hint(R.string.enter_positive_integer)).thenReturn("Enter positive integer");
        Mockito.when(editTextHintCache.hint(R.string.enter_date)).thenReturn("Enter date");
        Mockito.when(editTextHintCache.hint(R.string.enter_coordinates)).thenReturn("Enter coordinates");
        Mockito.when(editTextHintCache.hint(R.string.enter_option_set)).thenReturn("Select or search from the list");

        formItemViewModelFactory = new FormItemViewModelFactoryImpl(editTextHintCache);
    }

    @Test
    public void textTypeIsMappedToCorrectEditTextViewModel() throws Exception {
        EditTextViewModel viewModel = mapToViewModelWithFactory(ValueType.TEXT);

        Integer expectedInputType = TYPE_CLASS_TEXT;
        Integer expectedMaxLines = 1;
        String expectedHint = "Enter text";

        assertThat(viewModel).isEqualTo(getEditTextViewModel(expectedInputType, expectedMaxLines, expectedHint));
    }

    @Test
    public void longTextTypeIsMappedToCorrectEditTextViewModel() throws Exception {
        EditTextViewModel viewModel = mapToViewModelWithFactory(ValueType.LONG_TEXT);

        Integer expectedInputType = TYPE_CLASS_TEXT;
        Integer expectedMaxLines = 3;
        String expectedHint = "Enter long text";

        assertThat(viewModel).isEqualTo(getEditTextViewModel(expectedInputType, expectedMaxLines, expectedHint));
    }

    @Test
    public void numberTypeIsMappedToCorrectEditTextViewModel() throws Exception {
        EditTextViewModel viewModel = mapToViewModelWithFactory(ValueType.NUMBER);

        Integer expectedInputType = InputType.TYPE_CLASS_NUMBER
                | InputType.TYPE_NUMBER_FLAG_DECIMAL
                | InputType.TYPE_NUMBER_FLAG_SIGNED;
        Integer expectedMaxLines = 1;
        String expectedHint = "Enter number";

        assertThat(viewModel).isEqualTo(getEditTextViewModel(expectedInputType, expectedMaxLines, expectedHint));
    }

    @Test
    public void integerTypeIsMappedToCorrectEditTextViewModel() throws Exception {
        EditTextViewModel viewModel = mapToViewModelWithFactory(ValueType.INTEGER);

        Integer expectedInputType = InputType.TYPE_CLASS_NUMBER |
                InputType.TYPE_NUMBER_FLAG_SIGNED;
        Integer expectedMaxLines = 1;
        String expectedHint = "Enter integer";

        assertThat(viewModel).isEqualTo(getEditTextViewModel(expectedInputType, expectedMaxLines, expectedHint));
    }

    @Test
    public void integerNegativeTypeIsMappedToCorrectEditTextViewModel() throws Exception {
        EditTextViewModel viewModel = mapToViewModelWithFactory(ValueType.INTEGER_NEGATIVE);

        Integer expectedInputType = InputType.TYPE_CLASS_NUMBER |
                InputType.TYPE_NUMBER_FLAG_SIGNED;
        Integer expectedMaxLines = 1;
        String expectedHint = "Enter negative integer";

        assertThat(viewModel).isEqualTo(getEditTextViewModel(expectedInputType, expectedMaxLines, expectedHint));
    }

    @Test
    public void integerZeroOrPositiveTypeIsMappedToCorrectEditTextViewModel() throws Exception {
        EditTextViewModel viewModel = mapToViewModelWithFactory(ValueType.INTEGER_ZERO_OR_POSITIVE);

        Integer expectedInputType = InputType.TYPE_CLASS_NUMBER;
        Integer expectedMaxLines = 1;
        String expectedHint = "Enter positive integer or zero";

        assertThat(viewModel).isEqualTo(getEditTextViewModel(expectedInputType, expectedMaxLines, expectedHint));
    }

    @Test
    public void integerPositiveTypeIsMappedToCorrectEditTextViewModel() throws Exception {
        EditTextViewModel viewModel = mapToViewModelWithFactory(ValueType.INTEGER_POSITIVE);

        Integer expectedInputType = InputType.TYPE_CLASS_NUMBER;
        Integer expectedMaxLines = 1;
        String expectedHint = "Enter positive integer";

        assertThat(viewModel).isEqualTo(getEditTextViewModel(expectedInputType, expectedMaxLines, expectedHint));
    }

    @Test
    public void booleanTypeIsMappedToRadioButtonViewModel() throws Exception {

        // the expected result of the mapping
        RadioButtonViewModel radioButtonViewModel =
                RadioButtonViewModel.create("booleanUid", "booleanLabel", true, true);

        FormItemViewModel viewModel = formItemViewModelFactory.create("booleanUid", "booleanLabel", true, "true",
                ValueType.BOOLEAN, null);

        assertThat(viewModel).isInstanceOf(RadioButtonViewModel.class);
        assertThat(viewModel).isEqualTo(radioButtonViewModel);
    }

    @Test
    public void trueOnlyTypeIsMappedToCheckBoxViewModel() throws Exception {

        // the expected result of the mapping
        CheckBoxViewModel checkBoxViewModel = CheckBoxViewModel.create("trueOnlyUid", "trueOnlyLabel", true, false);

        FormItemViewModel viewModel = formItemViewModelFactory.create("trueOnlyUid", "trueOnlyLabel", true, "false",
                ValueType.TRUE_ONLY, null);

        assertThat(viewModel).isInstanceOf(CheckBoxViewModel.class);
        assertThat(viewModel).isEqualTo(checkBoxViewModel);
    }

    @Test
    public void dateTypeIsMappedToDateViewModel() throws Exception {

        // the expected result of the mapping
        DateViewModel expectedDateViewModel = DateViewModel.create("dateUid", "dateLabel", true, "2016-12-11");

        FormItemViewModel viewModel = formItemViewModelFactory.create("dateUid", "dateLabel", true, "2016-12-11",
                ValueType.DATE, null);

        assertThat(viewModel).isInstanceOf(DateViewModel.class);
        assertThat(viewModel).isEqualTo(expectedDateViewModel);
    }

    @Test
    public void coordinateTypeIsMappedToCoordinateViewModel() throws Exception {

        // the expected result of the mapping
        CoordinateViewModel coordinateViewModel = CoordinateViewModel.create("coordinateUid", "coordinateLabel", true,
                "12.56734", "71.876576");

        FormItemViewModel viewModel = formItemViewModelFactory.create("coordinateUid", "coordinateLabel", true,
                "12.56734,71.876576", ValueType.COORDINATE, null);

        assertThat(viewModel).isInstanceOf(CoordinateViewModel.class);
        assertThat(viewModel).isEqualTo(coordinateViewModel);

    }

    @Test
    public void mapToOptionSetIfOptionSetIsPresent() throws Exception {

        // the expected result of the mapping
        OptionSetViewModel optionSetViewModel = OptionSetViewModel.create("optionSetUid", "optionSetLabel", true,
                "selectedOptionSetCode", "Select or search from the list");

        // BOOLEAN type
        FormItemViewModel viewModel = formItemViewModelFactory.create("optionSetUid", "optionSetLabel", true, "",
                ValueType.BOOLEAN, "selectedOptionSetCode");
        assertThat(viewModel).isInstanceOf(OptionSetViewModel.class);
        assertThat(viewModel).isEqualTo(optionSetViewModel);

        // TEXT type
        viewModel = formItemViewModelFactory.create("optionSetUid", "optionSetLabel", true, "", ValueType.TEXT,
                "selectedOptionSetCode");
        assertThat(viewModel).isInstanceOf(OptionSetViewModel.class);
        assertThat(viewModel).isEqualTo(optionSetViewModel);
    }

    private EditTextViewModel mapToViewModelWithFactory(ValueType valueType) {
        FormItemViewModel viewModel = formItemViewModelFactory.create("textUid", "textLabel", true, "textValue",
                valueType, null);
        assertThat(viewModel).isInstanceOf(EditTextViewModel.class);
        return (EditTextViewModel) viewModel;
    }

    private EditTextViewModel getEditTextViewModel(Integer inputType, Integer maxLines, String hint) {
        return EditTextViewModel.create("textUid", "textLabel", true, "textValue",
                inputType, maxLines, hint, new ArrayList<>());
    }

    @Test
    public void createOptionSetViewModelCorrectly() throws Exception {
        FormItemViewModel viewModel = formItemViewModelFactory.create("optionSetUid", "optionSetLabel", true,
                "optionSetValue", ValueType.TEXT, "optionSetUid");

        assertThat(viewModel).isInstanceOf(OptionSetViewModel.class);
        assertThat(viewModel.uid()).isEqualTo("optionSetUid");
    }
}