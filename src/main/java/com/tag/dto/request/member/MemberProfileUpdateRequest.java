package com.tag.dto.request.member;

import jakarta.validation.constraints.Size;

public record MemberProfileUpdateRequest(@Size(max = 500) String introduction, String profileImageName) {
}
