package com.game.controller;

import com.game.entity.JsonEntity;
import com.game.model.ServerArea;
import com.game.service.IConfigService;
import com.game.service.IServerAreaService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

/**
 * Created by jeniss on 17/5/23.
 */
@RestController
@RequestMapping("/cron")
public class CronController {
    private static final Logger logger = Logger.getLogger(CronController.class);

    @Autowired
    public IServerAreaService serverAreaService;

    @Autowired
    public IConfigService configService;

    @RequestMapping("/crawlingDataToSave.do")
    public JsonEntity crawlingDataToSave() {
        List<ServerArea> serverAreaList = serverAreaService.getAll();

        //get url of data
        String gameUrl = configService.getConfigByCode("url_game").getValue();
        String areaUrl = configService.getConfigByCode("url_area").getValue();
        String serverUrl = configService.getConfigByCode("url_server").getValue();
        String typeUrl = configService.getConfigByCode("url_type").getValue();

        String urlPrefix = "http://www.uu898.com/newTrade.aspx?gm=61%c=-3";

        for (ServerArea serverArea : serverAreaList) {
            for (ServerArea child : serverArea.getChildServerAreas()) {
                String urlStr = urlPrefix + "&area=" + serverArea.getCode() + "&srv=" + child.getCode();
                String htmlContent = getHtmlContent(urlStr);

                System.out.println(htmlContent);
                break;
            }
        }

        return new JsonEntity();
    }

    public String getHtmlContent(String htmlUrl) {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = null;
        try {
            URL url = new URL(htmlUrl);
            URLConnection connection = url.openConnection();
            String contentType = connection.getContentType();
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), contentType));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }
}
