package com.payware.business.repository;

import com.payware.domain.ExchangeRate;
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
public class ExchangeRateRepository extends AbstractRepository<Long, ExchangeRate> {

    @Inject
    public ExchangeRateRepository(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public void deleteAll() {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.createQuery("DELETE FROM ExchangeRate").executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            log.warn("Error ", e);
            assert transaction != null;
            transaction.rollback();
        }
    }

    @Transactional
    public ExchangeRate findByBase(String base) {
        Transaction transaction = sessionFactory.getCurrentSession().getTransaction();
        Optional<ExchangeRate> exchangeRate = Optional.empty();
        try {
            transaction.begin();
            Query<ExchangeRate> findByBase = sessionFactory.getCurrentSession()
                .createNamedQuery("Exchange.findByBase", ExchangeRate.class);
            exchangeRate = Optional.of(findByBase.setParameter("base", base).getSingleResult());
            transaction.commit();
        } catch (NoResultException e) {
            transaction.rollback();
            log.warn("Not Found: {}", e.getMessage());
        }

        return exchangeRate.orElse(null);
    }

    public boolean isEmpty() {
        Transaction transaction = sessionFactory.getCurrentSession().getTransaction();
        Long count = null;
        try {
            transaction.begin();
            Query<Long> countQuery = sessionFactory.getCurrentSession()
                .createNamedQuery("Exchange.count", Long.class);
            count = countQuery.getSingleResult();
            transaction.commit();
        } catch (NoResultException e) {
            transaction.rollback();
            log.warn("Not Found: {}", e.getMessage());
        }

        return count != null && count == 0;
    }
}
