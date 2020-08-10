package lab.nicc.kioskyoungcheon;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;

/**
 * Created by NG1 on 2017-07-29.
 */

public class SeatFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.seat_item, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ImageView topImg = (ImageView) view.findViewById(R.id.topImg);
        LinearLayout top = (LinearLayout) view.findViewById(R.id.top);
        ImageView pic = (ImageView) view.findViewById(R.id.team_pic);
        TextView teamName = (TextView) view.findViewById(R.id.team_name);
        TextView teamDuty = (TextView) view.findViewById(R.id.team_duty);
        TextView seatText = (TextView) view.findViewById(R.id.seat_text);
        seatText.setVisibility(View.GONE);
        try {
            File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/KIOSKData/staff/" + getArguments().getString("pic"));
            String imgSrc = getArguments().getString("pic");
            if (!imgSrc.equals("") && f.exists())
                Glide.with(getContext()).load(f).diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true).fitCenter().into(pic);

            teamName.setText(getArguments().getString("name"));
            teamDuty.setText(getArguments().getString("duty"));
        }
        catch (IllegalArgumentException e){
        }
        catch(NullPointerException e){
            try{
                String teamNameArgs = getArguments().getString("team");
                if(34 < teamNameArgs.length()){
                    teamNameArgs = teamNameArgs.substring(0, 16) + "\n" + teamNameArgs.substring(18, 34) + "\n" + teamNameArgs.substring(37, teamNameArgs.length());
                    seatText.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 126));
                    seatText.setBackgroundResource(R.drawable.team_3);
                } else if(17 < teamNameArgs.length()){
                    teamNameArgs = teamNameArgs.substring(0, 16) + "\n" + teamNameArgs.substring(18, teamNameArgs.length());
                    seatText.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 106));
                    seatText.setBackgroundResource(R.drawable.team_2);
                }

                seatText.setText(teamNameArgs);

                topImg.setVisibility(View.GONE);
                top.setVisibility(View.GONE);
                seatText.setVisibility(View.VISIBLE);
            } catch (NullPointerException es){
                topImg.setVisibility(View.GONE);
                top.setVisibility(View.GONE);
                seatText.setVisibility(View.VISIBLE);
            }
        }

    }
}