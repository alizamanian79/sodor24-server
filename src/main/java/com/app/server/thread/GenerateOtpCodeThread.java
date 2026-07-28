package com.app.server.thread;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.concurrent.Callable;

@Component
@RequiredArgsConstructor
public class GenerateOtpCodeThread implements Callable<String> {

    private final SecureRandom secureRandom = new SecureRandom();

    private int characterNumber = 6;

    public int getCharacterNumber() {
        return characterNumber;
    }

    public void setCharacterNumber(int characterNumber) {
        if (characterNumber <= 0) {
            throw new IllegalArgumentException("Character number must be greater than zero.");
        }
        this.characterNumber = characterNumber;
    }

    @Override
    public String call() {
        return codeGeneration();
    }

    private String codeGeneration() {
        StringBuilder code = new StringBuilder(characterNumber);
        code.append(secureRandom.nextInt(9) + 1);

        for (int i = 1; i < characterNumber; i++) {
            code.append(secureRandom.nextInt(10));
        }

        return code.toString();
    }
}