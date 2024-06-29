package com.example.szakdolg.util;

import java.util.Map;

public class XMLUtil {

   public static String convertToXml(
      Map<String, Object> map,
      String rootElement
   ) {
      StringBuilder xmlBuilder = new StringBuilder();
      xmlBuilder.append("<").append(rootElement).append(">");

      for (Map.Entry<String, Object> entry : map.entrySet()) {
         xmlBuilder.append("<").append(entry.getKey()).append(">");

         if (entry.getValue() instanceof Map) {
            // Recursively handle nested maps
            xmlBuilder.append(
               convertToXml(
                  (Map<String, Object>) entry.getValue(),
                  entry.getKey()
               )
            );
         } else {
            xmlBuilder.append(entry.getValue().toString());
         }

         xmlBuilder.append("</").append(entry.getKey()).append(">");
      }

      xmlBuilder.append("</").append(rootElement).append(">");
      return xmlBuilder.toString();
   }
}
