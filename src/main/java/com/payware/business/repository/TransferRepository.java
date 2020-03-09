package com.payware.business.repository;

import com.payware.domain.Card;
import com.payware.domain.Transfer;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

@Slf4j
public class TransferRepository extends AbstractRepository<Long, Transfer> {

    @Inject
    public TransferRepository(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Transactional
    public List<Transfer> findAll() {
        List<Transfer> entities = new ArrayList<>();
        try (Session session = sessionFactory.openSession()) {
            TypedQuery<Tuple> query = session.createQuery(
                "SELECT t.card1, t.card2, t.amount, t.created FROM Transfer t",
                Tuple.class);

            List<Tuple> tuples = query.getResultList();
            entities.addAll(tuples.stream().map(t -> {
                Card card1 = (Card) t.get(0);
                Card card2 = (Card) t.get(1);
                BigDecimal amount = new BigDecimal(String.valueOf(t.get(2)));
                LocalDateTime created = (LocalDateTime) t.get(3);

                return Transfer.builder().card1(card1)
                    .card2(card2).amount(amount).created(created).build();

            }).collect(Collectors.toList()));
        } catch (Exception e) {
            log.warn("Error ", e);
        }
        return entities;
    }


    @Override
    public void deleteAll() {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.createQuery("DELETE FROM Transfer").executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            log.warn("Error ", e);
            assert transaction != null;
            transaction.rollback();
        }
    }
}
