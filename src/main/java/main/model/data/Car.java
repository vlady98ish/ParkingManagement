package main.model.data;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Car {
    private String regno;
    private String mark;
    private User owner;

    @Override
    public String toString() {
        return mark + " (" + regno + ")";
    }
}
