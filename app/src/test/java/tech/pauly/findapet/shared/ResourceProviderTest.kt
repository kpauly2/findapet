package tech.pauly.findapet.shared

import android.content.Context
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import tech.pauly.findapet.R
import tech.pauly.findapet.data.models.Sex

class ResourceProviderTest {

    @RelaxedMockK
    private lateinit var context: Context

    private lateinit var subject: ResourceProvider

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        subject = ResourceProvider(context)
    }

    @Test
    fun getSexString_noArgs_constructsCorrectString() {
        subject.getSexString(R.string.pet_adopted_dialog_body, Sex.MALE)

        verify {
            context.getString(R.string.pet_adopted_dialog_body)
        }
    }

    @Test
    fun getSexString_oneSentencePlacement_constructsCorrectString() {
        val sex = mockk<Sex> {
            every { getGrammaticalForm(any()) } returns R.string.pronoun_male_subject
        }
        every { context.getString(R.string.pronoun_male_subject) } returns "he"

        subject.getSexString(R.string.pet_adopted_dialog_body, sex, SentencePlacement.SUBJECT)

        verify {
            context.getString(R.string.pet_adopted_dialog_body, "he")
        }
    }

    @Test
    fun getSexString_multipleSentencePlacement_constructsCorrectString() {
        val sex = mockk<Sex> {
            every { getGrammaticalForm(SentencePlacement.SUBJECT) } returns R.string.pronoun_male_subject
            every { getGrammaticalForm(SentencePlacement.OBJECT) } returns R.string.pronoun_male_object
        }
        every { context.getString(R.string.pronoun_male_subject) } returns "he"
        every { context.getString(R.string.pronoun_male_object) } returns "him"

        subject.getSexString(R.string.pet_adopted_dialog_body, sex, SentencePlacement.SUBJECT, SentencePlacement.OBJECT)

        verify {
            context.getString(R.string.pet_adopted_dialog_body, "he", "him")
        }
    }
    @Test
    fun getSexString_multipleSentencePlacementAndOtherArgs_constructsCorrectString() {
        val sex = mockk<Sex> {
            every { getGrammaticalForm(SentencePlacement.SUBJECT) } returns R.string.pronoun_male_subject
            every { getGrammaticalForm(SentencePlacement.OBJECT) } returns R.string.pronoun_male_object
        }
        every { context.getString(R.string.pronoun_male_subject) } returns "he"
        every { context.getString(R.string.pronoun_male_object) } returns "him"

        subject.getSexString(R.string.pet_adopted_dialog_body, sex, SentencePlacement.SUBJECT, SentencePlacement.OBJECT, "other")

        verify {
            context.getString(R.string.pet_adopted_dialog_body, "he", "him", "other")
        }
    }
}