package cn.org.tpeach.nosql.tools;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class ConfigMapper implements Serializable
{
	private static final long serialVersionUID = -4676438977813158436L;
	private String value;
	private String comment;
}