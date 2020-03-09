package com.payware.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MapKeyColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@NamedQueries(value = {
    @NamedQuery(name = "Exchange.count", query = "SELECT COUNT(e) FROM ExchangeRate e"),
    @NamedQuery(name = "Exchange.findByBase", query = "SELECT e FROM ExchangeRate e WHERE e.base=:base")
})
public class ExchangeRate {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String base;

    private String date;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "Currency_Rate")
    @MapKeyColumn(name = "Currency")
    @Column(name = "Rate")
    private Map<String, BigDecimal> rates = new HashMap<>();

    @CreationTimestamp
    private LocalDateTime created;

    @UpdateTimestamp
    private LocalDateTime updated;
}
