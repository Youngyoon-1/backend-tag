package com.tag.application;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.tag.application.mail.SendMailService;
import com.tag.domain.member.Member;
import com.tag.domain.member.MemberRepository;
import com.tag.dto.response.thankYouMessage.SaveThankYouMessageResult;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.spring6.SpringTemplateEngine;

@ExtendWith(MockitoExtension.class)
class SendMailServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private JavaMailSender javaMailSender;

    private final SpringTemplateEngine springTemplateEngine = new SpringTemplateEngine();

    private SendMailService sendMailService;

    @BeforeEach
    void setUp() {
        sendMailService = new SendMailService(memberRepository, javaMailSender, springTemplateEngine, "admin@test.com",
                "http://front-domain");
    }

    @Test
    void 메일을_발송한다() {
        // given
        final SaveThankYouMessageResult saveThankYouMessageResult = new SaveThankYouMessageResult(1L, 2L);
        final Member recipient = Member.builder()
                .id(2L)
                .email("recipient@test.com")
                .isConfirmedMailNotification(true)
                .build();
        BDDMockito.given(memberRepository.findById(2L))
                .willReturn(Optional.of(recipient));
        BDDMockito.given(memberRepository.findEmailById(1L))
                .willReturn(Optional.of("writer@test.com"));
        BDDMockito.given(javaMailSender.createMimeMessage())
                .willReturn(new MimeMessage((Session) null));

        // when
        sendMailService.sendMail(saveThankYouMessageResult);

        // then
        BDDMockito.verify(javaMailSender)
                .send(BDDMockito.any(MimeMessage.class));
    }

    @Test
    void 감사메세지를_스스로_작성한_경우_메일이_발송되지_않는다() {
        // given
        final SaveThankYouMessageResult saveThankYouMessageResult = new SaveThankYouMessageResult(1L, 1L);

        // when
        sendMailService.sendMail(saveThankYouMessageResult);

        // then
        BDDMockito.verify(javaMailSender, BDDMockito.never())
                .send(BDDMockito.any(MimeMessage.class));
    }

    @Test
    void 메일_발송시_감사메세지_받는_회원아이디가_유효하지_않을_경우_예외가_발생한다() {
        // given
        final SaveThankYouMessageResult saveThankYouMessageResult = new SaveThankYouMessageResult(1L, 2L);
        BDDMockito.given(memberRepository.findById(2L))
                .willReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> sendMailService.sendMail(saveThankYouMessageResult))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 회원 입니다.");
    }

    @Test
    void 메일_발송시_감사메세지_작성_회원아이디가_유효하지_않을_경우_예외가_발생한다() {
        // given
        final SaveThankYouMessageResult saveThankYouMessageResult = new SaveThankYouMessageResult(1L, 2L);
        final Member member = Member.builder()
                .id(2L)
                .isConfirmedMailNotification(true)
                .build();
        BDDMockito.given(memberRepository.findById(2L))
                .willReturn(Optional.of(member));
        BDDMockito.given(memberRepository.findEmailById(1L))
                .willReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> sendMailService.sendMail(saveThankYouMessageResult))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 회원 입니다.");
    }

    @Test
    void 감사메세지_받는사람이_알림을_끈경우_메일이_발송되지_않는다() {
        // given
        final SaveThankYouMessageResult saveThankYouMessageResult = new SaveThankYouMessageResult(1L, 2L);
        final Member member = Member.builder()
                .id(2L)
                .build();
        BDDMockito.given(memberRepository.findById(2L))
                .willReturn(Optional.of(member));

        // when
        sendMailService.sendMail(saveThankYouMessageResult);

        // then
        BDDMockito.verify(javaMailSender, BDDMockito.never())
                .send(BDDMockito.any(MimeMessage.class));
    }
}
