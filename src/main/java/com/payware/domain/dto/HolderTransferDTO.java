package com.payware.domain.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class HolderTransferDTO {

  private long cardHolder1;
  private long cardHolder2;
  private BigDecimal amount;
}
