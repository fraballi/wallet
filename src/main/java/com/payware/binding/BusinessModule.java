package com.payware.binding;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.payware.business.controller.BankAccountsController;
import com.payware.business.controller.BankController;
import com.payware.business.controller.CardController;
import com.payware.business.controller.CardHolderController;
import com.payware.business.controller.SourcesController;
import com.payware.business.controller.TransferController;
import com.payware.business.repository.BankAccountRepository;
import com.payware.business.repository.BankRepository;
import com.payware.business.repository.CardHolderRepository;
import com.payware.business.repository.CardRepository;
import com.payware.business.repository.ContactInfoRepository;
import com.payware.business.repository.CountryRepository;
import com.payware.business.repository.ExchangeRateRepository;
import com.payware.business.repository.TransferRepository;
import com.payware.business.routing.AppRouting;
import com.payware.business.routing.BankAccountRouting;
import com.payware.business.routing.BankRouting;
import com.payware.business.routing.CardHolderRouting;
import com.payware.business.routing.CardRouting;
import com.payware.business.routing.Routing;
import com.payware.business.routing.SourcesRouting;
import com.payware.business.routing.TransferRouting;
import com.payware.business.service.BankService;
import com.payware.business.service.CardHolderService;
import com.payware.business.service.CardProcessor;
import com.payware.business.service.CountryService;
import com.payware.business.service.CurrencyProcessor;
import com.payware.business.service.ExchangeRateService;
import com.payware.business.service.TransferService;

public class BusinessModule extends AbstractModule {

    @Override
    protected void configure() {
        services();
        repositories();
        controllers();
        routing();
    }

    private void services() {
        bind(TransferService.class);
        bind(CardHolderService.class);
        bind(CountryService.class);
        bind(ExchangeRateService.class);
        bind(BankService.class);
    }

    private void repositories() {
        bind(TransferRepository.class);
        bind(ExchangeRateRepository.class);
        bind(CardRepository.class);
        bind(CardHolderRepository.class);
        bind(BankRepository.class);
        bind(BankAccountRepository.class);
        bind(CountryRepository.class);
        bind(ContactInfoRepository.class);

        bind(CardProcessor.class);
        bind(CurrencyProcessor.class);
    }

    private void controllers() {
        bind(TransferController.class);
        bind(CardController.class);
        bind(CardHolderController.class);
        bind(SourcesController.class);
        bind(BankController.class);
        bind(BankAccountsController.class);
    }

    private void routing() {
        Multibinder<Routing> routesBinder = Multibinder.newSetBinder(binder(), Routing.class);
        routesBinder.addBinding().to(AppRouting.class);
        routesBinder.addBinding().to(TransferRouting.class);
        routesBinder.addBinding().to(CardRouting.class);
        routesBinder.addBinding().to(CardHolderRouting.class);
        routesBinder.addBinding().to(SourcesRouting.class);
        routesBinder.addBinding().to(BankRouting.class);
        routesBinder.addBinding().to(BankAccountRouting.class);
    }
}
