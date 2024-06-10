package pulleydoreurae.careerquestbackend.chat;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pulleydoreurae.careerquestbackend.chat.domain.MessageSubDto;

@RequiredArgsConstructor
@Service
@Slf4j
public class RedisPublisher {

	private final ChannelTopic channelTopic;

	@Resource(name = "redisChatTemplate")
	private final RedisTemplate redisTemplate;

	public void publish(MessageSubDto messageSubDto){
		log.info("RedisPublisher publishing .. {}", messageSubDto.getChatMessageDto().getMessage());
		redisTemplate.convertAndSend(channelTopic.getTopic(), messageSubDto);
	}

}
