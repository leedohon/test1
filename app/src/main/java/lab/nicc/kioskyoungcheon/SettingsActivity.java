package lab.nicc.kioskyoungcheon;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.util.Log;
import android.widget.Toast;

import com.android.xhapimanager.XHApiManager;

import com.tsengvn.typekit.TypekitContextWrapper;

import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SettingsActivity extends PreferenceActivity {
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent("lab.nicc");
        intent.putExtra("settings", true);
        sendBroadcast(intent);

        getFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content,
                        new PrefFragment()).commit();
    }

    protected static void setTeamListSelectMultiSelectListPreferenceData(MultiSelectListPreference mp) {
        try {
            MainActivity.jsonParser.loadJsonData();
            MainActivity.jsonParser.parseWholeStaffJson();
            try {
                MainActivity.items = new String[MainActivity.jsonParser.wholeGetTeams[0].length];
                for (int i = 0; i < MainActivity.jsonParser.wholeGetTeams[0].length; i++) {
                    try {
                        MainActivity.items[i] = MainActivity.jsonParser.wholeGetTeams[0][i].getString("section_name").replace("&gt;", ">");
                        MainActivity.fullCodeHash.put(MainActivity.jsonParser.wholeGetTeams[0][i].getString("section_fullcode"), MainActivity.jsonParser.wholeGetTeams[0][i].getString("section_name"));
                        MainActivity.teamNameHash.put(MainActivity.jsonParser.wholeGetTeams[0][i].getString("section_name"), MainActivity.jsonParser.wholeGetTeams[0][i].getString("section_fullcode"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            CharSequence[] entries = MainActivity.items;
            mp.setEntries(entries);
            mp.setDefaultValue(entries[0]);
            mp.setEntryValues(entries);
        } catch (Exception e){}

    }
    protected static void setTeamSelectListPreferenceData(ListPreference lp) {
        try {
            MainActivity.jsonParser.loadJsonData();
            MainActivity.jsonParser.parseWholeStaffJson();
            try {
                MainActivity.items = new String[MainActivity.jsonParser.wholeGetTeams[0].length];
                for (int i = 0; i < MainActivity.jsonParser.wholeGetTeams[0].length; i++) {
                    try {
                        MainActivity.items[i] = MainActivity.jsonParser.wholeGetTeams[0][i].getString("section_name").replace("&gt;", ">");
                        MainActivity.fullCodeHash.put(MainActivity.jsonParser.wholeGetTeams[0][i].getString("section_fullcode"), MainActivity.jsonParser.wholeGetTeams[0][i].getString("section_name"));
                        MainActivity.teamNameHash.put(MainActivity.jsonParser.wholeGetTeams[0][i].getString("section_name"), MainActivity.jsonParser.wholeGetTeams[0][i].getString("section_fullcode"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            CharSequence[] entries = MainActivity.items;
            lp.setEntries(entries);
            lp.setDefaultValue(entries[0]);
            lp.setEntryValues(entries);
        } catch (Exception e) {}
    }

    protected static void setBuildingSelectListPreferenceData(MultiSelectListPreference mp) {
        try {
            CharSequence[] entries = MainActivity.buildings;
            mp.setEntries(entries);
            mp.setDefaultValue(entries);
            mp.setEntryValues(entries);
        } catch (Exception e) {}
    }

    protected static void setShowPublicData(MultiSelectListPreference mp) {
        List<String> arrayList = new ArrayList<>();

        arrayList.add("갤러리");
        arrayList.add("공지사항");
        arrayList.add("홍보영상");

        CharSequence[] entries = arrayList.toArray(new String[]{});
        mp.setEntries(entries);
        mp.setDefaultValue(entries);
        mp.setEntryValues(entries);
    }

    protected static void setOrgLayoutSelectListPreferenceData(MultiSelectListPreference mp) {
        try {
            MainActivity.jsonParser.loadJsonData();
            MainActivity.jsonParser.parseWholeStaffJson();
            try {
                MainActivity.items = new String[MainActivity.jsonParser.wholeGetTeams[0].length];
                for (int i = 0; i < MainActivity.jsonParser.wholeGetTeams[0].length; i++) {
                    try {
                        MainActivity.items[i] = MainActivity.jsonParser.wholeGetTeams[0][i].getString("section_name").replace("&gt;", ">");
                        MainActivity.fullCodeHash.put(MainActivity.jsonParser.wholeGetTeams[0][i].getString("section_fullcode"), MainActivity.jsonParser.wholeGetTeams[0][i].getString("section_name"));
                        MainActivity.teamNameHash.put(MainActivity.jsonParser.wholeGetTeams[0][i].getString("section_name"), MainActivity.jsonParser.wholeGetTeams[0][i].getString("section_fullcode"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            CharSequence[] entries = MainActivity.items;
            mp.setEntries(entries);
            mp.setDefaultValue(entries);
            mp.setEntryValues(entries);

        } catch (Exception e) {}
    }

    protected static void setAppListPreferenceData(ListPreference lp) {
        try {
            List<ApplicationInfo> apps = MainActivity.context.getPackageManager().getInstalledApplications(0);

            List<CharSequence> entries = new ArrayList<>();
            List<CharSequence> entryValues = new ArrayList<>();
            int i = 0;
            for (ApplicationInfo app : apps) {
                if ((app.flags & (ApplicationInfo.FLAG_UPDATED_SYSTEM_APP | ApplicationInfo.FLAG_SYSTEM)) == 0) {
                    entries.add(app.processName);
                    entryValues.add(Integer.toString(i++));
                }
            }
            CharSequence[] arrayEntries = new String[entries.size()];
            CharSequence[] arrayEntryValues = new String[entryValues.size()];
            for (i = 0; i < entries.size(); i++) {
                arrayEntries[i] = entries.get(i);
                arrayEntryValues[i] = entries.get(i);
            }
            lp.setEntries(arrayEntries);
            lp.setDefaultValue("1");
            lp.setEntryValues(arrayEntryValues);
        } catch (Exception e) {}
    }

    /*protected static void setOrgLayoutMode(View view) {
        LayoutInflater li = LayoutInflater.from(view.getContext());
        View promptsView = li.inflate(R.layout.org_layout_select, null);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getContext());
        alertDialogBuilder.setView(promptsView);

        final RadioButton s1 = (RadioButton) promptsView.findViewById(R.id.select1);
        final RadioButton s2 = (RadioButton) promptsView.findViewById(R.id.select2);

        switch (orgLayoutMode) {
            case 1:
                s1.setChecked(true);
                s2.setChecked(false);
                break;
            case 2:
                s1.setChecked(false);
                s2.setChecked(true);
                break;
        }

        s1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    s2.setChecked(false);
                    orgLayoutMode = 1;
                    PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).edit().putInt("orgLayoutMode", MainActivity.orgLayoutMode).commit();
                }
            }
        });

        s2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    s1.setChecked(false);
                    orgLayoutMode = 2;
                    PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).edit().putInt("orgLayoutMode", MainActivity.orgLayoutMode).commit();
                }
            }
        });

        alertDialogBuilder
                .setTitle("레이아웃 선택")
                .setCancelable(true)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        }
                );
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }*/

    public static class PrefFragment extends PreferenceFragment{
        //private Timer timer;

        @Override
        public void onCreate(final Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref);



            final ListPreference teamSelectListPreference = (ListPreference) findPreference("team_select");
            setTeamSelectListPreferenceData(teamSelectListPreference);

            teamSelectListPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    setTeamSelectListPreferenceData(teamSelectListPreference);
                    return false;
                }
            });

            final MultiSelectListPreference teamListSelectMultiSelectListPreference = (MultiSelectListPreference) findPreference("team_list_select");
            setTeamListSelectMultiSelectListPreferenceData(teamListSelectMultiSelectListPreference);

            teamListSelectMultiSelectListPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    setTeamListSelectMultiSelectListPreferenceData(teamListSelectMultiSelectListPreference);
                    return false;
                }
            });


            final MultiSelectListPreference buildingSelectPreference = (MultiSelectListPreference) findPreference("building_select");
            setBuildingSelectListPreferenceData(buildingSelectPreference);

            buildingSelectPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    setBuildingSelectListPreferenceData(buildingSelectPreference);
                    return false;
                }
            });

            final MultiSelectListPreference orgLayoutSelectPreference = (MultiSelectListPreference) findPreference("org_layout_select");
            setOrgLayoutSelectListPreferenceData(orgLayoutSelectPreference);

            orgLayoutSelectPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    //setOrgLayoutMode(getView());
                    setOrgLayoutSelectListPreferenceData(orgLayoutSelectPreference);
                    return false;
                }
            });

            final MultiSelectListPreference showPublicDataSelectPreference = (MultiSelectListPreference) findPreference("show_public_data");
            setShowPublicData(showPublicDataSelectPreference);
            showPublicDataSelectPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    setShowPublicData(showPublicDataSelectPreference);
                    return false;
                }
            });

            Preference offManager = (Preference) findPreference("offManager");
            offManager.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    exit = true;
                    ActivityManager am = (ActivityManager) MainActivity.context.getApplicationContext().getSystemService(Activity.ACTIVITY_SERVICE);
                    am.killBackgroundProcesses("lab.nicc.kioskmanager");

                    getActivity().finish();
                    Log.d("killProc","killProc");
                    getActivity().finishAffinity();
                    System.runFinalization();
                    System.exit(0);
                    Log.d("onDestroy","Fin");

                    //android.os.Process.killProcess(android.os.Process.myPid());

                    return false;
                }
            });

            Preference systemSettings = (Preference) findPreference("systemSettings");
            systemSettings.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                    return false;
                }
            });

            /*final ListPreference appListPreference = (ListPreference) findPreference("appList");
            setAppListPreferenceData(appListPreference);

            appListPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    setAppListPreferenceData(appListPreference);
                    return false;
                }
            });

            appListPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    ActivityManager am = (ActivityManager) context.getApplicationContext().getSystemService(Activity.ACTIVITY_SERVICE);
                    am.killBackgroundProcesses("lab.nicc.kioskmanager");

                    int index = appListPreference.findIndexOfValue(newValue.toString());
                    Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(appListPreference.getEntries()[index].toString());
                    if (launchIntent != null) {
                        startActivity(launchIntent);
                    }
                    return false;
                }
            });*/

            Preference back = (Preference) findPreference("back");
            back.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    exit = false;
                    getActivity().finish();
                    return false;
                }
            });

            EditTextPreference managerServerAddressPreference = (EditTextPreference) findPreference("server_address");
            managerServerAddressPreference.setSummary(managerServerAddressPreference.getText());
            managerServerAddressPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    MainActivity.context.sendBroadcast(new Intent("managerSettings").putExtra("serverAddress", (String) newValue));
                    preference.setSummary((String) newValue);
                    return true;
                }
            });

            /*EditTextPreference managerImgServerAddressPreference = (EditTextPreference) findPreference("img_server_address");
            managerImgServerAddressPreference.setSummary(managerImgServerAddressPreference.getText());
            managerImgServerAddressPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    MainActivity.context.sendBroadcast(new Intent("managerSettings").putExtra("imgServerAddress", (String) newValue));
                    preference.setSummary((String) newValue);
                    return true;
                }
            });*/

            Preference managerManuallyUpdate = (Preference) findPreference("manually_update");
            managerManuallyUpdate.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    MainActivity.context.sendBroadcast(new Intent("managerSettings").putExtra("manuallyUpdate", true));
                    return false;
                }
            });

            final SwitchPreference managerControlMode = (SwitchPreference) findPreference("control_mode");

            /*managerControlMode.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    XHApiManager xhApiManager = new XHApiManager();
                    if(preference.isChecked()){
                        Log.d("newValue", "true");
                        xhApiManager.XHShowOrHideStatusBar(false);}
                    else if(newValue.equals(false)){
                        Log.d("newValue", "false");
                        xhApiManager.XHShowOrHideStatusBar(true);}
                    return false;
                }
            });*/
            managerControlMode.setOnPreferenceClickListener(new SwitchPreference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if (managerControlMode.isChecked()==true){
                        managerControlMode.setChecked(false);

                    } else if (managerControlMode.isChecked()==false){
                        managerControlMode.setChecked(true);
                    }
                    return false;
                }
            });
            managerControlMode.setOnPreferenceChangeListener(new SwitchPreference.OnPreferenceChangeListener(){
                @Override

                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    try{
                        final XHApiManager xhApiManager = new XHApiManager();
                        XHApiManagerSingleTon singleTon = XHApiManagerSingleTon.getInstance();
                        if (managerControlMode.isChecked()==true){
                            xhApiManager.XHShowOrHideStatusBar(true);
                            singleTon.setBoolean(true);

                        } else if(managerControlMode.isChecked()==false){
                            xhApiManager.XHShowOrHideStatusBar(false);
                            singleTon.setBoolean(false);
                            final Timer timer = new Timer();
                            final ExecutorService excServ = Executors.newSingleThreadExecutor();
                            final Runnable Update = new Runnable() {
                                public void run(){
                                    XHApiManagerSingleTon singleTon = XHApiManagerSingleTon.getInstance();
                                    if (singleTon.getBoolean() == false) {
                                        xhApiManager.XHShowOrHideStatusBar(false);
                                    }
                                    else if(singleTon.getBoolean() == true){
                                        timer.cancel();
                                    }
                                }
                            };
                            timer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    excServ.execute(Update);
                                }
                            }, 0, 100);
                        }} catch(Exception e){
                        Log.d("cannot find Xhapi", "xhapi");
                    }
                    return false;
                }
            });

            IntEditTextPreference autoHome = (IntEditTextPreference) findPreference("auto_home");
            autoHome.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    try {
                        if (Integer.parseInt((String) newValue) < 30 || Integer.parseInt((String) newValue) > 300) {
                            Toast.makeText(MainActivity.context, getResources().getString(R.string.auto_home_integer_message), Toast.LENGTH_SHORT).show();
                            return false;
                        } else {
                            return true;
                        }
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.context, getResources().getString(R.string.auto_home_integer_message), Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }
            });

            try {
                PackageInfo pInfo = MainActivity.context.getPackageManager().getPackageInfo(MainActivity.context.getPackageName(), 0);
                Preference viewerVersion = findPreference("viewer_version");
                Preference managerVersion = findPreference("manager_version");
                viewerVersion.setTitle("Viewer 버전 : " + pInfo.versionName);

                pInfo = MainActivity.context.getPackageManager().getPackageInfo("lab.nicc.kioskmanager", 0);
                managerVersion.setTitle("Manager 버전 : " + pInfo.versionName);

                viewerVersion.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        new Thread(){
                            public void run(){
                                String DownloadUrl = "http://socupdate.nicc.kr/update/yeongcheon/Kiosk.apk";
                                String fileName = "kioskClient.apk";
                                String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/downloadedKiosk";
                                try {
                                    InputStream inputStream = new URL(DownloadUrl).openStream();
                                    File file = new File(dir);
                                    file.mkdirs();
                                    file = new File(dir, fileName);
                                    FileOutputStream out = new FileOutputStream(file);
                                    writeFile(inputStream, out);
                                    out.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();
                        return false;
                    }
                });

                managerVersion.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        new Thread(){
                            public void run(){
                                String DownloadUrl = "http://socupdate.nicc.kr/update/yeongcheon/Manager.apk";
                                String fileName = "kioskManager.apk";
                                String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/downloadedKiosk";
                                try {
                                    InputStream inputStream = new URL(DownloadUrl).openStream();
                                    File file = new File(dir);
                                    file.mkdirs();
                                    file = new File(dir, fileName);
                                    FileOutputStream out = new FileOutputStream(file);
                                    writeFile(inputStream, out);
                                    out.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();
                        return false;
                    }
                });


            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        void writeFile(InputStream is, OutputStream os) throws IOException{
            byte[] buf = new byte[1024];
            double len = 0;
            while((len = is.read(buf)) > 0)
                os.write(buf, 0, (int) len);
            os.flush();
        }
    }

    static boolean exit = false;
    @Override
    protected void onDestroy() {
        super.onDestroy();

        Intent intent = new Intent("lab.nicc");
        intent.putExtra("settings", false);
        sendBroadcast(intent);

        getApplicationContext().sendBroadcast(new Intent("settingsEnd").putExtra("exit", exit));
    }
}
