package tech.pauly.findapet.shared.events;

import java.util.Objects;

public class ActivityEvent extends BaseViewEvent {

    private Class startActivityClass;
    private boolean finishActivity = false;

    public static ActivityEvent build(Object emitter) {
        return new ActivityEvent(emitter.getClass());
    }

    private ActivityEvent(Class emitter) {
        this.emitter = emitter;
    }

    public ActivityEvent finishActivity() {
        this.finishActivity = true;
        return this;
    }

    public ActivityEvent startActivity(Class startActivityClass) {
        this.startActivityClass = startActivityClass;
        return this;
    }

    public Class getStartActivityClass() {
        return startActivityClass;
    }

    public boolean isFinishActivity() {
        return finishActivity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ActivityEvent that = (ActivityEvent) o;
        return finishActivity == that.finishActivity && Objects.equals(startActivityClass, that.startActivityClass);
    }

    @Override
    public int hashCode() {

        return Objects.hash(startActivityClass, finishActivity);
    }
}
