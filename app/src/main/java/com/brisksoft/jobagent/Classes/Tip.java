package com.brisksoft.jobagent.Classes;

/**
 * Created by brenden on 7/22/17.
 */

public class Tip {
    private String title;
    private String description;
    private String link;


    public Tip(){
        super();
    }

    public Tip(String title, String description, String link) {
        super();
        this.title = title;
        this.description = description;
        this.link = link;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }
    public void setLink(String link) {
        this.link = link;
    }

    @Override
    public String toString() {
        return this.title + ", " + this.description + ", " + this.link;
    }

}