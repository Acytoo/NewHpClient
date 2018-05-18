package com.acytoo.newhpcliend.utils;


/**
 * Since there is no date type in sqlite, and using text store date information
 * makes the efficiency low, i will use Integer to store date.
 * Alec Chen
 * To make db easier, i use int to present boolean for done and autodelete
 */
public class Plans{
    private int _id;            //id, the primary key
    private long _plan_time;      //the date of your plan
    private int _priority;       //priority, how important the plan is
    private long _plan_set_time;  //When did the user set his plan
    private String _source;      //Where does the plan come from, school or teacher, class or himself
    private String _todos;          //basically the content of the plan
    private int _done;          //if the plan is done, finished
    private int _auto_delete;   //if the plan can be deleted automatically
    private long _alarm_time;    //the time when the user may want an alarm to warn them

    public Plans(long _plan_time, int _priority, long _plan_set_time, String _source, String _todos,
                 int _done, int _auto_delete, long _alarm_time) {
        this._plan_time = _plan_time;
        this._priority = _priority;
        this._plan_set_time = _plan_set_time;
        this._source = _source;
        this._todos = _todos;
        this._done = _done;
        this._auto_delete = _auto_delete;
        this._alarm_time = _alarm_time;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public long get_plan_time() {
        return _plan_time;
    }

    public void set_plan_time(long _plan_time) {
        this._plan_time = _plan_time;
    }

    public int get_priority() {
        return _priority;
    }

    public void set_priority(int _priority) {
        this._priority = _priority;
    }

    public long get_plan_set_time() {
        return _plan_set_time;
    }

    public void set_plan_set_time(long _plan_set_time) {
        this._plan_set_time = _plan_set_time;
    }

    public String get_source() {
        return _source;
    }

    public void set_source(String _source) {
        this._source = _source;
    }

    public String get_todos() {
        return _todos;
    }

    public void set_todos(String _todos) {
        this._todos = _todos;
    }

    public int get_done() {
        return _done;
    }

    public void set_done(int _done) {
        this._done = _done;
    }

    public int get_auto_delete() {
        return _auto_delete;
    }

    public void set_auto_delete(int _auto_delete) {
        this._auto_delete = _auto_delete;
    }

    public long get_alarm_time() {
        return _alarm_time;
    }

    public void set_alarm_time(long _alarm_time) {
        this._alarm_time = _alarm_time;
    }
}

