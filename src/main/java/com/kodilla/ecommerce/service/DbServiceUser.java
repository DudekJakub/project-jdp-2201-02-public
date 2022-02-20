package com.kodilla.ecommerce.service;

import com.kodilla.ecommerce.domain.Cart;
import com.kodilla.ecommerce.domain.ConfirmationToken;
import com.kodilla.ecommerce.domain.User;

import com.kodilla.ecommerce.exception.*;
import com.kodilla.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import com.kodilla.ecommerce.repository.CartRepository;

import java.util.ArrayList;
import java.util.UUID;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class DbServiceUser implements UserDetailsService {
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ConfirmationTokenService confirmationTokenService;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("There is no user with given email in database!"));
    }

    @Transactional
    public void enableAppUser(final String email) {
        userRepository.findByUsername(email)
                .orElseThrow(() -> new UsernameNotFoundException("There is no user with given email in database!"))
                .setEnabled(true);
    }

    @Transactional
    public String signUpUser(final User user) throws EmailAlreadyExistsInDatabaseException {
        boolean alreadyExists = userRepository
                .findByUsername(user.getUsername())
                .isPresent();

        if (alreadyExists) throw new EmailAlreadyExistsInDatabaseException();

        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        Cart userCart = Cart.builder().products(new ArrayList<>()).build();
        user.setCart(userCart);
        cartRepository.save(userCart);
        userRepository.save(user);

        ConfirmationToken confirmationToken = confirmationTokenService.createConfirmationToken(user);
        confirmationTokenService.saveConfirmationToken(confirmationToken);

        return confirmationToken.getToken();
    }

    public User blockUser(final Long idUser) throws UserNotFoundException, UserAlreadyBlockedException {
        User userFromDb = userRepository.findById(idUser).orElseThrow(UserNotFoundException::new);

        if (!userFromDb.isEnabled()) {
            throw new UserAlreadyBlockedException();
        }

        userFromDb.setEnabled(false);
        userFromDb.setActive(false);
        userRepository.save(userFromDb);
        return userFromDb;
    }

    public String generateKey(String username, String password) throws UserNotFoundException {
        User userFromDb = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        if (password.equals(userFromDb.getPassword())) {
            userFromDb.setKeyGenerationTime(System.currentTimeMillis());
            userFromDb.setUserKey(UUID.randomUUID().toString());
            userRepository.save(userFromDb);
            return userFromDb.getUserKey();
        }
        return "Wrong user credentials.";
    }
}