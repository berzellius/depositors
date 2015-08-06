package com.mfodepositorsacc.util;

import com.mfodepositorsacc.dmodel.Deposit;
import com.mfodepositorsacc.exceptions.WrongInputDataException;
import com.mfodepositorsacc.dmodel.User;
import com.mfodepositorsacc.dmodel.UserRole;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

/**
 * Created by berz on 06.11.14.
 */
@Service
public interface UserLoginUtil {

    public User getCurrentLogInUser();

    boolean userHaveRole(User currentUser, UserRole.Role admin);

    void addUserWithRole(User user, UserRole.Role role) throws WrongInputDataException;

    void changePassForUser(User user, String password) throws WrongInputDataException;

    void activateUser(String code, String password, String passwordConfirm) throws WrongInputDataException;

    Long countNonActivatedRegistration();

    Specification<User> getNonConfirmedRegistrationSpecification();

    void requestToRestorePassword(Deposit deposit) throws WrongInputDataException;

    void restorePassword(String code, String password, String passwordConfirm) throws WrongInputDataException;
}
