package com.reza.learning.service;

import com.reza.learning.model.Person;
import net.datafaker.Faker;

public class PersonFactory {
    private static final Faker faker = new Faker();

    public static Person create() {
        return new Person(
            faker.name().firstName(),
            faker.name().lastName(),
            faker.name().fullName(),
            faker.internet().emailAddress(),
            faker.name().username(),
            faker.internet().password(),
            faker.phoneNumber().phoneNumber(),
            faker.company().name(),
            faker.job().title(),
            faker.address().fullAddress(),
            faker.address().city(),
            faker.address().state(),
            faker.address().country(),
            faker.address().zipCode(),
            faker.internet().domainName(),
            faker.color().name(),
            faker.animal().name(),
            faker.book().title(),
            faker.yoda().quote(),
            faker.lorem().paragraph(),
            faker.university().name(),
            faker.finance().creditCard(),
            faker.finance().iban(),
            faker.finance().bic(),
            faker.avatar().image(),
            faker.internet().uuid(),
            faker.internet().ipV4Address(),
            faker.internet().ipV6Address(),
            faker.internet().macAddress(),
            faker.file().fileName(),
            faker.file().mimeType(),
            faker.internet().url(),
            faker.date().birthday().toString(),
            faker.date().birthday().toInstant().toString(),
            faker.currency().code(),
            faker.barcode().toString(),
            faker.color().hex()
        );
    }
}
