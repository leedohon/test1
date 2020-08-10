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

public class TeamFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(getArguments().getBoolean("leaders"))
            return inflater.inflate(R.layout.leader_fragment_no_title, container, false);
        return inflater.inflate(R.layout.team_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ImageView pic = (ImageView) view.findViewById(R.id.team_pic);
        TextView teamName = (TextView) view.findViewById(R.id.team_name);
        TextView teamDuty = (TextView) view.findViewById(R.id.team_duty);
        TextView teamMission = (TextView) view.findViewById(R.id.team_mission);
        ImageView teamIsWorking = (ImageView) view.findViewById(R.id.work_icon);
        if(getArguments().getBoolean("leaders")) {
            pic = (ImageView) view.findViewById(R.id.leader_pic);
            teamName = (TextView) view.findViewById(R.id.leader_name);
            teamDuty = (TextView) view.findViewById(R.id.leader_duty);
            teamMission = (TextView) view.findViewById(R.id.leader_mission);
        }

        File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/KIOSKData/staff/" + getArguments().getString("pic"));

        String imgSrc = getArguments().getString("pic");
        if (!imgSrc.equals("") && f.exists())
            Glide.with(getContext()).load(f).diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true).fitCenter().into(pic);

        teamName.setText(getArguments().getString("name"));
        teamDuty.setText(getArguments().getString("duty"));
        if(teamDuty.getText().length()>=7){
            teamDuty.setTextSize(15);
        }
        if(teamName.getText().toString().length()>=25)
            teamName.setText(teamName.getText().toString().replaceAll(" ", ""));
        else if(teamName.getText().toString().length()>=20){
            teamName.setText(teamName.getText().toString().replaceAll("  ", " "));
        }
        teamMission.setText(getArguments().getString("mission"));

        teamIsWorking.setImageResource(R.drawable.new_led_gray);
        switch (getArguments().getInt("isWorking")) {
            case 1:
                teamIsWorking.setImageResource(R.drawable.new_led_green);
                break;
        }

        //원래꺼
/*        teamIsWorking.setImageResource(R.drawable.work_icon);
        switch (getArguments().getInt("isWorking")) {
            case 1:
                teamIsWorking.setImageResource(R.drawable.work_icon_blue);
                break;
        }*/
    }
}