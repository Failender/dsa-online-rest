package de.failender.dsaonline.rest.script;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class TypeDto {
	private String name;
	private String description;
	private List<String> values;
}
