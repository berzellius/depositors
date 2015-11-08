package com.mfodepositorsacc.service;

import com.mfodepositorsacc.dmodel.ManagedUnit;
import com.mfodepositorsacc.dmodel.User;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Created by berz on 03.11.2015.
 */
@Service
public interface ManagedUnitsService {
    ManagedUnit seekManagedUnitForUser(User user);

    Set<User> getUsersByManagedUnit(ManagedUnit managedUnit);

    void addUserToUnit(ManagedUnit managedUnit, User user);

    void addNewUserToRoleUnits(User user);
}
