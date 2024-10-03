# 1. Zadaća

## Razvoj skalabilnog produkcijskog IKT sustava

**Zadatak:** U javi implementirajte Spring Boot aplikaciju za rad sa semantičkim verzijama definiranim u [_Semantic Versioning 2.0.0_ specifikaciji](https://semver.org/spec/v2.0.0.html).

### Funkcionalni zahtjevi

Aplikacija mora implementirati `GET` HTTP metodu na dvije (2) putanje (_path_) koje primaju svoje ulazne parametre kroz _query string_:

1. `GET /versions/max` koja prima 2 parametra: `v1` i `v2` čije vrijednosti su semantičke verzije. API vraća veću od dvije verzije.
2. `GET /versions/next` koja prima 2 parametra: semantičku verziju `v` i `type` koji može biti: `MAJOR`, `MINOR` ili `PATCH`. API vraća iduću release verziju nakon `v` dobivenu povećavanjem pozicije `type`.

Aplikacija mora očekivati HTTP upite (_request_) na localhostu (`localhost`) na portu `8080`.

U slučaju greške u obrađivanju ili neočekivanih ulaznih podataka aplikacija može vratiti tekst sa opisom greške, ali se mora nakon toga nastaviti izvršavati.

#### Primjeri korištenja

```shell
$ curl 'http://localhost:8080/versions/max?v1=1.2.3&v2=1.3.1'
1.3.1
$ curl 'http://localhost:8080/versions/max?v1=1.0.1-SNAPSHOT&v2=1.0.1'
1.0.1
$ curl 'http://localhost:8080/versions/max?v1=0.1.1&v2=0.2.0-SNAPSHOT'
0.2.0-SNAPSHOT
```

```shell
$ curl 'http://localhost:8080/versions/next?v=1.2.3&type=MINOR'
1.3.0
$ curl 'http://localhost:8080/versions/next?v=0.0.1-SNAPSHOT&type=MAJOR'
1.0.0
$ curl 'http://localhost:8080/versions/next?v=1.3.15%2Bbuild-123&type=PATCH'
1.3.16
```

Primjeri ponašanja u slučaju neispravnih ulaznih podataka (sama poruka o grešci je samo ilustracija i u rješenju ne mora nužno izgledati ovako):

```shell
$ curl 'http://localhost:8080/versions/max?v1=0.1.1&v2=neispravna-verzija'
Verzion "neispravna-verzija" does not adhere to the SemVer 2.0.0 specification
$ curl 'http://localhost:8080/versions/max?v1=1.0.0&v2=2.0.0'
2.0.0
```

```shell
$ curl 'http://localhost:8080/versions/next?v=1.0.0&type=neispravni-type'
Type "neispravni-type" is not supported type value
$ curl 'http://localhost:8080/versions/next?v=1.0.0&type=MAJOR'
2.0.0
```

### Nefunkcionalni zahtjevi

1. Projekt mora biti objavljen kao git repozitorij na [GitHubu](https://github.com/).
2. Projekt mora koristiti [maven](https://maven.apache.org/) alat. Mora prolaziti naredba `mvn clean install` izvršena u vršnom direktoriju repozitorija.
3. Aplikacija mora biti napisana u Javi 21 i koristiti [Spring Boot](https://spring.io/projects/spring-boot) (verziju `3.2.3`). Mora se moći pokrenuti naredbom `mvn spring-boot:run` izvršenom u vršnom direktoriju repozitorija.
4. Aplikacija mora podići web poslužitelj (_server_) koji sluša dolazne HTTP upite (_request_) na localhostu (`localhost`) na portu `8080`.
