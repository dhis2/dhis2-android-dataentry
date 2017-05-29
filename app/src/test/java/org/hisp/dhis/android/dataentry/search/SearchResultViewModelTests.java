package org.hisp.dhis.android.dataentry.search;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(JUnit4.class)
public class SearchResultViewModelTests {

    @Test
    public void equalsAndHashcodeMethodsShouldConformToContract() {
        EqualsVerifier.forClass(SearchResultViewModel.create("test_te_uid", "test_te_name").getClass())
                .suppress(Warning.NULL_FIELDS)
                .verify();
    }

    @Test
    public void propertiesShouldBePropagated() {
        SearchResultViewModel searchResultViewModel = SearchResultViewModel.create("test_te_uid", "test_te_name");
        assertThat(searchResultViewModel.uid()).isEqualTo("test_te_uid");
        assertThat(searchResultViewModel.label()).isEqualTo("test_te_name");
    }
}
