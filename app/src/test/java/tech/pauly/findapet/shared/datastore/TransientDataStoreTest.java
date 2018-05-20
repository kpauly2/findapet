package tech.pauly.findapet.shared.datastore;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ConcurrentMap;

import io.reactivex.observers.TestObserver;

import static org.assertj.core.api.Assertions.assertThat;

public class TransientDataStoreTest {

    private TransientDataStore subject;

    private TestObserver<Class> dataSubjectObserver = new TestObserver<>();
    @Before
    public void setup() {
        subject = new TransientDataStore();
        subject.getDataSubject().subscribe(dataSubjectObserver);
    }

    @Test
    public void save_savesUseCaseAndEmitsSubject() {
        TestUseCase useCase = new TestUseCase(0);

        subject.save(useCase);

        assertThat(subject.getTransientData()).hasSize(1);
        assertThat(subject.getTransientData()).containsEntry(TestUseCase.class, useCase);
        dataSubjectObserver.assertValue(TestUseCase.class);
    }

    @Test
    public void save_useCaseExists_overwritesPreviousUseCase() {
        TestUseCase firstUseCase = new TestUseCase(1);
        TestUseCase secondUseCase = new TestUseCase(2);

        subject.save(firstUseCase);
        subject.save(secondUseCase);

        ConcurrentMap<Class, UseCase> transientData = subject.getTransientData();
        assertThat(transientData).hasSize(1);
        assertThat(transientData.get(TestUseCase.class)).isEqualToComparingFieldByField(secondUseCase);
    }

    @Test
    public void containsUseCase_useCaseExists_returnsTrue() {
        TestUseCase useCase = new TestUseCase(0);
        subject.save(useCase);

        assertThat(subject.containsUseCase(TestUseCase.class)).isTrue();
    }

    @Test
    public void containsUseCase_useCaseDoesNotExist_returnsFalse() {
        assertThat(subject.containsUseCase(TestUseCase.class)).isFalse();

        TestUseCase useCase = new TestUseCase(0);
        subject.save(useCase);

        assertThat(subject.containsUseCase(AnotherTestUseCase.class)).isFalse();
    }

    @Test
    public void get_useCaseDoesNotExist_returnsNull() {
        assertThat(subject.get(TestUseCase.class)).isEqualTo(null);

        TestUseCase useCase = new TestUseCase(0);
        subject.save(useCase);

        assertThat(subject.get(AnotherTestUseCase.class)).isEqualTo(null);
    }

    @Test
    public void get_useCaseExists_getsUseCaseAndRemovesFromMap() {
        TestUseCase useCase = new TestUseCase(0);
        subject.save(useCase);

        UseCase receivedUseCase = subject.get(TestUseCase.class);

        assertThat(receivedUseCase).isEqualToComparingFieldByField(useCase);
        assertThat(subject.getTransientData()).hasSize(0);
        assertThat(subject.getTransientData()).doesNotContainEntry(TestUseCase.class, useCase);
    }

    @Test
    public void observeUseCase_useCaseSaved_returnsClassOfUseCaseSaved() {
        TestObserver<Class> observer = new TestObserver<>();
        TestUseCase useCase = new TestUseCase(0);

        subject.observeUseCase(TestUseCase.class).subscribe(observer);
        subject.save(useCase);

        observer.assertValue(TestUseCase.class)
                .assertNotComplete();
    }

    @Test
    public void observeUseCase_differentUseCaseSaved_fireNoEvent() {
        TestObserver<Class> observer = new TestObserver<>();
        TestUseCase useCase = new TestUseCase(0);

        subject.observeUseCase(AnotherTestUseCase.class).subscribe(observer);
        subject.save(useCase);

        observer.assertNoValues()
                .assertNotComplete();
    }

    @Test
    public void observeAndGetUseCase_useCaseSaved_returnsUseCaseRemoved() {
        TestObserver<UseCase> observer = new TestObserver<>();
        TestUseCase useCase = new TestUseCase(0);

        subject.observeAndGetUseCase(TestUseCase.class).subscribe(observer);
        subject.save(useCase);

        observer.assertValue(useCase)
                .assertNotComplete();
    }

    @Test
    public void observeAndGetUseCase_differentUseCaseSaved_fireNoEvent() {
        TestObserver<UseCase> observer = new TestObserver<>();
        TestUseCase useCase = new TestUseCase(0);

        subject.observeAndGetUseCase(AnotherTestUseCase.class).subscribe(observer);
        subject.save(useCase);

        observer.assertNoValues()
                .assertNotComplete();
    }

    // Can't use mocks as Mockito mutates the class name
    private class TestUseCase implements UseCase {
        private int value;

        TestUseCase(int value) {
            this.value = value;
        }
    }
    private class AnotherTestUseCase implements UseCase {}
}