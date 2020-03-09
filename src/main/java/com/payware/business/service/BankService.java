package com.payware.business.service;

import com.payware.business.repository.BankRepository;
import com.payware.domain.Bank;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class BankService {

  private BankRepository bankRepository;

  @Inject
  public BankService(BankRepository bankRepository) {
    this.bankRepository = bankRepository;
  }

  public List<Bank> findAll() {
    return new ArrayList<>(bankRepository.findAll(Bank.class));
  }

  public Bank find(long id) {
    return bankRepository.find(Bank.class, id);
  }
}
