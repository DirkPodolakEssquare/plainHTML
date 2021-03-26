package de.essquare.driftbottle;

import java.util.Objects;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "User")
public class User {

    public static final String EMAIL_KEY = "email";

    private String userId;
    private String email;

    public User() {
    }

    public User(String userId, String email) {
        this.userId = userId;
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(final String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final User user = (User) o;
        return Objects.equals(userId, user.userId) && Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, email);
    }

    @Override
    public String toString() {
        return "User{" +
               "userId='" + userId + '\'' +
               ", email='" + email + '\'' +
               '}';
    }

    public static UserBuilder builder() {
        return UserBuilder.builder();
    }

    public static final class UserBuilder {
        private String userId;
        private String email;

        private UserBuilder() {}

        public static UserBuilder builder() { return new UserBuilder(); }

        public UserBuilder withUserId(String userId) {
            this.userId = userId;
            return this;
        }

        public UserBuilder withEmail(String email) {
            this.email = email;
            return this;
        }

        public User build() {
            return new User(userId, email);
        }
    }
}
