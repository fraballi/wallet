# Wallet - Money Transfer

### Demo Application for Money Transfers using **Javalin Micro-framework**

| Author                      | Email                 |
| --------------------------- | --------------------- |
| Felix Roberto Aballi Morell | felixaballi@gmail.com |

---

### **Dependencies**

| Dependency                   | Version         |
| ---------------------------- | --------------- |
| _**Javalin**_                | 3.7.0           |
| _Java AdoptOpenJDK_          | 8.0.242.hs-adpt |
| _**Google Guice**_               | 4.2.2           |
| _Google Guice Multibindings_ | 4.2.2           |
| _Google Gson_                 | 2.8.5           |
| _Slf4j_                      | 1.8.0-beta      |
| _**Cfg4j**_                  | 4.4.0           |
| _Lombok_                     | 1.18.12         |
| _Jackson_                    | 2.10.3          |
| _**H2**_                     | 1.4.200         |
| _**Hibernate**_              | 5.4.1.Final     |
| _Hibernate Validator_        | 6.1.2.Final     |
| _Apache Http Client_         | 4.5.12          |
| _JUnit Jupiter_              | 5.6.0           |
| _JUnit_                      | 4.13            |
| _Mockito_                    | 3.3.0           |
| _Unirest_                    | 3.4.00          |
| _AssertJ_                    | 3.11.1          |

### **Application Configuration**

> **File**: src/main/resources/**application.yml**

```yaml
javalin:
  name: Wallet Application
  port: 7000
  contextPath: /api/v1
  exchangeRateService: https://api.exchangeratesapi.io/latest
  countriesService: https://restcountries.eu/rest/v2/all
```
>**Note:** 
> Two external services were used on providing more realistic approaches: **api.exchangeratesapi.io** and **restcountries.eu**. 
>
> When a money transfer takes place: 
> 1) a ***BankAccount#java.util.Currency currency*** plays a role during money conversion
> ***e.g Code**: 'GBP' into 'EUR', 'GBP' into 'USD' or 'EUR' into 'EUR'*, real exchange rates are used for that purpose.
> 2) The *Base Rate* for the demonstration is: **EUR** equals **1**

## API Endpoints

> @see **application.yml**
>
> ### Context Path: **_/api/v1_**

#### Health Check

_Note: Useful for Microservices Heartbeats on Registry/Discovery/Circuit Breaker (**e.g Apache Zookeeper, Netflix OSS Eureka or Hashicorp Consul**)_

> | Path          | Method |
> | ------------- | ------ |
> | _**/health**_ | GET    |

#### Countries

> @see **SourcesRouting.class**
> | Path | Method |
> | ------------ | ------ |
> | _/countries_ | GET |
> | _/countries/:id_ | GET |
> | _/rest/countries_ | GET |

#### Exchange Rates

> @see **SourcesRouting.class**
> | Path | Method |
> | ---- | ------ |
> | _/rates_ | GET |
> | _/rates/:id_ | GET |
> | _/rest/rates_ | GET |

#### Banks

> @see **BankRouting.class**
> | Path | Method |
> | ---- | ------ |
> | _/banks_ | GET |
> | _/banks/:id_ | GET |

#### Bank Accounts

> @see **BankAccountRouting.class**
> | Path | Method |
> | ---- | ------ |
> | _/accounts_ | GET |
> | _/accounts/:id_ | GET |

#### Cards

> @see **CardRouting.class**
> | Path | Method |
> | ---- | ------ |
> | _/cards_ | GET |
> | _/cards/:id_ | GET |
> | _/cards/:id/balance_ | GET |
> | _/cards/:id/account_ | GET |
> | _/cards/:pan/pan_ | GET |
> | _/cards/:pan/pan/balance_ | GET |
> | _/cards/:transfer_ | **POST** |

#### Card Holders

> @see **BankAccountRouting.class**
> | Path | Method |
> | ---- | ------ |
> | _/holders_ | GET |
> | _/holders/:id_ | GET |
> | _/holders/:id/balance_ | GET |
> | _/holders/:id/cards_ | GET |
> | _/holders/:id/accounts_ | GET |
> | _/holders/:transfer_ | **POST** |

#### Transfers

> @see **TransferRouting.class**
> | Path | Method |
> | ---- | ------ |
> | _/transfers_ | GET |
> | _/transfers/:id_ | GET |

---

## Tests

> ### Particular interest: 
> - **CardTests#checkTransfer**
> - **CardTests#checkBalanceByPan**
> - **CardHolderTests#checkTransfer**
> - **CardHolderTests#checkBalanceById**

> | Folder                                   | Library                  | Test Type               |
> | ---------------------------------------- | ------------------------ | ----------------------- |
> | \_/src/test/java/.../JpaTests            | JUnit                    | Functional, Integration |
> | \_/src/test/java/.../IntegrationTests    | JUnit5, AssertJ, Unirest | Integration             |
> | \_/src/test/java/.../FunctionalTests404  | JUnit5, AssertJ, Unirest | Functional              |
> | **\_/src/test/java/.../CardTests**       | JUnit5, AssertJ, Unirest | Functional              |
> | **\_/src/test/java/.../CardHolderTests** | JUnit5, AssertJ, Unirest | Functional              |
