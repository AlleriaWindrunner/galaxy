/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.galaxy.lemon.common;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * 金额计算
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class Amount {
    public static final int DEFAULT_SCALE = 2;
    private BigDecimal opr1;

    public Amount(int amt) {
        this.opr1 = new BigDecimal(amt);
    }

    public Amount(long amt) {
        this.opr1 = new BigDecimal(amt);
    }

    public Amount(double amt) {
        this.opr1 = new BigDecimal(Double.toString(amt));
    }

    public Amount(String amt) {
        this.opr1 = new BigDecimal(amt);
    }

    public Amount(BigInteger amt) {
        this.opr1 = new BigDecimal(amt);
    }

    public Amount(BigDecimal amt) {
        this.opr1 = amt;
    }

    BigDecimal getOpr() {
        return this.opr1;
    }

    public String toString() {
        return this.opr1.toString();
    }

    public Amount add(Amount... opr2) {
        BigDecimal result = this.opr1;
        Amount[] arr$ = opr2;
        int len$ = opr2.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            Amount tmp = arr$[i$];
            result = result.add(tmp.opr1);
        }

        return new Amount(result);
    }

    public Amount sub(Amount... opr2) {
        BigDecimal result = this.opr1;
        Amount[] arr$ = opr2;
        int len$ = opr2.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            Amount tmp = arr$[i$];
            result = result.subtract(tmp.opr1);
        }

        return new Amount(result);
    }

    public Amount mulAndRound(Amount... opr2) {
        BigDecimal result = this.opr1;
        Amount[] arr$ = opr2;
        int len$ = opr2.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            Amount tmp = arr$[i$];
            result = result.multiply(tmp.opr1);
        }

        result = result.divide(new BigDecimal("1"), 2, 4);
        return new Amount(result);
    }

    public Amount mul(Amount... opr2) {
        BigDecimal result = this.opr1;
        Amount[] arr$ = opr2;
        int len$ = opr2.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            Amount tmp = arr$[i$];
            result = result.multiply(tmp.opr1);
        }

        return new Amount(result);
    }

    public Amount mulAndRound(int scale, Amount... opr2) {
        BigDecimal result = this.opr1;
        Amount[] arr$ = opr2;
        int len$ = opr2.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            Amount tmp = arr$[i$];
            result = result.multiply(tmp.opr1);
        }

        result = result.divide(new BigDecimal("1"), scale, 4);
        return new Amount(result);
    }

    public Amount div(int scale, Amount... opr2) {
        if (opr2.length == 1) {
            return new Amount(this.opr1.divide(opr2[0].opr1, scale, 4));
        } else {
            BigDecimal result = this.opr1;
            Amount[] arr$ = opr2;
            int len$ = opr2.length;

            for(int i$ = 0; i$ < len$; ++i$) {
                Amount tmp = arr$[i$];
                result = result.divide(tmp.opr1, scale * 2, 4);
            }

            result = result.divide(new BigDecimal("1"), scale, 4);
            return new Amount(result);
        }
    }

    public Amount div(Amount... opr2) {
        if (opr2.length == 1) {
            return new Amount(this.opr1.divide(opr2[0].opr1, 2, 4));
        } else {
            BigDecimal result = this.opr1;
            Amount[] arr$ = opr2;
            int len$ = opr2.length;

            for(int i$ = 0; i$ < len$; ++i$) {
                Amount tmp = arr$[i$];
                result = result.divide(tmp.opr1, 4, 4);
            }

            result = result.divide(new BigDecimal("1"), 2, 4);
            return new Amount(result);
        }
    }

    public Amount round(int scale) {
        return new Amount(this.opr1.divide(new BigDecimal("1"), scale, 4));
    }

    public Amount round() {
        return new Amount(this.opr1.divide(new BigDecimal("1"), 2, 4));
    }

    public int compareTo(Amount opr2) {
        return this.opr1.compareTo(opr2.getOpr());
    }

    public Amount min(Amount... opr2) {
        BigDecimal tmp1 = this.opr1;
        Amount[] arr$ = opr2;
        int len$ = opr2.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            Amount tmp = arr$[i$];
            tmp1 = tmp1.min(tmp.opr1);
        }

        return new Amount(tmp1);
    }

    public Amount max(Amount... opr2) {
        BigDecimal tmp1 = this.opr1;
        Amount[] arr$ = opr2;
        int len$ = opr2.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            Amount tmp = arr$[i$];
            tmp1 = tmp1.max(tmp.opr1);
        }

        return new Amount(tmp1);
    }
}
