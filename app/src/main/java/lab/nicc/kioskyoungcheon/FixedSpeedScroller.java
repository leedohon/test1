package lab.nicc.kioskyoungcheon;

import android.content.Context;
import android.widget.Scroller;

public class FixedSpeedScroller extends Scroller {
    private int mDuration = 5000;

    public FixedSpeedScroller(Context context) {
        super(context);
    }

    public void setmDuration(int mDuration){
        this.mDuration = mDuration;
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        super.startScroll(startX, startY, dx, dy, mDuration);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy) {
        super.startScroll(startX, startY, dx, dy, mDuration);
    }
}