package gk.tweetsched.api.repository;

import com.fasterxml.jackson.module.kotlin.KotlinModule;
import gk.tweetsched.dto.Tweet;
import io.vertx.core.json.Json;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

import java.util.List;
import java.util.ArrayList;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(TweetRepository.class);
    private static final String TWEETS_HASH = "tweets";
    private JedisPool pool;

    public TweetRepository() {
        init();
    }

    public TweetRepository(String redisUrl, int port, String password) {
        init();
        this.pool = new JedisPool(new JedisPoolConfig(), redisUrl, port, Protocol.DEFAULT_TIMEOUT, password);
    }

    private void init() {
        Json.mapper.registerModule(new KotlinModule());
        Json.prettyMapper.registerModule(new KotlinModule());
    }

    public List<Tweet> getAll() {
        try (Jedis jedis = pool.getResource()) {
            return jedis.hgetAll(TWEETS_HASH)
                    .values()
                    .stream()
                    .map(tw -> Json.decodeValue(tw, Tweet.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return new ArrayList<>();
    }

    public Tweet get(String id) {
        try (Jedis jedis = pool.getResource()) {
            return Json.decodeValue(jedis.hget(TWEETS_HASH, id), Tweet.class);
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return null;
    }

    public void save(Tweet tweet) {
        try (Jedis jedis = pool.getResource()) {
            jedis.hset(TWEETS_HASH, tweet.getId(), Json.encodePrettily(tweet));
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

    public void delete(String id) {
        try (Jedis jedis = pool.getResource()) {
            jedis.hdel(TWEETS_HASH, id);
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }
}
