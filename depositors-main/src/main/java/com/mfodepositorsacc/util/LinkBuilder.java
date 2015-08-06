package com.mfodepositorsacc.util;

import com.mfodepositorsacc.dmodel.User;

/**
 * Created by berz on 11.06.15.
 */
public interface LinkBuilder {

    public String getProfileActivation(User user);

    public String getProfilePasswordRestore(User user);
}
