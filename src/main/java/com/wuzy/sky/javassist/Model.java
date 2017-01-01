package com.wuzy.sky.javassist;

import java.util.Set;

/**
 * Created by apple on 2016/10/15.
 */
public class Model {
    private String type;
    private Set<String> values;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Set<String> getValues() {
        return values;
    }

    public void setValues(Set<String> values) {
        this.values = values;
    }
}
