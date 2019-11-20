package cn.org.tpeach.nosql.tools;

import cn.org.tpeach.nosql.constant.ConfigConstant;
import cn.org.tpeach.nosql.constant.PublicConstant;
import cn.org.tpeach.nosql.exception.ServiceException;
import lombok.Getter;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public enum ConfigParser {
	INSTANCE;
	public static ConfigParser getInstance() {
		return ConfigParser.INSTANCE;
	}

	// 默认注释符号
	private final String comments = "#";
	// 组
	private Pattern _section = Pattern.compile("\\s*\\[([^]]*)\\]\\s*");
	// 数组
	private Pattern _sectionList = Pattern.compile("\\s*\\<([^]]*)\\>\\s*");
	private Pattern _keyValue = Pattern.compile("\\s*([^=]*)=(.*)");
	private final static String format = "%-10s = %s %s\r\n";
	@Getter
	private Map<String, Object> entries = new LinkedHashMap<>();
	private File file;

	/**
	 *
	 * @param section
	 * @param key
	 * @param value
	 * @param isNewValue 为true时替换值 false只有原来的值不存在时才会覆盖
	 */
	public void safePutMapperData( String section,String key,String value,boolean isNewValue){
		Map<String, ConfigMapper> mapper = (Map<String, ConfigMapper>)  getEntries().get(section);
		if(mapper == null){
			mapper = new HashMap<>();
			getEntries().put(section,mapper);
		}
		ConfigMapper configMapper = mapper.get(key);
		if(configMapper == null){
			configMapper = ConfigMapper.builder().build();
		}
		if(isNewValue){
			configMapper.setValue(value);
		}else if(StringUtils.isBlank(configMapper.getValue())){
			configMapper.setValue(value);
		}

		mapper.put(key,configMapper);
	}
	public void safePutMapperData( String section,String key,String value){
		safePutMapperData(section,key,value,true);
	}
	public File getFile() throws IOException {
		if (file == null) {
			file = IOUtil.getFile(PublicConstant.REDIS_CONFIG_PATH);
		}
		return file;
	}

	public void parseReader() {

		try {

			entries.clear();
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(getFile()), "UTF-8"));
			String currentLine, currentsection = null, currentList = null, currentComment = null;
			String currentKey, currentValue;
			int listIndex = -1;
			// 读取
			while ((currentLine = br.readLine()) != null) {
				currentLine = currentLine.trim();
				if (isComment(currentLine)) {
					entries.put(StringUtils.getUUID(), ConfigMapper.builder().comment(currentLine).build());
				}
				currentComment = getComment(currentLine);
				// 移除注释
				currentLine = removeComment(currentLine).trim();
				if (StringUtils.isNotBlank(currentLine)) {
					// 查找数组
					Matcher m = _sectionList.matcher(currentLine);
					if (m.matches()) {
						String listKey = m.group(1).trim();
						if (StringUtils.isBlank(listKey)) {
							throw new ServiceException("配置文件异常,<>不能为空");
						}
						if (listKey.startsWith("/")) {
							currentList = null;
							listIndex = -1;
						} else {
							currentList = listKey;
							entries.put(listKey, new LinkedList<Map<String, Map<String, ConfigMapper>>>());
						}
						continue;
					}
					// 查找组
					m = _section.matcher(currentLine);
					if (m.matches()) {
						currentsection = m.group(1).trim();
						if (currentList != null) {
							List<Map<String, Map<String, ConfigMapper>>> list = (List<Map<String, Map<String, ConfigMapper>>>) entries.get(currentList);
							Map<String, Map<String, ConfigMapper>> map = new HashMap<>(1);
							map.put(currentsection, new LinkedHashMap<>());
							list.add(map);
							listIndex++;
						} else {
							entries.put(currentsection, new LinkedHashMap<>());
						}
						continue;
					}
					if (currentsection != null) {
						m = _keyValue.matcher(currentLine);
						if (m.matches()) {
							currentKey = m.group(1).trim();
							currentValue = m.group(2).trim();
							Map<String, ConfigMapper> group;
							if (currentList != null) {
								List<Map<String, Map<String, ConfigMapper>>> list = (List<Map<String, Map<String, ConfigMapper>>>) entries.get(currentList);
								Map<String, Map<String, ConfigMapper>> map = list.get(listIndex);
								group = map.get(currentsection);
							} else {
								group = (Map<String, ConfigMapper>) entries.get(currentsection);
							}
							group.put(currentKey,
									ConfigMapper.builder().comment(currentComment).value(currentValue).build());
						}

					}

				}
			}
			readAfter();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * @throws IOException
	 */
	private void readAfter() throws IOException {
		if (MapUtils.isEmpty(entries)) {
			Map<String, ConfigMapper> mapper = new LinkedHashMap<>(2);
			mapper.put(ConfigConstant.LANGUAGE, ConfigMapper.builder().value(ConfigConstant.Local.zh).build());
			mapper.put(ConfigConstant.COUNTRY, ConfigMapper.builder().value(ConfigConstant.Local.CN).comment("中文").build());
			entries.put(ConfigConstant.Section.LOCAL, mapper);
			//字体
			mapper = new LinkedHashMap<>(1);
			mapper.put(ConfigConstant.FONTSIZE, ConfigMapper.builder().value(ConfigConstant.DEFAULT_FONTSIZE).build());
			entries.put(ConfigConstant.Section.FONT, mapper);
			//编码
			mapper = new LinkedHashMap<>(1);
			mapper.put(ConfigConstant.CHARACTER, ConfigMapper.builder().value(PublicConstant.CharacterEncoding.UTF_8).build());
			entries.put(ConfigConstant.Section.CHARACTER_ENCODING, mapper);
			//实验特性
			mapper = new LinkedHashMap<>(1);
			mapper.put(ConfigConstant.MAGNIFYTEXT_DIALOG_SWITH, ConfigMapper.builder().value("0").comment("双击打开大窗口编辑Key数据").build());
			mapper.put(ConfigConstant.IS_LOADING_TEXT, ConfigMapper.builder().value("0").comment("是否开启大文本分页加载").build());
			mapper.put(ConfigConstant.APPEND_TEXT_NUMBER, ConfigMapper.builder().value("10000").comment("单次加载数量").build());
			mapper.put(ConfigConstant.APPEND_TEXT_WAITTIME, ConfigMapper.builder().value("1000").comment("单次加载渲染时间（毫秒）").build());
			mapper.put(ConfigConstant.MEMORY_FIXEDRATE_PERIOD, ConfigMapper.builder().value("5").comment("获取内存间隔").build());
			mapper.put(ConfigConstant.LOADING_GLOBEL_TIMEOGT_ENABLED, ConfigMapper.builder().value("5").comment("loading全局超時設置為1時所有超時設置失效").build());
			entries.put(ConfigConstant.Section.EXPERIMENT, mapper);

			writhConfigFile();
		} else {
			if (entries.get(ConfigConstant.Section.LOCAL) == null) {
				Map<String, Object> newMapper = new LinkedHashMap<>();
				Map<String, ConfigMapper> mapper = new LinkedHashMap<>(2);
				mapper.put(ConfigConstant.LANGUAGE, ConfigMapper.builder().value(ConfigConstant.Local.zh).build());
				mapper.put(ConfigConstant.COUNTRY, ConfigMapper.builder().value(ConfigConstant.Local.CN).build());
				entries.put(ConfigConstant.Section.LOCAL, mapper);
				newMapper.put(ConfigConstant.Section.LOCAL, mapper);
				IOUtil.fileAppendFW(file, parseWrite(newMapper));
			}
			if (entries.get(ConfigConstant.Section.FONT) == null) {
				Map<String, Object> newMapper = new LinkedHashMap<>();
				Map<String, ConfigMapper> mapper = new LinkedHashMap<>(1);
				mapper.put(ConfigConstant.FONTSIZE, ConfigMapper.builder().value(ConfigConstant.DEFAULT_FONTSIZE).build());
				entries.put(ConfigConstant.Section.FONT, mapper);
				newMapper.put(ConfigConstant.Section.FONT, mapper);
				IOUtil.fileAppendFW(file, parseWrite(newMapper));
			}
			if (entries.get(ConfigConstant.Section.CHARACTER_ENCODING) == null) {
				Map<String, Object> newMapper = new LinkedHashMap<>();
				Map<String, ConfigMapper> mapper = new LinkedHashMap<>(1);
				mapper.put(ConfigConstant.CHARACTER, ConfigMapper.builder().value(PublicConstant.CharacterEncoding.UTF_8).build());
				entries.put(ConfigConstant.Section.CHARACTER_ENCODING, mapper);
				newMapper.put(ConfigConstant.Section.CHARACTER_ENCODING, mapper);
				IOUtil.fileAppendFW(file, parseWrite(newMapper));
			}
			if (entries.get(ConfigConstant.Section.EXPERIMENT) == null) {
				Map<String, Object> newMapper = new LinkedHashMap<>();
				Map<String, ConfigMapper> mapper = new LinkedHashMap<>(1);
				mapper.put(ConfigConstant.MAGNIFYTEXT_DIALOG_SWITH, ConfigMapper.builder().value("0").comment("双击打开大窗口编辑Key数据").build());
				mapper.put(ConfigConstant.IS_LOADING_TEXT, ConfigMapper.builder().value("0").comment("是否开启大文本分页加载").build());
				mapper.put(ConfigConstant.APPEND_TEXT_NUMBER, ConfigMapper.builder().value("10000").comment("单次加载数量").build());
				mapper.put(ConfigConstant.APPEND_TEXT_WAITTIME, ConfigMapper.builder().value("1000").comment("单次加载渲染时间（毫秒）").build());
				mapper.put(ConfigConstant.MEMORY_FIXEDRATE_PERIOD, ConfigMapper.builder().value("5").comment("获取内存间隔").build());
				mapper.put(ConfigConstant.LOADING_GLOBEL_TIMEOGT_ENABLED, ConfigMapper.builder().value("5").comment("loading全局超時設置為1時所有超時設置失效").build());
				entries.put(ConfigConstant.Section.EXPERIMENT, mapper);
				newMapper.put(ConfigConstant.Section.EXPERIMENT, mapper);
				IOUtil.fileAppendFW(file, parseWrite(newMapper));
			}
		}
//		System.err.println(GsonUtil.gson2String(entries));
	}

	public void writhConfigFile() throws IOException {
		String parseWrite = parseWrite(entries);
		IOUtil.writeConfigFile(parseWrite, getFile());
	}
	
	
	public String parseWrite(Map<String, Object> entries) {
		if (MapUtils.isEmpty(entries)) {
			return "";
		}

		StringBuffer sb = new StringBuffer();
		for (Map.Entry<String, Object> entry : entries.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			if (value instanceof Map) {
				sb.append("[" + key + "]\r\n");
				Map<String, ConfigMapper> mappper = (Map<String, ConfigMapper>) value;
				appendMapper(sb, mappper);
				sb.append("\r\n");
				// 数组
			} else if (value instanceof List) {
				List<Map<String, Map<String, ConfigMapper>>> list = (List<Map<String, Map<String, ConfigMapper>>>) value;
				sb.append("<" + key + ">\r\n");
				boolean flag = true;
				for (Map<String, Map<String, ConfigMapper>> map : list) {
					if (MapUtils.isNotEmpty(map)) {
						if(!flag) {
							sb.append("\r\n");
						}
						flag = true;
						for (Entry<String, Map<String, ConfigMapper>> entryMap : map.entrySet()) {
							if(!flag) {
								sb.append("\r\n");
							}
							sb.append("[" + entryMap.getKey() + "]\r\n");
							appendMapper(sb, entryMap.getValue());
							flag = false;
						}
					}
					flag = false;

				}
				sb.append("</" + key + ">\r\n\r\n");
				// 注释
			} else if (value instanceof ConfigMapper) {
				ConfigMapper v = (ConfigMapper) value;
				sb.append(v.getComment() + "\r\n");
			} else {
				throw new ServiceException("parseWrite异常，发现未知类型" + value.getClass().getName());
			}
			
		}

		return sb.toString();
	}

	private void appendMapper(StringBuffer sb, Map<String, ConfigMapper> mappper) {
		if (MapUtils.isEmpty(mappper)) {
			return;
		}
		for (Map.Entry<String, ConfigMapper> entry : mappper.entrySet()) {
			String comment =  StringUtils.isBlank(entry.getValue().getComment())?"":comments+entry.getValue().getComment();
			String value = StringUtils.defaultEmptyToString(entry.getValue().getValue());
			if(value.contains("#") || value.contains("'")) {
				value = "'"+value+"'";
			}
			sb.append(String.format(format, entry.getKey(), value ,comment));
		}
	}

	
	private  String getComment(String currentLine) {
		int lastIndex = currentLine.lastIndexOf(comments);
		if (lastIndex != -1) {
			try {
			//如果 #号两边没有被 ''包围则是注释
			String prefix = currentLine.substring(0, lastIndex);
			String suffix = currentLine.substring(lastIndex+1);
			if(prefix.indexOf("'") == -1 && suffix.lastIndexOf("'")  == -1) {
				return suffix;
			}
			}catch (Exception e) {}
			
		}
		return "";

	}

	/**
	 * @param currentLine
	 * @return
	 */

	private String removeComment(String currentLine) {
		int lastIndex = currentLine.lastIndexOf(comments);
		if (lastIndex != -1) {
			String prefix = currentLine.substring(0, lastIndex);
			String suffix = currentLine.substring(lastIndex+1);
			if(prefix.indexOf("'") == -1 && suffix.lastIndexOf("'")  == -1) {
				currentLine = prefix;
			}
			currentLine = removeApostrophe(currentLine);
		}else {
			currentLine = removeApostrophe(currentLine);
		}
		return currentLine;
	}
	/**
	 * @param currentLine
	 * @return
	 */
	private String removeApostrophe(String currentLine) {
		int indexOf = currentLine.indexOf("'");
		int lastIndexOf = currentLine.lastIndexOf("'");
		if(indexOf != lastIndexOf) {
			currentLine = currentLine.substring(0,indexOf) +currentLine.substring(indexOf+1,lastIndexOf);
		}
		return currentLine;
	}

	private boolean isComment(String line) {
		if (line.startsWith(comments)) {
			return true;
		}
		return false;

	}
	
	
	public List<Map<String, Map<String, ConfigMapper>>> getListBySectionList (String section) {
		Object value = entries.get(section);
		if(value instanceof List) {
			List<Map<String, Map<String, ConfigMapper>>> list = (List<Map<String, Map<String, ConfigMapper>>>) value;
			return list;
		}
		return null;
	}
	
	
	public String getString(String section, String key, String defaultvalue) {
		Object value = entries.get(section);
		if (value instanceof Map) {
			return getString((Map<String, ConfigMapper>) value, section, key, defaultvalue);
		}
		return defaultvalue;
	}
	public String getString(Map<String, ConfigMapper> kv,String section, String key, String defaultvalue) {
		if (kv == null || kv.get(key) == null ||StringUtils.isBlank(kv.get(key).getValue())  ) {
			return defaultvalue;
		}
		return kv.get(key).getValue();
	}
	
	public short getShort(String section, String key, short defaultvalue) {
		Object value = entries.get(section);
		if (value instanceof Map) {
			return getShort((Map<String, ConfigMapper>) value, section, key, defaultvalue);
		}
		return defaultvalue;
	}
	public short getShort(Map<String, ConfigMapper> kv,String section, String key, short defaultvalue) {
		if (kv == null || kv.get(key) == null ||StringUtils.isBlank(kv.get(key).getValue())  ) {
			return defaultvalue;
		}
		return Short.parseShort(kv.get(key).getValue());
	}
	public int getInt(String section, String key, int defaultvalue) {
		Object value = entries.get(section);
		if (value instanceof Map) {
			return getInt((Map<String, ConfigMapper>) value, section, key, defaultvalue);
		}
		return defaultvalue;
	}
	public int getInt(Map<String, ConfigMapper> kv,String section, String key, int defaultvalue) {
		if (kv == null || kv.get(key) == null ||StringUtils.isBlank(kv.get(key).getValue())  ) {
			return defaultvalue;
		}
		return Integer.parseInt(kv.get(key).getValue());
	}
	public long getLong(Map<String, ConfigMapper> kv,String section, String key, long defaultvalue) {
		if (kv == null || kv.get(key) == null ||StringUtils.isBlank(kv.get(key).getValue())  ) {
			return defaultvalue;
		}
		return Long.parseLong(kv.get(key).getValue());
	}
	public long getLong(String section, String key, long defaultvalue) {
		Object value = entries.get(section);
		if (value instanceof Map) {
			return getLong((Map<String, ConfigMapper>) value, section, key, defaultvalue);
		}
		return defaultvalue;
	}


}