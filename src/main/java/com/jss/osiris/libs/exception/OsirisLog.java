package com.jss.osiris.libs.exception;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.jss.osiris.modules.miscellaneous.model.IId;
import com.jss.osiris.modules.profile.model.Employee;

@Entity
@Table(indexes = { @Index(name = "idx_osiris_log_read", columnList = "is_read") })
public class OsirisLog implements Serializable, IId {

    public static String OSRIS_LOG = "OSRIS_LOG";
    public static String UNHANDLED_LOG = "UNHANDLED_LOG";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "osiris_log_sequence")
    private Integer id;

    @Column(nullable = false, length = 200)
    private String className;

    @Column(nullable = false, length = 300)
    private String methodName;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(columnDefinition = "TEXT")
    private String stackTrace;

    @Column(columnDefinition = "TEXT")
    private String causeStackTrace;

    @ManyToOne
    @JoinColumn(name = "id_employee")
    private Employee currentUser;

    private LocalDateTime createdDateTime;

    private String logType;

    @Column(name = "is_read")
    private Boolean isRead;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    public Employee getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(Employee currentUser) {
        this.currentUser = currentUser;
    }

    public LocalDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(LocalDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public String getLogType() {
        return logType;
    }

    public void setLogType(String logType) {
        this.logType = logType;
    }

    public static String getOSRIS_LOG() {
        return OSRIS_LOG;
    }

    public static void setOSRIS_LOG(String oSRIS_LOG) {
        OSRIS_LOG = oSRIS_LOG;
    }

    public static String getUNHANDLED_LOG() {
        return UNHANDLED_LOG;
    }

    public static void setUNHANDLED_LOG(String uNHANDLED_LOG) {
        UNHANDLED_LOG = uNHANDLED_LOG;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCauseStackTrace() {
        return causeStackTrace;
    }

    public void setCauseStackTrace(String causeStackTrace) {
        this.causeStackTrace = causeStackTrace;
    }

}
