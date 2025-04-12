package ru.andryss.trousseau.service;

import ru.andryss.trousseau.generated.model.SignInRequest;
import ru.andryss.trousseau.generated.model.SignUpRequest;

public interface AuthService {
    String signUp(SignUpRequest request);
    String signIn(SignInRequest request);
}
