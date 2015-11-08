package com.mfodepositorsacc.service;

import com.mfodepositorsacc.dmodel.ManagedUnit;
import com.mfodepositorsacc.dmodel.NewsItem;
import com.mfodepositorsacc.dmodel.User;
import com.mfodepositorsacc.exceptions.WrongInputDataException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * Created by berz on 26.10.2015.
 */
@Service
public interface NewsService {

    List<NewsItem> newsItemsByManagedUnits(Set<ManagedUnit> managedUnits);

    List<NewsItem> newsItemsByManagedUnitsPublished(Set<ManagedUnit> managedUnits);

    void createNewsItemForOneUser(User user, NewsItem newsItem) throws WrongInputDataException;

    void saveNewsItem(Set<ManagedUnit> managedUnits, NewsItem newsItem);

    public void createNewsItemForOneUser(User user, String title, String text) throws WrongInputDataException;

    void unpublish(NewsItem newsItem);

    void publish(NewsItem newsItem);

    void delete(NewsItem newsItem);

    void updateNewsItem(NewsItem newsItem, NewsItem newsItemModified) throws WrongInputDataException;

    void updateNewsItem(NewsItem newsItem, String name, String description) throws WrongInputDataException;
}
