package com.payware.business.repository;

import com.payware.domain.Card;
import java.util.Optional;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

@Slf4j
public class CardRepository extends AbstractRepository<Long, Card> {

    @Inject
    public CardRepository(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public void deleteAll() {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.createQuery("DELETE FROM Card").executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            log.warn("Error ", e);
            assert transaction != null;
            transaction.rollback();
        }
    }

    @Transactional
    public Card findByPan(long pan) {
        Transaction transaction = sessionFactory.getCurrentSession().getTransaction();
        Optional<Card> card = Optional.empty();
        try {
            transaction.begin();
            Query<Card> findByPan = sessionFactory.getCurrentSession()
                .createNamedQuery("Card.findByPan", Card.class);
            card = Optional.of(findByPan.setParameter("pan", pan).getSingleResult());
            transaction.commit();
        } catch (NoResultException e) {
            transaction.rollback();
            log.warn("Not Found: {}", e.getMessage());
        }

        return card.orElse(null);
    }
}
