package org.hisp.dhis.android.dataentry.utils;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(AndroidJUnit4.class)
public class StringUtilsIntegrationTests {

    @Test
    public void stringShouldBeHtmlifiedCorrectly() {
        String expected = "Mike\nIke\nPike\nDike";

        String actual = StringUtils.htmlify(Arrays.asList(
                "Mike", "Ike", "Pike", "Dike"
        )).toString();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void noNewLineCharactersIfLessThanTwoTokens() {
        String expected = "Mike";

        String actual = StringUtils.htmlify(Arrays.asList(
                "Mike"
        )).toString();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldReturnEmptyStringIfNoTokens() {
        String expected = "";

        String actual = StringUtils.htmlify(Arrays.asList()).toString();

        assertThat(actual).isEqualTo(expected);
    }
}
