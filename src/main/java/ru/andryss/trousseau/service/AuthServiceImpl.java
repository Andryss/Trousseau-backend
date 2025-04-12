package ru.andryss.trousseau.service;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;
import ru.andryss.trousseau.exception.Errors;
import ru.andryss.trousseau.generated.model.SignInRequest;
import ru.andryss.trousseau.generated.model.SignUpRequest;
import ru.andryss.trousseau.model.SessionEntity;
import ru.andryss.trousseau.model.UserEntity;
import ru.andryss.trousseau.model.UserRole;
import ru.andryss.trousseau.repository.UserRepository;
import ru.andryss.trousseau.security.JwtTokenUtil;
import ru.andryss.trousseau.security.UserData;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;
    private final TimeService timeService;
    private final SessionService sessionService;
    private final TransactionTemplate transactionTemplate;

    private final DateTimeFormatter idFormatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmssSSS");

    @Override
    public String signUp(SignUpRequest request) {
        log.info("Signing up user {}", request.getUsername());

        String username = request.getUsername();
        Optional<UserEntity> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            throw Errors.usernameForbidden(username);
        }

        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        ZonedDateTime now = timeService.now();
        user.setId(idFormatter.format(now));
        user.setCreatedAt(now.toInstant());

        List<UserRole> userRoles = getUserRoles(request);
        List<String> roleIds = userRoles.stream()
                .map(UserRole::getId)
                .toList();

        transactionTemplate.executeWithoutResult(status -> {
            userRepository.save(user);
            userRepository.saveUserRoles(user.getId(), roleIds);
        });

        return generateTokenFromUser(user);
    }

    private List<UserRole> getUserRoles(SignUpRequest request) {
        List<UserRole> roles = new ArrayList<>();
        if (!StringUtils.isBlank(request.getUsername())
                && !CollectionUtils.isEmpty(request.getContacts())) {
            roles.add(UserRole.USER);

            if (!StringUtils.isBlank(request.getRoom())) {
                roles.add(UserRole.SELLER);
            }
        }
        return roles;
    }

    @Override
    public String signIn(SignInRequest request) {
        log.info("Signing in user {}", request.getUsername());

        Optional<UserEntity> optionalUser = userRepository.findByUsername(request.getUsername());
        if (optionalUser.isEmpty()) {
            throw Errors.unauthorized();
        }

        UserEntity user = optionalUser.get();
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        if (!encodedPassword.equals(user.getPasswordHash())) {
            throw Errors.unauthorized();
        }

        return generateTokenFromUser(user);
    }

    private String generateTokenFromUser(UserEntity user) {
        List<String> userRoles = userRepository.findUserRoles(user.getId());
        List<String> userPrivileges = userRepository.findRolesPrivileges(userRoles);

        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String userRole : userRoles) {
            authorities.add(new SimpleGrantedAuthority(String.format("ROLE_%s", userRole)));
        }
        for (String userPrivilege : userPrivileges) {
            authorities.add(new SimpleGrantedAuthority(userPrivilege));
        }

        UserData userData = new UserData();
        userData.setId(user.getId());
        userData.setUsername(user.getUsername());
        userData.setAuthorities(authorities);

        String accessToken = jwtTokenUtil.generateAccessToken(userData);

        SessionEntity session = new SessionEntity();
        session.setId(accessToken);
        session.setUserId(user.getId());
        session.setMeta(Map.of());
        session.setCreatedAt(timeService.now().toInstant());

        sessionService.newSession(session);

        return accessToken;
    }
}
