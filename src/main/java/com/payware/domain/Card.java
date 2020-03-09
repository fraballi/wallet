package com.payware.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.validator.constraints.Range;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@NamedQuery(name = "Card.findByPan", query = "SELECT c FROM Card c WHERE c.pan =:pan")
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Range(min = 8, max = 19)
    @Column(length = 19)
    private long pan;
    @Range(min = 3, max = 3)
    @Column(length = 3)
    private int cvv;
    @Range(min = 4, max = 4)
    @Column(length = 4)
    private int securityCode;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", referencedColumnName = "id")
    private BankAccount bankAccount;

    private PaymentNetwork paymentNetwork;

    private CardType cardType;

    private LocalDate expiryDate;

    @CreationTimestamp
    private LocalDateTime created;

    @UpdateTimestamp
    private LocalDateTime updated;

    public enum PaymentNetwork {
        VISA, MASTER_CARD, AMERICAN_EXPRESS, DISCOVER, INTERLINK, STAR, INTERAC, PULSE, JDB, READY_LINK;
    }

    public enum CardType {
        DEBIT, CREDIT, PRE_PAID;
    }
}
