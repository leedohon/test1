package lab.nicc.kioskyoungcheon;


import android.animation.Animator;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.suke.widget.SwitchButton;
import com.tsengvn.typekit.TypekitContextWrapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class MainActivity extends AppCompatActivity {

    static String teamCode;//현재 설정된 부서 코드
    static String prevCode;//기존에 설정된 부서 코드 여부 판별(Shared Preferences)
    int[] flipCounter = {0};
    private static Timer timer;//조직도 메뉴 자동 스크롤을 위한 타이머
    private static Timer wholeTimer;//일정 시간(2분) 이후 설정한 메뉴(현 조직도)로 자동 이동
    private static Timer popupTimer;//일정 시간(10초) 후 팝업 자동 닫힘 타이머
    private static Timer noticeTimer;//일정시간(3초) 마다 공지사항 제목 하단 바에 바꿔서 출력
    private static int rnd = 0;
    private static boolean isSeatPopup = false;
    private static boolean isPlayVideo = false;
    static boolean popupCut = false;
    public static Typeface typefaceBarunGothicBold;
    public static Typeface typefaceBarunGothic;
    public static Typeface typefacenanummjexbold;
    public static Typeface typefaceNANUMMYEONGJOBOLD;
    public static Typeface typefacenanumsqareeb;
    public static Typeface typefacenanumeqareb;
    private String lunchContents;
    final long DELAY_MS = 5000 * 2; // Auto paging start time
    final long PERIOD_MS = 10000; // Auto paging period time
    final long NOTICE_PERIOD_MS = 3000; // Bottom notice period time
    static long AUTO_ORGANIZATION_PERIOD_MS = 1000 * 60 * 2;
    static final long AUTO_POPUP_CLOSE_MS = 10000;
    static final int PROGRESS_BAR_MS = 600;
    int pagerCounter = 0;
    static JsonParser jsonParser; //json정보를 파싱해 관련 정보를 저장하고 있는 객체.(JsonParser 클래스 참조)
    static ImageView[] galleryViews; //갤러리 이미지 로딩 속도 향상을 위해 객체를 미리 저장해두는 배열
    protected static int[] staticLunchTime = {0, 0, 0, 0, 0, 0, 1};
    private boolean lunchBoolean;
    private String currentScene = "";
    private int activityStartFlag = 0;
    int aboutIndex = -1;
    private String serverAddr = null;

    GestureDetector mGestureDetector;
    static HashMap<String, String> fullCodeHash = new HashMap<>();//팀 코드를 key, 해당 팀(부서) 이름을 value로 가지는 해시맵
    static HashMap<String, String> teamNameHash = new HashMap<>();//부서이름 key, 팀 코드 value
    public static String[] items, buildings;//전체 부서명 배열,
    public static HashMap<String, Boolean> buildingChecked; // 청사 표출 선택지에서 표출하기로 선택된 건물 이름과 boolean 값을 가지는 해시.
    public static HashMap<String, Boolean> orgChecked; // 부서마다 시장 레이아웃/ 일반 레이아웃 선택 기능, 부서 코드와 boolean 값을 가지는 해시.
    private String[] selectSetString;

    static Context context;//앱 컨텍스트 로드 후 수시 사용

    SharedPreferences pref;
    static boolean wholeMode; // 조직도 모드인지 판별하는 변수
    boolean foodMode;
    CustomViewMainVideo customViewPopupVideo;
    static boolean orgLayoutMode = false;

    BroadcastReceiver buildingButtonReceiver = new BroadcastReceiver() { // 직원 검색에서 해당 직원의 근무부서 위치 표출하는 버튼을 누르면 동작.
        @Override
        public void onReceive(final Context context, Intent intent) {
            noRoom = 0;
            try {
                final LinearLayout buildingFrame = (LinearLayout) findViewById(R.id.search_building_layout);
                buildingFrame.removeAllViews(); // 레이아웃의 모든 뷰 제거(초기화)

                for (int i = 0; i < jsonParser.getBuildingNumViewer(); i++) // 빌딩의 개수만큼
                    for (int j = 0; j < jsonParser.getBuildingFloorCountViewer(i); j++) // 빌딩의 층수만큼
                        buildingFrame.addView(buildingImageAdder(i, j, intent.getStringExtra("teamCode"))); // 팀코드를 더해서 LinearLayout에 View 삽입.
                // 오버로드, 탭, 인덱스, 팀코드를 통해 buildingpics 정보를 json으로 받아옴.
                autoOrganizationController(); // 일정 시간 지나면 자동으로 특정 메뉴로 넘어가는 기능.
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    };
    BroadcastReceiver lunchReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (intent.getBooleanExtra("Lunch", false)) {
                    lunchBoolean = true;
                    Log.d("lunchBroadcast", "true");
                    findViewById(R.id.lunch_bg).setVisibility(View.VISIBLE);
                    TextView lunchContent = (TextView) findViewById(R.id.lunch_text);
                    TextView lunchTime = (TextView) findViewById(R.id.lunch_time);
                    TextView textView3 = (TextView) findViewById(R.id.lunch_texts);
                    TextView dutyTime = (TextView) findViewById(R.id.duty_time);
                    TextView textView5 = (TextView) findViewById(R.id.duty_texts);
                    if (typefaceBarunGothicBold == null) {
                        typefaceBarunGothicBold = Typeface.createFromAsset(getAssets(), "NanumBarunGothicBold.otf");
                    }
                    lunchContent.setTypeface(typefaceBarunGothicBold);
                    lunchTime.setTypeface(typefaceBarunGothicBold);
                    dutyTime.setTypeface(typefaceBarunGothicBold);
                    textView3.setTypeface(typefaceBarunGothicBold);
                    textView5.setTypeface(typefaceBarunGothicBold);

                    if (!lunchContents.equals(""))
                        lunchContent.setText(lunchContents);
                    else
                        lunchContent.setText("지금은 점심시간 입니다.");

                    lunchTime.setText(String.format("%02d", staticLunchTime[0]) + ":" + String.format("%02d", staticLunchTime[1])
                            + "~" + String.format("%02d", staticLunchTime[3]) + ":" + String.format("%02d", staticLunchTime[4]));
                } else {
                    Log.d("lunchBroadcast", "false");
                    findViewById(R.id.lunch_bg).setVisibility(View.GONE);
                    lunchBoolean = false;
                }
            } catch (NullPointerException e) {
                //다른 페이지 참조 시 lunch_bg를 참조할 수 없음. 하지만, 이 부분은 무시해도 무관함.
            }
        }
    };
    BroadcastReceiver buildingReceiver = new BroadcastReceiver() { // 위 리시버와 연계되어 사용하는 리시버.
        @Override
        public void onReceive(final Context context, final Intent intent) {
            try {
                if (intent.getBooleanExtra("none", false)) {// BooleanExtra의 none이 false일 시,(근무지 WebView가 없을 시)
                    noRoom = 0;
                    Toast toast = Toast.makeText(context, fullCodeHash.get(intent.getStringExtra("teamCode")) + " 근무자입니다", Toast.LENGTH_LONG);
                    ViewGroup group = (ViewGroup) toast.getView();
                    TextView messageTextView = (TextView) group.getChildAt(0);
                    messageTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 35);
                    toast.show();
                    return;
                }

                gotInterface = true; // 인터페이스가 활성화 되어있는가(자바 스크립트 기반)

                final LinearLayout buildingFrame = (LinearLayout) findViewById(R.id.search_building_layout); // buildingFrame 인터페이스를 링킹

                String a = intent.getStringExtra("fileName");
                final WebView currentBuilding = buildingImageAdder(intent.getStringExtra("teamCode"),                           // 현재 인텐트된 프래그먼트에서 StringExtre의 값들을 받아와
                        intent.getStringExtra("fileName").substring(0, intent.getStringExtra("fileName").length() - 4));//  각 teamCode, fileName의 마지막 4글자를 입력으로 사용하여 사용.

                buildingFrame.removeAllViews(); // buildingFrame의 모든 뷰를 제거
                buildingFrame.addView(currentBuilding); // 위에서 선언한 WebView인 currentBuilding 뷰를 더함.

                final LinearLayout buttonFrame = (LinearLayout) findViewById(R.id.building_button_layout);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        buildingFrame.setVisibility(View.VISIBLE);
                        buttonFrame.setVisibility(View.VISIBLE);

                        findViewById(R.id.popup_org_button).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    teamCode = intent.getStringExtra("teamCode");
                                    autoFlipped = true; // boolean, 데이터 모름.
                                    gotInterface = false; // 인터페이스 활성화 false
                                    startOrganization(); // 조직도 설정, 전체 조직도 모드 여부가 중요함.
                                } catch (OutOfMemoryError e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                buildingFrame.removeAllViews();
                                buildingFrame.setVisibility(GONE);//빌딩 프레임 레이아웃도 숨김
                                buttonFrame.setVisibility(GONE);//버튼 프레임 레이아웃도 숨김
                                gotInterface = false;

                                System.gc();
                                autoOrganizationController();
                            }
                        });
                    }
                }, 300);

                buildingFrame.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        buildingFrame.removeAllViews();
                        buildingFrame.setVisibility(GONE);
                        buttonFrame.setVisibility(GONE);
                        gotInterface = false;

                        autoOrganizationController();
                    }
                });
            } catch (NullPointerException e) {
            }
        }
    };

    BroadcastReceiver orgButtonReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) { // 직원검색에서 해당 직원의 근무부서 조직도 표출하는 버튼을 누르면 동작.
            try {
                teamCode = intent.getStringExtra("teamCode"); // teamCode를 받아와서
                autoFlipped = true; // ?? 설명 없음.
                releasePlayer();
                startActivity(new Intent(MainActivity.this, MainActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("scene", "organization")
                        .putExtra("flop", autoFlipped) // flop extra에 true.
                        .putExtra("teamCode", teamCode)); // teamCode에 팀코드 삽입.
                finish();
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }
        }

    };

    BroadcastReceiver orgRefreshReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            boolean screenSaver = pref.getBoolean("screen_saver", false);
            if (!isSeatPopup && !wholeMode && currentScene.equals("organization")) { // 조직도 모드가 아닐 시 190419
                Log.d("doUpdae", "updae");
                releasePlayer();
                startActivity(new Intent(MainActivity.this, MainActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("scene", "organization")); // 현재 인텐트된 메인 액티비티의 플래그를 FLAG_ACTIVITY_NEW_TASK로,
                // 또한, scene에 organization을 삽입.
                finish();
            } else if (foodMode || (screenSaver && currentScene.equals("main") && wholeMode)) {
                Log.d("doUpdae", "updae");
                releasePlayer();
                startActivity(new Intent(MainActivity.this, MainActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("scene", "main")); // 현재 인텐트된 메인 액티비티의 플래그를 FLAG_ACTIVITY_NEW_TASK로,
                finish();
            }
        }
    };

    BroadcastReceiver getServerAddr = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            serverAddr = intent.getStringExtra("serverAddr");
        }
    };

    BroadcastReceiver mainRefreshReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (intent.getBooleanExtra("exit", false)) { // exit가 비어있으면 false를 받겠음. exit가 true일 시,
                    sendBroadcast(new Intent("lab.nicc").putExtra("exit", true)); // broadcast로 인해 exit, true를 전송 후 종료
                    finish();
                } else {
                    sendBroadcast(new Intent("lab.nicc").putExtra("settings", false)); // broadcast로 인해 settings, false를 전송
                }
                SharedPreferences pm = PreferenceManager.getDefaultSharedPreferences(getBaseContext()); // sharedPreferences 를 이용해 현재 디폴트 쉐어드 프리페어런스를 가져옴.

                foodMode = pm.getBoolean("food_mode", false);
                Log.d("foodMode?", String.valueOf(foodMode));
                sendBroadcast(new Intent("getFoodMode").putExtra("foodMode", foodMode));
                wholeMode = pm.getBoolean("whole_mode", false); // wholemode의 값을 pm으로부터 받아와서 설정. 디폴트는 true.


                if (!wholeMode)//조직도 모드가 아닐 시
                    teamCode = teamNameHash.get(pm.getString("team_select", jsonParser.wholeGetTeams[0][1].getString("section_fullcode")));//
                else//조직도 모드일 시
                    teamCode = "";

                SharedPreferences pref = getSharedPreferences("kiosk_data", MODE_WORLD_READABLE); //kiosk_data를 SharedPreferences에 삽입.(설정 가져오기)
                SharedPreferences.Editor editor = pref.edit(); // edit 메소드의 결과를 editor에 삽입.
                editor.putString("team_code", teamCode); // 팀 코드를 삽입한다.(팀 코드에 맞는 것들을 불러와저장하는 식인듯. 아마 메인 전체 리프래쉬 부분이 이 브로드 캐스트)
                context.sendBroadcast(new Intent("sendWholeMode")
                        .putExtra("wholeMode", wholeMode));

                context.sendBroadcast(new Intent("sendTeamCode")
                        .putExtra("teamCodes", teamCode)
                        .putExtra("wholeMode", wholeMode));
                try {
                    Log.d("teamCode", teamCode);
                } catch (Exception e) {
                }
                try {
                    String setStringToCode[] = new String[selectSetString.length];
                    for (int i = 0; i < selectSetString.length; i++) {
                        setStringToCode[i] = teamNameHash.get(selectSetString[i]);
                        Log.d("teamCodeLink", "suc");
                    }
                    context.sendBroadcast(new Intent("sendTeamCodeLink")
                            .putExtra("teamCodes1", setStringToCode)
                            .putExtra("wholeMode", wholeMode));
                    Log.d("teamCodeLink", "suc1");
                } catch (NullPointerException e) {
                    context.sendBroadcast(new Intent("sendTeamCodeLink")
                            .putExtra("teamCodes1", "")
                            .putExtra("wholeMode", wholeMode));
                    Log.d("teamCodeLink", "suc2");
                    e.printStackTrace();
                }

                Set<String> selections;
                try {
                    selections = pm.getStringSet("building_select", null); // building_select의 값을 불러옴.(없으면 null로 초기화)
                } catch (ClassCastException e) {
                    e.printStackTrace();
                    selections = null;
                }//190417

                if (selections == null) { // null이면
                    selections = new TreeSet<>(); // treeset생성
                    for (int i = 0; i < buildings.length; i++)
                        selections.add(Integer.toString(i));//buildings의 갯수만큼 selections에 더함.
                }

                String[] selected = selections.toArray(new String[]{}); // selected에 빌딩의 갯수만큼 더해진 값을 넣음.

                for (int i = 0; i < buildings.length; i++) {
                    editor.putBoolean(jsonParser.getBuildingName(i) + "building", false).commit(); // 에디터(SharedPreferences에 있는 Editor항목에) 건물이름 삽입.(모든 건물 수 만큼 반복)
                    buildingChecked.put(jsonParser.getBuildingName(i) + "building", false); // buildingChecked에 건물이름 삽입(상동)
                    Log.d("checkedBuilding", "putFalse" + jsonParser.getBuildingName(i));
                }

                for (int i = 0; i < selected.length; i++) { //빌딩의 갯수만큼(왜 for문이 3개나 되는가...)
                    try {
                        editor.putBoolean(selected[i] + "building", true); //
                        for (int j = 0; j < jsonParser.getBuildingNum(); j++) {
                            if (jsonParser.getBuildingName(j).equals(selected[i])) {
                                buildingChecked.put(selected[i], true);
                                editor.putBoolean(selected[i] + "building", true).commit();
                                Log.d("checkedBuilding", "putTrue" + selected[i]);
                            }
                        }
                    } catch (IndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                }

                Set<String> orgSelections;
                try {
                    orgSelections = pm.getStringSet("org_layout_select", null);
                } catch (ClassCastException e) {
                    orgSelections = null;
                }

                if (orgSelections == null) {
                    orgSelections = new TreeSet<>();
                    for (int i = 0; i < items.length; i++)
                        orgSelections.add(Integer.toString(i));
                }

                String[] orgSelected = orgSelections.toArray(new String[]{});

                for (int i = 0; i < items.length; i++) {
                    editor.putBoolean(items[i], false);
                    orgChecked.put(teamNameHash.get(items[i]), false);
                }

                for (int i = 0; i < orgSelected.length; i++) {
                    editor.putBoolean(orgSelected[i], true);
                    orgChecked.put(teamNameHash.get(orgSelected[i]), true);
                }

                editor.commit();
                startMain();
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    };

    BroadcastReceiver keyboardReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String nameString = intent.getStringExtra("strings");
            if (intent.getIntExtra("close", 0) == -1) {
                findViewById(R.id.keyboard_wrapper).setVisibility(GONE);
                searchLoadingText.setVisibility(INVISIBLE);
                Fragment prevListFrag = getSupportFragmentManager().findFragmentByTag("searchListFrag");
                if (prevListFrag != null)
                    getSupportFragmentManager().beginTransaction().remove(prevListFrag);

                removeSearchTextFocus();
                return;
            }

            searchText.setText(nameString);
            searchText.setSelection(nameString.length());

            autoOrganizationController();
        }
    };

    BroadcastReceiver firstBootReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            showAdminModeDialog();
            Toast.makeText(context, "최초 실행 시 설정이 필요합니다.", Toast.LENGTH_LONG).show();
        }
    };

    BroadcastReceiver keyboardEnterReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            KoreanKeyboard.enterText.setVisibility(GONE);
            KoreanKeyboard.searchPb.setVisibility(View.VISIBLE);
            if (SearchListFragment.bgWork != null)
                SearchListFragment.bgWork.cancel(true);
            SearchListFragment.bgWork = (SearchListFragment.BackgroundWork) new SearchListFragment.BackgroundWork(context, intent).execute();
            Thread searchThread = new Thread() {
                public void run() {
                    try {
                        jsonParser.parseWholeStaffJson();
                        //jsonParser.getSectionNameHash();

                        String nameString = String.valueOf(searchText.getText());
                        Intent searchIntent = new Intent("searchInput");
                        searchIntent.putExtra("strings", nameString);

                        sendBroadcast(searchIntent);
                    } catch (InflateException e) {
                    }
                }
            };

            searchThread.start();

            autoOrganizationController();
        }
    };


    void removeSearchTextFocus() {
        try {
            searchText.setText("");
            searchText.setFocusable(false);
            searchText.setFocusable(true);
            searchText.setFocusableInTouchMode(true); // 사용자의 터치에 의하여 포커스를 가질 수 있는가..?
            gotInterface = false;
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() { // 액티비티 제거될 시 모든 리시버 회수.
        super.onDestroy();
        /*ActivityManager am = (ActivityManager) context.getApplicationContext().getSystemService(Activity.ACTIVITY_SERVICE);
        am.killBackgroundProcesses("lab.nicc.kioskmanager");*/

        unregisterReceiver(firstBootReceiver);
        unregisterReceiver(buildingButtonReceiver);
        unregisterReceiver(orgButtonReceiver);
        unregisterReceiver(orgRefreshReceiver);
        unregisterReceiver(mainRefreshReceiver);
        unregisterReceiver(keyboardReceiver);
        unregisterReceiver(keyboardEnterReceiver);
        unregisterReceiver(buildingReceiver);
        unregisterReceiver(lunchReceiver);
    }

    static int launched = 0;
    static int gpioLaunched = 0;
    static GpioValue gpioValue;
    //private static final XHApiManager xhApiManager = new XHApiManager();    /// 앱 시작 시 데이터 로드
    /// 인터넷 연결 불가 시 로컬 데이터 사용

    /*SharedPreferences PM =  getSharedPreferences("lab.nicc.kioskuiseong_preferences",MODE_PRIVATE);
    boolean managerControlMode = PM.getBoolean("control_mode", true);
    XHApiManagerSingleTon singleTon = XHApiManagerSingleTon.getInstance();
        if (managerControlMode==true){
        xhApiManager.XHShowOrHideStatusBar(true);
        singleTon.setBoolean(true);

    } else if(managerControlMode==false&&timer == null){
        xhApiManager.XHShowOrHideStatusBar(false);
        singleTon.setBoolean(false);
        final Timer timer = new Timer();
        final ExecutorService excServ = Executors.newFixedThreadPool(1);
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
    }*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerReceiver(firstBootReceiver, new IntentFilter("firstBootReceiver"));
        registerReceiver(getServerAddr, new IntentFilter("getServerAddr"));
        registerReceiver(buildingButtonReceiver, new IntentFilter("buildingClicked"));
        registerReceiver(orgButtonReceiver, new IntentFilter("orgClicked"));
        registerReceiver(orgRefreshReceiver, new IntentFilter("updated"));
        registerReceiver(mainRefreshReceiver, new IntentFilter("settingsEnd"));
        registerReceiver(keyboardReceiver, new IntentFilter("keyboardInput"));
        registerReceiver(keyboardEnterReceiver, new IntentFilter("keyboardEnterInput"));
        registerReceiver(buildingReceiver, new IntentFilter("buildingLocation"));
        registerReceiver(lunchReceiver, new IntentFilter("lunchReceiver"));
        // 리시버 등록 및 사용. (IntentFilter에 액션 내용들 삽입.)

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                //sendBroadcast(new Intent("KIOSKIsExcepted"));
                //e.printStackTrace();
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd_kk-mm-ss");
                Log.d("TAG", "uncaughtException: exceexcepted");
                String string = sdf.format(cal.getTime()) + "_ErrorLog" + ".txt";
                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/KIOSKData/ErrorLog/", string);
                try {
                    FileWriter fileWriter = new FileWriter(file);
                    ByteArrayOutputStream bout = new ByteArrayOutputStream();
                    e.printStackTrace(new PrintStream(bout));
                    bout.flush();
                    String error = new String(bout.toByteArray());
                    fileWriter.write(error);
                    fileWriter.flush();
                    Log.d("TAG", "uncaughtException: write file");
                } catch (IOException e1) {
                    e1.printStackTrace();
                    file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/KIOSKData/ErrorLog/", string);
                    String error = "can't write";
                    FileWriter fileWriter = null;
                    try {
                        fileWriter = new FileWriter(file);
                        fileWriter.write(error);
                        fileWriter.flush();
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }
                }
                Log.d("TAG", "uncaughtException: exceexcepted");
                PendingIntent mainActivity = PendingIntent.getActivity(getApplicationContext(), 192837, new Intent(getApplicationContext(), MainActivity.class), PendingIntent.FLAG_ONE_SHOT);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 1000, mainActivity);
                System.exit(2);
            }
        });

        Intent intent = new Intent("lab.nicc");
        intent.putExtra("settings", true);
        sendBroadcast(intent);

        jsonParser = new JsonParser(teamCode, getApplicationContext());

        SwipeDetector swipeDetector = new SwipeDetector();
        mGestureDetector = new GestureDetector(this, swipeDetector);

        jsonParser.loadJsonData();
        jsonParser.parseWholeStaffJson();
        try {
            teamCode = jsonParser.wholeGetTeams[0][0].getString("section_fullcode");
            pref = getSharedPreferences("kiosk_data", MODE_PRIVATE);
            teamCode = pref.getString("team_code", "none");
        } catch (JSONException | NullPointerException e) {
            e.printStackTrace();
        }
        jsonParser.parseStaffJson(teamCode);
//        Log.d("teamcode", teamCode);
        jsonParser.parseGalleryJson();
        jsonParser.parseNoticeJson();
        jsonParser.parseBuildingJson();
        jsonParser.parseTourJson();
        jsonParser.parseFoodMenuJson();
        jsonParser.parseFoodMSGJson();
        jsonParser.parseFoodOriginJson();
        jsonParser.parseVideoJson();
        jsonParser.parseBgJson();
        jsonParser.parseAboutJson();
        try {
            jsonParser.parseAlertJson();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        try {
            galleryViews = new ImageView[jsonParser.getGalleryCount()];
        } catch (NullPointerException e) {
        }

        pref = getSharedPreferences("kiosk_data", MODE_WORLD_READABLE);
        prevCode = pref.getString("team_code", "");

        orgLayoutMode = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("orgLayoutMode", false); // 부서별 대표자 레이아웃 설정. 초기는 비설정 상태

        try {
            items = new String[jsonParser.wholeGetTeams[0].length];
            for (int i = 0; i < jsonParser.wholeGetTeams[0].length; i++) {
                try {
                    items[i] = jsonParser.wholeGetTeams[0][i].getString("section_name").replace("&gt;", ">");
                    fullCodeHash.put(jsonParser.wholeGetTeams[0][i].getString("section_fullcode"), jsonParser.wholeGetTeams[0][i].getString("section_name"));
                    teamNameHash.put(jsonParser.wholeGetTeams[0][i].getString("section_name"), jsonParser.wholeGetTeams[0][i].getString("section_fullcode"));//키로 찾기, 벨류로 찾기 뭉치기 가능
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        buildings = new String[jsonParser.getBuildingNum()];
        buildingChecked = new HashMap<>();

        for (int i = jsonParser.getBuildingNum() - 1; i >= 0; i--) {
            buildings[i] = jsonParser.getBuildingName(i);
            buildingChecked.put(buildings[i] + "building", pref.getBoolean(buildings[i] + "building", false));
        }

        Set<String> orgSelections;
        try {
            orgSelections = PreferenceManager.getDefaultSharedPreferences(this).getStringSet("org_layout_select", null);
        } catch (ClassCastException e) {
            e.printStackTrace();
            orgSelections = null;
        }//190417

        try {
            if (orgSelections == null) {
                orgSelections = new TreeSet<>();
                for (int i = 0; i < items.length; i++)
                    orgSelections.add(Integer.toString(i));
            }

            String[] orgSelected = orgSelections.toArray(new String[]{});

            orgChecked = new HashMap<>();

            for (int i = 0; i < orgSelected.length; i++) {
                orgChecked.put(teamNameHash.get(orgSelected[i]), true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        context = this;

        wholeMode = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getBoolean("whole_mode", true);
        //wholeMode = false;
        // 조직도 모드 셋팅 불러오기.
        teamCode = pref.getString("team_code", getResources().getString(R.string.시장)); // 군수?코드를 불러온다?
        if (timer != null)
            timer.cancel();


        /*if (launched == 0)//static int launched = 0으로 초기화.
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        for (int i = 1; i <= 255; i++) {
                            Thread socketClient = new SocketClient(i);
                            socketClient.start();//SocketClient 실행. 0~255까지 반복시행.
                        }

                        launched++;
                        if (SocketClient.adSuccess != 0)//성공 시 스레드 break
                            break;
                        try {//실패시 10초 대기 후 재연결 시도.
                            Thread.sleep(1000 * 10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();*/
        //소켓 클라이언트

        sensorChanged = getIntent().getBooleanExtra("sensor", false); // boolean sensorChanged 로 초기화 되어있음.
        String sensor = getIntent().getStringExtra("scene");
        if (sensor == null && !sensorChanged)
            sensor = "main";

        SharedPreferences pm = PreferenceManager.getDefaultSharedPreferences(getBaseContext()); // sharedPreferences 를 이용해 현재 디폴트 쉐어드 프리페어런스를 가져옴.

        foodMode = pm.getBoolean("food_mode", false);
        Log.d("foodMode??", String.valueOf(foodMode));
        if (foodMode == false) {
            try {
                popupCut = false;
                switch (sensor) { // secne에 따라 switch문으로 인해 선택시행.
                    case "main":
                        startMain(); // 메인 실행
                        break;
                    case "organization":
                        autoFlipped = getIntent().getBooleanExtra("flop", false);
                        if (autoFlipped)
                            teamCode = getIntent().getStringExtra("teamCode");
                        startOrganization(); // 조직도 flop, 팀코드 초기화 / 조직도임.
                        break;
                    case "building":
                        startBuilding(); // 건물위치?
                        break;
                    case "video":
                        startMovie();
                        break;
                    case "gallery":
                        startGallery(); // 갤러리
                        break;
                    case "about":
                        startAbout(); // 어바웃
                        break;
                    case "notice": // 공지
                        startNotice(getIntent().getIntExtra("index", -1));
                        break;
                    case "nosensor":
                        break;
                    default:
                        startMain();
                }
                currentScene = sensor;//currentScene을 현제 씬으로 가져옴.
                Log.d("currentSceneChk2", "currentScene is " + currentScene);
            } catch (NullPointerException e) {
                Log.d("bottomController", "startMain");
                e.printStackTrace();
                //startMain();//예외사항 발생 시 startMain() 호출.
            }
        } else {
            startFood();
            currentScene = "food";//currentScene을 현제 씬으로 가져옴.
            Log.d("currentSceneChk2", "currentScene is " + currentScene);
        }

        try {
            /*gpioValue.onSensorValueChangeListener(new OnSensorValueChangeListener() {
                @Override
                public void onSensorValueChanged(int newVal) {
                    if (prevVal != newVal) { // static int prevVal = 0으로 초기화.
                        prevVal = newVal;
                        if (newVal != 0) {//scene is main
                            if (gpioLaunched++ != 0 && !currentScene.equals("gallery")) {//gpioLaunched가 0이 아니면서 newVal이 0이 아니면.
                                startActivity(new Intent(MainActivity.this, MainActivity.class)
                                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("scene", "gallery").putExtra("sensor", true));
                                finish();
                            }
                        } else {
                            if (gpioLaunched++ != 0 && !currentScene.equals("main")) { // gpioLaunched가 0이 아니면
                                startActivity(new Intent(MainActivity.this, MainActivity.class)
                                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("scene", "main").putExtra("sensor", true));
                                finish();
                            }
                        }
                        //결론적으로 gpioLaunched는 +1됨.
                    }
                }
            });*/
        } catch (Exception e) {
        }
    }

    static int prevVal = 0;
    boolean sensorChanged;

    private void sceneSetter(String scene) {
        this.currentScene = scene;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }

    public void updateUI() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

    }

    public void showAdminModeDialog() {
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.admin_layout, null);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView.findViewById(R.id.admin_pw);

        // set dialog message
        alertDialogBuilder
                .setTitle("관리자 모드")
                .setCancelable(false)
                .setNegativeButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String user_text = (userInput.getText()).toString();
                                if (user_text.equals(getResources().getString(R.string.admin_mode_pw))) {
                                    if (wholeTimer != null) {
                                        wholeTimer.cancel();
                                    }
                                    Intent intent = new Intent("lab.nicc");
                                    intent.putExtra("settings", true);
                                    sendBroadcast(intent); // 브로드캐스트 기반 데이터 전송 사용했음. putExtra를 send.(settings, true)

                                    Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
                                    releasePlayer();
                                    startActivity(settingsIntent); // settingsIntent 객체를 만들어 MainActivity위에 SettingsActivity를 띄움.(진짜 띄우나??;;)
                                } else {
                                    String message = "암호가 올바르지 않습니다.";
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setTitle("Error");
                                    builder.setMessage(message);
                                    builder.setPositiveButton("Cancel", null);
                                    builder.setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {
                                            showAdminModeDialog();
                                        }
                                    });
                                    builder.create().show();
                                }
                            }
                        })
                .setPositiveButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }

                        }

                );

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

    }

    public void showSeatSettingDialog() {
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.admin_layout, null);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView.findViewById(R.id.admin_pw);

        // set dialog message
        alertDialogBuilder
                .setTitle("좌석배치도 편집")
                .setCancelable(false)
                .setNegativeButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String user_text = (userInput.getText()).toString();

                                if (user_text.equals(getResources().getString(R.string.admin_mode_pw))) {
                                    Log.d("teamcodeplan", "= " + teamCode);
                                    releasePlayer();
                                    startActivity(new Intent(MainActivity.this, SeatPlannerActivity.class).putExtra("teamCode", teamCode));//SeatPlannerActivity는 내부 조정 등을 할 수 있음.

                                    // !!SeatPlannerActivity!!
                                } else {
                                    String message = "암호가 올바르지 않습니다.";
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setTitle("Error");
                                    builder.setMessage(message);
                                    builder.setPositiveButton("Cancel", null);
                                    builder.setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {
                                            showSeatSettingDialog();
                                        }
                                    });
                                    builder.create().show();
                                }
                            }
                        })
                .setPositiveButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }

                        }

                );

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

    }

    public void startFood() {
        setContentView(R.layout.new_activity_food);
        try {
            startAlert();

            jsonParser.loadJsonData();
            jsonParser.parseFoodMenuJson();
            jsonParser.parseFoodMSGJson();
            jsonParser.parseFoodOriginJson();

            LayoutInflater vi = LayoutInflater.from(context);
            View group = vi.inflate(R.layout.org_tmp, null);

            Calendar now = Calendar.getInstance();
            int temp = 0;
            int counter = 0;
            boolean firstFood = true;
            int[] layoutId = {R.id.food_packer1, R.id.food_packer2, R.id.food_packer3, R.id.food_packer4, R.id.food_packer5};
            TextView foodBottomText = (TextView) findViewById(R.id.food_bottom_text);
            TextView foodOriginText = (TextView) findViewById(R.id.food_origin_text);

            if (typefaceBarunGothic == null) {
                typefaceBarunGothic = Typeface.createFromAsset(getAssets(), "NanumBarunGothic.otf");
            }
            foodBottomText.setTypeface(typefaceBarunGothic);
            foodOriginText.setTypeface(typefaceBarunGothic);

            foodBottomText.setText(jsonParser.getFoodMSGContext(0));
            foodOriginText.setText(jsonParser.getFoodOriginContext(0));
            Log.d("lineCount", String.valueOf(foodOriginText.length()));
            if (foodOriginText.length() >= 60) {
                foodOriginText.setTextSize(29f);
            }
            foodBottomText.setSelected(true);
            int nowDayOfWeek;

            while (counter != 5) {
                nowDayOfWeek = now.get(Calendar.DAY_OF_WEEK);
                if (nowDayOfWeek == 1 || nowDayOfWeek == 7) {
                    continue;
                } else if (firstFood) {
                    temp = nowDayOfWeek;
                    now.add(Calendar.DAY_OF_MONTH, 2 - nowDayOfWeek);
                }
                nowDayOfWeek = now.get(Calendar.DAY_OF_WEEK);
                //setFoodPacker(layoutId[nowDayOfWeek-2], vi, (now.get(Calendar.MONTH) + 1) + "월" + now.get(Calendar.DAY_OF_MONTH) + "일",jsonParser.getFoodMenu(now), nowDayOfWeek, firstFood);
                setFoodPacker(layoutId[nowDayOfWeek - 2], vi, (now.get(Calendar.MONTH) + 1) + "월" + now.get(Calendar.DAY_OF_MONTH) + "일", jsonParser.getFoodMenu(now), nowDayOfWeek,
                        temp == now.get(Calendar.DAY_OF_WEEK));
                firstFood = false;
                Log.d("dateCheck", (now.get(Calendar.MONTH) + 1) + "월" + now.get(Calendar.DAY_OF_MONTH) + "일" + nowDayOfWeek);
                now.add(Calendar.DAY_OF_MONTH, 1);
                counter++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            FrameLayout foodMenuButton = (FrameLayout) findViewById(R.id.food_menu);
            foodMenuButton.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    showAdminModeDialog();
                    return false;
                }
            });
        }
    }

    public void startAlert() {
        try {
            TextView alertText = (TextView) findViewById(R.id.alert_popup);
            int tempIndex = 0;
            int[] tempDate = {0, 0, 0, 0, 0, 0};
            int tempTime = 0;
            int currentTime = 0;
            Calendar cal = Calendar.getInstance();
            int nowYear = cal.get(cal.YEAR);
            int nowMonth = cal.get(cal.MONTH) + 1;
            int nowDate = cal.get(cal.DATE);
            boolean visibleAlert = false;
            alertText.setVisibility(GONE);
            for (int i = 0; i < jsonParser.getAlertCount(); i++) {
                if (!jsonParser.getAlertVisible(i).equals("TRUE")) {
                    continue;
                }

                int[] currentDate = jsonParser.getAlertDate(i);
                String[] liveTime = jsonParser.getAlertLiveTime(i);

                try {
                    SimpleDateFormat sdt = new SimpleDateFormat("yyyy-MM-dd");
                    Date startLiveTime = sdt.parse(liveTime[0] + "-" + liveTime[1] + "-" + liveTime[2]);
                    Date endLiveTime = sdt.parse(liveTime[3] + "-" + liveTime[4] + "-" + liveTime[5]);
                    Date nowTime = cal.getTime();

                    Log.d("startLiveTime", String.valueOf(startLiveTime.getTime()));
                    Log.d("endLiveTime", String.valueOf(endLiveTime.getTime()));
                    Log.d("nowLiveTime", String.valueOf(nowTime.getTime()));

                    if (!(startLiveTime.getTime() < nowTime.getTime() && nowTime.getTime() < endLiveTime.getTime())) {
                        continue;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.d("getAlertType", String.valueOf(jsonParser.getAlertType(i)));
                Log.d("getAlertType", String.valueOf(jsonParser.getAlertType(tempIndex)));

                if (jsonParser.getAlertCategory(i).equals("launch")) {
                    staticLunchTime = jsonParser.getAlertLunchTime(i);
                    lunchContents = jsonParser.getAlertContent(i);
                    Log.d("LunchChk", "lunch is true" + staticLunchTime[0]);
                    continue;
                }

                tempTime = (tempDate[3] * 60 * 60) + (tempDate[4] * 60) + (tempDate[5]);
                currentTime = (currentDate[3] * 60 * 60) + (currentDate[4] * 60) + (currentDate[5]);
                if (jsonParser.getAlertType(i) > jsonParser.getAlertType(tempIndex) && i != 0) {
                    tempIndex = i;
                    tempDate = currentDate;
                    Log.d("tempcurrent", "TempTime : " + tempTime + " CurrentTime : alert" + currentTime);
                } else if (tempDate[0] < currentDate[0]) {
                    tempIndex = i;
                    tempDate = currentDate;
                    Log.d("tempcurrent", "TempTime : " + tempTime + " CurrentTime : " + currentTime);
                } else if (tempDate[0] == currentDate[0]) {
                    if (tempDate[1] < currentDate[1]) {
                        tempIndex = i;
                        tempDate = currentDate;
                        Log.d("tempcurrent", "TempTime : " + tempTime + " CurrentTime : " + currentTime);
                    } else if (tempDate[1] == currentDate[1]) {
                        if (tempDate[2] < currentDate[2]) {
                            tempIndex = i;
                            tempDate = currentDate;
                            Log.d("tempcurrent", "TempTime : " + tempTime + " CurrentTime : " + currentTime);
                        } else if (tempDate[2] == currentDate[2]) {
                            if (tempTime < currentTime) {
                                tempIndex = i;
                                tempDate = currentDate;
                                Log.d("tempcurrent", "TempTime : " + tempTime + " CurrentTime : " + currentTime);
                            } else
                                continue;
                        }
                    }
                }
                visibleAlert = true;
            }
            if (jsonParser.getAlertVisible(tempIndex).equals("TRUE") && !jsonParser.getAlertCategory(tempIndex).equals("launch") && visibleAlert) {
                Log.d("AlertTag", "AlertVisible");
                alertText.setVisibility(VISIBLE);
            } else {
                Log.d("AlertTag", "AlertGone");
                alertText.setVisibility(GONE);
            }
            alertText.setText(jsonParser.getAlertContent(tempIndex));
            Log.d("alertData", jsonParser.getAlertContent(tempIndex) + " " + tempIndex);
            switch (jsonParser.getAlertType(tempIndex)) {
                case 1:
                    Log.d("AlertTag", "AlertWarning");
                    alertText.setBackgroundResource(R.drawable.alert_yellow);
                    alertText.setTextColor(0xFF333333);
                    break;
                case 2:
                    Log.d("AlertTag", "AlertDanger");
                    alertText.setBackgroundResource(R.drawable.alert_red);
                    alertText.setTextColor(0xFFEEEEEE);
                    break;
                case 0:
                    Log.d("AlertTag", "AlertDefault");
                    alertText.setBackgroundResource(R.drawable.alert_blue);
                    alertText.setTextColor(0xFFEEEEEE);
                    break;

            }
        } catch (Exception e) {
            Log.d("AlertTag", "catch");
            e.printStackTrace();
        }
    }

    /// 메인 메뉴
    public void startMain() {

        setContentView(R.layout.new_activity_main);
        startAlert();


        boolean selectMode = false;
        try {
            Set<String> selectSet = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getStringSet("team_list_select", null);
            selectSetString = selectSet.toArray(new String[]{});
            if (selectSetString.length != 0) {
                selectMode = true;
                Log.d("selectSetStringlength", String.valueOf(selectSetString.length));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Set is None", "none");
            selectMode = false;
        }

        RelativeLayout mainActivity = (RelativeLayout) findViewById(R.id.main_content);
        backgroundSetter(mainActivity, "main");
        currentScene = "main";
        if (!wholeMode) {
            if (selectMode)
                logoSetter("처음화면", 2);
            else
                logoSetter("처음화면", 1);
        } else {
            logoSetter("처음화면");
        }
        noticeBarController("main");
        bottomButtonController("main");
        if (wholeTimer != null)
            wholeTimer.cancel();

        autoOrganizationController();

        /*final FrameLayout invisibleButton = (FrameLayout) findViewById(R.id.invisible);
        invisibleButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showAdminModeDialog();
                return false;
            }
        });*/

        DigitalClock invisibleButton = (DigitalClock) findViewById(R.id.digitalClock1);
        invisibleButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showAdminModeDialog();
                return false;
            }
        });

        /*
        if (prevCode.equals("") && !wholeMode) {
            wholeMode = true;
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("whole_mode", wholeMode);
            try {startMain();}
            catch (OutOfMemoryError e){e.printStackTrace();}
        }
        */

        try {
            final String[] items = new String[jsonParser.wholeGetTeams[0].length];
            for (int i = 0; i < jsonParser.wholeGetTeams[0].length; i++) {
                try {
                    items[i] = jsonParser.wholeGetTeams[0][i].getString("section_name");
                    fullCodeHash.put(jsonParser.wholeGetTeams[0][i].getString("section_fullcode"), jsonParser.wholeGetTeams[0][i].getString("section_name"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        try {
//Home buttons
            final ImageView buildingBox = (ImageView) findViewById(R.id.box_building);//메인화면 큰버튼.
            final ImageView galleryBox = (ImageView) findViewById(R.id.box_gallery);
            final ImageView organizationBox = (ImageView) findViewById(R.id.box_organization);
            final ImageView noticeBox = (ImageView) findViewById(R.id.box_notice);
            final ImageView aboutBox = (ImageView) findViewById(R.id.box_about);

// 원래꺼
/*            final LinearLayout buildingBox = (LinearLayout) findViewById(R.id.box_building);
            final LinearLayout galleryBox = (LinearLayout) findViewById(R.id.box_gallery);
            final LinearLayout organizationBox = (LinearLayout) findViewById(R.id.box_organization);
            final LinearLayout noticeBox = (LinearLayout) findViewById(R.id.box_notice);
            final LinearLayout aboutBox = (LinearLayout) findViewById(R.id.box_about);*/

            if (wholeMode) {
                aboutBox.setImageResource(R.drawable.new_main_box_7_out);
            }

//Home buttons' bounce animation
            final Animation bounceMenu = AnimationUtils.loadAnimation(this, R.anim.bounce);
            View.OnClickListener boxButtonListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    returnMemories(); // 글로벌 변수 초기화 및 타이머 초기화?
                    jsonParser.loadJsonData();

                    if (v == buildingBox) {
                        animation_func(v, "building");
                        buildingBox.startAnimation(bounceMenu);
                    } else if (v == galleryBox) {
                        galleryBox.startAnimation(bounceMenu);
                        animation_func(v, "gallery");
                    } else if (v == organizationBox) {
                        organizationBox.startAnimation(bounceMenu);
                        animation_func(v, "organization");
                    } else if (v == noticeBox) {
                        noticeBox.startAnimation(bounceMenu);
                        animation_func(v, "notice");
                    } else if (v == aboutBox) {
                        aboutBox.startAnimation(bounceMenu);
                        animation_func(v, "about");
                    }

                    buildingBox.setOnClickListener(null);
                    galleryBox.setOnClickListener(null);
                    organizationBox.setOnClickListener(null);
                    noticeBox.setOnClickListener(null);
                    aboutBox.setOnClickListener(null);
                }

                void animation_func(View v, final String buttonString) {
                    v.startAnimation(bounceMenu);
                    bounceMenu.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            releasePlayer();
                            startActivity(new Intent(MainActivity.this, MainActivity.class)
                                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("scene", buttonString));
                            finish();
                            bounceMenu.setAnimationListener(null);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                }
            };

            buildingBox.setOnClickListener(boxButtonListener);
            aboutBox.setOnClickListener(boxButtonListener);
            galleryBox.setOnClickListener(boxButtonListener);
            organizationBox.setOnClickListener(boxButtonListener);
            noticeBox.setOnClickListener(boxButtonListener);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
    }

    ViewFlipper buildingFlipper;

    void tabInitalize(final TextView v) {
        v.setBackgroundResource(R.drawable.save_btn_out_xml);
        v.setTextColor(0xFF9c9c9c);
        v.setVisibility(GONE);
    }

    /// 청사안내 메뉴
    public void startBuilding() {
        setContentView(R.layout.new_activity_building);
        currentScene = "building";
        final TextView[] floorTextList = {(TextView) findViewById(R.id.floor_text_1), (TextView) findViewById(R.id.floor_text_2), (TextView) findViewById(R.id.floor_text_3),
                (TextView) findViewById(R.id.floor_text_4), (TextView) findViewById(R.id.floor_text_5), (TextView) findViewById(R.id.floor_text_6),
                (TextView) findViewById(R.id.floor_text_7), (TextView) findViewById(R.id.floor_text_8), (TextView) findViewById(R.id.floor_text_9)};

        final TextView[] floorTextContentsList = {(TextView) findViewById(R.id.floor_text_contents_1), (TextView) findViewById(R.id.floor_text_contents_2), (TextView) findViewById(R.id.floor_text_contents_3),
                (TextView) findViewById(R.id.floor_text_contents_4), (TextView) findViewById(R.id.floor_text_contents_5), (TextView) findViewById(R.id.floor_text_contents_6),
                (TextView) findViewById(R.id.floor_text_contents_7), (TextView) findViewById(R.id.floor_text_contents_8), (TextView) findViewById(R.id.floor_text_contents_9)};

        final RelativeLayout[] floorLayoutList = {(RelativeLayout) findViewById(R.id.floor_text_layout_1), (RelativeLayout) findViewById(R.id.floor_text_layout_2), (RelativeLayout) findViewById(R.id.floor_text_layout_3),
                (RelativeLayout) findViewById(R.id.floor_text_layout_4), (RelativeLayout) findViewById(R.id.floor_text_layout_5), (RelativeLayout) findViewById(R.id.floor_text_layout_6),
                (RelativeLayout) findViewById(R.id.floor_text_layout_7), (RelativeLayout) findViewById(R.id.floor_text_layout_8), (RelativeLayout) findViewById(R.id.floor_text_layout_9)};

        final FrameLayout[] flipBtn = {(FrameLayout) findViewById(R.id.flip_btn_1), (FrameLayout) findViewById(R.id.flip_btn_2), (FrameLayout) findViewById(R.id.flip_btn_3),
                (FrameLayout) findViewById(R.id.flip_btn_4), (FrameLayout) findViewById(R.id.flip_btn_5), (FrameLayout) findViewById(R.id.flip_btn_6),
                (FrameLayout) findViewById(R.id.flip_btn_7), (FrameLayout) findViewById(R.id.flip_btn_8), (FrameLayout) findViewById(R.id.flip_btn_9)};


        startAlert();
        Log.d("startBuilding", "inside");
        if (buildingFlipper != null) {
            buildingFlipper.removeAllViews();
            buildingFlipper = null;
        }

        jsonParser.loadJsonData();
        jsonParser.parseBuildingJson();

        final RelativeLayout mainActivity = (RelativeLayout) findViewById(R.id.building_content);
        backgroundSetter(mainActivity, "building");

        logoSetter("청사안내");
        noticeBarController("building");
        bottomButtonController("building");
        autoOrganizationController();

        try {
            buildingFlipper = (ViewFlipper) findViewById(R.id.building_flipper);

            final TextView firstTab = (TextView) findViewById(R.id.firstTab);
            final TextView secondTab = (TextView) findViewById(R.id.secondTab);
            final TextView thirdTab = (TextView) findViewById(R.id.thirdTab);
            final TextView fourthTab = (TextView) findViewById(R.id.fourthTab);

            final TextView fifthTab = (TextView) findViewById(R.id.fifthTab);
            final TextView sixthTab = (TextView) findViewById(R.id.sixthTab);
            final TextView seventhTab = (TextView) findViewById(R.id.seventhTab);
            final TextView eighthTab = (TextView) findViewById(R.id.eighthTab);

            tabInitalize(firstTab);
            tabInitalize(secondTab);
            tabInitalize(thirdTab);
            tabInitalize(fourthTab);
            tabInitalize(fifthTab);
            tabInitalize(sixthTab);
            tabInitalize(seventhTab);
            tabInitalize(eighthTab);
            firstTab.setBackgroundResource(R.drawable.save_btn_xml);
            firstTab.setTextColor(0xFFFFFFFF);
            final int bNum = jsonParser.getBuildingNumViewer();
            for (int i = 0; i < jsonParser.getBuildingFloorCountViewer(bNum - 1); i++) {
                buildingFlipper.addView(buildingImageAdder(bNum - 1, i));
            }


            final Button next = (Button) findViewById(R.id.next);
            final Button prev = (Button) findViewById(R.id.prev);

            RelativeLayout tabLayout = (RelativeLayout) findViewById(R.id.relativeLayout2);
            final String[] buildingTabText = new String[1];
            final TextView[] buildingText = {(TextView) findViewById(R.id.building_name_text), (TextView) findViewById(R.id.stage_info)};
            buildingText[0].setText(jsonParser.getBuildingNameViewer(bNum - 1));

            if (typefacenanummjexbold == null) {
                typefacenanummjexbold = Typeface.createFromAsset(getAssets(), "nanummjexbold.ttf");
            }
            buildingText[0].setTypeface(typefacenanummjexbold);
            buildingText[1].setTypeface(typefacenanummjexbold);


            for (; ; ) {
                Log.d("Building Num + parsing", (bNum - 1) + " + " + jsonParser.getBuildingNameViewer(bNum - 1));
                tabLayout.setVisibility(View.VISIBLE);
                firstTab.setVisibility(View.VISIBLE);
                firstTab.setText(jsonParser.getBuildingNameViewer(bNum - 1));
                if (bNum == 1) {
                    break;
                }
                tabLayout.setVisibility(View.VISIBLE);
                firstTab.setVisibility(View.VISIBLE);
                firstTab.setText(jsonParser.getBuildingNameViewer(bNum - 1));

                secondTab.setVisibility(View.VISIBLE);
                secondTab.setText(jsonParser.getBuildingNameViewer(bNum - 2));
                if (bNum == 2) {
                    break;
                }
                thirdTab.setVisibility(View.VISIBLE);
                thirdTab.setText(jsonParser.getBuildingNameViewer(bNum - 3));
                if (bNum == 3) {
                    break;
                }
                fourthTab.setVisibility(View.VISIBLE);
                fourthTab.setText(jsonParser.getBuildingNameViewer(bNum - 4));
                if (bNum == 4) {
                    break;
                }
                fifthTab.setVisibility(View.VISIBLE);
                fifthTab.setText(jsonParser.getBuildingNameViewer(bNum - 5));
                if (bNum == 5) {
                    break;
                }
                sixthTab.setVisibility(View.VISIBLE);
                sixthTab.setText(jsonParser.getBuildingNameViewer(bNum - 6));
                if (bNum == 6) {
                    break;
                }
                seventhTab.setVisibility(View.VISIBLE);
                seventhTab.setText(jsonParser.getBuildingNameViewer(bNum - 7));
                if (bNum == 7) {
                    break;
                }
                eighthTab.setVisibility(View.VISIBLE);
                eighthTab.setText(jsonParser.getBuildingNameViewer(bNum - 8));
                break;
            }

            View.OnClickListener tabClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int j = 0; j < buildingFlipper.getChildCount(); j++) {
                        floorTextContentsList[j].setTextColor(0xFFc9c9c9);
                        floorTextList[j].setTextColor(0xFFc9c9c9);
                        flipBtn[j].setVisibility(View.GONE);
                    }
                    autoOrganizationController();
                    flipCounter[0] = 0;

                    for (int i = 0; i < floorTextList.length; i++) {
                        Log.d("chk i", "i = " + i);
                        Log.d("chiLog", "view = " + floorLayoutList[i]);
                        floorLayoutList[i].setVisibility(View.INVISIBLE);
                        flipBtn[i].setVisibility(View.GONE);
                    }

                    flipBtn[0].setVisibility(View.VISIBLE);
                    floorTextContentsList[0].setTextColor(0xFFFFFFFF);
                    floorTextList[0].setTextColor(0xFFFFFFFF);

                    firstTab.setBackgroundResource(R.drawable.save_btn_out_xml);
                    secondTab.setBackgroundResource(R.drawable.save_btn_out_xml);
                    thirdTab.setBackgroundResource(R.drawable.save_btn_out_xml);
                    fourthTab.setBackgroundResource(R.drawable.save_btn_out_xml);
                    fifthTab.setBackgroundResource(R.drawable.save_btn_out_xml);
                    sixthTab.setBackgroundResource(R.drawable.save_btn_out_xml);
                    seventhTab.setBackgroundResource(R.drawable.save_btn_out_xml);
                    eighthTab.setBackgroundResource(R.drawable.save_btn_out_xml);

                    firstTab.setTextColor(0xFF9c9c9c);
                    secondTab.setTextColor(0xFF9c9c9c);
                    thirdTab.setTextColor(0xFF9c9c9c);
                    fourthTab.setTextColor(0xFF9c9c9c);
                    fifthTab.setTextColor(0xFF9c9c9c);
                    sixthTab.setTextColor(0xFF9c9c9c);
                    seventhTab.setTextColor(0xFF9c9c9c);
                    eighthTab.setTextColor(0xFF9c9c9c);

                    buildingFlipper.removeAllViews();

                    int tmp = 0;
                    if (v == firstTab) {
                        buildingTabText[0] = (String) firstTab.getText();
                        firstTab.setBackgroundResource(R.drawable.save_btn_xml);
                        firstTab.setTextColor(0xFFFFFFFF);
                        tmp = 0;
                    } else if (v == secondTab) {
                        buildingTabText[0] = (String) secondTab.getText();
                        secondTab.setBackgroundResource(R.drawable.save_btn_xml);
                        secondTab.setTextColor(0xFFFFFFFF);
                        tmp = 1;
                    } else if (v == thirdTab) {
                        buildingTabText[0] = (String) thirdTab.getText();
                        thirdTab.setBackgroundResource(R.drawable.save_btn_xml);
                        thirdTab.setTextColor(0xFFFFFFFF);
                        tmp = 2;
                    } else if (v == fourthTab) {
                        buildingTabText[0] = (String) fourthTab.getText();
                        fourthTab.setBackgroundResource(R.drawable.save_btn_xml);
                        fourthTab.setTextColor(0xFFFFFFFF);
                        tmp = 3;
                    } else if (v == fifthTab) {
                        buildingTabText[0] = (String) fifthTab.getText();
                        fifthTab.setBackgroundResource(R.drawable.save_btn_xml);
                        fifthTab.setTextColor(0xFFFFFFFF);
                        tmp = 4;
                    } else if (v == sixthTab) {
                        buildingTabText[0] = (String) sixthTab.getText();
                        sixthTab.setBackgroundResource(R.drawable.save_btn_xml);
                        sixthTab.setTextColor(0xFFFFFFFF);
                        tmp = 5;
                    } else if (v == seventhTab) {
                        buildingTabText[0] = (String) seventhTab.getText();
                        seventhTab.setBackgroundResource(R.drawable.save_btn_xml);
                        seventhTab.setTextColor(0xFFFFFFFF);
                        tmp = 6;
                    } else if (v == eighthTab) {
                        buildingTabText[0] = (String) eighthTab.getText();
                        eighthTab.setBackgroundResource(R.drawable.save_btn_xml);
                        eighthTab.setTextColor(0xFFFFFFFF);
                        tmp = 7;
                    }
                    TextView tv = (TextView) findViewById(R.id.building_name_text);
                    tv.setText(buildingTabText[0]);
                    buildingFlipper.removeAllViews();

                    for (int i = 0; i < jsonParser.getBuildingFloorCountViewer(bNum - tmp - 1); i++) {
                        try {
                            if (jsonParser.bViewerArray.get(bNum - tmp - 1).getJSONObject(i).getString("bu_name").equals("전체")) {
                                buildingFlipper.addView(buildingImageAdder(bNum - tmp - 1, i));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    for (int i = 0; i < jsonParser.getBuildingFloorCountViewer(bNum - tmp - 1); i++) {
                        try {
                            if (jsonParser.bViewerArray.get(bNum - tmp - 1).getJSONObject(i).getString("bu_name").equals("전체")) {
                                continue;
                            }
                            buildingFlipper.addView(buildingImageAdder(bNum - tmp - 1, i));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    if (buildingFlipper.getChildCount() == 1) {
                        next.setOnClickListener(null);
                        prev.setOnClickListener(null);
                        next.setVisibility(View.INVISIBLE);
                        prev.setVisibility(View.INVISIBLE);
                    } else {
                        next.setVisibility(View.VISIBLE);
                        prev.setVisibility(View.VISIBLE);
                        boolean chkPreview = false;
                        int allPreview = 0;
                        int tmpI = 0;
                        for (int i = 1; i < buildingFlipper.getChildCount() + 1; i++) {
                            Log.d("int i", "i = " + i);
                            try {
                                for (int j = 0; j < buildingFlipper.getChildCount(); j++) {
                                    if (!chkPreview && !jsonParser.bViewerArray.get(bNum - tmp - 1).getJSONObject(i).getString("bu_name").equals("전체"))
                                        continue;
                                    else if (!chkPreview) {
                                        allPreview = j;
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if (allPreview == 0)
                                tmpI = i;
                            else
                                tmpI = i - 1;
                            if (buildingFlipper.getChildCount() == i) {
                                i = 0;
                                tmpI = allPreview;
                                Log.d("chk000", "000");
                            }
                            Log.d("int i", "i = " + i);
                            floorLayoutList[i].setVisibility(View.VISIBLE);
                            try {
                                String text;

                                text = jsonParser.bViewerArray.get(bNum - tmp - 1).getJSONObject(tmpI).getString("bu_name");
                                floorTextList[i].setText(text);
                                //floorTextList[i].setTypeface(Typeface.createFromAsset(getAssets(), "nanumsqb.ttf"));
                                //floorTextContentsList[i].setTypeface(Typeface.createFromAsset(getAssets(), "nanumsqb.ttf"));
                                if (text.equals("지하")) {
                                    text = "B1";
                                } else if (text.contains("지하")) {
                                    text = "B" + text.charAt(3);
                                } else if (text.equals("1층")) {
                                    text = getResources().getString(R.string._1층);
                                } else if (text.equals("2층")) {
                                    text = getResources().getString(R.string._2층);
                                } else if (text.equals("3층")) {
                                    text = getResources().getString(R.string._3층);
                                } else if (text.contains("층")) {
                                    text = text.charAt(0) + "th";
                                } else if (text.contains("전체")) {
                                    text = getResources().getString(R.string._전체);
                                } else
                                    text = "";

                                floorTextContentsList[i].setText(text);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            final int finalI = i;

                            floorLayoutList[i].setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    autoOrganizationController();
                                    for (int j = 0; j < buildingFlipper.getChildCount(); j++) {
                                        floorTextContentsList[j].setTextColor(0xFFc9c9c9);
                                        floorTextList[j].setTextColor(0xFFc9c9c9);
                                        flipBtn[j].setVisibility(View.GONE);
                                    }

                                    for (int j = 0; j < 9; j++) {
                                        if (flipCounter[0] > finalI) {
                                            flipCounter[0]--;
                                            autoOrganizationController();
                                            buildingFlipper.setInAnimation(getApplicationContext(), R.anim.in_from_left);
                                            buildingFlipper.setOutAnimation(getApplicationContext(), R.anim.out_to_right);
                                            buildingFlipper.showPrevious();
                                            Log.d("flipCounter", flipCounter[0] + " ");

                                        } else if (flipCounter[0] < finalI) {
                                            flipCounter[0]++;
                                            autoOrganizationController();
                                            buildingFlipper.setInAnimation(getApplicationContext(), R.anim.in_from_right);
                                            buildingFlipper.setOutAnimation(getApplicationContext(), R.anim.out_to_left);
                                            buildingFlipper.showNext();
                                            Log.d("flipCounter", flipCounter[0] + "  ");

                                        } else {
                                            floorTextList[finalI].setTextColor(0xffffffff);
                                            floorTextContentsList[finalI].setTextColor(0xffffffff);
                                            flipBtn[finalI].setVisibility(View.VISIBLE);
                                            break;
                                        }
                                        floorTextList[finalI].setTextColor(0xffffffff);
                                        floorTextContentsList[finalI].setTextColor(0xffffffff);
                                        flipBtn[finalI].setVisibility(View.VISIBLE);
                                    }
                                }
                            });
                            if (i == 0)
                                break;
                        }
                        next.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    autoOrganizationController();
                                    flipCounter[0]++;
                                    for (int j = 0; j < buildingFlipper.getChildCount(); j++) {
                                        floorTextContentsList[j].setTextColor(0xFFc9c9c9);
                                        floorTextList[j].setTextColor(0xFFc9c9c9);
                                        flipBtn[j].setVisibility(View.GONE);
                                    }

                                    if (flipCounter[0] > buildingFlipper.getChildCount() - 1)
                                        flipCounter[0] = 0;
                                    else if (flipCounter[0] < 0)
                                        flipCounter[0] = buildingFlipper.getChildCount() - 1;

                                    autoOrganizationController();
                                    buildingFlipper.setInAnimation(getApplicationContext(), R.anim.in_from_right);
                                    buildingFlipper.setOutAnimation(getApplicationContext(), R.anim.out_to_left);
                                    buildingFlipper.showNext();

                                    Log.d("flipCounter", flipCounter[0] + " ");
                                    floorTextList[flipCounter[0]].setTextColor(0xffffffff);
                                    floorTextContentsList[flipCounter[0]].setTextColor(0xffffffff);
                                    flipBtn[flipCounter[0]].setVisibility(View.VISIBLE);
                                } catch (ArrayIndexOutOfBoundsException e) {
                                    e.printStackTrace();
                                    /*startActivity(new Intent(MainActivity.this, MainActivity.class)
                                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("scene", "main"));
                                    finish();*/
                                }
                            }
                        });

                        prev.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    autoOrganizationController();
                                    flipCounter[0]--;
                                    for (int j = 0; j < buildingFlipper.getChildCount(); j++) {
                                        floorTextContentsList[j].setTextColor(0xFFc9c9c9);
                                        floorTextList[j].setTextColor(0xFFc9c9c9);
                                        flipBtn[j].setVisibility(View.GONE);
                                    }
                                    if (flipCounter[0] > buildingFlipper.getChildCount() - 1)
                                        flipCounter[0] = 0;
                                    else if (flipCounter[0] < 0)
                                        flipCounter[0] = buildingFlipper.getChildCount() - 1;

                                    autoOrganizationController();
                                    buildingFlipper.setInAnimation(getApplicationContext(), R.anim.in_from_left);
                                    buildingFlipper.setOutAnimation(getApplicationContext(), R.anim.out_to_right);
                                    buildingFlipper.showPrevious();

                                    Log.d("flipCounter", flipCounter[0] + " ");
                                    floorTextList[flipCounter[0]].setTextColor(0xffffffff);
                                    floorTextContentsList[flipCounter[0]].setTextColor(0xffffffff);
                                    flipBtn[flipCounter[0]].setVisibility(View.VISIBLE);
                                } catch (ArrayIndexOutOfBoundsException e) {
                                    e.printStackTrace();
                                    /*startActivity(new Intent(MainActivity.this, MainActivity.class)
                                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("scene", "main"));
                                    finish();*/
                                }
                            }
                        });
                    }

                    if (flipCounter[0] > buildingFlipper.getChildCount())
                        flipCounter[0] = 0;
                    else if (flipCounter[0] < 0)
                        flipCounter[0] = buildingFlipper.getChildCount() - 1;
                }
            };

            firstTab.setOnClickListener(tabClickListener);
            secondTab.setOnClickListener(tabClickListener);
            thirdTab.setOnClickListener(tabClickListener);
            fourthTab.setOnClickListener(tabClickListener);
            fifthTab.setOnClickListener(tabClickListener);
            sixthTab.setOnClickListener(tabClickListener);
            seventhTab.setOnClickListener(tabClickListener);
            eighthTab.setOnClickListener(tabClickListener);

            if (buildingFlipper.getChildCount() != 1) {
                next.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            autoOrganizationController();
                            flipCounter[0]++;
                            for (int j = 0; j < buildingFlipper.getChildCount(); j++) {
                                floorTextContentsList[j].setTextColor(0xFFc9c9c9);
                                floorTextList[j].setTextColor(0xFFc9c9c9);
                                flipBtn[j].setVisibility(View.GONE);
                            }

                            if (flipCounter[0] > buildingFlipper.getChildCount() - 1)
                                flipCounter[0] = 0;
                            else if (flipCounter[0] < 0)
                                flipCounter[0] = buildingFlipper.getChildCount() - 1;

                            autoOrganizationController();
                            buildingFlipper.setInAnimation(getApplicationContext(), R.anim.in_from_right);
                            buildingFlipper.setOutAnimation(getApplicationContext(), R.anim.out_to_left);
                            buildingFlipper.showNext();

                            Log.d("flipCounter", flipCounter[0] + " " + (buildingFlipper.getChildCount() - 1));
                            floorTextList[flipCounter[0]].setTextColor(0xffffffff);
                            floorTextContentsList[flipCounter[0]].setTextColor(0xffffffff);
                            flipBtn[flipCounter[0]].setVisibility(View.VISIBLE);
                        } catch (ArrayIndexOutOfBoundsException e) {
                            e.printStackTrace();
                            /*startActivity(new Intent(MainActivity.this, MainActivity.class)
                                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("scene", "main"));
                            finish();*/
                        }
                    }
                });

                prev.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            autoOrganizationController();
                            flipCounter[0]--;
                            for (int j = 0; j < buildingFlipper.getChildCount(); j++) {
                                floorTextContentsList[j].setTextColor(0xFFc9c9c9);
                                floorTextList[j].setTextColor(0xFFc9c9c9);
                                flipBtn[j].setVisibility(View.GONE);
                            }
                            if (flipCounter[0] > buildingFlipper.getChildCount() - 1)
                                flipCounter[0] = 0;
                            else if (flipCounter[0] < 0)
                                flipCounter[0] = buildingFlipper.getChildCount() - 1;

                            autoOrganizationController();
                            buildingFlipper.setInAnimation(getApplicationContext(), R.anim.in_from_left);
                            buildingFlipper.setOutAnimation(getApplicationContext(), R.anim.out_to_right);
                            buildingFlipper.showPrevious();

                            Log.d("flipCounter", flipCounter[0] + " ");
                            floorTextList[flipCounter[0]].setTextColor(0xffffffff);
                            floorTextContentsList[flipCounter[0]].setTextColor(0xffffffff);
                            flipBtn[flipCounter[0]].setVisibility(View.VISIBLE);
                        } catch (ArrayIndexOutOfBoundsException e) {
                            e.printStackTrace();
                            /*startActivity(new Intent(MainActivity.this, MainActivity.class)
                                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("scene", "main"));
                            finish();*/
                        }
                    }
                });
            }
            try {
                firstTab.performClick();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {

            }
        } catch (Exception e) {
            e.printStackTrace();
            /*startActivity(new Intent(MainActivity.this, MainActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("scene", "main"));
            finish();*/
        }
    }

    //18.12.28까지 완.
    private int currentY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        try {
            mGestureDetector.onTouchEvent(event);
            if (findViewById(R.id.about_container) != null) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        currentY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int y = (int) event.getRawY();
                        findViewById(R.id.about_container).scrollBy(0, currentY - y);
                        currentY = y;
                        break;
                }
                return false;
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return super.onTouchEvent(event);
    }


    /// Add images to building flipper
    protected ImageView buildingImageAdder(int tabNum, int index) { // 이미지 리턴시키는 메서드(오버로딩) 각 탭, 층별 청사안내 이미지 리턴
        try {
            File buildingFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/KIOSKData/building/" + jsonParser.buildingPics[tabNum][index]); // 절대경로 + 빌딩이미지
            // 파서로 인한 결과값
            // 내부 저장소?

            ImageView imageView = new ImageView(this);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

            Glide.with(this).load(buildingFile).diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true).dontAnimate().into(imageView);

            return imageView;
        } catch (OutOfMemoryError o) {
            o.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return null;
    }


    protected WebView buildingImageAdder(int tabNum, int index, final String teamCode) { // 직원검색에서 부서위치 찾을 때 탭/인덱스별
        final WebView webView = new WebView(this);
        WebSettings settings = webView.getSettings();
        WebViewSet(webView, settings, teamCode);
        webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return (event.getAction() == MotionEvent.ACTION_MOVE);
            }
        });

        String fileName = jsonParser.buildingPics[tabNum][index].substring(0, jsonParser.buildingPics[tabNum][index].length() - 4); //buildingpics를 이용한 json 쿼리, s_building 어쩌고..
        webView.loadUrl("file:///android_asset/" + fileName + ".html");// 에셋에 저장된 Url중 건물이름으로 불러옴.

        return webView;//웹뷰 리턴
    }

    protected WebView buildingImageAdder(final String teamCode, final String fileName) { // 팀코드 / 파일명
        WebView webView = new WebView(this);
        WebSettings settings = webView.getSettings();
        WebViewSet(webView, settings, teamCode);

        //webView.setY(100);

        webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final LinearLayout buildingFrame = (LinearLayout) findViewById(R.id.search_building_layout);
                final LinearLayout buttonFrame = (LinearLayout) findViewById(R.id.building_button_layout);
                buildingFrame.removeAllViews();
                buildingFrame.setVisibility(GONE);
                buttonFrame.setVisibility(GONE);
                gotInterface = false;

                return (event.getAction() == MotionEvent.ACTION_MOVE);
            }
        });

        //webView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        webView.loadUrl("file:///android_asset/" + fileName + ".html");//파일네임으로 불러오기.

        return webView;
    }

    protected void WebViewSet(final WebView webView, WebSettings settings, final String teamCode) { // buildingImageAdder에 있는 settings를 가져다 모은 메서드
        settings.setJavaScriptEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(false);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setDomStorageEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);

        webView.setHorizontalScrollBarEnabled(false);
        webView.setVerticalScrollBarEnabled(false);
        webView.setBackgroundColor(Color.TRANSPARENT);
        webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                //webView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                view.loadUrl("javascript:set_var(\"" + teamCode + "\");"); // teamCode에 맞는 데이터를
                // 자바스크립트로 로드.
            }
        });
        webView.addJavascriptInterface(new BuildingJavascriptInterface(), "buildingInterface");//웹뷰에 자바스크립트 인터페이스 삽입(buildingInterface - ?? 자바스크립트 정보 필요)!help!

        settings.setAllowUniversalAccessFromFileURLs(true);
    }

    ///부서안내/관광안내 메뉴
    private void fullScreenViewerSetting(String currentScene, File[] file) {
        int count = 0;
        RelativeLayout mainActivity = null;
        String picPath = null;

        if (currentScene.equals("about")) {
            count = jsonParser.getTourCount();
            setContentView(R.layout.new_activity_about);
            mainActivity = (RelativeLayout) findViewById(R.id.tour_content);
        } else if (currentScene.equals("gallery")) {
            count = jsonParser.getGalleryCount();
            setContentView(R.layout.new_activity_gallery);
            mainActivity = (RelativeLayout) findViewById(R.id.gallery_content);
        }

        for (int i = 0; i < count; i++) {
            if (currentScene.equals("about")) {
                picPath = jsonParser.getTourPicPath(i);
            } else if (currentScene.equals("gallery")) {
                picPath = jsonParser.getGalleryPicPath(i);
            } else
                return;
            if (currentScene.equals("about"))
                currentScene = "tour";

            file[i] = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/KIOSKData/" + currentScene + "/" + picPath);
            if (currentScene.equals("tour"))
                currentScene = "about";
        }
        try {
            setGalleryThread(file, count);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        noticeBarController(currentScene);
        bottomButtonController(currentScene);
        backgroundSetter(mainActivity, currentScene);
        autoOrganizationController();
        for (int i = 0; i < count; i++) {
            file[i] = null;
        }
    }

    private void fullScreenViewerLayout(String currentScene) {
        try {

            Button nexts = null;
            Button prevs = null;
            if (currentScene.equals("gallery")) {
                galleryTitle = (TextView) findViewById(R.id.gallery_text);
                galleryFlipper = (ViewFlipper) findViewById(R.id.gallery_flipper);
                nexts = (Button) findViewById(R.id.next);
                prevs = (Button) findViewById(R.id.prev);
            } else if (currentScene.equals("about")) {
                galleryFlipper = (ViewFlipper) findViewById(R.id.about_flipper);
                nexts = (Button) findViewById(R.id.about_next);
                prevs = (Button) findViewById(R.id.about_prev);
            }

            galleryFlipper.removeAllViews();
            int count = 0;

            if (currentScene.equals("about")) {
                count = jsonParser.getTourCount();
            } else if (currentScene.equals("gallery")) {
                count = jsonParser.getGalleryCount();
            }
            for (int i = 0; i < count; i++) {
                if (galleryViews[i].getParent() != null)
                    ((ViewFlipper) galleryViews[i].getParent()).removeView(galleryViews[i]);
                galleryFlipper.addView(galleryViews[i]);
            }

            if (currentScene.equals("gallery"))
                galleryTitle.setText(jsonParser.getGalleryTitle(galleryFlipper.getDisplayedChild()));

            final Button next = nexts;
            final Button prev = prevs;
            final String finalCurrentScene = currentScene;
            View.OnClickListener pagerButtonListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    autoOrganizationController();

                    if (v == next) {
                        galleryFlipper.setInAnimation(getApplicationContext(), R.anim.in_from_right);
                        galleryFlipper.setOutAnimation(getApplicationContext(), R.anim.out_to_left);
                        if (galleryIndex >= imgNum) galleryIndex = 0;
                        galleryFlipper.showNext();
                    } else if (v == prev) {
                        galleryFlipper.setInAnimation(getApplicationContext(), R.anim.in_from_left);
                        galleryFlipper.setOutAnimation(getApplicationContext(), R.anim.out_to_right);
                        if (galleryIndex < 0) galleryIndex = imgNum - 1;
                        galleryFlipper.showPrevious();
                    }
                    if (finalCurrentScene.equals("gallery"))
                        galleryTitle.setText(jsonParser.getGalleryTitle(galleryFlipper.getDisplayedChild()));
                }
            };

            next.setOnClickListener(pagerButtonListener);
            prev.setOnClickListener(pagerButtonListener);

            final Handler handler = new Handler();
            final Runnable Update = new Runnable() {
                public void run() {
                    galleryFlipper.setInAnimation(getApplicationContext(), R.anim.in_from_right);
                    galleryFlipper.setOutAnimation(getApplicationContext(), R.anim.out_to_left);
                    if (galleryIndex >= imgNum) galleryIndex = 0;
                    galleryFlipper.showNext();
                    if (finalCurrentScene.equals("gallery"))
                        galleryTitle.setText(jsonParser.getGalleryTitle(galleryFlipper.getDisplayedChild()));
                }
            };

            ///Auto slide timer
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(Update);
                }
            }, DELAY_MS, PERIOD_MS);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
    }

    ///부서안내/관광안내 메뉴
    public void startAbout() {
        jsonParser.parseAboutJson();
        currentScene = "about";
        if (wholeTimer != null)
            wholeTimer.cancel();
        setContentView(R.layout.new_activity_about);
        startAlert();
        final RelativeLayout mainActivity = (RelativeLayout) findViewById(R.id.about_content);
        backgroundSetter(mainActivity, "about");

        logoSetter("관광안내");
        noticeBarController("about");
        bottomButtonController("about");
        localIconSetter();
        autoOrganizationController();
        boolean selectMode = false;
        findViewById(R.id.tour_content).setVisibility(GONE);
        findViewById(R.id.notice_main).setVisibility(VISIBLE);
        findViewById(R.id.wrapper4).setVisibility(GONE);
        try {
            Set<String> selectSet = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getStringSet("team_list_select", null);
            selectSetString = selectSet.toArray(new String[]{});
            if (selectSetString.length != 0) {
                findViewById(R.id.notice_main).setVisibility(GONE);
                findViewById(R.id.wrapper4).setVisibility(VISIBLE);
                selectMode = true;
                Log.d("selectSetStringlength", String.valueOf(selectSetString.length));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Set is None", "none");
            findViewById(R.id.notice_main).setVisibility(VISIBLE);
            findViewById(R.id.wrapper4).setVisibility(GONE);
            selectMode = false;
        }
        ImageView aboutImage = (ImageView) findViewById(R.id.about_image);
        aboutImage.setScaleType(ImageView.ScaleType.FIT_XY);
        ScrollView scrollView = (ScrollView) findViewById(R.id.about_container);
        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                autoOrganizationController();
            }
        });

        aboutIndex = -1;
        try {
            if (!wholeMode) {
                if (!selectMode) {
                    for (int i = 0; i < jsonParser.aboutPics.length; i++) {
                        if (jsonParser.aboutPics[i].getString("section_cd").equals(teamCode)) {
                            aboutIndex = i;
                            Log.d("aboutIndexChecker", String.valueOf(aboutIndex));
                            Glide.with(this).load(Environment.getExternalStorageDirectory().getAbsolutePath()
                                    + "/KIOSKData/about/" + jsonParser.getAboutPicPath(aboutIndex)).asBitmap().diskCacheStrategy(DiskCacheStrategy.NONE).format(DecodeFormat.PREFER_ARGB_8888).into(aboutImage);
                            scrollView.scrollBy(0, 0);
                        }
                    }
                    if (aboutIndex == -1) {
                        jsonParser.loadJsonData();
                        jsonParser.parseTourJson();
                        try {
                            File[] file;
                            file = new File[jsonParser.getTourCount()];

                            fullScreenViewerSetting(currentScene, file);
                            fullScreenViewerLayout(currentScene);
                        } catch (NullPointerException e) {
                            findViewById(R.id.wrapper4).setVisibility(GONE);
                        }
                        findViewById(R.id.notice_main).setVisibility(GONE);
                        findViewById(R.id.tour_content).setVisibility(VISIBLE);
                    }
                } else {
                    selectPackerMethod(2);
                    Log.d("aboutIndexChecker(else)", String.valueOf(aboutIndex));
                }
            } else {

                jsonParser.loadJsonData();
                jsonParser.parseTourJson();
                try {
                    File[] file;
                    file = new File[jsonParser.getTourCount()];

                    fullScreenViewerSetting(currentScene, file);
                    fullScreenViewerLayout(currentScene);
                } catch (NullPointerException e) {
                    findViewById(R.id.wrapper4).setVisibility(GONE);
                }
                findViewById(R.id.notice_main).setVisibility(GONE);
                findViewById(R.id.tour_content).setVisibility(VISIBLE);
            }
            //Glide.with(this).load(galleryFile[i]).dontAnimate().fitCenter().into(galleryViews[i]);

        } catch (JSONException | ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    /// 포토갤러리 필드
    int imgNum, galleryIndex;
    ViewFlipper galleryFlipper;
    TextView galleryTitle;

    /// 포토갤러리 메뉴
    public void startGallery() {
        try {
            jsonParser.loadJsonData();
            jsonParser.parseGalleryJson();
            jsonParser.parseGalleryWholeJson();
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            Set<String> publicSet = preferences.getStringSet("show_public_data", null);
            if (publicSet != null) {
                if (!publicSet.contains("갤러리"))
                    jsonParser.subGalleryJson();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        RelativeLayout mainActivity = null;
        setContentView(R.layout.new_activity_gallery);
        mainActivity = (RelativeLayout) findViewById(R.id.gallery_content);

        startAlert();

        mainActivity.setBackgroundColor(Color.GRAY);
        noticeBarController(currentScene);
        bottomButtonController(currentScene);

        currentScene = "gallery";
        File[] galleryFile = null;
        try {
            galleryFile = new File[jsonParser.getGalleryCount()];
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        try {
            fullScreenViewerSetting(currentScene, galleryFile);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        try {
            fullScreenViewerLayout(currentScene);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public File[] setGalleryThread(File[] galleryFile, int count) {
        for (int i = 0; i < count; i++) {
            galleryViews[i] = new ImageView(context);
            galleryViews[i].setScaleType(ImageView.ScaleType.FIT_XY);

            try {
                Glide.with(this).load(galleryFile[i]).override(1920, 965).fitCenter().diskCacheStrategy(DiskCacheStrategy.NONE).into(galleryViews[i]);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

        }
        return galleryFile;
    }
    /// 부서조직도 필드

    static ViewPager mViewPager;
    final boolean[] isRunning = new boolean[1];
    static OrganizationPagerAdapter mOrganizationPagerAdapter; // Organization viewpager adapter
    int currentPage = 0, tmp = 0; // Organization viewpager index, Popup working status temp
    public int sucCount, teamNum, exPage, exPageFix, pastTeam, pageCounter, pageIndex, allPageCounter, devideCounter;
    public boolean chkFirst = true, firstDevide = true;
    public HashMap<Integer, Integer> highestHash = new HashMap<>();
    public HashMap<Integer, Integer> pagePositionHash = new HashMap<>();
    HashMap<Integer, Integer> swHash = new HashMap<>();
    boolean orgMode;
    int orgTab = 1; //본관탭
    public int presentMates = 0;
    static EditText searchText;
    static boolean searchToggle = true;
    boolean globalSelectMode = false;
    static TextView searchCount;
    static LinearLayout searchLoadingText;
    static TextView searchLoadingTextS;
    static TextView searchLoadingTextS2;
    static ProgressBar searchLoadingPb;
    static LinearLayout searchCountText;

    /// 부서조직도 메뉴
    public void startOrganization() {
        setContentView(R.layout.new_activity_organization);
        currentScene = "organization";
        startAlert();
        orgMode = false;
        currentPage = 0;
        searchToggle = true;
        globalSelectMode = false;
        Log.d("OrganizationTag", "start");
        boolean selectMode = false;
        try {
            Set<String> selectSet = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getStringSet("team_list_select", null);
            selectSetString = selectSet.toArray(new String[]{});
            if (selectSetString.length != 0) {
                selectMode = true;
                Log.d("selectSetStringlength", String.valueOf(selectSetString.length));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Set is None", "none");
            selectMode = false;
        }

        if (mViewPager != null) {
            mViewPager.removeAllViews();
            mViewPager = null;
            mOrganizationPagerAdapter = null;
        }
        System.gc(); // 왜 했을까?? !help!
        jsonParser.parseStaffJson(teamCode);
        jsonParser.parseBuildingJson();

        customViewPopupVideo = (CustomViewMainVideo) findViewById(R.id.custom_view_popup_video);

        try {
            Log.d("OrganizationTag", "try1");

            /*try {
                findViewById(R.id.teamLogo).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showSeatSettingDialog();
                    }
                });
            } catch (Exception e) {
            }*/

            LinearLayout backButton = (LinearLayout) findViewById(R.id.backbutton);
            backButton.setVisibility(View.INVISIBLE);

            findViewById(R.id.keyboard_wrapper).setVisibility(GONE);

            searchText = (EditText) findViewById(R.id.search_text);
            searchText.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    int inType = searchText.getInputType(); // backup the input type
                    searchText.setInputType(InputType.TYPE_NULL); // disable soft input
                    searchText.onTouchEvent(event); // call native handler
                    searchText.setInputType(inType);
                    return true;
                }
            });

            if (!wholeMode)
                findViewById(R.id.search).setVisibility(GONE);//서치버튼 비활성화
            findViewById(R.id.search).setOnClickListener(new View.OnClickListener() {// 서치버튼 찾기와 동시에 리스너 설정
                @Override
                public void onClick(View v) {
                    autoOrganizationController();

                    findViewById(R.id.keyboard_wrapper).setVisibility(VISIBLE);//keyboard_wrapper는 relative layout으로 구성되어 있으며, 검색창 전체를 뜻함.

                    searchCount = (TextView) findViewById(R.id.search_count); // 검색횟수 표출 텍스트 뷰
                    searchLoadingText = (LinearLayout) findViewById(R.id.search_loading_text);
                    searchLoadingTextS = (TextView) findViewById(R.id.search_loading_texts);
                    searchLoadingTextS2 = (TextView) findViewById(R.id.search_loading_texts2);
                    searchLoadingPb = (ProgressBar) findViewById(R.id.search_pbs);

                    searchLoadingText.setVisibility(INVISIBLE);

                    searchCountText = (LinearLayout) findViewById(R.id.search_count_text); // 검색횟수 표출 레이아웃. 최초 투명하게, 검색 후 보이도록.
                    searchCountText.setVisibility(View.INVISIBLE);

                    final FragmentTransaction searchListTransaction = getSupportFragmentManager().beginTransaction();
                    FragmentTransaction keyboardTransaction = getSupportFragmentManager().beginTransaction();

                    /*Fragment prevListFrag = getSupportFragmentManager().findFragmentByTag("searchListFrag");
                    if (prevListFrag != null) {
                        searchListTransaction.remove(prevListFrag);
                    }*/ // 왜 있는걸까? !help!
                    searchListTransaction.replace(R.id.search_list_frag, new SearchListFragment(), "searchListFrag").commit(); // 교체 후 바로 SearchListFrag 로 태그 부여.

                    /*Fragment prevKeyboardFrag = getSupportFragmentManager().findFragmentByTag("keyboardFrag");//
                    if (prevKeyboardFrag != null)
                        searchListTransaction.remove(prevKeyboardFrag);*/ // 왜?????
                    keyboardTransaction.replace(R.id.keyboard_frag, new KoreanKeyboard(), "keyboardFrag").commit(); // 교체 후 바로 keyboardFrag로 태그 부여.

                    /*findViewById(R.id.close_keyboard).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            findViewById(R.id.keyboard_wrapper).setVisibility(GONE);
                            Fragment prevListFrag = getSupportFragmentManager().findFragmentByTag("searchListFrag");
                            if (prevListFrag != null)
                                searchListTransaction.remove(prevListFrag);

                            removeSearchTextFocus();
                        }
                    });*/
                    findViewById(R.id.username_icon).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent keyIntent = new Intent("keyboardEnterInput");
                            context.sendBroadcast(keyIntent);
                        }
                    });
                }
            });
            Log.d("OrganizationTag", "try2");
            com.suke.widget.SwitchButton switchButton = (com.suke.widget.SwitchButton) findViewById(R.id.switch_button);
            switchButton.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                    autoOrganizationController();

                    searchToggle = !isChecked;
                    TextView memberTogle = (TextView) findViewById(R.id.member_toggle);
                    TextView workTogle = (TextView) findViewById(R.id.work_toggle);
                    memberTogle.setTextColor(Color.WHITE);
                    workTogle.setTextColor(Color.WHITE);

                    if (searchToggle) {
//marker(code)
                        memberTogle.setTextColor(0xFF99CC00);

                        ((EditText) findViewById(R.id.search_text)).setHint("직원명으로 검색합니다.");
                    } else {
                        workTogle.setTextColor(0xFF99CC00);
                        ((EditText) findViewById(R.id.search_text)).setHint("부서명으로 검색합니다.");
                    }
                    searchText.setText("");
                    FragmentTransaction keyboardTransaction = getSupportFragmentManager().beginTransaction();
                    Fragment prevKeyboardFrag = getSupportFragmentManager().findFragmentByTag("keyboardFrag");
                    /*if (prevKeyboardFrag != null)
                        keyboardTransaction.remove(prevKeyboardFrag);*/ // 왜 있는걸까 대체??
                    keyboardTransaction.replace(R.id.keyboard_frag, new KoreanKeyboard(), "keyboardFrag").commit();

                }
            });

            final RelativeLayout lead = (RelativeLayout) findViewById(R.id.leader);
            final RelativeLayout below = (RelativeLayout) findViewById(R.id.below);

            final RelativeLayout wrapper = (RelativeLayout) findViewById(R.id.wrapper1);
            final RelativeLayout wrapper2 = (RelativeLayout) findViewById(R.id.wrapper2);
            final RelativeLayout wrapper3 = (RelativeLayout) findViewById(R.id.wrapper3);
            final LinearLayout wrapper4 = (LinearLayout) findViewById(R.id.wrapper4);
            final LinearLayout tabWrapper = (LinearLayout) findViewById(R.id.tab_wrapper);
            final ImageView leaderBox = (ImageView) findViewById(R.id.leader_box);
            final ImageView belowBox = (ImageView) findViewById(R.id.below_box);

            Log.d("OrganizationTag", "try3");
            RelativeLayout mainActivity = (RelativeLayout) findViewById(R.id.organization_content);
            backgroundSetter(mainActivity, "organization");

            logoSetter("조직도");
            noticeBarController("organization");
            bottomButtonController("organization");

            autoOrganizationController();

            final TextView tab1 = (TextView) findViewById(R.id.tab1);
            final TextView tab2 = (TextView) findViewById(R.id.tab2);
            final TextView tab3 = (TextView) findViewById(R.id.tab3);
            findViewById(R.id.slide_bar).setVisibility(GONE);
            wrapper.setVisibility(GONE);
            wrapper2.setVisibility(GONE);
            wrapper3.setVisibility(GONE);

            tab1.setBackgroundResource(R.drawable.save_btn_out_xml);
            tab2.setBackgroundResource(R.drawable.save_btn_out_xml);
            tab3.setBackgroundResource(R.drawable.save_btn_out_xml);
            tab1.setTextColor(0xFF9c9c9c);
            tab2.setTextColor(0xFF9c9c9c);
            tab3.setTextColor(0xFF9c9c9c);
            Log.d("OrganizationTag", "try33");
            setOrgTab(orgTab, selectMode);

            switch (orgTab) {
                case 1:
                    wrapper.setVisibility(View.VISIBLE);
                    tab1.setBackgroundResource(R.drawable.save_btn_xml);
                    tab1.setTextColor(0xFFffffff);
                    break;
                case 2:
                    wrapper2.setVisibility(View.VISIBLE);
                    tab2.setBackgroundResource(R.drawable.save_btn_xml);
                    tab2.setTextColor(0xFFffffff);
                    break;
                case 3:
                    wrapper3.setVisibility(View.VISIBLE);
                    tab3.setBackgroundResource(R.drawable.save_btn_xml);
                    tab3.setTextColor(0xFFffffff);
                    break;
            }

            tab1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    orgTab = 1;

                    wrapper.setVisibility(View.VISIBLE);
                    wrapper2.setVisibility(GONE);
                    wrapper3.setVisibility(GONE);

                    tab1.setBackgroundResource(R.drawable.save_btn_xml);
                    tab1.setTextColor(0xFFffffff);
                    tab2.setBackgroundResource(R.drawable.save_btn_out_xml);
                    tab2.setTextColor(0xFF9c9c9c);
                    tab3.setBackgroundResource(R.drawable.save_btn_out_xml);
                    tab3.setTextColor(0xFF9c9c9c);

                    setOrgTab(orgTab, false);

                    autoOrganizationController();
                }
            });
            tab2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    orgTab = 2;

                    wrapper.setVisibility(GONE);
                    wrapper2.setVisibility(View.VISIBLE);
                    wrapper3.setVisibility(GONE);

                    tab1.setBackgroundResource(R.drawable.save_btn_out_xml);
                    tab1.setTextColor(0xFF9c9c9c);
                    tab2.setBackgroundResource(R.drawable.save_btn_xml);
                    tab2.setTextColor(0xFFffffff);
                    tab3.setBackgroundResource(R.drawable.save_btn_out_xml);
                    tab3.setTextColor(0xFF9c9c9c);

                    setOrgTab(orgTab, false);

                    autoOrganizationController();
                }
            });
            tab3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    orgTab = 3;

                    wrapper.setVisibility(GONE);
                    wrapper2.setVisibility(GONE);
                    wrapper3.setVisibility(View.VISIBLE);

                    tab1.setBackgroundResource(R.drawable.save_btn_out_xml);
                    tab1.setTextColor(0xFF9c9c9c);
                    tab2.setBackgroundResource(R.drawable.save_btn_out_xml);
                    tab2.setTextColor(0xFF9c9c9c);
                    tab3.setBackgroundResource(R.drawable.save_btn_xml);
                    tab3.setTextColor(0xFFffffff);

                    setOrgTab(orgTab, false);

                    autoOrganizationController();
                }
            });

            mViewPager = (ViewPager) findViewById(R.id.container);
            Log.d("OrganizationTag", "try4");

            if (wholeMode && !autoFlipped) {
                teamCode = "";
                Log.d("OrganizationTag", "tryi1");
            } else if (!wholeMode) {
                teamCode = pref.getString("team_code", getResources().getString(R.string.시장));
                if (selectMode) {
                    logoSetter("selectMode", 2);
                    Log.d("OrganizationTag", "tryi2");
                } else {
                    logoSetter(fullCodeHash.get(teamCode));
                    Log.d("OrganizationTag", "tryi3");
                }
            } else {
                wrapper.setVisibility(GONE);
                wrapper2.setVisibility(GONE);
                wrapper3.setVisibility(GONE);
                tabWrapper.setVisibility(GONE);
                findViewById(R.id.slide_bar).setVisibility(VISIBLE);
                lead.setVisibility(View.VISIBLE);
                below.setVisibility(View.INVISIBLE);//YC_SPEC INVISIBLE(VISIBLE)
                leaderBox.setVisibility(View.VISIBLE);
                belowBox.setVisibility(View.VISIBLE);
                mViewPager.setVisibility(View.VISIBLE);
                setTeamList();
                findViewById(R.id.search).setVisibility(GONE);
                setOrganization();
                Log.d("OrganizationTag", "tryi3");
                return;
            } // 검색모드로 찾기 시 발생되는 이벤트

            if (teamCode == "" || wholeMode) {
                if (orgTab == 1) {
                    wrapper.setVisibility(View.VISIBLE);
                    Log.d("OrganizationTag", "tryii1");
                } else {
                    wrapper.setVisibility(View.INVISIBLE);
                    Log.d("OrganizationTag", "tryii2");
                }

                lead.setVisibility(View.INVISIBLE);
                below.setVisibility(View.INVISIBLE);
                mViewPager.setVisibility(View.INVISIBLE);
                leaderBox.setVisibility(View.INVISIBLE);
                belowBox.setVisibility(View.INVISIBLE);
            } else if (selectMode) {
                globalSelectMode = true;
                wrapper.setVisibility(GONE);
                wrapper2.setVisibility(GONE);
                wrapper3.setVisibility(GONE);
                wrapper4.setVisibility(VISIBLE);
                tabWrapper.setVisibility(GONE);

                lead.setVisibility(View.INVISIBLE);
                below.setVisibility(View.INVISIBLE);
                mViewPager.setVisibility(View.INVISIBLE);
                leaderBox.setVisibility(View.INVISIBLE);
                belowBox.setVisibility(View.INVISIBLE);
            } else {
                Log.d("OrganizationTag", "tryii3");
                wrapper.setVisibility(GONE);
                wrapper2.setVisibility(GONE);
                wrapper3.setVisibility(GONE);
                tabWrapper.setVisibility(GONE);

                lead.setVisibility(View.VISIBLE);//해당부서장
                below.setVisibility(View.INVISIBLE);//YC_SPEC INVISIBLE(VISIBLE)
                leaderBox.setVisibility(View.VISIBLE);
                belowBox.setVisibility(View.VISIBLE);
                mViewPager.setVisibility(View.VISIBLE);
                setTeamList();
                Log.d("OrganizationTag", "setTeamList");
                setOrganization();
                Log.d("OrganizationTag", "setOrganization");
            }
            Log.d("OrganizationTag", "tryfin");
            mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                    if (state != 0)
                        isRunning[0] = true;
                    else
                        isRunning[0] = false;
                }
            });

        } catch (InflateException e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    final AtomicInteger sNextGeneratedId = new AtomicInteger(1);//원자적 정수 생성.(값 = 1)

    public void setSeatPopup(int teamNum, int sortNo) {
        try {
            popupCut = true;
            autoOrganizationController();
            final FrameLayout seatFrame = (FrameLayout) findViewById(R.id.seat_popup);
            final FrameLayout seatFrameParent = (FrameLayout) findViewById(R.id.seat_popup_parent);
            final TextView title = (TextView) findViewById(R.id.popup_title);
            title.setVisibility(VISIBLE);
            String cut;
            if (MainActivity.fullCodeHash.get(teamCode).contains("&gt;")) {
                cut = MainActivity.fullCodeHash.get(teamCode).substring(MainActivity.fullCodeHash.get(teamCode).indexOf(";") + 1, MainActivity.fullCodeHash.get(teamCode).length());//; 다음부터 끝까지.
            } else if (MainActivity.fullCodeHash.get(teamCode).contains(">")) {
                cut = MainActivity.fullCodeHash.get(teamCode).substring(MainActivity.fullCodeHash.get(teamCode).indexOf(">") + 1, MainActivity.fullCodeHash.get(teamCode).length()); // > 다음부터 끝까지
            } else {
                cut = MainActivity.fullCodeHash.get(teamCode); // 그냥 쌩으로
            }
            title.setText(cut + " 좌석배치도");
            //title.setTypeface(Typeface.createFromAsset(getAssets(), "nanumsqb.ttf"));
            findViewById(R.id.dark).setVisibility(GONE);
            seatFrame.setVisibility(View.VISIBLE);
            seatFrameParent.setVisibility(View.VISIBLE);
            seatFrame.removeAllViews();

            final Button closeButton = (Button) findViewById(R.id.seat_close);
            closeButton.setVisibility(View.VISIBLE);
            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    seatFrame.removeAllViews();
                    seatFrame.setVisibility(GONE);
                    seatFrameParent.setVisibility(GONE);
                    title.setVisibility(GONE);
                    closeButton.setVisibility(GONE);
                    findViewById(R.id.dark).setVisibility(GONE);
                    popupCut = false;
                }
            });
            findViewById(R.id.dark).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    seatFrame.removeAllViews();
                    seatFrame.setVisibility(GONE);
                    seatFrameParent.setVisibility(GONE);
                    title.setVisibility(GONE);
                    closeButton.setVisibility(GONE);
                    findViewById(R.id.dark).setVisibility(GONE);
                }
            });

            SharedPreferences pref = getSharedPreferences("kiosk_data", MODE_WORLD_READABLE);//좌석배치도 정보는 SharedPreference에 저장됨.
            String bgMode = pref.getString("teamCode_bg" + teamCode, "seatBg01");
            switch (bgMode) {
                case "seatBg01":
                    seatFrame.setBackgroundResource(R.drawable.seat_bg01);
                    break;
                case "seatBg02":
                    seatFrame.setBackgroundResource(R.drawable.seat_bg02);
                    break;
                case "seatBg03":
                    seatFrame.setBackgroundResource(R.drawable.seat_bg03);
                    break;
            }
            Boolean presetMates = false;
            JSONObject presetMateObject = null;
            if (!pref.getString(teamCode, "none").equals("none")) {// 팀 코드가 활성화 된 상태이면
                presetMates = true;
                if (pref.getString(teamCode, "none").equals("{}"))
                    presetMates = false;
                try {
                    presetMateObject = new JSONObject(pref.getString(teamCode, "none"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            for (int i = 0; i < jsonParser.getTeamCount() + 1; i++) {//과당 팀 수
                LinearLayout newFrame = new LinearLayout(getApplicationContext());
                newFrame.setOrientation(LinearLayout.VERTICAL);
                newFrame.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                while (true) {
                    int result = sNextGeneratedId.get();
                    int nv = result + 1;
                    if (nv > 0x00FFFFFF)
                        nv = 1;
                    if (sNextGeneratedId.compareAndSet(result, nv)) {
                        newFrame.setId(result);
                        break;
                    }
                }

                seatFrame.addView(newFrame);
                Log.d("addViewCheck", "adv");
                Fragment spf;
                spf = new SeatFragmentView();
                if (presetMates) {
                    try {
                        newFrame.setX((int) (float) (presetMateObject.getJSONArray(String.valueOf(-1 - i))
                                .getJSONObject(0).getInt("x") * 1.17));
                        newFrame.setY((int) (float) (presetMateObject.getJSONArray(String.valueOf(-1 - i))
                                .getJSONObject(0).getInt("y") * 1.30));


                        final Bundle arg = new Bundle();
                        arg.putString("team", jsonParser.getTeamName(i).replace("  ", " "));
                        spf.setArguments(arg);


                    } catch (JSONException e) {
                        e.printStackTrace();
                        newFrame.setVisibility(GONE);
                    }
                    FragmentTransaction transactions = getSupportFragmentManager().beginTransaction();
                    transactions.add(newFrame.getId(), spf).commit();
                } else if (!presetMates) {
                    newFrame.setX(400);
                    newFrame.setY(420);
                    Log.d("presetMateObject_test", "is " + presetMates);
                    Log.d("addViewCheck", "else");

                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.add(newFrame.getId(), spf).commit();
                    break;
                }
                for (int j = 0; j < jsonParser.getMateCount(i) + 1; j++) {//팀당 인원 수
                    newFrame = new LinearLayout(getApplicationContext());
                    newFrame.setOrientation(LinearLayout.VERTICAL);//상관없음. 초기화용.
                    newFrame.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));//레이아웃 파라메터, width와 height를 모두 WARP_CONTENT로.

                    final int seatPos = jsonParser.getSeatPos(i, j);

                    while (true) {
                        int result = sNextGeneratedId.get(); // result가 s.NextGeneratedId.get() 로 정의됨.(즉, 1임)
                        int nv = result + 1;
                        if (nv > 0x00FFFFFF)
                            nv = 1;
                        //그동안 result는 변경되지 않았는데..
                        if (sNextGeneratedId.compareAndSet(result, nv)) {//result==sNextGeneratedId.get과 또다시 비교? 그리고, sNextGeneratedId의 값으로 nv를? !help!
                            newFrame.setId(result);//최초 result는 1, 이후 nv가 0x00FFFFFF(16진수 FFFFFF)이하인 경우, 1씩 증가하며 인덱싱..? 왜 원자적 정수값을 사용하였는가 !help!
                            break;
                        }
                    }


                    seatFrame.addView(newFrame);//에러해결. 18/12/31 09:48
                    Log.d("addViewCheck", "adv");

                    spf = new SeatFragmentView();
                    if (presetMates) {
                        try {
                            newFrame.setX((int) (float) (presetMateObject.getJSONArray(String.valueOf(jsonParser.getMemberIdentifier(i, j)))
                                    .getJSONObject(0).getInt("x") * 1.17));
                            newFrame.setY((int) (float) (presetMateObject.getJSONArray(String.valueOf(jsonParser.getMemberIdentifier(i, j)))
                                    .getJSONObject(0).getInt("y") * 1.30));


                            final Bundle arg = new Bundle();
                            arg.putInt("teamNum", i);
                            arg.putInt("id", jsonParser.getMemberIdentifier(i, j));
                            arg.putString("pic", jsonParser.getStaffPicPath(i, j));
                            arg.putString("team", jsonParser.getTeamName(i));
                            arg.putString("name", jsonParser.getName(i, j));
                            arg.putString("duty", jsonParser.getDuty(i, j));
                            spf.setArguments(arg);

                            final int finalMasterTeam = i;
                            final int finalMasterNum = j;
                            newFrame.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    try {
                                        Log.d("PopupStatus", ": " + finalMasterNum);
                                        setPopupInSeat(finalMasterTeam, finalMasterNum);
                                    } catch (NullPointerException e) {
                                        Log.d("PopupStatus", "run in anotherActivity");
                                    }
                                }
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                            newFrame.setVisibility(GONE);
                        } catch (NullPointerException e) {
                            Log.d("nullreferences", "true");
                            newFrame.setVisibility(GONE);
                        }
                    }
                    FragmentTransaction transactions = getSupportFragmentManager().beginTransaction();
                    transactions.add(newFrame.getId(), spf).commit();
                }
            }
            if (sortNo != -1) {
                final int seatPos = sortNo;
                final int i = teamNum;
                try {
                    ImageView item = new ImageView(getApplicationContext());

                    item.setImageResource(R.drawable.border);
                    item.setLayoutParams(new ViewGroup.LayoutParams(161, 97));
                    item.setX((int) (float) (presetMateObject.getJSONArray(String.valueOf(jsonParser.getMemberIdentifier(i, seatPos)))
                            .getJSONObject(0).getInt("x") * 1.17) + 6);
                    item.setY((int) (float) (presetMateObject.getJSONArray(String.valueOf(jsonParser.getMemberIdentifier(i, seatPos)))
                            .getJSONObject(0).getInt("y") * 1.30) + 8);
                    seatFrame.addView(item);
//marker
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            Log.d("presetMateObject", presetMateObject + "?");
            if (presetMateObject != null) {//가구
                int[] futitems;
                SeatPlannerActivity seatPlannerActivity = new SeatPlannerActivity();
                futitems = seatPlannerActivity.getItems();

                for (int i = 0; i < presetMateObject.length(); i++) {
                    try {
                        boolean currentData = false;
                        for (int j = 0; futitems.length > j; j++) {
                            if (futitems[j] == Integer.parseInt(presetMateObject.getJSONArray(presetMateObject.names().getString(i)).getJSONObject(0).getString("e"))) {
                                currentData = true;
                                break;
                            }
                        }
                        if (!currentData)
                            continue;

                        ImageView item = new ImageView(getApplicationContext());
                        item.setBackgroundResource(Integer.parseInt(presetMateObject.getJSONArray(presetMateObject.names().getString(i)).getJSONObject(0).getString("e")));
                        item.setTag(Integer.parseInt(presetMateObject.getJSONArray(presetMateObject.names().getString(i)).getJSONObject(0).getString("e")));
                        item.setRotation(Integer.parseInt(presetMateObject.getJSONArray(presetMateObject.names().getString(i)).getJSONObject(0).getString("r")));

                        item.setLayoutParams(new ViewGroup.LayoutParams((int) (Integer.parseInt(presetMateObject.getJSONArray(presetMateObject.names().getString(i)).getJSONObject(0).getString("w")) * 1.17),
                                (int) (Integer.parseInt(presetMateObject.getJSONArray(presetMateObject.names().getString(i)).getJSONObject(0).getString("h")) * 1.30)));

                        item.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                                v.startDrag(null, shadowBuilder, v, 0);

                                return true;
                            }
                        });
                        seatFrame.addView(item);

                        item.setX((int) (float) ((presetMateObject.getJSONArray(presetMateObject.names().getString(i)).getJSONObject(0).getInt("x")) * 1.17));
                        item.setY((int) (float) ((presetMateObject.getJSONArray(presetMateObject.names().getString(i)).getJSONObject(0).getInt("y")) * 1.30));

                        item.setId(Integer.parseInt(presetMateObject.names().getString(i)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void selectPackerMethod(int flags) {
        //flags 1 = Organization
        //flags 2 = About
        LayoutInflater vi = LayoutInflater.from(context);
        View group = vi.inflate(R.layout.org_tmp, null);
        ViewGroup itemPoint = (ViewGroup) group;
        ViewGroup groupPoint;
        String code = null;
        if (flags == 1)
            removeSearchTextFocus();
        int[] codes;
        int[] bgCodes;
        String[] strings;
        final int MAYOR_LAYER = 0;
        final int SUBMAYOR_LAYER = 1;
        final int MAYOR_DOWN_LAYER = 2;
        final int DEPARTMENT_LAYER = 3;
        final int SUBJECT_LAYER = 4;

        int[] selectPackerList = {R.id.select_packer1, R.id.select_packer2, R.id.select_packer3, R.id.select_packer4};
        group = vi.inflate(R.layout.org_tmp, null);
        Log.d("packerisRunning", "if ok");
        Log.d("selectSetString", String.valueOf(selectSetString.length));

        Iterator it = sortSelectSetString();

        int counter = 0;
        while (it.hasNext()) {
            String temp = (String) it.next();
            Log.d("packerisRunning", "for ok" + temp);
            String[] stringCodes = new String[]{temp};
            bgCodes = new int[]{MAYOR_LAYER, SUBMAYOR_LAYER};
            strings = new String[]{temp};
            if (flags == 1) {
                setSelectPacker(selectPackerList[counter], vi, stringCodes, bgCodes, strings, selectSetString.length);
            } else if (flags == 2) {
                setSelectPackerAbout(selectPackerList[counter], vi, stringCodes, bgCodes, strings, selectSetString.length);
            }
            findViewById(selectPackerList[counter]).setVisibility(VISIBLE);
            counter++;
        }
        return;
    }

    public void setOrgTab(int index, boolean selectMode) {
        autoOrganizationController();

        LayoutInflater vi = LayoutInflater.from(context);
        View group = vi.inflate(R.layout.org_tmp, null);
        ViewGroup itemPoint = (ViewGroup) group;
        ViewGroup groupPoint;
        String code = null;

        removeSearchTextFocus();
        int[] codes;
        int[] bgCodes;
        String[] strings;
        final int MAYOR_LAYER = 0;
        final int SUBMAYOR_LAYER = 1;
        final int MAYOR_DOWN_LAYER = 2;
        final int DEPARTMENT_LAYER = 3;
        final int SUBJECT_LAYER = 4;
        final int H_BAR = 5;//
        final int V_BAR = 6;
        final int P_BAR = 7;//
        final int T_BAR = 8;//
        final int RT_BAR = 9;//
        final int R_BAR = 10;//
        final int L_BAR = 11;//
        int codeLength = 0;
        LinearLayout packer1;
        RelativeLayout.LayoutParams packerParams;
        if (selectMode && !wholeMode) {
            LinearLayout backButton = (LinearLayout) findViewById(R.id.select_backbutton);
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    returnMemories();
                    FrameLayout dark = (FrameLayout) findViewById(R.id.dark);
                    ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar);
                    //dark.setVisibility(View.VISIBLE);
                    pb.setVisibility(View.VISIBLE);

                    autoOrganizationController();
                    autoFlipped = false;

                    if (wholeMode) teamCode = null;

                    releasePlayer();
                    startActivity(new Intent(MainActivity.this, MainActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("scene", "organization"));// 메인을 인텐트하여 FLAG_ACTIVITY_NEW_TASK에 scene벨류에 organization을..?
                    finish();// 조직도 시 백버튼 누르면 레이아웃 제거?
                }
            });
            selectPackerMethod(1);
            return;
        } else {
            LinearLayout backButton = (LinearLayout) findViewById(R.id.select_backbutton);
            backButton.setVisibility(GONE);
        }
        switch (index) {
            case 1:
                codes = new int[]{R.string.시장, R.string.부시장,0,  R.string.정책기획실, R.string.청렴감사실, R.string.홍보전산실};
                bgCodes = new int[]{MAYOR_LAYER, SUBMAYOR_LAYER, 999, MAYOR_DOWN_LAYER, MAYOR_DOWN_LAYER, MAYOR_DOWN_LAYER};
                strings = new String[]{"시장", "부시장", "", "정책기획실", "청렴감사실", "홍보전산실"};
                setPacker(R.id.main_packer1, vi, codes, bgCodes, strings);
                findViewById(R.id.main_packer1).setVisibility(VISIBLE);
                if (codeLength < codes.length)
                    codeLength = codes.length;

                codes = new int[]{R.string.행정지원국, R.string.총무과, R.string.인구정책과, R.string.세정과, R.string.회계과, R.string.새마을체육과, R.string.종합민원과};
                bgCodes = new int[]{DEPARTMENT_LAYER, SUBJECT_LAYER, SUBJECT_LAYER, SUBJECT_LAYER, SUBJECT_LAYER, SUBJECT_LAYER, SUBJECT_LAYER};
                strings = new String[]{"행정지원국", "총무과", "인구정책과", "세정과", "회계과", "새마을체육과", "종합민원과"};
                setPacker(R.id.main_packer2, vi, codes, bgCodes, strings);
                findViewById(R.id.main_packer2).setVisibility(VISIBLE);
                if (codeLength < codes.length)
                    codeLength = codes.length;

                codes = new int[]{R.string.경제환경산업국, R.string.일자리노사과, R.string.기업유치과, R.string.환경보호과, R.string.자원순환과, R.string.산림과};
                bgCodes = new int[]{DEPARTMENT_LAYER, SUBJECT_LAYER, SUBJECT_LAYER, SUBJECT_LAYER, SUBJECT_LAYER, SUBJECT_LAYER};
                strings = new String[]{"경제환경산업국", "일자리노사과", "기업유치과", "환경보호과", "자원순환과", "산림과"};
                setPacker(R.id.main_packer3, vi, codes, bgCodes, strings);
                findViewById(R.id.main_packer3).setVisibility(VISIBLE);
                if (codeLength < codes.length)
                    codeLength = codes.length;

                codes = new int[]{R.string.문화관광복지국, R.string.문화예술과, R.string.관광진흥과, R.string.복지정책과, R.string.사회복지과, R.string.가족행복과};
                bgCodes = new int[]{DEPARTMENT_LAYER, SUBJECT_LAYER, SUBJECT_LAYER, SUBJECT_LAYER, SUBJECT_LAYER, SUBJECT_LAYER};
                strings = new String[]{"문화관광복지국", "문화예술과", "관광진흥과", "복지정책과", "사회복지과", "가족행복과"};
                setPacker(R.id.main_packer4, vi, codes, bgCodes, strings);
                findViewById(R.id.main_packer4).setVisibility(VISIBLE);
                if (codeLength < codes.length)
                    codeLength = codes.length;

                codes = new int[]{R.string.도시건설국, R.string.도시계획과, R.string.건설과, R.string.교통행정과, R.string.안전재난하천과, R.string.건축디자인과, R.string.지적정보과};
                bgCodes = new int[]{DEPARTMENT_LAYER, SUBJECT_LAYER, SUBJECT_LAYER, SUBJECT_LAYER, SUBJECT_LAYER, SUBJECT_LAYER, SUBJECT_LAYER};
                strings = new String[]{"도시건설국", "도시계획과", "건설과", "교통행정과", "안전재난하천과", "건축디자인과", "지적정보과"};
                setPacker(R.id.main_packer5, vi, codes, bgCodes, strings);
                findViewById(R.id.main_packer5).setVisibility(VISIBLE);
                if (codeLength < codes.length)
                    codeLength = codes.length;

                //packer1 = (LinearLayout) findViewById(R.id.main_packer1);
                //packerParams = (RelativeLayout.LayoutParams) packer1.getLayoutParams();
                //packerParams.topMargin = (11-codeLength)*30;

               /*
                소스코드 복사용 샘플
                codes = new int[]{0, R.string.};
                bgCodes = new int[]{DEPARTMENT_LAYER, SUBJECT_LAYER, SUBJECT_LAYER, SUBJECT_LAYER, SUBJECT_LAYER, SUBJECT_LAYER};
                strings = new String[]{};
                setPacker(R.id.main_packer, vi, codes, bgCodes, strings);
                findViewById(R.id.main_packer).setVisibility(VISIBLE);
                if(codeLength<codes.length)
                    codeLength = codes.length;*/

                return;

            case 2:
                /*codes = new int[]{0, R.string.보건행정, R.string.예방의약, R.string.질병관리, R.string.건강증진, R.string.방문보건, R.string.출산지원};
                bgCodes = new int[]{DEPARTMENT_LAYER, SUBJECT_LAYER, SUBJECT_LAYER, SUBJECT_LAYER, SUBJECT_LAYER, SUBJECT_LAYER, SUBJECT_LAYER};
                strings = new String[]{"보건소", "보건행정", "예방의약", "질병관리", "건강증진", "방문보건", "출산지원"};
                setPacker(R.id.business_packer1, vi, codes, bgCodes, strings);
                findViewById(R.id.business_packer1).setVisibility(VISIBLE);
                if (codeLength < codes.length)
                    codeLength = codes.length;

                codes = new int[]{0, R.string.감염병관리, R.string.치매안심센터, R.string.보건위생, R.string.보건지소, R.string.보건진료소};
                bgCodes = new int[]{999, SUBJECT_LAYER, SUBJECT_LAYER, SUBJECT_LAYER, SUBJECT_LAYER, SUBJECT_LAYER};
                strings = new String[]{"", "감염병관리", "치매안심센터", "보건위생", "보건지소", "보건진료소"};
                setPacker(R.id.business_packer2, vi, codes, bgCodes, strings);
                findViewById(R.id.business_packer2).setVisibility(VISIBLE);
                if (codeLength < codes.length)
                    codeLength = codes.length;
                    */

                codes = new int[]{0, R.string.의회사무국};
                bgCodes = new int[]{DEPARTMENT_LAYER, SUBJECT_LAYER};
                strings = new String[]{"의회", "의회사무국"};
                setPacker(R.id.business_packer1, vi, codes, bgCodes, strings);
                findViewById(R.id.business_packer1).setVisibility(VISIBLE);
                if (codeLength < codes.length)
                    codeLength = codes.length;

                codes = new int[]{0, R.string.보건위생과, R.string.건강관리과};
                bgCodes = new int[]{DEPARTMENT_LAYER, SUBJECT_LAYER, SUBJECT_LAYER};
                strings = new String[]{"보건소", "보건위생과", "건강관리과"};
                setPacker(R.id.business_packer2, vi, codes, bgCodes, strings);
                findViewById(R.id.business_packer2).setVisibility(VISIBLE);
                if (codeLength < codes.length)
                    codeLength = codes.length;

                codes = new int[]{0, R.string.농업정책과, R.string.과수한방과, R.string.축산과, R.string.농촌지도과, R.string.기술지원과};
                bgCodes = new int[]{DEPARTMENT_LAYER, SUBJECT_LAYER, SUBJECT_LAYER, SUBJECT_LAYER, SUBJECT_LAYER, SUBJECT_LAYER};
                strings = new String[]{"농업기술센터", "농업정책과", "과수한방과", "축산과", "농촌지도과", "기술지원과"};
                setPacker(R.id.business_packer3, vi, codes, bgCodes, strings);
                findViewById(R.id.business_packer3).setVisibility(VISIBLE);
                if (codeLength < codes.length)
                    codeLength = codes.length;

                codes = new int[]{0, R.string.평생학습관, R.string.상수도사업소, R.string.환경사업소, R.string.체육시설사업소, R.string.공원관리사업소};
                bgCodes = new int[]{DEPARTMENT_LAYER, SUBJECT_LAYER, SUBJECT_LAYER, SUBJECT_LAYER, SUBJECT_LAYER, SUBJECT_LAYER};
                strings = new String[]{"사업소", "평생학습관", "상수도사업소", "환경사업소", "체육시설사업소", "공원관리사업소"};
                setPacker(R.id.business_packer4, vi, codes, bgCodes, strings);
                findViewById(R.id.business_packer4).setVisibility(VISIBLE);
                if (codeLength < codes.length)
                    codeLength = codes.length;

                /*packer1 = (LinearLayout) findViewById(R.id.business_packer1);
                packerParams = (RelativeLayout.LayoutParams) packer1.getLayoutParams();
                packerParams.topMargin = (11 - codeLength) * 30 + 55;*/

                return;
            case 3:
                codes = new int[]{R.string.금호읍, R.string.청통면, R.string.신녕면, R.string.화산면};
                bgCodes = new int[]{SUBJECT_LAYER, SUBJECT_LAYER, SUBJECT_LAYER, SUBJECT_LAYER, SUBJECT_LAYER};
                strings = new String[]{"금호읍", "청통면", "신녕면", "화산면"};
                setPacker(R.id.umd_packer1, vi, codes, bgCodes, strings);
                findViewById(R.id.umd_packer1).setVisibility(VISIBLE);
                if (codeLength < codes.length)
                    codeLength = codes.length;

                codes = new int[]{R.string.화북면, R.string.화남면, R.string.자양면};
                bgCodes = new int[]{SUBJECT_LAYER, SUBJECT_LAYER, SUBJECT_LAYER};
                strings = new String[]{"화북면", "화남면", "자양면"};
                setPacker(R.id.umd_packer2, vi, codes, bgCodes, strings);
                findViewById(R.id.umd_packer2).setVisibility(VISIBLE);
                if (codeLength < codes.length)
                    codeLength = codes.length;

                codes = new int[]{R.string.임고면, R.string.고경면, R.string.북안면};
                bgCodes = new int[]{SUBJECT_LAYER, SUBJECT_LAYER, SUBJECT_LAYER};
                strings = new String[]{"임고면", "고경면", "북안면"};
                setPacker(R.id.umd_packer3, vi, codes, bgCodes, strings);
                findViewById(R.id.umd_packer3).setVisibility(VISIBLE);
                if (codeLength < codes.length)
                    codeLength = codes.length;

                codes = new int[]{R.string.대창면, R.string.동부동, R.string.중앙동};
                bgCodes = new int[]{SUBJECT_LAYER, SUBJECT_LAYER, SUBJECT_LAYER};
                strings = new String[]{"대창면", "동부동", "중앙동"};
                setPacker(R.id.umd_packer4, vi, codes, bgCodes, strings);
                findViewById(R.id.umd_packer4).setVisibility(VISIBLE);
                if (codeLength < codes.length)
                    codeLength = codes.length;

                codes = new int[]{R.string.서부동, R.string.완산동, R.string.남부동};
                bgCodes = new int[]{SUBJECT_LAYER, SUBJECT_LAYER, SUBJECT_LAYER};
                strings = new String[]{"서부동", "완산동", "남부동"};
                setPacker(R.id.umd_packer5, vi, codes, bgCodes, strings);
                findViewById(R.id.umd_packer5).setVisibility(VISIBLE);
                if (codeLength < codes.length)
                    codeLength = codes.length;

                /*packer1 = (LinearLayout) findViewById(R.id.umd_packer1);
                packerParams = (RelativeLayout.LayoutParams) packer1.getLayoutParams();
                packerParams.topMargin = (11 - codeLength) * 30 + 55;*/
                return;
        }
    }

    HashMap<Integer, Bundle> teamMateHash;
    HashMap<Integer, Integer> teamLengthHash;

    public void setOrganization(){
        setOrganization(teamCode);
    }

    public void setOrganization(String teamCode) {
        Log.d("TAG", "setOrganization: orgStarted: " + teamCode);
        pageCounter = allPageCounter = 1;
        sucCount = exPage = exPageFix = pastTeam = presentMates = devideCounter = 0;
        globalSelectMode = false;
        jsonParser.parseStaffJson(teamCode);
        try {
            findViewById(R.id.keyboard_wrapper).setVisibility(GONE); // 키보드 래퍼 제거.
            searchLoadingText.setVisibility(INVISIBLE);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        try {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            Set<String> orgSelections = preferences.getStringSet("org_layout_select", null);
            for(String orgSelect : orgSelections){
                if(fullCodeHash.get(teamCode).equals(orgSelect)){
                    orgLayoutMode = true;
                }
            }

            if (orgLayoutMode == true) { // 대표자 레이아웃 설정 시( 큰 사진 )
                setContentView(R.layout.new_activity_org_mayor); // new_activity_org_mayor가 큰 사진 레이아웃.
                RelativeLayout mainActivity = (RelativeLayout) findViewById(R.id.organization_content);
                backgroundSetter(mainActivity, "organization");

                logoSetter("조직도");
                noticeBarController("organization");
                bottomButtonController("organization");

                mViewPager = (ViewPager) findViewById(R.id.container);
                orgMode = true;
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        logoSetter("조직도", 1);

        LinearLayout backButton = (LinearLayout) findViewById(R.id.backbutton);
        backButton.setVisibility(GONE);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnMemories();
                FrameLayout dark = (FrameLayout) findViewById(R.id.dark);
                ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar);
                //dark.setVisibility(View.VISIBLE);
                pb.setVisibility(View.VISIBLE);

                autoOrganizationController();
                autoFlipped = false;

                if (wholeMode)
                    MainActivity.teamCode = null;

                releasePlayer();
                startActivity(new Intent(MainActivity.this, MainActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("scene", "organization"));// 메인을 인텐트하여 FLAG_ACTIVITY_REORDER_TO_FRONT에 scene벨류에 organization을..?
                finish();// 조직도 시 백버튼 누르면 레이아웃 제거?
            }
        });

        LinearLayout seatButton = (LinearLayout) findViewById(R.id.seatbutton);

        try {
            if (wholeMode) {
                backButton.setVisibility(View.VISIBLE);
                seatButton.setVisibility(GONE);
            } else {
                backButton.setVisibility(GONE);
                seatButton.setVisibility(View.VISIBLE);
            }
            seatButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //XHApiManager xhApiManager = new XHApiManager();
                    //xhApiManager.XHShowOrHideStatusBar(true);

                    setSeatPopup(0, -1);
                }
            });
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        try {

            /*ImageView hand = (ImageView) findViewById(R.id.hand);
            hand.setVisibility(View.VISIBLE);
            Glide.with(getApplicationContext()).load(R.drawable.new_hand_gif).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(hand);
            */ // Hand 이미지(조직도 하단 gif)

            RelativeLayout slideBar = (RelativeLayout) findViewById(R.id.slide_bar);
            slideBar.setVisibility(View.VISIBLE);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        RelativeLayout lead, below = null, belowLayout = null;
        ImageView leaderPic, belowPic = null, belowBox = null, belowIsWorking = null, leaderIsWorking;
        TextView leaderTeam, leaderName, leaderDuty, leaderMission, belowTeam = null, belowName = null, belowDuty = null, belowMission = null;
        lead = (RelativeLayout) findViewById(R.id.leader);
        leaderPic = (ImageView) findViewById(R.id.leader_pic);
        leaderTeam = (TextView) findViewById(R.id.leader_team);
        leaderName = (TextView) findViewById(R.id.leader_name);
        leaderDuty = (TextView) findViewById(R.id.leader_duty);
        leaderMission = (TextView) findViewById(R.id.leader_mission);
        leaderIsWorking = (ImageView) findViewById(R.id.work_icon); // 일 하고 있는지 표시하는 아이콘.
        try {
            below = (RelativeLayout) findViewById(R.id.below);
            belowLayout = (RelativeLayout) findViewById(R.id.below_lay);
            belowPic = (ImageView) findViewById(R.id.leader_below_pic);
            belowTeam = (TextView) findViewById(R.id.below_team);
            belowName = (TextView) findViewById(R.id.leader_below_name);
            belowDuty = (TextView) findViewById(R.id.leader_below_duty);
            belowMission = (TextView) findViewById(R.id.leader_below_mission);
            belowIsWorking = (ImageView) findViewById(R.id.work_icon_);
            belowBox = (ImageView) findViewById(R.id.below_box);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        String imgSrc;
        int thirdNum = 2;
        try {
            File sdCard = Environment.getExternalStorageDirectory();
            /*
            for (int i = 0; i < jsonParser.getMateCount(0) + 1; i++) {
                if (jsonParser.getDuty(0, i).contains("국장") || jsonParser.getDuty(0, i).contains("소장")) {
                    try {
                        LinearLayout orgTitle = (LinearLayout) findViewById(R.id.org_title);
                        orgTitle.setBackgroundResource(R.drawable.new_title_1_bg);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }*/

            File f;
            int masterNum = 0;
            int belowNum = 1;
            if (jsonParser.getSeatPos(0, 0) == 0) {
                masterNum = 0;
                belowNum = 1;
            }
            else {
                if(jsonParser.getMateCount(0)!=0) {
                    masterNum = 1;
                    belowNum = 0;
                }
            }//190417
            Log.d("TAG", "setOrganization: "  + masterNum + "/" + jsonParser.getMateCount(0));

            try {
                if (jsonParser.getMates[0].length > 1) { // 메이트가 존재하면
                    f = new File(sdCard.getAbsolutePath() + "/KIOSKData/staff/" + jsonParser.getStaffPicPath(0, masterNum)); // sdCard에서 불러와 사진을 로드.
                    String duty = jsonParser.getDuty(0, masterNum); // 마스터 넘버의 직무를 불러온다. (몇 번째인지 무관.)
                    StringBuilder result = new StringBuilder();
                    for (int i = 0; i < duty.length(); i++) {
                        if (i > 0) {
                            result.append("  ");
                        } // 최초 제외, 이후 공백 2개씩 추가. 시  장 이런 식.

                        result.append(duty.charAt(i));
                    }

                    if (result.toString().length() >= 30)
                        leaderTeam.setText(result.toString().replace("  ", ""));
                    else if (result.toString().length() >= 20) {
                        leaderTeam.setText(result.toString().replace("  ", " "));
                    } else
                        leaderTeam.setText(result);

                    if (!orgMode)
                        belowTeam.setText(jsonParser.getTeamName(0));

                } else {
                    f = new File(sdCard.getAbsolutePath() + "/KIOSKData/staff/" + jsonParser.getStaffPicPath(0, masterNum));
                    leaderTeam.setText(jsonParser.getTeamName(masterNum)); // 팀명을 불러온다.
                }

                leaderName.setText(jsonParser.getName(0, masterNum)); // 리더의 이름/직무/직책 설정.
                leaderDuty.setText(jsonParser.getDuty(0, masterNum));
                leaderMission.setText(jsonParser.getMission(0, masterNum));
                if (leaderIsWorking != null)
                    setLeaderIsWorking(leaderIsWorking, masterNum);
                final int finalMasterNum = masterNum;
                final ImageView finalLeaderIsWorking = leaderIsWorking;
                lead.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setPopup(finalLeaderIsWorking, finalMasterNum);
                    }
                });

                imgSrc = jsonParser.getStaffPicPath(0, masterNum); // 마스터 넘버에 있는 사진을 파싱
                if (!imgSrc.equals("") && f.exists()) // 사진이 존재하면(파싱 결과 OK)
                    Glide.with(this).load(f).diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true).fitCenter().into(leaderPic); // Glide로 내부 저장소에서 불러온다
                else // 실패 시
                    Glide.with(this).load(R.drawable.no_img_b).fitCenter().into(leaderPic); // Glide로 실패이미지 표출.

                if (!orgMode) { // 대표자 모드가 아닐 시
                    //File f2 = new File(getResources().getString(R.string.staff_path) + jsonParser.getMemberIdentifier(0, 1) + ".jpg");
                    File f2 = new File(sdCard.getAbsolutePath() + "/KIOSKData/staff/" + jsonParser.getStaffPicPath(0, belowNum)); // 자료를 불러와
                    imgSrc = jsonParser.getStaffPicPath(0, belowNum); // 빌로우 넘버에 있는 사진을 파싱
                    if (!imgSrc.equals("") && f2.exists()) // 파싱결과 OK
                        Glide.with(this).load(f2).diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true).fitCenter().into(belowPic);
                    else // 실패
                        Glide.with(this).load(R.drawable.no_img_b).fitCenter().into(belowPic);
                    belowName.setText(jsonParser.getName(0, belowNum));
                    belowDuty.setText(jsonParser.getDuty(0, belowNum));
                    belowMission.setText(jsonParser.getMission(0, belowNum));
                    if (belowIsWorking != null)
                        setLeaderIsWorking(belowIsWorking, belowNum);

                    final int finalBelowNum = belowNum;
                    final ImageView finalBelowIsWorking = belowIsWorking;
                    belowLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            setPopup(finalBelowIsWorking, finalBelowNum);
                        }
                    });
                }
            } catch (NullPointerException e) {
                if (!orgMode && leaderName.getText().equals("No Data")) {
                    lead.setVisibility(View.INVISIBLE);
                }
                if (!orgMode && belowName.getText().equals("No Data")) {
                    below.setVisibility(View.INVISIBLE);
                    belowBox.setVisibility(View.INVISIBLE);
                }
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (!orgMode && leaderName.getText().equals("No Data")) {
                lead.setVisibility(View.INVISIBLE);
            }
            if (!orgMode) {// 대표자 모드가 아닐 시 below 전체 제거.
                below.setVisibility(View.INVISIBLE);
                belowBox.setVisibility(View.INVISIBLE);
            }
        }

        try {
            teamNum = jsonParser.getTeamCount();
            Log.d("teamNum", String.valueOf(teamNum));
            mOrganizationPagerAdapter = new OrganizationPagerAdapter(getSupportFragmentManager());

            int addableNum = 0;
            teamMateHash = new HashMap<>();
            teamLengthHash = new HashMap<>();

            for (int i = 1; i < teamNum + 1; i++) {
                int highestSeat = 0;
                try {

                    for (int j = 0; j <= jsonParser.getMateCount(i); j++)
                        if (jsonParser.getSeatPos(i, j) > highestSeat)
                            highestSeat = jsonParser.getSeatPos(i, j);

                    int teamCount = highestSeat;
                    int subNumber = 0;
                    if (teamCount > 16) {
                        subNumber = teamCount;
                        teamLengthHash.put(addableNum, 10);
                        Log.d("MainActivity", "teamLengthHash : " + addableNum + " = 10");
                        subNumber -= 10;
                        addableNum += 1;
                        try {
                            for (int j = 0; j < 10; j++) {
                                if (subNumber < 10)
                                    break;
                                teamLengthHash.put(addableNum, 10);
                                Log.d("MainActivity", "teamLengthHash : " + addableNum + " = 10");
                                addableNum += 1;
                                subNumber -= 10;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Log.d("subnumber", String.valueOf(subNumber));
                        if (subNumber != 0) {
                            if (subNumber <= 6 && subNumber != 0) {
                                teamLengthHash.put(addableNum - 1, 8);
                                Log.d("MainActivity", "teamLengthHash : " + addableNum + " = 8?");
                                teamLengthHash.put(addableNum, 8);
                                Log.d("MainActivity", "teamLengthHash : " + addableNum + " = 8?");
                                addableNum += 1;
                            } else if (subNumber <= 8) {
                                teamLengthHash.put(addableNum, 8);
                                Log.d("MainActivity", "teamLengthHash : " + addableNum + " = 8?");
                                addableNum += 1;
                            } else {
                                teamLengthHash.put(addableNum, 10);
                                Log.d("MainActivity", "teamLengthHash : " + addableNum + " = 10?");
                                addableNum += 1;
                            }
                        }
                    } else {
                        if (teamCount <= 8) {
                            teamLengthHash.put(addableNum, 8);
                            Log.d("MainActivity", "teamLengthHash : " + addableNum + " = 8");
                            addableNum += 1;
                        } else if (teamCount <= 10) {
                            teamLengthHash.put(addableNum, 10);
                            Log.d("MainActivity", "teamLengthHash : " + addableNum + " = 10");
                            addableNum += 1;
                        } else {
                            teamLengthHash.put(addableNum, 8);
                            addableNum += 1;
                            teamLengthHash.put(addableNum, 8);
                            Log.d("MainActivity", "teamLengthHash : " + addableNum + " = 8, 8");
                            addableNum += 1;

                        }
                    }

                    highestHash.put(i, highestSeat);
                    swHash.put(i, subNumber);
                    for (int j = 0; j < teamLengthHash.size(); j++) {
                        Log.d("MainActivity", "teamLengthHashCounts " + j + " : " + teamLengthHash.get(j));
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            }//marker


/*
        int tmpTeamNum = teamNum;
        for (int i = 0; i < teamNum; i++) {
            if (jsonParser.getMates[i] == null || jsonParser.getMates[i][0] == null)
                tmpTeamNum--;
            if (highestHash.get(i) % 6 == 0 && (float)highestHash.get(i) / 6 > 1)
                tmpTeamNum--;
        }
        teamNum = tmpTeamNum;
        */
            teamNum = teamLengthHash.size();
            Log.d("MainActivity", "addableNum = " + addableNum);
            if (teamNum <= 2 && !orgMode)
                teamNum = 3;
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        ImageView viewpagerShadow = (ImageView) findViewById(R.id.viewpager_shadow);
        if (orgMode && teamNum == 1) {
            RelativeLayout leader = (RelativeLayout) findViewById(R.id.leader);
            leader.setX(leader.getX() + 190);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) viewpagerShadow.getLayoutParams();
            layoutParams.leftMargin = 190;
            viewpagerShadow.setLayoutParams(layoutParams);
            mViewPager.setX(mViewPager.getX() + 190);
            mOrganizationPagerAdapter.setPageWidth(1);
        } else if (orgMode) {
            RelativeLayout leader = (RelativeLayout) findViewById(R.id.leader);
            leader.setX(leader.getX() + 90);

            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) viewpagerShadow.getLayoutParams();
            layoutParams.leftMargin = 180;
            viewpagerShadow.setLayoutParams(layoutParams);

            mViewPager.setX(mViewPager.getX() + 180);
            mOrganizationPagerAdapter.setPageWidth(.5f);
        } /*else if (teamNum < 4) {
            teamNum++;
        }??무슨 이유로 teamNum 증가시키지??*/

        if (teamNum == 2) {
            mOrganizationPagerAdapter.setPageWidth(.5f);
            mViewPager.requestLayout();
        }

        try {
            mOrganizationPagerAdapter.setTeamNum(teamNum);
            mViewPager.setOffscreenPageLimit(teamNum);
            mViewPager.setAdapter(mOrganizationPagerAdapter);
            setViewPagerScrollSpeed(mViewPager, 2);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        final boolean[] direction = {false};
        final Handler handler = new Handler();

        final Runnable Update = new Runnable() {
            public void run() {
                try {
                    pagerCounter = pagerMovement(direction, mViewPager.getCurrentItem(), pagerCounter);
                    Log.d("AutoPageFlipper", pagerCounter + " pagerCounter");
                } catch (NullPointerException e) {
                    Log.d("NullPager", "Null");
                }
            }
        };

        ///Auto organization slide timer
        if (timer != null)
            timer.cancel();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, DELAY_MS - 8000, PERIOD_MS - 8000);

        //viewpager 좌측 그림자 제어
        final ImageView shadow = (ImageView) findViewById(R.id.viewpager_shadow);
        try {
            mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    try {
                        if (!fading) {
                            if (mViewPager.getScrollX() <= 10 && shadow.getVisibility() == VISIBLE) {
                                fading = true;
                                YoYo.with(Techniques.FadeOut).duration(300).onEnd(new YoYo.AnimatorCallback() {
                                    @Override
                                    public void call(Animator animator) {
                                        shadow.setVisibility(INVISIBLE);
                                        fading = false;
                                    }
                                }).playOn(shadow);
                            } else if (mViewPager.getScrollX() > 10 && shadow.getVisibility() == INVISIBLE) {
                                fading = true;
                                shadow.setVisibility(View.VISIBLE);
                                YoYo.with(Techniques.FadeIn).duration(300).onEnd(new YoYo.AnimatorCallback() {
                                    @Override
                                    public void call(Animator animator) {
                                        fading = false;
                                    }
                                }).playOn(shadow);
                            }
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onPageSelected(int position) {
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        ///When app get touch, kill timer to stop auto sliding
        mViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                autoOrganizationController();
                setViewPagerScrollSpeed(mViewPager, 2);

                if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
                    timer.cancel();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            handler.post(Update);
                        }
                    }, DELAY_MS - 8000, PERIOD_MS - 8000);
                }

                return false;
            }
        });
    }

    boolean fading = false;

    /// Popup screen setter

    public void setPopup(final ImageView leaderIsWorking, final int mate) {
        autoOrganizationController();
        if (popupCut)
            return;

        final String[] passwordInput = new String[1]; // Password input of popup menu
        final FrameLayout dark = (FrameLayout) findViewById(R.id.dark);// 어두워 지는 프레임
        final RelativeLayout popup = (RelativeLayout) findViewById(R.id.popup);

        dark.setVisibility(View.VISIBLE);
        popup.setVisibility(View.INVISIBLE);

        final ImageView popupPic = (ImageView) findViewById(R.id.popup_pic);
        final TextView popupName = (TextView) findViewById(R.id.popup_name);
        final TextView popupDuty = (TextView) findViewById(R.id.popup_duty);
        final TextView popupPhone = (TextView) findViewById(R.id.popup_phone);
        final TextView popupMail = (TextView) findViewById(R.id.popup_mail);
        final TextView popupMission = (TextView) findViewById(R.id.popup_mission);
        final Button popupClose = (Button) findViewById(R.id.popup_close); // 팝업 내용들.

        if (popupTimer != null)
            popupTimer.cancel();
        popupTimer = new Timer();

        final Techniques in, out;
        in = Techniques.BounceInDown;
        out = Techniques.SlideOutUp;

        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                dark.setOnClickListener(null);
                popupClose.setOnClickListener(null);

                YoYo.with(out).duration(500).onEnd(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) { // 애니메이션 위에서 아래로 요요처럼 떨어지는 것.
                        popupTimer.cancel(); // 중간에 취소할 경우 타이머 초기화
                        dark.setVisibility(GONE);// 어두워지기 사라지고
                        popup.setVisibility(GONE);// 팝업도 사라짐.
                        popupPhone.setVisibility(View.VISIBLE);  //나머지는 그대로 한 뒤 팝업 위로 사라지게.
                        popupMail.setVisibility(View.VISIBLE);
                        popupMission.setVisibility(View.VISIBLE);
                        autoOrganizationController();
                    }
                }).playOn(popup);
            }
        };
        popupTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update); // update 스레드 실행.(메세지 전송)
            }
        }, AUTO_POPUP_CLOSE_MS, AUTO_POPUP_CLOSE_MS);//최초 10초, 이후 10초 반복.


        popup.setOnClickListener(null);
        dark.setOnClickListener(new View.OnClickListener() { // 어두운 부분 클릭하면
            @Override
            public void onClick(View v) {
                dark.setOnClickListener(null);

                YoYo.with(out).duration(500).onEnd(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        popupTimer.cancel();
                        dark.setVisibility(GONE);
                        popup.setVisibility(GONE);
                        popupPhone.setVisibility(View.VISIBLE);
                        popupMail.setVisibility(View.VISIBLE);
                        popupMission.setVisibility(View.VISIBLE);
                        autoOrganizationController();
                    }
                }).playOn(popup); // 위와 같이 사라짐.
            }
        });
        popupClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // 닫기 버튼 눌러도
                popupClose.setOnClickListener(null);

                YoYo.with(out).duration(500).onEnd(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        popupTimer.cancel();
                        dark.setVisibility(GONE);
                        popup.setVisibility(GONE);
                        popupPhone.setVisibility(View.VISIBLE);
                        popupMail.setVisibility(View.VISIBLE);
                        popupMission.setVisibility(View.VISIBLE);
                        autoOrganizationController();
                    }
                }).playOn(popup); // 위와 같이 사라짐.
            }
        });

        File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/KIOSKData/staff/" + jsonParser.getStaffPicPath(0, mate)); // 리더의 사진 불러옴.

        String imgSrc = jsonParser.getStaffPicPath(0, mate);
        if (!imgSrc.equals("") && f.exists())//파일이름이 공백이 아니고 존재하는 경우.
            Glide.with(this).load(f).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).fitCenter().into(new ViewTarget<ImageView, GlideDrawable>(popupPic) {
                @Override
                public void onResourceReady(GlideDrawable resource, GlideAnimation anim) {// 리소스가 준비되면 사진 표출.
                    ImageView myView = this.view;
                    myView.setImageDrawable(resource);

                    popup.setVisibility(View.VISIBLE);
                    YoYo.with(in).duration(700).playOn(popup); // Marker
                }
            });
        else // 존재하지 않으면
            Glide.with(this).load(R.drawable.no_img_b).fitCenter().into(new ViewTarget<ImageView, GlideDrawable>(popupPic) {// 이미지 준비중을 띄운다.
                @Override
                public void onResourceReady(GlideDrawable resource, GlideAnimation anim) {
                    ImageView myView = this.view;
                    myView.setImageDrawable(resource);

                    popup.setVisibility(View.VISIBLE);
                    YoYo.with(in).duration(700).playOn(popup);
                }
            });

        if (typefacenanumsqareeb == null) {
            typefacenanumsqareeb = Typeface.createFromAsset(getAssets(), "nanumsquareeb.ttf");
        }
        popupName.setText(jsonParser.getName(0, mate));
        popupName.setTypeface(typefacenanumsqareeb);
        popupDuty.setText(jsonParser.getDuty(0, mate));
        popupDuty.setTypeface(typefacenanumsqareeb);
        popupPhone.setText(jsonParser.getTel(0, mate));
        popupPhone.setTypeface(typefacenanumsqareeb);
        popupMail.setText(jsonParser.getMail(0, mate));
        popupMail.setTypeface(typefacenanumsqareeb);
        popupMission.setText(jsonParser.getMission(0, mate)); // 순서가 중요치 않음. (애니메이션 시작부부터 설정하기 시작하기 때문..)
        popupMission.setTypeface(typefacenanumsqareeb);
        findViewById(R.id.popup_call).setVisibility(VISIBLE);
        if (wholeMode)
            findViewById(R.id.popup_call).setVisibility(GONE);

        try {
            findViewById(R.id.popup_call).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { // 향후 개발 과제. (marker)
                    /*new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Socket socket = new Socket();
                                SocketAddress socketAddress = new InetSocketAddress(SocketClient.address + SocketClient.adSuccess, 12333); // 소켓통신
                                socket.connect(socketAddress);
                                BufferedWriter networkWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));


                                String input = jsonParser.getName(0, mate) + " " + jsonParser.getDuty(0, mate);
                                if (!input.equals("")) {
                                    Log.d("ClientThread", "sended : " + input);
                                    networkWriter.write(input);
                                    networkWriter.newLine();
                                    networkWriter.flush();
                                }

                                Thread.sleep(100);
                            } catch (Exception e) {
                                e.printStackTrace();
                                return;
                            }
                        }
                    }).start();*/


                    //marker

                    setSeatPopup(0, mate);

                    popupClose.setOnClickListener(null);
                    YoYo.with(out).duration(500).onEnd(new YoYo.AnimatorCallback() {
                        @Override
                        public void call(Animator animator) {
                            popupTimer.cancel();
                            dark.setVisibility(GONE);
                            popup.setVisibility(GONE);
                            popupPhone.setVisibility(View.VISIBLE);
                            popupMail.setVisibility(View.VISIBLE);
                            popupMission.setVisibility(View.VISIBLE);
                        }
                    }).playOn(popup);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

        final TextView popupWorking = (TextView) findViewById(R.id.working);
        final TextView popupVacation = (TextView) findViewById(R.id.vacation);
        final TextView popupTrip = (TextView) findViewById(R.id.trip);
        final TextView popupEducation = (TextView) findViewById(R.id.education);

        popupWorking.setBackgroundResource(R.drawable.new_status_1_off); // 초기화 한 뒤
        popupVacation.setBackgroundResource(R.drawable.new_status_2_off);
        popupTrip.setBackgroundResource(R.drawable.new_status_3_off);
        popupEducation.setBackgroundResource(R.drawable.new_status_4_off);

        switch (jsonParser.getIsWorking(0, mate)) { // 상태에 따라 설정 변경
            case 1:
                popupWorking.setBackgroundResource(R.drawable.new_status_1_on);
                break;
            case 2:
                popupVacation.setBackgroundResource(R.drawable.new_status_2_on);
                break;
            case 3:
                popupTrip.setBackgroundResource(R.drawable.new_status_3_on);
                break;
            case 4:
                popupEducation.setBackgroundResource(R.drawable.new_status_4_on);
                break;
        }

        final String password = jsonParser.getPassword();
        final RelativeLayout passwordLayout = (RelativeLayout) findViewById(R.id.passwordLayout);
        passwordLayout.setVisibility(GONE);

        final TextView passwordText = (TextView) findViewById(R.id.password_field);
        final TextView p1 = (TextView) findViewById(R.id.p1);
        final TextView p2 = (TextView) findViewById(R.id.p2);
        final TextView p3 = (TextView) findViewById(R.id.p3);
        final TextView p4 = (TextView) findViewById(R.id.p4);
        final TextView p5 = (TextView) findViewById(R.id.p5);
        final TextView p6 = (TextView) findViewById(R.id.p6);
        final TextView p7 = (TextView) findViewById(R.id.p7);
        final TextView p8 = (TextView) findViewById(R.id.p8);
        final TextView p9 = (TextView) findViewById(R.id.p9);
        final TextView p0 = (TextView) findViewById(R.id.p0);
        final TextView undo = (TextView) findViewById(R.id.undo);
        final TextView ok = (TextView) findViewById(R.id.ok);
        final TextView cancel = (TextView) findViewById(R.id.cancel);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupPhone.setVisibility(View.VISIBLE);
                popupMail.setVisibility(View.VISIBLE);
                popupMission.setVisibility(View.VISIBLE);
                passwordLayout.setVisibility(GONE);

                popupWorking.setBackgroundResource(R.drawable.new_status_1_off);
                popupVacation.setBackgroundResource(R.drawable.new_status_2_off);
                popupTrip.setBackgroundResource(R.drawable.new_status_3_off);
                popupEducation.setBackgroundResource(R.drawable.new_status_4_off);

                switch (jsonParser.getIsWorking(0, mate)) { // 현 상태에 따라서 점등. ( 이미지 교체 )
                    case 1:
                        popupWorking.setBackgroundResource(R.drawable.new_status_1_on);
                        break;
                    case 2:
                        popupVacation.setBackgroundResource(R.drawable.new_status_2_on);
                        break;
                    case 3:
                        popupTrip.setBackgroundResource(R.drawable.new_status_3_on);
                        break;
                    case 4:
                        popupEducation.setBackgroundResource(R.drawable.new_status_4_on);
                        break;
                }

                if (popupTimer != null) // 다 확인 뒤 타이머가 있으면 캔슬.
                    popupTimer.cancel();
                popupTimer = new Timer(); // 새 타이머 생성
                final Handler handler = new Handler();
                final Runnable Update = new Runnable() {
                    public void run() {
                        dark.setVisibility(GONE);
                        popup.setVisibility(GONE);
                        popupPhone.setVisibility(View.VISIBLE);
                        popupMail.setVisibility(View.VISIBLE);
                        popupMission.setVisibility(View.VISIBLE);
                    }
                };
                popupTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        handler.post(Update);
                    }
                }, AUTO_POPUP_CLOSE_MS, AUTO_POPUP_CLOSE_MS);
            }
        });

        View.OnTouchListener passwordTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                    v.setBackgroundResource(R.drawable.new_num_bg_over);
                else
                    v.setBackgroundResource(R.drawable.new_num_out);
                return false;
            }
        };
        View.OnClickListener passwordButtonListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupTimer != null)
                    popupTimer.cancel();
                popupTimer = new Timer();
                final Handler handler = new Handler();
                final Runnable Update = new Runnable() {
                    public void run() {
                        dark.setVisibility(GONE);
                        popup.setVisibility(GONE);
                        popupPhone.setVisibility(View.VISIBLE);
                        popupMail.setVisibility(View.VISIBLE);
                        popupMission.setVisibility(View.VISIBLE);
                    }
                };
                popupTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        handler.post(Update);
                    }
                }, AUTO_POPUP_CLOSE_MS, AUTO_POPUP_CLOSE_MS);

                if (v == p1)
                    passwordInput[0] += "1";
                else if (v == p2)
                    passwordInput[0] += "2";
                else if (v == p3)
                    passwordInput[0] += "3";
                else if (v == p4)
                    passwordInput[0] += "4";
                else if (v == p5)
                    passwordInput[0] += "5";
                else if (v == p6)
                    passwordInput[0] += "6";
                else if (v == p7)
                    passwordInput[0] += "7";
                else if (v == p8)
                    passwordInput[0] += "8";
                else if (v == p9)
                    passwordInput[0] += "9";
                else if (v == p0)
                    passwordInput[0] += "0";
                else if (v == undo)
                    if (!passwordInput[0].equals(""))
                        passwordInput[0] = passwordInput[0].substring(0, passwordInput[0].length() - 1);

                String stars = "";
                for (int i = 0; i < passwordInput[0].length(); i++) {
                    stars += "*";
                }
                passwordText.setText(stars);
            }
        };

        p1.setOnClickListener(passwordButtonListener);
        p2.setOnClickListener(passwordButtonListener);
        p3.setOnClickListener(passwordButtonListener);
        p4.setOnClickListener(passwordButtonListener);
        p5.setOnClickListener(passwordButtonListener);
        p6.setOnClickListener(passwordButtonListener);
        p7.setOnClickListener(passwordButtonListener);
        p8.setOnClickListener(passwordButtonListener);
        p9.setOnClickListener(passwordButtonListener);
        p0.setOnClickListener(passwordButtonListener);
        undo.setOnClickListener(passwordButtonListener);

        p1.setOnTouchListener(passwordTouchListener);
        p2.setOnTouchListener(passwordTouchListener);
        p3.setOnTouchListener(passwordTouchListener);
        p4.setOnTouchListener(passwordTouchListener);
        p5.setOnTouchListener(passwordTouchListener);
        p6.setOnTouchListener(passwordTouchListener);
        p7.setOnTouchListener(passwordTouchListener);
        p8.setOnTouchListener(passwordTouchListener);
        p9.setOnTouchListener(passwordTouchListener);
        p0.setOnTouchListener(passwordTouchListener);
        //undo.setOnTouchListener(passwordTouchListener);

        tmp = 0;


        View.OnClickListener isWorkingButtonListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) { // 하단부 근무중 등.. 을 표시하는 버튼에 대한 리스너.
                if (popupTimer != null)
                    popupTimer.cancel();
                popupTimer = new Timer();
                final Handler handler = new Handler();
                final Runnable Update = new Runnable() {
                    public void run() {
                        dark.setVisibility(GONE);
                        popup.setVisibility(GONE);
                        popupPhone.setVisibility(View.VISIBLE);
                        popupMail.setVisibility(View.VISIBLE);
                        popupMission.setVisibility(View.VISIBLE);
                    }
                };
                popupTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        handler.post(Update);
                    }
                }, AUTO_POPUP_CLOSE_MS, AUTO_POPUP_CLOSE_MS);

                popupWorking.setBackgroundResource(R.drawable.new_status_1_off);
                popupVacation.setBackgroundResource(R.drawable.new_status_2_off);
                popupTrip.setBackgroundResource(R.drawable.new_status_3_off);
                popupEducation.setBackgroundResource(R.drawable.new_status_4_off);
                passwordLayout.setVisibility(View.VISIBLE);
                passwordText.setText("");

                if (v == popupWorking) {
                    popupWorking.setBackgroundResource(R.drawable.new_status_1_on);
                    tmp = 1;

                } else if (v == popupVacation) {
                    popupVacation.setBackgroundResource(R.drawable.new_status_2_on);
                    tmp = 2;

                } else if (v == popupTrip) {
                    popupTrip.setBackgroundResource(R.drawable.new_status_3_on);
                    tmp = 3;

                } else if (v == popupEducation) {
                    popupEducation.setBackgroundResource(R.drawable.new_status_4_on);
                    tmp = 4;

                } else if (v == ok) { // ok버튼 누를 시
                    if (passwordInput[0].equals(password) || (passwordInput[0].equals("") && password.equals("null"))) {
                        jsonParser.setIsWorking(0, mate, tmp);
                        sendBroadcast(new Intent("memberStatus")
                                .putExtra("member", jsonParser.getMemberIdentifier(0, mate))
                                .putExtra("status", tmp)
                                .putExtra("LocalFlag", false));
                        SharedPreferences pref = getSharedPreferences("kiosk_data", MODE_PRIVATE);
                        SharedPreferences.Editor edit = pref.edit();
                        edit.putInt(jsonParser.getMemberIdentifier(0, mate) + "memberStatus", tmp).commit();
                        Log.d("memberIdentifierLog", jsonParser.getMemberIdentifier(0, mate) + "memberStatus" + " " + tmp);
                        dark.setVisibility(GONE);
                        popup.setVisibility(GONE);
                        popupPhone.setVisibility(View.VISIBLE);
                        popupMail.setVisibility(View.VISIBLE);
                        popupMission.setVisibility(View.VISIBLE);
                    }
                }

                if (jsonParser.getIsWorking(0, mate) == tmp) {
                    passwordLayout.setVisibility(GONE);
                    popupPhone.setVisibility(View.VISIBLE);
                    popupMail.setVisibility(View.VISIBLE);
                    popupMission.setVisibility(View.VISIBLE);
                }

                passwordInput[0] = "";
                setLeaderIsWorking(leaderIsWorking, mate, tmp);//marker
            }
        };

        popupWorking.setOnClickListener(isWorkingButtonListener);
        popupVacation.setOnClickListener(isWorkingButtonListener);
        popupTrip.setOnClickListener(isWorkingButtonListener);
        popupEducation.setOnClickListener(isWorkingButtonListener);
        ok.setOnClickListener(isWorkingButtonListener);

        StringBuffer data;
        FileInputStream fis;
        BufferedReader buffer;
        String str;

        File dir;
        File file;
        FileWriter fw;
        try {
            data = new StringBuffer();
            JSONArray wholeArray = new JSONArray();
            try {
                fis = new FileInputStream(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/KIOSKData", "KIOSK_MEMBER_LOG.json")); // 멤버 로그에 삽입.
                buffer = new BufferedReader(new InputStreamReader(fis, "utf-8"));
                str = buffer.readLine();
                while (str != null) {
                    data.append(str + "\n");
                    str = buffer.readLine();
                }
                wholeArray = new JSONArray(data.toString()); // 데이터를 읽어와 JSONArray에 삽입.
            } catch (Exception e) {
                e.printStackTrace();
            }

            JSONObject memberObject = new JSONObject();
            memberObject.put("time", Calendar.getInstance().getTime());
            memberObject.put("id", jsonParser.getMemberIdentifier(0, mate));
            memberObject.put("dept", jsonParser.getDeptName(0, mate));
            memberObject.put("team", jsonParser.getTeamName(0).replace(" ", ""));
            memberObject.put("name", jsonParser.getName(0, mate));
            memberObject.put("duty", jsonParser.getDuty(0, mate));

            wholeArray.put(memberObject); // JSONArray에 추가 정보 삽입 후

            dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/KIOSKData");
            file = new File(dir, "KIOSK_MEMBER_LOG.json");
            fw = new FileWriter(file);
            fw.write(wholeArray.toString());
            fw.close();//저장하고 닫는다.
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setPopupInSeat(final int team, final int mate) {
        autoOrganizationController();

        final String[] passwordInput = new String[1]; // Password input of popup menu
        final FrameLayout dark = (FrameLayout) findViewById(R.id.dark);// 어두워 지는 프레임
        final RelativeLayout popup = (RelativeLayout) findViewById(R.id.popup);

        dark.setVisibility(View.VISIBLE);
        popup.setVisibility(View.INVISIBLE);

        final ImageView popupPic = (ImageView) findViewById(R.id.popup_pic);
        final TextView popupName = (TextView) findViewById(R.id.popup_name);
        final TextView popupDuty = (TextView) findViewById(R.id.popup_duty);
        final TextView popupPhone = (TextView) findViewById(R.id.popup_phone);
        final TextView popupMail = (TextView) findViewById(R.id.popup_mail);
        final TextView popupMission = (TextView) findViewById(R.id.popup_mission);
        final Button popupClose = (Button) findViewById(R.id.popup_close); // 팝업 내용들.

        if (popupTimer != null)
            popupTimer.cancel();
        popupTimer = new Timer();

        final Techniques in, out;
        in = Techniques.BounceInDown;
        out = Techniques.SlideOutUp;

        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                dark.setOnClickListener(null);
                popupClose.setOnClickListener(null);

                YoYo.with(out).duration(500).onEnd(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) { // 애니메이션 위에서 아래로 요요처럼 떨어지는 것.
                        popupTimer.cancel(); // 중간에 취소할 경우 타이머 초기화
                        dark.setVisibility(GONE);// 어두워지기 사라지고
                        popup.setVisibility(GONE);// 팝업도 사라짐.
                        popupPhone.setVisibility(View.VISIBLE);  //나머지는 그대로 한 뒤 팝업 위로 사라지게.
                        popupMail.setVisibility(View.VISIBLE);
                        popupMission.setVisibility(View.VISIBLE);
                        autoOrganizationController();
                    }
                }).playOn(popup);
            }
        };
        popupTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update); // update 스레드 실행.(메세지 전송)
            }
        }, AUTO_POPUP_CLOSE_MS, AUTO_POPUP_CLOSE_MS);//최초 10초, 이후 10초 반복.


        popup.setOnClickListener(null);
        dark.setOnClickListener(new View.OnClickListener() { // 어두운 부분 클릭하면
            @Override
            public void onClick(View v) {
                dark.setOnClickListener(null);

                YoYo.with(out).duration(500).onEnd(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        popupTimer.cancel();
                        dark.setVisibility(GONE);
                        popup.setVisibility(GONE);
                        popupPhone.setVisibility(View.VISIBLE);
                        popupMail.setVisibility(View.VISIBLE);
                        popupMission.setVisibility(View.VISIBLE);
                        autoOrganizationController();
                    }
                }).playOn(popup); // 위와 같이 사라짐.
            }
        });
        popupClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // 닫기 버튼 눌러도
                popupClose.setOnClickListener(null);

                YoYo.with(out).duration(500).onEnd(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        popupTimer.cancel();
                        dark.setVisibility(GONE);
                        popup.setVisibility(GONE);
                        popupPhone.setVisibility(View.VISIBLE);
                        popupMail.setVisibility(View.VISIBLE);
                        popupMission.setVisibility(View.VISIBLE);
                        autoOrganizationController();
                    }
                }).playOn(popup); // 위와 같이 사라짐.
            }
        });

        File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/KIOSKData/staff/" + jsonParser.getStaffPicPath(team, mate)); // 리더의 사진 불러옴.

        String imgSrc = jsonParser.getStaffPicPath(team, mate);
        if (!imgSrc.equals("") && f.exists())//파일이름이 공백이 아니고 존재하는 경우.
            Glide.with(this).load(f).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).fitCenter().into(new ViewTarget<ImageView, GlideDrawable>(popupPic) {
                @Override
                public void onResourceReady(GlideDrawable resource, GlideAnimation anim) {// 리소스가 준비되면 사진 표출.
                    ImageView myView = this.view;
                    myView.setImageDrawable(resource);

                    popup.setVisibility(View.VISIBLE);
                    YoYo.with(in).duration(700).playOn(popup); // Marker
                }
            });
        else // 존재하지 않으면
            Glide.with(this).load(R.drawable.no_img_b).fitCenter().into(new ViewTarget<ImageView, GlideDrawable>(popupPic) {// 이미지 준비중을 띄운다.
                @Override
                public void onResourceReady(GlideDrawable resource, GlideAnimation anim) {
                    ImageView myView = this.view;
                    myView.setImageDrawable(resource);

                    popup.setVisibility(View.VISIBLE);
                    YoYo.with(in).duration(700).playOn(popup);
                }
            });

        popupName.setText(jsonParser.getName(team, mate));
        popupDuty.setText(jsonParser.getDuty(team, mate));
        popupPhone.setText(jsonParser.getTel(team, mate));
        popupMail.setText(jsonParser.getMail(team, mate));
        popupMission.setText(jsonParser.getMission(team, mate)); // 순서가 중요치 않음. (애니메이션 시작부부터 설정하기 시작하기 때문..)
        findViewById(R.id.popup_call).setVisibility(INVISIBLE);

        try {
            findViewById(R.id.popup_call).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { // 향후 개발 과제. (marker)
                    setSeatPopup(0, mate);


                    popupClose.setOnClickListener(null);
                    YoYo.with(out).duration(500).onEnd(new YoYo.AnimatorCallback() {
                        @Override
                        public void call(Animator animator) {
                            popupTimer.cancel();
                            dark.setVisibility(GONE);
                            popup.setVisibility(GONE);
                            popupPhone.setVisibility(View.VISIBLE);
                            popupMail.setVisibility(View.VISIBLE);
                            popupMission.setVisibility(View.VISIBLE);
                        }
                    }).playOn(popup);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        final TextView popupWorking = (TextView) findViewById(R.id.working);
        final TextView popupVacation = (TextView) findViewById(R.id.vacation);
        final TextView popupTrip = (TextView) findViewById(R.id.trip);
        final TextView popupEducation = (TextView) findViewById(R.id.education);

        popupWorking.setBackgroundResource(R.drawable.new_status_1_off); // 초기화 한 뒤
        popupVacation.setBackgroundResource(R.drawable.new_status_2_off);
        popupTrip.setBackgroundResource(R.drawable.new_status_3_off);
        popupEducation.setBackgroundResource(R.drawable.new_status_4_off);

        switch (jsonParser.getIsWorking(team, mate)) { // 상태에 따라 설정 변경
            case 1:
                popupWorking.setBackgroundResource(R.drawable.new_status_1_on);
                break;
            case 2:
                popupVacation.setBackgroundResource(R.drawable.new_status_2_on);
                break;
            case 3:
                popupTrip.setBackgroundResource(R.drawable.new_status_3_on);
                break;
            case 4:
                popupEducation.setBackgroundResource(R.drawable.new_status_4_on);
                break;
        }

        final String password = jsonParser.getPassword();
        final RelativeLayout passwordLayout = (RelativeLayout) findViewById(R.id.passwordLayout);
        passwordLayout.setVisibility(GONE);

        final TextView passwordText = (TextView) findViewById(R.id.password_field);
        final TextView p1 = (TextView) findViewById(R.id.p1);
        final TextView p2 = (TextView) findViewById(R.id.p2);
        final TextView p3 = (TextView) findViewById(R.id.p3);
        final TextView p4 = (TextView) findViewById(R.id.p4);
        final TextView p5 = (TextView) findViewById(R.id.p5);
        final TextView p6 = (TextView) findViewById(R.id.p6);
        final TextView p7 = (TextView) findViewById(R.id.p7);
        final TextView p8 = (TextView) findViewById(R.id.p8);
        final TextView p9 = (TextView) findViewById(R.id.p9);
        final TextView p0 = (TextView) findViewById(R.id.p0);
        final TextView undo = (TextView) findViewById(R.id.undo);
        final TextView ok = (TextView) findViewById(R.id.ok);
        final TextView cancel = (TextView) findViewById(R.id.cancel);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupPhone.setVisibility(View.VISIBLE);
                popupMail.setVisibility(View.VISIBLE);
                popupMission.setVisibility(View.VISIBLE);
                passwordLayout.setVisibility(GONE);

                popupWorking.setBackgroundResource(R.drawable.new_status_1_off);
                popupVacation.setBackgroundResource(R.drawable.new_status_2_off);
                popupTrip.setBackgroundResource(R.drawable.new_status_3_off);
                popupEducation.setBackgroundResource(R.drawable.new_status_4_off);

                switch (jsonParser.getIsWorking(team, mate)) { // 현 상태에 따라서 점등. ( 이미지 교체 )
                    case 1:
                        popupWorking.setBackgroundResource(R.drawable.new_status_1_on);
                        break;
                    case 2:
                        popupVacation.setBackgroundResource(R.drawable.new_status_2_on);
                        break;
                    case 3:
                        popupTrip.setBackgroundResource(R.drawable.new_status_3_on);
                        break;
                    case 4:
                        popupEducation.setBackgroundResource(R.drawable.new_status_4_on);
                        break;
                }

                if (popupTimer != null) // 다 확인 뒤 타이머가 있으면 캔슬.
                    popupTimer.cancel();
                popupTimer = new Timer(); // 새 타이머 생성
                final Handler handler = new Handler();
                final Runnable Update = new Runnable() {
                    public void run() {
                        dark.setVisibility(GONE);
                        popup.setVisibility(GONE);
                        popupPhone.setVisibility(View.VISIBLE);
                        popupMail.setVisibility(View.VISIBLE);
                        popupMission.setVisibility(View.VISIBLE);
                    }
                };
                popupTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        handler.post(Update);
                    }
                }, AUTO_POPUP_CLOSE_MS, AUTO_POPUP_CLOSE_MS);
            }
        });

        View.OnTouchListener passwordTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                    v.setBackgroundResource(R.drawable.new_num_bg_over);
                else
                    v.setBackgroundResource(R.drawable.new_num_out);
                return false;
            }
        };
        View.OnClickListener passwordButtonListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupTimer != null)
                    popupTimer.cancel();
                popupTimer = new Timer();
                final Handler handler = new Handler();
                final Runnable Update = new Runnable() {
                    public void run() {
                        dark.setVisibility(GONE);
                        popup.setVisibility(GONE);
                        popupPhone.setVisibility(View.VISIBLE);
                        popupMail.setVisibility(View.VISIBLE);
                        popupMission.setVisibility(View.VISIBLE);
                    }
                };
                popupTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        handler.post(Update);
                    }
                }, AUTO_POPUP_CLOSE_MS, AUTO_POPUP_CLOSE_MS);

                if (v == p1)
                    passwordInput[0] += "1";
                else if (v == p2)
                    passwordInput[0] += "2";
                else if (v == p3)
                    passwordInput[0] += "3";
                else if (v == p4)
                    passwordInput[0] += "4";
                else if (v == p5)
                    passwordInput[0] += "5";
                else if (v == p6)
                    passwordInput[0] += "6";
                else if (v == p7)
                    passwordInput[0] += "7";
                else if (v == p8)
                    passwordInput[0] += "8";
                else if (v == p9)
                    passwordInput[0] += "9";
                else if (v == p0)
                    passwordInput[0] += "0";
                else if (v == undo)
                    if (!passwordInput[0].equals(""))
                        passwordInput[0] = passwordInput[0].substring(0, passwordInput[0].length() - 1);

                String stars = "";
                for (int i = 0; i < passwordInput[0].length(); i++) {
                    stars += "*";
                }
                passwordText.setText(stars);
            }
        };

        p1.setOnClickListener(passwordButtonListener);
        p2.setOnClickListener(passwordButtonListener);
        p3.setOnClickListener(passwordButtonListener);
        p4.setOnClickListener(passwordButtonListener);
        p5.setOnClickListener(passwordButtonListener);
        p6.setOnClickListener(passwordButtonListener);
        p7.setOnClickListener(passwordButtonListener);
        p8.setOnClickListener(passwordButtonListener);
        p9.setOnClickListener(passwordButtonListener);
        p0.setOnClickListener(passwordButtonListener);
        undo.setOnClickListener(passwordButtonListener);

        p1.setOnTouchListener(passwordTouchListener);
        p2.setOnTouchListener(passwordTouchListener);
        p3.setOnTouchListener(passwordTouchListener);
        p4.setOnTouchListener(passwordTouchListener);
        p5.setOnTouchListener(passwordTouchListener);
        p6.setOnTouchListener(passwordTouchListener);
        p7.setOnTouchListener(passwordTouchListener);
        p8.setOnTouchListener(passwordTouchListener);
        p9.setOnTouchListener(passwordTouchListener);
        p0.setOnTouchListener(passwordTouchListener);
        //undo.setOnTouchListener(passwordTouchListener);

        tmp = 0;


        View.OnClickListener isWorkingButtonListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) { // 하단부 근무중 등.. 을 표시하는 버튼에 대한 리스너.
                if (popupTimer != null)
                    popupTimer.cancel();
                popupTimer = new Timer();
                final Handler handler = new Handler();
                final Runnable Update = new Runnable() {
                    public void run() {
                        dark.setVisibility(GONE);
                        popup.setVisibility(GONE);
                        popupPhone.setVisibility(View.VISIBLE);
                        popupMail.setVisibility(View.VISIBLE);
                        popupMission.setVisibility(View.VISIBLE);
                    }
                };
                popupTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        handler.post(Update);
                    }
                }, AUTO_POPUP_CLOSE_MS, AUTO_POPUP_CLOSE_MS);

                popupWorking.setBackgroundResource(R.drawable.new_status_1_off);
                popupVacation.setBackgroundResource(R.drawable.new_status_2_off);
                popupTrip.setBackgroundResource(R.drawable.new_status_3_off);
                popupEducation.setBackgroundResource(R.drawable.new_status_4_off);
                passwordLayout.setVisibility(View.VISIBLE);
                passwordText.setText("");

                if (v == popupWorking) {
                    popupWorking.setBackgroundResource(R.drawable.new_status_1_on);
                    tmp = 1;

                } else if (v == popupVacation) {
                    popupVacation.setBackgroundResource(R.drawable.new_status_2_on);
                    tmp = 2;

                } else if (v == popupTrip) {
                    popupTrip.setBackgroundResource(R.drawable.new_status_3_on);
                    tmp = 3;

                } else if (v == popupEducation) {
                    popupEducation.setBackgroundResource(R.drawable.new_status_4_on);
                    tmp = 4;

                } else if (v == ok) { // ok버튼 누를 시
                    if (passwordInput[0].equals(password) || (passwordInput[0].equals("") && password.equals("null"))) {
                        jsonParser.setIsWorking(team, mate, tmp);
                        sendBroadcast(new Intent("memberStatus")
                                .putExtra("member", jsonParser.getMemberIdentifier(team, mate))
                                .putExtra("status", tmp)
                                .putExtra("LocalFlag", false));
                        SharedPreferences pref = getSharedPreferences("kiosk_data", MODE_PRIVATE);
                        SharedPreferences.Editor edit = pref.edit();
                        edit.putInt(jsonParser.getMemberIdentifier(team, mate) + "memberStatus", tmp).commit();
                        Log.d("memberIdentifierLog", jsonParser.getMemberIdentifier(0, mate) + "memberStatus" + " " + tmp);
                        dark.setVisibility(GONE);
                        popup.setVisibility(GONE);
                        popupPhone.setVisibility(View.VISIBLE);
                        popupMail.setVisibility(View.VISIBLE);
                        popupMission.setVisibility(View.VISIBLE);
                    }
                }

                if (jsonParser.getIsWorking(team, mate) == tmp) {
                    passwordLayout.setVisibility(GONE);
                    popupPhone.setVisibility(View.VISIBLE);
                    popupMail.setVisibility(View.VISIBLE);
                    popupMission.setVisibility(View.VISIBLE);
                }

                passwordInput[0] = "";
            }
        };

        popupWorking.setOnClickListener(isWorkingButtonListener);
        popupVacation.setOnClickListener(isWorkingButtonListener);
        popupTrip.setOnClickListener(isWorkingButtonListener);
        popupEducation.setOnClickListener(isWorkingButtonListener);
        ok.setOnClickListener(isWorkingButtonListener);

        StringBuffer data;
        FileInputStream fis;
        BufferedReader buffer;
        String str;

        File dir;
        File file;
        FileWriter fw;
        try {
            data = new StringBuffer();
            JSONArray wholeArray = new JSONArray();
            try {
                fis = new FileInputStream(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/KIOSKData", "KIOSK_MEMBER_LOG.json")); // 멤버 로그에 삽입.
                buffer = new BufferedReader(new InputStreamReader(fis, "utf-8"));
                str = buffer.readLine();
                while (str != null) {
                    data.append(str + "\n");
                    str = buffer.readLine();
                }
                wholeArray = new JSONArray(data.toString()); // 데이터를 읽어와 JSONArray에 삽입.
            } catch (Exception e) {
                e.printStackTrace();
            }

            JSONObject memberObject = new JSONObject();
            memberObject.put("time", Calendar.getInstance().getTime());
            memberObject.put("id", jsonParser.getMemberIdentifier(team, mate));
            memberObject.put("dept", jsonParser.getDeptName(team, mate));
            memberObject.put("team", jsonParser.getTeamName(0).replace(" ", ""));
            memberObject.put("name", jsonParser.getName(team, mate));
            memberObject.put("duty", jsonParser.getDuty(team, mate));

            wholeArray.put(memberObject); // JSONArray에 추가 정보 삽입 후

            dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/KIOSKData");
            file = new File(dir, "KIOSK_MEMBER_LOG.json");
            fw = new FileWriter(file);
            fw.write(wholeArray.toString());
            fw.close();//저장하고 닫는다.
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /// Change working status text when status changed
    public void setLeaderIsWorking(ImageView leaderIsWorking, int mate) { // 리더가 일하는 중이면 초록색, 아니면 회색 점등.
        leaderIsWorking.setImageResource(R.drawable.new_led_gray);
        switch (jsonParser.getIsWorking(0, mate)) {
            case 1:
                Log.d("SetIsWorking", String.valueOf(jsonParser.getIsWorking(0, mate)) + " " + mate);
                leaderIsWorking.setImageResource(R.drawable.new_led_green);
                break;
        }
    }

    public void setLeaderIsWorking(ImageView leaderIsWorking, int mate, int checkWork) { // 리더가 일하는 중이면 초록색, 아니면 회색 점등.
        leaderIsWorking.setImageResource(R.drawable.new_led_gray);
        Log.d("SetIsWorking", String.valueOf(jsonParser.getIsWorking(0, mate)));
        switch (jsonParser.getIsWorking(0, mate)) {
            case 1:
                Log.d("SetIsWorking", String.valueOf(mate));
                leaderIsWorking.setImageResource(R.drawable.new_led_green);
                break;
        }
        if (checkWork != 1) {
            leaderIsWorking.setImageResource(R.drawable.new_led_gray);
        } else {
            leaderIsWorking.setImageResource(R.drawable.new_led_green);
        }
    }

    //원래꺼
    /// Change working status text when status changed
/*    public void setLeaderIsWorking(ImageView leaderIsWorking, int mate) {
        leaderIsWorking.setImageResource(R.drawable.work_icon);
        switch (jsonParser.getIsWorking(0, mate)) {
            case 1:
                leaderIsWorking.setImageResource(R.drawable.work_icon_blue);
                break;
        }
    }*/

    /// 공지사항 필드
    static ListView noticeList;
    static NoticeAdapter wholeAdapter, fAdapter, sAdapter;

    /// 공지사항 메뉴
    public void startNotice(int noticeIndex) {
        jsonParser.parseNoticeJson();
        jsonParser.parseNoticeWholeJson();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> publicSet = preferences.getStringSet("show_public_data", null);
        if (publicSet != null) {
            if (!publicSet.contains("공지사항"))
                jsonParser.subNoticeJson();
        }

        setContentView(R.layout.new_activity_notice);
        startAlert();
        final RelativeLayout mainActivity = (RelativeLayout) findViewById(R.id.notice_content);
        backgroundSetter(mainActivity, "notice");
        currentScene = "notice";

        noticeBarController("notice");
        logoSetter("공지사항");
        autoOrganizationController();

        try {
            final FrameLayout dark = (FrameLayout) findViewById(R.id.dark);
            final RelativeLayout popup = (RelativeLayout) findViewById(R.id.popup);
            final LinearLayout popupClose = (LinearLayout) findViewById(R.id.popup_close);

            popupClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    autoOrganizationController();
                    dark.setVisibility(GONE);
                    popup.setVisibility(View.INVISIBLE);
                }
            });

            noticeList = (ListView) findViewById(R.id.notice_listview);
            wholeAdapter = new NoticeAdapter();
            wholeAdapter.setContext(context);
            fAdapter = new NoticeAdapter();
            fAdapter.setContext(context);
            sAdapter = new NoticeAdapter();
            sAdapter.setContext(context);

            for (int i = 0; i < jsonParser.getNoticeCount(); i++)
                wholeAdapter.addItem(new NoticeItem(jsonParser.getNoticeTitle(i), jsonParser.getNoticeContent(i), jsonParser.getNoticePicPath(i)));

            for (int i = 0; i < jsonParser.getNoticeCount(); i++)
                if (jsonParser.getNoticeTitle(i).contains("[공지]")) {
                    fAdapter.addItem(new NoticeItem(jsonParser.getNoticeTitle(i), jsonParser.getNoticeContent(i), jsonParser.getNoticePicPath(i)));
                }
            for (int i = 0; i < jsonParser.getNoticeCount(); i++)
                if (jsonParser.getNoticeTitle(i).contains("[행사]")) {
                    fAdapter.addItem(new NoticeItem(jsonParser.getNoticeTitle(i), jsonParser.getNoticeContent(i), jsonParser.getNoticePicPath(i)));
                }
            for (int i = 0; i < jsonParser.getNoticeCount(); i++)
                if (!jsonParser.getNoticeTitle(i).contains("[공지]") && !jsonParser.getNoticeTitle(i).contains("[행사]")) {
                    Log.d("notice?", "app?");
                    fAdapter.addItem(new NoticeItem(jsonParser.getNoticeTitle(i), jsonParser.getNoticeContent(i), jsonParser.getNoticePicPath(i)));
                }

            noticeList.setAdapter(fAdapter);

            final boolean[] tab = {false};

            noticeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    autoOrganizationController();

                    NoticeItem item;
                    if (!tab[0])
                        item = (NoticeItem) fAdapter.getItem(position);
                    else
                        item = (NoticeItem) sAdapter.getItem(position);
                    setNoticePopup(item);
                }
            });
            noticeList.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    autoOrganizationController();
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                }
            });

            try {
                if (noticeIndex != -1) {
                    if (noticeIndex - 1 < 0)
                        noticeIndex = wholeAdapter.getCount();

                    NoticeItem tmpItem = (NoticeItem) wholeAdapter.getItem(noticeIndex - 1);
                    setNoticePopup(tmpItem);

                    if (tmpItem.getTitle().contains("[공지]")) {
                        noticeList.setAdapter(fAdapter);
                    } else if (tmpItem.getTitle().contains("[행사]")) {
                        noticeList.setAdapter(fAdapter);
                    } else if (!tmpItem.getTitle().contains("[공지]") && !tmpItem.getTitle().contains("[행사]")) {
                        noticeList.setAdapter(fAdapter);
                        Log.d("notice?", "app?");
                    }
                }

            } catch (ArrayIndexOutOfBoundsException e) {

            }

            bottomButtonController("notice");
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
    }

    public void setNoticePopup(NoticeItem item) {
        final RelativeLayout popup = (RelativeLayout) findViewById(R.id.popup);
        final TextView popupTitle = (TextView) findViewById(R.id.notice_popup_title);
        final TextView popupArticle = (TextView) findViewById(R.id.notice_popup_article);
        final ImageView popupImage = (ImageView) findViewById(R.id.popupImage);
        final ImageView popupImageScaled = (ImageView) findViewById(R.id.popupImageScaled);
        final FrameLayout scaledDark = (FrameLayout) findViewById(R.id.scaledDark);

        autoOrganizationController();

        scaledDark.setVisibility(GONE);
        popupImageScaled.setVisibility(View.INVISIBLE);

        popupTitle.setText(item.getTitle());
        popupArticle.setText(item.getArticle());

        File sdCard = Environment.getExternalStorageDirectory();
        final File f;

        f = new File(sdCard.getAbsolutePath() + "/KIOSKData/notice/" + item.getFile());
        Glide.with(context).load(f).into(popupImage);

        popupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoOrganizationController();
                scaledDark.setVisibility(View.VISIBLE);
                popupImageScaled.setVisibility(View.VISIBLE);
                Glide.with(context).load(f).into(popupImageScaled);
                scaledDark.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        scaledDark.setVisibility(GONE);
                        popupImageScaled.setVisibility(View.INVISIBLE);
                    }
                });
                popupImageScaled.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        scaledDark.setVisibility(GONE);
                        popupImageScaled.setVisibility(View.INVISIBLE);
                    }
                });
            }
        });

        final ListView noticeList = (ListView) findViewById(R.id.notice_listview);

        final LinearLayout close = (LinearLayout) findViewById(R.id.popup_close);
        YoYo.with(Techniques.SlideOutLeft).duration(300).onEnd(new YoYo.AnimatorCallback() {
            @Override
            public void call(Animator animator) {
                close.setVisibility(VISIBLE);
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        autoOrganizationController();
                        YoYo.with(Techniques.SlideOutRight).duration(300).onEnd(new YoYo.AnimatorCallback() {
                            @Override
                            public void call(Animator animator) {
                                close.setVisibility(INVISIBLE);
                            }
                        }).playOn(popup);
                        noticeList.setVisibility(VISIBLE);
                        YoYo.with(Techniques.SlideInLeft).duration(300).playOn(noticeList);
                    }
                });
            }
        }).playOn(noticeList);
        popup.setVisibility(View.VISIBLE);
        YoYo.with(Techniques.SlideInRight).duration(300).playOn(popup);

        popup.setOnClickListener(null);
    }

    private PlayerView exoPlayerView;
    private SimpleExoPlayer player;
    private Boolean playWhenReady = true;
    private int currentWindow = 0;
    private Long playbackPosition = 0L;
    private DefaultTrackSelector trackSelector = new DefaultTrackSelector();

    private void startMovie() {
        setContentView(R.layout.new_activity_movie);
        //startAlert();

        jsonParser.loadJsonData();
        jsonParser.parseVideoJson();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> publicSet = preferences.getStringSet("show_public_data", null);
        if (publicSet != null) {
            if (!publicSet.contains("홍보영상")) {
                jsonParser.subVideoJson("none");
            }
        }
        bottomButtonController("video");
        final RelativeLayout mainActivity = (RelativeLayout) findViewById(R.id.movie_content);
        mainActivity.setBackgroundColor(Color.GRAY);


        startAlert();
        backgroundSetter(mainActivity, "video");
        currentScene = "video";

        noticeBarController("video");
        logoSetter("홍보영상");
        autoOrganizationController();

        try {
            final FrameLayout dark = (FrameLayout) findViewById(R.id.dark);
            final RelativeLayout popup = (RelativeLayout) findViewById(R.id.popup);
            final LinearLayout popupClose = (LinearLayout) findViewById(R.id.popup_close);

            popupClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    autoOrganizationController();
                    dark.setVisibility(GONE);
                    popup.setVisibility(View.INVISIBLE);
                }
            });

            ListView noticeList = (ListView) findViewById(R.id.notice_listview);

            NoticeAdapter wholeAdapter = new NoticeAdapter();
            wholeAdapter.setContext(context);
            NoticeAdapter fAdapter = new NoticeAdapter();
            fAdapter.setContext(context);
            NoticeAdapter sAdapter = new NoticeAdapter();
            sAdapter.setContext(context);
            String compTeamCode = null;
            if (wholeMode || teamCode.equals("") || teamCode.equals("none"))
                compTeamCode = "none";
            else
                compTeamCode = teamCode;
            for (int i = 0; i < jsonParser.getVideoCount(); i++) {
                if (compTeamCode.equals(jsonParser.getVideoTeamCode(i)) || jsonParser.getVideoTeamCode(i).equals("none")) {
                    wholeAdapter.addItem(new NoticeItem(jsonParser.getVideoName(i), jsonParser.getVideoFile(i)));
                    fAdapter.addItem(new NoticeItem(jsonParser.getVideoName(i), jsonParser.getVideoFile(i)));
                }
            }

            noticeList.setAdapter(fAdapter);

            final boolean[] tab = {false};
            final NoticeAdapter fFAdapter = fAdapter;
            final NoticeAdapter fSAdapter = sAdapter;
            noticeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    autoOrganizationController();

                    NoticeItem item;
                    if (!tab[0])
                        item = (NoticeItem) fFAdapter.getItem(position);
                    else
                        item = (NoticeItem) fSAdapter.getItem(position);
                    setMoviePopup(item);
                }
            });
            noticeList.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    autoOrganizationController();
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                }
            });

        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
    }

    private void initializePlayer(String[] sample, int index) {
        isPlayVideo = true;
        if (index >= sample.length)
            index = 0;
        //Animation slideIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slidein);
        if (player == null) {
            player = ExoPlayerFactory.newSimpleInstance(this.getApplicationContext(), trackSelector);
            exoPlayerView.setPlayer(player);
        }

        exoPlayerView.hideController();

        TrustManager[] dummyTrustManager = new TrustManager[]{new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }};

        try {
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, dummyTrustManager, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
        MediaSource mediaSource;
        try {
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/KIOSKData/", sample[index]);
            mediaSource = buildMediaSource(Uri.fromFile(file), true);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String serverAddress = preferences.getString("server_address", getResources().getString(R.string.server_address));
                HttpURLConnection connection = (HttpURLConnection) new URL(serverAddress + "/resources/upload/movie/" + sample[index]).openConnection();
                connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
                connection.setRequestProperty("Accept-Language", "ko-KR,ko;q=0.8,en-US;q=0.5,en;q=0.3");
                connection.setRequestProperty("Connection", "keep-alive");
                //Log.d("Network", String.valueOf(connection.getInputStream()));
                mediaSource = buildMediaSource(Uri.parse(serverAddress + "/resources/upload/movie/" + sample[index]), false);
            } catch (IOException e1) {
                e1.printStackTrace();
                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/KIOSKData/", sample[index]);
                mediaSource = buildMediaSource(Uri.fromFile(file), true);
            }
        }


        player.prepare(mediaSource, true, false);
        player.setPlayWhenReady(playWhenReady);
        final String[] finalSample = sample;
        final int finalIndex = index;
        Player.EventListener eventListener = new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playbackState == Player.STATE_ENDED) {
                    final RelativeLayout popup = (RelativeLayout) findViewById(R.id.popup);
                    final LinearLayout close = (LinearLayout) findViewById(R.id.popup_close);
                    final ListView noticeList = (ListView) findViewById(R.id.notice_listview);

                    YoYo.with(Techniques.SlideOutRight).duration(300).onEnd(new YoYo.AnimatorCallback() {
                        @Override
                        public void call(Animator animator) {
                            close.setVisibility(INVISIBLE);
                            autoOrganizationController();
                        }
                    }).playOn(popup);
                    noticeList.setVisibility(VISIBLE);
                    YoYo.with(Techniques.SlideInLeft).duration(300).playOn(noticeList);
                    releasePlayer();
                    isPlayVideo = false;
                    autoOrganizationController();
                }
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

            }

            @Override
            public void onPositionDiscontinuity(int reason) {

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }

            @Override
            public void onSeekProcessed() {

            }
        };

        player.addListener(eventListener);
        //exoPlayerView.startAnimation(slideIn);
    }

    public void setMoviePopup(NoticeItem item) {
        final RelativeLayout popup = (RelativeLayout) findViewById(R.id.popup);
//        final TextView popupTitle = (TextView) findViewById(R.id.notice_popup_title);

        exoPlayerView = (PlayerView) findViewById(R.id.exoPlayerView);

        autoOrganizationController();

        //popupTitle.setText(item.getTitle());
        String[] Playlist = {item.getFile()};
        initializePlayer(Playlist, 0);

        final ListView noticeList = (ListView) findViewById(R.id.notice_listview);

        final LinearLayout close = (LinearLayout) findViewById(R.id.popup_close);
        YoYo.with(Techniques.SlideOutLeft).duration(300).onEnd(new YoYo.AnimatorCallback() {
            @Override
            public void call(Animator animator) {
                close.setVisibility(VISIBLE);
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        autoOrganizationController();
                        YoYo.with(Techniques.SlideOutRight).duration(300).onEnd(new YoYo.AnimatorCallback() {
                            @Override
                            public void call(Animator animator) {
                                close.setVisibility(INVISIBLE);
                            }
                        }).playOn(popup);
                        noticeList.setVisibility(VISIBLE);
                        YoYo.with(Techniques.SlideInLeft).duration(300).playOn(noticeList);
                        releasePlayer();
                    }
                });
            }
        }).playOn(noticeList);
        popup.setVisibility(View.VISIBLE);
        YoYo.with(Techniques.SlideInRight).duration(300).playOn(popup);

        popup.setOnClickListener(null);
    }

    private MediaSource buildMediaSource(Uri parse, boolean isFile) {
        String userAgent = Util.getUserAgent(this, "ExoTest");
        if (!isFile)
            return new ExtractorMediaSource.Factory(new DefaultHttpDataSourceFactory(userAgent))
                    .createMediaSource(parse);
        else
            return new ExtractorMediaSource.Factory(new DefaultDataSourceFactory(getApplicationContext(), userAgent))
                    .createMediaSource(parse);
    }

    private void releasePlayer() {
        try {
            isPlayVideo = false;
            if (player != null) {
                playbackPosition = player.getCurrentPosition();
                currentWindow = player.getCurrentWindowIndex();
                playWhenReady = player.getPlayWhenReady();

                exoPlayerView.setPlayer(null);
                player.release();
                player = null;
            }
        } catch (Exception e) {
            autoOrganizationController();
        }
        try {
            customViewPopupVideo.releasePlayer();
        } catch (Exception e) {
        }
    }

    public void backgroundSetter(final RelativeLayout activity, String scene) {
        clockSetter();

        if (scene.equals("main"))
            return;
/*
        //원래꺼
        int rnd = new Random().nextInt(6) + 1;
        Glide.with(context).load(getResources().getIdentifier("bg" + rnd, "drawable",
                context.getPackageName())).into(new ViewTarget<RelativeLayout, GlideDrawable>(activity) {
            @Override
            public void onResourceReady(GlideDrawable resource, GlideAnimation anim) {
                RelativeLayout myView = this.view;
                // Set your resource on myView and/or start your animation here.
                myView.setBackground(resource);
            }
        });*/

        rnd = new Random().nextInt(7) + 1;

        Glide.with(context)
                .load(getResources().getIdentifier("new_main_bg_" + rnd, "drawable",
                        context.getPackageName()))
                .skipMemoryCache(true)
                .into(new ViewTarget<RelativeLayout, GlideDrawable>(activity) {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation anim) {
                        RelativeLayout myView = this.view;
                        // Set your resource on myView and/or start your animation here.
                        myView.setBackground(resource);
                    }
                });
    }

    /// Set title logo
    public void logoSetter(String menu, int flags) { // string으로 변경할 시(아마 이미지 뒤 글자로 된 로고인듯)
        localIconSetter(); // 미사용 메소드
        //flag 1 = whole
        //flag 2 = select

        boolean selectMode = false;
        try {
            Set<String> selectSet = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getStringSet("team_list_select", null);
            selectSetString = selectSet.toArray(new String[]{});
            if (selectSetString.length != 0) {
                selectMode = true;
                Log.d("selectSetStringlength", String.valueOf(selectSetString.length));
            }
        } catch (Exception e) {
            Log.d("Set is None", "none");
            selectMode = false;
        }

        TextView logo = (TextView) findViewById(R.id.teamLogo);
        /*Typeface fontAwesome = Typeface.createFromAsset(getAssets(), "fa-regular-400.ttf");
        try {
            logo.setTypeface(fontAwesome);
        } catch (NullPointerException e) {
            e.printStackTrace();
            logo = (TextView) findViewById(R.id.teamLogos);
            logo.setTypeface(fontAwesome);
        }*/
        String cut = "";
        try {
            if (flags == 1) {
                if (fullCodeHash.get(teamCode).contains("&gt;")) {
                    cut = fullCodeHash.get(teamCode).substring(fullCodeHash.get(teamCode).indexOf(";") + 1, fullCodeHash.get(teamCode).length());//; 다음부터 끝까지.
                } else if (fullCodeHash.get(teamCode).contains(">")) {
                    cut = fullCodeHash.get(teamCode).substring(fullCodeHash.get(teamCode).indexOf(">") + 1, fullCodeHash.get(teamCode).length()); // > 다음부터 끝까지
                } else {
                    cut = fullCodeHash.get(teamCode); // 그냥 쌩으로
                }
            } else if (flags == 2) {
                Iterator it = sortSelectSetString();
                if (selectMode == true && !wholeMode) {
                    while (it.hasNext()) {
                        cut = cut + (String) it.next() + "/";
                    }
                    cut = cut.substring(0, cut.length() - 1);
                }
            } else {
                cut = menu;
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            cut = menu;
        }

        Log.d("wholeModeChk", "wholemode is " + wholeMode);
        Log.d("currentSceneChk", "currentScene is " + currentScene);
        if (currentScene.equals("about")) {
            if (!wholeMode) {
                cut = "부서소개";
            }
        }

        if (menu.equals("조직도")) {
            try {
                logo.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        showSeatSettingDialog();
                        return false;
                    }
                });
            } catch (NullPointerException e) {
                logo = (TextView) findViewById(R.id.teamLogos);
                logo.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        showSeatSettingDialog();
                        return false;
                    }
                });
            }
        }
        //원래꺼 아님
        Shader textShader = new LinearGradient(0, 0, 0, 20,//쉐이더
                new int[]{Color.parseColor("#ffffaf"), Color.parseColor("#eabb53")},
                new float[]{0, 1}, Shader.TileMode.CLAMP);


        if (typefaceNANUMMYEONGJOBOLD == null) {
            typefaceNANUMMYEONGJOBOLD = Typeface.createFromAsset(getAssets(), "NANUMMYEONGJOBOLD.ttf");
            Log.d("setTypeface", "nnmb");
        }

        logo.setTypeface(typefaceNANUMMYEONGJOBOLD);

        logo.getPaint().setShader(textShader);

        logo.setText(cut);

        findViewById(R.id.title_home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!currentScene.equals("main")) {
                    releasePlayer();
                    startActivity(new Intent(MainActivity.this, MainActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("scene", "main"));
                    finish();
                }
            }
        });
    }

    public void logoSetter(String menu) {
        logoSetter(menu, 0);
    } // 디폴트 생성자 가 아니라, 오버로드 재귀호출.

    void bottomButtonLogCreator(String menu) {
        StringBuffer data;
        FileInputStream fis;
        BufferedReader buffer;
        String str;

        File dir;
        File file;
        FileWriter fw;
        try {
            data = new StringBuffer();
            JSONArray wholeArray = new JSONArray();
            try {
                fis = new FileInputStream(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/KIOSKData", "KIOSK_MENU_LOG.json"));
                buffer = new BufferedReader(new InputStreamReader(fis, "utf-8"));
                str = buffer.readLine();
                while (str != null) {
                    data.append(str + "\n");
                    str = buffer.readLine();
                }
                wholeArray = new JSONArray(data.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }

            JSONObject memberObject = new JSONObject();
            memberObject.put("time", Calendar.getInstance().getTime());
            memberObject.put("menu", menu);

            wholeArray.put(memberObject);

            dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/KIOSKData");
            file = new File(dir, "KIOSK_MENU_LOG.json");
            fw = new FileWriter(file);
            fw.write(wholeArray.toString());
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /// Control bottom buttons
    public void bottomButtonController(final String scene) {
        //bottomIconLoader();
        System.gc();
        sceneSetter(scene);
        jsonParser.loadJsonData();
        final ImageView homeButton = (ImageView) findViewById(R.id.bottom_home);
        final ImageView mayorButton = (ImageView) findViewById(R.id.bottom_past);
        final ImageView organizationButton = (ImageView) findViewById(R.id.bottom_organization);
        final ImageView buildingButton = (ImageView) findViewById(R.id.bottom_building);
        final ImageView videoButton = (ImageView) findViewById(R.id.bottom_video);
        final ImageView aboutButton = (ImageView) findViewById(R.id.bottom_about);
        final ImageView galleryButton = (ImageView) findViewById(R.id.bottom_gallery);
        final ImageView noticeButton = (ImageView) findViewById(R.id.bottom_notice);

        homeButton.setScaleType(ImageView.ScaleType.CENTER_CROP);
        mayorButton.setScaleType(ImageView.ScaleType.CENTER_CROP);
        organizationButton.setScaleType(ImageView.ScaleType.CENTER_CROP);
        buildingButton.setScaleType(ImageView.ScaleType.CENTER_CROP);
        videoButton.setScaleType(ImageView.ScaleType.CENTER_CROP);
        aboutButton.setScaleType(ImageView.ScaleType.CENTER_CROP);
        galleryButton.setScaleType(ImageView.ScaleType.CENTER_CROP);
        noticeButton.setScaleType(ImageView.ScaleType.CENTER_CROP);

        mayorButton.setVisibility(GONE);

        if (wholeMode) {
            if (scene.equals("about"))
                aboutButton.setImageResource(R.drawable.new_mnu_9_over);
            else
                aboutButton.setImageResource(R.drawable.new_mnu_9_out);
        }

        //원래꺼
/*        if (scene.equals("about"))
            aboutButton.setImageResource(R.drawable.mnu_about_section_in);
        else
            aboutButton.setImageResource(R.drawable.mnu_about_section_out);*/

        View.OnClickListener bottomButtonListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                releasePlayer();
                returnMemories();
                jsonParser.loadJsonData();
                FrameLayout dark = (FrameLayout) findViewById(R.id.dark);
                ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar);
                //dark.setVisibility(View.VISIBLE);
                pb.setVisibility(View.VISIBLE);

                if (SearchListFragment.bgWork != null) {
                    SearchListFragment.bgWork.cancel(true);
                    SearchListFragment.bgWork = null;
                }

                autoFlipped = false;
                if (v == homeButton && !scene.equals("main")) {
                    bottomButtonLogCreator("main");
                    startActivity(new Intent(MainActivity.this, MainActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("scene", "main")); // 덮어쓰기. scene과 main이 합치.
                    finish();
                } else if ((v == organizationButton /*&& !scene.equals("organization")) || (v == organizationButton && wholeMode*/)) {
                    bottomButtonLogCreator("organization");
                    startActivity(new Intent(MainActivity.this, MainActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("scene", "organization"));
                    finish();
                } else if (v == buildingButton && !scene.equals("building")) {
                    bottomButtonLogCreator("building");
                    startActivity(new Intent(MainActivity.this, MainActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("scene", "building"));
                    Log.d("startBuilding", "build");
                    finish();
                } else if (v == videoButton) {
                    bottomButtonLogCreator("video");
                    startActivity(new Intent(MainActivity.this, MainActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("scene", "video"));
                    Log.d("startVideo", "video");
                    finish();
                } else if (v == aboutButton) {
                    bottomButtonLogCreator("about");
                    startActivity(new Intent(MainActivity.this, MainActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("scene", "about"));
                    finish();
                } else if (v == galleryButton && !scene.equals("gallery")) {
                    bottomButtonLogCreator("gallery");
                    startActivity(new Intent(MainActivity.this, MainActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("scene", "gallery"));
                    finish();
                } else if (v == noticeButton && !scene.equals("notice")) {
                    bottomButtonLogCreator("notice");
                    startActivity(new Intent(MainActivity.this, MainActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("scene", "notice"));
                    finish();
                } else {
                    dark.setVisibility(GONE);
                    pb.setVisibility(GONE);
                }
                try {
                    customViewPopupVideo.releasePlayer();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        homeButton.setOnClickListener(bottomButtonListener);
        organizationButton.setOnClickListener(bottomButtonListener);
        buildingButton.setOnClickListener(bottomButtonListener);
        videoButton.setOnClickListener(bottomButtonListener);
        aboutButton.setOnClickListener(bottomButtonListener);
        galleryButton.setOnClickListener(bottomButtonListener);
        noticeButton.setOnClickListener(bottomButtonListener);
        mayorButton.setOnClickListener(bottomButtonListener);

/*
        RelativeLayout menuBar = (RelativeLayout) findViewById(R.id.menu_bar);
        RelativeLayout innerBar = (RelativeLayout) findViewById(R.id.inner_bar);
//Right

        menuBar.setRotation(-90);
        menuBar.setScaleX(-1);
        menuBar.setX((420));

        ViewGroup.MarginLayoutParams lpimgFooter = (ViewGroup.MarginLayoutParams) noticeButton.getLayoutParams();
        lpimgFooter.rightMargin = 420;
        noticeButton.setLayoutParams(lpimgFooter);

        for(int i = 0; i < menuBar.getChildCount(); i++){
            if(menuBar.getChildAt(i) != innerBar) {
                menuBar.getChildAt(i).setScaleX(-1);
                menuBar.getChildAt(i).setRotation(-90);
            }
        }
*/

//Left
/*
        menuBar.setRotation(90);
        menuBar.setScaleX(1);
        menuBar.setX(-(420));

        innerBar.setVisibility(View.GONE);

        ViewGroup.MarginLayoutParams buttonMargin = (ViewGroup.MarginLayoutParams) noticeButton.getLayoutParams();
        buttonMargin.rightMargin = 420;
        noticeButton.setLayoutParams(buttonMargin);

        for(int i = 0; i < menuBar.getChildCount(); i++){
            if(menuBar.getChildAt(i) != innerBar) {
                menuBar.getChildAt(i).setRotation(-90);
            }
            if(menuBar.getChildAt(i) != noticeButton) {
                ViewGroup.MarginLayoutParams buttonRightMargin = (ViewGroup.MarginLayoutParams) menuBar.getChildAt(i).getLayoutParams();
                buttonRightMargin.rightMargin = 5;
                menuBar.getChildAt(i).setLayoutParams(buttonRightMargin);
            }
        }
        */
    }

    /// Bottom notice bar control
    static int noticeIndex = 0;

    public void noticeBarController(final String scene) { // 공지나 행사 띄워주는 바 인듯.
        jsonParser.parseNoticeJson();
        jsonParser.parseNoticeWholeJson();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> publicSet = preferences.getStringSet("show_public_data", null);
        if (publicSet != null) {
            if (!publicSet.contains("공지사항"))
                jsonParser.subNoticeJson();
        }
        final String[] now = {null};

        if (noticeTimer != null)
            noticeTimer.cancel();
        final TextView noticeBar = (TextView) findViewById(R.id.notice_bar);

        //원래꺼
        //noticeBar.getLayoutParams().width = 850;

        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                now[0] = jsonParser.getNoticeTitle(noticeIndex++);
                noticeBar.setText(now[0]);
                if (noticeIndex >= jsonParser.getNoticeCount())
                    noticeIndex = 0;
            }
        };

        noticeBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar);

                if (!scene.equals("notice")) {
                    pb.setVisibility(View.VISIBLE);
                    releasePlayer();
                    startActivity(new Intent(MainActivity.this, MainActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("scene", "notice").putExtra("index", noticeIndex));
                    finish();
                } else {
                    try {
                        int tmp = noticeIndex;
                        if (noticeIndex - 1 < 0) {
                            noticeIndex = wholeAdapter.getCount();
                        }
                        NoticeItem tmpItem = (NoticeItem) wholeAdapter.getItem(noticeIndex - 1);
                        setNoticePopup(tmpItem);

                        if (tmpItem.getTitle().contains("[공지]")) {
                            noticeList.setAdapter(fAdapter);
                        } else if (tmpItem.getTitle().contains("[행사]")) {
                            noticeList.setAdapter(fAdapter);
                        } else if (!tmpItem.getTitle().contains("[공지]") && !tmpItem.getTitle().contains("[행사]")) {
                            noticeList.setAdapter(fAdapter);
                        }

                        noticeIndex = tmp;
                    } catch (ArrayIndexOutOfBoundsException e) {
                    }
                }
            }
        });

        ///Auto slide timer
        noticeTimer = new Timer();
        noticeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 0, NOTICE_PERIOD_MS);
    }


    boolean autoFlipped = false;

    /// Auto change to organization scene
    public void autoOrganizationController() {
        Log.d("organization!", "start");
        if (wholeTimer != null)
            wholeTimer.cancel();

        boolean selectMode = false;
        try {
            Set<String> selectSet = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getStringSet("team_list_select", null);
            selectSetString = selectSet.toArray(new String[]{});
            if (selectSetString.length != 0) {
                selectMode = true;
                Log.d("selectSetStringlength", String.valueOf(selectSetString.length));
            }
        } catch (Exception e) {
            Log.d("Set is None", "none");
            selectMode = false;
        }

        /*if (sensorChanged)
            return;*/

        AUTO_ORGANIZATION_PERIOD_MS = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getInt("auto_home", 120) * 1000;
        /// Auto organization handler;
        final Handler handler = new Handler();
        final boolean finalSelectMode = selectMode;
        final Runnable Update = new Runnable() {
            public void run() {
                FrameLayout dark = (FrameLayout) findViewById(R.id.dark);
                final ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar);
                //dark.setVisibility(View.VISIBLE);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        try {
                            gotInterface = false;
                            if (wholeMode) {//현재 조직도 모드이면
                                try {
                                    if (isSeatPopup == true || isPlayVideo == true) {
                                        Log.d("block", "block");
                                    } else if (lunchBoolean) {
                                        pb.setVisibility(View.VISIBLE);
                                        if (SearchListFragment.bgWork != null) {//서치리스트가 가동중이면
                                            SearchListFragment.bgWork.cancel(true);//인터럽트 하고
                                            SearchListFragment.bgWork = null;//널로 변경( 아 초기화 인듯 )
                                        }

                                        releasePlayer();
                                        startActivity(new Intent(MainActivity.this, MainActivity.class)
                                                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("scene", "main").putExtra("sensor", true));//갤러리로 자동 이동
                                        finish();
                                    } else if (/*gpioLaunched++ != 0 && */!currentScene.equals("main")) {
                                        pb.setVisibility(View.VISIBLE);
                                        if (SearchListFragment.bgWork != null) {//서치리스트가 가동중이면
                                            SearchListFragment.bgWork.cancel(true);//인터럽트 하고
                                            SearchListFragment.bgWork = null;//널로 변경( 아 초기화 인듯 )
                                        }

                                        releasePlayer();
                                        startActivity(new Intent(MainActivity.this, MainActivity.class)
                                                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("scene", "main").putExtra("sensor", true));//갤러리로 자동 이동
                                        finish();
                                    } else {
                                        runScreenSaver(finalSelectMode);
                                    }
                                } catch (OutOfMemoryError o) {
                                    o.printStackTrace();
                                }
                            } else {//조직도 모드가 아니면
                                if (isSeatPopup == true || isPlayVideo == true) {
                                    Log.d("block", "block");
                                } else if (lunchBoolean) {
                                    try {
                                        pb.setVisibility(View.VISIBLE);
                                        if (SearchListFragment.bgWork != null) {
                                            SearchListFragment.bgWork.cancel(true);
                                            SearchListFragment.bgWork = null;
                                        }

                                        releasePlayer();
                                        startActivity(new Intent(MainActivity.this, MainActivity.class)
                                                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("scene", "main").putExtra("sensor", true));
                                        Log.d("organization!", "closed main");
                                        finish();
                                    } catch (OutOfMemoryError e) {
                                        e.printStackTrace();
                                    }
                                } else if (/*gpioLaunched++ != 0 && */ !currentScene.equals("organization") || (finalSelectMode && !globalSelectMode)) {
                                    pb.setVisibility(View.VISIBLE);
                                    if (SearchListFragment.bgWork != null) {
                                        SearchListFragment.bgWork.cancel(true);
                                        SearchListFragment.bgWork = null;
                                    }

                                    releasePlayer();
                                    startActivity(new Intent(MainActivity.this, MainActivity.class)
                                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("scene", "organization").putExtra("sensor", true));
                                    Log.d("organization!", "closed organization");
                                    finish();
                                } else {
                                    runScreenSaver(finalSelectMode);
                                }
                            }
                        } catch (OutOfMemoryError e) {
                            e.printStackTrace();
                        }
                    }
                }, PROGRESS_BAR_MS);
            }

        };

        wholeTimer = new Timer();
        wholeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
                Log.d("organization!", "closed handler");
            }
        }, AUTO_ORGANIZATION_PERIOD_MS);
    }

    /// Manage memories when scene changed
    public static void returnMemories() {
        if (timer != null) {
            timer.cancel();
        }
        mViewPager = null;
    }

    void goneOrganizationItems() {
        final RelativeLayout lead = (RelativeLayout) findViewById(R.id.leader);
        final RelativeLayout below = (RelativeLayout) findViewById(R.id.below);
        final RelativeLayout wrapper = (RelativeLayout) findViewById(R.id.wrapper1);
        final RelativeLayout wrapper2 = (RelativeLayout) findViewById(R.id.wrapper2);
        final RelativeLayout wrapper3 = (RelativeLayout) findViewById(R.id.wrapper3);
        final LinearLayout wrapper4 = (LinearLayout) findViewById(R.id.wrapper4);
        final LinearLayout tabWrapper = (LinearLayout) findViewById(R.id.tab_wrapper);
        final ImageView leaderBox = (ImageView) findViewById(R.id.leader_box);
        final ImageView belowBox = (ImageView) findViewById(R.id.below_box);
        final FrameLayout teamGuSung = (FrameLayout) findViewById(R.id.team_gu_sung);
        final RelativeLayout teamList = (RelativeLayout) findViewById(R.id.team_list);
        final LinearLayout seatButton = (LinearLayout) findViewById(R.id.seatbutton);
        final LinearLayout selectBackButton = (LinearLayout) findViewById(R.id.select_backbutton);
        final RelativeLayout slideBar = (RelativeLayout) findViewById(R.id.slide_bar);
        wrapper.setVisibility(GONE);
        wrapper2.setVisibility(GONE);
        wrapper3.setVisibility(GONE);
        wrapper4.setVisibility(VISIBLE);
        tabWrapper.setVisibility(GONE);

        lead.setVisibility(View.INVISIBLE);
        below.setVisibility(View.INVISIBLE);
        mViewPager.setVisibility(View.INVISIBLE);
        leaderBox.setVisibility(View.INVISIBLE);
        belowBox.setVisibility(View.INVISIBLE);
        teamGuSung.setVisibility(View.INVISIBLE);
        teamList.setVisibility(View.INVISIBLE);
        seatButton.setVisibility(View.INVISIBLE);
        selectBackButton.setVisibility(View.INVISIBLE);
        slideBar.setVisibility(View.INVISIBLE);

        findViewById(R.id.seat_popup_parent).setVisibility(GONE);
        findViewById(R.id.popup_title).setVisibility(GONE);
        findViewById(R.id.seat_close).setVisibility(GONE);
    }

    void runScreenSaver(boolean finalSelectMode) {
        try {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            boolean screenSaver = pref.getBoolean("screen_saver", false);
            if (screenSaver) {
                if (finalSelectMode && currentScene.equals("organization")) {
                    goneOrganizationItems();
                }
                jsonParser.parseGalleryJson();
                jsonParser.parseGalleryWholeJson();
                jsonParser.parseVideoJson();

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                Set<String> publicSet = preferences.getStringSet("show_public_data", null);
                boolean publicVideo = true;
                if (publicSet != null) {
                    if (!publicSet.contains("갤러리")) {
                        jsonParser.subGalleryJson();
                    }
                    if (!publicSet.contains("홍보영상")) {
                        jsonParser.subVideoJson("none");
                        publicVideo = false;
                    }
                }

                customViewPopupVideo = (CustomViewMainVideo) findViewById(R.id.custom_view_popup_video);
                try {
                    customViewPopupVideo.releasePlayer();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.d("autoOrgController", "run: videoPopup");

                if (typefacenanumeqareb == null) {
                    typefacenanumeqareb = Typeface.createFromAsset(getAssets(), "nanumsqb.ttf");
                }
                customViewPopupVideo.setTypeface(typefacenanumsqareeb);
                customViewPopupVideo.setVisibility(VISIBLE);
                String serverAddress = preferences.getString("server_address", getResources().getString(R.string.server_address));
                customViewPopupVideo.setLocalVideoMode(true, serverAddress);
                List<String> videoList = new ArrayList<>();
                for (int i = 0; i < jsonParser.getVideoCount(); i++) {
                    if (jsonParser.getVideoTeamCode(i).equals(teamCode) || (publicVideo && jsonParser.getVideoTeamCode(i).equals("none"))) {
                        videoList.add(jsonParser.getVideoFile(i));

                        Log.d("TAG", "runScreenSaver: addVideos: " + jsonParser.getVideoFile(i));
                    }
                }

                for (int i = 0; i < jsonParser.getGalleryCount(); i++) {
                    videoList.add(jsonParser.getGalleryPicPath(i));
                    Log.d("TAG", "runScreenSaver: addVideos: " + jsonParser.getGalleryPicPath(i));
                }
                if (videoList.size() != 0) {
                    if (currentScene.equals("organization")) {
                        videoList.add("no_layout");
                    }
                    customViewPopupVideo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                customViewPopupVideo.setVisibility(GONE);
                                customViewPopupVideo.releasePlayer();
                                autoOrganizationController();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    customViewPopupVideo.playVideo(videoList);
                } else {
                    try {
                        customViewPopupVideo.setVisibility(GONE);
                        customViewPopupVideo.releasePlayer();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
    }

    /// Organization menu viewpager scroll speed controller
    private static void setViewPagerScrollSpeed(ViewPager mViewPager, int isFast) {
        try {
            Field mScroller = null;
            mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            FixedSpeedScroller scroller = new FixedSpeedScroller(mViewPager.getContext());
            if (isFast == 1)
                scroller.setmDuration(500);
            else if (isFast == 2)
                scroller.setmDuration(1500);
            else
                scroller.setmDuration(5000);
            mScroller.set(mViewPager, scroller);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public void localIconSetter() {
        //ImageView localIcon = (ImageView) findViewById(R.id.gj_logo);
        //Glide.with(this).load(R.drawable.icon_yecheon).fit().centerInside().into(localIcon);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        updateUI();
    }

    public void clockSetter() {
        DigitalClock dc = (DigitalClock) findViewById(R.id.digitalClock1);

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent e) {
        super.dispatchTouchEvent(e);
        return mGestureDetector.onTouchEvent(e);
    }

    class SwipeDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                // Swipe left (next)
                autoOrganizationController();

                final TextView[] floorTextList = {(TextView) findViewById(R.id.floor_text_1), (TextView) findViewById(R.id.floor_text_2), (TextView) findViewById(R.id.floor_text_3),
                        (TextView) findViewById(R.id.floor_text_4), (TextView) findViewById(R.id.floor_text_5), (TextView) findViewById(R.id.floor_text_6),
                        (TextView) findViewById(R.id.floor_text_7), (TextView) findViewById(R.id.floor_text_8), (TextView) findViewById(R.id.floor_text_9)};

                final TextView[] floorTextContentsList = {(TextView) findViewById(R.id.floor_text_contents_1), (TextView) findViewById(R.id.floor_text_contents_2), (TextView) findViewById(R.id.floor_text_contents_3),
                        (TextView) findViewById(R.id.floor_text_contents_4), (TextView) findViewById(R.id.floor_text_contents_5), (TextView) findViewById(R.id.floor_text_contents_6),
                        (TextView) findViewById(R.id.floor_text_contents_7), (TextView) findViewById(R.id.floor_text_contents_8), (TextView) findViewById(R.id.floor_text_contents_9)};

                final FrameLayout[] flipBtn = {(FrameLayout) findViewById(R.id.flip_btn_1), (FrameLayout) findViewById(R.id.flip_btn_2), (FrameLayout) findViewById(R.id.flip_btn_3),
                        (FrameLayout) findViewById(R.id.flip_btn_4), (FrameLayout) findViewById(R.id.flip_btn_5), (FrameLayout) findViewById(R.id.flip_btn_6),
                        (FrameLayout) findViewById(R.id.flip_btn_7), (FrameLayout) findViewById(R.id.flip_btn_8), (FrameLayout) findViewById(R.id.flip_btn_9)};

                if (e1.getX() > e2.getX()) {
                    if (buildingFlipper != null && buildingFlipper.getChildCount() != 1) {
                        flipCounter[0]++;
                        for (int j = 0; j < buildingFlipper.getChildCount(); j++) {
                            floorTextContentsList[j].setTextColor(0xFFc9c9c9);
                            floorTextList[j].setTextColor(0xFFc9c9c9);
                            flipBtn[j].setVisibility(View.GONE);
                        }

                        if (flipCounter[0] > buildingFlipper.getChildCount() - 1)
                            flipCounter[0] = 0;
                        else if (flipCounter[0] < 0)
                            flipCounter[0] = buildingFlipper.getChildCount() - 1;

                        autoOrganizationController();
                        buildingFlipper.setInAnimation(getApplicationContext(), R.anim.in_from_right);
                        buildingFlipper.setOutAnimation(getApplicationContext(), R.anim.out_to_left);
                        buildingFlipper.showNext();

                        Log.d("flipCounter", flipCounter[0] + " " + (buildingFlipper.getChildCount() - 1));
                        floorTextList[flipCounter[0]].setTextColor(0xffffffff);
                        floorTextContentsList[flipCounter[0]].setTextColor(0xffffffff);
                        flipBtn[flipCounter[0]].setVisibility(View.VISIBLE);
                    }
                    if (galleryFlipper != null) {
                        galleryFlipper.setInAnimation(getApplicationContext(), R.anim.in_from_right);
                        galleryFlipper.setOutAnimation(getApplicationContext(), R.anim.out_to_left);
                        galleryFlipper.showNext();
                        if (galleryTitle != null)
                            galleryTitle.setText(jsonParser.getGalleryTitle(galleryFlipper.getDisplayedChild()));
                    }
                }

                // Swipe right (previous)
                if (e1.getX() < e2.getX()) {
                    if (buildingFlipper != null && buildingFlipper.getChildCount() != 1) {
                        flipCounter[0]--;
                        for (int j = 0; j < buildingFlipper.getChildCount(); j++) {
                            floorTextContentsList[j].setTextColor(0xFFc9c9c9);
                            floorTextList[j].setTextColor(0xFFc9c9c9);
                            flipBtn[j].setVisibility(View.GONE);
                        }
                        if (flipCounter[0] > buildingFlipper.getChildCount() - 1)
                            flipCounter[0] = 0;
                        else if (flipCounter[0] < 0)
                            flipCounter[0] = buildingFlipper.getChildCount() - 1;

                        autoOrganizationController();
                        buildingFlipper.setInAnimation(getApplicationContext(), R.anim.in_from_left);
                        buildingFlipper.setOutAnimation(getApplicationContext(), R.anim.out_to_right);
                        buildingFlipper.showPrevious();

                        Log.d("flipCounter", flipCounter[0] + " ");
                        floorTextList[flipCounter[0]].setTextColor(0xffffffff);
                        floorTextContentsList[flipCounter[0]].setTextColor(0xffffffff);
                        flipBtn[flipCounter[0]].setVisibility(View.VISIBLE);
                    }
                    if (galleryFlipper != null) {
                        galleryFlipper.setInAnimation(getApplicationContext(), R.anim.in_from_left);
                        galleryFlipper.setOutAnimation(getApplicationContext(), R.anim.out_to_right);
                        galleryFlipper.showPrevious();
                        if (galleryTitle != null)
                            galleryTitle.setText(jsonParser.getGalleryTitle(galleryFlipper.getDisplayedChild()));
                    }
                }

                return super.onFling(e1, e2, velocityX, velocityY);
            } catch (NullPointerException | IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
            return false;
        }
    }

    boolean gotInterface = false;
    int noRoom = 0;

    final class BuildingJavascriptInterface {
        @JavascriptInterface
        public void getExists(final String fileName, final String teamCode) {
            int floorNum = 0;
            for (int i = 0; i < jsonParser.getBuildingNumViewer(); i++)
                for (int j = 0; j < jsonParser.getBuildingFloorCountViewer(i); j++)
                    floorNum++;

            if (!gotInterface) {
                noRoom++;
                if (fileName.equals("none")) {
                    if (noRoom == floorNum - 3)
                        sendBroadcast(new Intent("buildingLocation")
                                .putExtra("none", true)
                                .putExtra("teamCode", teamCode));
                    return;
                }

                sendBroadcast(new Intent("buildingLocation")
                        .putExtra("fileName", fileName.replace("file:///android_asset/building/", ""))
                        .putExtra("teamCode", teamCode));
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    public static void setIsSeatPopup(boolean set) {
        isSeatPopup = set;
        Log.d("isPopup", " :" + isSeatPopup);
    }

    public static boolean getIsSeatPopup() {
        return isSeatPopup;
    }

    public static int getRnd() {
        return rnd;
    }

    private void setSelectPackerAbout(int groupPointId, LayoutInflater vi, String[] codes, int[] bgCodes, String[] strings, int packerCounter) {
        ViewGroup groupPoint = (ViewGroup) findViewById(groupPointId);
        ViewGroup itemPoint;
        if (groupPoint.getChildCount() != 0)
            groupPoint.removeAllViews();
        String code = null;
        for (int i = 0; i < codes.length; i++) {
            try {
                code = codes[i];
                itemPoint = (ViewGroup) vi.inflate(R.layout.org_tmp_select_team, null);
                View item = vi.inflate(R.layout.org_layout_select_team, null);
                TextView text = (TextView) item.findViewById(R.id.textview);
                text.setText(strings[i]);

                code = teamNameHash.get(code);
                final String finalCode3 = code;
                setOnClickPackerAbout(code, finalCode3, groupPoint);

                itemPoint.addView(item);
                groupPoint.addView(itemPoint);

            } catch (Exception e) {
                Log.d("printError", "error");
                e.printStackTrace();
            }
        }
    }

    private void setSelectPacker(int groupPointId, LayoutInflater vi, String[] codes, int[] bgCodes, String[] strings, int packerCounter) {
        findViewById(R.id.team_list).setVisibility(View.GONE);
        findViewById(R.id.team_gu_sung).setVisibility(View.GONE);
        ViewGroup groupPoint = (ViewGroup) findViewById(groupPointId);
        ViewGroup itemPoint;
        if (groupPoint.getChildCount() != 0)
            groupPoint.removeAllViews();
        String code = null;
        for (int i = 0; i < codes.length; i++) {
            try {
                code = codes[i];
                itemPoint = (ViewGroup) vi.inflate(R.layout.org_tmp_select_team, null);
                View item = vi.inflate(R.layout.org_layout_select_team, null);
                TextView text = (TextView) item.findViewById(R.id.textview);
                text.setText(strings[i]);

                code = teamNameHash.get(code);
                final String finalCode3 = code;
                setOnClickPacker(code, finalCode3, groupPoint);

                itemPoint.addView(item);
                groupPoint.addView(itemPoint);

            } catch (Exception e) {
                Log.d("printError", "error");
                e.printStackTrace();
            }
        }
    }

    private void setPackerStyle(int i, TextView text, int[] bgCodes) {
        switch (bgCodes[i]) {
            case 0:
                text.setBackgroundResource(R.drawable.mayor_layer);
                text.setTextColor(0xFFFFFFFF);
                break;
            case 1:
                text.setBackgroundResource(R.drawable.submayor_layer);
                text.setTextColor(0xFFFFFFFF);
                break;
            case 2:
                text.setBackgroundResource(R.drawable.mayor_down_layer);
                text.setTextColor(0xFFFFFFFF);
                break;
            case 3:
                text.setBackgroundResource(R.drawable.department_layer);
                text.setTextColor(0xFF333333);
                break;
            case 4:
                text.setBackgroundResource(R.drawable.subject_layer);
                text.setTextColor(0xFF333333);
                break;
        }
    }

    private void setOnClickPackerContents(final String finalCode3) {
        Log.d("targetTeamCode", finalCode3);
        if ((!jsonParser.parseStaffJson(finalCode3))) {
            teamCode = (String) getResources().getText(R.string.시장);
        } else {
            teamCode = finalCode3;
            Log.d("targetTeamCode", finalCode3);
        }

        final RelativeLayout lead = (RelativeLayout) findViewById(R.id.leader);
        final RelativeLayout below = (RelativeLayout) findViewById(R.id.below);
        final RelativeLayout wrapper = (RelativeLayout) findViewById(R.id.wrapper1);
        final RelativeLayout wrapper2 = (RelativeLayout) findViewById(R.id.wrapper2);
        final RelativeLayout wrapper3 = (RelativeLayout) findViewById(R.id.wrapper3);
        final LinearLayout wrapper4 = (LinearLayout) findViewById(R.id.wrapper4);
        final LinearLayout tabWrapper = (LinearLayout) findViewById(R.id.tab_wrapper);
        final ImageView leaderBox = (ImageView) findViewById(R.id.leader_box);
        final ImageView belowBox = (ImageView) findViewById(R.id.below_box);

        wrapper.setVisibility(GONE);
        wrapper2.setVisibility(GONE);
        wrapper3.setVisibility(GONE);
        wrapper4.setVisibility(GONE);
        tabWrapper.setVisibility(GONE);

        lead.setVisibility(View.VISIBLE);
        below.setVisibility(View.INVISIBLE);//YC_SPEC INVISIBLE(VISIBLE)
        leaderBox.setVisibility(View.VISIBLE);
        belowBox.setVisibility(View.VISIBLE);
        mViewPager.setVisibility(View.VISIBLE);
        findViewById(R.id.search).setVisibility(GONE);

        setTeamList();
        setOrganization(finalCode3);
        try {
            logoSetter(fullCodeHash.get(finalCode3), 1);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void setOnClickPacker(String code, final String finalCode3, ViewGroup viewGroup) {
        if (!code.equals("noData")) {
            viewGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setOnClickPackerContents(finalCode3);
                    LinearLayout backButton = (LinearLayout) findViewById(R.id.select_backbutton);
                    try {
                        backButton.setVisibility(VISIBLE);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void setOnClickPacker(final String code, final String finalCode3, TextView text) {
        if (!code.equals("noData")) {
            text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("TAG", "onClick: oonclickedpacker: " + finalCode3);
                    setOnClickPackerContents(finalCode3);
                }
            });
        }
    }

    private void setOnClickPackerAbout(String code, final String finalCode3, ViewGroup text) {
        if (!code.equals("noData")) {
            text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("targetTeamCode", finalCode3);
                    if ((!jsonParser.parseStaffJson(finalCode3))) {
                        teamCode = (String) getResources().getText(R.string.시장);
                    } else {
                        teamCode = teamNameHash.get(finalCode3);
                        Log.d("targetTeamCode", finalCode3);
                    }

                    ImageView aboutImage = (ImageView) findViewById(R.id.about_image);
                    ScrollView scrollView = (ScrollView) findViewById(R.id.about_container);
                    scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                        @Override
                        public void onScrollChanged() {
                            autoOrganizationController();
                        }
                    });
                    try {
                        for (int i = 0; i < jsonParser.aboutPics.length; i++) {
                            try {
                                if (jsonParser.aboutPics[i].getString("section_cd").equals(finalCode3)) {
                                    aboutIndex = i;
                                    Log.d("aboutIndexChecker", String.valueOf(aboutIndex));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        if (aboutIndex == -1) {
                            jsonParser.loadJsonData();
                            jsonParser.parseTourJson();

                            File[] file;
                            file = new File[jsonParser.getTourCount()];

                            fullScreenViewerSetting(currentScene, file);
                            fullScreenViewerLayout(currentScene);

                            findViewById(R.id.notice_main).setVisibility(GONE);
                            findViewById(R.id.tour_content).setVisibility(VISIBLE);
                        } else {
                            Glide.with(context).load(Environment.getExternalStorageDirectory().getAbsolutePath()
                                    + "/KIOSKData/about/" + jsonParser.getAboutPicPath(aboutIndex)).asBitmap().skipMemoryCache(true).format(DecodeFormat.PREFER_ARGB_8888).into(aboutImage);
                            scrollView.scrollBy(0, 0);
                            findViewById(R.id.notice_main).setVisibility(VISIBLE);
                            findViewById(R.id.wrapper4).setVisibility(GONE);
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void setPacker(int groupPointId, LayoutInflater vi, int[] codes, int[] bgCodes, String[] strings) {
        //groupPoint = (ViewGroup) findViewById(R.id.business_packer5);
        //groupPoint - 패커 데이터 저장
        //vi - 인플레이트 데이터 저장
        //String[] codes - 코드 데이터 저장
        findViewById(R.id.team_list).setVisibility(View.GONE);
        findViewById(R.id.team_gu_sung).setVisibility(View.GONE);
        ViewGroup groupPoint = (ViewGroup) findViewById(groupPointId);
        ViewGroup itemPoint;
        if (groupPoint.getChildCount() != 0)
            groupPoint.removeAllViews();
        String code = null;
        for (int i = 0; i < codes.length; i++) { // // 사업소/의회 2
            try {
                if (codes[i] == 0) {
                    code = "noData";
                } else {
                    code = getResources().getString(codes[i]);
                }
                itemPoint = (ViewGroup) vi.inflate(R.layout.org_tmp, null);
                View item = vi.inflate(R.layout.org_layout, null);
                TextView text = (TextView) item.findViewById(R.id.textview);
                text.setText(strings[i]);
                setPackerStyle(i, text, bgCodes);


                final String finalCode3 = code;
                Log.d("finalCode3", code);
                setOnClickPacker(code, finalCode3, text);

                itemPoint.addView(item);
                groupPoint.addView(itemPoint);
            } catch (Exception e) {
                Log.d("printError", "error");
                e.printStackTrace();
            }
        }
    }

    private void setFoodPacker(int groupPointId, LayoutInflater vi, String date, String menu, int dayOfWeek, boolean now) {
        //groupPoint = (ViewGroup) findViewById(R.id.business_packer5);
        //groupPoint - 패커 데이터 저장
        //vi - 인플레이트 데이터 저장
        //String[] codes - 코드 데이터 저장
        ViewGroup groupPoint = (ViewGroup) findViewById(groupPointId);
        ViewGroup itemPoint;
        if (groupPoint.getChildCount() != 0)
            groupPoint.removeAllViews();
        String dayOfWeekString = null;
        switch (dayOfWeek) {
            case 2:
                dayOfWeekString = "월요일";
                break;
            case 3:
                dayOfWeekString = "화요일";
                break;
            case 4:
                dayOfWeekString = "수요일";
                break;
            case 5:
                dayOfWeekString = "목요일";
                break;
            case 6:
                dayOfWeekString = "금요일";
                break;
        }
        try {
            itemPoint = (ViewGroup) vi.inflate(R.layout.org_tmp, null);
            View item = vi.inflate(R.layout.food_list, null);
            TextView foodDate = (TextView) item.findViewById(R.id.food_date);
            TextView foodMenu = (TextView) item.findViewById(R.id.food_contents);
            TextView foodDayOfWeek = (TextView) item.findViewById(R.id.food_day_of_week);

            if (typefaceBarunGothic == null) {
                typefaceBarunGothic = Typeface.createFromAsset(getAssets(), "NanumBarunGothic.otf");
            }
            foodDate.setTypeface(typefaceBarunGothic);
            foodMenu.setTypeface(typefaceBarunGothic);
            foodDayOfWeek.setTypeface(typefaceBarunGothic);

            menu = menu.replace("n", "\n");

            foodDate.setText(date);
            foodMenu.setText(menu);
            foodDayOfWeek.setText(dayOfWeekString);
            if (now == true) {
                item.findViewById(R.id.food_box).setBackgroundResource(R.drawable.food_over_box);
                item.findViewById(R.id.food_date).setBackgroundResource(R.drawable.food_date_over_box);
                item.findViewById(R.id.food_circle).setBackgroundResource(R.drawable.food_over_circle);
            }

            itemPoint.addView(item);
            groupPoint.addView(itemPoint);
        } catch (Exception e) {
            Log.d("printError", "error");
            e.printStackTrace();
        }
    }

    private int pagerMovement(boolean[] direction, int currentPage, int pagerCounter) {
        if (isRunning[0] == true)
            return pagerCounter;

        setViewPagerScrollSpeed(mViewPager, 1);
        if (pagerCounter == 10 || pagerCounter == 5) {
            Log.d("pagerMovement", "runPagerMovement" + teamNum);
            if (currentPage >= teamNum - 3) {
                direction[0] = true;
            } else if (currentPage <= 0) {
                direction[0] = false;
            }
            if (currentPage < 3 && mViewPager.getChildCount() < 5 && pagerCounter != 10) {
                currentPage += 2;
                Log.d("pagerMovement", "runPagerMovement + current");
            } else if (currentPage > mViewPager.getChildCount() - 3 && pagerCounter != 10) {
                currentPage -= 2;
                Log.d("pagerMovement", "runPagerMovement - current");
            }
            if (pagerCounter == 10) {
                /*if (teamNum <= currentPage + 3) {
                    Log.d("pagerMovement", "runPagerMovement + current page+3");
                    mViewPager.setCurrentItem(currentPage + teamNum - currentPage + 2);
                } else {*/
                Log.d("pagerMovement", "runPagerMovement + current page");
                mViewPager.setCurrentItem(currentPage + 1);
                //}//190416 쓸데없는 if문 제거완료.
            } else if (!direction[0]) {
                Log.d("pagerMovement", "runPagerMovement + current");
                mViewPager.setCurrentItem(currentPage + 1);
            } else {
                Log.d("pagerMovement", "runPagerMovement - current");
                mViewPager.setCurrentItem(currentPage - 1);
            }

            pagerCounter = 0;
        } else {
            pagerCounter++;
        }
        return pagerCounter;
    }

    private void setTeamList() {
        try {
            findViewById(R.id.team_list).setVisibility(View.VISIBLE);
            findViewById(R.id.team_gu_sung).setVisibility(View.VISIBLE);
        } catch (NullPointerException e) {
            e.printStackTrace();
            Log.d("setTeamListTag", "Don't have layout. is it Leader?");
        }
        try {
            int teamCount = jsonParser.getTeamCount();
            int maxHeight = 225;
            boolean countTeams = false;
            final RelativeLayout threeTeam = (RelativeLayout) findViewById(R.id.three_container);
            final RelativeLayout sixTeam = (RelativeLayout) findViewById(R.id.six_container);

            TextView[] tContainer = {(TextView) findViewById(R.id.t_container1), (TextView) findViewById(R.id.t_container2), (TextView) findViewById(R.id.t_container3)};

            FrameLayout[] tBorder = {(FrameLayout) findViewById(R.id.t_horizontal_bar1), (FrameLayout) findViewById(R.id.t_horizontal_bar2)};

            TextView[] bContainer = {(TextView) findViewById(R.id.b_container1), (TextView) findViewById(R.id.b_container2), (TextView) findViewById(R.id.b_container3),
                    (TextView) findViewById(R.id.b_container4), (TextView) findViewById(R.id.b_container5), (TextView) findViewById(R.id.b_container6),
                    (TextView) findViewById(R.id.b_container7), (TextView) findViewById(R.id.b_container8), (TextView) findViewById(R.id.b_container9),
                    (TextView) findViewById(R.id.b_container10), (TextView) findViewById(R.id.b_container11), (TextView) findViewById(R.id.b_container12),
                    (TextView) findViewById(R.id.b_container13), (TextView) findViewById(R.id.b_container14), (TextView) findViewById(R.id.b_container15),
                    (TextView) findViewById(R.id.b_container16), (TextView) findViewById(R.id.b_container17), (TextView) findViewById(R.id.b_container18)};

            FrameLayout[] bBorder = {(FrameLayout) findViewById(R.id.horizontal_bar1), (FrameLayout) findViewById(R.id.horizontal_bar2), (FrameLayout) findViewById(R.id.horizontal_bar3),
                    (FrameLayout) findViewById(R.id.horizontal_bar4), (FrameLayout) findViewById(R.id.horizontal_bar5), (FrameLayout) findViewById(R.id.horizontal_bar6),
                    (FrameLayout) findViewById(R.id.horizontal_bar7), (FrameLayout) findViewById(R.id.horizontal_bar8)};
            maxHeight = maxHeight - (teamCount + 1) * 3 + 3;
            if (teamCount <= 2) {
                threeTeam.setVisibility(View.GONE);
                sixTeam.setVisibility(View.GONE);
                findViewById(R.id.team_gu_sung).setVisibility(View.GONE);
                findViewById(R.id.team_list).setVisibility(View.GONE);
            } else if (teamCount == 3) {
                threeTeam.setVisibility(View.VISIBLE);
                sixTeam.setVisibility(View.GONE);
            } else if (teamCount > 18) {
                findViewById(R.id.team_gu_sung).setVisibility(View.GONE);
                findViewById(R.id.team_list).setVisibility(View.GONE);
            } else {
                threeTeam.setVisibility(View.GONE);
                sixTeam.setVisibility(View.VISIBLE);
                countTeams = true;
                maxHeight = maxHeight + ((teamCount + 1) * 3) / 2;
            }

            for (int i = 0; i <= teamCount; i++) {
                if (teamCount == 3 && i != 0) {
                    int tmpIndex = i - 1;
                    tContainer[tmpIndex].setText(jsonParser.getTeamName(i).replace(" ", ""));
                    tContainer[tmpIndex].setTextSize(maxHeight / (teamCount) / 3);
                    tContainer[tmpIndex].getLayoutParams().height = maxHeight / (teamCount);
                    tContainer[tmpIndex].setVisibility(View.VISIBLE);
                    if (tmpIndex != 0)
                        tBorder[tmpIndex - 1].setVisibility(View.VISIBLE);
                    final int finalI = i;
                    tContainer[tmpIndex].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            boolean direction[] = {false};
                            try {
                                int pageNum = pagePositionHash.get(finalI);
                                Log.d("pagerMovementNumb", String.valueOf(pageNum - 1));
                                pagerCounter = pagerMovement(direction, pageNum - 1, 10);

                            } catch (NullPointerException e) {
                                Log.d("retry", "logs");
                            }
                        }
                    });
                } else if (countTeams && i != 0) {
                    int tmpIndex = i - 1;
                    bContainer[tmpIndex].setText(jsonParser.getTeamName(i).replace(" ", ""));
                    bContainer[tmpIndex].setTextSize(maxHeight / ((int) Math.ceil((double) (teamCount + 1) / 2)) / 3);
                    if (bContainer[tmpIndex].getTextSize() <= (maxHeight / 5 / 3))
                        bContainer[tmpIndex].setTextSize(maxHeight / 5 / 3);
                    bContainer[tmpIndex].getLayoutParams().height = maxHeight / (int) Math.ceil((double) (teamCount) / 2);
                    bContainer[tmpIndex].setVisibility(View.VISIBLE);
                    if (tmpIndex > 1 && tmpIndex % 2 == 0)
                        bBorder[tmpIndex / 2 - 1].setVisibility(View.VISIBLE);
                    final int finalI = i;
                    bContainer[tmpIndex].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            boolean direction[] = {false};
                            try {
                                int pageNum = pagePositionHash.get(finalI);
                                Log.d("pagerMovementNumb", String.valueOf(pageNum - 1));
                                pagerCounter = pagerMovement(direction, pageNum - 1, 10);
                            } catch (NullPointerException e) {
                                Log.d("retry", "logs");
                            }
                        }
                    });
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Organization Failed", "Failed");
            /*
            if (SearchListFragment.bgWork != null) {//서치리스트가 가동중이면
                SearchListFragment.bgWork.cancel(true);//인터럽트 하고
                SearchListFragment.bgWork = null;//널로 변경( 아 초기화 인듯 )
            }

            startActivity(new Intent(MainActivity.this, MainActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("scene", "main").putExtra("sensor", true));//갤러리로 자동 이동
            finish();
            */
        }
    }

    private Iterator sortSelectSetString() {
        HashMap<String, Integer> sortHash = new HashMap<>();
        for (int j = 0; j < selectSetString.length; j++) {
            for (int i = 0; i < jsonParser.wholeGetTeams[0].length; i++) {
                try {
                    if (teamNameHash.get(selectSetString[j]).equals(jsonParser.wholeGetTeams[0][i].get("section_fullcode").toString())) {
                        Log.d("sortHash", selectSetString[j]);
                        sortHash.put(selectSetString[j], Integer.parseInt(jsonParser.wholeGetTeams[0][i].get("section_sort").toString()));
                        break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        final HashMap<String, Integer> sortHashf = sortHash;
        List<String> list = new ArrayList();
        list.addAll(sortHashf.keySet());
        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                Object v1 = sortHashf.get(o1);
                Object v2 = sortHashf.get(o2);
                return ((Comparable) v2).compareTo(v1);
            }
        });
        Collections.reverse(list);
        Iterator it = list.iterator();
        return it;
    }
}

