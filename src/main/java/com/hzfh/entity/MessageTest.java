package com.hzfh.entity;

import java.io.Serializable;

/**
 * com.hzfh.entity
 *
 * @author rencc
 * @Note
 * @Date 2017-08-22 13:35
 */
public class MessageTest implements Serializable {
    private int id;
    private String message;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", message='" + message + '\'' +
                '}';
    }
}
