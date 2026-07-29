package com.app.server.service.impliment;
import org.springframework.stereotype.Component;
import java.security.SecureRandom;

@Component
public class RandomCodeGenerator {

    private final SecureRandom secureRandom = new SecureRandom();

    public String generate(int characterNumber) {
        StringBuilder code = new StringBuilder(characterNumber);

        code.append(secureRandom.nextInt(9) + 1);

        for (int i = 1; i < characterNumber; i++) {
            code.append(secureRandom.nextInt(10));
        }

        return code.toString();
    }
}