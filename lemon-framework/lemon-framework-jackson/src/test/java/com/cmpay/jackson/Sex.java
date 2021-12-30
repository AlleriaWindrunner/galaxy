package com.cmpay.jackson;

import com.galaxy.lemon.framework.valuable.Valuable;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public enum Sex implements Valuable<String> {
    MALE("男"), FEMALE("女");

    private String value;

    Sex(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return this.value;
    }
}
