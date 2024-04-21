package hello.itemservice.repository.jdbctemplate;

import hello.itemservice.domain.Item;
import hello.itemservice.repository.ItemRepository;
import hello.itemservice.repository.ItemSearchCondition;
import hello.itemservice.repository.ItemUpdateDto;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.util.StringUtils;

@Slf4j
@RequiredArgsConstructor
public class JdbcTemplateItemRepositoryV1 implements ItemRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Item save(Item item) {
        String sql = "insert into item (item_name, price, quantity) values (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql, new String[]{"id"});
            statement.setString(1, item.getItemName());
            statement.setInt(2, item.getPrice());
            statement.setInt(3, item.getQuantity());
            return statement;
        }, keyHolder);

        long key = Objects.requireNonNull(keyHolder.getKey()).longValue();
        item.setId(key);

        return item;
    }

    @Override
    public void update(Long itemId, ItemUpdateDto updateParam) {
        String sql = "update item set item_name=?, price=?, quantity=? where id=?";
        jdbcTemplate.update(sql,
                updateParam.itemName(),
                updateParam.price(),
                updateParam.quantity(),
                itemId);
    }

    @Override
    public Optional<Item> findById(Long id) {
        String sql = "select id, item_name, price, quantity from item where id = ?";
        try {
            Item item = jdbcTemplate.queryForObject(sql, itemRowMapper(), id);
            return Optional.of(Objects.requireNonNull(item));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Item> findAll(ItemSearchCondition condition) {
        String itemName = condition.itemName();
        Integer maxPrice = condition.maxPrice();

        String sql = "select id, item_name, price, quantity from item";
        if (StringUtils.hasText(itemName) || maxPrice != null) {
            sql += " where";
        }

        boolean andFlag = false;
        List<Object> param = new ArrayList<>();
        if (StringUtils.hasText(itemName)) {
            sql += " item_name like concat('%',?,'%')";
            param.add(itemName);
            andFlag = true;
        }
        if (maxPrice != null) {
            if (andFlag) {
                sql += " and";
            }
            sql += " price <= ?";
            param.add(maxPrice);
        }
        log.info("sql={}", sql);

        return jdbcTemplate.query(sql, itemRowMapper(), param.toArray());
    }
    private RowMapper<Item> itemRowMapper() {
        return (resultSet, rowNumber) -> {
            Item item = new Item();
            item.setId(resultSet.getLong("id"));
            item.setItemName(resultSet.getString("item_name"));
            item.setPrice(resultSet.getInt("price"));
            item.setQuantity(resultSet.getInt("quantity"));
            return item;
        };
    }
}
