package com.tag.application;

import com.tag.domain.Member;
import com.tag.domain.MemberRepository;
import com.tag.dto.response.SaveThankYouMessageResult;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
@Transactional(readOnly = true)
@Slf4j
public class SendMailService {

    private static final String SUBJECT = "[Tag] 새로운 감사메세지가 도착했어요.\uD83D\uDCEB";

    private final MemberRepository memberRepository;
    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;
    private final String fromEmail;
    private final String frontDomain;

    public SendMailService(final MemberRepository memberRepository, final JavaMailSender javaMailSender,
                           final SpringTemplateEngine templateEngine,
                           @Value("${spring.mail.username}") final String fromEmail,
                           @Value("${tag.front-domain}") final String frontDomain) {
        this.memberRepository = memberRepository;
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
        this.fromEmail = fromEmail;
        this.frontDomain = frontDomain;
    }

//    @Async
    public void sendMail(final SaveThankYouMessageResult saveThankYouMessageResult) {
        final Long recipientId = saveThankYouMessageResult.getRecipientId();
        final Long writerMemberId = saveThankYouMessageResult.getWriterMemberId();
        if (recipientId.equals(writerMemberId)) {
            return;
        }
        final Member recipient = memberRepository.findById(recipientId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 아이디 입니다."));
        if (recipient.isConfirmedMailNotification()) {
            final String writerEmail = memberRepository.findEmailById(writerMemberId)
                    .orElseThrow(() -> new RuntimeException("존재하지 않는 아이디 입니다."));
            final MimeMessage message = createMineMessage(recipient, writerEmail);
            send(message, recipient);
        }
    }

    private MimeMessage createMineMessage(final Member recipient, final String writerEmail) {
        final MimeMessage message = javaMailSender.createMimeMessage();
        try {
            final MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setSubject(SUBJECT);
            helper.setFrom(fromEmail);
            final String recipientEmail = recipient.getEmail();
            helper.setTo(recipientEmail);
            final Context context = new Context();
            context.setVariable("name", writerEmail);
            context.setVariable("link", frontDomain + "/members/" + recipient.getId());
            final String body = templateEngine.process("thankYouMessageNotificationTemplate.html", context);
            helper.setText(body, true);
        } catch (MessagingException e) {
            throw new RuntimeException("MimeMessageHelper 생성 중 오류가 발생했습니다.");
        }
        return message;
    }

    private void send(final MimeMessage message, final Member recipient) {
        try {
            javaMailSender.send(message);
        } catch (Exception e) {
            log.error("Email 전송 중 오류가 발생했습니다. Email : {} MemberId : {}", recipient.getEmail(), recipient.getId(), e);
        }
    }
}
