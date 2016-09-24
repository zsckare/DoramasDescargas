package xyz.zsckare.doramasdownloader.Models;


public class DoramaThumbModel {
    String name, img, url;

    public DoramaThumbModel(String name, String img, String url) {
        this.name = name;
        this.img = img;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "DoramaThumbModel{" +
                "name='" + name + '\'' +
                ", img='" + img + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
