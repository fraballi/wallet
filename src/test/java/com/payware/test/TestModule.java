package com.payware.test;

import com.google.inject.AbstractModule;
import com.payware.business.repository.CardHolderRepository;
import com.payware.business.repository.CardRepository;
import com.payware.business.repository.CountryRepository;
import com.payware.util.HibernateUtil;
import org.hibernate.SessionFactory;

public class TestModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(SessionFactory.class).toInstance(HibernateUtil.getSessionFactory());
        bind(CountryRepository.class);
        bind(CardRepository.class);
        bind(CardHolderRepository.class);
    }
}
