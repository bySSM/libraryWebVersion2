package ru.smm.springcourse.models;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
@Table(name = "Book")
public class Book {

    @Id
    @Column(name = "book_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int book_id;

    @NotEmpty(message = "Название книги не должно быть пустым") // проверка, что поле не пустое
    @Size(min = 2, max = 150, message = "Название книги должно быть от 2 до 150 символов длинной")
    @Column(name = "title")
    private String title;

    @NotEmpty(message = "Имя автора не должно быть пустым") // проверка, что поле не пустое
    @Size(min = 2, max = 150, message = "Имя автора должно быть от 2 до 150 символов длинной")
    @Column(name = "author")
    private String author;

    @Min(value = 1500, message = "Год должен быть больше 1500")
    @Column(name = "year")
    private int year;

    @ManyToOne()
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private Person owner;

    @Transient // Hibernate не будет замечать этого поля, что нам и нужно. По-умолчанию false.
    private boolean overdue; // Просрочил или нет сдачу книги человек

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date takenAt;

    public Book() {

    }

    public Book(String name, String author, int year) {
        this.title = name;
        this.author = author;
        this.year = year;
    }

    public int getBook_id() {
        return book_id;
    }

    public void setBook_id(int book_id) {
        this.book_id = book_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public Person getOwner() {
        return owner;
    }

    public void setOwner(Person owner) {
        this.owner = owner;
    }

    public boolean isOverdue() {
        return overdue;
    }

    public void setOverdue(boolean overdue) {
        this.overdue = overdue;
    }

    public Date getTakenAt() {
        return takenAt;
    }

    public void setTakenAt(Date createdAt) {
        this.takenAt = createdAt;
    }
}
