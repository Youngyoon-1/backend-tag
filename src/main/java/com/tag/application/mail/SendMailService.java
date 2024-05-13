package com.tag.application.mail;

import com.tag.domain.member.Member;
import com.tag.domain.member.MemberRepository;
import com.tag.dto.response.thankYouMessage.SaveThankYouMessageResult;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
public class SendMailService {

    private static final String SUBJECT = "[Tag] 새로운 감사메세지가 도착했어요.\uD83D\uDCEB";
    private static final String ENCODING_UTF_8 = "UTF-8";
    private static final String THANK_YOU_MESSAGE_NOTIFICATION_TEMPLATE_HTML = "thankYouMessageNotificationTemplate.html";
    private static final String CONTEXT_KEY_NAME = "name";
    private static final String CONTEXT_KEY_LINK = "link";
    private static final String MID_PATH_GET_MEMBER_INFO = "/members/";

    private static final String MEMBER_DOES_NOT_EXIST = "존재하지 않는 회원 입니다.";
    private static final String FAIL_CREATE_MIME_MESSAGE_HELPER = "MimeMessageHelper 생성 중 오류가 발생했습니다.";

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

    public void sendMail(final SaveThankYouMessageResult saveThankYouMessageResult) {
        final long recipientId = saveThankYouMessageResult.getRecipientId();
        final long writerMemberId = saveThankYouMessageResult.getWriterMemberId();
        if (recipientId == writerMemberId) {
            return;
        }
        final Member recipient = memberRepository.findById(recipientId)
                .orElseThrow(() -> new IllegalArgumentException(MEMBER_DOES_NOT_EXIST));
        if (recipient.isConfirmedMailNotification()) {
            final String writerEmail = memberRepository.findEmailById(writerMemberId)
                    .orElseThrow(() -> new IllegalArgumentException(MEMBER_DOES_NOT_EXIST));
            final MimeMessage message = createMineMessage(recipient, writerEmail);
            javaMailSender.send(message);
        }
    }

    private MimeMessage createMineMessage(final Member recipient, final String writerEmail) {
        final MimeMessage message = javaMailSender.createMimeMessage();
        try {
            final MimeMessageHelper helper = new MimeMessageHelper(message, true, ENCODING_UTF_8);
            helper.setSubject(SUBJECT);
            helper.setFrom(fromEmail);
            final String recipientEmail = recipient.getEmail();
            helper.setTo(recipientEmail);
            final String body = createHtml(writerEmail, recipient);
            helper.setText(body, true);
        } catch (final MessagingException e) {
            throw new IllegalArgumentException(FAIL_CREATE_MIME_MESSAGE_HELPER);
        }
        return message;
    }

    private String createHtml(final String writerEmail, final Member recipient) {
        final Context context = new Context();
        context.setVariable(CONTEXT_KEY_NAME, writerEmail);
        context.setVariable(CONTEXT_KEY_LINK, frontDomain + MID_PATH_GET_MEMBER_INFO + recipient.getId());
        return templateEngine.process(THANK_YOU_MESSAGE_NOTIFICATION_TEMPLATE_HTML, context);
    }
}
