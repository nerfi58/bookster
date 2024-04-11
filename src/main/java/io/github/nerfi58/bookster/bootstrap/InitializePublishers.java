package io.github.nerfi58.bookster.bootstrap;

import io.github.nerfi58.bookster.entities.Publisher;
import io.github.nerfi58.bookster.repositories.PublisherRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@Order(5)
public class InitializePublishers implements CommandLineRunner {

    private final PublisherRepository publisherRepository;

    public InitializePublishers(PublisherRepository publisherRepository) {
        this.publisherRepository = publisherRepository;
    }

    @Override
    public void run(String... args) {

        Publisher pearson = new Publisher();
        pearson.setName("Pearson");

        Publisher manning = new Publisher();
        manning.setName("Manning");

        Publisher bloomsbury = new Publisher();
        bloomsbury.setName("Bloomsbury");

        publisherRepository.saveAll(Set.of(pearson, manning, bloomsbury));
    }
}
