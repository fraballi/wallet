package com.payware.business.service;

import com.payware.business.repository.BankAccountRepository;
import javax.inject.Inject;

public class BankAccountService {

  private BankAccountRepository bankAccountRepository;

  @Inject
  public BankAccountService(
      BankAccountRepository bankAccountRepository) {
    this.bankAccountRepository = bankAccountRepository;
  }
}
