package com.kelab.usercenter.serivce.impl;

import com.alibaba.fastjson.JSON;
import com.kelab.info.base.PaginationResult;
import com.kelab.info.base.query.BaseQuery;
import com.kelab.info.context.Context;
import com.kelab.info.usercenter.info.*;
import com.kelab.info.usercenter.query.NewsQuery;
import com.kelab.info.usercenter.query.NewsRollQuery;
import com.kelab.info.usercenter.query.ScrollPictureQuery;
import com.kelab.usercenter.convert.AboutConvert;
import com.kelab.usercenter.convert.NewsConvert;
import com.kelab.usercenter.convert.NewsRollConvert;
import com.kelab.usercenter.convert.ScrollPictureConvert;
import com.kelab.usercenter.dal.domain.AboutDomain;
import com.kelab.usercenter.dal.domain.NewsDomain;
import com.kelab.usercenter.dal.domain.NewsRollDomain;
import com.kelab.usercenter.dal.domain.ScrollPictureDomain;
import com.kelab.usercenter.dal.repo.AboutRepo;
import com.kelab.usercenter.dal.repo.NewsRepo;
import com.kelab.usercenter.dal.repo.NewsRollRepo;
import com.kelab.usercenter.dal.repo.ScrollPictureRepo;
import com.kelab.usercenter.serivce.PlatformInfoService;
import com.kelab.usercenter.support.ContextLogger;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class PlatformInfoServiceImpl implements PlatformInfoService {

    private ContextLogger contextLogger;

    private ScrollPictureRepo scrollPictureRepo;

    private NewsRollRepo newsRollRepo;

    private NewsRepo newsRepo;

    private AboutRepo aboutRepo;

    public PlatformInfoServiceImpl(ContextLogger contextLogger,
                                   ScrollPictureRepo scrollPictureRepo,
                                   NewsRollRepo newsRollRepo,
                                   NewsRepo newsRepo,
                                   AboutRepo aboutRepo) {
        this.contextLogger = contextLogger;
        this.scrollPictureRepo = scrollPictureRepo;
        this.newsRollRepo = newsRollRepo;
        this.newsRepo = newsRepo;
        this.aboutRepo = aboutRepo;
    }

    @Override
    public PaginationResult<ScrollPictureInfo> queryScrollPicturePage(Context context, ScrollPictureQuery query) {
        PaginationResult<ScrollPictureInfo> result = new PaginationResult<>();
        List<Integer> totalIds = CommonService.totalIds(query);
        if (!CollectionUtils.isEmpty(totalIds)) {
            List<ScrollPictureDomain> domains = scrollPictureRepo.queryByIds(totalIds);
            result.setPagingList(scrollPictureDomainToInfo(domains));
            result.setTotal(domains.size());
        } else {
            List<ScrollPictureDomain> domains = scrollPictureRepo.queryPage(query);
            result.setPagingList(scrollPictureDomainToInfo(domains));
            result.setTotal(scrollPictureRepo.queryTotal(query));
        }
        return result;
    }

    @Override
    public void saveScrollPicture(Context context, ScrollPictureDomain record) {
        scrollPictureRepo.save(record);
    }

    @Override
    public void updateScrollPicture(Context context, ScrollPictureDomain record) {
        scrollPictureRepo.update(record);
    }

    @Override
    public void deleteScrollPicture(Context context, List<Integer> ids) {
        List<ScrollPictureDomain> old = scrollPictureRepo.queryByIds(ids);
        scrollPictureRepo.delete(ids);
        contextLogger.info(context, "删除轮播图：%s", JSON.toJSONString(old));
    }

    @Override
    public PaginationResult<NewsInfo> queryNewsPage(Context context, NewsQuery query) {
        PaginationResult<NewsInfo> result = new PaginationResult<>();
        List<Integer> totalIds = CommonService.totalIds(query);
        if (!CollectionUtils.isEmpty(totalIds)) {
            List<NewsDomain> domains = newsRepo.queryByIds(totalIds);
            result.setPagingList(newsDomainToInfo(domains));
            result.setTotal(domains.size());
        } else {
            List<NewsDomain> domains = newsRepo.queryPage(query);
            result.setPagingList(newsDomainToInfo(domains));
            result.setTotal(newsRepo.queryTotal(query));
        }
        return result;
    }

    @Override
    public void saveNews(Context context, NewsDomain record) {
        record.setPublishUser(context.getOperatorId());
        record.setPubTime(System.currentTimeMillis());
        record.setViewNum(0);
        newsRepo.save(record);
    }

    @Override
    public void updateNews(Context context, NewsDomain record) {
        newsRepo.update(record);
    }

    @Override
    public void deleteNews(Context context, List<Integer> ids) {
        List<NewsDomain> old = newsRepo.queryByIds(ids);
        newsRepo.delete(ids);
        contextLogger.info(context, "删除新闻：%s", JSON.toJSONString(old));
    }

    @Override
    public void addViewNumber(Integer id) {
        newsRepo.addViewNumber(id);
    }

    @Override
    public PaginationResult<NewsRollInfo> queryNewsRollPage(Context context, NewsRollQuery query) {
        PaginationResult<NewsRollInfo> result = new PaginationResult<>();
        List<Integer> totalIds = CommonService.totalIds(query);
        if (!CollectionUtils.isEmpty(totalIds)) {
            List<NewsRollDomain> domains = newsRollRepo.queryByIds(totalIds);
            result.setPagingList(newsRollDomainToInfo(domains));
            result.setTotal(domains.size());
        } else {
            List<NewsRollDomain> domains = newsRollRepo.queryPage(query);
            result.setPagingList(newsRollDomainToInfo(domains));
            result.setTotal(newsRollRepo.queryTotal(query));
        }
        return result;
    }

    @Override
    public void saveNewsRoll(Context context, NewsRollDomain record) {
        record.setPubTime(System.currentTimeMillis());
        newsRollRepo.save(record);
    }

    @Override
    public void updateNewsRoll(Context context, NewsRollDomain record) {
        newsRollRepo.update(record);
    }

    @Override
    public void deleteNewsRoll(Context context, List<Integer> ids) {
        List<NewsRollDomain> old = newsRollRepo.queryByIds(ids);
        newsRollRepo.delete(ids);
        contextLogger.info(context, "删除通知：%s", JSON.toJSONString(old));
    }

    @Override
    public PaginationResult<AboutInfo> queryAboutPage(Context context, BaseQuery query) {
        PaginationResult<AboutInfo> result = new PaginationResult<>();
        List<Integer> totalIds = CommonService.totalIds(query);
        if (!CollectionUtils.isEmpty(totalIds)) {
            List<AboutDomain> domains = aboutRepo.queryByIds(totalIds);
            result.setPagingList(aboutDomainToInfo(domains));
            result.setTotal(domains.size());
        } else {
            List<AboutDomain> domains = aboutRepo.queryPage(query);
            result.setPagingList(aboutDomainToInfo(domains));
            result.setTotal(aboutRepo.queryTotal());
        }
        return result;
    }

    @Override
    public void saveAbout(Context context, AboutDomain record) {
        // 查找最新的一条数据, 这里用queryPage访问，可以走之前的缓存
        BaseQuery query = new BaseQuery();
        query.setPage(1);
        query.setRows(10);
        List<AboutDomain> domains = this.aboutRepo.queryPage(query);
        if (CollectionUtils.isEmpty(domains)) {
            record.setAboutOrder(1);
        } else {
            record.setAboutOrder(domains.get(0).getAboutOrder() + 1);
        }
        aboutRepo.save(record);
    }

    @Override
    public void updateAbout(Context context, AboutDomain record) {
        aboutRepo.update(record);
    }

    @Override
    public void deleteAbout(Context context, List<Integer> ids) {
        List<AboutDomain> old = aboutRepo.queryByIds(ids);
        aboutRepo.delete(ids);
        contextLogger.info(context, "删除关于：%s", JSON.toJSONString(old));
    }

    @Override
    public void changeAboutOrder(Context context, ChangeOrderInfo record) {
        aboutRepo.changeAboutOrder(record);
    }

    private List<ScrollPictureInfo> scrollPictureDomainToInfo(List<ScrollPictureDomain> domains) {
        if (CollectionUtils.isEmpty(domains)) {
            return Collections.emptyList();
        }
        return domains.stream().map(ScrollPictureConvert::domainToInfo).collect(Collectors.toList());
    }

    private List<NewsRollInfo> newsRollDomainToInfo(List<NewsRollDomain> domains) {
        if (CollectionUtils.isEmpty(domains)) {
            return Collections.emptyList();
        }
        return domains.stream().map(NewsRollConvert::domainToInfo).collect(Collectors.toList());
    }

    private List<AboutInfo> aboutDomainToInfo(List<AboutDomain> domains) {
        if (CollectionUtils.isEmpty(domains)) {
            return Collections.emptyList();
        }
        return domains.stream().map(AboutConvert::domainToInfo).collect(Collectors.toList());
    }

    private List<NewsInfo> newsDomainToInfo(List<NewsDomain> domains) {
        if (CollectionUtils.isEmpty(domains)) {
            return Collections.emptyList();
        }
        return domains.stream().map(NewsConvert::domainToInfo).collect(Collectors.toList());
    }
}
