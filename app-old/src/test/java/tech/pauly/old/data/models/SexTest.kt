package tech.pauly.old.data.models

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import tech.pauly.old.R
import tech.pauly.old.shared.SentencePlacement

class SexTest {

    @Test
    fun getGrammaticalForm_maleSubject_returnsCorrectPlacement() {
        assertThat(Sex.MALE.getGrammaticalForm(SentencePlacement.SUBJECT))
                .isEqualTo(R.string.pronoun_male_subject)
    }

    @Test
    fun getGrammaticalForm_maleObject_returnsCorrectPlacement() {
        assertThat(Sex.MALE.getGrammaticalForm(SentencePlacement.OBJECT))
                .isEqualTo(R.string.pronoun_male_object)
    }

    @Test
    fun getGrammaticalForm_femaleSubject_returnsCorrectPlacement() {
        assertThat(Sex.FEMALE.getGrammaticalForm(SentencePlacement.SUBJECT))
                .isEqualTo(R.string.pronoun_female_subject)
    }

    @Test
    fun getGrammaticalForm_femaleObject_returnsCorrectPlacement() {
        assertThat(Sex.FEMALE.getGrammaticalForm(SentencePlacement.OBJECT))
                .isEqualTo(R.string.pronoun_female_object)
    }

    @Test
    fun getGrammaticalForm_unknownSubject_returnsCorrectPlacement() {
        assertThat(Sex.UNKNOWN.getGrammaticalForm(SentencePlacement.SUBJECT))
                .isEqualTo(R.string.pronoun_missing)
    }

    @Test
    fun getGrammaticalForm_unknownObject_returnsCorrectPlacement() {
        assertThat(Sex.UNKNOWN.getGrammaticalForm(SentencePlacement.OBJECT))
                .isEqualTo(R.string.pronoun_missing)
    }

    @Test
    fun getGrammaticalForm_missingSubject_returnsCorrectPlacement() {
        assertThat(Sex.MISSING.getGrammaticalForm(SentencePlacement.SUBJECT))
                .isEqualTo(R.string.pronoun_missing)
    }

    @Test
    fun getGrammaticalForm_missingObject_returnsCorrectPlacement() {
        assertThat(Sex.MISSING.getGrammaticalForm(SentencePlacement.OBJECT))
                .isEqualTo(R.string.pronoun_missing)
    }
}