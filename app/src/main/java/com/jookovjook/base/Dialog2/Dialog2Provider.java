package com.jookovjook.base.Dialog2;

public class Dialog2Provider {
    private String text;
    private int type;
    private int _id;

    public Dialog2Provider() {}

    public Dialog2Provider(String text, int type, int _id) {
        this.text = text;
        this.type = type;
        this._id = _id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int type) {
        this._id = _id;
    }
}
