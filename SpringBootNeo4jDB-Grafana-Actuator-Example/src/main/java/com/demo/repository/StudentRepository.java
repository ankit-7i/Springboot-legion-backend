package com.demo.repository;


import org.springframework.data.neo4j.repository.Neo4jRepository;

import com.demo.entity.Student;

public interface StudentRepository extends Neo4jRepository<Student, Long> {
}