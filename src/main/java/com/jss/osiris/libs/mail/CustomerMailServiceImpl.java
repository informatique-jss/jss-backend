package com.jss.osiris.libs.mail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.mail.model.CustomerMail;
import com.jss.osiris.libs.mail.repository.CustomerMailRepository;
import com.jss.osiris.modules.miscellaneous.model.Attachment;
import com.jss.osiris.modules.miscellaneous.service.AttachmentService;
import com.jss.osiris.modules.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.profile.service.EmployeeService;
import com.jss.osiris.modules.quotation.model.Confrere;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.quotation.model.Quotation;
import com.jss.osiris.modules.tiers.model.Responsable;
import com.jss.osiris.modules.tiers.model.Tiers;

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

    @Autowired
    ConstantService constantService;

    @Override
    public CustomerMail getMail(Integer id) {
        Optional<CustomerMail> mail = customerMailRepository.findById(id);
        if (mail.isPresent())
            return mail.get();
        return null;
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
    public void addMailToQueue(CustomerMail mail) {
        mail.setCreatedDateTime(LocalDateTime.now());
        mail.setHasErrors(false);
        mail.setIsSent(false);
        mail.setSendToMeEmployee(employeeService.getCurrentEmployee());
        customerMailRepository.save(mail);

        if (mail.getAttachments() != null)
            for (Attachment attachment : mail.getAttachments()) {
                attachment.setCustomerMail(mail);
                attachmentService.addOrUpdateAttachment(attachment);
            }
    }

    @Transactional
    @Override
    public void sendNextMail() throws OsirisException {
        List<CustomerMail> mails = customerMailRepository.findAllByOrderByCreatedDateTimeAsc();

        if (mails != null && mails.size() > 0) {
            CustomerMail mail = mails.get(0);

            try {
                prepareAndSendMail(mail);
            } catch (Exception e) {
                mail.setHasErrors(true);
                customerMailRepository.save(mail);
                if (e instanceof OsirisException)
                    throw e;
                throw new OsirisException(e, "Error when generating mail n°" + mail.getId());
            }

            if (!mail.getSendToMe()) {
                File mailPdf = null;
                try {
                    List<Attachment> attachments = null;
                    mailPdf = mailHelper.generateGenericPdf(mail);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HHmm");
                    if (mail.getCustomerOrder() != null)
                        attachments = attachmentService.addAttachment(new FileInputStream(mailPdf),
                                mail.getCustomerOrder().getId(),
                                CustomerOrder.class.getSimpleName(), constantService.getAttachmentTypeAutomaticMail(),
                                "Customer_mail_" + formatter.format(LocalDateTime.now()) + ".pdf", false,
                                "Mail automatique n°" + mail.getId());
                    if (mail.getQuotation() != null)
                        attachments = attachmentService.addAttachment(new FileInputStream(mailPdf),
                                mail.getQuotation().getId(),
                                Quotation.class.getSimpleName(), constantService.getAttachmentTypeAutomaticMail(),
                                "Customer_mail_" + formatter.format(LocalDateTime.now()) + ".pdf", false,
                                "Mail automatique n°" + mail.getId());
                    if (mail.getTiers() != null)
                        attachments = attachmentService.addAttachment(new FileInputStream(mailPdf),
                                mail.getTiers().getId(),
                                Tiers.class.getSimpleName(), constantService.getAttachmentTypeAutomaticMail(),
                                "Customer_mail_" + formatter.format(LocalDateTime.now()) + ".pdf", false,
                                "Mail automatique n°" + mail.getId());
                    if (mail.getResponsable() != null)
                        attachments = attachmentService.addAttachment(new FileInputStream(mailPdf),
                                mail.getResponsable().getId(),
                                Responsable.class.getSimpleName(), constantService.getAttachmentTypeAutomaticMail(),
                                "Customer_mail_" + formatter.format(LocalDateTime.now()) + ".pdf", false,
                                "Mail automatique n°" + mail.getId());
                    if (mail.getConfrere() != null)
                        attachments = attachmentService.addAttachment(new FileInputStream(mailPdf),
                                mail.getConfrere().getId(),
                                Confrere.class.getSimpleName(), constantService.getAttachmentTypeAutomaticMail(),
                                "Customer_mail_" + formatter.format(LocalDateTime.now()) + ".pdf", false,
                                "Mail automatique n°" + mail.getId());

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
                    mail.setHasErrors(true);
                    customerMailRepository.save(mail);
                    throw new OsirisException(e, "Impossible to read invoice PDF temp file");
                } catch (Exception e) {
                    mail.setHasErrors(true);
                    customerMailRepository.save(mail);
                    throw new OsirisException(e, "Impossible to generate mail PDF");
                } finally {
                    if (mailPdf != null)
                        mailPdf.delete();
                }
            }

            mail.setCustomerMailAssoAffaireOrders(null);
            mail.setVatMails(null);
            mail.setIsSent(true);
            mail.setVatMails(null);
            mail.setCustomerMailAssoAffaireOrders(null);
            customerMailRepository.save(mail);
        }
    }

    private void prepareAndSendMail(CustomerMail mail) throws OsirisException {
        boolean canSend = true;
        MimeMessage message = mailHelper.generateGenericMail(mail);

        if (mailDomainFilter != null && !mailDomainFilter.equals("")) {
            Address[] recipients;
            try {
                recipients = message.getRecipients(Message.RecipientType.TO);
            } catch (MessagingException e) {
                throw new OsirisException(e, "Unable to find recipients To for mail " + mail.getId());
            }
            if (recipients != null)
                for (Address address : recipients) {
                    String[] chunk = address.toString().split("@");
                    if (chunk.length != 2 || !chunk[1].equals(mailDomainFilter))
                        canSend = false;
                }
            try {
                recipients = message.getRecipients(Message.RecipientType.CC);
            } catch (MessagingException e) {
                throw new OsirisException(e, "Unable to find recipients Cc for mail " + mail.getId());
            }
            if (recipients != null)
                for (Address address : recipients) {
                    String[] chunk = address.toString().split("@");
                    if (chunk.length != 2
                            || !chunk[1].toLowerCase().trim().equals(mailDomainFilter.toLowerCase().trim()))
                        canSend = false;
                }
        }

        if (canSend)
            mailHelper.getMailSender().send(message);
    }

}
