package com.example.news.job;

import com.example.news.service.ArticleService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.InterruptableJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.UnableToInterruptJobException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ArticleCollectJob extends QuartzJobBean implements InterruptableJob {

    @Autowired
    private ArticleService articleService;

    @Override
    public void interrupt() throws UnableToInterruptJobException {
        log.warn("Interrupted : ArticleCollectJob");
    }

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        log.info("START : ArticleCollectJob");
        articleService.collectArticles();
        log.info("END : ArticleCollectJob");
    }
}
