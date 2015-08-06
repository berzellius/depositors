package com.mfodepositorsacc.preparator;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Created by berz on 10.06.15.
 */
public class MimeMessagePreparatorImpl implements MimeMessagePreparator {

    private SimpleMailMessage mailMessage;
    private String body;
    private String to;
    private String subject;

    public MimeMessagePreparatorImpl(SimpleMailMessage mailMessage, String body, String subject, String to){
        this.mailMessage = mailMessage;
        this.body = body;
        this.to = to;
        this.subject = subject;
    }

    @Override
    public void prepare(MimeMessage mimeMessage) throws Exception {
        MimeMessageHelper message = new MimeMessageHelper(mimeMessage, false, "UTF-8");

        if(this.getMailMessage() != null){
            message.setFrom(new InternetAddress(this.getMailMessage().getFrom()));
            message.setText(this.getBody(), true);
            message.setSubject(this.getSubject());
            message.setTo(this.getTo());
        }
    }

    public SimpleMailMessage getMailMessage() {
        return mailMessage;
    }

    public void setMailMessage(SimpleMailMessage mailMessage) {
        this.mailMessage = mailMessage;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
