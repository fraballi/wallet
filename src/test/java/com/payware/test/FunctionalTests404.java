package com.payware.test;

import static org.assertj.core.api.Assertions.assertThat;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.junit.jupiter.api.Test;

class FunctionalTests404 extends AbstractTests {

  @Test
  void checkCountry404() {
    HttpResponse<String> response = Unirest.get(api + "/countries/1000000").asString();
    assertThat(response.getStatus()).isEqualTo(404);

    assertThat(response.getBody()).containsIgnoringCase("Entity not found");
  }

  @Test
  void checkExchangeRate404() {
    HttpResponse<String> response = Unirest.get(api + "/rates/1000000").asString();
    assertThat(response.getStatus()).isEqualTo(404);

    assertThat(response.getBody()).containsIgnoringCase("Entity not found");
  }

  @Test
  void checkCardHolder404() {
    HttpResponse<String> response = Unirest.get(api + "/holders/1000000").asString();
    assertThat(response.getStatus()).isEqualTo(404);

    assertThat(response.getBody()).containsIgnoringCase("Entity not found");
  }

  @Test
  void checkCardById404() {
    HttpResponse<String> response = Unirest.get(api + "/cards/1000000").asString();
    assertThat(response.getStatus()).isEqualTo(404);

    assertThat(response.getBody()).containsIgnoringCase("Entity not found");
  }

  @Test
  void checkCardByPan404() {
    HttpResponse<String> response = Unirest.get(api + "/cards/1000000/pan").asString();
    assertThat(response.getStatus()).isEqualTo(404);

    assertThat(response.getBody()).containsIgnoringCase("Entity not found");
  }

  @Test
  void checkBank404() {
    HttpResponse<String> response = Unirest.get(api + "/banks/1000000").asString();
    assertThat(response.getStatus()).isEqualTo(404);

    assertThat(response.getBody()).containsIgnoringCase("Entity not found");
  }

  @Test
  void checkBankAccount404() {
    HttpResponse<String> response = Unirest.get(api + "/accounts/1000000").asString();
    assertThat(response.getStatus()).isEqualTo(404);

    assertThat(response.getBody()).containsIgnoringCase("Entity not found");
  }

  @Test
  void checkTransfer404() {
    HttpResponse<String> response = Unirest.get(api + "/transfers/1000000").asString();
    assertThat(response.getStatus()).isEqualTo(404);

    assertThat(response.getBody()).containsIgnoringCase("Entity not found");
  }
}
