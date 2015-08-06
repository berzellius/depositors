package com.mfodepositorsacc.config;

import com.mfodepositorsacc.dmodel.DepositorTypeSettings;
import com.mfodepositorsacc.dmodel.SumSettings;
import com.mfodepositorsacc.enumerated.Source;
import com.mfodepositorsacc.interceptors.AddTemplatesDataInterceptor;
import com.mfodepositorsacc.repository.SumSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by berz on 20.10.14.
 */
@Configuration
public class ConfigMVCConfigurerAdapter extends WebMvcConfigurerAdapter {

    @Autowired
    AddTemplatesDataInterceptor addTemplatesDataInterceptor;

    @Autowired
    LocaleChangeInterceptor localeChangeInterceptor;

    @Autowired
    SumSettingsRepository sumSettingsRepository;

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {

        registry.addInterceptor(addTemplatesDataInterceptor);

        registry.addInterceptor(localeChangeInterceptor);

    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/WEB-INF/static/");
    }

    @Override
    public void addFormatters(org.springframework.format.FormatterRegistry registry){
        registry.addConverter(stringSourceConverter());
        registry.addConverter(stringDepositorFormTypeConverter());
        registry.addConverter(stringDateConverter());
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        PageableHandlerMethodArgumentResolver resolver = new PageableHandlerMethodArgumentResolver();
        resolver.setFallbackPageable(new PageRequest(0, 5));
        argumentResolvers.add(resolver);
    }

    public Converter<String, Source> stringSourceConverter(){
        return new Converter<String, Source>() {
            @Override
            public Source convert(String s) {

                Source source = Source.valueOfSource(s);

                return source;
            }
        };
    }

    public Converter<String, DepositorTypeSettings.DepositorFormType> stringDepositorFormTypeConverter(){
        return new Converter<String, DepositorTypeSettings.DepositorFormType>() {
            @Override
            public DepositorTypeSettings.DepositorFormType convert(String s) {
                return DepositorTypeSettings.DepositorFormType.valueOf(s);
            }
        };
    }

    public Converter<String, Date> stringDateConverter(){
        return new Converter<String, Date>() {
            @Override
            public Date convert(String s) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.mm.yyyy");
                try {
                    return simpleDateFormat.parse(s);
                } catch (ParseException e) {
                    return null;
                }
            }
        };
    }

    public Converter<String, SumSettings> stringSumSettingsConverter(){
        return new Converter<String, SumSettings>() {
            @Override
            public SumSettings convert(String s) {
                Long id = Long.decode(s);
                return sumSettingsRepository.findOne(id);
            }
        };
    }

}
