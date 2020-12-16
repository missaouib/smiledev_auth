package com.devcamp.project2.domain.login;

import com.sun.istack.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    @Nullable
    User findFirstByUid(String uid);

    @Nullable
    User findFirstById(long id);

}
