package com.mfodepositorsacc.settings;

import java.util.*;

/**
 * Created by berz on 23.11.14.
 */
public abstract class CommonProjectSettings implements ProjectSettings {

    @Override
    public final List<String> getAllowedFileMimeTypes(){
        List<String> types = new LinkedList<String>();
        types.add("image/png");
        types.add("image/jpeg");
        types.add("image/gif");
        types.add("image/tiff");

        types.add("application/pdf");

        return types;
    }

    @Override
    public final HashMap<String, Object> getMailerSettings(){

        HashMap<String, Object> settings = new LinkedHashMap<String, Object>();

        Properties props = new Properties();
        // setting properties
        props.put("mail.debug", "true");
        props.put("mail.smtp.auth", "true");
        //props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtps.ssl.checkserveridentity", "true");
        props.put("mail.smtps.ssl.trust", "*");
        props.put("mail.smtp.host", "mail.yazaimy.ru");
        props.put("mail.smtp.port", "25");

        settings.put("properties", props);
        settings.put("username", "infio@yazaimy.ru");
        settings.put("password", "finnal322899");
        settings.put("from", "infio@yazaimy.ru");


        return settings;
    }
}
