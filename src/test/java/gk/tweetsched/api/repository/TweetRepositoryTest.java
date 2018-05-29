package gk.tweetsched.api.repository;

import gk.tweetsched.api.data.Tweet;
import io.vertx.core.json.Json;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.testng.annotations.Test;
import redis.clients.jedis.Jedis;

import org.testng.annotations.BeforeMethod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * TweetRepositoryTest class.
 * <p>
 * Date: May 29, 2018
 * <p>
 *
 * @author Gleb Kosteiko.
 */
public class TweetRepositoryTest {
    private static final String TWEETS_HASH = "tweets";
    private static final String ID1 = "3";
    private static final String TWEET1 = "{\"id\":\"3\",\"message\":\"Test tweet!\"}";
    private static final String TWEET_MESSAGE2 = "Another one tweet.";
    Map<String, String> resultMap = new HashMap<>();

    @Mock
    private Jedis jedis;
    @InjectMocks
    private TweetRepository repo = new TweetRepository();

    @BeforeMethod
    public void setUp() {
        initMocks(this);
        resultMap.put(ID1, TWEET1);
    }

    @Test
    public void testGetAll() {
        when(jedis.hgetAll(TWEETS_HASH)).thenReturn(resultMap);

        List<Tweet> results = repo.getAll();
        assertTrue(!results.isEmpty());

        verify(jedis).hgetAll(eq(TWEETS_HASH));
        verifyNoMoreInteractions(jedis);
    }

    @Test
    public void testGet() {
        when(jedis.hget(TWEETS_HASH, ID1)).thenReturn(TWEET1);

        Tweet resultTweet = repo.get(ID1);
        assertTrue(resultTweet != null);
        assertEquals(ID1, resultTweet.getId());

        verify(jedis).hget(eq(TWEETS_HASH), eq(ID1));
        verifyNoMoreInteractions(jedis);
    }

    @Test
    public void testSave() {
        Tweet tweet2 = new Tweet(TWEET_MESSAGE2);
        repo.save(tweet2);

        verify(jedis).hset(eq(TWEETS_HASH), eq(tweet2.getId()), eq(Json.encodePrettily(tweet2)));
        verifyNoMoreInteractions(jedis);
    }

    @Test
    public void testDelete() {
        repo.delete(ID1);

        verify(jedis).hdel(eq(TWEETS_HASH), eq(ID1));
        verifyNoMoreInteractions(jedis);
    }
}
