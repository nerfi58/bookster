package io.github.nerfi58.bookster.bootstrap;

import io.github.nerfi58.bookster.entities.Publisher;
import io.github.nerfi58.bookster.repositories.PublisherRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(5)
public class InitializePublishers implements CommandLineRunner {

    private final PublisherRepository publisherRepository;

    public InitializePublishers(PublisherRepository publisherRepository) {
        this.publisherRepository = publisherRepository;
    }

    @Override
    public void run(String... args) {
        Publisher publisher = new Publisher();
        publisher.setName("WYDAWNICTWO POLSKIE");

        publisherRepository.save(publisher);
    }
}
