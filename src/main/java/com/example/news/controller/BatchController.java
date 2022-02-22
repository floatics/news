package com.example.news.controller;

import static org.quartz.JobBuilder.newJob;

import com.example.news.job.ArticleCollectJob;
import com.example.news.service.ArticleService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
public class BatchController {
    @Autowired
    private Scheduler scheduler;

    @Autowired
    ArticleService articleService;

    @PostConstruct
    public void Start() {
        JobDetail aggreReqJobDetail = buildJobDetail(ArticleCollectJob.class, "articleCollectJob", "article", new HashMap());
        try {
            log.info("스케줄 등록");
            scheduler.scheduleJob(aggreReqJobDetail, buildJobTrigger("0 0 * * * ?"));
            log.info("시작시 기본데이터 최초 수집");
            articleService.collectArticles();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    public Trigger buildJobTrigger(String scheduleExp) {
        return TriggerBuilder.newTrigger()
                .withSchedule(CronScheduleBuilder.cronSchedule(scheduleExp)).build();
    }

    public JobDetail buildJobDetail(Class job, String name, String group, Map params) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.putAll(params);
        return newJob(job).withIdentity(name, group)
                .usingJobData(jobDataMap)
                .build();
    }

}
