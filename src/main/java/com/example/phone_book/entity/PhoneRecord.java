package com.example.phone_book.entity;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "telephone_book")
public class PhoneRecord {

    private ObjectId id;
    private String firstName;
    private String lastName;
    private String phone;
    private String telegramId;

}
