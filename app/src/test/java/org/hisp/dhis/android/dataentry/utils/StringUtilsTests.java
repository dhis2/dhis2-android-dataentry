package org.hisp.dhis.android.dataentry.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(JUnit4.class)
public class StringUtilsTests {

    @Test
    public void stringShouldBeJoinedCorrectly() {
        String expected = "Mike\nIke\nPike\nDike";

        String actual = StringUtils.join(Arrays.asList(
                "Mike", "Ike", "Pike", "Dike"
        ));

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void noNewLineCharactersIfLessThanTwoTokens() {
        String expected = "Mike";

        String actual = StringUtils.join(Arrays.asList(
                "Mike"
        ));

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldReturnEmptyStringIfNoTokens() {
        String expected = "";

        String actual = StringUtils.join(Arrays.asList());

        assertThat(actual).isEqualTo(expected);
    }
}
