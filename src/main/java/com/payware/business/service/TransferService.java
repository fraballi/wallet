package com.payware.business.service;

import com.google.inject.Inject;
import com.payware.business.repository.TransferRepository;
import com.payware.domain.Transfer;
import com.payware.domain.dto.CardTransferDTO;
import com.payware.domain.dto.HolderTransferDTO;
import com.payware.domain.dto.TransferResultDTO;
import com.payware.logging.Transferable;
import java.util.List;

public class TransferService {

    private final CardProcessor cardProcessor;
    private final TransferRepository transferRepository;

    @Inject
    public TransferService(CardProcessor cardProcessor, TransferRepository transferRepository) {
        this.cardProcessor = cardProcessor;
        this.transferRepository = transferRepository;
    }

    @Transferable
    public TransferResultDTO charge(HolderTransferDTO transfer) {
        return cardProcessor.verify(transfer);
    }

    @Transferable
    public TransferResultDTO charge(CardTransferDTO transfer) {
        return cardProcessor.verify(transfer);
    }

    public Transfer find(long id) {
        return transferRepository.find(Transfer.class, id);
    }

    public List<Transfer> findAll() {
        return transferRepository.findAll();
    }
}
