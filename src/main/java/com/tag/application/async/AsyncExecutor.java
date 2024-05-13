package com.tag.application.async;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AsyncExecutor {

    @Async
    public void execute(final Runnable asyncTask) {
        try {
            asyncTask.run();
        } catch (final Exception e) {
            log.error("비동기 작업이 실패했습니다.", e);
        }
    }
}
