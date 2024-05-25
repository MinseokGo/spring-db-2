package hello.itemservice.repository.jpa;

import static hello.itemservice.domain.QItem.item;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import hello.itemservice.domain.Item;
import hello.itemservice.repository.ItemRepository;
import hello.itemservice.repository.ItemSearchCondition;
import hello.itemservice.repository.ItemUpdateDto;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Repository
@Transactional(readOnly = true)
public class JpaItemRepositoryV3 implements ItemRepository {

    private final EntityManager entityManager;
    private final JPAQueryFactory queryFactory;

    public JpaItemRepositoryV3(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public Item save(Item item) {
        entityManager.persist(item);
        return item;
    }

    @Override
    public void update(Long id, ItemUpdateDto updateParam) {
        Item item = entityManager.find(Item.class, id);
        item.setItemName(updateParam.itemName());
        item.setPrice(updateParam.price());
        item.setQuantity(updateParam.quantity());
    }

    @Override
    public Optional<Item> findById(Long id) {
        Item item = entityManager.find(Item.class, id);
        return Optional.ofNullable(item);
    }

    @Override
    public List<Item> findAll(ItemSearchCondition condition) {
        String itemName = condition.itemName();
        Integer maxPrice = condition.maxPrice();

        return queryFactory.select(item)
                .from(item)
                .where(likeItemName(itemName), maxPrice(maxPrice))
                .fetch();
    }

    private BooleanExpression likeItemName(String itemName) {
        if (StringUtils.hasText(itemName)) {
            return item.itemName.like("%" + itemName + "%");
        }
        return null;
    }

    private BooleanExpression maxPrice(Integer maxPrice) {
        if (maxPrice != null) {
            return item.price.loe(maxPrice);
        }
        return null;
    }

    public List<Item> findAllOld(ItemSearchCondition condition) {
        String itemName = condition.itemName();
        Integer maxPrice = condition.maxPrice();

        // QItem item = QItem.item;
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if (StringUtils.hasText(itemName)) {
            booleanBuilder.and(item.itemName.like("%" + itemName + "%"));
        }
        if (maxPrice != null) {
            booleanBuilder.and(item.price.loe(maxPrice));
        }

        return queryFactory.select(item)
                .from(item)
                .where(booleanBuilder)
                .fetch();
    }
}
