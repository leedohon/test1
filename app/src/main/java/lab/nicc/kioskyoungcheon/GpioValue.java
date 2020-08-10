package lab.nicc.kioskyoungcheon;

/**
 * Created by user on 2018-12-04.
 */

public class GpioValue {
    private OnSensorValueChangeListener listener;

    private int value;

    public void onSensorValueChangeListener(OnSensorValueChangeListener listener) {
        this.listener = listener;
    }

    public int get() {
        return value;
    }

    public void set(int value) {
        this.value = value;

        if(listener != null) {
            listener.onSensorValueChanged(value);
        }
    }
}
