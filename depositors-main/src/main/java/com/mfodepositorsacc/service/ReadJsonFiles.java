package com.mfodepositorsacc.service;

import com.mfodepositorsacc.maincontract.MainContract;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Created by berz on 10.11.2015.
 */
@Service
public interface ReadJsonFiles {
    MainContract readMainContract() throws IOException;

    void setMainContractFile(String mainContractFile);

    void setJsonFilesLocation(String jsonFilesLocation);

    void setExtension(String extension);
}
