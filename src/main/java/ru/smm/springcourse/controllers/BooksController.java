package ru.smm.springcourse.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.smm.springcourse.models.Book;
import ru.smm.springcourse.models.Person;
import ru.smm.springcourse.services.BooksService;
import ru.smm.springcourse.services.PeopleService;

import javax.validation.Valid;

@Controller
@RequestMapping("/books")
public class BooksController {

    private final BooksService booksService;
    private final PeopleService peopleService;

    @Autowired
    public BooksController(BooksService booksService, PeopleService peopleService) {
        this.booksService = booksService;
        this.peopleService = peopleService;
    }

    // Получим все книги из БД и передадим на отображение в представление
    // required = false - значит, что параметр необязательный
    @GetMapping() // будет пустой, потому что уже есть /books на классе
    public String index(Model model,
                        @RequestParam(value = "sort_by_year", required = false) boolean sortByYear,
                        @RequestParam(value = "page", required = false) Integer page,
                        @RequestParam(value = "books_per_page", required = false) Integer booksPerPage) {

        if (page != null || booksPerPage != null)
            model.addAttribute("books", booksService.pagination(page, booksPerPage, sortByYear));
        else
            model.addAttribute("books", booksService.findAll(sortByYear));

        return "books/index";
    }

    // Получим одну книгу по id из DAO и передадим на отображение в представление
    // @PathVariable("id") - вытащит из запроса (/books/3) число 3 и передаст в качестве аргумента в метод
    @GetMapping("/{id}")
    public String show(@PathVariable("id") int id, Model model, @ModelAttribute("person") Person person) {
        model.addAttribute("book", booksService.findOne(id));

        Person bookOwner = booksService.getBookOwner(id);

        if (bookOwner != null)
            model.addAttribute("owner", bookOwner);
        else
            model.addAttribute("people", peopleService.findAll());

        return "books/show";
    }

    // Будет возвращать HTML форму для создания новой книги
    @GetMapping("/new")
    public String newBook(@ModelAttribute("book") Book book) {

        return "books/new";
    }

    // Будет принимать POST запрос, брать данные из POST запроса, будет добавлять книгу в БД
    // @ModelAttribute("book") берет из формы значения и добавляет их book
    // bindingResult - объект, куда помещается ошибка, если пользователь ввел не валидные данные.
    // bindingResult должен в параметрах идти всегда после того объекта, который валидируется
    @PostMapping()
    public String create(@ModelAttribute("book") @Valid Book book,
                         BindingResult bindingResult) {

        // Проверяем, есть ли ошибки при валидации person
        if (bindingResult.hasErrors())
            // Если да, то выводятся ошибки и повторяется попытка пользователя
            return "books/new";

        booksService.save(book);

        // redirect говорит браузеру перейти на другую страницу
        return "redirect:/books";
    }

    // Метод для редактирования страницы книги
    @GetMapping("/{id}/edit")
    public String edit(@PathVariable("id") int id, Model model) {
        // Помещаем в модель книгу с данным id
        model.addAttribute("book", booksService.findOne(id));

        return "books/edit";
    }

    // Метод, который принимает PATCH запрос на адрес /books/id
    // @ModelAttribute("book") берет из формы значения и добавляет их book
    @PatchMapping("/{id}")
    public String update(@ModelAttribute("book") @Valid Book book,
                         BindingResult bindingResult,
                         @PathVariable("id") int id) {

        // Проверяем, есть ли ошибки при валидации person
        if (bindingResult.hasErrors())
            // Если да, то выводятся ошибки и повторяется попытка пользователя
            return "books/edit";

        // Ищем книгу в БД с таким id и меняем ее значения на те, которые пришли в форме edit
        booksService.update(id, book);

        return "redirect:/books";
    }

    // Метод для удаления человека
    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id) {
        booksService.delete(id);

        return "redirect:/books";
    }

    // Метод освобождения книги с человека
    @PatchMapping("/{id}/release")
    public String release(@PathVariable("id") int id) {
        booksService.release(id);

        return "redirect:/books/" + id;
    }

    // Метод назначения книги человеку
    @PatchMapping("/{id}/assign")
    public String assign(@PathVariable("id") int id, @ModelAttribute("person") Person selectedPerson) {
        // у selectedPerson назначено только поле id, все остальные поля - null
        booksService.assign(id, selectedPerson);

        return "redirect:/books/" + id;
    }

    // Метод для отображения страницы поиска книг
    @GetMapping("/search")
    public String searchPage() {

        return "/books/search";
    }

    // Метод для поиска книг
    @PostMapping("/search")
    public String searchByTitle(Model model, @RequestParam(value = "query") String query) {

        model.addAttribute("books", booksService.searchByTitle(query));

        return "books/search";
    }
}
