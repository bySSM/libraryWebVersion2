package ru.smm.springcourse.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.smm.springcourse.models.Book;
import ru.smm.springcourse.models.Person;
import ru.smm.springcourse.repositories.BooksRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class BooksService {

    private final BooksRepository booksRepository;

    @Autowired
    public BooksService(BooksRepository booksRepository) {
        this.booksRepository = booksRepository;
    }

    // Вернет список всех книг
    public List<Book> findAll(boolean sortByYear) {
        if (sortByYear)
            return booksRepository.findAll(Sort.by("year"));
        else
            return booksRepository.findAll();
    }

    public List<Book> pagination(Integer page, Integer booksPerPage, boolean sortByYear) {
        if (sortByYear)
            return booksRepository.findAll(PageRequest.of(page, booksPerPage, Sort.by("year"))).getContent();
        else
            return booksRepository.findAll(PageRequest.of(page, booksPerPage)).getContent();
    }

    // вернет одну книгу из БД по ее id
    public Book findOne(int id) {
        Optional<Book> foundBook = booksRepository.findById(id);

        // Возвращаем книгу, если она была найдена, иначе - null
        return foundBook.orElse(null);
    }

    // сохранение книгу в БД
    @Transactional // тут будет больший приоритет над аннотацией класса
    public void save(Book book) {
        booksRepository.save(book);
    }

    // Обновляем данные книги в БД
    @Transactional
    public void update(int id, Book updatedBook) {
        Book bookToBeUpdated = booksRepository.findById(id).get();

        // добавляем по сути новую книгу (которая не находится в Persistence context), поэтому нужен save()
        updatedBook.setBook_id(id);
        updatedBook.setOwner(bookToBeUpdated.getOwner()); // чтобы не терялась связь при обновлении

        // JPA будет видеть, что передается книга с таким же id,
        // поэтому будет не сохранять новую, а обновлять книгу с таким id
        booksRepository.save(updatedBook);
    }

    // Удаление книги из БД
    @Transactional
    public void delete(int id) {
        booksRepository.deleteById(id);
    }

    // Join'им таблицы Book и Person и получаем человека, которому принадлежит книга с указанным id
    public Person getBookOwner(int id) {

        Optional<Book> book = booksRepository.findById(id);

        return book.map(Book::getOwner).orElse(null);
    }

    // Метод освобождения книги (вызывается, когда пользователь возвращает книгу в библиотеку)
    @Transactional
    public void release(int id) {
        booksRepository.findById(id).ifPresent(
                book -> {
                    book.setOwner(null);
                    book.setTakenAt(null);
                }
        );
    }

    // Метод назначения книги (вызывается, когда пользователь берет книгу в библиотеке)
    @Transactional
    public void assign(int id, Person selectedPerson) {
        booksRepository.findById(id).ifPresent(
                book -> {
                    book.setOwner(selectedPerson);
                    book.setTakenAt(new Date());
                }
        );
    }

    public List<Book> searchByTitle(String query) {

        return booksRepository.findByTitleStartingWith(query);
    }
}
