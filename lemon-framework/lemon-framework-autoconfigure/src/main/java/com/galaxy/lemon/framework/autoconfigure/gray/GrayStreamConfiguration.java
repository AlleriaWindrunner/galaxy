package com.galaxy.lemon.framework.autoconfigure.gray;

import com.galaxy.lemon.common.utils.StringUtils;
import com.galaxy.lemon.framework.stream.BindingNameDecorator;
import com.galaxy.lemon.framework.utils.LemonUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

import static com.galaxy.lemon.framework.gray.constant.GrayConstants.*;

/**
 * @author fuyuxi
 * @date 2018/10/24
 * @time 16:02
 */
@ConditionalOnMissingClass("com.galaxy.lemon.gateway.GatewayBootApplication")
@ConditionalOnClass(BindingNameDecorator.class)
public class GrayStreamConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = "lemon.gray.stream.enabled", matchIfMissing = true)
    public BindingNameDecorator bindingNameDecorator() {
        return this::addGrayTag;
    }

    /**
     * 灰度特殊处理 判断环境变量配置如果是 添加后缀
     * 灰度环境 exchange 绑定不同
     *
     * @param bindingName
     * @return
     */
    private String addGrayTag(String bindingName) {
        String versionId = LemonUtils.getProperty(VERSIONID);
        if (StringUtils.isNotBlank(versionId) && versionId.endsWith(GRAY_TAG)) {
            return bindingName + HYPHEN + GRAY_TAG;
        } else {
            return bindingName;
        }
    }
}
