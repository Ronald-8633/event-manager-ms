package br.com.eventmanager.application.service;

import br.com.eventmanager.adapter.inbound.rest.exception.BusinessException;
import br.com.eventmanager.adapter.outbound.persistence.UserRepository;
import br.com.eventmanager.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static java.time.LocalDateTime.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("User not found: " + email));
    }

    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public void deleteUser(String id) {
        if (!userRepository.existsById(id)) {
            throw new BusinessException("User not found: " + id);
        }
        userRepository.deleteById(id);
    }

    public void addOrganizedEvent(String userId, String eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found: " + userId));

        if (user.getOrganizedEvents() == null) {
            user.setOrganizedEvents(new HashSet<>());
        }

        user.getOrganizedEvents().add(eventId);
        user.setUpdatedAt(now());

        userRepository.save(user);
    }

    public void removeOrganizedEvent(String userId, String eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found: " + userId));

        if (user.getOrganizedEvents() != null) {
            user.getOrganizedEvents().remove(eventId);
            user.setUpdatedAt(now());
            userRepository.save(user);
        }
    }
}
