package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEvent implements Serializable {
    private String eventType;
    private Long userId;
    private String name;
    private String email;
}
