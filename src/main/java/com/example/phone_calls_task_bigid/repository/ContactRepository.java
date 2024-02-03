package com.example.phone_calls_task_bigid.repository;
import com.example.phone_calls_task_bigid.model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
/**
 * Repository interface for managing contacts.
 */
@Repository
public interface ContactRepository extends JpaRepository<Contact, String> {
    /**
     * Updates the phone number of a contact.
     *
     * @param oldPhoneNumber The old phone number to be updated.
     * @param newPhoneNumber The new phone number to replace the old one.
     * @return The number of updated contacts (should be 0 or 1).
     */
    /*@Modifying
    @Query("UPDATE Contact c SET c.phoneNumber = :newPhoneNumber WHERE c.phoneNumber = :oldPhoneNumber")
    int updatePhoneNumber(@Param("oldPhoneNumber") String oldPhoneNumber, @Param("newPhoneNumber") String newPhoneNumber);*/

    /**
     * Finds a contact by their phone number.
     *
     * @param phoneNumber The phone number to search for.
     * @return An optional containing the contact if found, or an empty optional otherwise.
     */
    Optional<Contact> findByPhoneNumber(String phoneNumber);
    /**
     * Checks if a contact with the given phone number exists.
     *
     * @param phoneNumber The phone number to check.
     * @return True if a contact with the given phone number exists, false otherwise.
     */
    boolean existsByPhoneNumber(String phoneNumber);
}
