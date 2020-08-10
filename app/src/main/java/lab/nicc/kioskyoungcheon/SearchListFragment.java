package lab.nicc.kioskyoungcheon;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by user on 2018-05-30.
 */

public class SearchListFragment extends Fragment {

    Context context;
    static ViewGroup staffList;

    static class BackgroundWork extends AsyncTask<Void, View, Void> {
        Context context;
        Intent intent;

        public BackgroundWork(Context context, Intent intent) {
            this.context = context;
            this.intent = intent;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            MainActivity.searchCount.setText("0");
            staffList.removeAllViews(); //뷰그룹의 모든 뷰 제거.
        }

        @Override
        protected void onProgressUpdate(View... items) { // publishProgress메소드 호출할 경우 UI작업을 실질적으로 하는 곳.
            MainActivity.searchCountText.setVisibility(View.VISIBLE);// 서치 카운트( 키보드 검색 시 몇개나 검색 되었는지 표출해주는 텍스트 뷰 ) 갑자기 이게 나온다고?
            if (items == null) {
                MainActivity.searchCountText.setVisibility(View.INVISIBLE); // 아이템이 없으면 보이지 않게.
                staffList.removeAllViews(); // 모든 뷰 제거.
                return;
            }

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(1750, 60);
            items[0].setLayoutParams(params);
            if (staffList.getChildCount() % 2 != 0)
                items[0].setBackgroundColor(Color.parseColor("#e1e6e9"));
            staffList.addView(items[0]);

            MainActivity.searchCount.setText(Integer.toString(Integer.parseInt(MainActivity.searchCount.getText().toString()) + 1)); // ?? 서치 카운트가 여기서 왜 나와?
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            KoreanKeyboard.enterText.setVisibility(View.VISIBLE);
            KoreanKeyboard.searchPb.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(Void... strings) {
            if (intent.getStringExtra("strings") == null || intent.getStringExtra("strings").length() < 2) {
                publishProgress(null);
                return null;
            }

            try {
                LayoutInflater vi = LayoutInflater.from(context);
                publishProgress(null);
                try {
                    Handler mHandler = new Handler(Looper.getMainLooper());
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            MainActivity.searchLoadingText.setVisibility(View.VISIBLE);
                            MainActivity.searchLoadingTextS.setVisibility(View.VISIBLE);
                            MainActivity.searchLoadingTextS2.setVisibility(View.GONE);
                            MainActivity.searchLoadingPb.setVisibility(View.VISIBLE);
                        }
                    });

                }catch(Exception e){
                }
                ArrayList<Integer> staffIds = new ArrayList<>();
                final JsonParser jsonParser = MainActivity.jsonParser;
                for (int k = 1; k < jsonParser.wholeGetTeams[0].length; k++) {
                    jsonParser.parseStaffJson(jsonParser.wholeGetTeams[0][k].getString("section_fullcode"));
                    for (int i = 0; i < jsonParser.getTeamCount() + 1; i++) {
                        for (int j = 0; j < jsonParser.getMateCount(i) + 1; j++) {

                            if (MainActivity.searchText.getText().length() < 2) {
                                publishProgress(null);
                                return null;
                            }

                            View item = vi.inflate(R.layout.search_item, null);
                            String searchText = MainActivity.searchToggle ? jsonParser.getName(i, j) : jsonParser.getMission(i, j); //이름 및 업무 검색 토글
                            if (KoreanTextMatcher.isMatch(searchText, intent.getStringExtra("strings"))) {
                                boolean duplicated = false;
                                for (int id : staffIds) {
                                    if (id == jsonParser.getMemberIdentifier(i, j)) {
                                        duplicated = true;
                                        break;
                                    }
                                }
                                if (duplicated)
                                    continue;
                                staffIds.add(jsonParser.getMemberIdentifier(i, j));
                                TextView searchName = (TextView) item.findViewById(R.id.search_name);
                                TextView searchDept = (TextView) item.findViewById(R.id.search_dept);
                                TextView searchTeam = (TextView) item.findViewById(R.id.search_team);
                                TextView searchWork = (TextView) item.findViewById(R.id.search_work);
                                TextView searchMission = (TextView) item.findViewById(R.id.search_mission);
                                Button buildingButton = (Button) item.findViewById(R.id.building_button);
                                Button orgButton = (Button) item.findViewById(R.id.org_button);

                                searchName.setText(jsonParser.getName(i, j));
                                searchDept.setText(jsonParser.getDeptName(i, j));
                                searchTeam.setText(jsonParser.getTeamName(i).replace(" ", ""));
                                searchWork.setText(jsonParser.getDuty(i, j));
                                searchMission.setText(jsonParser.getMission(i, j));

                                final String section = jsonParser.getMemberSection(0, k);

                                final int finalI = i;
                                buildingButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        context.sendBroadcast(new Intent("buildingClicked").putExtra("teamCode", section).putExtra("teamNum", finalI));
                                    }
                                });
                                orgButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        context.sendBroadcast(new Intent("orgClicked").putExtra("teamCode", section));
                                    }
                                });

                                publishProgress(item);
                            }
                        }
                    }
                }
                Handler mHandler = new Handler(Looper.getMainLooper());
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        MainActivity.searchLoadingTextS.setVisibility(View.GONE);
                        MainActivity.searchLoadingTextS2.setVisibility(View.VISIBLE);
                        MainActivity.searchLoadingPb.setVisibility(View.GONE);}
                });
            } catch (Exception e) {
            }
            return null;
        }
    }

    static BackgroundWork bgWork;

    BroadcastReceiver stringReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            if (bgWork != null)
                bgWork.cancel(true);
            bgWork = (BackgroundWork) new BackgroundWork(context, intent).execute();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.search_fragment, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        context = MainActivity.context;
        staffList = (ViewGroup) view.findViewById(R.id.staff_list);
        context.registerReceiver(stringReceiver, new IntentFilter("searchInput"));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (bgWork != null) {
            bgWork.cancel(true);
            bgWork = null;
        }
    }
}