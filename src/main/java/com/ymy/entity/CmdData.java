package com.ymy.entity;

import com.alibaba.fastjson.JSONObject;

public class CmdData {
    String cmd;

    JSONObject data;

    String error;

    public CmdData(String cmd, JSONObject data) {
        this.cmd = cmd;
        this.data = data;
    }

    public CmdData(String error) {
        this.error = error;
    }

    public String getCmd() {
        return cmd;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public JSONObject getData() {
        return data;
    }

    public void setData(JSONObject data) {
        this.data = data;
    }
}
