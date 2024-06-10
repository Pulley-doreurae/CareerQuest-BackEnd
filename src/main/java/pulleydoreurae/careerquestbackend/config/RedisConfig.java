package pulleydoreurae.careerquestbackend.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisKeyValueAdapter;
import org.springframework.data.redis.core.RedisKeyValueTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.mapping.RedisMappingContext;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import pulleydoreurae.careerquestbackend.chat.RedisSubscriber;

/**
 * Redis 설정 파일
 *
 * @author : parkjihyeok
 * @since : 2024/01/21
 */

@Configuration
public class RedisConfig {

	private final String redisHost;
	private final int redisPort;
	private final String redisChatHost;
	private final int redisChatPort;

	public RedisConfig(@Value("${spring.data.redis.host}") String redisHost,
			@Value("${spring.data.redis.port}") int redisPort,
			@Value("${spring.data.redis.chat_host}") String redisChatHost,
			@Value("${spring.data.redis.chat_port}") int redisChatPort) {
		this.redisHost = redisHost;
		this.redisPort = redisPort;
		this.redisChatHost = redisChatHost;
		this.redisChatPort = redisChatPort;
	}

	@Bean("redisConnectionFactory")
	public RedisConnectionFactory redisConnectionFactory() {
		return new LettuceConnectionFactory(redisHost, redisPort);
	}

	@Bean("redisTemplate")
	public RedisTemplate<Object, Object> redisTemplate(@Qualifier("redisConnectionFactory") RedisConnectionFactory redisConnectionFactory) {
		RedisTemplate<Object, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(redisConnectionFactory);
		return template;
	}

	@Bean
	public StringRedisTemplate stringRedisTemplate(@Qualifier("redisTemplate") RedisTemplate<Object, Object> redisTemplate) {
		return new StringRedisTemplate(redisTemplate.getConnectionFactory());
	}

	@Bean
	public RedisMappingContext redisMappingContext() {
		return new RedisMappingContext();
	}

	@Bean
	public RedisKeyValueAdapter redisKeyValueAdapter(@Qualifier("redisTemplate") RedisTemplate<Object, Object> redisTemplate) {
		return new RedisKeyValueAdapter(redisTemplate);
	}

	@Bean
	public RedisKeyValueTemplate redisKeyValueTemplate(RedisKeyValueAdapter redisKeyValueAdapter, RedisMappingContext redisMappingContext) {
		return new RedisKeyValueTemplate(redisKeyValueAdapter, redisMappingContext);
	}


	@Bean(name = "redisChatConnectionFactory")
	public RedisConnectionFactory redisChatConnectionFactory() {
		return new LettuceConnectionFactory(redisChatHost, redisChatPort);
	}

	@Bean(name = "redisChatTemplate")
	public RedisTemplate<String, Object> redisChatTemplate(@Qualifier("redisChatConnectionFactory") RedisConnectionFactory connectionFactory) {
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(connectionFactory);
		template.setKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
		template.setHashKeySerializer(new StringRedisSerializer());
		template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
		template.afterPropertiesSet();

		return template;
	}

	@Bean
	public ChannelTopic channelTopic() {
		return new ChannelTopic("chatroom");
	}

	/**
	 * redis 에 발행(publish)된 메시지 처리를 위한 리스너 설정
	 */
	@Bean
	public RedisMessageListenerContainer redisMessageListener (
		MessageListenerAdapter listenerAdapterChatMessage,
		ChannelTopic channelTopic
	) {
		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(redisConnectionFactory());
		container.addMessageListener(listenerAdapterChatMessage, channelTopic);
		return container;
	}

	/** 실제 메시지를 처리하는 subscriber 설정 추가*/
	@Bean
	public MessageListenerAdapter listenerAdapterChatMessage(RedisSubscriber subscriber) {
		return new MessageListenerAdapter(subscriber, "sendMessage");
	}

	@Bean
	public RedisMessageListenerContainer redisMessageListenerRoomList (
		MessageListenerAdapter listenerAdapterChatRoomList,
		ChannelTopic channelTopic
	) {
		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(redisConnectionFactory());
		container.addMessageListener(listenerAdapterChatRoomList, channelTopic);
		return container;
	}


	/** 실제 메시지 방을 처리하는 subscriber 설정 추가*/
	@Bean
	public MessageListenerAdapter listenerAdapterChatRoomList(RedisSubscriber subscriber) {
		return new MessageListenerAdapter(subscriber, "sendRoomList");
	}
}
