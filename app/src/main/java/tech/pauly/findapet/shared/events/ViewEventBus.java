package tech.pauly.findapet.shared.events;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import tech.pauly.findapet.shared.BaseFragment;

/**
 * A Singleton class to manage events that the Presentation layer needs the View layer to handle.
 *
 * This is workaround to get strong MVVM where the Presentation objects (View Models) don't have
 * references to their View counterparts (Activities/Fragments). This is made difficult by the
 * platform sometimes necessitating the View objects to do more than they should in traditional
 * MVVM.
 *
 * Using this pattern, the ViewModels can create Event objects with all of the business logic inside
 * and simply enter the Event into the bus. Then the corresponding View object can observe the bus
 * and respond to that object, usually just sending the Event to a function in the BaseActivity/BaseFragment
 * (see {@link BaseFragment#activityEvent(ActivityEvent)}) which then performs standard logic on the
 * event. Note one field must exist in all events: the emitter class. This is to provide the functionality
 * for the View to act only on certain objects' events, or act differently on different objects' events.
 */
@Singleton
public class ViewEventBus {

    public PublishSubject<BaseViewEvent> subject = PublishSubject.create();

    @Inject
    public ViewEventBus() {}

    public void send(BaseViewEvent event) {
        subject.onNext(event);
    }

    public Observable<ActivityEvent> activity(Class emitterClass) {
        return subject.filter(event -> event instanceof ActivityEvent
                                       && fromEmitter(event, emitterClass))
                      .map(event -> (ActivityEvent) event);
    }

    public Observable<FragmentEvent> fragment(Class emitterClass) {
        return subject.filter(event -> event instanceof FragmentEvent
                                       && fromEmitter(event, emitterClass))
                      .map(event -> (FragmentEvent) event);
    }

    public Observable<PermissionEvent> permission(Class emitterClass) {
        return subject.filter(event -> event instanceof PermissionEvent
                                       && fromEmitter(event, emitterClass))
                      .map(event -> (PermissionEvent) event);
    }

    private boolean fromEmitter(BaseViewEvent event, Class viewModelClass) {
        return viewModelClass.getName().equals((event).getEmitter().getName());
    }
}
