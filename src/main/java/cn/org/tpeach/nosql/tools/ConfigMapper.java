package cn.org.tpeach.nosql.tools;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConfigMapper {
	private String value;
	private String comment;
}