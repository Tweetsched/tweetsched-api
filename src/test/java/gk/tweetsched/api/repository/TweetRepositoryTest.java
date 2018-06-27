package gk.tweetsched.api.repository;

import gk.tweetsched.dto.Tweet;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.testng.annotations.Test;
import redis.clients.jedis.Jedis;

import org.testng.annotations.BeforeMethod;
import redis.clients.jedis.JedisPool;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
    private JedisPool pool;
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
        when(pool.getResource()).thenReturn(jedis);
        when(jedis.hgetAll(TWEETS_HASH)).thenReturn(resultMap);

        List<Tweet> results = repo.getAll();
        assertTrue(!results.isEmpty());

        verify(pool).getResource();
        verifyNoMoreInteractions(pool);
    }

    @Test
    public void testGet() {
        when(pool.getResource()).thenReturn(jedis);
        when(jedis.hget(TWEETS_HASH, ID1)).thenReturn(TWEET1);

        Tweet resultTweet = repo.get(ID1);
        assertTrue(resultTweet != null);
        assertEquals(ID1, resultTweet.getId());

        verify(pool).getResource();
        verifyNoMoreInteractions(pool);
    }

    @Test
    public void testSave() {
        when(pool.getResource()).thenReturn(jedis);

        Tweet tweet2 = new Tweet();
        tweet2.setId(String.valueOf(UUID.randomUUID()));
        tweet2.setMessage(TWEET_MESSAGE2);
        repo.save(tweet2);

        verify(pool).getResource();
        verifyNoMoreInteractions(pool);
    }

    @Test
    public void testDelete() {
        when(pool.getResource()).thenReturn(jedis);

        repo.delete(ID1);

        verify(pool).getResource();
        verifyNoMoreInteractions(pool);
    }
}
