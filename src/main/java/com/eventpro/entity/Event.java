package com.eventpro.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String category;
    
    private LocalDate date;
    
    @Column(name = "end_date")
    private LocalDate endDate;
    
    private LocalTime time;
    
    private String venue;
    
    private String location;
    
    @Column(length = 1000)
    private String description;
    
    private Integer capacity;
    
    private Integer registered;
    
    private Double price;
    
    @Column(name = "early_bird_price")
    private Double earlyBirdPrice;
    
    @Column(name = "early_bird_deadline")
    private LocalDate earlyBirdDeadline;
    
    private String thumbnail;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizer_id")
    private User organizer;
    
    private String status; // upcoming, completed, etc.

    @ElementCollection
    @CollectionTable(name = "event_tags", joinColumns = @JoinColumn(name = "event_id"))
    @Column(name = "tag")
    private List<String> tags;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TicketType> ticketTypes;
    
    private Double rating;
    
    private Integer reviews;

    @ElementCollection
    @CollectionTable(name = "event_images", joinColumns = @JoinColumn(name = "event_id"))
    @Column(name = "image_url")
    private List<String> images;
    
    @Column(name = "is_past")
    private Boolean isPast;
}
