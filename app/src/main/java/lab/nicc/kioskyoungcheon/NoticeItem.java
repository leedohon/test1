package lab.nicc.kioskyoungcheon;

/**
 * Created by NG1 on 2017-08-16.
 */

public class NoticeItem{
    String title, article, file;

    public NoticeItem(String title, String article, String file){
        this.title = title;
        this.article = article;
        this.file = file;
    }

    public NoticeItem(String title, String file){
        this.title = title;
        this.file = file;
        this.article = "movieMode-code01";
    }

    public String getTitle(){
        return title;
    }

    public String getArticle(){
        return article;
    }

    public String getFile(){
        return file;
    }
}

