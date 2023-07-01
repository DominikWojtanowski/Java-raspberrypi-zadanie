package com.dominik;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class LifeBit {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private int status;
    private String message;
    private long uptime;

    public LifeBit(long id, long uptime, int status, String message) {
        this.id = id;
        this.uptime = uptime;
        this.status = status;
        this.message = message;
    }

    public LifeBit(int status, long uptime, String message) {
        this.uptime = uptime;
        this.status = status;
        this.message = message;
    }

    public LifeBit() {
    }

    public long getUptime() {
        return uptime;
    }

    public void setUptime(long uptime) {
        this.uptime = uptime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
