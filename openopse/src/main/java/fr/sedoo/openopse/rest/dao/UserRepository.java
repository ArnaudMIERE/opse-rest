package fr.sedoo.openopse.rest.dao;

import org.springframework.data.mongodb.repository.MongoRepository;

import fr.sedoo.openopse.rest.domain.User;

public interface UserRepository extends MongoRepository<User, String>{
	User findByEmail(String email);

}
