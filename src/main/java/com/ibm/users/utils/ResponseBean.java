package com.ibm.users.utils;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.Serializable;
import java.util.LinkedHashMap;

public class ResponseBean implements Serializable {

    private static final long serialVersionUID = -2818219614368513135L;

    public int getStatus() {
    	return status;
	}
	
	public void setStatus(int status) {
		this.status = status;
	}
	
	public String getMsg() {
		return msg;
	}
	
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	public LinkedHashMap<String, JsonNode> getData() {
		return data;
	}
	
	public void setData(LinkedHashMap<String, JsonNode> data) {
		this.data = data;
	}
	
	private int status;
    private String msg;
    private LinkedHashMap<String, JsonNode> data = new LinkedHashMap<String, JsonNode>();

    public ResponseBean(int status, String msg) {
	    this.status = status;
	    this.msg = msg;
    }

    public static final ResponseBean status(int status) {
    	return new ResponseBean(status, "");
    }

    public ResponseBean data(String name, Object value) throws Exception {
	    this.data.put(name, JSONUtil.toJsonNode(value));
	    return this;
    }

    public ResponseBean data(Object value) throws Exception {
	    this.data.put("result", JSONUtil.toJsonNode(value));
	    return this;
    }

    public ResponseBean error(Object value) throws Exception {
	    this.data.put("error", JSONUtil.toJsonNode(value));
	    return this;
    }

}
