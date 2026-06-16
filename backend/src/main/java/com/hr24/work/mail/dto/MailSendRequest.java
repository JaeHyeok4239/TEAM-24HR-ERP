package com.hr24.work.mail.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MailSendRequest {

    private List<Long> receiverIds;
    private String title;
    private String content;
    private Long parentMailId;
}
