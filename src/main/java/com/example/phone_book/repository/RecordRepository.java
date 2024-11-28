package com.example.phone_book.repository;

import java.util.Optional;

import com.example.phone_book.entity.PhoneRecord;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecordRepository extends MongoRepository<PhoneRecord, Long> {
    Optional<PhoneRecord> findById(ObjectId id);
}
