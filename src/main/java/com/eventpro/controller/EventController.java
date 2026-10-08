package com.eventpro.controller;

import com.eventpro.entity.Event;
import com.eventpro.repository.EventRepository;
import com.eventpro.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<Event>> getAllEvents() {
        return ResponseEntity.ok(eventRepository.findAll());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable Long id) {
        return eventRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createEvent(@RequestBody Map<String, Object> payload) {
        try {
            Event event = new Event();
            
            if (payload.containsKey("title")) event.setTitle((String) payload.get("title"));
            if (payload.containsKey("category")) event.setCategory((String) payload.get("category"));
            if (payload.containsKey("description")) event.setDescription((String) payload.get("description"));
            if (payload.containsKey("venue")) event.setVenue((String) payload.get("venue"));
            if (payload.containsKey("location")) event.setLocation((String) payload.get("location"));
            if (payload.containsKey("thumbnail")) event.setThumbnail((String) payload.get("thumbnail"));
            if (payload.containsKey("status") && payload.get("status") != null) {
                event.setStatus(payload.get("status").toString());
            } else {
                event.setStatus("upcoming");
            }

            if (payload.containsKey("date") && payload.get("date") != null) {
                event.setDate(java.time.LocalDate.parse(payload.get("date").toString()));
            }
            if (payload.containsKey("endDate") && payload.get("endDate") != null && !payload.get("endDate").toString().isEmpty()) {
                try {
                    event.setEndDate(java.time.LocalDate.parse(payload.get("endDate").toString()));
                } catch (Exception e) {
                    event.setEndDate(event.getDate());
                }
            } else {
                event.setEndDate(event.getDate());
            }

            if (payload.containsKey("time") && payload.get("time") != null) {
                String timeStr = payload.get("time").toString();
                if (timeStr.length() == 5) timeStr += ":00";
                try {
                    event.setTime(java.time.LocalTime.parse(timeStr));
                } catch (Exception e) {}
            }

            if (payload.containsKey("capacity") && payload.get("capacity") != null) {
                event.setCapacity(Integer.valueOf(payload.get("capacity").toString()));
            }
            if (payload.containsKey("registered") && payload.get("registered") != null) {
                event.setRegistered(Integer.valueOf(payload.get("registered").toString()));
            } else {
                event.setRegistered(0);
            }
            if (payload.containsKey("price") && payload.get("price") != null) {
                event.setPrice(Double.valueOf(payload.get("price").toString()));
            }
            if (payload.containsKey("earlyBirdPrice") && payload.get("earlyBirdPrice") != null) {
                event.setEarlyBirdPrice(Double.valueOf(payload.get("earlyBirdPrice").toString()));
            }
            if (payload.containsKey("earlyBirdDeadline") && payload.get("earlyBirdDeadline") != null && !payload.get("earlyBirdDeadline").toString().isEmpty()) {
                try {
                    event.setEarlyBirdDeadline(java.time.LocalDate.parse(payload.get("earlyBirdDeadline").toString()));
                } catch (Exception e) {}
            }

            // Resolve organizer
            Long orgId = null;
            if (payload.containsKey("organizer") && payload.get("organizer") instanceof Map) {
                Map<?, ?> orgMap = (Map<?, ?>) payload.get("organizer");
                if (orgMap.containsKey("id") && orgMap.get("id") != null) {
                    String s = orgMap.get("id").toString().replaceAll("\\D", "");
                    if (!s.isEmpty()) orgId = Long.valueOf(s);
                }
            }
            if (orgId == null && payload.containsKey("organizerId") && payload.get("organizerId") != null) {
                String s = payload.get("organizerId").toString().replaceAll("\\D", "");
                if (!s.isEmpty()) orgId = Long.valueOf(s);
            }
            if (orgId != null) {
                userRepository.findById(orgId).ifPresent(event::setOrganizer);
            }
            if (event.getOrganizer() == null && !userRepository.findAll().isEmpty()) {
                com.eventpro.entity.User defaultOrg = userRepository.findAll().stream()
                        .filter(u -> u.getRole() == com.eventpro.entity.User.Role.organizer)
                        .findFirst()
                        .orElse(userRepository.findAll().get(0));
                event.setOrganizer(defaultOrg);
            }

            // Tags
            if (payload.containsKey("tags") && payload.get("tags") instanceof List) {
                List<?> rawTags = (List<?>) payload.get("tags");
                List<String> tags = rawTags.stream().map(Object::toString).toList();
                event.setTags(tags);
            }

            // Ticket types
            if (payload.containsKey("ticketTypes") && payload.get("ticketTypes") instanceof List) {
                List<?> rawTT = (List<?>) payload.get("ticketTypes");
                List<com.eventpro.entity.TicketType> ttList = new java.util.ArrayList<>();
                for (Object item : rawTT) {
                    if (item instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> ttMap = (Map<String, Object>) item;
                        com.eventpro.entity.TicketType tt = new com.eventpro.entity.TicketType();
                        tt.setName(ttMap.get("name") != null ? ttMap.get("name").toString() : "General");
                        tt.setPrice(ttMap.get("price") != null ? Double.valueOf(ttMap.get("price").toString()) : 0.0);
                        tt.setAvailable(ttMap.get("available") != null ? Integer.valueOf(ttMap.get("available").toString()) : 100);
                        tt.setEvent(event);
                        ttList.add(tt);
                    }
                }
                event.setTicketTypes(ttList);
            }

            Event saved = eventRepository.save(event);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to create event: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Event> updateEvent(@PathVariable Long id, @RequestBody Event eventDetails) {
        return eventRepository.findById(id).map(event -> {
            if (eventDetails.getTitle() != null) event.setTitle(eventDetails.getTitle());
            if (eventDetails.getCategory() != null) event.setCategory(eventDetails.getCategory());
            if (eventDetails.getDate() != null) event.setDate(eventDetails.getDate());
            if (eventDetails.getEndDate() != null) event.setEndDate(eventDetails.getEndDate());
            if (eventDetails.getTime() != null) event.setTime(eventDetails.getTime());
            if (eventDetails.getVenue() != null) event.setVenue(eventDetails.getVenue());
            if (eventDetails.getLocation() != null) event.setLocation(eventDetails.getLocation());
            if (eventDetails.getDescription() != null) event.setDescription(eventDetails.getDescription());
            if (eventDetails.getCapacity() != null) event.setCapacity(eventDetails.getCapacity());
            if (eventDetails.getPrice() != null) event.setPrice(eventDetails.getPrice());
            if (eventDetails.getEarlyBirdPrice() != null) event.setEarlyBirdPrice(eventDetails.getEarlyBirdPrice());
            if (eventDetails.getEarlyBirdDeadline() != null) event.setEarlyBirdDeadline(eventDetails.getEarlyBirdDeadline());
            if (eventDetails.getThumbnail() != null) event.setThumbnail(eventDetails.getThumbnail());
            if (eventDetails.getStatus() != null) event.setStatus(eventDetails.getStatus());
            Event updated = eventRepository.save(event);
            return ResponseEntity.ok(updated);
        }).orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Event> patchEvent(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        return eventRepository.findById(id).map(event -> {
            if (updates.containsKey("registered")) {
                event.setRegistered(((Number) updates.get("registered")).intValue());
            }
            if (updates.containsKey("status")) {
                event.setStatus(updates.get("status").toString());
            }
            Event updated = eventRepository.save(event);
            return ResponseEntity.ok(updated);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable Long id) {
        if (!eventRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        eventRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
