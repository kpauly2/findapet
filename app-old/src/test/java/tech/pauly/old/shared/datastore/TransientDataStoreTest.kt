package tech.pauly.old.shared.datastore

import io.reactivex.observers.TestObserver
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import kotlin.reflect.KClass

class TransientDataStoreTest {

    private val dataSubjectObserver = TestObserver<KClass<*>>()

    private val subject = TransientDataStore()

    @Before
    fun setup() {
        subject.dataSubject.subscribe(dataSubjectObserver)
    }

    @Test
    fun plusAssign_savesUseCaseAndEmitsSubject() {
        val useCase = TestUseCase()

        subject += useCase

        assertThat(subject.transientData).hasSize(1)
        assertThat(subject.transientData).containsEntry(TestUseCase::class, useCase)
        dataSubjectObserver.assertValue(TestUseCase::class)
    }

    @Test
    fun plusAssign_useCaseExists_overwritesPreviousUseCase() {
        val firstUseCase = TestUseCase()
        val secondUseCase = TestUseCase()

        subject += firstUseCase
        subject += secondUseCase

        val transientData = subject.transientData
        assertThat(transientData).hasSize(1)
        assertThat(transientData[TestUseCase::class]).isEqualToComparingFieldByField(secondUseCase)
    }

    @Test
    fun containsUseCase_useCaseExists_returnsTrue() {
        val useCase = TestUseCase()
        subject += useCase

        assertThat(subject.containsUseCase(TestUseCase::class)).isTrue()
    }

    @Test
    fun containsUseCase_useCaseDoesNotExist_returnsFalse() {
        assertThat(subject.containsUseCase(TestUseCase::class)).isFalse()

        val useCase = TestUseCase()
        subject += useCase

        assertThat(subject.containsUseCase(AnotherTestUseCase::class)).isFalse()
    }

    @Test
    fun get_useCaseDoesNotExist_returnsNull() {
        assertThat(subject[TestUseCase::class]).isEqualTo(null)

        val useCase = TestUseCase()
        subject += useCase

        assertThat(subject[AnotherTestUseCase::class]).isEqualTo(null)
    }

    @Test
    fun get_useCaseExists_getsUseCaseAndRemovesFromMap() {
        val useCase = TestUseCase()
        subject += useCase

        val receivedUseCase = subject[TestUseCase::class]

        assertThat(receivedUseCase).isEqualToComparingFieldByField(useCase)
        assertThat(subject.transientData).hasSize(0)
        assertThat(subject.transientData).doesNotContainEntry(TestUseCase::class, useCase)
    }

    @Test
    fun observeUseCase_useCaseSaved_returnsClassOfUseCaseSaved() {
        val observer = TestObserver<KClass<*>>()
        val useCase = TestUseCase()

        subject.observeUseCase(TestUseCase::class).subscribe(observer)
        subject += useCase

        observer.assertValue(TestUseCase::class)
                .assertNotComplete()
    }

    @Test
    fun useCaseSaved_observeUseCase_returnsClassOfLastUseCaseSaved() {
        val observer = TestObserver<KClass<*>>()
        val useCase = TestUseCase()

        subject += useCase
        subject.observeUseCase(TestUseCase::class).subscribe(observer)

        observer.assertValue(TestUseCase::class)
                .assertNotComplete()
    }

    @Test
    fun observeUseCase_differentUseCaseSaved_fireNoEvent() {
        val observer = TestObserver<KClass<*>>()
        val useCase = TestUseCase()

        subject.observeUseCase(AnotherTestUseCase::class).subscribe(observer)
        subject += useCase

        observer.assertNoValues()
                .assertNotComplete()
    }

    @Test
    fun observeAndGetUseCase_useCaseSaved_returnsUseCaseRemoved() {
        val observer = TestObserver<UseCase>()
        val useCase = TestUseCase()

        subject.observeAndGetUseCase(TestUseCase::class).subscribe(observer)
        subject += useCase

        observer.assertValue(useCase)
                .assertNotComplete()
    }

    @Test
    fun observeAndGetUseCase_differentUseCaseSaved_fireNoEvent() {
        val observer = TestObserver<UseCase>()
        val useCase = TestUseCase()

        subject.observeAndGetUseCase(AnotherTestUseCase::class).subscribe(observer)
        subject += useCase

        observer.assertNoValues()
                .assertNotComplete()
    }

    // Can't use mocks as Mockito mutates the class name
    private inner class TestUseCase : UseCase

    private inner class AnotherTestUseCase : UseCase
}