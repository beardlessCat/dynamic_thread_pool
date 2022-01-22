package com.bigyj.pool.alarm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ThreadPoolAlarmEmail implements ThreadPoolAlarm{
    @Override
    public void alarm(AlarmMetaData alarmMetaData) {
        log.info("发送报警邮件"+alarmMetaData.toString());
    }
}
