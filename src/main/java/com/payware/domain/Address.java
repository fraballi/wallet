package com.payware.domain;

import lombok.*;

import javax.persistence.Embeddable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
@ToString
public
class Address {

    private String street1;
    private String street2;
    private String city;
}
