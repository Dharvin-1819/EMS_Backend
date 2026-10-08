package com.eventpro.config;

import com.eventpro.entity.Event;
import com.eventpro.entity.Ticket;
import com.eventpro.entity.TicketType;
import com.eventpro.entity.User;
import com.eventpro.repository.EventRepository;
import com.eventpro.repository.TicketRepository;
import com.eventpro.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // 1. Seed Users if empty
        if (userRepository.count() == 0) {
            System.out.println("--> Seeding initial MySQL users...");

            User admin = User.builder()
                    .name("System Administrator")
                    .email("admin@eventpro.com")
                    .password(passwordEncoder.encode("admin123"))
                    .role(User.Role.admin)
                    .phone("+91 98765 43210")
                    .joinedAt(LocalDate.of(2025, 1, 10))
                    .status("approved")
                    .build();

            User organizer = User.builder()
                    .name("Apex Event Management")
                    .email("organizer@eventpro.com")
                    .password(passwordEncoder.encode("org123"))
                    .role(User.Role.organizer)
                    .phone("+91 98765 43211")
                    .joinedAt(LocalDate.of(2025, 2, 1))
                    .status("approved")
                    .build();

            User user = User.builder()
                    .name("Rahul Sharma")
                    .email("user@eventpro.com")
                    .password(passwordEncoder.encode("user123"))
                    .role(User.Role.user)
                    .phone("+91 98765 43212")
                    .joinedAt(LocalDate.of(2025, 3, 15))
                    .status("approved")
                    .build();

            userRepository.saveAll(Arrays.asList(admin, organizer, user));
            System.out.println("--> Successfully seeded 3 users (admin, organizer, user).");
        }

        // 2. Seed Events if empty
        if (eventRepository.count() == 0) {
            System.out.println("--> Seeding initial MySQL events...");

            User organizer = userRepository.findByEmail("organizer@eventpro.com").orElse(null);

            Event event1 = Event.builder()
                    .title("Tech Summit 2026")
                    .category("Technology")
                    .date(LocalDate.now().plusDays(15))
                    .endDate(LocalDate.now().plusDays(17))
                    .time(LocalTime.of(9, 30))
                    .venue("Bangalore International Exhibition Centre")
                    .location("Bangalore, India")
                    .description("The largest AI and Cloud Computing Summit bringing together tech innovators, developers, and industry leaders from across the globe.")
                    .capacity(5000)
                    .registered(1420)
                    .price(1499.0)
                    .earlyBirdPrice(999.0)
                    .earlyBirdDeadline(LocalDate.now().plusDays(5))
                    .thumbnail("/images/technology.png")
                    .organizer(organizer)
                    .status("upcoming")
                    .rating(4.8)
                    .reviews(154)
                    .isPast(false)
                    .tags(Arrays.asList("AI", "Cloud", "Software", "Innovation"))
                    .images(Arrays.asList("/images/technology.png"))
                    .build();

            TicketType tt1 = TicketType.builder().name("General Pass").price(999.0).available(3000).event(event1).build();
            TicketType tt2 = TicketType.builder().name("VIP All Access").price(2499.0).available(500).event(event1).build();
            event1.setTicketTypes(Arrays.asList(tt1, tt2));

            Event event2 = Event.builder()
                    .title("Sunburn Neon Music Fest")
                    .category("Music")
                    .date(LocalDate.now().plusDays(25))
                    .endDate(LocalDate.now().plusDays(26))
                    .time(LocalTime.of(17, 0))
                    .venue("Jawaharlal Nehru Stadium")
                    .location("Delhi NCR, India")
                    .description("An unforgettable weekend of EDM, visual laser shows, and performances by top international DJs.")
                    .capacity(12000)
                    .registered(8500)
                    .price(2999.0)
                    .earlyBirdPrice(1999.0)
                    .earlyBirdDeadline(LocalDate.now().plusDays(10))
                    .thumbnail("/images/music.png")
                    .organizer(organizer)
                    .status("upcoming")
                    .rating(4.9)
                    .reviews(310)
                    .isPast(false)
                    .tags(Arrays.asList("EDM", "Concert", "Music", "Festival"))
                    .images(Arrays.asList("/images/music.png"))
                    .build();

            TicketType tt3 = TicketType.builder().name("Fan Pit").price(1999.0).available(5000).event(event2).build();
            TicketType tt4 = TicketType.builder().name("VVIP Lounge").price(5999.0).available(200).event(event2).build();
            event2.setTicketTypes(Arrays.asList(tt3, tt4));

            Event event3 = Event.builder()
                    .title("Global Startup Expo 2026")
                    .category("Business")
                    .date(LocalDate.now().plusDays(40))
                    .endDate(LocalDate.now().plusDays(41))
                    .time(LocalTime.of(10, 0))
                    .venue("Jio World Convention Centre")
                    .location("Mumbai, India")
                    .description("Connect with venture capitalists, angel investors, and pitch your startup idea on stage.")
                    .capacity(3000)
                    .registered(1200)
                    .price(799.0)
                    .earlyBirdPrice(499.0)
                    .earlyBirdDeadline(LocalDate.now().plusDays(20))
                    .thumbnail("/images/business.png")
                    .organizer(organizer)
                    .status("upcoming")
                    .rating(4.6)
                    .reviews(88)
                    .isPast(false)
                    .tags(Arrays.asList("Startups", "Venture Capital", "Networking"))
                    .images(Arrays.asList("/images/business.png"))
                    .build();

            TicketType tt5 = TicketType.builder().name("Delegate Pass").price(499.0).available(2000).event(event3).build();
            event3.setTicketTypes(Arrays.asList(tt5));

            eventRepository.saveAll(Arrays.asList(event1, event2, event3));
            System.out.println("--> Successfully seeded 3 sample events into MySQL database.");
        }

        // 3. Seed Tickets if empty
        if (ticketRepository.count() == 0) {
            System.out.println("--> Seeding sample MySQL tickets...");

            User regularUser = userRepository.findByEmail("user@eventpro.com").orElse(null);
            List<Event> events = eventRepository.findAll();

            if (regularUser != null && !events.isEmpty()) {
                Event targetEvent = events.get(0);
                Ticket ticket = Ticket.builder()
                        .ticketNumber("EVP-2026-TK001")
                        .event(targetEvent)
                        .user(regularUser)
                        .price(targetEvent.getEarlyBirdPrice() != null ? targetEvent.getEarlyBirdPrice() : targetEvent.getPrice())
                        .purchasedAt(LocalDate.now().minusDays(2))
                        .status("active")
                        .qrCode("QR-EVP-SAMPLE-001")
                        .seats(1)
                        .build();

                ticketRepository.save(ticket);
                System.out.println("--> Successfully seeded sample ticket into MySQL database.");
            }
        }
    }
}
