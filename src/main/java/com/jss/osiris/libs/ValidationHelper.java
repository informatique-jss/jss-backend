package com.jss.osiris.libs;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.validator.routines.EmailValidator;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.osiris.miscellaneous.model.ICode;
import com.jss.osiris.modules.osiris.miscellaneous.model.IId;
import com.jss.osiris.modules.osiris.miscellaneous.model.Mail;

import fr.marcwrobel.jbanking.bic.Bic;
import fr.marcwrobel.jbanking.iban.Iban;

@Service
public class ValidationHelper {

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

    /**
     * Check that the input value matches the expected class
     * 
     * @param value
     * @param isMandatory
     * @param className
     * @return
     * @throws OsirisValidationException
     * @throws OsirisException
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Object validateReferential(IId value, Boolean isMandatory, String className)
            throws OsirisValidationException, OsirisException {
        if (className == null || className.equals(""))
            throw new OsirisValidationException("no class name defined !");
        if (className.startsWith("get"))
            className = className.substring((3));
        if (value == null && isMandatory)
            throw new OsirisValidationException(className);
        if (value != null) {
            if (value.getClass().getSimpleName().contains("HibernateProxy"))
                value = (IId) Hibernate.unproxy(value);

            if (value.getId() == null)
                throw new OsirisValidationException(className);

            String[] valueClass = value.getClass().getName().split("\\.");
            valueClass[valueClass.length - 2] = "service";
            valueClass[valueClass.length - 1] = valueClass[valueClass.length - 1] + "Service";

            Class serviceClass = null;
            try {
                serviceClass = Class.forName(String.join(".", valueClass));
            } catch (ClassNotFoundException e) {
                throw new OsirisException(e, "Class not found in ValidationHelper generic check method. Class : "
                        + value.getClass().getName());
            }

            Object service = null;
            try {
                service = ctx.getBean(serviceClass);
            } catch (NoSuchBeanDefinitionException e) {
                throw new OsirisException(e, "Bean not found in ValidationHelper generic check method. Bean for "
                        + serviceClass);
            }

            Method m = null;
            try {
                m = serviceClass.getDeclaredMethod("get" + value.getClass().getSimpleName(), Integer.class);
            } catch (NoSuchMethodException e) {
                throw new OsirisException(e, "Method not found in ValidationHelper generic check method. Bean : "
                        + serviceClass
                        + ". Method : " + "get" + value.getClass().getSimpleName());
            }

            Object returnValue;
            try {
                returnValue = m.invoke(service, value.getId());
            } catch (IllegalAccessException e) {
                throw new OsirisException(e,
                        "Illegal access to methode in ValidationHelper generic check method. Bean : "
                                + serviceClass
                                + ". Method : " + "get" + value.getClass().getSimpleName());
            } catch (InvocationTargetException e) {
                throw new OsirisException(e,
                        "Invocation target error to methode in ValidationHelper generic check method. Bean : "
                                + serviceClass
                                + ". Method : " + "get" + value.getClass().getSimpleName() + ". Value id : "
                                + value.getId());
            }
            if (returnValue == null)
                throw new OsirisValidationException(className);
            return returnValue;
        }
        return null;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Object validateReferential(ICode value, Boolean isMandatory, String className)
            throws OsirisValidationException, OsirisException {
        if (value == null && isMandatory)
            throw new OsirisValidationException(className);
        if (value != null) {
            if (value.getCode() == null)
                throw new OsirisValidationException(className);

            String[] valueClassTmp = value.getClass().getName().split("\\.");
            ArrayList<String> valueClass = new ArrayList<String>();

            for (String val : valueClassTmp) {
                if (val.equals("model"))
                    valueClass.add("service");
                else
                    valueClass.add(val);
            }

            valueClass.set(valueClass.size() - 1, valueClass.get(valueClass.size() - 1) + "Service");

            Class serviceClass = null;
            try {
                serviceClass = Class.forName(String.join(".", valueClass));
            } catch (ClassNotFoundException e) {
                throw new OsirisException(e, "Class not found in ValidationHelper generic check method. Class : "
                        + value.getClass().getName());
            }

            Object service = null;
            try {
                service = ctx.getBean(serviceClass);
            } catch (NoSuchBeanDefinitionException e) {
                throw new OsirisException(e, "Bean not found in ValidationHelper generic check method. Bean for "
                        + serviceClass);
            }

            Method m = null;
            try {
                m = serviceClass.getDeclaredMethod("get" + value.getClass().getSimpleName());
            } catch (NoSuchMethodException e) {
                throw new OsirisException(e, "Method not found in ValidationHelper generic check method. Bean : "
                        + serviceClass
                        + ". Method : " + "get" + value.getClass().getSimpleName());
            }

            List<ICode> returnValue = null;
            try {
                returnValue = (List<ICode>) m.invoke(service);
            } catch (IllegalAccessException e) {
                throw new OsirisException(e,
                        "Illegal access to methode in ValidationHelper generic check method. Bean : "
                                + serviceClass
                                + ". Method : " + "get" + value.getClass().getSimpleName());
            } catch (InvocationTargetException e) {
                throw new OsirisException(e,
                        "Invocation target error to methode in ValidationHelper generic check method. Bean : "
                                + serviceClass
                                + ". Method : " + "get" + value.getClass().getSimpleName());
            }

            if (returnValue == null)
                throw new OsirisValidationException(className);

            for (ICode entity : returnValue) {
                if (entity.getCode().equals(value.getCode()))
                    return entity;
            }
        }
        return null;
    }

    public void validateString(String value, Boolean isMandatory, Integer maxLength, String fieldName)
            throws OsirisValidationException {
        if ((value == null || value.equals("")) && isMandatory)
            throw new OsirisValidationException(fieldName);
        if (value != null) {
            if (maxLength != null && value.length() > maxLength)
                throw new OsirisValidationException(fieldName);
        }
    }

    public void validateInteger(Integer value, Boolean isMandatory, String fieldName) throws OsirisValidationException {
        if ((value == null) && isMandatory)
            throw new OsirisValidationException(fieldName);
    }

    public void validateIban(String value, Boolean isMandatory, String fieldName)
            throws OsirisValidationException, OsirisClientMessageException {
        if ((value == null) && isMandatory)
            throw new OsirisValidationException(fieldName);

        if (!isMandatory && (value == null || value.trim().equals("")))
            return;

        if (value != null && !Iban.isValid(value.replaceAll(" ", "")))
            throw new OsirisClientMessageException("IBAN saisi non valide");
    }

    public void validateBic(String value, Boolean isMandatory, String fieldName)
            throws OsirisValidationException, OsirisClientMessageException {
        if ((value == null) && isMandatory)
            throw new OsirisValidationException(fieldName);

        if (!isMandatory && (value == null || value.trim().equals("")))
            return;

        if (value != null && !Bic.isValid(value.replaceAll(" ", "")))
            throw new OsirisClientMessageException("BIC saisi non valide");
    }

    public void validateFloat(Float value, Boolean isMandatory, String fieldName) throws OsirisValidationException {
        if ((value == null) && isMandatory)
            throw new OsirisValidationException(fieldName);
    }

    public void validateString(String value, Boolean isMandatory, String fieldName) throws OsirisValidationException {
        validateString(value, isMandatory, null, fieldName);
    }

    public void validateDate(LocalDate value, Boolean isMandatory, String fieldName) throws OsirisValidationException {
        validateDateMax(value, isMandatory, null, fieldName);
    }

    public void validateDateMax(LocalDate value, Boolean isMandatory, LocalDate maxDate, String fieldName)
            throws OsirisValidationException {
        if ((value == null) && isMandatory)
            throw new OsirisValidationException(fieldName);
        if (value != null && maxDate != null) {
            if (maxDate != null && value.isAfter(maxDate))
                throw new OsirisValidationException(fieldName);
        }
    }

    public void validateDateMin(LocalDate value, Boolean isMandatory, LocalDate minDate, String fieldName)
            throws OsirisValidationException {
        if ((value == null) && isMandatory)
            throw new OsirisValidationException(fieldName);
        if (value != null && minDate != null) {
            if (minDate != null && value.isBefore(minDate))
                throw new OsirisValidationException(fieldName);
        }
    }

    public void validateDateTime(LocalDateTime value, Boolean isMandatory, String fieldName)
            throws OsirisValidationException {
        validateDateTimeMax(value, isMandatory, null, fieldName);
    }

    public void validateDateTimeMax(LocalDateTime value, Boolean isMandatory, LocalDateTime maxDate, String fieldName)
            throws OsirisValidationException {
        if ((value == null) && isMandatory)
            throw new OsirisValidationException(fieldName);
        if (value != null) {
            if (maxDate != null && value.isAfter(maxDate))
                throw new OsirisValidationException(fieldName);
        }
    }

    public void validateDateTimeMin(LocalDateTime value, Boolean isMandatory, LocalDateTime minDate, String fieldName)
            throws OsirisValidationException {
        if ((value == null) && isMandatory)
            throw new OsirisValidationException(fieldName);
        if (value != null) {
            if (minDate != null && value.isBefore(minDate))
                throw new OsirisValidationException(fieldName);
        }
    }

    public boolean validateMailList(List<Mail> mails) {
        for (Mail mail : mails) {
            if (!validateMail(mail.getMail()))
                return false;
        }
        return true;
    }

    public boolean validateMail(Mail mail) {
        if (!validateMail(mail.getMail()))
            return false;
        return true;
    }

    public boolean validateMail(String mail) {
        EmailValidator emailvalidator = EmailValidator.getInstance(true);
        if (mail != null) {
            String[] mailSplit = mail.toLowerCase().split("\\.");
            if (mailSplit[mailSplit.length - 1].equals("notaires"))
                return true;
        }
        if (mail == null || mail.length() > 250 || !emailvalidator.isValid(mail))
            return false;
        if (mail.toLowerCase().contains("é") || mail.toLowerCase().contains("è")
                || mail.toLowerCase().contains("ê"))
            return false;
        return true;
    }
}