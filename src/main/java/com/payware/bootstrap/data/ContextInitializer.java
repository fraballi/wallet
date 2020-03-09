package com.payware.bootstrap.data;

import com.google.common.collect.ImmutableList;
import com.payware.business.repository.BankAccountRepository;
import com.payware.business.repository.BankRepository;
import com.payware.business.repository.CardHolderRepository;
import com.payware.business.repository.CardRepository;
import com.payware.business.repository.CountryRepository;
import com.payware.business.repository.ExchangeRateRepository;
import com.payware.business.repository.TransferRepository;
import com.payware.business.service.CountryService;
import com.payware.business.service.CurrencyProcessor;
import com.payware.business.service.ExchangeRateService;
import com.payware.domain.Address;
import com.payware.domain.Bank;
import com.payware.domain.BankAccount;
import com.payware.domain.Card;
import com.payware.domain.Card.CardType;
import com.payware.domain.Card.PaymentNetwork;
import com.payware.domain.CardHolder;
import com.payware.domain.ContactInfo;
import com.payware.domain.Country;
import com.payware.domain.ExchangeRate;
import com.payware.domain.Transfer;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ContextInitializer {

  private final CountryRepository countryRepository;
  private final ExchangeRateRepository exchangeRateRepository;
  private final BankRepository bankRepository;
  private final BankAccountRepository bankAccountRepository;
  private final CardRepository cardRepository;
  private final CardHolderRepository cardHolderRepository;
  private final TransferRepository transferRepository;

  private List<Country> countries = new ArrayList<>();
  private List<ExchangeRate> exchangeRates = new ArrayList<>();
  private List<Bank> banks = new ArrayList<>();
  private List<BankAccount> bankAccounts = new ArrayList<>();
  private List<Card> cards = new ArrayList<>();
  private List<CardHolder> cardHolders = new ArrayList<>();
  private List<Transfer> transfers = new ArrayList<>();

  private ExchangeRateService exchangeRateService;
  private CountryService countryService;

  @Inject
  public ContextInitializer(CountryRepository countryRepository,
      ExchangeRateRepository exchangeRateRepository,
      BankRepository bankRepository, BankAccountRepository bankAccountRepository,
      CardRepository cardRepository, CardHolderRepository cardHolderRepository,
      TransferRepository transferRepository,
      ExchangeRateService exchangeRateService,
      CountryService countryService) {

    this.countryRepository = countryRepository;
    this.exchangeRateRepository = exchangeRateRepository;
    this.bankRepository = bankRepository;
    this.bankAccountRepository = bankAccountRepository;
    this.cardRepository = cardRepository;
    this.cardHolderRepository = cardHolderRepository;
    this.transferRepository = transferRepository;
    this.exchangeRateService = exchangeRateService;
    this.countryService = countryService;
  }

  public void init() {
    createCountries();
    createExchangeRates();
    createBanks();
    createBankAccounts();
    createCards();
    createCardHolders();
    createTransfers();
  }

  private void createCountries() {
    try {
      countries.addAll(countryService.get());
    } catch (IOException e) {
      log.warn("[Api Exception] Initializing countries: " + e.getMessage());
    }

    if (countries.isEmpty()) {
      countries.addAll(ImmutableList.of(
          Country.builder().name("United Kingdom").build(),
          Country.builder().name("Germany").build(),
          Country.builder().name("United States").build()
      ));
    }
    countryRepository.save(countries);
  }

  private void createExchangeRates() {
    ExchangeRate apiRates = null;
    try {
      apiRates = exchangeRateService.get();
    } catch (IOException e) {
      log.warn("[Api Exception] Initializing exchange rates: " + e.getMessage());
    }

    if (Objects.nonNull(apiRates)) {
      apiRates.getRates().putIfAbsent(CurrencyProcessor.DEFAULT_BASE, new BigDecimal(1));
      exchangeRates.add(apiRates);
    } else {
      Map<String, BigDecimal> euroRates = new HashMap<>();
      euroRates.put("USD", new BigDecimal(1.1104));
      euroRates.put("GBP", new BigDecimal(0.8907));
      euroRates.put("EUR", new BigDecimal(1));

      Map<String, BigDecimal> usdRates = new HashMap<>();
      usdRates.put("EUR", new BigDecimal(0.8896));
      usdRates.put("GBP", new BigDecimal(0.6699));
      usdRates.put("USD", new BigDecimal(1));

      Map<String, BigDecimal> gbpRates = new HashMap<>();
      gbpRates.put("EUR", new BigDecimal(1.1093));
      gbpRates.put("USD", new BigDecimal(1.3301));
      usdRates.put("GBP", new BigDecimal(1));

      exchangeRates.addAll(ImmutableList.of(
          ExchangeRate.builder().base("GBP").rates(gbpRates).build(),
          ExchangeRate.builder().base("EUR").rates(euroRates).build(),
          ExchangeRate.builder().base("USD").rates(usdRates).build()
      ));
    }
    if (exchangeRateRepository.isEmpty())
      exchangeRateRepository.save(exchangeRates);
  }

  private void createBanks() {
    Optional<Country> unitedKingdom = countries.stream()
        .filter(c -> c.getName().contains("United Kingdom")).findFirst();
    Optional<Country> germany = countries.stream()
        .filter(c -> c.getName().contains("Germany")).findFirst();
    Optional<Country> unitedStates = countries.stream()
        .filter(c -> c.getName().contains("United States of America")).findFirst();

    banks.addAll(ImmutableList.of(
        Bank.builder().branding("Barclays").country(unitedKingdom.get()).contactInfo(
            ContactInfo.builder()
                .address(Address.builder().city("London").street1("Crown Court").build()).build())
            .build(),
        Bank.builder().branding("Deutsche Bank").country(germany.get()).contactInfo(
            ContactInfo.builder()
                .address(Address.builder().city("Berlin").street1("Karl-Marx-Allee").build())
                .build())
            .build(),
        Bank.builder().branding("Citi Bank").country(unitedStates.get()).contactInfo(
            ContactInfo.builder()
                .address(Address.builder().city("New York").street1("5th Avenue").build()).build())
            .build()
    ));

    bankRepository.save(banks);
  }

  private void createBankAccounts() {
    Random rnd = new Random();
    bankAccounts.addAll(ImmutableList.of(
        BankAccount.builder().amount(new BigDecimal(500)).bank(banks.get(0))
            .number(rnd.nextLong()).currency(Currency.getInstance(Locale.UK)).build(),
        BankAccount.builder().amount(new BigDecimal(1000)).bank(banks.get(1))
            .number(rnd.nextLong()).currency(Currency.getInstance(Locale.GERMANY)).build(),
        BankAccount.builder().amount(new BigDecimal(10000)).bank(banks.get(1))
            .number(rnd.nextLong()).currency(Currency.getInstance(Locale.US)).build()
    ));

    bankAccountRepository.save(bankAccounts);
  }

  private void createCards() {
    Random rnd = new Random();
    cards.addAll(ImmutableList.of(
        Card.builder().pan(rnd.nextLong()).securityCode(rnd.nextInt(4))
            .bankAccount(bankAccounts.get(0))
            .cardType(CardType.DEBIT)
            .paymentNetwork(
                PaymentNetwork.VISA).cvv(rnd.nextInt(3))
            .expiryDate(LocalDate.now().plusDays(50)).build(),
        Card.builder().pan(rnd.nextLong()).securityCode(rnd.nextInt(4))
            .bankAccount(bankAccounts.get(1))
            .cardType(CardType.DEBIT)
            .paymentNetwork(
                PaymentNetwork.VISA).cvv(rnd.nextInt(3))
            .expiryDate(LocalDate.now().plusDays(100)).build(),
        Card.builder().pan(rnd.nextLong()).securityCode(rnd.nextInt(4))
            .bankAccount(bankAccounts.get(2))
            .cardType(CardType.DEBIT)
            .paymentNetwork(
                PaymentNetwork.VISA).cvv(rnd.nextInt(3))
            .expiryDate(LocalDate.now().plusDays(150)).build()));

    cardRepository.save(cards);
  }

  private void createCardHolders() {
    cardHolders.addAll(ImmutableList.of(
        CardHolder.builder().cardSet(Collections.singleton(cards.get(0))).name("Charles")
            .lastName("Chaplin").age(30).contactInfo(
            ContactInfo.builder()
                .address(Address.builder().city("London").street1("East Street").build())
                .build()).build(),
        CardHolder.builder().cardSet(Collections.singleton(cards.get(1))).name("Karl")
            .lastName("Valentin").age(40).contactInfo(
            ContactInfo.builder()
                .address(Address.builder().city("Munich").street1("Planegg").build())
                .build()).build(),
        CardHolder.builder().cardSet(Collections.singleton(cards.get(2))).name("Joseph Frank")
            .lastName("Keaton").age(50).contactInfo(
            ContactInfo.builder()
                .address(Address.builder().city("Kansas").street1("Piqua").build())
                .build()).build()
    ));

    cardHolderRepository.save(cardHolders);
  }

  private void createTransfers() {
    transfers.addAll(ImmutableList.of(
        Transfer.builder().amount(new BigDecimal(100)).card1(cards.get(0))
            .card2(cards.get(1)).build()));

    transferRepository.save(transfers);
  }
}
