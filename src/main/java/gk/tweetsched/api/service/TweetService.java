package gk.tweetsched.api.service;

import gk.tweetsched.api.data.Tweet;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * TweetService class.
 * <p>
 * Date: May 26, 2018
 * <p>
 *
 * @author Gleb Kosteiko.
 */
public class TweetService {
    private Jedis jedis;
    private Map<Integer, Tweet> readingList = new LinkedHashMap<>();

    private void createSomeData() {
        Tweet tweet1 = new Tweet("Fallacies of distributed computing");
        readingList.put(tweet1.getId(), tweet1);
        Tweet tweet2 = new Tweet("Reactive Manifesto");
        readingList.put(tweet2.getId(), tweet2);
    }

    public TweetService() {
        this.jedis = new Jedis();
        createSomeData();
    }

    public List<Tweet> getAll() {
        return new ArrayList<>(readingList.values());
    }

    public Tweet get(int id) {
        return readingList.get(id);
    }

    public void save(Tweet tweet) {
        readingList.put(tweet.getId(), tweet);
    }

    public void delete(int id) {
        readingList.remove(id);
    }
}
