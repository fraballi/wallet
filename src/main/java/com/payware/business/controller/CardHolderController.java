package com.payware.business.controller;

import com.payware.business.service.CardHolderService;
import com.payware.business.service.TransferService;
import com.payware.domain.BankAccount;
import com.payware.domain.Card;
import com.payware.domain.CardHolder;
import com.payware.domain.dto.HolderTransferDTO;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.transaction.Transactional;

@Singleton
public class CardHolderController {
    private CardHolderService cardHolderService;
    private TransferService transferService;

    @Inject
    public CardHolderController(CardHolderService cardHolderService,
        TransferService transferService) {
        this.cardHolderService = cardHolderService;
        this.transferService = transferService;
    }

    public void balanceById(Context ctx) {
        final long id = Long.parseLong(Objects.requireNonNull(ctx.pathParam("id")));
            CardHolder cardHolder = cardHolderService.find(id);
        if (Objects.nonNull(cardHolder)) {
            Map<Long, BigDecimal> result = cardHolder.getCardSet().stream().collect(Collectors
                .toMap(Card::getId,
                    entry -> entry.getBankAccount().getAmount()));
            ctx.json(result);
        } else {
            throw new NotFoundResponse("Entity not found");
        }
    }

    public void findBy(Context ctx) {
        final long id = Long.parseLong(Objects.requireNonNull(ctx.pathParam("id")));
        CardHolder entity = cardHolderService.find(id);
        if (Objects.nonNull(entity)) {
            ctx.json(entity);
        } else {
            throw new NotFoundResponse("Entity not found");
        }
    }

    public void findAll(Context ctx) {
        ctx.json(cardHolderService.findAll());
    }

    public void cardsById(Context ctx) {
        final long id = Long.parseLong(Objects.requireNonNull(ctx.pathParam("id")));
        CardHolder entity = cardHolderService.find(id);
        if (Objects.nonNull(entity)) {
            Map<Long, Card> result = entity.getCardSet().stream().collect(Collectors
                .toMap(Card::getPan, entry -> entry));
            ctx.json(result);
        } else {
            throw new NotFoundResponse("Entity not found");
        }
    }

    public void accountsById(Context ctx) {
        final long id = Long.parseLong(Objects.requireNonNull(ctx.pathParam("id")));
        CardHolder entity = cardHolderService.find(id);
        if (Objects.nonNull(entity)) {
            Map<Long, BankAccount> result = entity.getCardSet().stream().collect(Collectors
                .toMap(Card::getPan, Card::getBankAccount));
            ctx.json(result);
        } else {
            throw new NotFoundResponse("Entity not found");
        }
    }

    @Transactional
    public void transfer(Context ctx) {
        final Optional<HolderTransferDTO> transfer = Optional
            .of(ctx.bodyAsClass(HolderTransferDTO.class));
        transfer.ifPresent(t -> {
            ctx.json(transferService.charge(t));
        });
    }
}
