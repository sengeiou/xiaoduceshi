package xiaoduhome.common.config;

import ai.qiwu.com.xiaoduhome.pojo.OverTimeDetail;
import ai.qiwu.com.xiaoduhome.pojo.UserBehaviorData;
import ai.qiwu.com.xiaoduhome.pojo.XiaoDuNoScreenStore;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author 苗权威
 * @dateTime 20-1-9 下午7:54
 */
@Configuration
public class RedisConfig {

    @Bean("userBehaviorTemplate")
    public RedisTemplate<String, UserBehaviorData> xiaoduRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        Jackson2JsonRedisSerializer<UserBehaviorData> serializer = new Jackson2JsonRedisSerializer<>(UserBehaviorData.class);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);

        serializer.setObjectMapper(objectMapper);

        RedisTemplate<String, UserBehaviorData> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(serializer);
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(serializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}
