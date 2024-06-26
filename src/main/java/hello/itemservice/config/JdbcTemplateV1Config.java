package hello.itemservice.config;

import hello.itemservice.repository.ItemRepository;
import hello.itemservice.repository.jdbctemplate.JdbcTemplateItemRepositoryV1;
import hello.itemservice.service.ItemService;
import hello.itemservice.service.ItemServiceV1;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
@RequiredArgsConstructor
public class JdbcTemplateV1Config {

    private final DataSource dataSource;

    @Bean
    public DriverManagerDataSource dataSource() {
        return new DriverManagerDataSource();
    }

    @Bean
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public ItemRepository itemRepository() {
        return new JdbcTemplateItemRepositoryV1(jdbcTemplate());
    }

    @Bean
    public ItemService itemService() {
        return new ItemServiceV1(itemRepository());
    }
}
