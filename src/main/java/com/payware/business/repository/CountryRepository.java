package com.payware.business.repository;

import com.payware.domain.Country;
import java.util.List;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

@Slf4j
public class CountryRepository extends AbstractRepository<Long, Country> {

    @Inject
    public CountryRepository(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public void deleteAll() {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.createQuery("DELETE FROM Country").executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            log.warn("Error ", e);
            assert transaction != null;
            transaction.rollback();
        }
    }
}
