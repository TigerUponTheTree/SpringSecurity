package com.sangeng.Config;

import com.sangeng.Utils.FastJsonRedisSerializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@Slf4j
public class RedisConfiguration {

    @Bean
    public RedisTemplate<Object,Object> redisTemplate(RedisConnectionFactory connectionFactory){
        log.info("开始创建redis模板对象...");
        RedisTemplate<Object,Object> template = new RedisTemplate();
        //设置redis的连接工厂对象
        template.setConnectionFactory(connectionFactory);
        FastJsonRedisSerializer<Object> serializer = new FastJsonRedisSerializer<>(Object.class);
        //使用StringRedisSerializer来序列化和反序列化redis的key值
        //通过更改默认的序列化器避免在查看的时候Redis中的数据呈现乱码状态
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);

        //Hash的key也采用StringRedisSerializer的序列化方式
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;
    }
}
