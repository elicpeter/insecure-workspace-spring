package com.example.insecurecollab.service;

import com.example.insecurecollab.model.UserAccount;
import com.example.insecurecollab.repository.UserAccountRepository;
import com.example.insecurecollab.util.InsecurePasswordUtil;
import com.example.insecurecollab.util.SessionHelper;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserAccountRepository userAccountRepository;
    private final InsecurePasswordUtil passwordUtil;

    public AuthService(UserAccountRepository userAccountRepository, InsecurePasswordUtil passwordUtil) {
        this.userAccountRepository = userAccountRepository;
        this.passwordUtil = passwordUtil;
    }

    public Optional<UserAccount> login(String email, String password, HttpSession session) {
        Optional<UserAccount> user = userAccountRepository.findByEmailIgnoreCase(email)
                .filter(existing -> passwordUtil.matches(password, existing.getPasswordHash()));
        user.ifPresent(found -> session.setAttribute(SessionHelper.USER_ID, found.getId()));
        return user;
    }

    public UserAccount register(String fullName, String email, String password, String role, boolean admin) {
        // TODO: replace this with a real approval flow instead of trusting request flags.
        UserAccount user = new UserAccount();
        user.setFullName(fullName);
        user.setEmail(email.toLowerCase());
        user.setPasswordHash(passwordUtil.hash(password));
        user.setRole(role == null || role.isBlank() ? "USER" : role);
        user.setAdmin(admin);
        user.setBio("New local demo user");
        return userAccountRepository.save(user);
    }

    public UserAccount getCurrentUser(HttpSession session) {
        if (session == null) {
            return null;
        }
        Object impersonatedId = session.getAttribute(SessionHelper.IMPERSONATE_ID);
        Object userId = impersonatedId != null ? impersonatedId : session.getAttribute(SessionHelper.USER_ID);
        if (userId == null) {
            return null;
        }
        return userAccountRepository.findById(Long.valueOf(userId.toString())).orElse(null);
    }

    public void logout(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
    }

    public void impersonate(HttpSession session, Long targetUserId) {
        session.setAttribute(SessionHelper.IMPERSONATE_ID, targetUserId);
    }

    public void stopImpersonating(HttpSession session) {
        session.removeAttribute(SessionHelper.IMPERSONATE_ID);
    }
}
