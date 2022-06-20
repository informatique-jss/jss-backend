package com.jss.jssbackend.libs;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.jss.jssbackend.modules.miscellaneous.model.IId;
import com.jss.jssbackend.modules.miscellaneous.model.Mail;

@Service
public class ValidationHelper {

    private static final Logger logger = LoggerFactory.getLogger(ValidationHelper.class);

    @Autowired
    ApplicationContext ctx;

    public boolean validateFrenchPhone(String phone) {
        String regex = "^(?:(?:\\+|00)33|0)\\s*[1-9](?:[\\s.-]*\\d{2}){4}$";
        Pattern p = Pattern.compile(regex);

        if (phone == null)
            return false;

        Matcher m = p.matcher(phone);
        return m.matches();
    }

    public boolean validateInternationalPhone(String phone) {
        String regex = "^(?:(?:\\+|00)[1-9][1-9]|0)\\s*[1-9](?:[\\s.-]*\\d{2}){4,6}$";
        Pattern p = Pattern.compile(regex);

        if (phone == null)
            return false;

        Matcher m = p.matcher(phone);
        return m.matches();
    }

    public boolean validateSiren(String siren) {
        String regex = "^(\\d{9}|\\d{3}[ ]\\d{3}[ ]\\d{3})$";
        Pattern p = Pattern.compile(regex);

        if (siren == null)
            return false;

        Matcher m = p.matcher(siren);
        return m.matches();
    }

    public boolean validateSiret(String siret) {
        String regex = "^\\d{14}$";
        Pattern p = Pattern.compile(regex);

        if (siret == null)
            return false;

        Matcher m = p.matcher(siret);
        return m.matches();
    }

    public boolean validateRna(String rna) {
        String regex = "^[Ww]\\d{9}$";
        Pattern p = Pattern.compile(regex);

        if (rna == null)
            return false;

        Matcher m = p.matcher(rna);
        return m.matches();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void validateReferential(IId value, Boolean isMandatory) throws Exception {
        if (value == null && isMandatory)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        if (value != null) {
            if (value.getId() == null)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

            String[] valueClass = value.getClass().getName().split("\\.");
            valueClass[valueClass.length - 2] = "service";
            valueClass[valueClass.length - 1] = valueClass[valueClass.length - 1] + "Service";

            Class serviceClass = null;
            try {
                serviceClass = Class.forName(String.join(".", valueClass));
            } catch (ClassNotFoundException e) {
                logger.error("Class not found in ValidationHelper generic check method. Class : "
                        + value.getClass().getName());
                throw e;
            }

            Object service = null;
            try {
                service = ctx.getBean(serviceClass);
            } catch (NoSuchBeanDefinitionException e) {
                logger.error("Bean not found in ValidationHelper generic check method. Bean for "
                        + serviceClass);
                throw e;
            }

            Method m = null;
            try {
                m = serviceClass.getDeclaredMethod("get" + value.getClass().getSimpleName(), Integer.class);
            } catch (NoSuchMethodException e) {
                logger.error("Method not found in ValidationHelper generic check method. Bean : "
                        + serviceClass
                        + ". Method : " + "get" + value.getClass().getSimpleName());
                throw e;
            }

            Object returnValue = m.invoke(service, value.getId());
            if (returnValue == null)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    public void validateString(String value, Boolean isMandatory, Integer maxLength) throws Exception {
        if ((value == null || value.equals("")) && isMandatory)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        if (value != null) {
            if (maxLength != null && value.length() > maxLength)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    public void validateString(String value, Boolean isMandatory) throws Exception {
        validateString(value, isMandatory, null);
    }

    public void validateDate(Date value, Boolean isMandatory) throws Exception {
        validateDateMax(value, isMandatory, null);
    }

    public void validateDateMax(Date value, Boolean isMandatory, Date maxDate) throws Exception {
        if ((value == null) && isMandatory)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        if (value != null) {
            if (maxDate != null && value.after(maxDate))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    public void validateDateMin(Date value, Boolean isMandatory, Date minDate)
            throws Exception {
        if ((value == null) && isMandatory)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        if (value != null) {
            if (minDate != null && value.before(minDate))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    public boolean validateMailList(List<Mail> mails) {
        EmailValidator emailvalidator = EmailValidator.getInstance();
        for (Mail mail : mails) {
            if (mail.getMail() == null || mail.getMail().length() > 30 || !emailvalidator.isValid(mail.getMail()))
                return false;
        }
        return true;
    }
}