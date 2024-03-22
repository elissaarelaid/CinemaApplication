package net.cinemaApplication.backend.repository;

import net.cinemaApplication.backend.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
