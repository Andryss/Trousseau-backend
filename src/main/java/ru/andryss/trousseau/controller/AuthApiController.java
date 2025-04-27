package ru.andryss.trousseau.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import ru.andryss.trousseau.generated.api.AuthApi;
import ru.andryss.trousseau.generated.model.AuthResponse;
import ru.andryss.trousseau.generated.model.ProfileDto;
import ru.andryss.trousseau.generated.model.SignInRequest;
import ru.andryss.trousseau.generated.model.SignUpRequest;
import ru.andryss.trousseau.model.UserEntity;
import ru.andryss.trousseau.security.UserData;
import ru.andryss.trousseau.service.AuthService;
import ru.andryss.trousseau.service.UserService;

@RestController
@RequiredArgsConstructor
public class AuthApiController extends BaseApiController implements AuthApi {

    private final AuthService authService;
    private final UserService userService;

    @Override
    public ProfileDto getProfileInfo() {
        UserData user = getUser();
        UserEntity userInfo = userService.findByIdOrThrow(user.getId());

        return new ProfileDto()
                .username(userInfo.getUsername())
                .contacts(userInfo.getContacts())
                .room(userInfo.getRoom())
                .privileges(user.getPrivileges());
    }

    @Override
    public AuthResponse signIn(SignInRequest request) {
        String token = authService.signIn(request);

        return new AuthResponse()
                .token(token);
    }

    @Override
    public void signOut() {
        String session = getSession();
        if (session == null) {
            return;
        }
        authService.signOut(session);
    }

    @Override
    public AuthResponse signUp(SignUpRequest request) {
        String token = authService.signUp(request);

        return new AuthResponse()
                .token(token);
    }
}
