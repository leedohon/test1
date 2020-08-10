package lab.nicc.kioskyoungcheon;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

/**
 * Created by NG1 on 2017-08-16.
 */

public class NoticeAdapter extends BaseAdapter {
    ArrayList<NoticeItem> items = new ArrayList<>();
    Context context;

    public void setContext(Context context){
        this.context = context;
    }

    @Override
    public int getCount(){
        return items.size();
    }

    public void addItem(NoticeItem item){
        items.add(item);
    }

    public Object getItem(int position){
        return items.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup){
        NoticeItemView view = new NoticeItemView(context);
        NoticeItem item = items.get(position);
        view.setNoticeTitle(item.getTitle());
        try {
            view.setNoticeArticle(item.getArticle());
        } catch (Exception e){
            e.printStackTrace();
        }

        return view;
    }
}
