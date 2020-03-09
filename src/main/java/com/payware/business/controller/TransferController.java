package com.payware.business.controller;

import com.payware.business.service.TransferService;
import com.payware.domain.Transfer;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import java.util.Objects;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TransferController {

    private TransferService transferService;

    @Inject
    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    public void findBy(Context ctx) {
        final long id = Long.parseLong(Objects.requireNonNull(ctx.pathParam("id")));
        Transfer entity = transferService.find(id);
        if (Objects.nonNull(entity)) {
            ctx.json(entity);
        } else {
            throw new NotFoundResponse("Entity not found");
        }
    }

    public void findAll(Context ctx) {
        ctx.json(transferService.findAll());
    }
}
