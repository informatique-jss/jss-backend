package com.jss.osiris.libs.mail;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

@Service
public class MailHelper {

    @Value("${mail.smtp.host}")
    private String mailHost;

    @Value("${mail.smtp.port}")
    private String mailPort;

    @Value("${mail.smtp.username}")
    private String mailUsername;

    @Value("${mail.smtp.password}")
    private String mailPassword;

    private JavaMailSender javaMailSender;

    public static final String EMAIL_TEMPLATE_ENCODING = "UTF-8";

    private static final String PNG_MIME = "image/png";

    @Autowired
    private ApplicationContext applicationContext;

    private JavaMailSender getMailSender() throws IOException {
        if (javaMailSender != null)
            return javaMailSender;

        final JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setHost(mailHost);
        mailSender.setPort(Integer.parseInt(mailPort));
        mailSender.setUsername(mailUsername);
        mailSender.setPassword(mailPassword);
        final Properties javaMailProperties = new Properties();
        javaMailProperties
                .load(this.applicationContext.getResource("classpath:application.properties").getInputStream());
        mailSender.setJavaMailProperties(javaMailProperties);

        javaMailSender = mailSender;

        return mailSender;
    }

    @Bean
    public TemplateEngine emailTemplateEngine() {
        final SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.addTemplateResolver(htmlTemplateResolver());
        // Message source, internationalization specific to emails
        return templateEngine;
    }

    private ITemplateResolver htmlTemplateResolver() {
        final ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setOrder(Integer.valueOf(1));
        templateResolver.setPrefix("mails/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding(EMAIL_TEMPLATE_ENCODING);
        templateResolver.setCacheable(false);
        return templateResolver;
    }

    // @Scheduled(initialDelay = 500, fixedDelay = 1000000)
    public void test() throws MessagingException {
        try {
            final MimeMessage mimeMessage = getMailSender().createMimeMessage();
            final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");
            message.setFrom(new InternetAddress("no_reply@jss.fr", "Journal Spécial des Sociétés"));
            message.setTo("agapin@jss.fr");
            message.setSubject("This is the message subject");
            message.setText("This is the message body");
            getMailSender().send(mimeMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // @Scheduled(initialDelay = 500, fixedDelay = 1000000)
    public void sendSimpleMail() throws MessagingException {

        try {
            // Prepare the evaluation context
            final Context ctx = new Context();
            ctx.setVariable("name", "alex");
            ctx.setVariable("subscriptionDate", new Date());
            ctx.setVariable("hobbies", Arrays.asList("Cinema", "Sports", "Music"));
            ctx.setVariable("imageResourceName", "imageResourceName");

            // Prepare message using a Spring helper
            MimeMessage mimeMessage;
            mimeMessage = getMailSender().createMimeMessage();
            final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            message.setFrom(new InternetAddress("no_reply@jss.fr", "Journal Spécial des Sociétés"));
            message.setTo("agapin@jss.fr");
            message.setSubject("This is the message subject");

            // Create the HTML body using Thymeleaf
            final String htmlContent = emailTemplateEngine().process("test", ctx);
            message.setText(htmlContent, true);

            final InputStreamSource imageSource = new ByteArrayResource(
                    Files.readAllBytes(ResourceUtils.getFile("classpath:images/jss.png").toPath()));
            message.addInline("imageResourceName", imageSource, PNG_MIME);

            // Send email
            getMailSender().send(mimeMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
