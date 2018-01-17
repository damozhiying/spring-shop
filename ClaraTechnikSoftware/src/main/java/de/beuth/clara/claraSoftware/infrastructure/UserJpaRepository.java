package de.beuth.clara.claraSoftware.infrastructure;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.beuth.clara.claraSoftware.domain.User;
import de.beuth.clara.claraSoftware.domain.imports.UserRepository;
import de.beuth.clara.claraSoftware.infrastructure.imports.ImportedUserJpaRepository;

@Service
public class UserJpaRepository implements UserRepository {

	private final ImportedUserJpaRepository impl;

	@Autowired
	public UserJpaRepository(final ImportedUserJpaRepository impl) {
		this.impl = impl;
	}

	@Override
	public void deleteAll() {
		impl.deleteAll();
	}

	@Override
	public User save(final User User) {
		return impl.save(User);
	}

	@Override
	public void delete(User User) {
		impl.delete(User);
	}

	@Override
	public Optional<User> find(Long id) {
		return impl.findOneById(id);
	}

	@Override
	public List<User> findAll() {
		return impl.findAllByOrderByIdDesc();
	}

}
