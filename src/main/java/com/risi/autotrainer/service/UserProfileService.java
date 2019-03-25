package com.risi.autotrainer.service;

import com.risi.autotrainer.domain.UserProfile;
import com.risi.autotrainer.repository.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserProfileService {

    @Autowired
    private UserProfileRepository repository;

    public void saveUserProfile(UserProfile profile) {
        repository.save(profile);
    }

    public Optional<UserProfile> getUserProfile(String userId) {
        return repository.findByUserId(userId);
    }
}
