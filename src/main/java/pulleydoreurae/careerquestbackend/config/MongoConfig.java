package pulleydoreurae.careerquestbackend.config;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import com.mongodb.MongoClientSettings;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

@Configuration
public class MongoConfig {

	private final String MongoHost;
	private final int MongoPort;
	private final String MongoDB;

	public MongoConfig(@Value("${spring.data.mongodb.host}") String MongoHost,
		@Value("${spring.data.mongodb.port}") int MongoPort,
		@Value("${spring.data.mongodb.db}") String MongoDB) {
		this.MongoHost = MongoHost;
		this.MongoPort = MongoPort;
		this.MongoDB = MongoDB;
	}

	@Bean
	public MongoClient mongoClient() {
		MongoClientSettings settings = MongoClientSettings.builder()
			.applyToClusterSettings(builder ->
				builder.hosts(Collections.singletonList(new ServerAddress(MongoHost, MongoPort))))
			.build();
		return MongoClients.create(settings);
	}

	@Bean
	public MongoTemplate mongoTemplate() {
		SimpleMongoClientDatabaseFactory factory = new SimpleMongoClientDatabaseFactory(mongoClient(), MongoDB);
		MappingMongoConverter converter = new MappingMongoConverter(factory, new MongoMappingContext());
		converter.setTypeMapper(new DefaultMongoTypeMapper(null));
		return new MongoTemplate(factory, converter);
	}
}
