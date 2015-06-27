/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.klinik.template;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JFrame;
import simple.escp.Template;
import simple.escp.json.JsonTemplate;
import simple.escp.swing.PrintPreviewPane;

/**
 *
 * @author cak-ust
 */
public class MainFrameTest extends JFrame {

    public MainFrameTest() throws URISyntaxException, IOException {
        super("Preview");

//        Template template = new JsonTemplate(Thread.currentThread().
//                    getContextClassLoader().getResource("invoice.json").toURI());
        Template template = new JsonTemplate(getClass().getResource("invoice.json").toURI());
        Map<String, Object> value = new HashMap<>();
        value.put("invoiceNo", "INVC-00001");
        List<Map<String, Object>> tables = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Map<String, Object> line = new HashMap<>();
            line.put("code", String.format("CODE-%d", i));
            line.put("name", String.format("Product Random %d", i));
            line.put("qty", String.format("%d", i * i));
            tables.add(line);
        }
        value.put("table_source", tables);

        PrintPreviewPane printPreview = new PrintPreviewPane(template, value, null);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(printPreview, BorderLayout.CENTER);

        setPreferredSize(new Dimension(500, 500));
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public static void main(String[] args) {
        try {
            new MainFrameTest();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
