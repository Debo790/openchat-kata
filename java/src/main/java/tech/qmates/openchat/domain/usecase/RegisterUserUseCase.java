package tech.qmates.openchat.domain.usecase;

import tech.qmates.openchat.domain.entity.User;
import tech.qmates.openchat.domain.repository.UserRepository;

public class RegisterUserUseCase {

    private final UserRepository userRepository;

    public RegisterUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void run(String username, String password, String about) throws UsernameAlreadyInUseException {
        if(userRepository.isUsernameAlreadyUsed(username))
            throw new UsernameAlreadyInUseException(username);

        User userToBeStored = new User(username, password, about);
        this.userRepository.store(userToBeStored);
    }

    public static class UsernameAlreadyInUseException extends Exception {
        public UsernameAlreadyInUseException(String username) {
            super("Username [" + username + "] already in use!");
        }
    }
}
