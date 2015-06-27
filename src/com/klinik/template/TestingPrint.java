/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.klinik.template;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import simple.escp.json.JsonTemplate;

/**
 *
 * @author cak-ust
 */
public class TestingPrint {

    JsonTemplate template;

    public static void main(String[] args) {

    }

    public TestingPrint() {
        try {
            this.template = new JsonTemplate(getClass().getResource("/template.json").toURI());

            Map<String, Object> map = new HashMap<>();
            map.put("invoiceNo", "INVC-00001");
            List<Map<String, Object>> tables = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                Map<String, Object> line = new HashMap<>();
                line.put("code", String.format("CODE-%d", i));
                line.put("name", String.format("Product Random %d", i));
                line.put("qty", String.format("%d", i * i));
                tables.add(line);
            }
            map.put("table_source", tables);

        } catch (IOException ex) {
            Logger.getLogger(TestingPrint.class.getName()).log(Level.SEVERE, null, ex);
        } catch (URISyntaxException ex) {
            Logger.getLogger(TestingPrint.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
