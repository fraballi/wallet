package com.payware.business.controller;

import com.payware.business.service.BankService;
import com.payware.domain.Bank;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import java.util.Objects;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class BankController {

  private BankService bankService;

  @Inject
  public BankController(BankService bankService) {
    this.bankService = bankService;
  }

  public void findBy(Context ctx) {
    final long id = Long.parseLong(Objects.requireNonNull(ctx.pathParam("id")));
    Bank entity = bankService.find(id);
    if (Objects.nonNull(entity)) {
      ctx.json(entity);
    } else {
      throw new NotFoundResponse("Entity not found");
    }
  }

  public void findAll(Context ctx) {
    ctx.json(bankService.findAll());
  }
}
