package com.mfodepositorsacc.service;

import com.mfodepositorsacc.dmodel.ManagedUnit;
import com.mfodepositorsacc.dmodel.ManagedUser;
import com.mfodepositorsacc.dmodel.NewsItem;
import com.mfodepositorsacc.dmodel.User;
import com.mfodepositorsacc.exceptions.WrongInputDataException;
import com.mfodepositorsacc.repository.ManagedUserRepository;
import com.mfodepositorsacc.repository.NewsItemRepository;
import com.mfodepositorsacc.specifications.NewsItemSpecifications;
import org.apache.commons.collections.set.ListOrderedSet;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Cleaner;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Created by berz on 26.10.2015.
 */
@Service
@Transactional
public class NewsServiceImpl implements NewsService {

    @Autowired
    ManagedUnitsService managedUnitsService;

    @Autowired
    NewsItemRepository newsItemRepository;

    private void checkNewsItem(NewsItem newsItem) throws WrongInputDataException {
        if(newsItem.getName().equals("")){
            throw new WrongInputDataException("bad data in news item!", WrongInputDataException.Reason.NAME_FIELD);
        }

        if(newsItem.getDescription().equals("")){
            throw new WrongInputDataException("bad data in news item!", WrongInputDataException.Reason.DESCRIPTION_FIELD);
        }
    }

    @Override
    public List<NewsItem> newsItemsByManagedUnits(Set<ManagedUnit> managedUnits){
        List<NewsItem> newsItems = newsItemRepository.findAll(
                Specifications
                        .where(NewsItemSpecifications.byManagedUnits(managedUnits))
                        .and(NewsItemSpecifications.nonDeleted())
        );

        return newsItems;
    }

    @Override
    public List<NewsItem> newsItemsByManagedUnitsPublished(Set<ManagedUnit> managedUnits) {
        List<NewsItem> newsItems = newsItemRepository.findAll(
                Specifications
                        .where(NewsItemSpecifications.byManagedUnits(managedUnits))
                        .and(NewsItemSpecifications.published())
                        .and(NewsItemSpecifications.nonDeleted())
        );

        return newsItems;
    }

    @Override
    public void createNewsItemForOneUser(User user, NewsItem newsItem) throws WrongInputDataException {

        checkNewsItem(newsItem);

        // Определяем ManagedUnit для этого user
        ManagedUnit managedUnit = managedUnitsService.seekManagedUnitForUser(user);

        Set<ManagedUnit> managedUnitSet = new ListOrderedSet();
        managedUnitSet.add(managedUnit);

        saveNewsItem(managedUnitSet, newsItem);
    }

    @Override
    public void saveNewsItem(Set<ManagedUnit> managedUnits, NewsItem newsItem){
        newsItem.setManagedUnits(managedUnits);
        newsItem.setDescription(removeForbidden(newsItem.getDescription()));
        newsItem.setDtmCreate(new Date());
        newsItem.setDeleted(false);
        newsItem.setPublished(true);

        newsItemRepository.save(newsItem);
    }

    @Override
    public void createNewsItemForOneUser(User user, String title, String text) throws WrongInputDataException {
        NewsItem newsItem = new NewsItem();
        newsItem.setName(title);
        newsItem.setDescription(text);

        createNewsItemForOneUser(user, newsItem);
    }

    @Override
    public void unpublish(NewsItem newsItem) {
        newsItem.setPublished(false);
        newsItemRepository.save(newsItem);
    }

    @Override
    public void publish(NewsItem newsItem) {
        newsItem.setPublished(true);
        newsItemRepository.save(newsItem);
    }

    @Override
    public void delete(NewsItem newsItem) {
        newsItem.setDeleted(true);
        newsItemRepository.save(newsItem);
    }

    @Override
    public void updateNewsItem(NewsItem newsItem, NewsItem newsItemModified) throws WrongInputDataException {
        checkNewsItem(newsItemModified);

        newsItem.setName(newsItemModified.getName());
        newsItem.setDescription(newsItemModified.getDescription());
        newsItem.setDtmUpdate(new Date());

        newsItemRepository.save(newsItem);
    }

    @Override
    public void updateNewsItem(NewsItem newsItem, String name, String description) throws WrongInputDataException {
        NewsItem newsItemModified = new NewsItem();
        newsItemModified.setName(name);
        newsItemModified.setDescription(description);

        updateNewsItem(newsItem, newsItemModified);
    }

    private String removeForbidden(String text) {

        String charset = "UTF8";
        String result = text;
        try {
            InputStream is = new ByteArrayInputStream(text.getBytes(charset));
            Document document = Jsoup.parse(is, charset, "/");
            Cleaner cleaner = new Cleaner(this.getWhiteList());
            Document document1 = cleaner.clean(document);
            result = document1.body().html();
        } catch (UnsupportedEncodingException e) {
            // no utf
            System.out.println(charset + " not supported!");
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            return result;
        }

        /*
        return Jsoup.clean(text, whitelist);
        */
    }

    private Whitelist getWhiteList(){
        Whitelist whitelist = Whitelist.relaxed();
        whitelist.addProtocols("a", "href", "http");
        whitelist.removeProtocols("a", "href", "javascript");
        whitelist.removeAttributes(":all",
                "onclick", "onmouseover", "onmouseout", "onload", "ondblclick", "onmousedown", "onmouseup", "onload", "onready");
        whitelist.removeTags("script", "iframe", "video", "audio");

        return whitelist;
    }
}
