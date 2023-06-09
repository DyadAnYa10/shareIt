package ru.practicum.shareit.booking;

import lombok.Data;

@Data
public class ErrorBookingResponse {
        private final String error;

        public ErrorBookingResponse(String description) {
            this.error = "Unknown state: " + description;
        }
}
