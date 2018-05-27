package gk.tweetsched.api.repository;

import gk.tweetsched.api.data.Tweet;
import io.vertx.core.json.Json;
import redis.clients.jedis.Jedis;

import java.util.*;
import java.util.stream.Collectors;

/**
 * TweetRepository class.
 * <p>
 * Date: May 26, 2018
 * <p>
 *
 * @author Gleb Kosteiko.
 */
public class TweetRepository {
    private static final String TWEETS_HASH = "tweets";
    private static final String REDIS_URL = "127.0.0.1";
    private Jedis jedis;

    public TweetRepository() {
        this.jedis = new Jedis(REDIS_URL);
    }

    public List<Tweet> getAll() {
        return jedis.hgetAll(TWEETS_HASH).values()
                .stream()
                .map(tw -> Json.decodeValue(tw, Tweet.class))
                .collect(Collectors.toList());
    }

    public Tweet get(String id) {
        return Json.decodeValue(jedis.hget(TWEETS_HASH, id), Tweet.class);
    }

    public void save(Tweet tweet) {
        jedis.hset(TWEETS_HASH, tweet.getId(), Json.encodePrettily(tweet));
    }

    public void delete(String id) {
        jedis.hdel(TWEETS_HASH,id);
    }
}
