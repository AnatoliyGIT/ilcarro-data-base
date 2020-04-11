package com.example.demo.repository;

import com.example.demo.documents.Filter;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FilterRepository extends MongoRepository<Filter, String> {
}
