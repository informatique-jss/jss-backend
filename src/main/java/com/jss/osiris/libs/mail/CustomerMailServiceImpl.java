package com.jss.osiris.libs.mail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.batch.model.Batch;
import com.jss.osiris.libs.batch.service.BatchService;
import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.libs.mail.model.CustomerMail;
import com.jss.osiris.libs.mail.repository.CustomerMailRepository;
import com.jss.osiris.modules.osiris.miscellaneous.model.Attachment;
import com.jss.osiris.modules.osiris.miscellaneous.model.CompetentAuthority;
import com.jss.osiris.modules.osiris.miscellaneous.model.Mail;
import com.jss.osiris.modules.osiris.miscellaneous.service.AttachmentService;
import com.jss.osiris.modules.osiris.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.osiris.profile.model.Employee;
import com.jss.osiris.modules.osiris.profile.service.EmployeeService;
import com.jss.osiris.modules.osiris.quotation.model.Confrere;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.quotation.model.Quotation;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;
import com.jss.osiris.modules.osiris.tiers.model.Tiers;

import jakarta.mail.Address;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class CustomerMailServiceImpl implements CustomerMailService {

    @Autowired
    MailHelper mailHelper;

    @Autowired
    CustomerMailRepository customerMailRepository;

    @Autowired
    EmployeeService employeeService;

    @Autowired
    AttachmentService attachmentService;

    @Value("${mail.domain.filter}")
    private String mailDomainFilter;

    @Value("${mail.temporized.temporization.seconds}")
    private Integer temporizedMailDeltaTime;

    @Autowired
    ConstantService constantService;

    @Autowired
    BatchService batchService;

    @Override
    public CustomerMail getMail(Integer id) {
        Optional<CustomerMail> mail = customerMailRepository.findById(id);
        if (mail.isPresent())
            return mail.get();
        return null;
    }

    private CustomerMail addOrUpdateCustomerMail(CustomerMail customerMail) {
        return customerMailRepository.save(customerMail);
    }

    @Override
    public List<CustomerMail> getMailsByQuotation(Quotation quotation) {
        return customerMailRepository.findByQuotation(quotation);
    }

    @Override
    public List<CustomerMail> getMailsByCustomerOrder(CustomerOrder customerOrder) {
        return customerMailRepository.findByCustomerOrder(customerOrder);
    }

    @Override
    public List<CustomerMail> getMailsByConfrere(Confrere confrere) {
        return customerMailRepository.findByConfrere(confrere);
    }

    @Override
    public List<CustomerMail> getMailsByTiers(Tiers tiers) {
        return customerMailRepository.findByTiers(tiers);
    }

    @Override
    public List<CustomerMail> getMailsByResponsable(Responsable responsable) {
        return customerMailRepository.findByResponsable(responsable);
    }

    @Override
    public CustomerMail cancelCustomerMail(CustomerMail customerMail) {
        customerMail.setIsCancelled(true);
        return addOrUpdateCustomerMail(customerMail);
    }

    @Override
    public void addMailToQueue(CustomerMail mail) throws OsirisException {
        mail.setCreatedDateTime(LocalDateTime.now());
        mail.setIsSent(false);

        Employee employee = employeeService.getCurrentEmployee();
        if (employee != null)
            mail.setSendToMeEmployee((Employee) employee);
        addOrUpdateCustomerMail(mail);

        if (mail.getAttachments() != null)
            for (Attachment attachment : mail.getAttachments()) {
                Attachment newAttachment = new Attachment();
                newAttachment.setAttachmentType(attachment.getAttachmentType());
                newAttachment.setCreatDateTime(LocalDateTime.now());
                newAttachment.setCustomerMail(mail);
                newAttachment.setIsDisabled(false);
                newAttachment.setCustomerMail(mail);
                newAttachment.setUploadedFile(attachment.getUploadedFile());
                newAttachment.setDescription(attachment.getDescription());
                newAttachment.setParentAttachment(attachment);
                attachmentService.addOrUpdateAttachment(newAttachment);
            }

        mail = getMail(mail.getId());

        if (mail.getCustomerOrder() != null && (mail.getMailTemplate().equals(CustomerMail.TEMPLATE_WAITING_DEPOSIT)
                || mail.getMailTemplate().equals(CustomerMail.TEMPLATE_CUSTOMER_ORDER_IN_PROGRESS)
                || mail.getMailTemplate().equals(CustomerMail.TEMPLATE_CUSTOMER_ORDER_FINALIZATION)
                || mail.getMailTemplate().equals(CustomerMail.TEMPLATE_SEND_CREDIT_NOTE)
                || mail.getMailTemplate().equals(CustomerMail.TEMPLATE_SEND_PUBLICATION_FLAG)
                || mail.getMailTemplate().equals(CustomerMail.TEMPLATE_SEND_PUBLICATION_RECEIPT)
                || mail.getMailTemplate().equals(CustomerMail.TEMPLATE_SEND_ATTACHMENTS)))
            manageTemporization(mail);
        else
            batchService.declareNewBatch(Batch.SEND_MAIL, mail.getId());
    }

    private synchronized void manageTemporization(CustomerMail mail) { // Synchronize to avoid mails not seen because of
                                                                       // parallel transactions...
        List<CustomerMail> customerOrderMails = customerMailRepository
                .findTemporizedMailsForCustomerOrder(mail.getCustomerOrder());
        LocalDateTime previousSendDateTime = LocalDateTime.now().plusSeconds(temporizedMailDeltaTime);
        mail.setIsCancelled(false);
        mail.setToSendAfter(previousSendDateTime); // if previous mail steal its date else now + tempo

        if (customerOrderMails != null && customerOrderMails.size() > 0) {
            // Order in progress then finalized => keep only finalized
            if (mail.getMailTemplate().equals(CustomerMail.TEMPLATE_CUSTOMER_ORDER_FINALIZATION)) {
                for (CustomerMail existingMail : customerOrderMails) {
                    if (existingMail.getMailTemplate().equals(CustomerMail.TEMPLATE_CUSTOMER_ORDER_IN_PROGRESS)) {
                        existingMail.setIsCancelled(true);
                        addOrUpdateCustomerMail(existingMail);
                        if (previousSendDateTime.isAfter(existingMail.getToSendAfter()))
                            previousSendDateTime = existingMail.getToSendAfter();
                    }
                }
            }

            // Order finalization then credit note => keep credit note and steal attachments
            if (mail.getMailTemplate().equals(CustomerMail.TEMPLATE_SEND_CREDIT_NOTE)) {
                for (CustomerMail existingMail : customerOrderMails) {
                    if (existingMail.getMailTemplate().equals(CustomerMail.TEMPLATE_CUSTOMER_ORDER_FINALIZATION)) {
                        existingMail.setIsCancelled(true);
                        addOrUpdateCustomerMail(existingMail);

                        if (existingMail.getAttachments() != null)
                            for (Attachment attachment : existingMail.getAttachments()) {
                                attachment.setCustomerMail(mail);
                                attachmentService.addOrUpdateAttachment(attachment);
                            }
                        if (previousSendDateTime.isAfter(existingMail.getToSendAfter()))
                            previousSendDateTime = existingMail.getToSendAfter();
                    }
                }
            }

            // Credit note then Order finalization => keep order finalization and steal
            // attachments
            if (mail.getMailTemplate().equals(CustomerMail.TEMPLATE_CUSTOMER_ORDER_FINALIZATION)) {
                for (CustomerMail existingMail : customerOrderMails) {
                    if (existingMail.getMailTemplate().equals(CustomerMail.TEMPLATE_SEND_CREDIT_NOTE)) {
                        existingMail.setIsCancelled(true);
                        addOrUpdateCustomerMail(existingMail);

                        if (existingMail.getAttachments() != null)
                            for (Attachment attachment : existingMail.getAttachments()) {
                                attachment.setCustomerMail(mail);
                                attachmentService.addOrUpdateAttachment(attachment);
                            }
                        if (previousSendDateTime.isAfter(existingMail.getToSendAfter()))
                            previousSendDateTime = existingMail.getToSendAfter();
                    }
                }
            }

            // Send Order finalization then attachment => keep order finalization and steal
            // attachments
            if (mail.getMailTemplate().equals(CustomerMail.TEMPLATE_SEND_ATTACHMENTS)) {
                for (CustomerMail existingMail : customerOrderMails) {
                    if (existingMail.getMailTemplate().equals(CustomerMail.TEMPLATE_CUSTOMER_ORDER_FINALIZATION)) {
                        if (sameConsignee(existingMail, mail)) {
                            mail.setIsCancelled(true);
                            addOrUpdateCustomerMail(mail);

                            if (mail.getAttachments() != null)
                                for (Attachment attachment : mail.getAttachments()) {
                                    attachment.setCustomerMail(existingMail);
                                    attachmentService.addOrUpdateAttachment(attachment);
                                }
                            if (previousSendDateTime.isAfter(existingMail.getToSendAfter()))
                                previousSendDateTime = existingMail.getToSendAfter();
                        }
                    }
                }
            }

            // Send order finalization then attachments => keep order finalization and steal
            // attachments
            if (mail.getMailTemplate().equals(CustomerMail.TEMPLATE_CUSTOMER_ORDER_FINALIZATION)) {
                for (CustomerMail existingMail : customerOrderMails) {
                    if (existingMail.getMailTemplate().equals(CustomerMail.TEMPLATE_SEND_ATTACHMENTS)) {
                        if (sameConsignee(existingMail, mail)) {
                            existingMail.setIsCancelled(true);
                            addOrUpdateCustomerMail(existingMail);

                            if (existingMail.getAttachments() != null)
                                for (Attachment attachment : existingMail.getAttachments()) {
                                    attachment.setCustomerMail(mail);
                                    attachmentService.addOrUpdateAttachment(attachment);
                                }
                            if (previousSendDateTime.isAfter(existingMail.getToSendAfter()))
                                previousSendDateTime = existingMail.getToSendAfter();
                        }
                    }
                }
            }

            // Publication receipt and / or publication flag then Publication receipt and /
            // or publication flag => keep publication receipt and
            // steal attachments
            if (mail.getMailTemplate().equals(CustomerMail.TEMPLATE_SEND_PUBLICATION_RECEIPT)
                    || mail.getMailTemplate().equals(CustomerMail.TEMPLATE_SEND_PUBLICATION_FLAG)) {
                for (CustomerMail existingMail : customerOrderMails) {
                    if (existingMail.getMailTemplate().equals(CustomerMail.TEMPLATE_SEND_PUBLICATION_FLAG)
                            || existingMail.getMailTemplate().equals(CustomerMail.TEMPLATE_SEND_PUBLICATION_RECEIPT)) {
                        existingMail.setIsCancelled(true);
                        addOrUpdateCustomerMail(existingMail);

                        if (existingMail.getAttachments() != null)
                            for (Attachment attachment : existingMail.getAttachments()) {
                                attachment.setCustomerMail(mail);
                                attachmentService.addOrUpdateAttachment(attachment);
                            }
                        if (previousSendDateTime.isAfter(existingMail.getToSendAfter()))
                            previousSendDateTime = existingMail.getToSendAfter();
                    }
                }
            }

            // Publication receipt and / or publication flag then order finalization => keep
            // order finalization and steal
            // attachments

            if (mail.getMailTemplate().equals(CustomerMail.TEMPLATE_CUSTOMER_ORDER_FINALIZATION)) {
                for (CustomerMail existingMail : customerOrderMails) {
                    if (existingMail.getMailTemplate().equals(CustomerMail.TEMPLATE_SEND_PUBLICATION_RECEIPT)
                            || existingMail.getMailTemplate().equals(CustomerMail.TEMPLATE_SEND_PUBLICATION_FLAG)) {
                        if (sameConsignee(existingMail, mail)) {
                            existingMail.setIsCancelled(true);
                            addOrUpdateCustomerMail(existingMail);

                            if (existingMail.getAttachments() != null)
                                for (Attachment attachment : existingMail.getAttachments()) {
                                    attachment.setCustomerMail(mail);
                                    attachmentService.addOrUpdateAttachment(attachment);
                                }
                            if (previousSendDateTime.isAfter(existingMail.getToSendAfter()))
                                previousSendDateTime = existingMail.getToSendAfter();
                        }
                    }
                }
            }

            // order finalization then Publication receipt and / or publication flag => keep
            // order finalization and steal
            // attachments

            if (mail.getMailTemplate().equals(CustomerMail.TEMPLATE_SEND_PUBLICATION_RECEIPT)
                    || mail.getMailTemplate().equals(CustomerMail.TEMPLATE_SEND_PUBLICATION_FLAG)) {
                for (CustomerMail existingMail : customerOrderMails) {
                    if (existingMail.getMailTemplate().equals(CustomerMail.TEMPLATE_CUSTOMER_ORDER_FINALIZATION)) {
                        if (sameConsignee(existingMail, mail)) {
                            mail.setIsCancelled(true);
                            addOrUpdateCustomerMail(mail);

                            if (mail.getAttachments() != null)
                                for (Attachment attachment : mail.getAttachments()) {
                                    attachment.setCustomerMail(existingMail);
                                    attachmentService.addOrUpdateAttachment(attachment);
                                }
                            if (previousSendDateTime.isAfter(existingMail.getToSendAfter()))
                                previousSendDateTime = existingMail.getToSendAfter();
                        }
                    }
                }
            }

            // Multiple send attachments => keep last one and steal attachments
            if (mail.getMailTemplate().equals(CustomerMail.TEMPLATE_SEND_ATTACHMENTS)) {
                for (CustomerMail existingMail : customerOrderMails) {
                    if (existingMail.getMailTemplate().equals(CustomerMail.TEMPLATE_SEND_ATTACHMENTS)) {
                        existingMail.setIsCancelled(true);
                        addOrUpdateCustomerMail(existingMail);

                        if (existingMail.getAttachments() != null)
                            for (Attachment attachment : existingMail.getAttachments()) {
                                attachment.setCustomerMail(mail);
                                attachmentService.addOrUpdateAttachment(attachment);
                            }
                        if (previousSendDateTime.isAfter(existingMail.getToSendAfter()))
                            previousSendDateTime = existingMail.getToSendAfter();
                    }
                }
            }
        }
    }

    private boolean sameConsignee(CustomerMail mail1, CustomerMail mail2) {
        Set<String> mail1List = new HashSet<String>();
        Set<String> mail2List = new HashSet<String>();
        if (mail1.getMailComputeResult() != null) {
            if (mail1.getMailComputeResult().getRecipientsMailCc() != null)
                for (Mail mail : mail1.getMailComputeResult().getRecipientsMailCc())
                    mail1List.add(mail.getMail());
            if (mail1.getMailComputeResult().getRecipientsMailTo() != null)
                for (Mail mail : mail1.getMailComputeResult().getRecipientsMailTo())
                    mail1List.add(mail.getMail());
        }
        if (mail2.getMailComputeResult() != null) {
            if (mail2.getMailComputeResult().getRecipientsMailCc() != null)
                for (Mail mail : mail2.getMailComputeResult().getRecipientsMailCc())
                    mail2List.add(mail.getMail());
            if (mail2.getMailComputeResult().getRecipientsMailTo() != null)
                for (Mail mail : mail2.getMailComputeResult().getRecipientsMailTo())
                    mail2List.add(mail.getMail());
        }
        return mail1List.containsAll(mail2List);
    }

    @Override
    public void sendTemporizedMails() throws OsirisException {
        List<CustomerMail> mails = customerMailRepository.findTemporizesMailsToSend(LocalDateTime.now());
        if (mails != null && mails.size() > 0)
            for (CustomerMail mail : mails)
                batchService.declareNewBatch(Batch.SEND_MAIL, mail.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendCustomerMailImmediatly(CustomerMail mail) throws OsirisException {
        mail = getMail(mail.getId());
        if (mail.getIsSent() == false && mail.getIsCancelled() == false)
            batchService.declareNewBatch(Batch.SEND_MAIL, mail.getId());
    }

    @Override
    public void sendMail(CustomerMail mail)
            throws OsirisException, OsirisValidationException, OsirisClientMessageException {
        if (mail != null && mail.getIsSent() == false) {
            prepareAndSendMail(mail);

            if (!mail.getSendToMe()) {
                File mailPdf = null;
                try {
                    List<Attachment> attachments = null;
                    mailPdf = mailHelper.generateGenericPdfOfMail(mail);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HHmm");
                    if (mail.getCustomerOrder() != null)
                        attachments = attachmentService.addAttachment(new FileInputStream(mailPdf),
                                mail.getCustomerOrder().getId(), null,
                                CustomerOrder.class.getSimpleName(), constantService.getAttachmentTypeAutomaticMail(),
                                "Customer_mail_" + formatter.format(LocalDateTime.now()) + ".pdf", false,
                                "Mail automatique n°" + mail.getId(), null, null, null);
                    if (mail.getQuotation() != null)
                        attachments = attachmentService.addAttachment(new FileInputStream(mailPdf),
                                mail.getQuotation().getId(), null,
                                Quotation.class.getSimpleName(), constantService.getAttachmentTypeAutomaticMail(),
                                "Customer_mail_" + formatter.format(LocalDateTime.now()) + ".pdf", false,
                                "Mail automatique n°" + mail.getId(), null, null, null);
                    if (mail.getTiers() != null)
                        attachments = attachmentService.addAttachment(new FileInputStream(mailPdf),
                                mail.getTiers().getId(), null,
                                Tiers.class.getSimpleName(), constantService.getAttachmentTypeAutomaticMail(),
                                "Customer_mail_" + formatter.format(LocalDateTime.now()) + ".pdf", false,
                                "Mail automatique n°" + mail.getId(), null, null, null);
                    if (mail.getResponsable() != null)
                        attachments = attachmentService.addAttachment(new FileInputStream(mailPdf),
                                mail.getResponsable().getId(), null,
                                Responsable.class.getSimpleName(), constantService.getAttachmentTypeAutomaticMail(),
                                "Customer_mail_" + formatter.format(LocalDateTime.now()) + ".pdf", false,
                                "Mail automatique n°" + mail.getId(), null, null, null);
                    if (mail.getConfrere() != null)
                        attachments = attachmentService.addAttachment(new FileInputStream(mailPdf),
                                mail.getConfrere().getId(), null,
                                Confrere.class.getSimpleName(), constantService.getAttachmentTypeAutomaticMail(),
                                "Customer_mail_" + formatter.format(LocalDateTime.now()) + ".pdf", false,
                                "Mail automatique n°" + mail.getId(), null, null, null);
                    if (mail.getCompetentAuthority() != null)
                        attachments = attachmentService.addAttachment(new FileInputStream(mailPdf),
                                mail.getCompetentAuthority().getId(), null,
                                CompetentAuthority.class.getSimpleName(),
                                constantService.getAttachmentTypeAutomaticMail(),
                                "Customer_mail_" + formatter.format(LocalDateTime.now()) + ".pdf", false,
                                "Mail automatique n°" + mail.getId(), null, null, null);

                    if (mail.getAttachments() == null)
                        mail.setAttachments(new ArrayList<Attachment>());

                    if (attachments != null)
                        for (Attachment attachment : attachments)
                            if (attachment.getAttachmentType().getId()
                                    .equals(constantService.getAttachmentTypeAutomaticMail().getId())
                                    && attachment.getCustomerMail() == null) {
                                mail.getAttachments().add(attachment);
                                attachment.setCustomerMail(mail);
                                attachmentService.addOrUpdateAttachment(attachment);
                            }
                } catch (FileNotFoundException e) {
                    mail.setIsCancelled(true);
                    addOrUpdateCustomerMail(mail);
                    throw new OsirisException(e, "Impossible to read invoice PDF temp file");
                } catch (Exception e) {
                    mail.setIsCancelled(true);
                    addOrUpdateCustomerMail(mail);
                    throw new OsirisException(e, "Impossible to generate mail PDF for mail " + mail.getId());
                } finally {
                    if (mailPdf != null)
                        mailPdf.delete();
                    addOrUpdateCustomerMail(mail);
                }
            }

            mail.setIsSent(true);
            addOrUpdateCustomerMail(mail);
        }
    }

    private void prepareAndSendMail(CustomerMail mail)
            throws OsirisException, OsirisValidationException, OsirisClientMessageException {
        boolean canSend = true;
        MimeMessage message = mailHelper.generateGenericMail(mail);

        Address[] recipients;
        try {
            recipients = message.getRecipients(Message.RecipientType.TO);
        } catch (MessagingException e) {
            throw new OsirisException(e, "Unable to find recipients To for mail " + mail.getId());
        }
        if (recipients != null && mailDomainFilter != null && !mailDomainFilter.equals(""))
            for (Address address : recipients) {
                String[] chunk = address.toString().split("@");
                if (chunk.length != 2 || !chunk[1].toLowerCase().trim().equals(mailDomainFilter.toLowerCase().trim()))
                    canSend = false;
            }
        try {
            recipients = message.getRecipients(Message.RecipientType.CC);
        } catch (MessagingException e) {
            throw new OsirisException(e, "Unable to find recipients Cc for mail " + mail.getId());
        }
        if (recipients != null && mailDomainFilter != null && !mailDomainFilter.equals(""))
            for (Address address : recipients) {
                String[] chunk = address.toString().split("@");
                if (chunk.length != 2
                        || !chunk[1].toLowerCase().trim().equals(mailDomainFilter.toLowerCase().trim()))
                    canSend = false;
            }

        if (canSend)
            mailHelper.getMailSender().send(message);
    }

}
