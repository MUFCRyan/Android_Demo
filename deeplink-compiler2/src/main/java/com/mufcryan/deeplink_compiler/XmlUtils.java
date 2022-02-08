package com.mufcryan.deeplink_compiler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class XmlUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(XmlUtils.class);

    /**
     * @Auther sun
     * @DateTime 2019/3/8 下午5:15
     * @Description:
     * @Param map        生成xml的map数据
     * @Param rootName  根结点名称
     * @Return: java.lang.String
     */
    public static String map2xml(Map<String, ?> map, String rootName) {
        Document doc = DocumentHelper.createDocument();
        Element rootEle = doc.addElement(rootName);
        if (null != map && !map.isEmpty()) {
            Set<? extends Map.Entry<String, ?>> entrySet = map.entrySet();
            for (Map.Entry<String, ?> entry : entrySet) {
                Element ele = rootEle.addElement(entry.getKey());
                ele.setText(String.valueOf(entry.getValue()));
            }
        } else {
            LOGGER.warn("[生成XML]Map为空");
        }
        StringWriter sw = new StringWriter();
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding("utf-8");
        try {
            XMLWriter xmlWriter = new XMLWriter(sw, format);
            xmlWriter.write(doc);
        } catch (IOException ex) {
            LOGGER.error("[生成XML]生成XML数据失败", ex);
        } finally {
            try {
                sw.close();
            } catch (IOException ex) {
                LOGGER.error("[生成XML]关闭IO异常", ex);
            }
        }
        return sw.toString();
    }

    public static void saveXml(String xmlString, String path, String fileName) {
        FileWriter writer = null;
        try {
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File target = new File(path, fileName);
            System.out.println("path = " + target.getPath());
            if (!target.exists()) {
                target.createNewFile();
            }
            writer = new FileWriter(target);
            writer.write(xmlString);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @Auther sun
     * @DateTime 2019/3/11 下午1:56
     * @Description: xml 转换为 map
     * @Param strXML
     * @Return: java.util.SortedMap<java.lang.String , java.lang.String>
     */
    public static SortedMap<String, String> dom4jXMLParse(String strXML) throws DocumentException {
        SortedMap<String, String> smap = new TreeMap<String, String>();
        Document doc = DocumentHelper.parseText(strXML);
        Element root = doc.getRootElement();
        for (Iterator iterator = root.elementIterator(); iterator.hasNext(); ) {
            Element e = (Element) iterator.next();
            smap.put(e.getName(), e.getText());
        }
        return smap;
    }

}