package com.tag.domain.comment;

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
@Table(name = "thank_you_message_comment")
@Getter
@Builder
@AllArgsConstructor
@ToString
public final class Comment implements Identifiable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "commentSeq")
    @SequenceGenerator(name = "commentSeq", sequenceName = "THANK_YOU_MESSAGE_COMMENT_SEQ", allocationSize = 1)
    private long id;

    @Column(name = "thank_you_message_id", nullable = false)
    private long thankYouMessageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "content", nullable = false, length = 400)
    private String content;

    public Comment() {
    }
}
