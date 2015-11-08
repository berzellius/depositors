package com.mfodepositorsacc.config;

import com.mfodepositorsacc.billing.BillingMainContract;
import com.mfodepositorsacc.billing.BillingMainContractImpl;
import com.mfodepositorsacc.dmodel.DepositorTypeSettings;
import com.mfodepositorsacc.interceptors.AddTemplatesDataInterceptor;
import com.mfodepositorsacc.service.*;
import com.mfodepositorsacc.settings.LocalProjectSettings;
import com.mfodepositorsacc.settings.ProjectSettings;
import com.mfodepositorsacc.settings.RemoteProjectSettings;
import com.mfodepositorsacc.util.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.ui.velocity.VelocityEngineFactoryBean;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.ResourceBundleViewResolver;

import java.util.Locale;
import java.util.Properties;

/**
 * Created by berz on 20.10.14.
 */
@Configuration
public class ServiceBeanConfiguration {

    @Bean
    AddTemplatesDataInterceptor addTemplatesDataInterceptor(){
        return new AddTemplatesDataInterceptor();
    }

    @Bean
    UserDetailsService userDetailsService(){
        return new UserDetailsServiceImpl();
    }

    @Bean
    LocaleUtil localeUtil(){
        return new LocaleUtilImpl();
    }

    @Bean
    public ReloadableResourceBundleMessageSource messageSource(){
        ReloadableResourceBundleMessageSource messageSource=new ReloadableResourceBundleMessageSource();
        String[] resources = {"classpath:/labels","classpath:/message"};
        messageSource.setBasenames(resources);
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor(){
        LocaleChangeInterceptor localeChangeInterceptor=new LocaleChangeInterceptor();
        localeChangeInterceptor.setParamName("locale");
        return localeChangeInterceptor;
    }

    @Bean
    public SessionLocaleResolver sessionLocaleResolver(){
        SessionLocaleResolver localeResolver=new SessionLocaleResolver();
        localeResolver.setDefaultLocale(new Locale("ru","RU"));
        return localeResolver;
    }

    @Bean
    public MultipartResolver multipartResolver() {
        CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver();
        commonsMultipartResolver.setDefaultEncoding("UTF-8");
        commonsMultipartResolver.setMaxUploadSize(10 * 1024 * 1024);

        return commonsMultipartResolver;
    }

    @Bean
    public ProjectSettings projectSettings(){
        // Локальный сервер
        return new LocalProjectSettings();
        // Боевой сервер
        //return new RemoteProjectSettings();
    }

    @Bean
    public UploadedFileService uploadedFileService(){
        return new UploadedFileServiceImpl();
    }

    @Bean
    public BillingMainContract billingMainContract(){
        return new BillingMainContractImpl();
    }

    @Bean
    public  BillingSystemUtils billingSystemUtils(){
        return new BillingSystemUtilsImpl();
    }

    @Bean
    public DepositCalculationService depositCalculationService(){
        return new DepositCalculationServiceImpl();
    }

    @Bean
    public DateUtil dateUtil(){
        return new DateUtilImpl();
    }

    @Bean
    public DepositorService depositorService(){
        return new DepositorServiceImpl();
    }

    @Bean
    public DepositorTypeSettings depositorTypeSettings(){
        return new DepositorTypeSettings();
    }

    @Bean
    public EmailMessageSender emailMessageSender(){
        EmailMessageSender sender = new EmailMessageSenderImpl();
        sender.setMailSender(mailSender());
        sender.setTemplateMessage(templateMessage());
        return sender;
    }

    @Bean
    public MessageSenderFabric messageSenderFabric(){
        return new MessageSenderFabricImpl();
    }

    @Bean
    public MailSender mailSender(){
        JavaMailSenderImpl ms = new JavaMailSenderImpl();

        ms.setJavaMailProperties((Properties) projectSettings().getMailerSettings().get("properties"));
        ms.setUsername((String) projectSettings().getMailerSettings().get("username"));
        ms.setPassword((String) projectSettings().getMailerSettings().get("password"));

        return ms;
    }

    @Bean
    public SimpleMailMessage templateMessage(){
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom((String) projectSettings().getMailerSettings().get("from"));

        return simpleMailMessage;
    }

    @Bean
    public VelocityEngineFactoryBean velocityEngineFactoryBean(){
        VelocityEngineFactoryBean velocityEngineFactoryBean = new VelocityEngineFactoryBean();
        velocityEngineFactoryBean.setResourceLoaderPath("classpath:/templates/email");
        velocityEngineFactoryBean.setPreferFileSystemAccess(false);

        return velocityEngineFactoryBean;
    }

    @Bean
    public LinkBuilder linkBuilder(){
        return new LinkBuilderImpl();
    }

    @Bean
    public DepositService depositService(){
        return new DepositServiceImpl();
    }

    @Bean
    public MoneyMotionRowUtil moneyMotionRowUtil(){
        return new MoneyMotionRowUtilImpl();
    }

    @Bean
    public NewsService newsService(){
        return new NewsServiceImpl();
    }

    @Bean
    public ManagedUnitsService managedUnitsService(){ return new ManagedUnitsServiceImpl();}
}
