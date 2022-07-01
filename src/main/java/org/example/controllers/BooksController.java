package org.example.controllers;


import org.example.models.Book;
import org.example.models.Person;
import org.example.services.BooksService;
import org.example.services.PeopleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/books")
public class BooksController {
    private final BooksService booksService;
    private final PeopleService peopleService;

    public BooksController(BooksService booksService, PeopleService peopleService) {
        this.booksService = booksService;
        this.peopleService = peopleService;
    }

    @GetMapping()
    public String index(Model model){
        model.addAttribute("books", booksService.findAll());
        return "books/index";
    }


    @GetMapping("{id}")
    public String show(@PathVariable("id") int id, Model model, @ModelAttribute("person")Person person){

        model.addAttribute("book", booksService.findOne(id));
        Person bookOwner = booksService.getBookOwner(id);

        if(bookOwner != null){
            model.addAttribute("owner", bookOwner);
        }
        else{
            model.addAttribute("people", peopleService.findAll());
        }
        return "books/show";
    }

    @GetMapping("/new")
    public String newBook(@ModelAttribute("book") Book book){
        return "books/new";
    }

    @PostMapping()
    public String create(@ModelAttribute("book") Book book){
        booksService.save(book);
        return "redirect:/books";
    }

    @GetMapping("/{id}/edit")
    public String edit(Model model, @PathVariable("id") int id) {
        model.addAttribute("book", booksService.findOne(id));
        return "books/edit";
    }

    @PostMapping("/{id}")
    public String update(@ModelAttribute("book")  Book book,
                         @PathVariable("id") int id) {

        booksService.update(id, book);
        return "redirect:/books";
    }


    @PostMapping("/{id}/release")
    public String release(@PathVariable("id") int id){
        booksService.release(id);
        return "redirect:/books/" + id;
    }

    @PostMapping("/{id}/assign")
    public String assign(@PathVariable("id") int id, @ModelAttribute("person") Person selectedPerson) {
        // У selectedPerson назначено только поле id, остальные поля - null
        booksService.assign(id, selectedPerson);
        return "redirect:/books/" + id;
    }

    @GetMapping("/search")
    public String searchPage() {
        return "books/search";
    }


    @PostMapping("/search")
    public String makeSearch(Model model, @RequestParam("query") String query) {
        model.addAttribute("books", booksService.searchByTitle(query));
        return "books/search";
    }


    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") int id) {
        booksService.delete(id);
        return "redirect:/books";
    }


}
