package de.failender.dsaonline.rest.event;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EventDto {
	private String name;
	private int date;
	private int id;
}
