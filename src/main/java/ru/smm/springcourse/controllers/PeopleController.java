package ru.smm.springcourse.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.smm.springcourse.models.Person;
import ru.smm.springcourse.services.PeopleService;
import ru.smm.springcourse.util.PersonValidator;

import javax.validation.Valid;

@Controller
@RequestMapping("/people")
public class PeopleController {

    private final PeopleService peopleService;
    private final PersonValidator personValidator;

    @Autowired
    public PeopleController(PeopleService peopleService, PersonValidator personValidator) {
        this.peopleService = peopleService;
        this.personValidator = personValidator;
    }

    // Получим всех людей из DAO и передадим на отображение в представление
    @GetMapping() // будет пустой, потому что уже есть /people на классе
    public String index(Model model) {
        model.addAttribute("people", peopleService.findAll());

        return "people/index";
    }

    // Получим одного человека по id из DAO и передадим на отображение в представление
    // @PathVariable("id") - вытащит из запроса (/people/3) число 3 и передаст в качестве аргумента в метод
    @GetMapping("/{id}")
    public String show(@PathVariable("id") int id, Model model) {
        model.addAttribute("person", peopleService.findOne(id));
        model.addAttribute("books", peopleService.getBooksByPersonId(id));

        return "people/show";
    }

    // Будет возвращать HTML форму для создания нового человека
    @GetMapping("/new")
    public String newPerson(@ModelAttribute("person") Person person) {

        return "people/new";
    }

    // Будет принимать POST запрос, брать данные из POST запроса, будет добавлять человека в БД
    // @ModelAttribute("person") берет из формы значения и добавляет их person
    // bindingResult - объект, куда помещается ошибка, если пользователь ввел не валидные данные.
    // bindingResult должен в параметрах идти всегда после того объекта, который валидируется
    @PostMapping()
    public String create(@ModelAttribute("person") @Valid Person person,
                         BindingResult bindingResult) {

        // Валидируем поле name, проверяем, чтобы был уникальными ФИО
        personValidator.validate(person, bindingResult);

        // Проверяем, есть ли ошибки при валидации person
        if (bindingResult.hasErrors())
            // Если да, то выводятся ошибки и повторяется попытка пользователя
            return "people/new";

        peopleService.save(person);

        // redirect говорит браузеру перейти на другую страницу
        return "redirect:/people";
    }

    // Метод для редактирования страницы человека
    @GetMapping("/{id}/edit")
    public String edit(@PathVariable("id") int id, Model model) {
        // Помещаем в модель человека с данным id
        model.addAttribute("person", peopleService.findOne(id));

        return "people/edit";
    }

    // Метод, который принимает PATCH запрос на адрес /people/id
    // @ModelAttribute("person") берет из формы значения и добавляет их person
    @PatchMapping("/{id}")
    public String update(@ModelAttribute("person") @Valid Person person,
                         BindingResult bindingResult,
                         @PathVariable("id") int id) {

        // Проверяем, есть ли ошибки при валидации person
        if (bindingResult.hasErrors())
            // Если да, то выводятся ошибки и повторяется попытка пользователя
            return "people/edit";

        // Ищем человека в БД с таким id и меняем его значения на те, которые пришли в форме edit
        peopleService.update(id, person);

        return "redirect:/people";
    }

    // Метод для удаления человека
    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id) {
        peopleService.delete(id);

        return "redirect:/people";
    }
}
