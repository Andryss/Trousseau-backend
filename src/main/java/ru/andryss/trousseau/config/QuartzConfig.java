package ru.andryss.trousseau.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import ru.andryss.trousseau.tms.BaseExecutor;

@Configuration
@Profile("!functionalTest")
public class QuartzConfig {

    @Bean
    public Scheduler scheduler(SchedulerFactoryBean factory, List<BaseExecutor> executors) throws SchedulerException {
        factory.setWaitForJobsToCompleteOnShutdown(true);
        Scheduler scheduler = factory.getScheduler();

        List<JobDetail> jobDetails = new ArrayList<>(executors.size());
        List<Trigger> triggers = new ArrayList<>(executors.size());

        for (BaseExecutor executor : executors) {
            JobDetail jobDetail = JobBuilder.newJob()
                    .ofType(executor.getClass())
                    .withIdentity(executor.getJobName())
                    .build();
            jobDetails.add(jobDetail);

            Trigger trigger = TriggerBuilder.newTrigger()
                    .forJob(jobDetail)
                    .withSchedule(CronScheduleBuilder.cronSchedule(executor.cronExpression()))
                    .build();
            triggers.add(trigger);
        }

        Set<JobKey> allJobKeys = scheduler.getJobKeys(GroupMatcher.anyGroup());
        jobDetails.forEach(details -> allJobKeys.remove(details.getKey()));
        for (JobKey keyToRemove : allJobKeys) {
            scheduler.deleteJob(keyToRemove);
        }

        for (int i = 0; i < triggers.size(); i++) {
            JobDetail jobDetail = jobDetails.get(i);
            Trigger trigger = triggers.get(i);

            if (scheduler.checkExists(jobDetail.getKey())) {
                scheduler.rescheduleJob(trigger.getKey(), trigger);
            } else {
                scheduler.scheduleJob(jobDetail, trigger);
            }
        }

        scheduler.start();
        return scheduler;
    }

}
