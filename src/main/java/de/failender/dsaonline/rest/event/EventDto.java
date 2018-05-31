package de.failender.dsaonline.rest.event;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EventDto {
	private String name;
	private String startDate;
	private String endDate;
	private int id;
}
