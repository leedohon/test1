package lab.nicc.kioskyoungcheon;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;

/**
 * Created by NG1 on 2017-07-29.
 */

public class LeaderFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (getArguments().getBoolean("isEmpty"))
            return inflater.inflate(R.layout.leader_fragment_empty, container, false);
        else if (getArguments().getBoolean("onlyTitle"))
            return inflater.inflate(R.layout.leader_fragment_only_title, container, false);
        else
            return inflater.inflate(R.layout.leader_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        if (getArguments().getBoolean("isEmpty"))
            return;
        TextView leaderTeam = (TextView) view.findViewById(R.id.leader_team);
        leaderTeam.setText(getArguments().getString("team"));
        if (getArguments().getBoolean("onlyTitle"))
            return;
        ImageView pic = (ImageView) view.findViewById(R.id.leader_pic);

        TextView leaderName = (TextView) view.findViewById(R.id.leader_name);
        TextView leaderDuty = (TextView) view.findViewById(R.id.leader_duty);
        TextView leaderMission = (TextView) view.findViewById(R.id.leader_mission);
        ImageView leaderIsWorking = (ImageView) view.findViewById(R.id.work_icon);

        File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/KIOSKData/staff/" + getArguments().getString("pic"));

        String imgSrc = getArguments().getString("pic");
        if (!imgSrc.equals("") && f.exists())
            Glide.with(getContext()).load(f).diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true).fitCenter().into(pic);

        leaderName.setText(getArguments().getString("name"));
        leaderDuty.setText(getArguments().getString("duty"));
        leaderMission.setText(getArguments().getString("mission"));


        Log.d("teamNameLength", String.valueOf(leaderTeam.getText().toString().length()));
        if(leaderTeam.getText().toString().length()>=25)
            leaderTeam.setText(leaderTeam.getText().toString().replaceAll("  ", ""));
        else if(leaderTeam.getText().toString().length()>=20){
            leaderTeam.setText(leaderTeam.getText().toString().replaceAll("  ", " "));
        }

        leaderIsWorking.setImageResource(R.drawable.new_led_gray);
        Log.d("getArguments().getInt", String.valueOf(getArguments().getInt("isWorking")));
        switch (getArguments().getInt("isWorking")) {
            case 1:
                leaderIsWorking.setImageResource(R.drawable.new_led_green);
                break;
        }

        //원래꺼
/*        leaderIsWorking.setImageResource(R.drawable.work_icon);
        switch (getArguments().getInt("isWorking")) {
            case 1:
                leaderIsWorking.setImageResource(R.drawable.work_icon_blue);
                break;
        }*/
    }
}