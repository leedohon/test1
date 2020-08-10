package lab.nicc.kioskyoungcheon;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;
import com.tsengvn.typekit.TypekitContextWrapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.concurrent.atomic.AtomicInteger;

import static lab.nicc.kioskyoungcheon.MainActivity.context;
import static lab.nicc.kioskyoungcheon.MainActivity.jsonParser;

/**
 * Created by user on 2018-08-16.
 */

public class SeatPlannerActivity extends AppCompatActivity {
    final int gridX = 96;
    final int gridY = 60;
    static Timer timer = null;
    int bgSetting;
    private int maxPosition = 0;
    final AtomicInteger sNextGeneratedId =
            new AtomicInteger(1);

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }

    ArrayList<Integer> ids;
    HashMap<Integer, Integer> idsHash;
    HashMap<Integer, Integer> positionHash;
    ArrayList<Integer> hashList;
    int[] items = {R.drawable.big_sofa, R.drawable.big_sofa2, R.drawable.big_sofa3,
            R.drawable.small_sofa, R.drawable.small_sofa2, R.drawable.small_sofa3,
            R.drawable.conf_big_table, R.drawable.conf_big_table2, R.drawable.conf_big_table3,
            R.drawable.conf_table, R.drawable.conf_table2, R.drawable.conf_table3,
            R.drawable.n_door_1, R.drawable.n_door_2,
            R.drawable.table, R.drawable.table2, R.drawable.table3,
            R.drawable.flowerpot, R.drawable.flowerpot_2,
            R.drawable.h_partition, R.drawable.v_partition,
            R.drawable.water, R.drawable.print, R.drawable.big_bookcase,
            R.drawable.window, R.drawable.window_2
    };
    int[] items_width = {260, 275, 248,
            97, 97, 100,
            210, 214, 214,
            132, 151, 96,
            150, 150,
            144, 155, 96,
            76, 96,
            29, 21,
            70, 96, 257,
            107, 91
    };
    int[] items_height = {134, 121, 112,
            102, 98, 109,
            138, 138, 148,
            130, 143, 144,
            150, 150,
            74, 74, 138,
            78, 90,
            203, 108,
            84, 108, 92,
            47, 47
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_settings);
        MainActivity.getRnd();
        RelativeLayout overview = (RelativeLayout) findViewById(R.id.seat_overview);
        Glide.with(context).load(getResources().getIdentifier("new_main_bg_" + MainActivity.getRnd(), "drawable",
                context.getPackageName())).into(new ViewTarget<RelativeLayout, GlideDrawable>(overview) {
            @Override
            public void onResourceReady(GlideDrawable resource, GlideAnimation anim) {
                RelativeLayout myView = this.view;
                // Set your resource on myView and/or start your animation here.
                myView.setBackground(resource);
            }
        });

        for (int i = 0; i < items.length; i++) {
            items_height[i] *= 0.7;
            items_width[i] *= 0.7;
        }
        MainActivity.setIsSeatPopup(true);
        sendBroadcast(new Intent("lab.nicc").putExtra("settings", true));

        final Intent intent = new Intent(this.getIntent());
        final String teamCodeExtra = intent.getStringExtra("teamCode");
        jsonParser.parseStaffJson(teamCodeExtra);
        Log.d("Confirm Text", " " + teamCodeExtra);
        final FrameLayout seatFrame = (FrameLayout) findViewById(R.id.seat_layout);
        final LinearLayout draggableMates = (LinearLayout) findViewById(R.id.draggable_mates);
        final LinearLayout draggableItems = (LinearLayout) findViewById(R.id.draggable_items);
        TextView teamNameTextView = (TextView) findViewById(R.id.seat_team);
        String cut;
        if (MainActivity.fullCodeHash.get(teamCodeExtra).contains("&gt;")) {
            cut = MainActivity.fullCodeHash.get(teamCodeExtra).substring(MainActivity.fullCodeHash.get(teamCodeExtra).indexOf(";") + 1, MainActivity.fullCodeHash.get(teamCodeExtra).length());//; 다음부터 끝까지.
        } else if (MainActivity.fullCodeHash.get(teamCodeExtra).contains(">")) {
            cut = MainActivity.fullCodeHash.get(teamCodeExtra).substring(MainActivity.fullCodeHash.get(teamCodeExtra).indexOf(">") + 1, MainActivity.fullCodeHash.get(teamCodeExtra).length()); // > 다음부터 끝까지
        } else {
            cut = MainActivity.fullCodeHash.get(teamCodeExtra); // 그냥 쌩으로
        }
        teamNameTextView.setText(cut + " 좌석배치도 설정");
        draggableMates.removeAllViews();

        final int[] numX = new int[gridX];
        final int[] numY = new int[gridY];
        for (int i = 0; i < gridX; i++)
            numX[i] = (1636 / gridX) * i;
        for (int i = 0; i < gridY; i++)
            numY[i] = (761 / gridY) * i;

        for (int i = 0; i < numX.length; i++) {
            for (int j = 0; j < numY.length; j++) {

                LinearLayout dot = new LinearLayout(this);
                dot.setLayoutParams(new ViewGroup.LayoutParams(3, 5));
                //dot.setBackgroundResource(R.drawable.circle_border);

                seatFrame.addView(dot);
                dot.setX(numX[i]);
                dot.setY(numY[j]);
            }
        }
        final boolean[] setStatus = {false};
        final int[] tmpId = {-1, 0};
        final int[] prevX = {0};
        final int[] prevY = {0};
        final int[] oldpreX = {0};
        final int[] oldpreY = {0};

        View.OnDragListener dragListener = new View.OnDragListener() {
            @SuppressLint("ResourceType")
            @Override
            public boolean onDrag(View v, DragEvent event) {
                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_LOCATION:

                        final int[] width = {0};
                        final int[] height = {0};
                        final int[] rotation = {0};
                        int sizeValue[] = {0, 0};
                        boolean isOnItems;
                        ViewGroup viewgroup;
                        View view;
                        FrameLayout containView;
                        try {
                            view = (View) event.getLocalState();
                            viewgroup = (ViewGroup) view.getParent();
                            containView = (FrameLayout) seatFrame;

                            if (containView != null && setStatus[0] == false) {
                                containView.removeView(view);
                                Log.d("removeView", "first");
                            }
                            if (containView != null && setStatus[0] == true) {
                                View tmpView = (View) findViewById(tmpId[0]);
                                containView.removeView(tmpView);
                                Log.d("removeView", "second");
                            }
                            isOnItems = (v.getClass()).toString().equals("class android.widget.RelativeLayout");
                            if (viewgroup != draggableItems && setStatus[0] == false && view.getId() < 99999999) {
                                try {
                                    viewgroup.removeView(view);
                                    Log.d("removeView", "third");
                                } catch (NullPointerException e) {
                                    Log.d("NullPointerException", "Already Null");
                                    e.printStackTrace();
                                }
                            } else if ((setStatus[0] == true || draggableItems == viewgroup) && (v == seatFrame ||
                                    !(v == findViewById(R.id.mates_scroll) || v == findViewById(R.id.draggable_mates) || v == findViewById(R.id.items_scroll) || v == findViewById(R.id.draggable_items)))) {
                                int index = -1;

                                RelativeLayout tmp = new RelativeLayout(getApplicationContext());
                                final ImageView tmpV = new ImageView(getApplicationContext());
                                ImageView but1 = new ImageView(getApplicationContext());
                                but1.setImageResource(R.drawable.right_btn);

                                tmpV.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                int i = 0;
                                for (int furnitureId : items) {
                                    sizeValue = setRotation(view, i, tmpV, tmp, furnitureId, sizeValue);
                                    i++;
                                }
                                try {
                                    tmpV.setImageDrawable(((ImageView) view).getDrawable());
                                } catch (ClassCastException e) {
                                    ((ImageView) ((RelativeLayout) view).getChildAt(2)).getDrawable();
                                    Log.d("seatSettingsCatch", "cacacac");
                                }
                                tmpV.setId((int) System.currentTimeMillis() / 999);
                                RelativeLayout.LayoutParams but1params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                                but1params.addRule(RelativeLayout.CENTER_IN_PARENT, tmpV.getId());
                                but1.setLayoutParams(but1params);
                                tmpV.setTag("itV");

                                tmp.addView(tmpV);
                                tmp.addView(but1);

                                setShadowBuild(tmp);

                                tmp.setOnDragListener(this);
                                tmp.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                                    @Override
                                    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                                        if (right - left != 0) {
                                            width[0] = right - left;
                                            height[0] = bottom - top;
                                        }
                                    }
                                });
                                if (tmpId[0] == -1)
                                    tmpId[0] = (int) (System.currentTimeMillis() / 1000);
                                if (tmpId[1] == 0) {
                                    tmpId[1] = (int) view.getTag();
                                }
                                tmp.setTag(tmpId[1]);
                                view = tmp;
                                view.setId(tmpId[0]);

                                Log.d("tags", view.getTag() + "?");
                                setStatus[0] = true;

                                final View finalView = view;
                                but1.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        finalView.setRotation(finalView.getRotation() + 90);
                                        rotation[0] = (int) finalView.getRotation();
                                    }
                                });
                            }
                            if ((v == seatFrame || tmpId[0] != -1)) {
                                try {
                                    containView.addView(view);
                                    float inputX = event.getX() - view.getWidth() / 2;
                                    if (sizeValue[0] != 0)
                                        inputX = event.getX() - sizeValue[0] / 2;
                                    Log.d("InputX", inputX + " " + event.getX() + "ifs");
                                    int distance = Math.abs(numX[0] - (int) inputX);
                                    int idx = 0;
                                    for (int i = 1; i < numX.length; i++) {
                                        int cdistance = Math.abs(numX[i] - (int) inputX);
                                        if (cdistance < distance) {
                                            idx = i;
                                            distance = cdistance;
                                        }
                                    }
                                    int revisedX = numX[idx];

                                    float inputY = event.getY() - view.getHeight() / 2;
                                    if (sizeValue[1] != 0)
                                        inputY = event.getY() - sizeValue[1] / 2;
                                    distance = Math.abs(numY[0] - (int) inputY);
                                    int idy = 0;
                                    for (int i = 1; i < numY.length; i++) {
                                        int cdistance = Math.abs(numY[i] - (int) inputY);
                                        if (cdistance < distance) {
                                            idy = i;
                                            distance = cdistance;
                                        }
                                    }
                                    int revisedY = numY[idy];
                                    if (!isOnItems) {
                                        prevX[0] = revisedX + seatFrame.getScrollX();
                                        prevY[0] = revisedY + seatFrame.getScrollY();
                                    }
                                    view.setTranslationX(prevX[0]);
                                    view.setTranslationY(prevY[0]);

                                    Log.d("SCROLL", seatFrame.getScrollX() + " " + seatFrame.getScrollY() + "\n" + revisedX + " " + revisedY);
                                    Log.d("getId", String.valueOf(view.getId()));
                                    oldpreX[0] = -1;
                                    oldpreY[0] = -1;
                                    //Toast.makeText(context, String.valueOf(revisedX) + ", " + String.valueOf(revisedY), Toast.LENGTH_SHORT).show();
                                } catch (IllegalStateException e) {
                                    Log.d("SeatPlannerActivity", "IllegalStateException");
                                }
                            } else {
                                try {
                                    containView.addView(view);
                                    if (oldpreX[0] == oldpreY[0] && oldpreX[0] == -1) {
                                        oldpreX[0] = prevX[0];
                                        oldpreY[0] = prevY[0];
                                    }

                                    float inputX = event.getX() - view.getWidth() / 2;
                                    Log.d("InputX", inputX + " " + event.getX() + "els");
                                    int distance = Math.abs(numX[0] - (int) inputX);
                                    int idx = 0;
                                    for (int i = 1; i < numX.length; i++) {
                                        int cdistance = numX[i] - (int) inputX;
                                        if (cdistance < distance) {
                                            idx = i;
                                            distance = cdistance;
                                        }
                                    }
                                    int revisedX = numX[idx];

                                    float inputY = event.getY() - view.getHeight() / 2;
                                    distance = Math.abs(numY[0] - (int) inputY);
                                    int idy = 0;
                                    for (int i = 1; i < numY.length; i++) {
                                        int cdistance = numY[i] - (int) inputY;
                                        if (cdistance < distance) {
                                            idy = i;
                                            distance = cdistance;
                                        }
                                    }
                                    int revisedY = numY[idy];
                                    if (view.getId() < 99999999) {
                                /*view.setTranslationX(inputX + seatFrame.getScrollX() + oldpreX[0]-v.getWidth()/2);
                                view.setTranslationY(inputY + seatFrame.getScrollY() + oldpreY[0]v.getHeight()/2);

                                prevX[0] = (int) (inputX + seatFrame.getScrollX() + oldpreX[0]-v.getWidth()/2);
                                prevY[0] = (int) (inputY + seatFrame.getScrollY() + oldpreY[0]+v.getHeight()/2);*/
                                    } else if (String.valueOf(v.getId()).equals(String.valueOf(view.getId()))) {
                                        float posX;
                                        float posY;

                                        switch ((int) view.getRotation() % 360) {

                                            case 0:
                                                Log.d("tag", "0");
                                                posX = inputX + seatFrame.getScrollX() + prevX[0];
                                                posY = inputY + seatFrame.getScrollY() + prevY[0];
                                                if (posX < 0)
                                                    posX = 0;
                                                if (posY < 0)
                                                    posY = 0;
                                                view.setTranslationX(posX);
                                                view.setTranslationY(posY);

                                                prevX[0] = (int) posX;
                                                prevY[0] = (int) posY;
                                                break;

                                            case 90:
                                                Log.d("tag", "90");
                                                posX = -inputY - seatFrame.getScrollY() + prevX[0];
                                                posY = inputX + seatFrame.getScrollX() + prevY[0];

                                                if (posX < 0)
                                                    posX = 0;
                                                if (posY < 0)
                                                    posY = 0;
                                                view.setTranslationX(posX);
                                                view.setTranslationY(posY);

                                                prevX[0] = (int) posX;
                                                prevY[0] = (int) posY;
                                                break;

                                            case 180:
                                                Log.d("tag", "180");
                                                posX = -inputX - seatFrame.getScrollX() + prevX[0];
                                                posY = -inputY - seatFrame.getScrollY() + prevY[0];

                                                if (posX < 0)
                                                    posX = 0;
                                                if (posY < 0)
                                                    posY = 0;
                                                view.setTranslationX(posX);
                                                view.setTranslationY(posY);

                                                prevX[0] = (int) posX;
                                                prevY[0] = (int) posY;
                                                break;

                                            case 270:
                                                Log.d("tag", "270");
                                                posX = inputY + seatFrame.getScrollY() + prevX[0];
                                                posY = -inputX - seatFrame.getScrollX() + prevY[0];

                                                if (posX < 0)
                                                    posX = 0;
                                                if (posY < 0)
                                                    posY = 0;
                                                view.setTranslationX(posX);
                                                view.setTranslationY(posY);

                                                prevX[0] = (int) posX;
                                                prevY[0] = (int) posY;
                                                break;

                                            default:
                                                Log.d("chkRotation", "v = " + v.getRotation() + " view = " + view.getRotation());
                                                break;
                                        }
                                        view.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    }
                                    Log.d("view", String.valueOf(v.getId()));
                                    Log.d("else", "place");

                                    Log.d("getId", String.valueOf(view.getId()));
                                } catch (IllegalStateException e) {
                                    Log.d("ill", "ll");
                                }
                            }
                        } catch (NullPointerException e) {
                            e.printStackTrace();//로케이션 이동 중 문제 발생 시 어차피 재시도하기 때문에 정의해줄 필요 없음. 익셉션이 난다면 getParents에서 발생함. 전체를 다시 시도하는 것이 올바름.
                        }

                        break;
                    case DragEvent.ACTION_DROP:
                        try {
                            view = (View) event.getLocalState();
                            if (v == seatFrame) {
                                view = (View) event.getLocalState();

                                if (setStatus[0] == true) {
                                    view.setTag(tmpId[1]);
                                    view.setId(tmpId[0]);
                                    ids.add(view.getId());
                                    idsHash.put(view.getId(), (int) view.getTag());
                                    Log.d("is it Item??", "real?");
                                } else {
                                }

                                Log.d("getId", String.valueOf(view.getId()));
                                draggableItems.removeAllViews();
                                restoreItem(items, draggableItems, items_width, items_height);
                            } else if (v == draggableMates || v == findViewById(R.id.mates_scroll)) {
                                view = (View) event.getLocalState();
                                viewgroup = (ViewGroup) view.getParent();
                                if (viewgroup != draggableMates && viewgroup != findViewById(R.id.mates_scroll) && viewgroup != draggableItems) {
                                    double binaryMul = 0.5;
                                    int first = 0;
                                    int last = draggableMates.getChildCount() - 1;
                                    int mid = 0;
                                    int currentHash;
                                    try {
                                        while (first <= last) {
                                            mid = (first + last) / 2;
                                            if (positionHash.get(draggableMates.getChildAt(mid).getId()) == positionHash.get(view.getId())) {
                                                break;
                                            } else {
                                                if (positionHash.get(draggableMates.getChildAt(mid).getId()) > positionHash.get(view.getId())) {
                                                    last = mid - 1;
                                                } else {
                                                    first = mid + 1;
                                                }
                                            }
                                        }
                                        viewgroup.removeView(view);
                                        draggableMates.addView(view, first);
                                        Log.d("position Hash :", " " + first);
                                    } catch (IndexOutOfBoundsException e) {
                                        viewgroup.removeView(view);
                                        draggableMates.addView(view, positionHash.get(view.getId()));
                                        e.printStackTrace();
                                        //draggableMates.addView(view);
                                    } catch (NullPointerException e) {
                                        e.printStackTrace();
                                    }
                                    view.setX(0);
                                    view.setY(0);
                                }
                            } else if (v == draggableItems || v == findViewById(R.id.items_scroll)) {
                                view = (View) event.getLocalState();
                                viewgroup = (ViewGroup) view.getParent();
                                Log.d("draggableItems", view + " " + viewgroup);
                                if (viewgroup != draggableItems && viewgroup != findViewById(R.id.items_scroll) && viewgroup != draggableMates) {
                                    Log.d("Not draggableitems", view + " " + viewgroup);
                                    try {
                                        if (view.getId() > 99999999)
                                            viewgroup.removeView(view);
                                    } catch (NullPointerException e) {
                                    }
                                }
                            }
                            setStatus[0] = false;
                            tmpId[0] = -1;
                            tmpId[1] = 0;
                            Log.d("getChildCount", "ChildCount = " + ((LinearLayout) findViewById(R.id.draggable_mates)).getChildCount());
                        } catch (NullPointerException e) {
                            e.printStackTrace();//초기화 시 발생되는 문제이므로 처리할 필요 없음. 한번 더 시도하게 할 것.
                            Toast.makeText(context, "일시적 오류입니다. 다시 시도해주세요.", Toast.LENGTH_LONG).show();
                        }
                        break;
                }
                return true;
            }
        };
        seatFrame.setOnDragListener(dragListener);
        draggableMates.setOnDragListener(dragListener);
        findViewById(R.id.mates_scroll).setOnDragListener(dragListener);

        draggableItems.setOnDragListener(dragListener);
        findViewById(R.id.items_scroll).setOnDragListener(dragListener);

        ids = new ArrayList<>();
        idsHash = new HashMap<>();
        positionHash = new HashMap<>();

        SharedPreferences pref = getSharedPreferences("kiosk_data", MODE_WORLD_READABLE);

        Boolean presetMates = false;
        JSONObject presetMateObject = null;
        if (!pref.getString(intent.getStringExtra("teamCode"), "none").equals("none")) {
            presetMates = true;
            try {
                presetMateObject = new JSONObject(pref.getString(intent.getStringExtra("teamCode"), "none"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        int count = 0;
        for (int i = 0; i < jsonParser.getTeamCount() + 1; i++) {
            maxPosition++;
            LinearLayout newFrameName = new LinearLayout(getApplicationContext());
            newFrameName.setOrientation(LinearLayout.VERTICAL);
            newFrameName.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            while (true) {
                int result = sNextGeneratedId.get();
                int nv = result + 1;
                if (nv > 0x00FFFFFF)
                    nv = 1;

                if (sNextGeneratedId.compareAndSet(result, nv)) {
                    newFrameName.setId(result);
                    ids.add(result);
                    idsHash.put(result, -1 - i);
                    break;
                }
            }

            positionHash.put(newFrameName.getId(), count++);
            Log.d("count", ": " + count);
            if (!presetMates) {
                draggableMates.addView(newFrameName);
            } else {
                seatFrame.addView(newFrameName);
                try {
                    newFrameName.setX(presetMateObject.getJSONArray(String.valueOf(-1 - i))
                            .getJSONObject(0).getInt("x"));
                    newFrameName.setY(presetMateObject.getJSONArray(String.valueOf(-1 - i))
                            .getJSONObject(0).getInt("y"));
                } catch (JSONException e) {
                    seatFrame.removeView(newFrameName);
                    draggableMates.addView(newFrameName);
                }
            }

            Fragment spft;
            spft = new SeatFragment();
            final Bundle args = new Bundle();
            args.putString("team", jsonParser.getTeamName(i).replace("  ", " "));
            Log.d("teamNameLength", String.valueOf(jsonParser.getTeamName(i).length()));
            Log.d("teamNameString", jsonParser.getTeamName(i));
            spft.setArguments(args);

            FragmentTransaction transactions = getSupportFragmentManager().beginTransaction();
            transactions.add(newFrameName.getId(), spft).commit();

            setShadowBuild(newFrameName);

            for (int j = 0; j < jsonParser.getMateCount(i) + 1; j++) {
                maxPosition++;
                boolean crashCheck = false;
                LinearLayout newFrame = new LinearLayout(getApplicationContext());
                newFrame.setOrientation(LinearLayout.VERTICAL);
                newFrame.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                final int seatPos = jsonParser.getSeatPos(i, j);
                Log.d("seatPos", String.valueOf(seatPos));
                while (true) {
                    int result = sNextGeneratedId.get();
                    int nv = result + 1;
                    if (nv > 0x00FFFFFF)
                        nv = 1;

                    if (sNextGeneratedId.compareAndSet(result, nv)) {
                        newFrame.setId(result);
                        ids.add(result);
                        try {
                            Log.d("chk", String.valueOf(jsonParser.getMemberIdentifier(i, j)));
                            idsHash.put(result, jsonParser.getMemberIdentifier(i, j));
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                            crashCheck = true;
                        }
                        break;
                    }
                }
                if (!crashCheck) {
                    positionHash.put(newFrame.getId(), count++);
                    Log.d("count", ": " + count);
                    if (!presetMates) {
                        draggableMates.addView(newFrame);
                    } else {
                        seatFrame.addView(newFrame);
                        try {
                            newFrame.setX(presetMateObject.getJSONArray(String.valueOf(jsonParser.getMemberIdentifier(i, j)))
                                    .getJSONObject(0).getInt("x"));
                            newFrame.setY(presetMateObject.getJSONArray(String.valueOf(jsonParser.getMemberIdentifier(i, j)))
                                    .getJSONObject(0).getInt("y"));
                        } catch (JSONException e) {
                            seatFrame.removeView(newFrame);
                            draggableMates.addView(newFrame);
                        }
                    }

                    Fragment spf;
                    spf = new SeatFragment();

                    final Bundle arg = new Bundle();
                    arg.putInt("teamNum", i);
                    arg.putInt("id", jsonParser.getMemberIdentifier(i, j));
                    arg.putString("pic", jsonParser.getStaffPicPath(i, j));
                    arg.putString("team", jsonParser.getTeamName(i));
                    arg.putString("name", jsonParser.getName(i, j));
                    arg.putString("duty", jsonParser.getDuty(i, j));
                    spf.setArguments(arg);

                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.add(newFrame.getId(), spf).commit();

                    setShadowBuild(newFrame);
                }
            }
        }

        /*for (int i = 1; i <= count; i++) {
            LinearLayout newFrameName = new LinearLayout(getApplicationContext());
            newFrameName.setOrientation(LinearLayout.VERTICAL);
            newFrameName.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            while (true) {
                int result = sNextGeneratedId.get();
                int nv = result + 1;
                if (nv > 0x00FFFFFF)
                    nv = 1;

                if (sNextGeneratedId.compareAndSet(result, nv)) {
                    newFrameName.setId(result);
                    ids.add(result);
                    idsHash.put(result, -1 - i - count);
                    break;
                }
            }

            positionHash.put(newFrameName.getId(), count + i);
            Log.d("count", ": " + count);
            draggableMates.addView(newFrameName);
        }*/

        if (presetMateObject != null)
            for (int i = 0; i < presetMateObject.length(); i++) {
                try {
                    boolean currentData = false;
                    for (int j = 0; items.length > j; j++) {
                        if (items[j] == Integer.parseInt(presetMateObject.getJSONArray(presetMateObject.names().getString(i)).getJSONObject(0).getString("e"))) {
                            currentData = true;
                            break;
                        }
                    }
                    if (!currentData)
                        continue;
                    ImageView itemV = new ImageView(getApplicationContext());
                    final RelativeLayout item = new RelativeLayout(getApplicationContext());

                    ImageView but1 = new ImageView(getApplicationContext());
                    but1.setImageResource(R.drawable.right_btn);
                    RelativeLayout.LayoutParams but1params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                    but1params.addRule(RelativeLayout.CENTER_IN_PARENT, itemV.getId());
                    but1.setLayoutParams(but1params);

                    itemV.setImageResource(Integer.parseInt(presetMateObject.getJSONArray(presetMateObject.names().getString(i)).getJSONObject(0).getString("e")));
                    itemV.setLayoutParams(new ViewGroup.LayoutParams(Integer.parseInt(presetMateObject.getJSONArray(presetMateObject.names().getString(i)).getJSONObject(0).getString("w")),
                            Integer.parseInt(presetMateObject.getJSONArray(presetMateObject.names().getString(i)).getJSONObject(0).getString("h"))));
                    item.setRotation(Integer.parseInt(presetMateObject.getJSONArray(presetMateObject.names().getString(i)).getJSONObject(0).getString("r")) % 360);
                    item.setTag(Integer.parseInt(presetMateObject.getJSONArray(presetMateObject.names().getString(i)).getJSONObject(0).getString("e")));
                    item.setLayoutParams(new ViewGroup.LayoutParams(Integer.parseInt(presetMateObject.getJSONArray(presetMateObject.names().getString(i)).getJSONObject(0).getString("w")),
                            Integer.parseInt(presetMateObject.getJSONArray(presetMateObject.names().getString(i)).getJSONObject(0).getString("h"))));

                    setShadowBuild(item);

                    item.addView(itemV);
                    itemV.setTag("itV");
                    item.addView(but1);
                    item.setOnDragListener(dragListener);
                    seatFrame.addView(item);

                    but1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            item.setRotation(item.getRotation() + 90 % 360);
                        }
                    });

                    item.setX(presetMateObject.getJSONArray(presetMateObject.names().getString(i)).getJSONObject(0).getInt("x"));
                    item.setY(presetMateObject.getJSONArray(presetMateObject.names().getString(i)).getJSONObject(0).getInt("y"));
                    item.setId(Integer.parseInt(presetMateObject.names().getString(i)));

                    ids.add(Integer.parseInt(presetMateObject.names().getString(i)));
                    idsHash.put(Integer.parseInt(presetMateObject.names().getString(i)), (int) item.getTag());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        /////////////////////////////////////////////////
        //////////// 우측 가구 아이템 리스트 ////////////
        /////////////////////////////////////////////////
        restoreItem(items, draggableItems, items_width, items_height);
        final SharedPreferences.Editor editor = pref.edit();
        findViewById(R.id.commit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject seatObject = new JSONObject();

                for (int i = 0; i < ids.size(); i++) {
                    try {
                        JSONArray tmpArray = new JSONArray();
                        JSONObject tmpObject;

                        if (seatFrame.findViewById(ids.get(i)).getTag() == null) {
                            tmpObject = new JSONObject("{\"x\":" + (int) seatFrame.findViewById(ids.get(i)).getX() + "," +
                                    "\"y\":" + (int) seatFrame.findViewById(ids.get(i)).getY() + "}");
                            Log.d("tmpObject", String.valueOf(tmpObject));
                            tmpArray.put(tmpObject);
                            seatObject.put(String.valueOf(idsHash.get(ids.get(i))), tmpArray);
                        } else {
                            tmpObject = new JSONObject("{\"x\":" + (int) seatFrame.findViewById(ids.get(i)).getX() + "," +
                                    "\"y\":" + (int) seatFrame.findViewById(ids.get(i)).getY() + "," +
                                    "\"e\":" + String.valueOf(seatFrame.findViewById(ids.get(i)).getTag()) + "," +
                                    "\"r\":" + String.valueOf(seatFrame.findViewById(ids.get(i)).getRotation()) + "," +
                                    "\"w\":" + String.valueOf(seatFrame.findViewById(ids.get(i)).findViewWithTag("itV").getWidth()) + "," +
                                    "\"h\":" + String.valueOf(seatFrame.findViewById(ids.get(i)).findViewWithTag("itV").getHeight()) + "}");
                            tmpArray.put(tmpObject);
                            seatObject.put(String.valueOf(ids.get(i)), tmpArray);
                            Log.d("tmpObject", String.valueOf(tmpObject));
                        }
                    } catch (JSONException | NullPointerException e) {
                        e.printStackTrace();
                        Log.d("tmpObject", String.valueOf(e));
                    }
                }

                editor.putString(intent.getStringExtra("teamCode"), seatObject.toString()).commit();
                editor.putString("teamCode_bg" + teamCodeExtra, "seatBg0" + bgSetting).commit();
                MainActivity.setIsSeatPopup(false);
                finish();
            }
        });

        findViewById(R.id.bg_btn01).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setBackgroundResource(R.drawable.bg_selx);
                findViewById(R.id.bg_btn02).setBackgroundResource(R.drawable.bg_btnx);
                findViewById(R.id.bg_btn03).setBackgroundResource(R.drawable.bg_btnx);
                findViewById(R.id.seat_layout).setBackgroundResource(R.drawable.seat_bg01i);
                bgSetting = 1;
            }
        });

        findViewById(R.id.bg_btn02).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setBackgroundResource(R.drawable.bg_selx);
                findViewById(R.id.bg_btn01).setBackgroundResource(R.drawable.bg_btnx);
                findViewById(R.id.bg_btn03).setBackgroundResource(R.drawable.bg_btnx);
                findViewById(R.id.seat_layout).setBackgroundResource(R.drawable.seat_bg02i);
                bgSetting = 2;
            }
        });

        findViewById(R.id.bg_btn03).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setBackgroundResource(R.drawable.bg_selx);
                findViewById(R.id.bg_btn01).setBackgroundResource(R.drawable.bg_btnx);
                findViewById(R.id.bg_btn02).setBackgroundResource(R.drawable.bg_btnx);
                findViewById(R.id.seat_layout).setBackgroundResource(R.drawable.seat_bg03i);
                bgSetting = 3;
            }
        });

        String bgMode = pref.getString("teamCode_bg" + teamCodeExtra, "seatBg01");

        switch (bgMode) {
            case "seatBg01":
                findViewById(R.id.bg_btn01).setBackgroundResource(R.drawable.bg_selx);
                findViewById(R.id.bg_btn02).setBackgroundResource(R.drawable.bg_btnx);
                findViewById(R.id.bg_btn03).setBackgroundResource(R.drawable.bg_btnx);
                findViewById(R.id.seat_layout).setBackgroundResource(R.drawable.seat_bg01i);
                bgSetting = 1;
                break;

            case "seatBg02":
                findViewById(R.id.bg_btn01).setBackgroundResource(R.drawable.bg_btnx);
                findViewById(R.id.bg_btn02).setBackgroundResource(R.drawable.bg_selx);
                findViewById(R.id.bg_btn03).setBackgroundResource(R.drawable.bg_btnx);
                findViewById(R.id.seat_layout).setBackgroundResource(R.drawable.seat_bg02i);
                bgSetting = 2;
                break;

            case "seatBg03":
                findViewById(R.id.bg_btn01).setBackgroundResource(R.drawable.bg_btnx);
                findViewById(R.id.bg_btn02).setBackgroundResource(R.drawable.bg_btnx);
                findViewById(R.id.bg_btn03).setBackgroundResource(R.drawable.bg_selx);
                findViewById(R.id.seat_layout).setBackgroundResource(R.drawable.seat_bg03i);
                bgSetting = 3;
                break;
        }

        findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.setIsSeatPopup(false);
                finish();
            }
        });
    }

    private int currentX, currentY;

    /*@Override
    public boolean onTouchEvent(MotionEvent event) {
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.seat_layout);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                currentX = (int) event.getRawX();
                currentY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                int x = (int) event.getRawX();
                int y = (int) event.getRawY();
                if (x < currentX) {
                    //frameLayout.setLayoutParams(new LinearLayout.LayoutParams(frameLayout.getWidth() + currentX - x + 100, frameLayout.getHeight()));
                    frameLayout.scrollBy(currentX/100, 0);
                } else if (x > currentX) {
                    //frameLayout.setLayoutParams(new LinearLayout.LayoutParams(frameLayout.getWidth() + x - currentX - 100, frameLayout.getHeight()));
                    frameLayout.scrollBy(-(currentX/100), 0);
                }

                if (y < currentY) {
                    //frameLayout.setLayoutParams(new LinearLayout.LayoutParams(frameLayout.getWidth(), frameLayout.getHeight() + currentY - y + 100));
                    frameLayout.scrollBy(0, currentY/100);
                } else if (y > currentY) {
                    //frameLayout.setLayoutParams(new LinearLayout.LayoutParams(frameLayout.getWidth(), frameLayout.getHeight() + y - currentY - 100));
                    frameLayout.scrollBy(0, -(currentY/100));
                }
                currentX = x;
                currentY = y;
                break;
        }
        return super.onTouchEvent(event);
    }*/

    @Override
    public void onDestroy() {
        super.onDestroy();

        //sendBroadcast(new Intent("lab.nicc").putExtra("settings", false));
        //왜 넣은걸까??
    }

    private void restoreItem(int[] items, LinearLayout draggableItems, int[] item_width, int[] item_height) {
        for (int i = 0; i < items.length; i++) {
            ImageView item = new ImageView(this);
            item.setImageResource(items[i]);
            item.setLayoutParams(new ViewGroup.LayoutParams(item_width[i], item_height[i]));
            item.setTag(items[i]);
            draggableItems.addView(item);

            setShadowBuild(item);
        }
    }

    private int[] setRotation(View view, int index, ImageView tmpV, RelativeLayout tmp, int drawableId, int[] sizeValue) {
        if ((int) (view.getTag()) == drawableId) {
            sizeValue = new int[]{items_width[index], items_height[index]};
            if (tmpV.getRotation() / 90 == 1 || tmpV.getRotation() / 90 == 3) {
                tmpV.setLayoutParams(new ViewGroup.LayoutParams(items_height[index], items_width[index]));
                tmp.setLayoutParams(new ViewGroup.LayoutParams(items_height[index], items_width[index]));
                Log.d("compare", drawableId + "land");
            } else {
                tmpV.setLayoutParams(new ViewGroup.LayoutParams(items_width[index], items_height[index]));
                tmp.setLayoutParams(new ViewGroup.LayoutParams(items_width[index], items_height[index]));
                Log.d("compare", drawableId + "port");
            }
            Log.d("compare", String.valueOf(drawableId));
            return sizeValue;
        }
        return sizeValue;
    }

    private void setShadowBuild(View v) {
        v.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        double rotationRad = Math.toRadians(v.getRotation());
                        final int w = (int) (v.getWidth() * v.getScaleX());
                        final int h = (int) (v.getHeight() * v.getScaleY());
                        double s = Math.abs(Math.sin(rotationRad));
                        double c = Math.abs(Math.cos(rotationRad));
                        final int width = (int) (w * c + h * s);
                        final int height = (int) (w * s + h * c);
                        View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v) {
                            @Override
                            public void onDrawShadow(Canvas canvas) {
                                canvas.scale(v.getScaleX(), v.getScaleY(), width / 2,
                                        height / 2);
                                canvas.rotate(v.getRotation(), width / 2, height / 2);
                                canvas.translate((width - v.getWidth()) / 2,
                                        (height - v.getHeight()) / 2);
                                super.onDrawShadow(canvas);
                            }

                            @Override
                            public void onProvideShadowMetrics(Point shadowSize,
                                                               Point shadowTouchPoint) {
                                shadowSize.set(width, height);
                                shadowTouchPoint.set(shadowSize.x / 2, shadowSize.y / 2);
                            }
                        };

                        v.startDrag(null, shadowBuilder, v, 0);
                }
                return false;
            }
        });

    }

    public int[] getItems() {
        return items;
    }
}
