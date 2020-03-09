package com.payware.business.repository;

import com.payware.domain.CardHolder;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import javax.inject.Inject;

@Slf4j
public class CardHolderRepository extends AbstractRepository<Long, CardHolder> {

    @Inject
    public CardHolderRepository(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public void deleteAll() {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.createQuery("DELETE FROM CardHolder").executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            log.warn("Error ", e);
            assert transaction != null;
            transaction.rollback();
        }
    }
}
