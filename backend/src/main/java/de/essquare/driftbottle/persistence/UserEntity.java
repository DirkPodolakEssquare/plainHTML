package de.essquare.driftbottle.persistence;

import java.util.Objects;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "User")
public class UserEntity {

    public static final String EMAIL_KEY = "email";

    private String userId;
    private String email;

    public UserEntity() {
    }

    public UserEntity(String userId, String email) {
        this.userId = userId;
        this.email = email;
    }

    @DynamoDBHashKey
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @DynamoDBIndexHashKey(globalSecondaryIndexName = "email-index")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserEntity user = (UserEntity) o;
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

        private final UserEntity user;

        private UserBuilder() {
            user = new UserEntity();
        }

        public static UserBuilder builder() {
            return new UserBuilder();
        }

        public UserBuilder withUserId(String userId) {
            user.setUserId(userId);
            return this;
        }

        public UserBuilder withEmail(String email) {
            user.setEmail(email);
            return this;
        }

        public UserEntity build() {
            return user;
        }
    }
}
