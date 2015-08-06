package com.mfodepositorsacc.batch;

import com.mfodepositorsacc.dmodel.Deposit;
import com.mfodepositorsacc.exceptions.DepositCapitalizationToCancelException;
import com.mfodepositorsacc.service.DepositService;
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
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by berz on 21.06.15.
 */
@Configuration
@EnableBatchProcessing
@EnableAutoConfiguration
@PropertySource("classpath:batch.properties")
public class DepositsCapitalizationBatchConfiguration {
    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private DepositService depositService;



    @Bean
    public ItemReader<Deposit> depositItemReader(){

        JpaPagingItemReader<Deposit> jpaPagingItemReader = new JpaPagingItemReader<Deposit>();
        jpaPagingItemReader.setEntityManagerFactory(entityManager.getEntityManagerFactory());
        jpaPagingItemReader.setQueryString("select d from Deposit d where nextCapitalization <= :td");

        HashMap<String, Object> params = new LinkedHashMap<String, Object>();
        params.put("td", new Date());

        jpaPagingItemReader.setParameterValues(params);

        return jpaPagingItemReader;
    }

    @Bean
    public ItemProcessor<Deposit, Deposit> depositItemProcessor(){
        return new ItemProcessor<Deposit, Deposit>() {
            @Override
            public Deposit process(Deposit deposit){
                try {
                    depositService.capitalize(deposit);
                    return deposit;
                } catch (DepositCapitalizationToCancelException e) {
                    depositService.dropCapitalizationDate(deposit);
                    return null;
                }
            }
        };
    }

    @Bean
    public Step capitalizeStep(
            StepBuilderFactory stepBuilderFactory, ItemReader<Deposit> reader,
            ItemProcessor<Deposit, Deposit> processor
    ){
        return stepBuilderFactory.get("capitalizeStep")
                .<Deposit, Deposit>chunk(1)
                .reader(reader)
                .processor(processor)
                .faultTolerant()
                .skip(RuntimeException.class)
                .skipLimit(2000)
                .build();
    }

    @Bean
    public Job capitalizationJob(Step capitalizeStep){
        RunIdIncrementer runIdIncrementer = new RunIdIncrementer();

        return jobBuilderFactory.get("capitalizationJob")
                .incrementer(runIdIncrementer)
                .flow(capitalizeStep)
                .end()
                .build();
    }
}
