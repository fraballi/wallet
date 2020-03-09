package com.payware.test;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Guice;
import com.payware.business.repository.CardRepository;
import com.payware.business.repository.ExchangeRateRepository;
import com.payware.business.service.CurrencyProcessor;
import com.payware.domain.Card;
import com.payware.domain.ExchangeRate;
import com.payware.domain.dto.CardTransferDTO;
import com.payware.domain.dto.TransferResultDTO;
import com.payware.util.HibernateProxyTypeAdapter;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Triple;
import org.junit.jupiter.api.Test;

@Slf4j
class CardTests extends AbstractTests {

  private CardRepository cardRepository;
  private ExchangeRateRepository exchangeRateRepository;

  @Test
  void checkCardExists() {
    cardRepository = Guice.createInjector(new TestModule()).getInstance(CardRepository.class);

    Collection<Card> cards = cardRepository.findAll(Card.class);
    assertThat(cards).isNotEmpty();

    Optional<Card> first = cards.stream().findFirst();
    assertThat(first.isPresent()).isTrue();

    first.ifPresent(c -> {
      HttpResponse<String> response = Unirest.get(api + "/cards/" + c.getId())
          .asString();

      assertThat(response.getStatus()).isEqualTo(200);
      assertThat(response.getBody()).isNotEmpty().contains(String.valueOf(c.getId()));
    });
  }

  @Test
  void checkBalanceById() {
    cardRepository = Guice.createInjector(new TestModule()).getInstance(CardRepository.class);

    Collection<Card> cards = cardRepository.findAll(Card.class);
    assertThat(cards).isNotEmpty();

    Optional<Card> first = cards.stream().findFirst();
    assertThat(first.isPresent()).isTrue();

    first.ifPresent(c -> {
      HttpResponse<String> response = Unirest.get(api + "/cards/" + c.getId() + "/balance")
          .asString();

      assertThat(response.getStatus()).isEqualTo(200);
      assertThat(response.getBody()).isNotEmpty().contains(String.valueOf(c.getId()));
    });
  }

  @Test
  void checkBalanceByPan() {
    cardRepository = Guice.createInjector(new TestModule()).getInstance(CardRepository.class);

    Collection<Card> cards = cardRepository.findAll(Card.class);
    assertThat(cards).isNotEmpty();

    Optional<Card> first = cards.stream().findFirst();
    assertThat(first.isPresent()).isTrue();

    first.ifPresent(c -> {
      HttpResponse<String> response = Unirest.get(api + "/cards/" + c.getPan() + "/pan/balance")
          .asString();

      assertThat(response.getStatus()).isEqualTo(200);
      assertThat(response.getBody()).isNotEmpty().contains(String.valueOf(c.getPan()));
    });
  }

  @Test
  void checkAccountById() {
    cardRepository = Guice.createInjector(new TestModule()).getInstance(CardRepository.class);

    Collection<Card> cards = cardRepository.findAll(Card.class);
    assertThat(cards).isNotEmpty();

    Optional<Card> first = cards.stream().findFirst();
    assertThat(first.isPresent()).isTrue();

    first.ifPresent(c -> {
      HttpResponse<String> response = Unirest.get(api + "/cards/" + c.getId() + "/account")
          .asString();

      assertThat(response.getStatus()).isEqualTo(200);

      GsonBuilder b = new GsonBuilder();
      b.registerTypeAdapterFactory(HibernateProxyTypeAdapter.FACTORY);
      Gson gson = b.create();

      Map responseMap = gson.fromJson(response.getBody(), Map.class);

      assertThat(responseMap.keySet().stream().map(k -> Long.parseLong(String.valueOf(k)))
          .mapToLong(k -> (long) k).boxed().collect(toList())).contains(c.getId());
    });
  }

  @Test
  void checkTransfer() {
    cardRepository = Guice.createInjector(new TestModule()).getInstance(CardRepository.class);
    exchangeRateRepository = Guice.createInjector(new TestModule())
        .getInstance(ExchangeRateRepository.class);

    List<Card> cards = cardRepository.findAll(Card.class).stream().limit(2).collect(toList());
    assertThat(cards).isNotEmpty().hasSize(2);

    GsonBuilder b = new GsonBuilder();
    b.registerTypeAdapterFactory(HibernateProxyTypeAdapter.FACTORY);
    Gson gson = b.create();

    final BigDecimal transferAmount = new BigDecimal(100);
    Card card1 = cards.get(0);
    Card card2 = cards.get(1);

    BigDecimal senderInitialAmount = card1.getBankAccount().getAmount();
    BigDecimal receiverInitialAmount = card2.getBankAccount().getAmount();

    assertThat(senderInitialAmount).isGreaterThan(new BigDecimal(0));

    HttpResponse<String> response = Unirest
        .post(api + "/cards/transfer")
        .body(gson.toJson(
            CardTransferDTO.builder().amount(transferAmount)
                .pan1(card1.getPan())
                .pan2(card2.getPan()).build()))
        .asString();

    assertThat(response.getStatus()).isEqualTo(200);
    assertThat(response.getBody()).isNotEmpty().containsIgnoringCase("status");

    TransferResultDTO resultDTO = gson.fromJson(response.getBody(), TransferResultDTO.class);
    assertThat(resultDTO.isTransactionStatus()).isTrue();

    assertThat(resultDTO.getSenderAmount()).isLessThanOrEqualTo(senderInitialAmount);
    assertThat(resultDTO.getReceiverAmount()).isGreaterThanOrEqualTo(receiverInitialAmount);

    final String senderCode = card1.getBankAccount().getCurrency().getCurrencyCode();
    final String receiverCode = card2.getBankAccount().getCurrency().getCurrencyCode();

    ExchangeRate byBase = exchangeRateRepository.findByBase(CurrencyProcessor.DEFAULT_BASE);
    assertThat(byBase).isNotNull();

    Map<String, BigDecimal> rates = Collections.unmodifiableMap(byBase.getRates());

    BigDecimal convertedAmount = CurrencyProcessor
        .calculate(Triple.of(senderCode, receiverCode, transferAmount), rates);

    assertThat(resultDTO.getSenderAmount())
        .isEqualTo(senderInitialAmount.subtract(convertedAmount));
    assertThat(resultDTO.getReceiverAmount()).isEqualTo(receiverInitialAmount.add(convertedAmount));

    log.info("Cards Transfer: {}", response.getBody());
  }
}
