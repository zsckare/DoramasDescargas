package xyz.zsckare.doramasdownloader.Models;

/**
 * Created by katty on 16/09/16.
 */
public class ThumbChapterModel {
    String name, url;

    public ThumbChapterModel(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
