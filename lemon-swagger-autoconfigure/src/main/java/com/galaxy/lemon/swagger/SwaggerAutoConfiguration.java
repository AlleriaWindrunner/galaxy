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

package com.galaxy.lemon.swagger;


import com.galaxy.lemon.common.LemonConstants;
import com.galaxy.lemon.common.LemonFramework;
import com.galaxy.lemon.common.utils.JudgeUtils;
import com.galaxy.lemon.common.utils.ReflectionUtils;
import com.galaxy.lemon.swagger.SwaggerProperties.GlobalParam;
import io.swagger.models.Swagger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.IgnoredRequestCustomizer;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.builders.*;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

import static com.galaxy.lemon.common.utils.JudgeUtils.isEmpty;

/**
 *
 * @author <mailto:eleven.hm@vip.163.com">eleven</a>
 * @see
 * @since 1.0.0
 */

@Configuration
@ConditionalOnClass({Swagger.class, EnableSwagger2.class})
@ConditionalOnWebApplication
@Conditional(SwaggerAvailableCondition.class)
public class SwaggerAutoConfiguration {
    private static final String CURRENT_ENV = LemonConstants.PROFILES_ACTIVE;
    public static final String SWAGGER_UI_URI = "/swagger-ui.html";

    @EnableConfigurationProperties(SwaggerProperties.class)
    @Configuration
    @EnableSwagger2
    public static class EnableSwagger2Configuration implements EnvironmentAware , BeanFactoryAware {
        private Environment environment;
        private SwaggerProperties swaggerProperties;
        private BeanFactory beanFactory;

        @Autowired
        public EnableSwagger2Configuration(SwaggerProperties swaggerProperties) {
            this.swaggerProperties = swaggerProperties;
        }

        @PostConstruct
        public void init() {
            if (isEmpty(this.swaggerProperties.getGlobalRequestParams()) && requiredGlobalRequestParams()) {
                setDefaultGlobalRequestParams(this.swaggerProperties);
            }
            this.swaggerProperties.getGlobalRequestParams().forEach((key, gp) -> JudgeUtils.callbackIfNecessary(isEmpty(gp.getName()), () -> gp.setName(key)));
        }

        private boolean requiredGlobalRequestParams() {
            return "true".equalsIgnoreCase(this.swaggerProperties.getEnabledDefaultGlobalRequestParams());
        }

        private void setDefaultGlobalRequestParams(SwaggerProperties swaggerProperties) {
            Map<String, GlobalParam> defaultGlobleParams = new HashMap<>();
            swaggerProperties.setGlobalRequestParams(defaultGlobleParams);
            defaultGlobleParams.put("x-auth-token", new GlobalParam("sessionId???????????????????????????","header","String"));
            defaultGlobleParams.put("x-lemon-secure", new GlobalParam("????????????","header","String"));
            defaultGlobleParams.put("x-lemon-sign", new GlobalParam("??????; ?????? ==>> Json -> Json + ??????; QueryString -> queryString+?????????Form -> ????????????????????????(gateway??????)????????????????????????+????????? PathVariable -> path??????????????????????????????+??????",
                    "header","String"));
            defaultGlobleParams.put("x-lemon-channel", new GlobalParam("????????? ????????????????????????????????????????????????","header","String"));
            defaultGlobleParams.put("Accept-Language", new GlobalParam("????????????","header","String"));
        }

        @Bean
        public Docket restfulApi() {
            boolean isShow = isShow();
            String scanPackage = swaggerProperties.getScanPackage();
            if (JudgeUtils.isBlank(scanPackage)) {
                scanPackage = LemonFramework.getScanPackage();
            }
            if (JudgeUtils.isBlank(scanPackage)) {
                List<String> autoPackages = AutoConfigurationPackages.get(beanFactory);
                scanPackage = Optional.ofNullable(autoPackages).map(s -> s.get(0)).orElse("com.galaxy.lemon");
            }

            return new Docket(DocumentationType.SWAGGER_2)
                    .enable(isShow)
                    .apiInfo(apiInfo())
                    .globalOperationParameters(operationParameters())
                    .forCodeGeneration(false)
                    .select()
                    .apis(RequestHandlerSelectors.basePackage(isShow ? scanPackage : "xx.xx.xx"))
                    // .apis(isShow ? RequestHandlerSelectors.any() : RequestHandlerSelectors.none()) // ?????????api????????????
                    .paths(isShow ? PathSelectors.any() : PathSelectors.none()) //???????????????????????????
                    .build()
                    // .enableUrlTemplating(isShow)
                    //   .genericModelSubstitutes(GenericDTO.class)
                    .useDefaultResponseMessages(false)
                    .globalResponseMessage(RequestMethod.GET, responseMessages())
                    .globalResponseMessage(RequestMethod.POST, responseMessages())
                    .globalResponseMessage(RequestMethod.PUT, responseMessages())
                    .globalResponseMessage(RequestMethod.DELETE, responseMessages())
                    .ignoredParameterTypes(ReflectionUtils.forNameThrowRuntimeExceptionIfNecessary("com.galaxy.lemon.framework.data.NoBody"));
        }

        private ApiInfo apiInfo() {
            return new ApiInfoBuilder()
                    .title(getTitle().toUpperCase())
                    .description("current environment is " + environment.getProperty(CURRENT_ENV)
                                    + Optional.ofNullable(this.swaggerProperties.getDescription()).map(m -> "; " + m).orElse("")
                        /*+" <div> <em> ?????????????????????msgCd???msgInfo??????, ????????????????????????XXX00000 </em> </div> "*/)
                    .termsOfServiceUrl(this.swaggerProperties.getServiceUrl())
                    .contact(new Contact(this.swaggerProperties.getContactName(), this.swaggerProperties.getContactUrl(), this.swaggerProperties.getContactEmail()))
                    .version(this.swaggerProperties.getVersion())
                    .build();
        }

        private List<Parameter> operationParameters() {
            return Optional.ofNullable(this.swaggerProperties.getGlobalRequestParams()).filter(JudgeUtils::isNotEmpty)
                    .map(g -> g.values().stream().map(this::buildParameter).collect(Collectors.toList())).orElseGet(ArrayList::new);
        }

        private List<ResponseMessage> responseMessages() {
            List<ResponseMessage> responses = new ArrayList<>();
            responses.add(new ResponseMessageBuilder().code(200).message("??????").build());
            responses.add(new ResponseMessageBuilder().code(500).message("?????????????????????").responseModel(new ModelRef("Error")).build());
            responses.add(new ResponseMessageBuilder().code(401).message("??????????????????").responseModel(new ModelRef("Error")).build());
            responses.add(new ResponseMessageBuilder().code(403).message("?????????????????????").responseModel(new ModelRef("Error")).build());
            responses.add(new ResponseMessageBuilder().code(404).message("?????????????????????").responseModel(new ModelRef("Error")).build());

            Map<String, ResponseMessage> globleResponses = this.swaggerProperties.getGlobalResponseMessage();
            if (JudgeUtils.isNotEmpty(globleResponses)) {
                responses.addAll(globleResponses.values());
            }
            return responses;
        }

        private Parameter buildParameter(GlobalParam gp) {
            ParameterBuilder parameterBuilder = new ParameterBuilder();
            return parameterBuilder.name(gp.getName()).description(gp.getDesc()).parameterType(gp.getType())
                    .required(gp.isRequired()).modelRef(new ModelRef(gp.getModelRef())).defaultValue(gp.getDefaultValue()).build();
        }

        private boolean isShow() {
            return true;
        }

        private String getTitle() {
            return this.environment.getProperty(LemonConstants.APPLICATION_NAME) + " APIs";
        }

        @Override
        public void setEnvironment(Environment environment) {
            this.environment = environment;
        }

        @Override
        public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
            this.beanFactory = beanFactory;
        }
    }

    @Configuration
    @ConditionalOnClass(WebSecurity.class)
    static class SwaggerIgnoredRequestConfiguration {

        private ServerProperties serverProperties;

        public SwaggerIgnoredRequestConfiguration(ObjectProvider<ServerProperties> serverProperties) {
            this.serverProperties = serverProperties.getIfAvailable();
        }

        @Bean
        public IgnoredRequestCustomizer swaggerIgnoredRequestCustomizer() {
            return new SwaggerIgnoredRequestCustomizer(this.serverProperties);
        }

        private static class SwaggerIgnoredRequestCustomizer implements IgnoredRequestCustomizer {

            private ServerProperties serverProperties;

            public SwaggerIgnoredRequestCustomizer(ServerProperties serverProperties) {
                this.serverProperties = serverProperties;
            }

            @Override
            public void customize(WebSecurity.IgnoredRequestConfigurer configurer) {
                String swaggerUI = resolvePath(SWAGGER_UI_URI);
                List<RequestMatcher> requestMatchers = new ArrayList<>();
                requestMatchers.add(new AntPathRequestMatcher(swaggerUI));
                requestMatchers.add(new AntPathRequestMatcher(swaggerUI + ".*"));
                requestMatchers.add(new AntPathRequestMatcher(swaggerUI + ".*/**"));
                requestMatchers.add(new AntPathRequestMatcher(resolvePath("/swagger-resources/**")));
                requestMatchers.add(new AntPathRequestMatcher(resolvePath("/v2/api-docs")));
                configurer.requestMatchers(new OrRequestMatcher(requestMatchers));
            }

            private String resolvePath(String path) {
                return Optional.ofNullable(this.serverProperties).map(s -> s.getPath(path)).orElse(path);
            }

        }

    }

}
