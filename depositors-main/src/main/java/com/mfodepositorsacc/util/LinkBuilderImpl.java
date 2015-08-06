package com.mfodepositorsacc.util;

import com.mfodepositorsacc.dmodel.User;
import com.mfodepositorsacc.settings.ProjectSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by berz on 11.06.15.
 */
@Component
public class LinkBuilderImpl implements LinkBuilder {
    @Autowired
    ProjectSettings projectSettings;

    @Override
    public String getProfileActivation(User user) {
        if(user.isEnabled() == false && user.getActivationCode() != null){
            return addPrefix("users/activation?code=".concat(user.getActivationCode()));
        }

        return null;
    }

    @Override
    public String getProfilePasswordRestore(User user) {
        if(user.getRestorePassRequested() && user.getActivationCode() != null){
            return addPrefix("users/restore_pass_link?code=".concat(user.getActivationCode()));
        }

        return null;
    }

    private String addPrefix(String url){
        return projectSettings.getSiteUrl().concat(url);
    }
}
