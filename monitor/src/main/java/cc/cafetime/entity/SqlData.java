package cc.cafetime.entity;

import java.util.Date;

/**
 * Created by liujing on 2017/3/6.
 */
public class SqlData {

    private long cost;
    private String sql;
    private Date date;

    public long getCost() {
        return cost;
    }

    public void setCost(long cost) {
        this.cost = cost;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
