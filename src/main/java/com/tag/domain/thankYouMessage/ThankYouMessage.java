package com.tag.domain.thankYouMessage;

import com.tag.domain.member.Member;
import com.tag.domain.pagination.Identifiable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Entity
@Table(name = "thank_you_message")
@Getter
@Builder
@AllArgsConstructor
@ToString
public final class ThankYouMessage implements Identifiable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "thankYouMessageSeq")
    @SequenceGenerator(name = "thankYouMessageSeq", sequenceName = "THANK_YOU_MESSAGE_SEQ", allocationSize = 1)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_member_id", nullable = false)
    private Member writerMember;

    @Column(name = "recipient_id", nullable = false)
    private long recipientId;

    @Column(name = "content", nullable = false, length = 400)
    private String content;

    public ThankYouMessage() {
    }
}
