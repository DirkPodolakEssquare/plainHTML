package de.essquare.driftbottle;

import java.util.UUID;

@org.springframework.stereotype.Service
class Service {

    private final DbRepository dbRepository;

    public Service(DbRepository dbRepository) {
        this.dbRepository = dbRepository;
    }

    public void postEmail(String inputEmail) {
        String email = inputEmail.toLowerCase();
        User user = dbRepository.getByEmail(email);
        if (user == null) {
            user = User.builder()
                       .withUserId(UUID.randomUUID().toString())
                       .withEmail(email)
                       .build();
            dbRepository.save(user);
        } else {
            throw new RuntimeException("user with email " + email + " already exists");
        }
    }

    public User getUser(String userId) {
        return dbRepository.load(userId);
    }
}
