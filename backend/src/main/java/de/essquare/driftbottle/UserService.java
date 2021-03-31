package de.essquare.driftbottle;

import java.util.UUID;

import org.springframework.stereotype.Service;

import de.essquare.driftbottle.persistence.UserDbRepository;
import de.essquare.driftbottle.persistence.UserEntity;

@Service
class UserService {

    private UserDbRepository userDbRepository;

    public UserService(UserDbRepository userDbRepository) {
        this.userDbRepository = userDbRepository;
    }

    public void postEmail(String inputEmail) {
        String email = inputEmail.toLowerCase();
        UserEntity user = userDbRepository.getByEmail(email);
        if (user == null) {
            user = UserEntity.builder()
                             .withUserId(UUID.randomUUID().toString())
                             .withEmail(email)
                             .build();
            userDbRepository.save(user);
        } else {
            throw new RuntimeException("user with email " + email + " already exists");
        }
    }

    public UserEntity getUser(String userId) {
        return userDbRepository.load(userId);
    }
}
