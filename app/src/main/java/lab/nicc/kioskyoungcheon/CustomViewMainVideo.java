package lab.nicc.kioskyoungcheon;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.exoplayer2.ui.PlayerView;

import java.util.List;

import lab.nicc.kioskyoungcheon.ExoPlayerPackage.MyExoPlayer;

/**
 * Created by user on 2020-02-07.
 */

public class CustomViewMainVideo extends RelativeLayout {

    PlayerView playerView;
    RelativeLayout playerViewLayout;
    ImageView imageView;
    RelativeLayout imageViewLayout;
    MyExoPlayer myExoPlayer;
    boolean localVideoMode;
    String serverAddress;
    TextView galleryText2;

    public CustomViewMainVideo(Context context) {
        super(context);
        initViews(context);
    }

    public CustomViewMainVideo(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(context);
    }

    public CustomViewMainVideo(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    private void initViews(Context context){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.custom_view_main_video, this);
        playerView = (PlayerView) findViewById(R.id.mainExoPlayerView);
        playerViewLayout = (RelativeLayout) findViewById(R.id.main_exo_player_view_layout);
        imageView = (ImageView) findViewById(R.id.main_image_view);
        imageViewLayout = (RelativeLayout) findViewById(R.id.main_image_view_layout);
        galleryText2 = (TextView) findViewById(R.id.gallery_text2);
    }

    public void setLocalVideoMode(boolean localVideoMode, String serverAddress){
        this.localVideoMode = localVideoMode;
        this.serverAddress = serverAddress;
    }

    public void playVideo(List<String> videoNames){
        String[] videoNameStrings = videoNames.toArray(new String[videoNames.size()]);

        myExoPlayer = MyExoPlayer.getInstance();
        myExoPlayer.bindExoPlayer(this, playerView, playerViewLayout, imageView, imageViewLayout, getContext());

        myExoPlayer.initializePlayer(videoNameStrings, 0, localVideoMode, serverAddress);
    }

    public void releasePlayer(){
        myExoPlayer.releaseView();
    }

    public void setTypeface(Typeface typeface) {
        galleryText2.setTypeface(typeface);
    }
}
