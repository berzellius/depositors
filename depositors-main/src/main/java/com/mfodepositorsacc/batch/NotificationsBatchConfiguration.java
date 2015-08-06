package com.mfodepositorsacc.batch;

import com.mfodepositorsacc.dmodel.Notification;
import com.mfodepositorsacc.exceptions.NotificationSendException;
import com.mfodepositorsacc.exceptions.WrongInputDataException;
import com.mfodepositorsacc.service.NotificationService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.HashMap;


/**
 * Created by berz on 04.06.15.
 */
@Configuration
@EnableBatchProcessing
@EnableAutoConfiguration
@PropertySource("classpath:batch.properties")
public class NotificationsBatchConfiguration {

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private NotificationService notificationService;


    @Bean
    public ItemReader<Notification> reader() {
        JpaPagingItemReader<Notification> jpaPagingItemReader = new JpaPagingItemReader<Notification>();
        jpaPagingItemReader.setEntityManagerFactory(entityManager.getEntityManagerFactory());
        jpaPagingItemReader.setQueryString("select n from Notification n where status = :s");

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("s", Notification.Status.WAITING);

        jpaPagingItemReader.setParameterValues(params);

        return jpaPagingItemReader;
    }

    @Bean
    public ItemProcessor<Notification, Notification> processor() {
        return new ItemProcessor<Notification, Notification>() {
            @Override
            public Notification process(final Notification notification){

                try {
                    notificationService.send(notification);
                } catch (WrongInputDataException e) {
                    System.out.println("problem with "
                            .concat(notification.toString())
                            .concat("; ")
                            .concat(e.getMessage())
                    );
                } catch (NotificationSendException e) {
                    System.out.println("problem with "
                            .concat(notification.toString())
                            .concat("; ")
                            .concat(e.getMessage())
                    );
                }

                return notification;
            }
        };
    }

    @Bean
    public Step sendStep(StepBuilderFactory stepBuilderFactory, ItemReader<Notification> reader,
                         ItemProcessor<Notification, Notification> processor) {
        return stepBuilderFactory.get("sendStep")
                .<Notification, Notification>chunk(10)
                .reader(reader)
                .processor(processor)
                .build();
    }

    @Bean
    public Job notificationSendJob(Step sendStep) throws Exception {
        RunIdIncrementer runIdIncrementer = new RunIdIncrementer();

        return jobBuilderFactory.get("notificationSendJob")
                .incrementer(runIdIncrementer)
                .flow(sendStep)
                .end()
                .build();
    }
}
