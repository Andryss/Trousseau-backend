package ru.andryss.trousseau.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import ru.andryss.trousseau.generated.api.AuthApi;
import ru.andryss.trousseau.generated.model.AuthResponse;
import ru.andryss.trousseau.generated.model.SignInRequest;
import ru.andryss.trousseau.generated.model.SignUpRequest;
import ru.andryss.trousseau.service.AuthService;

@RestController
@RequiredArgsConstructor
public class AuthApiController extends BaseApiController implements AuthApi {

    private final AuthService authService;

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
