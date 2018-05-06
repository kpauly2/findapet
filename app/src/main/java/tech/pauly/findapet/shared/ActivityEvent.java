package tech.pauly.findapet.shared;

public class ActivityEvent extends BaseViewEvent {

    public enum ActivityEventType {
        START,
        FINISH;
    }

    private Class startActivityClass;
    private ActivityEventType type;

    public static ActivityEvent build(Object emitter) {
        return new ActivityEvent(emitter.getClass());
    }

    public ActivityEvent(Class emitter) {
        this.emitter = emitter;
    }

    public ActivityEvent startActivity(Class startActivityClass) {
        this.startActivityClass = startActivityClass;
        type = ActivityEventType.START;
        return this;
    }

    public ActivityEvent finishActivity() {
        type = ActivityEventType.FINISH;
        return this;
    }

    public Class getStartActivityClass() {
        return startActivityClass;
    }

    public ActivityEventType getType() {
        return type;
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

        if (startActivityClass != null ? !startActivityClass.equals(that.startActivityClass) : that.startActivityClass != null) {
            return false;
        }
        return type == that.type;
    }

    @Override
    public int hashCode() {
        int result = startActivityClass != null ? startActivityClass.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }
}
