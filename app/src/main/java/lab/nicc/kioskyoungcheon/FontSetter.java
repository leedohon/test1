package lab.nicc.kioskyoungcheon;

import android.app.Application;

import com.tsengvn.typekit.Typekit;

/**
 * Created by MinHyeong on 2017-12-13.
 */

public class FontSetter extends Application {
    public void onCreate(){
        super.onCreate();

        Typekit.getInstance()
                .addNormal(Typekit.createFromAsset(this, "NanumBarunGothic.otf"))
                .addBold(Typekit.createFromAsset(this, "NanumBarunGothicBold.otf"));

        /*
        final XHApiManager gpioManager = new XHApiManager();
        gpioManager.XHServiceInit();
        gpioManager.XHSetGpioValue(0, 0);

        MainActivity.gpioValue = new GpioValue();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    MainActivity.gpioValue.set(gpioManager.XHReadGpioValue(0));
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();*/
    }
}
