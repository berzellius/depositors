package com.mfodepositorsacc.service;

import com.mfodepositorsacc.maincontract.MainContract;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by berz on 10.11.2015.
 */
@Service
public class ReadJsonFilesImpl implements ReadJsonFiles {
    private String mainContractFile;
    private String jsonFilesLocation;
    private String extension;

    @Override
    public MainContract readMainContract() throws IOException {
        String path = this.jsonFilesLocation + this.mainContractFile + "." + this.extension;
        ClassPathResource classPathResource = new ClassPathResource(path);
        InputStream is = classPathResource.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));

        String result = "";
        String str;
        while ((str = bufferedReader.readLine()) != null) {
            result += str;
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationConfig.Feature.AUTO_DETECT_FIELDS, true);

        MainContract mainContract = mapper.readValue(classPathResource.getFile(), MainContract.class);
        return mainContract;
    }

    @Override
    public void setMainContractFile(String mainContractFile) {
        this.mainContractFile = mainContractFile;
    }

    @Override
    public void setJsonFilesLocation(String jsonFilesLocation) {
        this.jsonFilesLocation = jsonFilesLocation;
    }

    @Override
    public void setExtension(String extension) {
        this.extension = extension;
    }
}
