package com.jss.jssbackend.libs;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationHelper {
    public static boolean validateFrenchPhone(String phone) {
        String regex = "^(?:(?:\\+|00)33|0)\\s*[1-9](?:[\\s.-]*\\d{2}){4}$";
        Pattern p = Pattern.compile(regex);

        if (phone == null)
            return false;

        Matcher m = p.matcher(phone);
        return m.matches();
    }

    public static boolean validateInternationalPhone(String phone) {
        String regex = "^(?:(?:\\+|00)[1-9][1-9]|0)\\s*[1-9](?:[\\s.-]*\\d{2}){4,6}$";
        Pattern p = Pattern.compile(regex);

        if (phone == null)
            return false;

        Matcher m = p.matcher(phone);
        return m.matches();
    }
}