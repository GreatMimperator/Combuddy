package ru.combuddy.backend.controllers.contact.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactList {
    List<BaseContactInfo> contacts;
}
