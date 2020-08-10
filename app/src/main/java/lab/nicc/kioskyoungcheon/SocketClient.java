package lab.nicc.kioskyoungcheon;

import android.util.Log;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Collections;
import java.util.List;

/**
 * Created by user on 2018-10-01.
 */
class SocketClient extends Thread {
    int adInt = 0;
    static int adSuccess = 0;
    static String address;

    SocketClient(int adInt) {
        this.adInt = adInt;

        String ip = getIPAddress();
        address = ip.substring(0, ip.lastIndexOf(".") + 1);
        //address = "10.0.2."; ??? where??
    }

    public static String getIPAddress() {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        boolean isIPv4 = sAddr.indexOf(':') < 0;

                        if (isIPv4)
                            return sAddr;

                    }
                }
            }
        } catch (Exception ex) {
        }
        return "";
    }

    @Override
    public void run() {
        try {
            Socket tmp = new Socket();
            SocketAddress socketAddress = new InetSocketAddress(address + adInt, 12333);
            tmp.connect(socketAddress,3000);
            adSuccess = adInt;
            Log.e("ServerFound", String.valueOf(adSuccess));
            tmp.close();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        } finally {
            interrupt();
        }
    }
}