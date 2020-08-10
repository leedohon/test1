package lab.nicc.kioskyoungcheon;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import static android.view.View.GONE;

/**
 * Created by NG1 on 2017-08-02.
 */

public class OrganizationFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    static final long AUTO_POPUP_CLOSE_MS = 10000;
    int failCount = 0;
    MainActivity parent;
    JsonParser jsonParser;
    int teamNum, mateNum, tmp = 0;
    HashMap<Integer, Integer> mateSeat = new HashMap<>();
    String passwordInput = "";
    int division = 0;
    boolean leaders;
    boolean addImage = false;
    boolean leadersOnTop = false;

    private static Timer popupTimer;

    public static OrganizationFragment newInstance(int sectionNumber) {
        OrganizationFragment fragment = new OrganizationFragment();

        Bundle args = new Bundle();

        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        Log.d("fragmentCounts", String.valueOf(args));
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.organization_main, container, false);

        //TextView textView = (TextView) rootView.findViewById(R.id.section_label);


        parent = (MainActivity) this.getActivity();
        parent.allPageCounter+=1;
        jsonParser = parent.jsonParser;

        teamNum = getArguments().getInt(ARG_SECTION_NUMBER) - parent.exPageFix;//exPageFix를 뺌.

        leaders = false;
        try {
            Log.d("OrganizationFragment", "DevideHash = " + parent.allPageCounter);
            division = parent.teamLengthHash.get(parent.allPageCounter-2);//???
            Log.d("OrganizationFragment", "DevideNumber = " + division);
        } catch (NullPointerException e) {
            e.printStackTrace();
            division = 8;
        }
        if (leaders) {
            division /= 2;
        }
        try {
            parent.devideCounter += parent.teamLengthHash.get(parent.allPageCounter - 3);
        } catch(Exception e){
            e.printStackTrace();
        }

        try {
            if (parent.presentMates != 0 && parent.presentMates + division == jsonParser.getMateCount(teamNum) && (float) parent.highestHash.get(teamNum) % division == 0 && teamNum == 1) {
                teamNum++;
                parent.exPageFix--;
            }

            mateNum = jsonParser.getMateCount(teamNum);
        } catch (ArrayIndexOutOfBoundsException | NullPointerException e) {
            return rootView;
            //mateNum = jsonParser.getMateCount(teamNum);
        }


        try {
            mateNum = jsonParser.getMateCount(teamNum);
        } catch (ArrayIndexOutOfBoundsException e) {
            return rootView;
            //mateNum = jsonParser.getMateCount(teamNum);
        }
        try {
            if (jsonParser.getTeamName(teamNum).replace("  ", "").equals("전문위원")) {
                leaders = true;
                rootView = inflater.inflate(R.layout.organization_leaders, container, false);
            } else if (jsonParser.getTeamName(teamNum).replace("  ", "").equals("차량등록담당")) {
                addImage = true;
                rootView.findViewById(R.id.organization_main_image).setVisibility(View.VISIBLE);
                rootView.findViewById(R.id.organization_main_image).setBackgroundResource(R.drawable.organization_infomation);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        int tmp = parent.highestHash.get(teamNum);
        try {
            Log.d("OrganizationFragment", "DevideCounter = " + parent.devideCounter);
            if (parent.pastTeam == teamNum) {
                if (!leaders) {
                    createLeaderFragment(true); // 리더 있는부분 팀프래그먼트 완성
                }
                else
                    createLeaderFragment(teamNum * division, jsonParser.getSeatPos(teamNum, parent.exPage * 3) - (parent.swHash.get(teamNum) + 1 - parent.exPage) * division, true);
                int forCounter = 0;
                for (int i = 0; i < jsonParser.getMateCount(teamNum) + 1; i++) {
                    if (!leaders) {
                        createTeamFragment(teamNum, i, jsonParser.getSeatPos(teamNum, i) - parent.devideCounter, jsonParser.getSeatPos(teamNum, i));
                        Log.d("OrganizationFragment", "Counting.." + i);
                        Log.d("OrganizationFragment", "Counting.." + jsonParser.getSeatPos(teamNum, i));
                    }
                    else
                        createLeaderFragment(i, jsonParser.getSeatPos(teamNum, i) - (parent.swHash.get(teamNum) + 1 - parent.exPage) * division, false);
                }
                Log.d("OrganizationFragment", "teamNum = " + teamNum);
                parent.exPage--;
                parent.pageCounter++;

                if (parent.sucCount == jsonParser.getMateCount(teamNum)) {
                    parent.exPage = 0;
                    parent.sucCount = 0;
                    parent.pageIndex += parent.pageCounter - 1;
                    parent.pagePositionHash.put(teamNum, parent.pageIndex - parent.pageCounter);
                    Log.d("pagePositionteamNum1", "teamNum = " + teamNum + ", position = " + (parent.pageIndex - parent.pageCounter));
                    parent.pageCounter = 1;
                }

                if (parent.exPageFix != 0 && parent.exPage != 0) {
                    parent.exPageFix++;
                }//else부 parent.sucCount 초기화 제거 190416
                containerListeners(rootView, parent.chkFirst);
                Log.d("failcounter", String.valueOf(failCount) + "sucCounter" + parent.sucCount + " " + jsonParser.getMateCount(teamNum));
                failCount = 0;

                return rootView;
            } else {
                parent.pageIndex += parent.pageCounter;
                parent.pagePositionHash.put(teamNum, parent.pageIndex - parent.pageCounter);
                Log.d("pagePositionteamNum4", "teamNum = " + teamNum + ", position = " + (parent.pageIndex - parent.pageCounter) + " " + parent.sucCount + " ");
                leadersOnTop = true;
                createLeaderFragment(false);
                containerListeners(rootView, parent.chkFirst);
                parent.pastTeam = teamNum;
                parent.presentMates = 0;
                Log.d("failcounter2", String.valueOf(failCount));
                failCount = 0;

                    Log.d("OrganizationFragment", "DevideCounter reset");
                    parent.devideCounter = 0;

            } // else부 parent.sucCount 초기화 제거 190416


            if ((float) tmp / division > 1) { // 몇 개 이상이면 페이지를 늘린다.
                parent.exPage = (float) tmp % division == 0 ? (int) Math.ceil((float) tmp / division) : (int) Math.ceil((float) tmp / division) - 1;
                parent.exPageFix++;

                if (parent.presentMates != 0 && parent.presentMates + division == tmp) {
                    parent.exPage--;
                    parent.pagerCounter++;
                    parent.presentMates = 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rootView;
    }

    public void createLeaderFragment(boolean isEmpty) {
        Fragment leaderFrag = new LeaderFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        if (isEmpty)
            parent.chkFirst = false;
        else
            parent.chkFirst = true;
        Bundle leaderArgs = new Bundle();

        int leaderSeat = getTeamLeaderSeat(teamNum);
        if (leaderSeat == -1) {
            leaderArgs.putBoolean("onlyTitle", true);
            leaderArgs.putString("team", jsonParser.getTeamName(teamNum));
            leaderFrag.setArguments(leaderArgs);
            transaction.replace(R.id.leader_container1, leaderFrag).commit();
            for (int i = 0; i < mateNum + 1; i++) {
                createTeamFragment(teamNum, i, jsonParser.getSeatPos(teamNum, i), false);
            }
            return;
        }
        leaderArgs.putBoolean("isEmpty", isEmpty);
        leaderArgs.putBoolean("onlyTitle", false);
        leaderArgs.putInt("id", jsonParser.getMemberIdentifier(teamNum, leaderSeat));
        leaderArgs.putString("pic", jsonParser.getStaffPicPath(teamNum, leaderSeat));
        leaderArgs.putString("team", jsonParser.getTeamName(teamNum));
        leaderArgs.putString("name", jsonParser.getName(teamNum, leaderSeat));
        leaderArgs.putString("duty", jsonParser.getDuty(teamNum, leaderSeat));
        //SharedPreferences pref = getContext().getSharedPreferences("kiosk_data", Context.MODE_PRIVATE);
        leaderArgs.putInt("isWorking", jsonParser.getIsWorking(teamNum, leaderSeat));
        leaderArgs.putString("mission", jsonParser.getMission(teamNum, leaderSeat));

        leaderFrag.setArguments(leaderArgs);
        mateSeat.put(0, leaderSeat);

        transaction.replace(R.id.leader_container1, leaderFrag).commit();
        for (int i = 0; i < mateNum + 1; i++) {
            if (jsonParser.getSeatPos(teamNum, i) != jsonParser.getSeatPos(teamNum, leaderSeat)) {
                boolean success = createTeamFragment(teamNum, i, jsonParser.getSeatPos(teamNum, i), false);
                if (!isEmpty && success)
                    parent.presentMates--;
            }
        }
    }

    public void createLeaderFragment(int mate, int position, boolean onTop) {
        if (!onTop) position++;

        if (position < 1 || position > division)
            return;

        Fragment leaderFrag = new LeaderFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();

        Bundle leaderArgs = new Bundle();

        leaderArgs.putInt("id", jsonParser.getMemberIdentifier(teamNum, mate));
        leaderArgs.putString("pic", jsonParser.getStaffPicPath(teamNum, mate));
        leaderArgs.putString("team", jsonParser.getTeamName(teamNum));
        leaderArgs.putString("name", jsonParser.getName(teamNum, mate));
        leaderArgs.putString("duty", jsonParser.getDuty(teamNum, mate));
        leaderArgs.putInt("isWorking", jsonParser.getIsWorking(teamNum, mate));
        leaderArgs.putString("mission", jsonParser.getMission(teamNum, mate));
        leaderArgs.putBoolean("leaders", leaders);

        leaderFrag.setArguments(leaderArgs);
        mateSeat.put(position, mate);

        if (leadersOnTop && position == division) {
            leadersOnTop = false;
            return;
        }
        try {
            transaction.replace(R.id.class.getField("team1_container" + position).getInt(null), leaderFrag).commit();
            parent.presentMates++;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public boolean createTeamFragment(int team, int mate, int position, boolean isWorking) {
        if ((position < 1 || position > division) && !isWorking) {
            failCount++;
            return false;
        }

        Fragment teamFrag;
        if (division % 5 == 0) {
            teamFrag = new FivTeamFragment();
        } else {
            teamFrag = new TeamFragment();
        }
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        Log.d("isWorking", String.valueOf(isWorking));
        try {
            if (parent.teamMateHash.get(mate).getInt("id") == jsonParser.getMemberIdentifier(team, mate) && !isWorking) {
                Log.d("OrganizationFragment", "teamArgsName fail");
                failCount++;
                return false;
            }
        } catch (NullPointerException e) {
        }
        Bundle teamArgs = new Bundle();
        SharedPreferences pref = getContext().getSharedPreferences("kiosk_data", Context.MODE_PRIVATE);
        teamArgs.putBoolean("leaders", leaders);
        teamArgs.putInt("id", jsonParser.getMemberIdentifier(team, mate));
        teamArgs.putString("pic", jsonParser.getStaffPicPath(team, mate));
        teamArgs.putString("name", jsonParser.getName(team, mate));
        teamArgs.putString("duty", jsonParser.getDuty(team, mate));
        teamArgs.putInt("isWorking", jsonParser.getIsWorking(team, mate));
        teamArgs.putString("mission", jsonParser.getMission(team, mate));
        Log.d("OrganizationFragment", "teamArgsName" + jsonParser.getName(team, mate));
        FrameLayout frameLayout = new FrameLayout(getContext());
        teamFrag.setArguments(teamArgs);
        if (isWorking) {
            try {
                transaction.replace(R.id.class.getField("team1_container" + position).getInt(null), teamFrag).commit();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
            return false;
        }
        mateSeat.put(position, mate);
        parent.teamMateHash.put(mate, teamArgs);

        try {
            transaction.replace(R.id.class.getField("team1_container" + position).getInt(null), teamFrag).commit();
            Log.d("Container_Created", "Created container : " + "team1_container" + position);
            parent.presentMates++;
            parent.sucCount++;
            return true;
        } catch (IllegalAccessException e) {
            Log.d("Container_Failed", "Created Failed : " + "team1_container" + position + " : " + e);
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            Log.d("Container_Failed", "Created Failed : " + "team1_container" + position + " : " + e);
            e.printStackTrace();
        }
        failCount++;
        return false;
    }

    public boolean createTeamFragment(int team, int mate, int position, int rawPosition) {
        //if (rawPosition > division * (parent.pageCounter + 1)) {
//            position = 99;
//        }
        return createTeamFragment(team, mate, position, false);
    }

    public void containerListeners(final View rootView, boolean chkFirst) {
        FrameLayout leaderFrame = (FrameLayout) rootView.findViewById(R.id.leader_container1);
        FrameLayout teamFrame1 = (FrameLayout) rootView.findViewById(R.id.team1_container1);
        FrameLayout teamFrame2 = (FrameLayout) rootView.findViewById(R.id.team1_container2);
        FrameLayout teamFrame3 = (FrameLayout) rootView.findViewById(R.id.team1_container3);
        FrameLayout teamFrame4 = (FrameLayout) rootView.findViewById(R.id.team1_container4);
        FrameLayout teamFrame5 = (FrameLayout) rootView.findViewById(R.id.team1_container5);
        FrameLayout teamFrame6 = (FrameLayout) rootView.findViewById(R.id.team1_container6);
        FrameLayout teamFrame7 = (FrameLayout) rootView.findViewById(R.id.team1_container7);
        FrameLayout teamFrame8 = (FrameLayout) rootView.findViewById(R.id.team1_container8);
        FrameLayout teamFrame9 = (FrameLayout) rootView.findViewById(R.id.team1_container9);
        FrameLayout teamFrame10 = (FrameLayout) rootView.findViewById(R.id.team1_container10);
        if (chkFirst == true) {
            leaderFrame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setPopup(0);
                }
            });
        }

        ViewGroup.LayoutParams fivTeamParams1 = teamFrame1.getLayoutParams();
        ViewGroup.LayoutParams fivTeamParams2 = teamFrame2.getLayoutParams();
        ViewGroup.LayoutParams fivTeamParams3 = teamFrame3.getLayoutParams();
        ViewGroup.LayoutParams fivTeamParams4 = teamFrame4.getLayoutParams();
        ViewGroup.LayoutParams fivTeamParams5 = teamFrame5.getLayoutParams();
        ViewGroup.LayoutParams fivTeamParams6 = teamFrame6.getLayoutParams();
        ViewGroup.LayoutParams fivTeamParams7 = teamFrame7.getLayoutParams();
        ViewGroup.LayoutParams fivTeamParams8 = teamFrame8.getLayoutParams();
        ViewGroup.LayoutParams fivTeamParams9 = teamFrame9.getLayoutParams();
        ViewGroup.LayoutParams fivTeamParams10 = teamFrame10.getLayoutParams();



        if(division%5 == 0) {
            fivTeamParams1.height = 110;
            fivTeamParams2.height = 110;
            fivTeamParams3.height = 110;
            fivTeamParams4.height = 110;
            fivTeamParams5.height = 110;
            fivTeamParams6.height = 110;
            fivTeamParams7.height = 110;
            fivTeamParams8.height = 110;
            fivTeamParams9.height = 110;
            fivTeamParams10.height = 110;
        } else {
            fivTeamParams1.height = 138;
            fivTeamParams2.height = 138;
            fivTeamParams3.height = 138;
            fivTeamParams4.height = 138;
            fivTeamParams5.height = 138;
            fivTeamParams6.height = 138;
            fivTeamParams7.height = 138;
            fivTeamParams8.height = 138;
            fivTeamParams9.height = 138;
            fivTeamParams10.height = 138;
        }

        teamFrame1.setLayoutParams(fivTeamParams1);
        teamFrame2.setLayoutParams(fivTeamParams2);
        teamFrame3.setLayoutParams(fivTeamParams3);
        teamFrame4.setLayoutParams(fivTeamParams4);
        teamFrame5.setLayoutParams(fivTeamParams5);
        teamFrame6.setLayoutParams(fivTeamParams6);
        teamFrame7.setLayoutParams(fivTeamParams7);
        teamFrame8.setLayoutParams(fivTeamParams8);
        teamFrame9.setLayoutParams(fivTeamParams9);
        teamFrame10.setLayoutParams(fivTeamParams10);

        teamFrame1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPopup(1);
            }
        });
        teamFrame2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPopup(2);
            }
        });
        teamFrame3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPopup(3);
            }
        });
        teamFrame4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPopup(4);
            }
        });
        teamFrame5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPopup(5);
            }
        });
        teamFrame6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPopup(6);
            }
        });
        teamFrame7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPopup(7);
            }
        });
        teamFrame8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPopup(8);
            }
        });
        teamFrame9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPopup(9);
            }
        });
        teamFrame10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPopup(10);
            }
        });
    }


    public void setPopup(final int position) {
        if (parent.popupCut)
            return;
        if (mateSeat.get(position) == null) return;

        final FrameLayout dark = (FrameLayout) parent.findViewById(R.id.dark);
        final RelativeLayout popup = (RelativeLayout) parent.findViewById(R.id.popup);

        dark.setVisibility(View.VISIBLE);
        popup.setVisibility(View.INVISIBLE);

        final ImageView popupPic = (ImageView) parent.findViewById(R.id.popup_pic);
        final TextView popupName = (TextView) parent.findViewById(R.id.popup_name);
        final TextView popupDuty = (TextView) parent.findViewById(R.id.popup_duty);
        final TextView popupPhone = (TextView) parent.findViewById(R.id.popup_phone);
        final TextView popupMail = (TextView) parent.findViewById(R.id.popup_mail);
        final TextView popupMission = (TextView) parent.findViewById(R.id.popup_mission);
        final Button popupClose = (Button) parent.findViewById(R.id.popup_close);

        if (parent.typefacenanumsqareeb == null) {
            parent.typefacenanumsqareeb = Typeface.createFromAsset(parent.getAssets(), "nanumsquareeb.ttf");
        }
        popupName.setTypeface(parent.typefacenanumsqareeb);
        popupDuty.setTypeface(parent.typefacenanumsqareeb);
        popupPhone.setTypeface(parent.typefacenanumsqareeb);
        popupMail.setTypeface(parent.typefacenanumsqareeb);
        popupMission.setTypeface(parent.typefacenanumsqareeb);

        final Techniques in, out;
        in = Techniques.BounceInDown;
        out = Techniques.SlideOutUp;

        if (popupTimer != null)
            popupTimer.cancel();
        popupTimer = new Timer();

        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                dark.setOnClickListener(null);
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
        };

        popupTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, AUTO_POPUP_CLOSE_MS, AUTO_POPUP_CLOSE_MS);

        dark.setOnClickListener(new View.OnClickListener() {
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
                    }
                }).playOn(popup);
            }
        });
        popupClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

        File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/KIOSKData/staff/" + jsonParser.getStaffPicPath(teamNum, mateSeat.get(position)));
        String imgSrc = jsonParser.getStaffPicPath(teamNum, mateSeat.get(position));
        if (!imgSrc.equals("") && f.exists())
            Glide.with(getContext()).load(f).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).fitCenter().into(new ViewTarget<ImageView, GlideDrawable>(popupPic) {
                @Override
                public void onResourceReady(GlideDrawable resource, GlideAnimation anim) {
                    ImageView myView = this.view;
                    myView.setImageDrawable(resource);

                    popup.setVisibility(View.VISIBLE);
                    YoYo.with(in).duration(700).playOn(popup);
                }
            });
        else
            Glide.with(getContext()).load(R.drawable.no_img_b).skipMemoryCache(true).fitCenter().into(new ViewTarget<ImageView, GlideDrawable>(popupPic) {
                @Override
                public void onResourceReady(GlideDrawable resource, GlideAnimation anim) {
                    ImageView myView = this.view;
                    myView.setImageDrawable(resource);

                    popup.setVisibility(View.VISIBLE);
                    YoYo.with(in).duration(700).playOn(popup);
                }
            });


        popupName.setText(jsonParser.getName(teamNum, mateSeat.get(position)));
        popupDuty.setText(jsonParser.getDuty(teamNum, mateSeat.get(position)));
        popupPhone.setText(jsonParser.getTel(teamNum, mateSeat.get(position)));
        popupMail.setText(jsonParser.getMail(teamNum, mateSeat.get(position)));
        popupMission.setText(jsonParser.getMission(teamNum, mateSeat.get(position)));
        parent.findViewById(R.id.popup_call).setVisibility(View.VISIBLE);
        if (parent.wholeMode)
            parent.findViewById(R.id.popup_call).setVisibility(View.GONE);

        try {
            parent.findViewById(R.id.popup_call).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    parent.setSeatPopup(teamNum, mateSeat.get(position));

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
        } catch (NullPointerException e) {
        }

        final TextView popupWorking = (TextView) parent.findViewById(R.id.working);
        final TextView popupVacation = (TextView) parent.findViewById(R.id.vacation);
        final TextView popupTrip = (TextView) parent.findViewById(R.id.trip);
        final TextView popupEducation = (TextView) parent.findViewById(R.id.education);

        popupWorking.setBackgroundResource(R.drawable.new_status_1_off);
        popupVacation.setBackgroundResource(R.drawable.new_status_2_off);
        popupTrip.setBackgroundResource(R.drawable.new_status_3_off);
        popupEducation.setBackgroundResource(R.drawable.new_status_4_off);

        switch (jsonParser.getIsWorking(teamNum, mateSeat.get(position))) {
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
        final RelativeLayout passwordLayout = (RelativeLayout) parent.findViewById(R.id.passwordLayout);
        passwordLayout.setVisibility(View.GONE);

        final TextView passwordText = (TextView) parent.findViewById(R.id.password_field);
        final TextView p1 = (TextView) parent.findViewById(R.id.p1);
        final TextView p2 = (TextView) parent.findViewById(R.id.p2);
        final TextView p3 = (TextView) parent.findViewById(R.id.p3);
        final TextView p4 = (TextView) parent.findViewById(R.id.p4);
        final TextView p5 = (TextView) parent.findViewById(R.id.p5);
        final TextView p6 = (TextView) parent.findViewById(R.id.p6);
        final TextView p7 = (TextView) parent.findViewById(R.id.p7);
        final TextView p8 = (TextView) parent.findViewById(R.id.p8);
        final TextView p9 = (TextView) parent.findViewById(R.id.p9);
        final TextView p0 = (TextView) parent.findViewById(R.id.p0);
        final TextView undo = (TextView) parent.findViewById(R.id.undo);
        final TextView ok = (TextView) parent.findViewById(R.id.ok);
        final TextView cancel = (TextView) parent.findViewById(R.id.cancel);

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
                switch (jsonParser.getIsWorking(teamNum, mateSeat.get(position))) {
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
            }
        });


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
                    passwordInput += "1";
                else if (v == p2)
                    passwordInput += "2";
                else if (v == p3)
                    passwordInput += "3";
                else if (v == p4)
                    passwordInput += "4";
                else if (v == p5)
                    passwordInput += "5";
                else if (v == p6)
                    passwordInput += "6";
                else if (v == p7)
                    passwordInput += "7";
                else if (v == p8)
                    passwordInput += "8";
                else if (v == p9)
                    passwordInput += "9";
                else if (v == p0)
                    passwordInput += "0";
                else if (v == undo)
                    if (!passwordInput.equals(""))
                        passwordInput = passwordInput.substring(0, passwordInput.length() - 1);

                String stars = "";
                for (int i = 0; i < passwordInput.length(); i++) {
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

        tmp = 0;

        View.OnClickListener isWorkingButtonListener = new View.OnClickListener() {
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

                popupWorking.setBackgroundResource(R.drawable.new_status_1_off);
                popupVacation.setBackgroundResource(R.drawable.new_status_2_off);
                popupTrip.setBackgroundResource(R.drawable.new_status_3_off);
                popupEducation.setBackgroundResource(R.drawable.new_status_4_off);

                passwordLayout.setVisibility(View.VISIBLE);
                passwordText.setText("");

                if (v == popupWorking) {
                    popupWorking.setBackgroundResource(R.drawable.new_status_1_on);
                    tmp = 1;
                    if (jsonParser.getIsWorking(teamNum, mateSeat.get(position)) == 1)
                        passwordLayout.setVisibility(View.GONE);

                } else if (v == popupVacation) {
                    popupVacation.setBackgroundResource(R.drawable.new_status_2_on);
                    tmp = 2;
                    if (jsonParser.getIsWorking(teamNum, mateSeat.get(position)) == 2)
                        passwordLayout.setVisibility(View.GONE);

                } else if (v == popupTrip) {
                    popupTrip.setBackgroundResource(R.drawable.new_status_3_on);
                    tmp = 3;
                    if (jsonParser.getIsWorking(teamNum, mateSeat.get(position)) == 3)
                        passwordLayout.setVisibility(View.GONE);

                } else if (v == popupEducation) {
                    popupEducation.setBackgroundResource(R.drawable.new_status_4_on);
                    tmp = 4;
                    if (jsonParser.getIsWorking(teamNum, mateSeat.get(position)) == 4)
                        passwordLayout.setVisibility(View.GONE);

                } else if (v == ok) {
                    if (passwordInput.equals(password) || (passwordInput.equals("") && password.equals("null"))) {
                        int tmpWorking = jsonParser.getIsWorking(teamNum, mateSeat.get(position));
                        jsonParser.setIsWorking(teamNum, mateSeat.get(position), tmp);//marker

                        MainActivity.context.sendBroadcast(new Intent("memberStatus")
                                .putExtra("member", jsonParser.getMemberIdentifier(teamNum, mateSeat.get(position)))
                                .putExtra("status", tmp));
                        dark.setVisibility(View.GONE);
                        popup.setVisibility(View.GONE);
                        popupPhone.setVisibility(View.VISIBLE);
                        popupMail.setVisibility(View.VISIBLE);
                        popupMission.setVisibility(View.VISIBLE);

                        if (position == 0)
                            createLeaderFragment(false);
                        else
                            createTeamFragment(teamNum, mateSeat.get(position), position, true);
                    }
                }

                if (jsonParser.getIsWorking(teamNum, mateSeat.get(position)) == tmp) {
                    passwordLayout.setVisibility(GONE);
                    popupPhone.setVisibility(View.VISIBLE);
                    popupMail.setVisibility(View.VISIBLE);
                    popupMission.setVisibility(View.VISIBLE);
                }
                passwordInput = "";
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
                fis = new FileInputStream(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/KIOSKData", "KIOSK_MEMBER_LOG.json"));
                buffer = new BufferedReader(new InputStreamReader(fis, "utf-8"));
                str = buffer.readLine();
                while (str != null) {
                    data.append(str + "\n");
                    str = buffer.readLine();
                }
                wholeArray = new JSONArray(data.toString());
            } catch (Exception e) {
            }

            JSONObject memberObject = new JSONObject();
            memberObject.put("time", Calendar.getInstance().getTime());
            memberObject.put("id", jsonParser.getMemberIdentifier(teamNum, mateSeat.get(position)));
            memberObject.put("dept", jsonParser.getDeptName(teamNum, mateSeat.get(position)));
            memberObject.put("team", jsonParser.getTeamName(teamNum).replace(" ", ""));
            memberObject.put("name", jsonParser.getName(teamNum, mateSeat.get(position)));
            memberObject.put("duty", jsonParser.getDuty(teamNum, mateSeat.get(position)));

            wholeArray.put(memberObject);

            dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/KIOSKData");
            file = new File(dir, "KIOSK_MEMBER_LOG.json");
            fw = new FileWriter(file);
            fw.write(wholeArray.toString());
            fw.close();
        } catch (Exception e) {
        }
    }

    public int getTeamLeaderSeat(int teamNum) {
        for (int i = 0; i <= jsonParser.getMateCount(teamNum); i++) {
            if (jsonParser.getSeatPos(teamNum, i) == 0)
                return i;
        }
        return -1;
    }

    public HashMap getPagePositionHash() {
        return parent.pagePositionHash;
    }
}