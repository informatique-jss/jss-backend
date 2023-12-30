package com.jss.osiris.modules.reporting.service;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;
import org.springframework.stereotype.Service;

@Service
public class ReportingHelper<T> {

    public ArrayList<HashMap<String, String>> getFakeData(Method[] methods) {
        ArrayList<HashMap<String, String>> fakeDatas = new ArrayList<HashMap<String, String>>();
        if (methods != null && methods.length > 0) {
            HashMap<String, String> value = new HashMap<String, String>();
            for (Method method : methods) {
                value.put(WordUtils.uncapitalize(method.getName().replace("get", "")), "");
            }
            fakeDatas.add(value);
        }
        return fakeDatas;
    }

    public ArrayList<HashMap<String, String>> filterOutputColumns(List<T> results,
            ArrayList<String> columns, Method[] methods) {
        ArrayList<HashMap<String, String>> outResults = new ArrayList<HashMap<String, String>>();
        if (results != null && results.size() > 0) {
            for (int i = 0; i < results.size(); i++) {
                T result = results.get(i);
                HashMap<String, String> set = new HashMap<String, String>();

                // On first get all to allow user to select all fields
                if (i == 0) {
                    for (Method method : methods) {
                        try {
                            Object value = method.invoke(result);
                            if (value == null)
                                value = "";
                            set.put(WordUtils.uncapitalize(method.getName().replace("get", "")), value.toString());
                        } catch (Exception e) {
                        }
                    }
                    outResults.add(set);
                } else {
                    for (String column : columns) {
                        Method method = null;
                        try {
                            method = result.getClass().getDeclaredMethod("get" + StringUtils.capitalize(column));
                        } catch (NoSuchMethodException e) {
                        }
                        if (method != null)
                            try {
                                Object value = method.invoke(result);
                                if (value != null)
                                    set.put(column, value.toString());
                            } catch (Exception e) {
                            }
                        outResults.add(set);
                    }
                }
            }
        }
        return outResults;
    }
}
