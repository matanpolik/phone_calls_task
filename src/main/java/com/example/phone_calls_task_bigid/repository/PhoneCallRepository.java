package com.example.phone_calls_task_bigid.repository;

import com.example.phone_calls_task_bigid.model.PhoneCall;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository interface for managing phone call records.
 */
@Repository
public interface PhoneCallRepository extends JpaRepository<PhoneCall, Long> {
    /**
     * Finds all phone calls associated with a specific phone number.
     *
     * @param phoneNumber The phone number for which to find associated phone calls.
     * @return A list of {@link PhoneCall} entities that are associated with the specified phone number.
     *         The list may be empty if no calls are found.
     */
    List<PhoneCall> findByPhoneNumber(String phoneNumber);

    /**
     * Finds all phone calls that have a duration greater than a specified value.
     *
     * @param duration The duration threshold in seconds. Phone calls with durations greater than this value will be returned.
     * @return A list of {@link PhoneCall} entities that have a duration greater than the specified threshold.
     *         The list may be empty if no such calls are found.
     */
    List<PhoneCall> findByDurationGreaterThan(Integer duration);

   /* @Modifying
    @Query("UPDATE PhoneCall pc SET pc.phoneNumber = :newPhoneNumber WHERE pc.phoneNumber = :oldPhoneNumber")
    int updatePhoneNumber(@Param("oldPhoneNumber") String oldPhoneNumber, @Param("newPhoneNumber") String newPhoneNumber);*/
}
