package ru.andryss.trousseau.tms;

import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

@Component
public class DummyJob extends BaseExecutor {
    @Override
    public String cronExpression() {
        return "0 */1 * * * ?"; // start each minute
    }

    @Override
    public void doJob(JobExecutionContext context) {
        log.info("DummyJob triggered");
    }
}
