package ru.andryss.trousseau.tms;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.scheduling.quartz.QuartzJobBean;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public abstract class BaseExecutor extends QuartzJobBean implements BeanNameAware {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    @Getter
    private String jobName;

    @Override
    public void setBeanName(@NotNull String name) {
        this.jobName = name;
    }

    @Override
    protected void executeInternal(@NotNull JobExecutionContext context) throws JobExecutionException {
        try {
            log.info("Job {} started", jobName);
            doJob(context);
            log.info("Job {} finished", jobName);
        } catch (Exception e) {
            log.error("Job {} failed with error", jobName, e);
        }
    }

    public abstract String cronExpression();
    public abstract void doJob(JobExecutionContext context);
}
