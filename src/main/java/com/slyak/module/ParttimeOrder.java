package com.slyak.module;

import java.io.Serializable;

/**
 * .
 * <p/>
 *
 * @author <a href="mailto:stormning@163.com">stormning</a>
 * @version V1.0, 2015/1/6
 */
public class ParttimeOrder implements Serializable {
    private long userId;
    private int status;
    private long parttimeId;

    public long getParttimeId() {
        return parttimeId;
    }

    public void setParttimeId(long parttimeId) {
        this.parttimeId = parttimeId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
