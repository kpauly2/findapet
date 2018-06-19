package tech.pauly.findapet.shared.datastore

import io.reactivex.observers.TestObserver
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

class TransientDataStoreTest {

    private val dataSubjectObserver = TestObserver<Class<*>>()

    private val subject = TransientDataStore()

    @Before
    fun setup() {
        subject.dataSubject.subscribe(dataSubjectObserver)
    }

    @Test
    fun save_savesUseCaseAndEmitsSubject() {
        val useCase = TestUseCase()

        subject.save(useCase)

        assertThat(subject.transientData).hasSize(1)
        assertThat(subject.transientData).containsEntry(TestUseCase::class.java, useCase)
        dataSubjectObserver.assertValue(TestUseCase::class.java)
    }

    @Test
    fun save_useCaseExists_overwritesPreviousUseCase() {
        val firstUseCase = TestUseCase()
        val secondUseCase = TestUseCase()

        subject.save(firstUseCase)
        subject.save(secondUseCase)

        val transientData = subject.transientData
        assertThat(transientData).hasSize(1)
        assertThat(transientData[TestUseCase::class.java]).isEqualToComparingFieldByField(secondUseCase)
    }

    @Test
    fun containsUseCase_useCaseExists_returnsTrue() {
        val useCase = TestUseCase()
        subject.save(useCase)

        assertThat(subject.containsUseCase(TestUseCase::class.java)).isTrue()
    }

    @Test
    fun containsUseCase_useCaseDoesNotExist_returnsFalse() {
        assertThat(subject.containsUseCase(TestUseCase::class.java)).isFalse()

        val useCase = TestUseCase()
        subject.save(useCase)

        assertThat(subject.containsUseCase(AnotherTestUseCase::class.java)).isFalse()
    }

    @Test
    fun get_useCaseDoesNotExist_returnsNull() {
        assertThat(subject[TestUseCase::class.java]).isEqualTo(null)

        val useCase = TestUseCase()
        subject.save(useCase)

        assertThat(subject[AnotherTestUseCase::class.java]).isEqualTo(null)
    }

    @Test
    fun get_useCaseExists_getsUseCaseAndRemovesFromMap() {
        val useCase = TestUseCase()
        subject.save(useCase)

        val receivedUseCase = subject[TestUseCase::class.java]

        assertThat(receivedUseCase).isEqualToComparingFieldByField(useCase)
        assertThat(subject.transientData).hasSize(0)
        assertThat(subject.transientData).doesNotContainEntry(TestUseCase::class.java, useCase)
    }

    @Test
    fun observeUseCase_useCaseSaved_returnsClassOfUseCaseSaved() {
        val observer = TestObserver<Class<*>>()
        val useCase = TestUseCase()

        subject.observeUseCase(TestUseCase::class.java).subscribe(observer)
        subject.save(useCase)

        observer.assertValue(TestUseCase::class.java)
                .assertNotComplete()
    }

    @Test
    fun observeUseCase_differentUseCaseSaved_fireNoEvent() {
        val observer = TestObserver<Class<*>>()
        val useCase = TestUseCase()

        subject.observeUseCase(AnotherTestUseCase::class.java).subscribe(observer)
        subject.save(useCase)

        observer.assertNoValues()
                .assertNotComplete()
    }

    @Test
    fun observeAndGetUseCase_useCaseSaved_returnsUseCaseRemoved() {
        val observer = TestObserver<UseCase>()
        val useCase = TestUseCase()

        subject.observeAndGetUseCase(TestUseCase::class.java).subscribe(observer)
        subject.save(useCase)

        observer.assertValue(useCase)
                .assertNotComplete()
    }

    @Test
    fun observeAndGetUseCase_differentUseCaseSaved_fireNoEvent() {
        val observer = TestObserver<UseCase>()
        val useCase = TestUseCase()

        subject.observeAndGetUseCase(AnotherTestUseCase::class.java).subscribe(observer)
        subject.save(useCase)

        observer.assertNoValues()
                .assertNotComplete()
    }

    // Can't use mocks as Mockito mutates the class name
    private inner class TestUseCase

    private inner class AnotherTestUseCase
}