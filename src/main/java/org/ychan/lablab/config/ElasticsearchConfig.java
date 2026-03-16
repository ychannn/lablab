package org.ychan.lablab.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackages = "org.ychan.lablab.repository")
public class ElasticsearchConfig {
}
