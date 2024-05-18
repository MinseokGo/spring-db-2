package hello.itemservice.repository.jpa;

import hello.itemservice.domain.Item;
import hello.itemservice.repository.ItemRepository;
import hello.itemservice.repository.ItemSearchCondition;
import hello.itemservice.repository.ItemUpdateDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaItemRepository implements ItemRepository {

    private final EntityManager entityManager;

    @Override
    @Transactional
    public Item save(Item item) {
        entityManager.persist(item);
        return item;
    }

    @Override
    @Transactional
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
    public List<Item> findAll(ItemSearchCondition cond) {
        String jpql = "select i from Item i";

        String itemName = cond.itemName();
        Integer maxPrice = cond.maxPrice();

        if (StringUtils.hasText(itemName) || maxPrice != null) {
            jpql += " where";
        }

        boolean andFlag = false;
        List<Object> params = new ArrayList<>();
        if (StringUtils.hasText(itemName)) {
            jpql += " i.itemName like concat('%', :itemName, '%')";
            params.add(itemName);
            andFlag = true;
        }

        if (maxPrice != null) {
            if (andFlag) {
                jpql += " and";
            }
            jpql += " i.price <= :maxPrice";
            params.add(maxPrice);
        }
        log.info("jpql={}", jpql);

        TypedQuery<Item> query = entityManager.createQuery(jpql, Item.class);
        if (StringUtils.hasText(itemName)) {
            query.setParameter("itemName", itemName);
        }
        if (maxPrice != null) {
            query.setParameter("maxPrice", maxPrice);
        }

        return query.getResultList();
    }
}
