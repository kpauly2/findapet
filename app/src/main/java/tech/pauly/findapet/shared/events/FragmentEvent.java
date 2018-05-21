package tech.pauly.findapet.shared.events;

import android.support.annotation.IdRes;

public class FragmentEvent extends BaseViewEvent {

    private Class fragmentClass;
    @IdRes
    private int containerId;

    public static FragmentEvent build(Object emitter) {
        return new FragmentEvent(emitter.getClass());
    }

    private FragmentEvent(Class emitter) {
        this.emitter = emitter;
    }

    public FragmentEvent fragment(Class fragmentClass) {
        this.fragmentClass = fragmentClass;
        return this;
    }

    public FragmentEvent container(@IdRes int id) {
        this.containerId = id;
        return this;
    }

    public Class getFragmentClass() {
        return fragmentClass;
    }

    public int getContainerId() {
        return containerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        FragmentEvent that = (FragmentEvent) o;

        if (containerId != that.containerId) {
            return false;
        }
        return fragmentClass != null ? fragmentClass.equals(that.fragmentClass) : that.fragmentClass == null;
    }

    @Override
    public int hashCode() {
        int result = fragmentClass != null ? fragmentClass.hashCode() : 0;
        result = 31 * result + containerId;
        return result;
    }
}
