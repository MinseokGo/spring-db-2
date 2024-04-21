package hello.itemservice.repository.jdbctemplate;

import hello.itemservice.domain.Item;
import hello.itemservice.repository.ItemRepository;
import hello.itemservice.repository.ItemSearchCondition;
import hello.itemservice.repository.ItemUpdateDto;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.util.StringUtils;

@Slf4j
@RequiredArgsConstructor
public class JdbcTemplateItemRepositoryV2 implements ItemRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public Item save(Item item) {
        String sql = "insert into item (item_name, price, quantity) "
                + "values (:itemName, :price, :quantity)";

        SqlParameterSource sqlParameterSource = new BeanPropertySqlParameterSource(item);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, sqlParameterSource, keyHolder);

        long key = Objects.requireNonNull(keyHolder.getKey()).longValue();
        item.setId(key);

        return item;
    }

    @Override
    public void update(Long itemId, ItemUpdateDto updateParam) {
        String sql = "update item "
                + "set item_name=:itemName, price=:price, quantity=:quantity "
                + "where id=:id";

        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("itemName", updateParam.itemName())
                .addValue("price", updateParam.price())
                .addValue("quantity", updateParam.quantity())
                .addValue("id", itemId);
        jdbcTemplate.update(sql, sqlParameterSource);
    }

    @Override
    public Optional<Item> findById(Long id) {
        String sql = "select id, item_name, price, quantity from item where id = :id";
        try {
            Map<String, Object> paramMap = Map.of("id", id);
            Item item = jdbcTemplate.queryForObject(sql, paramMap, itemRowMapper());
            return Optional.of(Objects.requireNonNull(item));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Item> findAll(ItemSearchCondition condition) {
        String itemName = condition.itemName();
        Integer maxPrice = condition.maxPrice();

        SqlParameterSource sqlParameterSource = new BeanPropertySqlParameterSource(condition);

        String sql = "select id, item_name, price, quantity from item";
        if (StringUtils.hasText(itemName) || maxPrice != null) {
            sql += " where";
        }

        boolean andFlag = false;
        if (StringUtils.hasText(itemName)) {
            sql += " item_name like concat('%',:itemName,'%')";
            andFlag = true;
        }
        if (maxPrice != null) {
            if (andFlag) {
                sql += " and";
            }
            sql += " price <= :maxPrice";
        }
        log.info("sql={}", sql);

        return jdbcTemplate.query(sql, sqlParameterSource, itemRowMapper());
    }
    private RowMapper<Item> itemRowMapper() {
        return BeanPropertyRowMapper.newInstance(Item.class);
    }
}
