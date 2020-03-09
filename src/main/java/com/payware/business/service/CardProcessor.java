package com.payware.business.service;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.payware.business.repository.CardHolderRepository;
import com.payware.business.repository.CardRepository;
import com.payware.business.repository.TransferRepository;
import com.payware.domain.BankAccount;
import com.payware.domain.Card;
import com.payware.domain.CardHolder;
import com.payware.domain.Transfer;
import com.payware.domain.dto.CardTransferDTO;
import com.payware.domain.dto.HolderTransferDTO;
import com.payware.domain.dto.TransferResultDTO;
import com.payware.domain.dto.TransferResultDTO.Builder;
import com.payware.logging.Transferable;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.function.BiConsumer;
import javax.transaction.Transactional;
import org.apache.commons.lang3.tuple.Triple;

public class CardProcessor {

    private final CardRepository cardRepository;
    private final CardHolderRepository cardHolderRepository;
    private final TransferRepository transferRepository;

    private CurrencyProcessor currencyProcessor;

    /**
     * Process transfer operation and updates amounts for sender/receiver
     */
    private BiConsumer<Triple<BankAccount, BankAccount, BigDecimal>, TransferResultDTO.Builder> transactionConsumer = (tuple, builder) -> {
        /* Set decrement */
        BigDecimal senderFinalAmount = tuple.getLeft().getAmount().subtract(tuple.getRight());
        tuple.getLeft().setAmount(senderFinalAmount);
        builder.senderAccountId(tuple.getLeft().getId()).senderAmount(senderFinalAmount);
        /* Set increment */
        BigDecimal receiverFinalAmount = tuple.getMiddle().getAmount().add(tuple.getRight());
        tuple.getMiddle().setAmount(receiverFinalAmount);
        builder.receiverAccountId(tuple.getMiddle().getId()).receiverAmount(receiverFinalAmount);
    };

    @Inject
    public CardProcessor(CardRepository cardRepository,
        CardHolderRepository cardHolderRepository, TransferRepository transferRepository,
        CurrencyProcessor currencyProcessor) {
        this.cardRepository = cardRepository;
        this.cardHolderRepository = cardHolderRepository;
        this.transferRepository = transferRepository;
        this.currencyProcessor = currencyProcessor;
    }

    @Transactional
    private void processTransaction(Builder result, Card c1, Card c2, BigDecimal convertedAmount) {
        transactionConsumer
            .accept(
                Triple.of(c1.getBankAccount(), c2.getBankAccount(), convertedAmount),
                result);

        transferRepository
            .save(Transfer.builder().amount(convertedAmount)
                .card1(c1).card2(c2).build());

        cardRepository.update(c1);
        cardRepository.update(c2);
        result.transactionStatus(true);
    }

    /**
     * Transfer amount between cards. Checks available amount in sender before transfer execution
     *
     * @param transfer {@link CardTransferDTO}
     * @return result {@link TransferResultDTO} Returns transaction result. If operation finishes successfully, 'transactionStatus' = true.
     */
    @Transactional
    @Transferable
    TransferResultDTO verify(CardTransferDTO transfer) {
        Preconditions.checkNotNull(transfer);

        TransferResultDTO.Builder result = TransferResultDTO.builder();
        Optional<Card> card1 = Optional.ofNullable(cardRepository
            .findByPan(transfer.getPan1()));

        card1.ifPresent(c1 -> {
            Optional<Card> card2 = Optional.ofNullable(cardRepository
                .findByPan(transfer.getPan2()));

            card2.ifPresent(c2 -> {
                BigDecimal convertedAmount = currencyProcessor
                    .convert(c1.getBankAccount().getCurrency(), c2.getBankAccount().getCurrency(),
                        transfer.getAmount());

                if (c1.getBankAccount().getAmount().compareTo(convertedAmount) >= 0) {
                    processTransaction(result, c1, c2, convertedAmount);
                }
            });
        });
        return result.build();
    }

    /**
     * Transfer amount between card holders from/into first available cards. Checks available amount
     * in sender before transfer execution
     *
     * @param transfer {@link HolderTransferDTO}
     * @return result {@link TransferResultDTO}
     */
    @Transactional
    @Transferable
    TransferResultDTO verify(HolderTransferDTO transfer) {
        Preconditions.checkNotNull(transfer);

        TransferResultDTO.Builder result = TransferResultDTO.builder();
        Optional<CardHolder> cardHolder1 = Optional.of(cardHolderRepository
            .find(CardHolder.class, transfer.getCardHolder1()));

        cardHolder1.ifPresent(ch1 -> {
            Optional<CardHolder> cardHolder2 = Optional.of(cardHolderRepository
                .find(CardHolder.class, transfer.getCardHolder2()));

            cardHolder2.ifPresent(ch2 -> {
                Optional<Card> card2 = ch2.getCardSet().stream().findFirst();
                card2.ifPresent(c2 -> {
                    Optional<Card> card1 = ch1.getCardSet().stream()
                        .filter(a -> a.getBankAccount().getAmount().compareTo(currencyProcessor
                            .convert(a.getBankAccount().getCurrency(),
                                c2.getBankAccount().getCurrency(),
                                transfer.getAmount())) >= 0)
                        .findAny();

                    card1.ifPresent(c1 -> {
                        BigDecimal convertedAmount = currencyProcessor
                            .convert(c1.getBankAccount().getCurrency(),
                                c2.getBankAccount().getCurrency(),
                                transfer.getAmount());

                        processTransaction(result, c1, c2, convertedAmount);
                    });
                });
            });
        });
        return result.build();
    }
}
