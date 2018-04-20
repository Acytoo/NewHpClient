package com.acytoo.newhpcliend;

public class Plans {
    private int _id;
    private String _date;
    private String _todos;

    public Plans(String _date, String _todos) {
        this._date = _date;
        this._todos = _todos;
    }

    public Plans() {
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String get_date() {
        return _date;
    }

    public void set_date(String _date) {
        this._date = _date;
    }

    public String get_todos() {
        return _todos;
    }

    public void set_todos(String _todos) {
        this._todos = _todos;
    }
}
