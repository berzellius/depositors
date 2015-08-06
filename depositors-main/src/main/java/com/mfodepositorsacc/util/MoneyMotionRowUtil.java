package com.mfodepositorsacc.util;

import com.mfodepositorsacc.accountingmachine.dmodel.Document;
import com.mfodepositorsacc.billing.dmodel.DepositPayInAccountDocument;
import com.mfodepositorsacc.dto.MoneyMotionRow;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Created by berz on 21.06.15.
 */
public interface MoneyMotionRowUtil {

    List<MoneyMotionRow> toList(Page<? extends Document> documents);

}
