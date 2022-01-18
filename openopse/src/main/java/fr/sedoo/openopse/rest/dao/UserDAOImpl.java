package fr.sedoo.openopse.rest.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.sedoo.openopse.rest.domain.User;

@Component
public class UserDAOImpl implements UserDAO{
	
	@Autowired
	private UserRepository userRepository;

	@Override
	public List<User> findAll() {
		// TODO Auto-generated method stub
		return userRepository.findAll();
	}
	@Override
	public User findByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	@Override
	public User upsert(User user) {
		// TODO Auto-generated method stub
		return userRepository.save(user);
	}
	
	@Override
	public void validateRegistration(String email) {
		User mail = findByEmail(email);
		mail.setStatus(true);
		userRepository.save(mail);
	}

}
