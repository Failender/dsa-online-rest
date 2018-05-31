package de.failender.dsaonline.rest.event;

import de.failender.dsaonline.data.entity.EventEntity;
import lombok.Data;

import java.math.BigInteger;

@Data
public class CreateEventDto {

	private String name;
	private String startDate;
	private String endDate;
	private EventEntity.Type type;
	private BigInteger ownerId;
}
