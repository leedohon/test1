package lab.nicc.kioskyoungcheon;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by NG1 on 2017-08-16.
 */

public class NoticeItemView extends LinearLayout {
    TextView noticeTitle;
    TextView noticeArticle;
    LinearLayout linearLayout;
    public NoticeItemView(Context context){
        super(context);
        init(context);
    }

    public void init(Context context){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        linearLayout = (LinearLayout) inflater.inflate(R.layout.notice_item, this, true);
        noticeTitle = (TextView) findViewById(R.id.notice_title);
        noticeArticle = (TextView) findViewById(R.id.notice_article);
    }

    public void setNoticeTitle(String title){
        noticeTitle.setText(title);
    }

    public void setNoticeArticle(String article){
        if(article.equals("movieMode-code01")){
            ImageView iv = (ImageView) linearLayout.findViewById(R.id.notice_icon);
            iv.setImageResource(R.drawable.ico_video);
            Log.d("IcoChanged", "suc");
        } else {
            article = article.replace(System.getProperty("line.separator"), " ");
            Log.d("article", article);
            noticeArticle.setText(article);
        }
    }
}
