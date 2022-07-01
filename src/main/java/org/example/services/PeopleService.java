package org.example.services;

import org.example.models.Book;
import org.example.models.Person;
import org.example.repositories.PeopleRepository;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PeopleService {

    private final PeopleRepository peopleRepository;


    public PeopleService(PeopleRepository peopleRepository) {
        this.peopleRepository = peopleRepository;
    }



    public List<Person> findAll(){
        return peopleRepository.findAll();
    }

    public Person findOne(int id){
        Optional<Person> byId = peopleRepository.findById(id);
        return byId.orElse(null);
    }


    @Transactional
    public void save(Person person){
        peopleRepository.save(person);
    }

    @Transactional
    public void delete(int id){
        peopleRepository.deleteById(id);
    }

    @Transactional
    public void update(int id, Person updatedPerson) {
        updatedPerson.setId(id);
        peopleRepository.save(updatedPerson);
    }

    public List<Book> getBooksByPersonId(int id){
        Optional<Person> person = peopleRepository.findById(id);

        if (person.isPresent()) {
            Hibernate.initialize(person.get().getBooks());

            // Проверка просроченности книг
            person.get().getBooks().forEach(book -> {
                long diffInMillies = Math.abs(book.getTakenAt().getTime() - new Date().getTime());
                // 864000000 милисекунд = 10 суток
                if (diffInMillies > 864000000)
                    book.setExpired(true);
            });

            return person.get().getBooks();
        }
        else {
            return Collections.emptyList();
        }

    }

}
