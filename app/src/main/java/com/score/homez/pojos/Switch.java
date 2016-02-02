package com.score.homez.pojos;

/**
 * Created by eranga on 2/1/16.
 */
public class Switch {
    String name;
    int status;

    public Switch(String name, int status) {
        this.name = name;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
