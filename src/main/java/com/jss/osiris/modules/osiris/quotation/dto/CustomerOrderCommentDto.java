package com.jss.osiris.modules.osiris.quotation.dto;

import java.time.LocalDateTime;

public class CustomerOrderCommentDto {

    private Integer id;
    private String comment;
    private Integer employee;
    private Integer currentCustomer;
    private Integer customerOrder;
    private LocalDateTime createdDateTime;
    private Boolean isRead;
    private Boolean isToDisplayToCustomer;
    private Boolean isFromChat;
    private Boolean isReadByCustomer;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(LocalDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public Boolean getIsToDisplayToCustomer() {
        return isToDisplayToCustomer;
    }

    public void setIsToDisplayToCustomer(Boolean isToDisplayToCustomer) {
        this.isToDisplayToCustomer = isToDisplayToCustomer;
    }

    public Boolean getIsFromChat() {
        return isFromChat;
    }

    public void setIsFromChat(Boolean isFromChat) {
        this.isFromChat = isFromChat;
    }

    public Boolean getIsReadByCustomer() {
        return isReadByCustomer;
    }

    public void setIsReadByCustomer(Boolean isReadByCustomer) {
        this.isReadByCustomer = isReadByCustomer;
    }

    public Integer getEmployee() {
        return employee;
    }

    public void setEmployee(Integer employee) {
        this.employee = employee;
    }

    public Integer getCurrentCustomer() {
        return currentCustomer;
    }

    public void setCurrentCustomer(Integer currentCustomer) {
        this.currentCustomer = currentCustomer;
    }

    public Integer getCustomerOrder() {
        return customerOrder;
    }

    public void setCustomerOrder(Integer customerOrder) {
        this.customerOrder = customerOrder;
    }

}
