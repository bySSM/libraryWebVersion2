package ru.smm.springcourse.services;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.smm.springcourse.models.Book;
import ru.smm.springcourse.models.Person;
import ru.smm.springcourse.repositories.PeopleRepository;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PeopleService {

    private final PeopleRepository peopleRepository;

    @Autowired
    public PeopleService(PeopleRepository peopleRepository) {
        this.peopleRepository = peopleRepository;
    }

    // Вернет список всех людей
    public List<Person> findAll() {
        return peopleRepository.findAll();
    }

    // вернет одного человека из БД по его id
    public Person findOne(int id) {
        Optional<Person> foundPerson = peopleRepository.findById(id);

        // Возвращаем человека, если он был найден, иначе - null
        return foundPerson.orElse(null);
    }

    // сохранение человека в БД
    @Transactional // тут будет больший приоритет над аннотацией класса
    public void save(Person person) {
        peopleRepository.save(person);
    }

    // Обновляем данные человека в БД
    @Transactional
    public void update(int id, Person updatedPerson) {
        updatedPerson.setUser_id(id);

        // JPA будет видеть, что передается человек с таким же id,
        // поэтому будет не сохранять нового, а обновлять человека с таким id
        peopleRepository.save(updatedPerson);
    }

    // Удаление человека из БД
    @Transactional
    public void delete(int id) {
        peopleRepository.deleteById(id);
    }

    // Для валидации уникальности имени
    public Optional<Person> getPersonByFullName(String fullName) {
        return peopleRepository.findByName(fullName);
    }

    // Возвращает список книг пользователя
    public List<Book> getBooksByPersonId(int id) {
        Optional<Person> person = peopleRepository.findById(id);

        if (person.isPresent()) {
            Hibernate.initialize(person.get().getBooks());
            // Мы внизу итерируемся по книгам, поэтому они точно будут загружены, но на всякий случай
            // не мешает всегда вызывать Hibernate.initialize()
            // (на случай, например, если код в дальнейшем поменяется и итерация по книгам удалится)

            // Проверка просрочил человек книгу или нет
            person.get().getBooks().forEach(book -> {
                long diffInMillis = Math.abs(book.getTakenAt().getTime() - new Date().getTime());
                // 864000000 миллисекунд = 10 дней
                if (diffInMillis > 864000000)
                    book.setOverdue(true); // книга просрочена
            });

            return person.get().getBooks();
        } else
            return Collections.emptyList();
    }
}
