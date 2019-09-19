package cn.org.tpeach.nosql.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextForm {

 
    /**
     * 标题
     */
    protected List<String> title = new ArrayList<>();
 
    /**
     * 数据
     */
    protected List<List<String>> datas = new ArrayList<>();
 
    /**
     * 最大列数
     */
    protected int maxCol = 0;

    /**
     * 每个单元格最大字符数
     */
    protected int colMaxLength = 8;

    /**
     * 表格水平组成符号
     */
    protected char separatorHorizontal = '-';
    /**
     * 表格数值组成符号
     */
    protected char separatorVerticality = '|';
    private TextForm() {
    }
 
    public static TextFormBulider bulider() {
        return new TextFormBulider(new TextForm());
    }

    public static class TextFormBulider {
        private TextForm textForm;

        protected TextFormBulider(TextForm textForm) {
            this.textForm = textForm;
        }

        public TextFormBulider title(String... titles) {
            if (textForm.maxCol < titles.length) {
                textForm.maxCol = titles.length;
            }
            for (String title : titles) {
                if (title == null) {
                    title = "null";
                }
                textForm.title.add(title);
            }
            return this;
        }


        public TextFormBulider separatorHorizontal(char separatorHorizontal) {
            textForm.separatorHorizontal = separatorHorizontal;
            return this;
        }
        public TextFormBulider separatorVerticality(char separatorVerticality) {
            textForm.separatorVerticality = separatorVerticality;
            return this;
        }
        public TextFormBulider colMaxLength(int colMaxLength) {
            textForm.colMaxLength = colMaxLength;
            return this;
        }

        public TextFormBulider addRow(String... cols) {
            if (textForm.maxCol < cols.length) {
                textForm.maxCol = cols.length;
            }
            List<String> list = new ArrayList<>(cols.length);
            for (String col : cols) {
                if (col == null) {
                    col = "null";
                }
                list.add(col);
            }
            textForm.datas.add(list);
            return this;
        }

        public TextForm finish() {
            int titleSize = textForm.title.size();
            if (titleSize < textForm.maxCol) {
                for (int i = 0; i < textForm.maxCol - titleSize; i++) {
                    textForm.title.add(null);
                }
            }
            for (List<String> data : textForm.datas) {
                int dataSize = data.size();
                if (dataSize < textForm.maxCol) {
                    for (int i = 0; i < textForm.maxCol - dataSize; i++) {
                        data.add(null);
                    }
                }
            }
            return textForm;
        }
    }
    /**
     * 格式化输出表格
     */
    public void printFormat(){
        System.out.println(formatTable());
    }
    public String formatTable() {
        StringBuffer sb = new StringBuffer();
        List<List<String>> formData = new ArrayList<>();
        formData.add(title);
        formData.addAll(datas);
        Map<Integer, Integer> colMaxLengthMap = colMaxLength(formData);
        int lineCount = 0;
        List<String> rows = new ArrayList<>();

        String pL = " ";
        String pR = " ";
        for (int i = 0; i < formData.size(); i++) {
            List<String> row = formData.get(i);

            String rowStr = separatorVerticality  +"" ;
            for (int j = 0; j < row.size(); j++) {
                Integer maxLength = colMaxLengthMap.get(j);
                String s = row.get(j);
                int repeatCount = 0;
                if(StringUtils.isNotEmpty(s)){
                    int chineseNum = getChineseNum(s);
                    int divide = (int) (chineseNum/1.3);
                    repeatCount =maxLength-s.length()-divide;
                }else{
                    repeatCount = maxLength;
                }

                String repeat = StringUtils.repeat(" ", repeatCount);
                rowStr += pL + s +repeat+ pR +separatorVerticality  ;
                if(lineCount < rowStr.length()){
                    lineCount = rowStr.length();
                }
            }
            rows.add(rowStr);
        }
        String line = StringUtils.repeat(separatorHorizontal, lineCount);
        sb.append(line);
        sb.append("\n");
        for (String row : rows) {
            sb.append(row);
            sb.append("\n");
            sb.append(line);
            sb.append("\n");
        }
        return sb.toString();
    }

     
    /**
     * 找到每一列最大的长度
     *
     * @param formData
     * @return
     */
    private Map<Integer, Integer> colMaxLength(List<List<String>> formData) {
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < formData.size(); i++) {
            int col = 0;
            List<String> strings = formData.get(i);
            while (strings.size() > col) {
                String val = strings.get(col);
                int length = 0;
                if(StringUtils.isNotEmpty(val)){
                    int chineseNum = getChineseNum(val);
                    length = val.length() + chineseNum;
                }

                Integer integer = map.get(col);

                if (integer == null) {
                    map.put(col, length);
                } else {
                    if (integer < length) {
                        map.put(col, length);
                    }
                }
                col++;
            }
        }
        return map;
    }
 
    /**
     * 找到每一列从右开始最小的空格长度
     *
     * @param formData
     * @return
     */
    private Map<Integer, Integer> colMinBlankLength(List<List<String>> formData) {
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < formData.size(); i++) {
            int col = 0;
            List<String> strings = formData.get(i);
            while (strings.size() > col) {
                String val = strings.get(col);
                int length = 0;
                for (int i1 = val.length() - 1; i1 >= 0; i1--) {
                    if (val.charAt(i1) == ' ') {
                        length++;
                    } else {
                        break;
                    }
                }
                Integer integer = map.get(col);
                if (integer == null) {
                    map.put(col, length);
                } else {
                    if (integer > length) {
                        map.put(col, length);
                    }
                }
                col++;
            }
        }
        return map;
    }
 
    /**
     * 获取中文数量
     *
     * @param val
     * @return
     */
    private int getChineseNum(String val) {
        if (val == null) {
            val = "null";
        }
        String regex = "[\u4e00-\u9fa5|。|，]";
        ArrayList<String> list = new ArrayList<String>();
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(val);
        while (matcher.find()) {
            list.add(matcher.group());
        }
        int size = list.size();
        return size;
    }

    public static void main(String[] args) {
        TextForm.bulider()
                .title("na的撒放上豆腐me的撒放上豆腐", "age", "se的撒放上豆腐x")//设置标题
                .addRow("的撒放上豆腐说的分公司的撒放上豆腐干地方发的顺丰得瑟gsf", "21", "男2")//添加行
                .addRow("wzeefgrerhei", "21", "男")//添加行

                .separatorVerticality('|')//设置表格由什么符号构成
                .separatorHorizontal('-')
                .finish()//完成
                .printFormat();//打印


    }
}