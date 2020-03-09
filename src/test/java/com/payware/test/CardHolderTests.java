package com.payware.test;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Guice;
import com.payware.business.repository.CardHolderRepository;
import com.payware.business.repository.ExchangeRateRepository;
import com.payware.business.service.CurrencyProcessor;
import com.payware.domain.Card;
import com.payware.domain.CardHolder;
import com.payware.domain.ExchangeRate;
import com.payware.domain.dto.HolderTransferDTO;
import com.payware.domain.dto.TransferResultDTO;
import com.payware.util.HibernateProxyTypeAdapter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Triple;
import org.junit.jupiter.api.Test;

@Slf4j
class CardHolderTests extends AbstractTests {

    private CardHolderRepository cardHolderRepository;
  private ExchangeRateRepository exchangeRateRepository;

    @Test
    void checkHolderExists() {
        cardHolderRepository = Guice.createInjector(new TestModule())
            .getInstance(CardHolderRepository.class);

        Collection<CardHolder> holders = cardHolderRepository.findAll(CardHolder.class);
        assertThat(holders).isNotEmpty();

        Optional<CardHolder> first = holders.stream().findFirst();
        assertThat(first.isPresent()).isTrue();

        first.ifPresent(h -> {
            HttpResponse<String> response = Unirest.get(api + "/holders/" + h.getId())
                .asString();

            assertThat(response.getStatus()).isEqualTo(200);
            assertThat(response.getBody()).isNotEmpty().contains(String.valueOf(h.getId()));
        });
    }

    @Test
    void checkHolderBalanceById() {
        cardHolderRepository = Guice.createInjector(new TestModule())
            .getInstance(CardHolderRepository.class);

        Collection<CardHolder> holders = cardHolderRepository.findAll(CardHolder.class);
        assertThat(holders).isNotEmpty();

        Optional<CardHolder> first = holders.stream().findFirst();
        assertThat(first.isPresent()).isTrue();

        first.ifPresent(h -> {
            HttpResponse<String> response = Unirest.get(api + "/holders/" + h.getId() + "/balance")
                .asString();

            assertThat(response.getStatus()).isEqualTo(200);

            GsonBuilder b = new GsonBuilder();
            b.registerTypeAdapterFactory(HibernateProxyTypeAdapter.FACTORY);
            Gson gson = b.create();

            Map responseMap = gson.fromJson(response.getBody(), Map.class);

            List<Long> ids = h.getCardSet().stream().map(Card::getId).collect(toList());
            assertThat(ids)
                .containsAll(
                    responseMap.keySet().stream().map(k -> Long.parseLong(String.valueOf(k)))
                        .mapToLong(k -> (long) k).boxed().collect(toList()));
        });
    }

    @Test
    void checkCardsById() {
        cardHolderRepository = Guice.createInjector(new TestModule())
            .getInstance(CardHolderRepository.class);

        Collection<CardHolder> holders = cardHolderRepository.findAll(CardHolder.class);
        assertThat(holders).isNotEmpty();

        Optional<CardHolder> first = holders.stream().findFirst();
        assertThat(first.isPresent()).isTrue();

        first.ifPresent(h -> {
            HttpResponse<String> response = Unirest.get(api + "/holders/" + h.getId() + "/cards")
                .asString();

            assertThat(response.getStatus()).isEqualTo(200);

            GsonBuilder b = new GsonBuilder();
            b.registerTypeAdapterFactory(HibernateProxyTypeAdapter.FACTORY);
            Gson gson = b.create();

            Map responseMap = gson.fromJson(response.getBody(), Map.class);

            List<Long> pans = h.getCardSet().stream().map(Card::getPan).collect(toList());
            assertThat(pans)
                .containsAll(
                    responseMap.keySet().stream().map(k -> Long.parseLong(String.valueOf(k)))
                        .mapToLong(k -> (long) k).boxed().collect(toList()));

        });
    }

    @Test
    void checkAccountsById() {
        cardHolderRepository = Guice.createInjector(new TestModule())
            .getInstance(CardHolderRepository.class);

        Collection<CardHolder> holders = cardHolderRepository.findAll(CardHolder.class);
        assertThat(holders).isNotEmpty();

        Optional<CardHolder> first = holders.stream().findFirst();
        assertThat(first.isPresent()).isTrue();

        first.ifPresent(h -> {
            HttpResponse<String> response = Unirest.get(api + "/holders/" + h.getId() + "/accounts")
                .asString();

            assertThat(response.getStatus()).isEqualTo(200);

            GsonBuilder b = new GsonBuilder();
            b.registerTypeAdapterFactory(HibernateProxyTypeAdapter.FACTORY);
            Gson gson = b.create();

            Map responseMap = gson.fromJson(response.getBody(), Map.class);

            List<Long> pans = h.getCardSet().stream().map(Card::getPan).collect(toList());
            assertThat(pans)
                .containsAll(
                    responseMap.keySet().stream().map(k -> Long.parseLong(String.valueOf(k)))
                        .mapToLong(k -> (long) k).boxed().collect(toList()));

        });
    }

    @Test
    void checkTransfer() {
        cardHolderRepository = Guice.createInjector(new TestModule())
            .getInstance(CardHolderRepository.class);
      exchangeRateRepository = Guice.createInjector(new TestModule())
          .getInstance(ExchangeRateRepository.class);

        List<CardHolder> holders = cardHolderRepository.findAll(CardHolder.class).stream().limit(2)
            .collect(toList());
        assertThat(holders).isNotEmpty().hasSize(2);

        GsonBuilder b = new GsonBuilder();
        b.registerTypeAdapterFactory(HibernateProxyTypeAdapter.FACTORY);
        Gson gson = b.create();

        final BigDecimal transferAmount = new BigDecimal(100);
        CardHolder holder1 = holders.get(0);
        CardHolder holder2 = holders.get(1);
        BigDecimal senderInitialAmount = new ArrayList<>(holder1.getCardSet()).get(0)
            .getBankAccount()
            .getAmount();
        BigDecimal receiverInitialAmount = new ArrayList<>(holder2.getCardSet()).get(0)
            .getBankAccount()
            .getAmount();

        HttpResponse<String> response = Unirest
            .post(api + "/holders/transfer")
            .body(gson.toJson(
                HolderTransferDTO.builder().amount(transferAmount)
                    .cardHolder1(holder1.getId())
                    .cardHolder2(holder2.getId()).build()))
            .asString();

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getBody()).isNotEmpty().containsIgnoringCase("status");

        TransferResultDTO resultDTO = gson.fromJson(response.getBody(), TransferResultDTO.class);
        assertThat(resultDTO.isTransactionStatus()).isTrue();

        Optional<Card> senderCard = holder1.getCardSet().stream()
            .filter(c -> resultDTO.getSenderAccountId() == c.getBankAccount().getId())
            .findFirst();
        assertThat(senderCard).isNotNull();

        Optional<Card> receiverCard = holder2.getCardSet().stream()
            .filter(c -> resultDTO.getReceiverAccountId() == c.getBankAccount().getId())
            .findFirst();
        assertThat(receiverCard).isNotNull();

      ExchangeRate byBase = exchangeRateRepository.findByBase(CurrencyProcessor.DEFAULT_BASE);
      assertThat(byBase).isNotNull();

      Map<String, BigDecimal> rates = Collections.unmodifiableMap(byBase.getRates());

      final String senderCode = senderCard.get().getBankAccount().getCurrency().getCurrencyCode();
      final String receiverCode = receiverCard.get().getBankAccount().getCurrency()
          .getCurrencyCode();
      BigDecimal convertedAmount = CurrencyProcessor
          .calculate(Triple
              .of(senderCode, receiverCode,
                  transferAmount), rates);

      assertThat(resultDTO.getSenderAmount())
          .isEqualTo(senderInitialAmount.subtract(convertedAmount));
      assertThat(resultDTO.getReceiverAmount())
          .isEqualTo(receiverInitialAmount.add(convertedAmount));

        log.info("Holders Transfer: {}", response.getBody());
    }
}
