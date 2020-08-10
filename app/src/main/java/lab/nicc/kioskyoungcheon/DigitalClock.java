package lab.nicc.kioskyoungcheon;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.AttributeSet;

import java.util.Calendar;
import java.util.StringTokenizer;

public class DigitalClock extends android.support.v7.widget.AppCompatTextView {
    int[] startTime = {7, 33, 30};
    int[] endTime = {8, 0, 20};
    boolean lunchSet = false;

    Calendar mCalendar;
    private final static String m12 = "yyyy.MM.dd (E)  h:mm aa";
    //private final static String m12 = "h:mm:ss aa";
    private final static String m24 = "k:mm:ss";
    private FormatChangeObserver mFormatChangeObserver;

    private Runnable mTicker;
    private Handler mHandler;

    private boolean mTickerStopped = false;

    String mFormat;

    public DigitalClock(Context context) {
        super(context);
        initClock(context);
    }

    public DigitalClock(Context context, AttributeSet attrs) {
        super(context, attrs);
        initClock(context);
    }

    private void initClock(Context context) {
        Resources r = context.getResources();

        if (mCalendar == null) {
            mCalendar = Calendar.getInstance();
        }

        mFormatChangeObserver = new FormatChangeObserver();
        getContext().getContentResolver().registerContentObserver(
                Settings.System.CONTENT_URI, true, mFormatChangeObserver);

        setFormat();
    }

    @Override
    protected void onAttachedToWindow() {
        mTickerStopped = false;
        super.onAttachedToWindow();
        mHandler = new Handler();

        /**
         * requests a tick on the next hard-second boundary
         */
        mTicker = new Runnable() {
            public void run() {
                String dateString = DateFormat.format(m24, mCalendar).toString();


                String useDateString = DateFormat.format(mFormat, mCalendar).toString();
                useDateString = useDateString.replace("Mon", "월");
                useDateString = useDateString.replace("Tue", "화");
                useDateString = useDateString.replace("Wed", "수");
                useDateString = useDateString.replace("Thu", "목");
                useDateString = useDateString.replace("Fri", "금");
                useDateString = useDateString.replace("Sat", "토");
                useDateString = useDateString.replace("Sun", "일");

                if (mTickerStopped) return;
                mCalendar.setTimeInMillis(System.currentTimeMillis());
                setText(useDateString);
                invalidate();
                long now = SystemClock.uptimeMillis();
                long next = now + (1000 - now % 1000);
                mHandler.postAtTime(mTicker, next);

                StringTokenizer stringTokenizer = new StringTokenizer(dateString, ":");
                int startTimes = (MainActivity.staticLunchTime[0] * 60 * 60) + (MainActivity.staticLunchTime[1] * 60) + (MainActivity.staticLunchTime[2]);
                int endTimes = (MainActivity.staticLunchTime[3] * 60 * 60) + (MainActivity.staticLunchTime[4] * 60) + (MainActivity.staticLunchTime[5]);
                int tokenTimes = (Integer.parseInt(stringTokenizer.nextToken()) * 60 * 60) + (Integer.parseInt(stringTokenizer.nextToken()) * 60) + (Integer.parseInt(stringTokenizer.nextToken()));
                if (!lunchSet) {
                    if (startTimes <= tokenTimes && endTimes >= tokenTimes) {
                        MainActivity.context.sendBroadcast(new Intent("lunchReceiver").putExtra("Lunch", true));
                        lunchSet = true;
                    }
                } else if(lunchSet) {
                    if (endTimes < tokenTimes || startTimes > tokenTimes) {
                        MainActivity.context.sendBroadcast(new Intent("lunchReceiver").putExtra("Lunch", false));
                        lunchSet = false;
                    }
                } else
                    lunchSet = false;
            }
        };
        mTicker.run();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mTickerStopped = true;
    }

    /**
     * Pulls 12/24 mode from system settings
     */
    private boolean get24HourMode() {
        return android.text.format.DateFormat.is24HourFormat(getContext());
    }

    private void setFormat() {
        if (get24HourMode()) {
            mFormat = m24;
        } else {
            mFormat = m12;
        }
    }

    private class FormatChangeObserver extends ContentObserver {
        public FormatChangeObserver() {
            super(new Handler());
        }

        @Override
        public void onChange(boolean selfChange) {
            setFormat();
        }
    }
}