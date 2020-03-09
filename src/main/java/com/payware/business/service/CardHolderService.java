package com.payware.business.service;

import com.payware.business.repository.CardHolderRepository;
import com.payware.domain.CardHolder;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class CardHolderService {

    private final CardHolderRepository cardHolderRepository;

    @Inject
    public CardHolderService(CardHolderRepository cardHolderRepository) {
        this.cardHolderRepository = cardHolderRepository;
    }

    public CardHolder find(long id) {
        return cardHolderRepository.find(CardHolder.class, id);
    }

  public List<CardHolder> findAll() {
    return new ArrayList<>(cardHolderRepository.findAll(CardHolder.class));
  }
}
