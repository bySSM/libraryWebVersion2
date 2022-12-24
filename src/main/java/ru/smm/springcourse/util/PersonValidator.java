package ru.smm.springcourse.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.smm.springcourse.models.Person;
import ru.smm.springcourse.services.PeopleService;

@Component
public class PersonValidator implements Validator {

    private final PeopleService peopleService;

    @Autowired
    public PersonValidator(PeopleService peopleService) {
        this.peopleService = peopleService;
    }

    // На объектах какого класса этот валидатор можно использовать
    @Override
    public boolean supports(Class<?> aClass) {
        return Person.class.equals(aClass);
    }


    @Override
    public void validate(Object o, Errors errors) {
        Person person = (Person) o;

        // 1ая проверка, есть ли человек с таким же ФИО в БД
        // isPresent() - метод обертки Optional, который проверяет, существует ли объект в этом Optional
        if (peopleService.getPersonByFullName(person.getName()).isPresent())
            // 1 арг - на каком поле произошла ошибка
            // 2 арг - код ошибки (пока нас не интересует)
            // 3 арг - сообщение об ошибке
            errors.rejectValue("name", "", "Эти ФИО уже используются");
    }
}
