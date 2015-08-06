import com.mfodepositorsacc.accountingmachine.service.AccountMachine;
import com.mfodepositorsacc.accountingmachine.service.AccountMachineImpl;
import com.mfodepositorsacc.billing.BillingMainContract;
import com.mfodepositorsacc.billing.BillingMainContractImpl;
import com.mfodepositorsacc.billing.service.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
/**
 * Created by berz on 12.01.15.
 */
@Configuration
public class TestBillingBeanConfiguration {

    @Bean
    BillingMainContract billingMainContract(){
        return new BillingMainContractImpl();
    }

    @Bean
    AccountMachine accountMachine(){
        return new AccountMachineImpl();
    }

    @Bean
    BillingService billingService(){
        return new BillingServiceImpl();
    }

    @Bean
    DocumentsService documentsService(){
        return new DocumentsServiceImpl();
    }

    @Bean
    AccountUtils accountUtils(){
        return new AccountUtilsImpl();
    }

}
