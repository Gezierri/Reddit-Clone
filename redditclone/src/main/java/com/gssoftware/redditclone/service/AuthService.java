package com.gssoftware.redditclone.service;

import com.gssoftware.redditclone.dto.RegisterRequest;
import com.gssoftware.redditclone.model.NotificationEmail;
import com.gssoftware.redditclone.model.User;
import com.gssoftware.redditclone.model.VerificationToken;
import com.gssoftware.redditclone.repository.UserRepository;
import com.gssoftware.redditclone.repository.VerificationTokenRepository;
import com.gssoftware.redditclone.service.exception.SpringRedditException;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final MailService mailService;

    @Transactional
    public void signup(RegisterRequest registerRequest) {
        User user = new User();
        user. setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setCreated(Instant.now());
        user.setEnabled(false);

        userRepository.save(user);
        String token = generateVerificationToken(user);

        mailService.sendMail(new NotificationEmail(
                "Please, activate your account",
                user.getEmail(),
                "Click on the below link\n" +
                        "http://localhost:8080/api/auth/accountVerification/" + token
        ));
    }

    private String generateVerificationToken(User user) {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationTokenRepository.save(verificationToken);
        return token;
    }

    public void verifyAccount(String token) {
        Optional<VerificationToken> userToken = verificationTokenRepository.findByToken(token);
        userToken.orElseThrow(() -> new SpringRedditException("Invalid token"));
        fetchUserAndEnable(userToken.get());
    }

    @Transactional
    private void fetchUserAndEnable(VerificationToken verificationToken) {
        String userName = verificationToken.getUser().getUsername();
        User user = userRepository.findByUsername(userName).orElseThrow(() -> new SpringRedditException("User not found!"));
        user.setEnabled(true);
        userRepository.save(user);
    }
}
