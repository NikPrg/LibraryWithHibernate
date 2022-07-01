package org.example.services;


import org.example.models.Book;
import org.example.models.Person;
import org.example.repositories.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class BooksService {
    private final BookRepository booksRepository;


    public BooksService(BookRepository bookRepository) {
        this.booksRepository = bookRepository;
    }


    public List<Book> findAll(){
        return booksRepository.findAll();
    }

    public Book findOne(int id){
        Optional<Book> byId = booksRepository.findById(id);
        return byId.orElse(null);
    }


    @Transactional
    public void save(Book book){
        booksRepository.save(book);
    }

    public Person getBookOwner(int id){
        return booksRepository.findById(id).map(Book::getOwner).orElse(null);
    }

    @Transactional
    public void release(int id){
        booksRepository.findById(id).ifPresent(
                book -> {
                    book.setOwner(null);
                    book.setTakenAt(null);
                });
    }

    // Назначает книгу человеку (этот метод вызывается, когда человек забирает книгу из библиотеки)
    @Transactional
    public void assign(int id, Person selectedPerson) {
        booksRepository.findById(id).ifPresent(
                book -> {
                    book.setOwner(selectedPerson);
                    book.setTakenAt(new Date()); // текущее время
                }
        );
    }

    @Transactional
    public void update(int id, Book updatedBook) {
        Book bookToBeUpdated = booksRepository.findById(id).get();

        updatedBook.setId(id);
        updatedBook.setOwner(bookToBeUpdated.getOwner()); // чтобы не терялась связь при обновлении

        booksRepository.save(updatedBook);
    }

    public List<Book> searchByTitle(String query) {
        return booksRepository.findByTitleStartingWith(query);
    }


    @Transactional
    public void delete(int id){
        booksRepository.deleteById(id);
    }

}
