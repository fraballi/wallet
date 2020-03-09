package com.payware.domain.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder(builderClassName = "Builder")
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TransferResultDTO {

  private long senderAccountId;
  private long receiverAccountId;

  private BigDecimal senderAmount;
  private BigDecimal receiverAmount;

  private boolean transactionStatus;
}
