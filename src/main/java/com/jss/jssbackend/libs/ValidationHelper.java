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

    public static boolean validateSiren(String siren) {
        String regex = "^(\\d{9}|\\d{3}[ ]\\d{3}[ ]\\d{3})$";
        Pattern p = Pattern.compile(regex);

        if (siren == null)
            return false;

        Matcher m = p.matcher(siren);
        return m.matches();
    }

    public static boolean validateSiret(String siret) {
        String regex = "^\\d{14}$";
        Pattern p = Pattern.compile(regex);

        if (siret == null)
            return false;

        Matcher m = p.matcher(siret);
        return m.matches();
    }

    public static boolean validateRna(String rna) {
        String regex = "^[Ww]\\d{9}$";
        Pattern p = Pattern.compile(regex);

        if (rna == null)
            return false;

        Matcher m = p.matcher(rna);
        return m.matches();
    }

}