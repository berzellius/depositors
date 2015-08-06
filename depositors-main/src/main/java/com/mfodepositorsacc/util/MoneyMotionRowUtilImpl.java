package com.mfodepositorsacc.util;

import com.mfodepositorsacc.accountingmachine.dmodel.Document;
import com.mfodepositorsacc.billing.dmodel.DepositOutcomeDocument;
import com.mfodepositorsacc.billing.dmodel.DepositPayInAccountDocument;
import com.mfodepositorsacc.billing.dmodel.DepositPayMonthlyProfitDocument;
import com.mfodepositorsacc.dto.MoneyMotionRow;
import com.mfodepositorsacc.repository.DepositRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by berz on 21.06.15.
 */
@Service
@Transactional
public class MoneyMotionRowUtilImpl implements MoneyMotionRowUtil {
    @Autowired
    DepositRepository depositRepository;

    @Override
    public List<MoneyMotionRow> toList(Page<? extends Document> documents) {
        List<MoneyMotionRow> moneyMotionRows = new LinkedList<MoneyMotionRow>();

        for(Document document : documents){
            Long depositId = null;

            // Вариантов не слишком много, можно напрямую перебрать
            if(document instanceof DepositPayInAccountDocument){
                depositId = ((DepositPayInAccountDocument) document).getDepositId();
            }

            if(document instanceof DepositOutcomeDocument){
                depositId = ((DepositOutcomeDocument) document).getDepositId();
            }

            if(document instanceof DepositPayMonthlyProfitDocument){
                depositId = ((DepositPayMonthlyProfitDocument) document).getDepositId();
            }

            if(depositId != null){
                MoneyMotionRow moneyMotionRow = new MoneyMotionRow(depositRepository.findOne(depositId), document);
                moneyMotionRows.add(moneyMotionRow);
            }
        }

        return moneyMotionRows;
    }
}
