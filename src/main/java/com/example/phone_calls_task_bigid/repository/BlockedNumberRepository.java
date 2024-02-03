package com.example.phone_calls_task_bigid.repository;

import com.example.phone_calls_task_bigid.model.BlockedNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface BlockedNumberRepository extends JpaRepository<BlockedNumber, String> {
    /**
     * Retrieves a blacklist entry by its phone number.
     *
     * @param phoneNumber The phone number to search for.
     * @return An optional containing the blacklist entry if found, or an empty optional otherwise.
     */
    Optional<BlockedNumber> findByPhoneNumber(String phoneNumber);
}
