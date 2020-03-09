package com.payware.business.controller;

import com.payware.business.service.CardService;
import com.payware.business.service.TransferService;
import com.payware.domain.BankAccount;
import com.payware.domain.Card;
import com.payware.domain.dto.CardTransferDTO;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.transaction.Transactional;

@Singleton
public class CardController {

  private CardService cardService;
  private TransferService transferService;

  @Inject
  public CardController(CardService cardService, TransferService transferService) {
    this.cardService = cardService;
    this.transferService = transferService;
  }

  public void findBy(Context ctx) {
    final long id = Long.parseLong(Objects.requireNonNull(ctx.pathParam("id")));
    Card entity = cardService.find(id);
    if (Objects.nonNull(entity)) {
      ctx.json(entity);
    } else {
      throw new NotFoundResponse("Entity not found");
    }
  }

  public void findAll(Context ctx) {
    ctx.json(cardService.findAll());
  }

  public void balanceById(Context ctx) {
    final long id = Long.parseLong(Objects.requireNonNull(ctx.pathParam("id")));
    Card entity = cardService.find(id);
    if (Objects.nonNull(entity)) {
      Map<Long, BigDecimal> balance = new HashMap<>();
      balance.put(entity.getId(), entity.getBankAccount().getAmount());
      ctx.json(balance);
    } else {
      throw new NotFoundResponse("Entity not found");
    }
  }

  public void accountById(Context ctx) {
    final long id = Long.parseLong(Objects.requireNonNull(ctx.pathParam("id")));
    Card entity = cardService.find(id);
    if (Objects.nonNull(entity)) {
      Map<Long, BankAccount> balance = new HashMap<>();
      balance.put(entity.getId(), entity.getBankAccount());
      ctx.json(balance);
    } else {
      throw new NotFoundResponse("Entity not found");
    }
  }

  @Transactional
  public void transfer(Context ctx) {
    final Optional<CardTransferDTO> transfer = Optional.of(ctx.bodyAsClass(CardTransferDTO.class));
    transfer.ifPresent(t -> ctx.json(transferService.charge(t)));
  }

  public void findByPan(Context ctx) {
    final long pan = Long.parseLong(Objects.requireNonNull(ctx.pathParam("pan")));
    Card entity = cardService.findByPan(pan);
    if (Objects.nonNull(entity)) {
      ctx.json(entity);
    } else {
      throw new NotFoundResponse("Entity not found");
    }
  }

  public void balanceByPan(Context ctx) {
    final long pan = Long.parseLong(Objects.requireNonNull(ctx.pathParam("pan")));
    Card entity = cardService.findByPan(pan);
    if (Objects.nonNull(entity)) {
      Map<Long, BigDecimal> balance = new HashMap<>();
      balance.put(entity.getPan(), entity.getBankAccount().getAmount());
      ctx.json(balance);
    } else {
      throw new NotFoundResponse("Entity not found");
    }
  }
}
