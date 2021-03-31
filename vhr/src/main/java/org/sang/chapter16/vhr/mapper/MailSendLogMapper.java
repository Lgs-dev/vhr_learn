package org.sang.chapter16.vhr.mapper;

import org.sang.chapter16.vhr.entity.MailSendLog;

public interface MailSendLogMapper {
    int insert(MailSendLog record);

    int insertSelective(MailSendLog record);
}