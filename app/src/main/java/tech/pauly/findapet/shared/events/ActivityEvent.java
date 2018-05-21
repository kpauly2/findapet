package tech.pauly.findapet.shared.events;

public class ActivityEvent extends BaseViewEvent {

    private Class startActivityClass;

    public static ActivityEvent build(Object emitter) {
        return new ActivityEvent(emitter.getClass());
    }

    private ActivityEvent(Class emitter) {
        this.emitter = emitter;
    }

    public ActivityEvent startActivity(Class startActivityClass) {
        this.startActivityClass = startActivityClass;
        return this;
    }

    public Class getStartActivityClass() {
        return startActivityClass;
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

        return startActivityClass != null ? startActivityClass.equals(that.startActivityClass) : that.startActivityClass == null;
    }

    @Override
    public int hashCode() {
        return startActivityClass != null ? startActivityClass.hashCode() : 0;
    }
}
