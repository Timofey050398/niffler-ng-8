package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.auth.AuthUserEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AuthUserDao {
    AuthUserEntity create(AuthUserEntity user);
    AuthUserEntity update(AuthUserEntity user);
    Optional<AuthUserEntity> findById(UUID id);
    Optional<AuthUserEntity> findByUsername(String username);
    void delete(AuthUserEntity user);
    List<AuthUserEntity> findAll();
}