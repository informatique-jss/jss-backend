package com.jss.osiris.libs;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisDuplicateException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisLog;
import com.jss.osiris.libs.exception.OsirisLogRepository;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.profile.model.Employee;
import com.jss.osiris.modules.profile.service.EmployeeService;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Autowired
    OsirisLogRepository osirisLogRepository;

    @Autowired
    EmployeeService employeeService;

    @Value("${dev.mode}")
    private Boolean devMode;

    @ExceptionHandler({ OsirisValidationException.class, OsirisException.class, Exception.class })
    public ResponseEntity<Object> handleExceptionOsiris(Exception ex) {
        if (ex instanceof OsirisValidationException) {
            return validationOsirisValidationException((OsirisValidationException) ex);
        } else if (ex instanceof OsirisClientMessageException) {
            return validationOsirisClientMessageException((OsirisClientMessageException) ex);
        } else if (ex instanceof OsirisDuplicateException) {
            return validationOsirisDuplicateException((OsirisDuplicateException) ex);
        } else if (ex instanceof OsirisException) {
            return validationOsirisException((OsirisException) ex);
        } else {
            return validationOtherException(ex);
        }
    }

    private ResponseEntity<Object> validationOsirisValidationException(OsirisValidationException exception) {
        List<String> customHeaders = new ArrayList<String>();
        customHeaders.add("incorrectField");
        HttpHeaders header = new HttpHeaders();
        header.setAccessControlExposeHeaders(customHeaders);
        header.set("incorrectField", exception.getMessage());
        return ResponseEntity.badRequest().headers(header).build();
    }

    private ResponseEntity<Object> validationOsirisClientMessageException(OsirisClientMessageException exception) {
        List<String> customHeaders = new ArrayList<String>();
        customHeaders.add("errorMessageToDisplay");
        HttpHeaders header = new HttpHeaders();
        header.setAccessControlExposeHeaders(customHeaders);
        header.set("errorMessageToDisplay", exception.getMessage());
        return ResponseEntity.badRequest().headers(header).build();
    }

    private ResponseEntity<Object> validationOsirisDuplicateException(OsirisDuplicateException exception) {
        List<String> customHeaders = new ArrayList<String>();
        customHeaders.add("duplicateIds");
        HttpHeaders header = new HttpHeaders();
        header.setAccessControlExposeHeaders(customHeaders);
        header.set("duplicateIds", exception.getMessage());
        return ResponseEntity.badRequest().headers(header).build();
    }

    private ResponseEntity<Object> validationOsirisException(OsirisException exception) {
        persistLog(exception, OsirisLog.OSRIS_LOG);
        List<String> customHeaders = new ArrayList<String>();
        customHeaders.add("error");
        HttpHeaders header = new HttpHeaders();
        header.setAccessControlExposeHeaders(customHeaders);
        header.set("error", exception.getMessage());
        return ResponseEntity.internalServerError().headers(header).build();
    }

    private ResponseEntity<Object> validationOtherException(Exception exception) {
        if (exception.getMessage() == null || !exception.getMessage().contains("Relais brisé (pipe)")
                && !exception.getMessage().contains("Connexion ré-initialisée par le correspondant")
                && !exception.getMessage()
                        .contains("Une connexion établie a été abandonnée par un logiciel de votre ordinateur hôte"))
            persistLog(exception, OsirisLog.UNHANDLED_LOG);
        List<String> customHeaders = new ArrayList<String>();
        customHeaders.add("error");
        HttpHeaders header = new HttpHeaders();
        header.setAccessControlExposeHeaders(customHeaders);
        header.set("error", exception.getMessage());
        return ResponseEntity.internalServerError().headers(header).build();
    }

    public OsirisLog persistLog(Exception exception, String logType) {
        OsirisLog osirisLog = null;
        try {
            if (devMode) {
                exception.printStackTrace();
                if (exception instanceof OsirisException && ((OsirisException) exception).getCauseException() != null)
                    ((OsirisException) exception).getCauseException().printStackTrace();
                return null;
            }
            osirisLog = new OsirisLog();
            osirisLog.setClassName(exception.getStackTrace()[0].getFileName().replace(".java", ""));
            osirisLog.setMethodName(exception.getStackTrace()[0].getMethodName());
            osirisLog.setStackTrace(ExceptionUtils.getStackTrace(exception));
            if (exception instanceof OsirisException && ((OsirisException) exception).getCauseException() != null)
                osirisLog.setCauseStackTrace(
                        ExceptionUtils.getStackTrace(((OsirisException) exception).getCauseException()));
            osirisLog.setMessage(exception.getMessage());
            osirisLog.setIsRead(false);
            osirisLog.setLogType(logType);

            Employee employee = employeeService.getCurrentEmployee();
            if (employee != null)
                osirisLog.setCurrentUser(employee);

            osirisLog.setCreatedDateTime(LocalDateTime.now());
            osirisLogRepository.save(osirisLog);
        } catch (Exception e) {
            // Avoid to catch log with Handler here : that will cause infinite recursion ...
            e.printStackTrace();
        }
        return osirisLog;
    }

    @Transactional(rollbackFor = Exception.class)
    public void purgeOsirisLog() {
        osirisLogRepository.deleteAll(osirisLogRepository.findLogsOlderThanMonths(3));
    }

    public List<OsirisLog> getLogs(boolean hideRead) {
        return IterableUtils.toList(osirisLogRepository.findLogs(hideRead));
    }

    public OsirisLog getLog(Integer id) {
        Optional<OsirisLog> log = osirisLogRepository.findById(id);
        if (log.isPresent())
            return log.get();
        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    public OsirisLog addOrUpdateLog(OsirisLog log) {
        return osirisLogRepository.save(log);
    }

    @Transactional(rollbackFor = Exception.class)
    public void purgeLogs() {
        osirisLogRepository.deleteAll(osirisLogRepository.findLogOlderThanMonths(6));
    }

}