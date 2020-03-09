package com.payware.business.service;

import com.payware.domain.ExchangeRate;
import com.payware.logging.Convertable;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Triple;

@Slf4j
public class CurrencyProcessor {

  public static final String DEFAULT_BASE = "EUR";
  private ExchangeRateService exchangeRateService;

  @Inject
  public CurrencyProcessor(ExchangeRateService exchangeRateService) {
    this.exchangeRateService = exchangeRateService;
  }

  /**
   * Gets new amount from currency conversion. If codes are equal to BASE, returns same amount. If
   * codes are different from BASE but equal each other, returns deduction referencing the first of
   * them
   *
   * @param triple {@link Triple} Contains <Code 1, Code 2, Amount>
   * @param rates {@link Map} Available exchange rates from BASE
   * @return amount {@link BigDecimal} Returns new amount
   */
  public static BigDecimal calculate(Triple<String, String, BigDecimal> triple,
      Map<String, BigDecimal> rates) {
    BigDecimal baseRate = new BigDecimal(1);
    BigDecimal code1Rate = rates.get(triple.getLeft());
    BigDecimal code2Rate = rates.get(triple.getMiddle());
    BigDecimal transferAmount = triple.getRight();
    BigDecimal diff1 = baseRate.subtract(code1Rate);
    BigDecimal diff2 = baseRate.subtract(code2Rate);
    boolean areBothBase = Stream.of(triple.getLeft(), triple.getMiddle())
        .allMatch(c -> c.equals(DEFAULT_BASE));

    /* CODES: NOT BASE, NO conversion */
    if (areBothBase) {
      return transferAmount;
    }

    BigDecimal amountIfBaseGrEqThanSender = baseRate.subtract(diff1).multiply(transferAmount);
    /* CODES: EQUAL */
    if (triple.getLeft().equals(triple.getMiddle())) {
      return (diff1.signum() == -1) ? baseRate.add(diff1).multiply(transferAmount)
          : amountIfBaseGrEqThanSender;
    } else {
      BigDecimal amountIfCode1GrEqThanBase = diff1.abs().add(baseRate).multiply(transferAmount);
      BigDecimal amountIfCode2GrEqThanBase = diff2.abs().add(baseRate).multiply(transferAmount);
      BigDecimal amountIfBaseGrEqThanReceiver = baseRate.subtract(diff2).multiply(transferAmount);
      BigDecimal amountFinalSender =
          (diff1.signum() == -1) ? amountIfCode1GrEqThanBase : amountIfBaseGrEqThanSender;
      BigDecimal amountFinalReceiver =
          (diff2.signum() == -1) ? amountIfCode2GrEqThanBase : amountIfBaseGrEqThanReceiver;
      /* CODES: NOT EQUAL */
      if (!triple.getLeft().equals(DEFAULT_BASE) && triple.getMiddle().equals(DEFAULT_BASE)) {
        /* Code1 */
        return amountFinalSender;
      } else {
        /* CODES: NOT BASE & NOT EQUAL, takes as reference the destination exchange rate */
        return amountFinalReceiver;
      }
    }
  }

  /**
   * Gets amount converted from currency codes
   *
   * @param var1 {@link Currency}
   * @param var2 {@link Currency}
   * @return amount {@link BigDecimal} Returns converted amount after currency calculation (if
   * needed)
   */
  @Convertable
  BigDecimal convert(Currency var1, Currency var2,
      BigDecimal amount) {

    if (var1.equals(var2)) {
      return amount;
    }

    Optional<ExchangeRate> byBase = Optional
        .ofNullable(exchangeRateService.findByBase(DEFAULT_BASE));

    return byBase.map(ExchangeRate::getRates).map(rates -> {
      List<String> transactionCodes = Stream
          .of(var1.getCurrencyCode(), var2.getCurrencyCode(), DEFAULT_BASE).distinct()
          .collect(Collectors.toList());

      rates.putIfAbsent(DEFAULT_BASE, new BigDecimal(1));

      if (rates.keySet().containsAll(transactionCodes)) {
        final String code1 = var1.getCurrencyCode();
        final String code2 = var2.getCurrencyCode();
        return calculate(Triple.of(code1, code2, amount), rates);
      }
      return amount;
    }).orElse(amount);
  }
}
