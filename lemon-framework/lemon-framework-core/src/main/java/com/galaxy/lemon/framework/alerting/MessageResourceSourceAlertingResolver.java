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

package com.galaxy.lemon.framework.alerting;

import com.galaxy.lemon.common.LemonConstants;
import com.galaxy.lemon.framework.i18n.LocaleMessageSource;
import com.galaxy.lemon.framework.alerting.ConfigurableAlerting;
import org.springframework.context.NoSuchMessageException;

import java.util.Optional;

/**
 * resolve alerting by message resources
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

public class MessageResourceSourceAlertingResolver extends AbstractAlertingResolver{

    private LocaleMessageSource localeMessageSource;
    private Boolean ignoreNoSuchMessageException = false;

    public MessageResourceSourceAlertingResolver(LocaleMessageSource localeMessageSource) {
        this.localeMessageSource = localeMessageSource;
    }

    public MessageResourceSourceAlertingResolver(LocaleMessageSource localeMessageSource, Boolean ignoreNoSuchMessageException) {
        this.localeMessageSource = localeMessageSource;
        this.ignoreNoSuchMessageException = ignoreNoSuchMessageException;
    }

    @Override
    protected ConfigurableAlerting doResolve(ConfigurableAlerting configurableAlerting) {
        try {
            configurableAlerting.setMsgInfo(Optional.ofNullable(this.localeMessageSource).map(l -> l.getMessage(configurableAlerting.getMsgCd())).orElse(LemonConstants.EMPTY_STRING));
        } catch (NoSuchMessageException e) {
            if (! this.ignoreNoSuchMessageException) {
                throw e;
            }
        }
        return configurableAlerting;
    }
}
