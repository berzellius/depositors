package com.mfodepositorsacc.service;

import com.mfodepositorsacc.dmodel.*;
import com.mfodepositorsacc.repository.*;
import com.mfodepositorsacc.specifications.UserSpecifications;
import org.apache.commons.collections.set.ListOrderedSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;

/**
 * Created by berz on 03.11.2015.
 */
@Service
@Transactional
public class ManagedUnitsServiceImpl implements ManagedUnitsService {

    @Autowired
    ManagedUserRepository managedUserRepository;

    @Autowired
    ManagedGroupRepository managedGroupRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserRoleRepository userRoleRepository;

    @Autowired
    ManagedGroupMemberRoleRepository managedGroupMemberRoleRepository;

    @Override
    public ManagedUnit seekManagedUnitForUser(User user) {
        ManagedUnit managedUnit = managedUserRepository.findByUser(user);

        if (managedUnit == null) {
            ManagedUser managedUser = new ManagedUser();
            managedUser.setUser(user);
            managedUser.setName("User[" + user.getId() + "]");

            managedUserRepository.save(managedUser);
            return managedUser;
        } else {
            return managedUnit;
        }
    }

    @Override
    public Set<User> getUsersByManagedUnit(ManagedUnit managedUnit) {
        Set<User> users = new ListOrderedSet();

        if (managedUnit instanceof ManagedUser) {
            User user = ((ManagedUser) managedUnit).getUser();
            if (user != null) {
                if (!users.contains(user)) {
                    users.add(user);
                }
            }
        }

        if (managedUnit instanceof ManagedGroup) {
            Set<ManagedGroupMember> managedGroupMembers = ((ManagedGroup) managedUnit).getManagedGroupMembers();
            if (managedGroupMembers != null) {
                for (ManagedGroupMember managedGroupMember : managedGroupMembers) {
                    if (managedGroupMember instanceof ManagedGroupMemberUnit) {
                        ManagedUnit unit = ((ManagedGroupMemberUnit) managedGroupMember).getManagedUnit();
                        if (unit != null) {
                            Set<User> usersInUnit = getUsersByManagedUnit(managedUnit);
                            for (User user : usersInUnit) {
                                if (!users.contains(user)) {
                                    users.add(user);
                                }
                            }
                        }
                    }

                    if (managedGroupMember instanceof ManagedGroupMemberRole) {
                        UserRole userRole = ((ManagedGroupMemberRole) managedGroupMember).getUserRole();
                        if (userRole != null) {
                            List<User> usersByRole = userRepository.findAll(
                                    Specifications.where(UserSpecifications.hasRole(
                                            userRoleRepository.findOneByRole(UserRole.Role.DEPOSITOR)
                                    ))
                            );

                            for (User user : usersByRole) {
                                if (!users.contains(user)) {
                                    users.add(user);
                                }
                            }
                        }
                    }
                }
            }
        }

        return users;
    }

    @Override
    public void addUserToUnit(ManagedUnit managedUnit, User user) {
        Set<User> users = managedUnit.getUsers();

        if (users == null) {
            users = new ListOrderedSet();
        }

        if (!users.contains(user)) {
            users.add(user);
        }
        managedUnit.setUsers(users);

        if(managedUnit instanceof ManagedGroup) {
            managedGroupRepository.save((ManagedGroup) managedUnit);
        }

        if(managedUnit instanceof ManagedUser){
            managedUserRepository.save((ManagedUser) managedUnit);
        }
    }

    @Override
    public void addNewUserToRoleUnits(User user) {
        Iterable<ManagedGroupMemberRole> managedGroupMemberRoleList = managedGroupMemberRoleRepository.findAll();
        for (ManagedGroupMember managedGroupMember : managedGroupMemberRoleList) {
            for (ManagedGroup managedGroup : managedGroupMember.getManagedGroups()) {
                addUserToUnit(managedGroup, user);
            }
        }

        ManagedUnit managedUnit = seekManagedUnitForUser(user);
        addUserToUnit(managedUnit, user);
    }
}
