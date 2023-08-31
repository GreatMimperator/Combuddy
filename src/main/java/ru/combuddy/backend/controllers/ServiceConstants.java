package ru.combuddy.backend.controllers;


import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.combuddy.backend.exceptions.general.IllegalPageNumberException;

@Service
@Getter
public class ServiceConstants {
    @Value("${service.postsPerPage}")
    private int postsPerPage;
    @Value("${service.usersBeginWithPerPage}")
    private int usersBeginWithPerPage;

    public static void checkPageNumber(int pageNumber) throws IllegalPageNumberException {
        if (pageNumber < 1) {
            throw new IllegalPageNumberException("Page number should not be less than 1");
        }
    }

    public PageRequest pageRequest(int pageNumberSinceOne) throws IllegalPageNumberException {
        checkPageNumber(pageNumberSinceOne);
        return PageRequest.of(pageNumberSinceOne - 1, postsPerPage);
    }
}
