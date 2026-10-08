package com.eventpro.repository;

import com.eventpro.entity.Ticket;
import com.eventpro.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByUser(User user);
    Optional<Ticket> findByTicketNumber(String ticketNumber);
}
