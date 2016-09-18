package xyz.zsckare.doramasdownloader.Models;

/**
 * Created by katty on 18/09/16.
 */
public class PageModel {

    String name, url;

    public PageModel(String name, String url) {
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

    @Override
    public String toString() {
        return "PageModel{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
