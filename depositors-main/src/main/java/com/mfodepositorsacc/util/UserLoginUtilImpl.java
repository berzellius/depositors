package com.mfodepositorsacc.util;

import com.mfodepositorsacc.dmodel.*;
import com.mfodepositorsacc.exceptions.WrongInputDataException;
import com.mfodepositorsacc.repository.NotificationRepository;
import com.mfodepositorsacc.repository.UserRepository;
import com.mfodepositorsacc.repository.UserRoleRepository;
import com.mfodepositorsacc.service.DepositCalculationService;
import com.mfodepositorsacc.specifications.UserSpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by berz on 06.11.14.
 */
@Service
@Transactional
public class UserLoginUtilImpl implements UserLoginUtil {

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    DepositCalculationService depositCalculationService;

    @Autowired
    UserRoleRepository userRoleRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    NotificationRepository notificationRepository;

    @Override
    public User getCurrentLogInUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName();
        if (name == null) return null;

        return (User) userDetailsService.loadUserByUsername(name);
    }

    @Override
    public boolean userHaveRole(User user, UserRole.Role role) {
        return user.getAuthorities().contains(userRoleRepository.findByRole(role).get(0));
    }

    @Override
    public void addUserWithRole(User user, final UserRole.Role role) throws WrongInputDataException {

        if(user.getUsername() == null || user.getUsername().equals(""))
            throw new WrongInputDataException("username is required for new user!", WrongInputDataException.Reason.USERNAME_FIELD);

        if(userRepository.findByUsername(user.getUsername()) != null)
            throw new WrongInputDataException("user with username = ".concat(user.getUsername()).concat(" already exists"), WrongInputDataException.Reason.DUPLICATE_USERNAME);

        if(
                role != UserRole.Role.DEPOSITOR &&
                        (user.getPassword() == null || user.getPassword().equals(""))
                )
            throw new WrongInputDataException("password is required for new user!", WrongInputDataException.Reason.PASSWORD_FIELD);


        Set<UserRole> userRoles = new HashSet<UserRole>(){{add(userRoleRepository.findByRole(role).get(0));}};
        user.setAuthorities(userRoles);

        Md5PasswordEncoder encoder = new Md5PasswordEncoder();
        user.setPassword(encoder.encodePassword(user.getPassword(), null));

        userRepository.save(user);

    }

    @Override
    public void changePassForUser(User user, String password) throws WrongInputDataException {
        if(password == null || password.equals("")) throw new WrongInputDataException("not empty password required!", WrongInputDataException.Reason.PASSWORD_FIELD);

        Md5PasswordEncoder encoder = new Md5PasswordEncoder();
        user.setPassword(encoder.encodePassword(password, null));

        userRepository.save(user);
    }

    @Override
    public void activateUser(String code, String password, String passwordConfirm) throws WrongInputDataException {

        User user = userRepository.findByActivationCode(code);
        if(user == null)
            throw new WrongInputDataException("wrong activation code", WrongInputDataException.Reason.USER_ACTIVATION_CODE);

        if(!password.equals(passwordConfirm))
            throw new WrongInputDataException("passwords mismatch", WrongInputDataException.Reason.PASSWORDS_MISMATCH);

        Md5PasswordEncoder encoder = new Md5PasswordEncoder();
        user.setPassword(encoder.encodePassword(password, null));
        user.setEnabled(true);
        user.setActivationCode(null);
        userRepository.save(user);
    }

    @Override
    public Long countNonActivatedRegistration() {
        return userRepository.count(getNonConfirmedRegistrationSpecification());
    }

    @Override
    public Specification<User> getNonConfirmedRegistrationSpecification(){
        return UserSpecifications.nonConfirmedRegistration(userRoleRepository.findByRole(UserRole.Role.DEPOSITOR).get(0));
    }

    @Override
    public void requestToRestorePassword(Deposit deposit) throws WrongInputDataException {
        if(deposit == null){
            throw new WrongInputDataException("no such deposit", WrongInputDataException.Reason.DEPOSIT_FIELD);
        }

        User user = userRepository.findByDeposit(deposit);

        if(user == null){
            throw new WrongInputDataException("there is no user for this deposit", WrongInputDataException.Reason.USER_FIELD);
        }

        user.setActivationCode(depositCalculationService.getRandomStringForActivationCode());
        user.setRestorePassRequested(true);

        userRepository.save(user);

        EmailNotification emailNotification = new EmailNotification();
        emailNotification.setTemplate(Notification.Template.RESTORE_PASSWORD);
        emailNotification.setUser(user);
        emailNotification.setDtmCreate(new Date());
        emailNotification.setStatus(Notification.Status.WAITING);
        notificationRepository.save(emailNotification);
    }

    @Override
    public void restorePassword(String code, String password, String passwordConfirm) throws WrongInputDataException {
        User user = userRepository.findByActivationCode(code);
        if(user == null)
            throw new WrongInputDataException("wrong activation code", WrongInputDataException.Reason.USER_ACTIVATION_CODE);

        if(!password.equals(passwordConfirm))
            throw new WrongInputDataException("passwords mismatch", WrongInputDataException.Reason.PASSWORDS_MISMATCH);

        Md5PasswordEncoder encoder = new Md5PasswordEncoder();
        user.setPassword(encoder.encodePassword(password, null));
        user.setEnabled(true);
        user.setActivationCode(null);
        userRepository.save(user);
    }
}
