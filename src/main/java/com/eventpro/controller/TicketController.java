package com.eventpro.controller;

import com.eventpro.entity.Ticket;
import com.eventpro.entity.Event;
import com.eventpro.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    @Autowired
    private TicketRepository ticketRepository;

    @GetMapping
    public ResponseEntity<List<Ticket>> getAllTickets() {
        return ResponseEntity.ok(ticketRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ticket> getTicketById(@PathVariable Long id) {
        return ticketRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Autowired
    private com.eventpro.repository.EventRepository eventRepository;

    @Autowired
    private com.eventpro.repository.UserRepository userRepository;

    @Autowired
    private com.eventpro.repository.TicketTypeRepository ticketTypeRepository;

    @PostMapping
    public ResponseEntity<?> createTicket(@RequestBody Map<String, Object> payload) {
        Ticket ticket = new Ticket();
        
        // Resolve event
        Long eventId = null;
        if (payload.containsKey("eventId") && payload.get("eventId") != null) {
            try {
                eventId = Long.valueOf(payload.get("eventId").toString().replaceAll("\\D", ""));
            } catch (Exception e) {}
        }
        if (eventId != null) {
            eventRepository.findById(eventId).ifPresent(ticket::setEvent);
        }
        if (ticket.getEvent() == null && !eventRepository.findAll().isEmpty()) {
            ticket.setEvent(eventRepository.findAll().get(0));
        }

        // Resolve user
        Long userId = null;
        if (payload.containsKey("userId") && payload.get("userId") != null) {
            try {
                userId = Long.valueOf(payload.get("userId").toString().replaceAll("\\D", ""));
            } catch (Exception e) {}
        }
        if (userId != null) {
            userRepository.findById(userId).ifPresent(ticket::setUser);
        }
        if (ticket.getUser() == null && !userRepository.findAll().isEmpty()) {
            ticket.setUser(userRepository.findAll().get(0));
        }

        // Resolve ticket type
        Long ticketTypeId = null;
        if (payload.containsKey("ticketTypeId") && payload.get("ticketTypeId") != null) {
            try {
                ticketTypeId = Long.valueOf(payload.get("ticketTypeId").toString().replaceAll("\\D", ""));
                ticketTypeRepository.findById(ticketTypeId).ifPresent(ticket::setTicketType);
            } catch (Exception e) {}
        }

        if (payload.containsKey("price")) {
            ticket.setPrice(Double.valueOf(payload.get("price").toString()));
        }
        if (payload.containsKey("seats")) {
            ticket.setSeats(Integer.valueOf(payload.get("seats").toString()));
        } else {
            ticket.setSeats(1);
        }

        ticket.setTicketNumber("EVP-" + LocalDate.now().getYear() + "-TK" + System.currentTimeMillis() % 10000);
        ticket.setQrCode("QR-EVP-" + System.currentTimeMillis());
        ticket.setPurchasedAt(LocalDate.now());
        ticket.setStatus("active");

        Ticket saved = ticketRepository.save(ticket);
        return ResponseEntity.ok(saved);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Ticket> patchTicket(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        return ticketRepository.findById(id).map(ticket -> {
            if (updates.containsKey("status")) {
                String newStatus = updates.get("status").toString();
                if ("cancelled".equalsIgnoreCase(newStatus) && !"cancelled".equalsIgnoreCase(ticket.getStatus())) {
                    if (ticket.getEvent() != null) {
                        Event ev = ticket.getEvent();
                        int seats = ticket.getSeats() != null ? ticket.getSeats() : 1;
                        int currentReg = ev.getRegistered() != null ? ev.getRegistered() : 0;
                        ev.setRegistered(Math.max(0, currentReg - seats));
                        eventRepository.save(ev);
                    }
                    ticket.setRefundStatus("Refunded");
                    ticket.setRefundAmount(ticket.getPrice() != null ? ticket.getPrice() : 0.0);
                    ticket.setRefundedAt(LocalDate.now());
                    ticket.setRefundMethod("Original Payment Method (UPI / Bank)");
                }
                ticket.setStatus(newStatus);
            }
            if (updates.containsKey("refundStatus")) {
                ticket.setRefundStatus(updates.get("refundStatus").toString());
            }
            if (updates.containsKey("refundAmount")) {
                try {
                    ticket.setRefundAmount(Double.valueOf(updates.get("refundAmount").toString()));
                } catch (Exception e) {}
            }
            if (updates.containsKey("refundMethod")) {
                ticket.setRefundMethod(updates.get("refundMethod").toString());
            }
            Ticket updated = ticketRepository.save(ticket);
            return ResponseEntity.ok(updated);
        }).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancelAndRefundTicket(@PathVariable Long id) {
        return ticketRepository.findById(id).map(ticket -> {
            if (ticket.getEvent() != null) {
                Event ev = ticket.getEvent();
                int seats = ticket.getSeats() != null ? ticket.getSeats() : 1;
                int currentReg = ev.getRegistered() != null ? ev.getRegistered() : 0;
                ev.setRegistered(Math.max(0, currentReg - seats));
                eventRepository.save(ev);
            }
            ticket.setStatus("cancelled");
            ticket.setRefundStatus("Refunded");
            ticket.setRefundAmount(ticket.getPrice() != null ? ticket.getPrice() : 0.0);
            ticket.setRefundedAt(LocalDate.now());
            ticket.setRefundMethod("Original Payment Method (UPI / Bank)");
            Ticket updated = ticketRepository.save(ticket);
            return ResponseEntity.ok(updated);
        }).orElse(ResponseEntity.notFound().build());
    }
}
