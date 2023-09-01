package ru.combuddy.backend.controllers.contact.models;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactList {
    @NotNull
    List<BaseContactInfo> contacts;
}
