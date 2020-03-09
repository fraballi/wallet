package com.payware.business.controller;

import com.payware.business.repository.BankAccountRepository;
import com.payware.domain.BankAccount;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import java.util.Objects;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class BankAccountsController {

  private BankAccountRepository bankAccountRepository;

  @Inject
  public BankAccountsController(
      BankAccountRepository bankAccountRepository) {
    this.bankAccountRepository = bankAccountRepository;
  }

  public void findBy(Context ctx) {
    final long id = Long.parseLong(Objects.requireNonNull(ctx.pathParam("id")));
    BankAccount entity = bankAccountRepository.find(BankAccount.class, id);
    if (Objects.nonNull(entity)) {
      ctx.json(entity);
    } else {
      throw new NotFoundResponse("Entity not found");
    }
  }

  public void findAll(Context ctx) {
    ctx.json(bankAccountRepository.findAll(BankAccount.class));
  }
}
