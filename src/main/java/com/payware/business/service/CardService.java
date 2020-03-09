package com.payware.business.service;

import com.payware.business.repository.CardRepository;
import com.payware.domain.Card;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class CardService {

  private final CardRepository cardRepository;

  @Inject
  public CardService(CardRepository cardRepository) {
    this.cardRepository = cardRepository;
  }

  public Card find(long id) {
    return cardRepository.find(Card.class, id);
  }

  public Card findByPan(long pan) {
    return cardRepository.findByPan(pan);
  }

  public List<Card> findAll() {
    return new ArrayList<>(cardRepository.findAll(Card.class));
  }
}
