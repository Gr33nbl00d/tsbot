package de.greenblood.tsbot.restservice;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class RandomPasswordGenerator {

    private SecureRandom random = new SecureRandom();

    private final String ALPHA_CAPS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private final String ALPHA = "abcdefghijklmnopqrstuvwxyz";
    private final String NUMERIC = "0123456789";
    private final String SPECIAL_CHARS = "!@#$";

    public String generatePassword(int length) {
        return generatePassword(length, ALPHA_CAPS + ALPHA + SPECIAL_CHARS + NUMERIC);
    }

    private String generatePassword(int len, String dic) {
        String result = "";
        for (int i = 0; i < len; i++) {
            int index = random.nextInt(dic.length());
            result += dic.charAt(index);
        }
        return result;
    }

}
