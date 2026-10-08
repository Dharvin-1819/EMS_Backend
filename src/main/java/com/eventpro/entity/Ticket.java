package com.eventpro.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tickets")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ticket_number", nullable = false, unique = true)
    private String ticketNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_type_id")
    private TicketType ticketType;

    private Double price;
    
    @Column(name = "purchased_at")
    private LocalDate purchasedAt;
    
    private String status; // used, valid, cancelled
    
    @Column(name = "qr_code")
    private String qrCode;
    
    private Integer seats;

    @Column(name = "refund_status")
    private String refundStatus;

    @Column(name = "refund_amount")
    private Double refundAmount;

    @Column(name = "refunded_at")
    private LocalDate refundedAt;

    @Column(name = "refund_method")
    private String refundMethod;
}
